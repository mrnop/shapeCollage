package com.isarainc.frame.collage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.isarainc.dialog.ProcessListener;
import com.isarainc.frame.size.Size;
import com.isarainc.frame.size.SizePickerDialogFragment;
import com.isarainc.layers.Layer;
import com.isarainc.layers.OnLayerChangeListener;
import com.isarainc.layers.StickerLayer;
import com.isarainc.layers.TextLayer;
import com.isarainc.polypicker.ImagePickerActivity;
import com.isarainc.shapecollage.R;
import com.isarainc.stickers.StickerInfo;
import com.isarainc.stickers.StickerPickerDialog;
import com.isarainc.text.StyleSettingDialog;
import com.isarainc.text.styles.TextStyle;
import com.isarainc.util.SaveTask;
import com.isarainc.util.Utils;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.Random;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity implements OnLayerChangeListener,
        StickerPickerDialog.OnImagePickListener, StyleSettingDialog.OnStyleChangeListener, ProcessListener {
    private static final String TAG = "HomeActivity";
    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    protected int currentColor = Color.argb(255, 255, 255, 255);
    Random rnd = new Random();
    private DrawView drawView;
    private String sharedfile = "shared.png";
    private SharedPreferences sharePrefs;

    private int imageSize;
    private ImageButton delete;

    private int width;
    private ShareActionProvider actionProvider;

    private BgManager bgManager;
    private LinearLayout textControl;
    private EditText text;
    private ToggleButton alongPath;
    private boolean isAlongPath = true;

    private MaterialDialog mProgressDialog;
    private boolean firstBack = true;
    private float stickerScale = 1.0f;
    private MaterialDialog mSaveProgressDialog;
    private Art art;

    public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_framecollage);
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
        width = Utils.getHeight(this);
        if (width <= 720) {
            imageSize = 720;

        } else {
            imageSize = width;
        }
        stickerScale = ((float) width) / 1000;
        textControl = (LinearLayout) findViewById(R.id.textControl);
        drawView.setOnFrameAdjustListener(new OnFrameAdjustListener() {

            @Override
            public void onAdjusting(final Layer layer, Frame frame) {
                firstBack = true;
                FragmentManager fm = getSupportFragmentManager();
                if (fm.findFragmentByTag("AdjustDialogFragment") == null) {
                    AdjustDialogFragment newFragment = new AdjustDialogFragment();

                    newFragment.setPframe(frame);
                    newFragment.setOnFrameUpdateListener(new AdjustDialogFragment.OnFrameUpdateListener() {

                        @Override
                        public void onUpdated(Frame frame) {
                            drawView.reGen(layer);
                            drawView.invalidate();

                        }

                    });
                    Bundle args = new Bundle();
                    newFragment.setArguments(args);
                    newFragment.show(fm, "AdjustDialogFragment");

                }

            }

        });
        drawView.setOnLayerChangeListener(this);
        drawView.setProcessListener(this);
        // drawView.setImageResolution(1024, 500);
        drawView.setImageResolution(imageSize, imageSize);

        ImageButton pickPictures = (ImageButton) findViewById(R.id.pickPictures);
        pickPictures.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_add_to_photos)
                .color(Color.WHITE)
                .sizeDp(24));
        pickPictures.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                firstBack = true;
                Intent intent = new Intent(HomeActivity.this, ImagePickerActivity.class);
                startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
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
                                            .putInt("frameBgColor",
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
                                            .putString("lastFrameBg",
                                                    name)
                                            .apply();

                                }

                            });
                }

            }
        });

        ImageButton setSize = (ImageButton) findViewById(R.id.setSize);
        assert setSize != null;
        setSize.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_photo_size_select_large)
                .color(Color.WHITE)
                .sizeDp(24));
        setSize.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                firstBack = true;
                FragmentManager fm = getSupportFragmentManager();
                if (fm.findFragmentByTag("SizePickerDialogFragment") == null) {
                    SizePickerDialogFragment newFragment = new SizePickerDialogFragment();
                    newFragment.setOnSizeListener(new SizePickerDialogFragment.OnSizeListener() {

                        @Override
                        public void onSizePicked(int index, Size size) {
                            if (size.isScale()) {
                                drawView.setImageResolution(width, width * size.getHeight() / size.getWidth());
                            } else {
                                drawView.setImageResolution(size.getWidth(), size.getHeight());
                            }
                            drawView.processImage();

                        }

                    });
                    Bundle args = new Bundle();

                    newFragment.setArguments(args);
                    newFragment.show(fm, "SizePickerDialogFragment");

                }

            }
        });

        ImageButton pickSticker = (ImageButton) findViewById(R.id.pickSticker);
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

        ImageButton textAdd = (ImageButton) findViewById(R.id.add);
        textAdd.setImageDrawable(new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_format_color_text)
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
                    TextStyle style = (TextStyle) (layer.getStyle()
                            .clone());
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
        String lastPictures = sharePrefs.getString("lastFramePictures", null);
        if (lastPictures != null) {
            String[] pics = lastPictures.split(",");
            for (String string : pics) {
                try {
                    Frame frame = new Frame(this);
                    frame.setSrc(string);
                    drawView.addFrame(frame);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } else {
            try {
                Frame frame = new Frame(this);
                frame.setSrcFromAsset("default1.jpg");
                drawView.addFrame(frame);
                frame = new Frame(this);
                frame.setSrcFromAsset("default2.jpg");
                drawView.addFrame(frame);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        currentColor = sharePrefs.getInt("frameBgColor", Color.WHITE);
        String lastBg = sharePrefs.getString("lastFrameBg", null);
        if (lastBg != null) {
            Bitmap bg = bgManager.loadBgImage(lastBg);
            drawView.setBg(bg);

        } else {
            drawView.setBg(null);
            drawView.setBgColor(currentColor);
        }
        drawView.processImage();
        saveImageToSD();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES) {
                Parcelable[] parcelableUris = data.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (parcelableUris == null) {
                    return;
                }

                // Java doesn't allow array casting, this is a little hack
                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

                if (uris != null) {

                    StringBuffer sb = new StringBuffer();
                    // ImageInternalFetcher imageFetcher = new ImageInternalFetcher(this, 500);
                    drawView.clearFrames();
                    for (Uri uri : uris) {
                        Frame frame = new Frame(this);
                        frame.setSrc(uri.toString());
                        drawView.addFrame(frame);
                        sb.append(uri.toString());
                        sb.append(',');

                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    sharePrefs.edit().putString("lastPictures", sb.toString()).apply();

                    sharePrefs.edit().putString("lastFramePictures", sb.toString()).apply();
                    drawView.processImage();
                }
            }
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

    private void showInterstitial() {
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
                delete.setEnabled(true);
                delete.setVisibility(View.VISIBLE);
            } else {
                textControl.setVisibility(View.GONE);
                delete.setEnabled(false);
                delete.setVisibility(View.GONE);
            }
            delete.setEnabled(true);
            delete.setVisibility(View.VISIBLE);

        } else {
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
}
