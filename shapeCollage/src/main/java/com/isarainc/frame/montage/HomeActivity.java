package com.isarainc.frame.montage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.MaterialDialog;
////import com.google.android.gms.ads.AdRequest;
////import com.google.android.gms.ads.AdView;
////import com.google.android.gms.ads.InterstitialAd;
import com.isarainc.arts.Art;
import com.isarainc.arts.ArtManager;
import com.isarainc.bg.BgManager;
import com.isarainc.bg.BgPickerDialogFragment;
import com.isarainc.crop.Crop;
import com.isarainc.dialog.ProcessListener;
import com.isarainc.filters.Filter;
import com.isarainc.layers.Layer;
import com.isarainc.layers.OnLayerChangeListener;
import com.isarainc.layers.StickerLayer;
import com.isarainc.layers.TextLayer;
import com.isarainc.main.GenPreviewTask;
import com.isarainc.shapecollage.R;
import com.isarainc.stickers.StickerInfo;
import com.isarainc.stickers.StickerPickerDialog;
import com.isarainc.text.StyleSettingDialog;
import com.isarainc.text.styles.TextStyle;
import com.isarainc.util.ImagePicker;
import com.isarainc.util.SaveTask;
import com.isarainc.util.Utils;
import com.michaelnovakjr.numberpicker.NumberPickerDialog;
import com.michaelnovakjr.numberpicker.NumberPickerDialog.OnNumberSetListener;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity implements OnLayerChangeListener, ProcessListener,
        StickerPickerDialog.OnImagePickListener, StyleSettingDialog.OnStyleChangeListener,
        GenPreviewTask.OnPreviewListener {

    private static final String TAG = "HomeActivity";
    private static final int PICK_IMAGE = 78;
    private DrawView drawView;

    private String sharedfile = "shared.png";

    public static final String TEMP_PHOTO_FILE_NAME = "temp_montage.jpg";
    protected int currentColor = Color.argb(255, 255, 255, 255);
    Random rnd = new Random();

    private SharedPreferences sharePrefs;

    // private int imageSize;
    private File mFileTemp;
    private String state;
    private Bitmap image;
    private int numberOfFrame = 5;
    private ShareActionProvider actionProvider;

    private BgManager bgManager;
    private int width;

    private LinearLayout textControl;
    private ImageButton pickSticker;
    private ImageButton textAdd;
    private EditText text;
    private ToggleButton alongPath;
    private boolean isAlongPath;
    private ImageButton delete;
    private MaterialDialog mProgressDialog;
    private LinearLayout filterControl;
    private LinearLayout filterCarousel;
    private Animation animation;

    private boolean firstBack = true;
    private float stickerScale = 1.0f;
    private MaterialDialog mSaveProgressDialog;
    private boolean generating = false;
    private boolean filterReady = false;
    private Art art;
    private String lastBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_framemontage);
        if (getIntent().hasExtra("ART")) {
            String artPath = getIntent().getStringExtra("ART");
            art = ArtManager.getInstance(this).loadArt(artPath);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        sharePrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        bgManager = BgManager.getInstance(this);
        //// adView = (AdView) this.findViewById(R.id.adView);

        // mInterstitial = new InterstitialAd(this);
        //// mInterstitial.setAdUnitId("ca-app-pub-5054886841152739/9555752409");

        //// AdRequest adRequest = new AdRequest.Builder().addTestDevice(
        // AdRequest.DEVICE_ID_EMULATOR).build();

        //// adView.loadAd(adRequest);

        //// mInterstitial.loadAd(adRequest);

        drawView = (DrawView) this.findViewById(R.id.drawView);

        drawView.setProcessListener(this);
        drawView.setOnLayerChangeListener(this);
        width = Utils.getHeight(this);
        if (width <= 720) {
            width = 720;
        }
        stickerScale = ((float) width) / 1000;
        ImageButton setPicCount = (ImageButton) findViewById(R.id.setPicCount);
        setPicCount.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_apps)
                .color(Color.WHITE)
                .sizeDp(24));
        setPicCount.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                firstBack = true;
                NumberPickerDialog dialog = new NumberPickerDialog(HomeActivity.this, -1, numberOfFrame,
                        getText(R.string.dialog_picker_title),
                        getText(R.string.dialog_set_number),
                        getText(R.string.dialog_cancel));
                dialog.setOnNumberSetListener(new OnNumberSetListener() {

                    @Override
                    public void onNumberSet(int selectedNumber) {
                        numberOfFrame = selectedNumber;
                        drawView.clearMontages();
                        drawView.setFrameCount(selectedNumber);
                        /*
                         * for (int i = 0; i < selectedNumber; i++) {
                         * Montage frame = new Montage(HomeActivity.this);
                         * 
                         * 
                         * int x = imageSize / 2;
                         * int y = imageSize / 2;
                         * if (width > imageSize && height > imageSize) {
                         * x = rnd.nextInt((width - imageSize));
                         * y = rnd.nextInt((height - imageSize));
                         * }
                         * 
                         * //Log.d(TAG, "x=" + x + "," + y);
                         * frame.setAngle(-30 + rnd.nextInt(60));
                         * frame.setX(imageSize / 2 + x);
                         * frame.setY(imageSize / 2 + y);
                         * //frame.refresh();
                         * MontageLayer layer = new MontageLayer();
                         * layer.init(HomeActivity.this);
                         * layer.setFrameSize(imageSize);
                         * layer.setFrame(frame);
                         * drawView.addLayer(layer);
                         * 
                         * }
                         */
                        drawView.invalidate();

                        sharePrefs.edit().putInt("numberOfFrame", numberOfFrame).apply();
                    }

                });
                dialog.show();
            }
        });

        ImageButton pickPictures = (ImageButton) findViewById(R.id.pickPictures);
        pickPictures.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_photo)
                .color(Color.WHITE)
                .sizeDp(24));
        pickPictures.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                firstBack = true;
                Intent chooseImageIntent = ImagePicker.getChooseImageIntent(HomeActivity.this);
                startActivityForResult(chooseImageIntent, PICK_IMAGE);
            }
        });
        filterControl = (LinearLayout) findViewById(R.id.filterControl);

        // Log.d(TAG, "load theme");
        filterCarousel = (LinearLayout) findViewById(R.id.carousel);

        ImageButton pickFilter = (ImageButton) findViewById(R.id.pickFilter);
        pickFilter.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_photo_filter)
                .color(Color.WHITE)
                .sizeDp(24));
        pickFilter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "toggleFilter");
                firstBack = true;
                if (!filterReady && !generating) {
                    if (image != null) {
                        generating = true;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mProgressDialog != null) {
                                    mProgressDialog.dismiss();
                                }

                                mProgressDialog = new MaterialDialog.Builder(HomeActivity.this)
                                        .title(R.string.gen_filter_thumb)
                                        .content(R.string.wait)
                                        .progress(true, 0).build();

                                mProgressDialog.show();
                            }
                        });

                        GenPreviewTask genPreviewTask = new GenPreviewTask(HomeActivity.this, filterCarousel);
                        genPreviewTask.setOnPreviewListener(new GenPreviewTask.OnPreviewListener() {
                            @Override
                            public void generating() {

                            }

                            @Override
                            public void generated() {
                                generating = false;
                                filterReady = true;
                                toggleFilter();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mProgressDialog != null) {
                                            mProgressDialog.dismiss();
                                            mProgressDialog = null;
                                        }
                                    }
                                });
                            }

                            @Override
                            public void picked(Filter filter) {
                                drawView.applyFilter(filter.getFilename());
                                hideFilter();
                            }
                        });

                        genPreviewTask.execute(Utils.scaleBitmap(image, 100, 100));
                    }
                } else {
                    toggleFilter();
                }

            }
        });

        ImageButton pickBg = (ImageButton) findViewById(R.id.pickBackground);
        pickBg.setImageDrawable(new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_presentation)
                .color(Color.WHITE)
                .sizeDp(24));
        pickBg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                firstBack = true;
                FragmentManager fm = getSupportFragmentManager();
                if (fm.findFragmentByTag("BgPickerDialogFragment") == null) {
                    BgPickerDialogFragment newFragment = new BgPickerDialogFragment();
                    Bundle args = new Bundle();
                    args.putInt("oldColor", currentColor);

                    newFragment.setArguments(args);
                    newFragment.show(fm, "BgPickerDialogFragment");
                    newFragment
                            .setOnBgSelectListener(new BgPickerDialogFragment.OnBgSelectListener() {

                                @Override
                                public void onColorChanged(
                                        int bgColor) {
                                    currentColor = bgColor;
                                    drawView.setBg(null);
                                    drawView.setBgColor(currentColor);
                                    drawView.invalidate();
                                    sharePrefs
                                            .edit()
                                            .putInt("montageColor",
                                                    currentColor)
                                            .apply();
                                }

                                @Override
                                public void onBgPicked(
                                        Bitmap bitmap, String name) {
                                    drawView.setBg(bitmap);
                                    drawView.invalidate();
                                    sharePrefs
                                            .edit()
                                            .putString("lastMontageBg",
                                                    name)
                                            .apply();
                                }

                            });
                }

            }
        });

        textControl = (LinearLayout) findViewById(R.id.textControl);

        pickSticker = (ImageButton) findViewById(R.id.pickSticker);
        pickSticker.setImageDrawable(new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_sticker)
                .color(Color.WHITE)
                .sizeDp(24));
        pickSticker.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                firstBack = true;
                FragmentManager fm = getSupportFragmentManager();
                if (fm.findFragmentByTag("StickerPickerDialogFragment") == null) {
                    StickerPickerDialog newFragment = new StickerPickerDialog();
                    newFragment.setOnImagePickListener(HomeActivity.this);
                    Bundle args = new Bundle();

                    newFragment.setArguments(args);
                    newFragment.show(fm, "StickerPickerDialogFragment");
                }

            }
        });
        delete = (ImageButton) findViewById(R.id.delete);
        delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                firstBack = true;
                drawView.delete();

            }
        });

        textAdd = (ImageButton) findViewById(R.id.add);
        textAdd.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_format_color_text)
                .color(Color.WHITE)
                .sizeDp(24));
        textAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                firstBack = true;
                TextLayer layer = new TextLayer(getResources().getString(
                        R.string.newLine));
                layer.init(HomeActivity.this);
                layer.setAlongPath(true);
                layer.setSize(drawView.getImageWidth() / 9);
                layer.setX(width / 2);
                layer.setY(width / 2);
                TextStyle style = TextStyle.random();
                // style.setColor(0, Color.WHITE);
                layer.setStyle(style);

                int x1 = (int) (drawView.getImageWidth() * 0.05);
                int x2 = (int) (drawView.getImageWidth() * 0.95);
                int y1 = (int) (drawView.getImageHeight() * 0.70);
                int y2 = (int) (drawView.getImageHeight() * 0.72);

                int dx = (x2 - x1) / 5;
                int step = x1;

                while (step < x2) {

                    layer.getSavePoints().add(
                            new Point(step, y1 + rnd.nextInt(y2 - y1)));
                    step += dx;
                }
                drawView.addLayer(layer);

                // drawView.setActiveLayer(layer);
                drawView.invalidate();

            }
        });
        // Text Control
        text = (EditText) this.findViewById(R.id.text1);

        text.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                firstBack = true;
                if (drawView.getActiveLayer() != null) {

                    if (drawView.getActiveLayer() instanceof TextLayer) {
                        ((TextLayer) drawView.getActiveLayer()).setText(s
                                .toString());
                    }

                    drawView.invalidate();
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {

            }

        });
        alongPath = (ToggleButton) findViewById(R.id.alongPath);
        alongPath.setChecked(isAlongPath);
        if (isAlongPath) {
            alongPath.setBackgroundDrawable(new IconicsDrawable(this)
                    .icon(CommunityMaterial.Icon.cmd_cursor_pointer)
                    .color(Color.RED)
                    .sizeDp(24));
        } else {
            alongPath.setBackgroundDrawable(new IconicsDrawable(this)
                    .icon(CommunityMaterial.Icon.cmd_cursor_pointer)
                    .color(Color.BLACK)
                    .sizeDp(24));
        }
        alongPath.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                firstBack = true;
                if (drawView.getActiveLayer() instanceof TextLayer) {
                    isAlongPath = !isAlongPath;

                    ((TextLayer) drawView.getActiveLayer())
                            .setAlongPath(isAlongPath);
                    alongPath.setChecked(isAlongPath);
                    if (isAlongPath) {
                        alongPath.setBackgroundDrawable(new IconicsDrawable(HomeActivity.this)
                                .icon(CommunityMaterial.Icon.cmd_cursor_pointer)
                                .color(Color.RED)
                                .sizeDp(24));
                    } else {
                        alongPath.setBackgroundDrawable(new IconicsDrawable(HomeActivity.this)
                                .icon(CommunityMaterial.Icon.cmd_cursor_pointer)
                                .color(Color.BLACK)
                                .sizeDp(24));
                    }
                    drawView.refresh();
                }

            }
        });

        ImageButton changeStyle = (ImageButton) findViewById(R.id.changeColor);
        changeStyle.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_palette)
                .color(Color.BLACK)
                .sizeDp(24));

        // changeColor.setBackgroundColor(currentColor);
        changeStyle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                firstBack = true;
                final StyleSettingDialog dialog = new StyleSettingDialog(HomeActivity.this);
                TextLayer layer = (TextLayer) drawView.getActiveLayer();
                try {
                    TextStyle style = (TextStyle) (layer.getStyle().clone());
                    dialog.setStyle(style);
                    dialog.setFont(style.getFont());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                dialog.setText(layer.getText());
                dialog.setOnStyleChangeListener(HomeActivity.this);
                dialog.show();

            }
        });
        // End Text Control

        Utils.verifyStoragePermissions(this);

        numberOfFrame = sharePrefs.getInt("numberOfFrame", 5);

        currentColor = sharePrefs.getInt("montageColor", Color.WHITE);
        lastBg = sharePrefs.getString("lastMontageBg", null);

        mFileTemp = new File(getWorkDir(), TEMP_PHOTO_FILE_NAME);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                Uri imageUri = intent
                        .getParcelableExtra(Intent.EXTRA_STREAM);
                // Log.d(TAG, "imageUri=" +getRealPathFromURI(imageUri));
                InputStream is;
                try {

                    is = getContentResolver().openInputStream(imageUri);
                    FileOutputStream fos = new FileOutputStream(mFileTemp);
                    copyStream(is, fos);
                    fos.close();
                    is.close();

                    loadTempImage();
                    drawView.setPhoto(image);
                    drawView.invalidate();
                    saveImageToSD();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } else {

            if (mFileTemp.exists()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                        mProgressDialog = new MaterialDialog.Builder(HomeActivity.this)
                                .title(R.string.loading_image)
                                .content(R.string.wait)
                                .progress(true, 0).build();

                        mProgressDialog.show();

                    }
                });
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        image = Utils.loadBitmap(mFileTemp, width);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                drawView.setPhoto(image);
                                drawView.setFrameCount(numberOfFrame);
                                if (lastBg != null) {
                                    Bitmap bg = bgManager.loadBgImage(lastBg);
                                    drawView.setBg(bg);
                                } else {
                                    drawView.setBg(null);
                                    drawView.setBgColor(currentColor);
                                }

                                drawView.invalidate();

                            }
                        });
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        saveImageToSD();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mProgressDialog != null) {
                                    mProgressDialog.dismiss();
                                    mProgressDialog = null;
                                }
                            }
                        });
                    }
                }.execute();

            } else {
                InputStream istr;
                try {
                    istr = getAssets().open("default1.png");
                    Bitmap bitmap = BitmapFactory.decodeStream(istr);
                    image = bitmap.copy(Config.ARGB_8888, true);
                    drawView.setPhoto(image);
                    drawView.setFrameCount(numberOfFrame);
                    if (lastBg != null) {
                        Bitmap bg = bgManager.loadBgImage(lastBg);
                        drawView.setBg(bg);
                    } else {
                        drawView.setBg(null);
                        drawView.setBgColor(currentColor);
                    }

                    drawView.invalidate();
                } catch (IOException e) {

                    e.printStackTrace();
                }

            }
        }

    }

    public File getWorkDir() {
        File dir = null;
        if (Utils.isSDCARDMounted()) {
            dir = new File(
                    Environment.getExternalStorageDirectory()
                            + File.separator + "Android"
                            + File.separator + "data"
                            + File.separator + getPackageName()
                            + File.separator + "workspace");
        } else {
            dir = new File(
                    Environment.getDataDirectory().getPath()
                            + File.separator + getPackageName()
                            + File.separator + "workspace");
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private void toggleFilter() {
        if (filterCarousel.getVisibility() == View.VISIBLE) {
            hideFilter();
        } else {
            showFilter();
        }

    }

    private void hideFilter() {
        if (filterCarousel.getVisibility() == View.VISIBLE) {
            animation = AnimationUtils.loadAnimation(this, R.anim.holder_bottom_back);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation arg0) {
                    filterCarousel.setVisibility(View.INVISIBLE);
                    drawView.bringToFront();
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationStart(Animation arg0) {
                }
            });
            filterControl.startAnimation(animation);
        }
    }

    private void showFilter() {
        if (filterCarousel.getVisibility() != View.VISIBLE) {

            animation = AnimationUtils.loadAnimation(this, R.anim.holder_bottom);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation arg0) {

                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationStart(Animation arg0) {
                    filterCarousel.setVisibility(View.VISIBLE);
                    // filterControl.bringToFront();

                }
            });
            filterControl.startAnimation(animation);
        }
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_IMAGE:
                try {
                    Uri uri = ImagePicker.getImageUriFromResult(HomeActivity.this, resultCode, data);
                    Crop.of(uri, Uri.fromFile(mFileTemp)).start(HomeActivity.this);

                } catch (Exception e) {

                    Log.e(TAG, "Error while creating temp file", e);
                }
                break;
            case Crop.REQUEST_CROP:

                if (resultCode == RESULT_OK) {

                    if (data == null) {
                        Log.d(TAG, "Null data, but RESULT_OK, from image picker!");
                        return;
                    }

                    final Bundle extras = data.getExtras();
                    // Log.d(TAG, "extras=" +extras);
                    if (extras != null) {
                        loadTempImage();
                        drawView.setPhoto(image);
                        drawView.invalidate();
                        if (image != null) {

                            int thumbHeight = 100;
                            int thumbWidth = thumbHeight * image.getWidth() / image.getHeight();

                            GenPreviewTask genPreviewTask = new GenPreviewTask(HomeActivity.this, filterCarousel);
                            genPreviewTask.setOnPreviewListener(this);
                            genPreviewTask.execute(Utils.scaleBitmap(image, thumbWidth, thumbHeight));
                        }
                    }
                }

                break;
        }
    }

    private void loadTempImage() {

        // performCrop(getTempUri() );

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;

        if (image != null) {
            image.recycle();
            image = null;
        }
        try {

            Bitmap myBitmap = BitmapFactory.decodeFile(mFileTemp.getPath(),
                    options);

            image = myBitmap;

        } catch (OutOfMemoryError oome) {
            System.gc();
            try {
                image = BitmapFactory.decodeFile(mFileTemp.getPath(),
                        options);
            } catch (OutOfMemoryError oome2) {
                System.gc();
                options.inSampleSize = 3;
                try {
                    image = BitmapFactory.decodeFile(
                            mFileTemp.getPath(), options);
                } catch (Exception ignored) {

                }
            }
        }
        ExifInterface exif;
        try {
            exif = new ExifInterface(mFileTemp.getAbsolutePath());

            int orientation = exif
                    .getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            Log.d("ExifInteface .........", "rotation =" + orientation);

            // exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);

            Log.d("orientation", "" + orientation);
            Matrix m = new Matrix();
            if ((orientation == 0)) {

            } else if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                m.postRotate(180);
                // m.postScale((float) bm.getWidth(), (float) bm.getHeight());
                // if(m.preRotate(90)){
                Log.e("in orientation", "" + orientation);
                image = Bitmap.createBitmap(image, 0, 0, image.getWidth(),
                        image.getHeight(), m, true);

            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90);
                Log.e("in orientation", "" + orientation);
                image = Bitmap.createBitmap(image, 0, 0, image.getWidth(),
                        image.getHeight(), m, true);

            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
                Log.e("in orientation", "" + orientation);
                image = Bitmap.createBitmap(image, 0, 0, image.getWidth(),
                        image.getHeight(), m, true);
            }

        } catch (IOException e1) {

            e1.printStackTrace();
        }

    }

    private void saveImageToSD() {
        String filename = "share";
        new SaveTask(this, new SaveTask.OnPictureSavedListener() {
            @Override
            public void onPictureSaved(Uri uri) {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");

                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

                actionProvider.setShareIntent(shareIntent);
            }
        }).saveImage("shapecollage", filename, drawView.getSaveBitmap());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.getMenuInflater().inflate(R.menu.shapecollage, menu);
        MenuItem saveItem = menu.findItem(R.id.save);
        saveItem.setIcon(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_save)
                .color(Color.WHITE)
                .sizeDp(24));
        MenuItem actionItem = menu.findItem(R.id.share);

        actionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(actionItem);
        actionProvider
                .setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        // Note that you can set/change the intent any time,
        // say when the user has selected an image.

        actionProvider
                .setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener() {
                    @Override
                    public boolean onShareTargetSelected(
                            ShareActionProvider actionProvider, Intent intent) {
                        saveImageToSD();
                        return false;
                    }

                });

        return true;

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Log.d(TAG, "onMenuItemClick=" + item.getItemId());
        if (item.getItemId() == android.R.id.home) {
            showInterstitial();
            finish();
        } else if (item.getItemId() == R.id.save) {
            if (mSaveProgressDialog != null) {
                mSaveProgressDialog.dismiss();
            }
            mSaveProgressDialog = new MaterialDialog.Builder(this)
                    .title(R.string.saving_image)
                    .content(R.string.wait)
                    .progress(true, 0).build();

            mSaveProgressDialog.show();
            final String filename = UUID.randomUUID().toString();
            new SaveTask(this, new SaveTask.OnPictureSavedListener() {
                @Override
                public void onPictureSaved(Uri uri) {
                    Toast.makeText(HomeActivity.this, "File saved " + filename, Toast.LENGTH_SHORT)
                            .show();
                    if (mSaveProgressDialog != null) {
                        mSaveProgressDialog.dismiss();
                        mSaveProgressDialog = null;
                    }
                    MediaScannerConnection.scanFile(HomeActivity.this, new String[] { sharedfile },
                            new String[] { "image/png" }, null);
                }
            }).execute(drawView.getSaveBitmap(), "shapecollage", filename);

        }
        return true;
    }

    @Override
    protected void onResume() {
        // adView.resume();

        super.onResume();
    }

    @Override
    protected void onPause() {
        // adView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // adView.destroy();
        drawView.recycle();

    }

    private void showInterstitial() {
    }

    @Override
    public void onBackPressed() {
        if (firstBack) {
            firstBack = false;
            Toast.makeText(this, R.string.warn_exit, Toast.LENGTH_SHORT).show();
        } else {
            showInterstitial();
            finish();

        }

    }

    @Override
    public void onLayerChanged(Layer activeLayer, int index) {
        firstBack = true;
        if (activeLayer != null) {
            if (activeLayer instanceof TextLayer) {
                textControl.setVisibility(View.VISIBLE);
                TextLayer txtLayer = (TextLayer) activeLayer;
                text.setText(txtLayer.getText());
            } else {
                textControl.setVisibility(View.GONE);
            }
            delete.setEnabled(true);
            delete.setVisibility(View.VISIBLE);

            hideFilter();

        } else {

            toggleFilter();

            textControl.setVisibility(View.GONE);
            delete.setEnabled(false);
            delete.setVisibility(View.GONE);
        }

    }

    @Override
    public void onImagePicked(Bitmap icon, StickerInfo info) {

        StickerLayer layer = new StickerLayer();
        if (icon != null) {
            layer.init(this);
            layer.setBitmap(icon);
            layer.setInfo(info);
            layer.setScale(stickerScale);
            layer.setX(drawView.getImageWidth() / 2);
            layer.setY(drawView.getImageHeight() / 2);
            drawView.addLayer(layer);

            drawView.invalidate();
        }

    }

    @Override
    public void onStyleChanged(TextStyle style) {
        if (drawView.getActiveLayer() instanceof TextLayer) {
            // imageControl.setVisibility(View.GONE);
            textControl.setVisibility(View.VISIBLE);
            TextLayer txtLayer = (TextLayer) drawView.getActiveLayer();
            txtLayer.setStyle(style);
            drawView.invalidate();
        }
    }

    @Override
    public void processing() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = new MaterialDialog.Builder(this)
                .title(R.string.processing_image)
                .content(R.string.wait)
                .progress(true, 0).build();

        mProgressDialog.show();
    }

    @Override
    public void processed() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public void generating() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                processing();
            }
        });
    }

    @Override
    public void generated() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                processed();
            }
        });

    }

    @Override
    public void picked(Filter filter) {
        drawView.applyFilter(filter.getFilename());
    }
}