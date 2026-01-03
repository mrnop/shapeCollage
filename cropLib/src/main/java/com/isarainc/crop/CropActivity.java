/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.isarainc.crop;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import androidx.core.content.FileProvider;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

/**
 * The activity can crop specific region of interest from an image.
 */
public class CropActivity extends MonitoredActivity {
    private static final String TAG = "CropImage";
    public static final String TITLE = "title";
    public static final String IMAGE_PATH = "image-path";
    public static final String IMAGE_URI = "image-uri";
    public static final String OUTPUT_URI = "output-uri";
    public static final String MASK = "mask";
    public static final String SCALE = "scale";
    public static final String ORIENTATION_IN_DEGREES = "orientation_in_degrees";
    public static final String ASPECT_X = "aspectX";
    public static final String ASPECT_Y = "aspectY";
    public static final String OUTPUT_X = "outputX";
    public static final String OUTPUT_Y = "outputY";
    public static final String SCALE_UP_IF_NEEDED = "scaleUpIfNeeded";
    public static final String EXTRA_ERROR = "error";
    public static final String CIRCLE_CROP = "circleCrop";
    public static final String RETURN_DATA = "return-data";
    public static final String RETURN_DATA_AS_BITMAP = "data";
    public static final String ACTION_INLINE_DATA = "inline-data";
    public static final int NO_STORAGE_ERROR = -1;
    public static final int CANNOT_STAT_ERROR = -2;
    private static final int ROTATE_WIDGET_SENSITIVITY_COEFFICIENT = 42;

    final int IMAGE_MAX_SIZE = 2048;
    private final Handler mHandler = new Handler();
    private final BitmapManager.ThreadSet mDecodingThreads = new BitmapManager.ThreadSet();
    boolean mWaitingToPick; // Whether we are wait the user to pick a face.
    boolean mSaving; // Whether the "save" button is already clicked.
    HighlightView mCrop;
    // These are various options can be specified in the intent.
    private Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.PNG;
    private Uri mSaveUri = null;
    private boolean mDoFaceDetection = true;
    private boolean mCircleCrop = false;
    private int mAspectX;
    private int mAspectY;
    private int mOutputX;
    private int mOutputY;
    private boolean mScale;
    private float angle = 0;
    private CropImageView mImageView;
    private ContentResolver mContentResolver;
    private Bitmap mBitmap;
    private Bitmap mWork;
    public Bitmap mask;
    private Uri mImageUri;
    private String mImagePath;
    // These options specifiy the output image size and whether we should
    // scale the output to fit it (or just crop it).
    private boolean mScaleUp = true;
    private Toolbar mToolbar;
    private TextView mTextViewRotate;
    private boolean flipVertical;
    private boolean flipHorizontal;
    private boolean mShowLoader = true;
    Runnable mRunFaceDetection = new Runnable() {

        @SuppressWarnings("hiding")
        float mScale = 1F;
        Matrix mImageMatrix;
        FaceDetector.Face[] mFaces = new FaceDetector.Face[3];
        int mNumFaces;

        // For each face, we create a HightlightView for it.
        private void handleFace(FaceDetector.Face f) {

            PointF midPoint = new PointF();

            int r = ((int) (f.eyesDistance() * mScale)) * 2;
            f.getMidPoint(midPoint);
            midPoint.x *= mScale;
            midPoint.y *= mScale;

            int midX = (int) midPoint.x;
            int midY = (int) midPoint.y;

            HighlightView hv = new HighlightView(mImageView);

            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            RectF faceRect = new RectF(midX, midY, midX, midY);
            faceRect.inset(-r, -r);
            if (faceRect.left < 0) {
                faceRect.inset(-faceRect.left, -faceRect.left);
            }

            if (faceRect.top < 0) {
                faceRect.inset(-faceRect.top, -faceRect.top);
            }

            if (faceRect.right > imageRect.right) {
                faceRect.inset(faceRect.right - imageRect.right,
                        faceRect.right - imageRect.right);
            }

            if (faceRect.bottom > imageRect.bottom) {
                faceRect.inset(faceRect.bottom - imageRect.bottom,
                        faceRect.bottom - imageRect.bottom);
            }

            hv.setup(mImageMatrix, imageRect, faceRect, mCircleCrop,
                    mAspectX != 0 && mAspectY != 0);

            mImageView.add(hv);
        }

        // Create a default HightlightView if we found no face in the picture.
        private void makeDefault() {

            HighlightView hv = new HighlightView(mImageView);

            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            // make the default size about 4/5 of the width or height
            int cropWidth = Math.min(width, height) * 4 / 5;
            int cropHeight = cropWidth;

            if (mAspectX != 0 && mAspectY != 0) {

                if (mAspectX > mAspectY) {
                    cropHeight = cropWidth * mAspectY / mAspectX;
                } else {
                    cropWidth = cropHeight * mAspectX / mAspectY;
                }
            }

            int x = (width - cropWidth) / 2;
            int y = (height - cropHeight) / 2;

            RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
            hv.setup(mImageMatrix, imageRect, cropRect, mCircleCrop,
                    mAspectX != 0 && mAspectY != 0);
            if (mask != null) {
                hv.setMask(mask);
            }
            mImageView.mHighlightViews.clear(); // Thong added for rotate

            mImageView.add(hv);

        }

        // Scale the image down for faster face detection.
        private Bitmap prepareBitmap() {
            if (mBitmap == null) {
                return null;
            }

            // 256 pixels wide is enough.
            if (mBitmap.getWidth() > 256) {
                mScale = 256.0F / mBitmap.getWidth();
            }
            Matrix matrix = new Matrix();
            matrix.setScale(mScale, mScale);
            return Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
        }

        public void run() {

            mImageMatrix = mImageView.getImageMatrix();
            Bitmap faceBitmap = prepareBitmap();

            mScale = 1.0F / mScale;
            if (faceBitmap != null && mDoFaceDetection) {
                FaceDetector detector = new FaceDetector(faceBitmap.getWidth(),
                        faceBitmap.getHeight(), mFaces.length);
                mNumFaces = detector.findFaces(faceBitmap, mFaces);
            }

            if (faceBitmap != null && faceBitmap != mBitmap) {
                faceBitmap.recycle();
            }

            mHandler.post(new Runnable() {
                public void run() {

                    mWaitingToPick = mNumFaces > 1;
                    if (mNumFaces > 0) {
                        for (int i = 0; i < mNumFaces; i++) {
                            handleFace(mFaces[i]);
                        }
                    } else {
                        makeDefault();
                    }
                    mImageView.invalidate();
                    if (mImageView.mHighlightViews.size() == 1) {
                        mCrop = mImageView.mHighlightViews.get(0);
                        mCrop.setFocus(true);
                    }

                    if (mNumFaces > 1) {
                        Toast.makeText(CropActivity.this,
                                "Multi face crop help",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    public static void showStorageToast(Activity activity) {

        showStorageToast(activity, calculatePicturesRemaining(activity));
    }

    public static void showStorageToast(Activity activity, int remaining) {
        String noStorageText = null;
        if (remaining == NO_STORAGE_ERROR) {
            String state = Environment.getExternalStorageState();
            if (state.equals(Environment.MEDIA_CHECKING)) {
                noStorageText = activity.getString(R.string.preparing_card);
            } else {
                noStorageText = activity.getString(R.string.no_storage_card);
            }
        } else if (remaining < 1) {
            noStorageText = activity.getString(R.string.not_enough_space);
        }

        if (noStorageText != null) {
            Toast.makeText(activity, noStorageText, Toast.LENGTH_LONG).show();
        }
    }

    public static int calculatePicturesRemaining(Activity activity) {

        try {
            /*
             * if (!ImageManager.hasStorage()) {
             * return NO_STORAGE_ERROR;
             * } else {
             */
            String storageDirectory = "";
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                storageDirectory = Environment.getExternalStorageDirectory().toString();
            } else {
                storageDirectory = activity.getFilesDir().toString();
            }
            StatFs stat = new StatFs(storageDirectory);
            float remaining = ((float) stat.getAvailableBlocksLong()
                    * (float) stat.getBlockSizeLong()) / 400000F;
            return (int) remaining;
            // }
        } catch (Exception ex) {
            // if we can't stat the filesystem then we don't know how many
            // pictures are remaining. it might be zero but just leave it
            // blank since we really don't know.
            return CANNOT_STAT_ERROR;
        }
    }

    @Override
    public void onCreate(Bundle icicle) {

        super.onCreate(icicle);
        mContentResolver = getContentResolver();

        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cropimage);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit Image");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_close)
                .color(Color.WHITE)
                .sizeDp(24));
        TextView mTitle = findViewById(R.id.crop_title);

        mImageView = (CropImageView) findViewById(R.id.image);

        showStorageToast(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.getString(TITLE) != null) {
                mTitle.setText(extras.getString(TITLE));
            }
            if (extras.getString(CIRCLE_CROP) != null) {
                mImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                mCircleCrop = true;
                mAspectX = 1;
                mAspectY = 1;
            }

            if (extras.containsKey(IMAGE_PATH)) {
                mImagePath = extras.getString(IMAGE_PATH);
                mBitmap = getBitmap(mImagePath);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mSaveUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(mImagePath));
                } else {
                    mSaveUri = Uri.fromFile(new File(mImagePath));
                }
            } else if (extras.containsKey(IMAGE_URI)) {
                mImageUri = extras.getParcelable(IMAGE_URI);
                mBitmap = getBitmap(mImageUri);
                if (extras.containsKey(OUTPUT_URI)) {
                    mSaveUri = extras.getParcelable(OUTPUT_URI);
                } else {
                    throw new IllegalArgumentException("Please specify " + OUTPUT_URI);
                }
            } else {
                throw new IllegalArgumentException("Please specify " + IMAGE_PATH + " or " + IMAGE_URI);
            }

            mShowLoader = false;
            supportInvalidateOptionsMenu();
            // mSaveUri = getImageUri(mImagePath);
            // mBitmap = getBitmap(mImagePath);
            try {
                if (extras.containsKey(ASPECT_X) && extras.get(ASPECT_X) instanceof Integer) {
                    mAspectX = extras.getInt(ASPECT_X);
                } else {
                    throw new IllegalArgumentException("aspect_x must be integer");
                }
                if (extras.containsKey(ASPECT_Y) && extras.get(ASPECT_Y) instanceof Integer) {
                    mAspectY = extras.getInt(ASPECT_Y);
                } else {
                    throw new IllegalArgumentException("aspect_y must be integer");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mOutputX = extras.getInt(OUTPUT_X);
            mOutputY = extras.getInt(OUTPUT_Y);
            mScale = extras.getBoolean(SCALE, true);
            mScaleUp = extras.getBoolean(SCALE_UP_IF_NEEDED, true);
            mask = (Bitmap) extras.getParcelable(MASK);
        }

        if (mBitmap == null) {

            Log.d(TAG, "finish!!!");
            finish();
            return;
        }

        mTextViewRotate = ((TextView) findViewById(R.id.text_view_rotate));
        HorizontalProgressWheelView mScrollWheel = ((HorizontalProgressWheelView) findViewById(
                R.id.rotate_scroll_wheel));

        mScrollWheel.setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
            @Override
            public void onScroll(float delta, float totalDistance) {
                angle += delta / ROTATE_WIDGET_SENSITIVITY_COEFFICIENT;
                mWork = Util.rotateImage(mBitmap, angle);
                RotateBitmap rotateBitmap = new RotateBitmap(mWork);
                mImageView.setImageRotateBitmapResetBase(rotateBitmap, true);
                mRunFaceDetection.run();
                mTextViewRotate.setText(String.format(Locale.getDefault(), "%.1f°", angle));
            }

            @Override
            public void onScrollEnd() {
                // mGestureCropImageView.setImageToWrapCropBounds();
            }

            @Override
            public void onScrollStart() {
                // mGestureCropImageView.cancelAllAnimations();
            }
        });

        mScrollWheel.setMiddleLineColor(Color.RED);
        ImageButton mReset = findViewById(R.id.wrapper_reset_rotate);
        mReset.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_clear)
                .color(Color.LTGRAY)
                .sizeDp(24));
        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWork = Util.rotateImage(mBitmap, 0);
                RotateBitmap rotateBitmap = new RotateBitmap(mWork);
                mImageView.setImageRotateBitmapResetBase(rotateBitmap, true);
                mRunFaceDetection.run();
                angle = 0;
                flipVertical = false;
                flipHorizontal = false;
                mTextViewRotate.setText(String.format(Locale.getDefault(), "%.1f°", angle));
            }
        });
        ImageButton mRoateAngle = findViewById(R.id.wrapper_rotate_by_angle);
        mRoateAngle.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_rotate_right)
                .color(Color.LTGRAY)
                .sizeDp(24));
        mRoateAngle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                angle = angle + 90;
                mWork = Util.rotateImage(mBitmap, angle);
                RotateBitmap rotateBitmap = new RotateBitmap(mWork);
                mImageView.setImageRotateBitmapResetBase(rotateBitmap, true);
                mRunFaceDetection.run();
                mTextViewRotate.setText(String.format(Locale.getDefault(), "%.1f°", angle));
            }
        });
        ImageButton mflipHorizontal = findViewById(R.id.flip_horizontal);

        mflipHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipHorizontal = !flipHorizontal;
                mWork = Util.flip(mBitmap, flipVertical, flipHorizontal);
                RotateBitmap rotateBitmap = new RotateBitmap(mWork);
                mImageView.setImageRotateBitmapResetBase(rotateBitmap, true);
                mRunFaceDetection.run();
            }
        });
        ImageButton mflipVertical = findViewById(R.id.flip_vertical);

        mflipVertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipVertical = !flipVertical;
                mWork = Util.flip(mBitmap, flipVertical, flipHorizontal);
                RotateBitmap rotateBitmap = new RotateBitmap(mWork);
                mImageView.setImageRotateBitmapResetBase(rotateBitmap, true);
                mRunFaceDetection.run();
            }
        });
        mTextViewRotate.setText(String.format(Locale.getDefault(), "%.1f°", angle));
        mWork = Util.rotateImage(mBitmap, 0);
        /*
         * findViewById(R.id.rotateLeft).setOnClickListener(
         * new View.OnClickListener() {
         * public void onClick(View v) {
         * 
         * mBitmap = Util.rotateImage(mBitmap, -90);
         * RotateBitmap rotateBitmap = new RotateBitmap(mBitmap);
         * mImageView.setImageRotateBitmapResetBase(rotateBitmap, true);
         * mRunFaceDetection.run();
         * }
         * });
         * 
         * findViewById(R.id.rotateRight).setOnClickListener(
         * new View.OnClickListener() {
         * public void onClick(View v) {
         * 
         * mBitmap = Util.rotateImage(mBitmap, 90);
         * RotateBitmap rotateBitmap = new RotateBitmap(mBitmap);
         * mImageView.setImageRotateBitmapResetBase(rotateBitmap, true);
         * mRunFaceDetection.run();
         * }
         * });
         */

        startFaceDetection();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.crop, menu);
        MenuItem menuItemLoader = menu.findItem(R.id.menu_loader);
        Drawable menuItemLoaderIcon = menuItemLoader.getIcon();
        if (menuItemLoaderIcon != null) {
            try {
                menuItemLoaderIcon.mutate();
                menuItemLoaderIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                menuItemLoader.setIcon(menuItemLoaderIcon);
            } catch (IllegalStateException e) {
                Log.i(TAG, String.format("%s - %s", e.getMessage(), getString(R.string.app_name)));
            }
            ((Animatable) menuItemLoader.getIcon()).start();
        }
        MenuItem checkItem = menu.findItem(R.id.action_check);
        // set your desired icon here based on a flag if you like
        checkItem.setIcon(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_check)
                .color(Color.WHITE)
                .sizeDp(24));
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.action_check).setVisible(!mShowLoader);
        menu.findItem(R.id.menu_loader).setVisible(mShowLoader);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        } else if (id == R.id.action_check) {
            try {
                onSaveClicked();
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Uri getImageUri(String path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(getApplicationContext(),
                    getApplicationContext().getPackageName() + ".provider", new File(path));
        } else {
            return Uri.fromFile(new File(path));
        }

    }

    private Bitmap getBitmap(String path) {
        Uri uri = getImageUri(path);
        return getBitmap(uri);
    }

    private Bitmap getBitmap(Uri uri) {
        InputStream in = null;
        try {
            in = mContentResolver.openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math
                        .round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = mContentResolver.openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();

            return b;
        } catch (Throwable e) {
            Log.e(TAG, "file " + uri + " not found");

        }
        return null;
    }

    private void startFaceDetection() {

        if (isFinishing()) {
            return;
        }

        mImageView.setImageBitmapResetBase(mBitmap, true);

        Util.startBackgroundJob(this, null,
                "Please wait\u2026",
                new Runnable() {
                    public void run() {

                        final CountDownLatch latch = new CountDownLatch(1);
                        final Bitmap b = mBitmap;
                        mHandler.post(new Runnable() {
                            public void run() {

                                if (b != mBitmap && b != null) {
                                    mImageView.setImageBitmapResetBase(b, true);
                                    mBitmap.recycle();
                                    mBitmap = b;
                                }
                                if (mImageView.getScale() == 1F) {
                                    mImageView.center(true, true);
                                }
                                latch.countDown();
                            }
                        });
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        mRunFaceDetection.run();
                    }
                }, mHandler);
    }

    private void onSaveClicked() throws Exception {
        // step api so that we don't require that the whole (possibly large)
        // bitmap doesn't have to be read into memory
        if (mSaving)
            return;

        if (mCrop == null) {

            return;
        }

        mSaving = true;

        Rect r = mCrop.getCropRect();

        int width = r.width();
        int height = r.height();

        // If we are circle cropping, we want alpha channel, which is the
        // third param here.
        Bitmap croppedImage;
        try {
            croppedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } catch (Exception e) {
            throw e;
        }
        if (croppedImage == null) {
            return;
        }

        {
            Canvas canvas = new Canvas(croppedImage);
            Rect dstRect = new Rect(0, 0, width, height);
            canvas.drawBitmap(mWork, r, dstRect, null);
        }

        if (mCircleCrop) {

            // OK, so what's all this about?
            // Bitmaps are inherently rectangular but we want to return
            // something that's basically a circle. So we fill in the
            // area around the circle with alpha. Note the all important
            // PortDuff.Mode.CLEAR.
            Canvas c = new Canvas(croppedImage);
            Path p = new Path();
            p.addCircle(width / 2F, height / 2F, width / 2F,
                    Path.Direction.CW);
            c.clipPath(p, Region.Op.DIFFERENCE);
            c.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
        }

        /* If the output is required to a specific size then scale or fill */
        if (mOutputX != 0 && mOutputY != 0) {

            if (mScale) {

                /* Scale the image to the required dimensions */
                Bitmap old = croppedImage;
                croppedImage = Util.transform(new Matrix(),
                        croppedImage, mOutputX, mOutputY, mScaleUp);
                if (old != croppedImage) {

                    old.recycle();
                }
            } else {

                /*
                 * Don't scale the image crop it to the size requested.
                 * Create an new image with the cropped image in the center and
                 * the extra space filled.
                 */

                // Don't scale the image but instead fill it so it's the
                // required dimension
                Bitmap b = Bitmap.createBitmap(mOutputX, mOutputY,
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(b);

                Rect srcRect = mCrop.getCropRect();
                Rect dstRect = new Rect(0, 0, mOutputX, mOutputY);

                int dx = (srcRect.width() - dstRect.width()) / 2;
                int dy = (srcRect.height() - dstRect.height()) / 2;

                /* If the srcRect is too big, use the center part of it. */
                srcRect.inset(Math.max(0, dx), Math.max(0, dy));

                /* If the dstRect is too big, use the center part of it. */
                dstRect.inset(Math.max(0, -dx), Math.max(0, -dy));

                /* Draw the cropped bitmap in the center */
                canvas.drawBitmap(mWork, srcRect, dstRect, null);

                /* Set the cropped bitmap as the new bitmap */
                croppedImage.recycle();
                croppedImage = b;
            }
        }

        // Return the cropped image directly or save it to the specified URI.
        Bundle myExtras = getIntent().getExtras();
        if (myExtras != null && (myExtras.getParcelable("data") != null
                || myExtras.getBoolean(RETURN_DATA))) {

            Bundle extras = new Bundle();
            extras.putParcelable(RETURN_DATA_AS_BITMAP, croppedImage);
            setResult(RESULT_OK,
                    (new Intent()).setAction(ACTION_INLINE_DATA).putExtras(extras));
            finish();
        } else {
            final Bitmap b = croppedImage;
            mShowLoader = true;
            supportInvalidateOptionsMenu();
            Util.startBackgroundJob(this, null, getString(R.string.saving_image),
                    new Runnable() {
                        public void run() {
                            saveOutput(b);
                        }
                    }, mHandler);
        }
    }

    private void saveOutput(Bitmap croppedImage) {

        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = mContentResolver.openOutputStream(mSaveUri);
                if (outputStream != null) {
                    croppedImage.compress(mOutputFormat, 100, outputStream);
                }
            } catch (Throwable ex) {

                Log.e(TAG, "Cannot open file: " + mSaveUri, ex);
                setResult(Crop.RESULT_ERROR, new Intent().putExtra(EXTRA_ERROR, ex));
                finish();
                return;
            } finally {

                Util.closeSilently(outputStream);
            }

            Bundle extras = new Bundle();
            Intent intent = new Intent(mSaveUri.toString());
            intent.putExtras(extras);
            if (mImagePath != null) {
                intent.putExtra(IMAGE_PATH, mImagePath);
            } else if (mImageUri != null) {
                intent.putExtra(IMAGE_URI, mImageUri);
            }
            intent.putExtra(ORIENTATION_IN_DEGREES, Util.getOrientationInDegree(this));
            setResult(RESULT_OK, intent);
        } else {

            Log.e(TAG, "not defined image url");
        }
        mShowLoader = false;
        supportInvalidateOptionsMenu();
        croppedImage.recycle();
        finish();
    }

    @Override
    protected void onPause() {

        super.onPause();
        BitmapManager.instance().cancelThreadDecoding(mDecodingThreads);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (mBitmap != null) {

            mBitmap.recycle();
        }
    }

}
