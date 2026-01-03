package com.isarainc.shapecollage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import com.isarainc.arts.Art;
import com.isarainc.arts.ArtManager;
import com.isarainc.arts.ShapeCollage;
import com.isarainc.bg.BgManager;
import com.isarainc.bg.BgPickerDialogFragment;
import com.isarainc.bg.BgPickerDialogFragment.OnBgSelectListener;
import com.isarainc.dialog.ProcessListener;
import com.isarainc.fonts.FontHolder;
import com.isarainc.fonts.FontManager;
import com.isarainc.layers.Layer;
import com.isarainc.layers.OnLayerChangeListener;
import com.isarainc.layers.OnLayerDeleteClick;
import com.isarainc.polypicker.ImagePickerActivity;
import com.isarainc.shapecollage.BorderSettingDialog.OnBorderChangedListener;
import com.isarainc.shapecollage.shape.CustomDialog;
import com.isarainc.shapecollage.shape.OnShapeListener;
import com.isarainc.shapecollage.shape.ShapeInfo;
import com.isarainc.shapecollage.shape.ShapeManager;
import com.isarainc.shapecollage.shape.ShapePickerDialog;
import com.isarainc.shapecollage.shape.XmasPickerDialog;
import com.isarainc.util.SaveTask;
import com.isarainc.util.TextUtil;
import com.isarainc.util.Utils;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import java.util.UUID;

public class HomeActivity extends AppCompatActivity implements ProcessListener,
        OnShapeListener, OnLayerChangeListener {

    private static final String TAG = "HomeActivity";
    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    private static final int ID_CUSTOM = 1;
    private static final int ID_SHAPE = 2;
    private static final int ID_XMAS = 3;

    private static final int ID_SQUARE = 1;
    private static final int ID_TILESTYLE = 2;
    private static final int ID_BACKGROUND = 3;
    private static final int ID_BORDER = 4;
    protected int currentColor = Color.argb(255, 255, 255, 255);
    protected int currentBorderSize = 4;
    private DrawView drawView;
    private String sharedfile = "shared.png";
    private Bitmap shape;
    private SharedPreferences sharePrefs;

    private SeekBar imageSize;
    private int currentBorderColor = Color.argb(255, 255, 255, 255);
    private int width;
    private float sizeRatio;
    private ShareActionProvider actionProvider;
    private boolean gridTile = false;
    private ActionItem tileStyleItem;
    private MaterialDialog mProgressDialog;
    private boolean square = true;
    private boolean firstBack = true;
    private MaterialDialog mSaveProgressDialog;
    private Art art;
    private FontManager fontManager;
    private String currentBg;
    private boolean isSquare = true;
    private boolean isGrid = false;

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

    public static Bitmap decodeSampledBitmapFromResource(String strPath,
            int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(strPath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inScaled = false;
        return BitmapFactory.decodeFile(strPath, options);
    }

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_shapecollage);
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

        fontManager = FontManager.getInstance(this);
        BgManager bgManager = BgManager.getInstance(this);
        sharePrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        ActionItem shapeItem = new ActionItem(ID_SHAPE, getResources()
                .getString(R.string.shape), ContextCompat.getDrawable(HomeActivity.this, R.drawable.m_shape));
        ActionItem customItem = new ActionItem(ID_CUSTOM, getResources()
                .getString(R.string.custom),
                ContextCompat.getDrawable(HomeActivity.this,
                        R.drawable.m_custom));
        // ActionItem xmasItem = new ActionItem(ID_XMAS, getResources()
        // .getString(R.string.xmas), ContextCompat.getDrawable(HomeActivity.this,
        // R.drawable.m_xmas));

        // use setSticky(true) to disable QuickAction dialog being dismissed
        // after an item is clicked
        // customItem.setSticky(true);
        // shapeItem.setSticky(true);

        // create QuickAction. Use QuickAction.VERTICAL or
        // QuickAction.HORIZONTAL param to define layout
        // orientation
        final QuickAction shapeQuickAction = new QuickAction(this,
                QuickAction.VERTICAL);

        // add action items into QuickAction
        shapeQuickAction.addActionItem(shapeItem);
        shapeQuickAction.addActionItem(customItem);
        // shapeQuickAction.addActionItem(xmasItem);
        // Set listener for action item clicked
        shapeQuickAction
                .setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(QuickAction source, int pos,
                            int actionId) {
                        firstBack = true;
                        ActionItem actionItem = shapeQuickAction
                                .getActionItem(pos);

                        // here we can filter which action item was clicked with
                        // pos or actionId parameter
                        if (actionId == ID_SHAPE) {
                            FragmentManager fm = getSupportFragmentManager();
                            if (fm.findFragmentByTag("ShapePickerDialogFragment") == null) {

                                ShapePickerDialog newFragment = new ShapePickerDialog();
                                newFragment
                                        .setOnShapePickListener(HomeActivity.this);
                                android.os.Bundle args = new android.os.Bundle();

                                newFragment.setArguments(args);
                                newFragment.show(fm,
                                        "ShapePickerDialogFragment");

                            }
                        } else if (actionId == ID_CUSTOM) {
                            FragmentManager fm = getSupportFragmentManager();
                            if (fm.findFragmentByTag("CustomDialogFragment") == null) {
                                CustomDialog newFragment = new CustomDialog();
                                newFragment
                                        .setOnShapePickListener(HomeActivity.this);
                                android.os.Bundle args = new android.os.Bundle();

                                newFragment.setArguments(args);
                                newFragment.show(fm, "CustomDialogFragment");

                            }
                        } else if (actionId == ID_XMAS) {

                            FragmentManager fm = getSupportFragmentManager();
                            if (fm.findFragmentByTag("XmasPickerDialogFragment") == null) {

                                XmasPickerDialog newFragment = new XmasPickerDialog();
                                newFragment
                                        .setOnShapePickListener(HomeActivity.this);
                                android.os.Bundle args = new android.os.Bundle();

                                newFragment.setArguments(args);
                                newFragment.show(fm,
                                        "XmasPickerDialogFragment");

                            }
                        }
                    }
                });
        ActionItem backgroundItem = new ActionItem(ID_BACKGROUND,
                getResources().getString(R.string.background),
                ContextCompat.getDrawable(HomeActivity.this, R.drawable.m_backgound));
        tileStyleItem = new ActionItem(ID_TILESTYLE,
                getResources().getString(R.string.tileStyle),
                ContextCompat.getDrawable(HomeActivity.this, R.drawable.m_grid_no));

        ActionItem squareItem = new ActionItem(ID_SQUARE,
                getResources().getString(R.string.square),
                ContextCompat.getDrawable(HomeActivity.this, R.drawable.m_square));
        ActionItem borderItem = new ActionItem(ID_BORDER, getResources()
                .getString(R.string.border),
                ContextCompat.getDrawable(HomeActivity.this,
                        R.drawable.m_border));

        final QuickAction settingQuickAction = new QuickAction(this,
                QuickAction.VERTICAL);

        // add action items into QuickAction
        settingQuickAction.addActionItem(squareItem);
        settingQuickAction.addActionItem(tileStyleItem);
        settingQuickAction.addActionItem(backgroundItem);
        settingQuickAction.addActionItem(borderItem);

        // Set listener for action item clicked
        settingQuickAction
                .setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(QuickAction source, int pos,
                            int actionId) {
                        // ActionItem actionItem = settingQuickAction.setIcon(
                        // .getActionItem(pos);

                        // here we can filter which action item was clicked with
                        // pos or actionId parameter
                        firstBack = true;
                        if (actionId == ID_SQUARE) {
                            square = !square;
                            drawView.setSquare(square);
                            drawView.reShape();
                        } else if (actionId == ID_TILESTYLE) {
                            gridTile = !gridTile;
                            if (gridTile) {
                                tileStyleItem
                                        .setIcon(ContextCompat.getDrawable(HomeActivity.this, R.drawable.m_grid_yes));
                            } else {
                                tileStyleItem
                                        .setIcon(ContextCompat.getDrawable(HomeActivity.this, R.drawable.m_grid_no));
                            }

                            drawView.setGrid(gridTile);
                            drawView.processShape();

                        } else if (actionId == ID_BACKGROUND) {

                            FragmentManager fm = getSupportFragmentManager();
                            if (fm.findFragmentByTag("BgPickerDialogFragment") == null) {
                                BgPickerDialogFragment newFragment = new BgPickerDialogFragment();
                                android.os.Bundle args = new android.os.Bundle();
                                args.putInt("oldColor", currentColor);

                                newFragment.setArguments(args);
                                newFragment.show(fm, "BgPickerDialogFragment");
                                newFragment
                                        .setOnBgSelectListener(new OnBgSelectListener() {

                                            @Override
                                            public void onColorChanged(
                                                    int bgColor) {
                                                currentColor = bgColor;
                                                drawView.setBg(null);
                                                drawView.setBackgroundColor(currentColor);
                                                drawView.invalidate();
                                                sharePrefs
                                                        .edit()
                                                        .putInt("currentColor",
                                                                currentColor)
                                                        .remove("lastBg")
                                                        .apply();
                                            }

                                            @Override
                                            public void onBgPicked(
                                                    Bitmap bitmap, String name) {

                                                drawView.setBg(bitmap);
                                                drawView.invalidate();
                                                sharePrefs
                                                        .edit()
                                                        .putString("lastBg",
                                                                name)
                                                        .remove("currentColor").apply();

                                            }

                                        });
                            }

                        } else if (actionId == ID_BORDER) {

                            FragmentManager fm = getSupportFragmentManager();
                            if (fm.findFragmentByTag("BorderSettingDialogFragment") == null) {
                                BorderSettingDialog newFragment = new BorderSettingDialog();
                                android.os.Bundle args = new android.os.Bundle();
                                args.putInt("oldBorderSize", currentBorderSize);
                                args.putInt("oldBorderColor",
                                        currentBorderColor);
                                newFragment.setArguments(args);
                                newFragment.show(fm,
                                        "BorderSettingDialogFragment");
                                newFragment
                                        .setOnBorderChangedListener(new OnBorderChangedListener() {

                                            @Override
                                            public void onBorderChanged(
                                                    int borderColor, int size) {
                                                currentBorderColor = borderColor;
                                                currentBorderSize = size;
                                                drawView.setBorderColor(currentBorderColor);
                                                drawView.setBorderSize(currentBorderSize);

                                                sharePrefs
                                                        .edit()
                                                        .putInt("currentBorderColor",
                                                                currentBorderColor)
                                                        .putInt("currentBorderSize",
                                                                currentBorderSize)
                                                        .apply();

                                                drawView.reShape();

                                            }

                                        });
                            }

                        }
                    }
                });
        // setContentView(R.layout.ac_home);
        FontManager.getInstance(this);
        ShapeManager shapeManager = ShapeManager.getInstance(this);

        //// // MobileAds.initialize(getApplicationContext(),
        // "ca-app-pub-3940256099942544~3347511713");
        //// // adView = (NativeExpressAdView) findViewById(R.id.adView);
        /*
         * // * AdRequest request = new AdRequest.Builder()
         * // * .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
         * .build();
         * //// * adView.loadAd(request);
         * 
         * // * mInterstitial = new InterstitialAd(this);
         * //// * mInterstitial.setAdUnitId("ca-app-pub-5054886841152739~6602286008");
         * 
         * //// * AdRequest adRequest = new AdRequest.Builder().addTestDevice(
         * // AdRequest.DEVICE_ID_EMULATOR).build();
         * 
         * //// * adView.loadAd(adRequest);
         * 
         * //// * mInterstitial.loadAd(adRequest);
         */

        drawView = (DrawView) this.findViewById(R.id.drawView);
        drawView.setOnLayerDeleteClick(new OnLayerDeleteClick() {

            @Override
            public void onLayerDeleteClick(Layer layer) {
                firstBack = true;
                drawView.delete();
            }
        });
        drawView.setProcessListener(this);

        width = Utils.getHeight(this);
        // Log.d(TAG, "width=" +width);
        if (width < 720) {
            width = 720;
            sizeRatio = (float) 720 / (float) 200;
            drawView.setImageResolution(720, 720);
            // drawView.setCollageSize(72);
        } else {
            sizeRatio = (float) width / (float) 200;
            try {
                drawView.setImageResolution(width, width);
            } catch (OutOfMemoryError ooe) {
                System.gc();
                width = Utils.getWidth(this);
                sizeRatio = width / 200;
                drawView.setImageResolution(width, width);
            }

        }

        final int[] gSize = { 13 };
        final int[] imSize = { (int) (gSize[0] * sizeRatio) };

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
        // sizeControl = (LinearLayout) findViewById(R.id.sizeControl);
        // layerControl = (LinearLayout) findViewById(R.id.layerControl);
        ImageButton pickShape = (ImageButton) findViewById(R.id.pickShape);
        assert pickShape != null;
        pickShape.setImageDrawable(new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_heart)
                .color(Color.WHITE)
                .sizeDp(24));

        pickShape.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                firstBack = true;
                shapeQuickAction.show(v);
                shapeQuickAction.setAnimStyle(QuickAction.ANIM_REFLECT);
            }
        });

        ImageButton pickSetting = (ImageButton) findViewById(R.id.pickSetting);
        pickSetting.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_settings)
                .color(Color.WHITE)
                .sizeDp(24));
        pickSetting.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                firstBack = true;
                settingQuickAction.show(v);
                settingQuickAction.setAnimStyle(QuickAction.ANIM_REFLECT);
            }
        });

        ImageButton layerdelete = (ImageButton) findViewById(R.id.layerdelete);
        layerdelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                drawView.delete();

            }
        });

        drawView.setOnLayerChangeListener(this);

        imageSize = (SeekBar) findViewById(R.id.collageSize);

        imageSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progressChanged = 50;

            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                progressChanged = progress;

            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                firstBack = true;
                imSize[0] = progressChanged;
                Log.d(TAG, "imageSize=" + imSize[0]);
                drawView.setCollageSize(imSize[0]);
                drawView.reShape();

            }
        });

        SeekBar gridSize = (SeekBar) findViewById(R.id.gridSize);

        assert gridSize != null;
        gridSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progressChanged = 6;

            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                progressChanged = progress;

            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                firstBack = true;
                gSize[0] = progressChanged;
                imSize[0] = (int) (gSize[0] * sizeRatio);
                drawView.setGridSize(gSize[0]);
                drawView.setCollageSize(imSize[0]);
                imageSize.setProgress(imSize[0]);
                imageSize
                        .setMax((int) (imSize[0] + width * 0.05));

                drawView.setActiveLayer(null, 0);
                drawView.processShape();

            }
        });

        if (art != null) {
            if (art.getValue() != null && art.getValue() instanceof ShapeCollage) {
                // background
                ShapeCollage sc = (ShapeCollage) art.getValue();
                isSquare = sc.isSquare();
                isGrid = sc.isGrid();
                if (sc.getBlackground() != null) {
                    if (sc.getBlackground().startsWith("#")) {
                        currentColor = Color.parseColor(sc.getBlackground());
                        currentBg = null;
                    } else {
                        currentBg = sc.getBlackground();

                    }
                }
                try {
                    currentBorderColor = Color.parseColor(sc.getBorderColor());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                currentBorderSize = sc.getBorderSize();

                // Shape
                ShapeCollage.Shape csh = sc.getShapeData();
                if (csh instanceof ShapeCollage.Bundle) {
                    ShapeCollage.Bundle cshi = (ShapeCollage.Bundle) csh;
                    ShapeInfo info = new ShapeInfo();
                    info.setFolder(cshi.getFolder());
                    info.setPath(cshi.getPath());
                    shape = shapeManager.loadShapeImage(info);
                } else if (csh instanceof ShapeCollage.Text) {
                    ShapeCollage.Text csht = (ShapeCollage.Text) csh;
                    Typeface typeface = Typeface.DEFAULT;
                    if (csht.getFont() != null) {
                        FontHolder fontHolder = fontManager.loadFont(csht.getFont());
                        // Log.d(TAG, "font change " +fontHolder);
                        if (fontHolder != null) {
                            typeface = fontHolder.getTypeface();
                        } else {
                            typeface = Typeface.DEFAULT;
                        }
                    }
                    shape = TextUtil.genBitmap(csht.getText(),
                            typeface);
                    // Log.d(TAG,"shape size=" + shape.getHeight() + "x"+ shape.getHeight());
                }

                imSize[0] = sc.getCollageSize();
                gSize[0] = sc.getGridSize();
            } else {

                currentColor = sharePrefs.getInt("currentColor", Color.WHITE);
                currentBg = sharePrefs.getString("lastBg", null);
                currentBorderColor = sharePrefs.getInt("currentBorderColor",
                        Color.WHITE);
                currentBorderSize = sharePrefs.getInt("currentBorderSize", 4);

                String lastShape = sharePrefs.getString("lastShapeInfo", null);
                if (lastShape != null) {
                    try {
                        ShapeInfo info = ShapeInfo.fromJson(lastShape);
                        shape = shapeManager.loadShapeImage(info);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (shape == null) {
                    ShapeInfo info = new ShapeInfo();
                    info.setType(ShapeInfo.TYPE_BUNDLE);
                    info.setInfo("heart3");
                    info.setFolder("2_hearts");
                    info.setPath("2_hearts/heart3.png");
                    shape = shapeManager.loadShapeImage(info);
                }
            }
        }
        imageSize.setProgress(imSize[0]);
        imageSize
                .setMax((int) (imSize[0] + width * 0.05));
        String lastPictures = sharePrefs.getString("lastPictures", null);
        if (lastPictures != null) {
            String[] pics = lastPictures.split(",");
            for (String string : pics) {
                try {
                    int collageSize = width / 4;

                    Bitmap bm = decodeSampledBitmapFromResource(string,
                            collageSize, collageSize);
                    if (bm != null) {
                        drawView.addPicture(bm);
                    }

                } catch (OutOfMemoryError oome) {
                    Bitmap bm = decodeSampledBitmapFromResource(string,
                            100, 100);
                    if (bm != null) {
                        drawView.addPicture(bm);
                    }
                }
            }
        }

        if (drawView.getPictures() == null || drawView.getPictures().isEmpty()) {
            Bitmap defaultImage = BitmapFactory.decodeResource(
                    this.getResources(), R.drawable.default_image);

            drawView.addPicture(defaultImage
                    .copy(Bitmap.Config.ARGB_8888, true));

        }

        drawView.setSquare(isSquare);
        drawView.setShape(shape);
        drawView.setGrid(isGrid);
        drawView.setCollageSize(imSize[0]);
        drawView.setGridSize(gSize[0]);
        drawView.setBackgroundColor(currentColor);
        drawView.setBorderColor(currentBorderColor);
        drawView.setBorderSize(currentBorderSize);
        if (currentBg != null) {
            Bitmap bg = bgManager.loadBgImage(currentBg);
            drawView.setBg(bg);
        }

        drawView.setActiveLayer(null, 0);
        drawView.processShape();

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
                    try {
                        drawView.clearPictures();
                    } catch (Exception e) {

                    }
                    StringBuffer sb = new StringBuffer();
                    // ImageInternalFetcher imageFetcher = new ImageInternalFetcher(this, 500);
                    for (Uri uri : uris) {

                        try {
                            int collageSize = width / 4;
                            Bitmap bm = decodeSampledBitmapFromResource(uri.toString(),
                                    collageSize, collageSize);
                            // Log.d(TAG,"bm=" + bm);
                            if (bm != null) {
                                drawView.addPicture(bm);
                            }

                        } catch (Throwable e) {
                            e.printStackTrace();
                            System.gc();
                            try {
                                Bitmap bm = decodeSampledBitmapFromResource(uri.toString(),
                                        100, 100);
                                if (bm != null) {
                                    drawView.addPicture(bm);
                                }
                            } catch (Throwable ooe) {
                                e.printStackTrace();
                                try {
                                    Bitmap bm = decodeSampledBitmapFromResource(uri.toString(),
                                            50, 50);
                                    if (bm != null) {
                                        drawView.addPicture(bm);
                                    }
                                } catch (OutOfMemoryError ooe2) {
                                    ooe2.printStackTrace();
                                }

                            }
                        }
                        sb.append(uri.toString());
                        sb.append(',');

                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    sharePrefs.edit().putString("lastPictures", sb.toString()).apply();

                    drawView.setActiveLayer(null, 0);
                    drawView.reShape();
                }
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
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

    private Intent createShareIntent() {

        String filename = "share";
        // UUID.randomUUID().toString();
        sharedfile = drawView.saveToFile(filename);

        String mimetype = "image/png"; // = your file's mimetype.
        // MimeTypeMap may be
        // useful.
        if (sharedfile != null) {
            MediaScannerConnection.scanFile(this, new String[] { sharedfile },
                    new String[] { mimetype }, null);
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");

        shareIntent.putExtra(Intent.EXTRA_STREAM,
                Uri.parse("file:///" + sharedfile));

        return shareIntent;
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

        actionProvider
                .setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener() {
                    @Override
                    public boolean onShareTargetSelected(
                            ShareActionProvider actionProvider, Intent intent) {
                        saveImageToSD();
                        return false;
                    }

                });
        actionProvider.setShareIntent(createShareIntent());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Log.d(TAG, "onMenuItemClick=" + item.getItemId());
        int id = item.getItemId();
        if (id == android.R.id.home) {
            showInterstitial();
            drawView.recycle();
            finish();
        } else if (id == R.id.save) {

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
            return true;
        } else if (id == R.id.share) {
            actionProvider.setShareIntent(createShareIntent());

        }
        return true;
    }

    private void showInterstitial() {
        if (System.currentTimeMillis() % 3 == 0) {
            // if (mInterstitial.isLoaded()) {
            // mInterstitial.show();

        } else {
            // AdRequest adRequest = new AdRequest.Builder()
            // .build();
            //// mInterstitial.loadAd(adRequest);

        }

    }

    @Override
    public void onBackPressed() {
        if (firstBack) {
            firstBack = false;
            Toast.makeText(this, R.string.warn_exit, Toast.LENGTH_SHORT).show();
        } else {
            showInterstitial();
            drawView.recycle();
            finish();

        }

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        drawView.recycle();
        super.onDestroy();
    }

    @Override
    public void onShapePicked(final Bitmap bitmap, ShapeInfo info, String extra) {
        Log.d(TAG, "onShapePicked=" + info);

        drawView.setShape(bitmap);
        drawView.processShape();

        sharePrefs.edit().putString("lastShapeInfo", info.toJson()).apply();

    }

    @Override
    public void onLayerChanged(Layer activeLayer, int index) {
        firstBack = true;

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
