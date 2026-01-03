package com.isarainc.namecollage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.FragmentManager;
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
////import com.google.android.gms.ads.AdRequest;
////import com.google.android.gms.ads.AdView;
////import com.google.android.gms.ads.InterstitialAd;
import com.isarainc.arts.Art;
import com.isarainc.arts.ArtManager;
import com.isarainc.arts.NameCollage;
import com.isarainc.bg.BgManager;
import com.isarainc.bg.BgPickerDialogFragment;
import com.isarainc.dialog.ProcessListener;
import com.isarainc.fonts.Font;
import com.isarainc.fonts.FontHolder;
import com.isarainc.fonts.FontManager;
import com.isarainc.namecollage.collage.CollageManager;
import com.isarainc.namecollage.collage.CollagePickerActivity;
import com.isarainc.shapecollage.R;
import com.isarainc.util.TextUtil;
import com.isarainc.util.Utils;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity implements ProcessListener {

    public static final int PHOTO_PICKED = 0;
    private static final String TAG = "HomeActivity";
    protected int currentColor = Color.argb(255, 255, 255, 255);
    protected Typeface typeface = Typeface.DEFAULT;
    private DrawView drawView;
    private String sharedfile = "shared.png";
    private SharedPreferences sharePrefs;

    private SeekBar gridSize;
    private FontManager fontManager;
    private List<Font> fonts;

    private CollageManager collageManager;
    private ImageButton collagePick;
    private ShareActionProvider actionProvider;
    private ImageButton pickBg;
    private BgManager bgManager;

    private MaterialDialog mProgressDialog;
    private String name;
    private boolean firstBack = true;
    private Art art;
    private float sizeRatio;
    private String currentBg;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_namecollage);
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
        fontManager = FontManager.getInstance(this);

        bgManager = BgManager.getInstance(this);

        FontManager.getInstance(this);

        //// adView = (AdView) this.findViewById(R.id.adView);

        // mInterstitial = new InterstitialAd(this);
        //// mInterstitial.setAdUnitId("ca-app-pub-5054886841152739/9555752409");

        //// AdRequest adRequest = new AdRequest.Builder().addTestDevice(
        // AdRequest.DEVICE_ID_EMULATOR).build();

        //// adView.loadAd(adRequest);

        //// mInterstitial.loadAd(adRequest);

        drawView = (DrawView) this.findViewById(R.id.drawView);
        drawView.setProcessListener(this);
        int width = Utils.getHeight(this);
        // Log.d(TAG, "width=" +width);
        if (width < 720) {
            width = 720;
            sizeRatio = (float) 720 / (float) 200;
            drawView.setImageResolution(720, 720);

        } else {
            sizeRatio = (float) width / (float) 200;
            drawView.setImageResolution(width, width);

        }
        Utils.verifyStoragePermissions(this);
        collageManager = CollageManager.getInstance(this);

        name = sharePrefs.getString("lastText", "Name");
        String lastFont = sharePrefs.getString("lastFont", "ColabLig.otf");
        FontHolder fontHolder = fontManager.loadFont(lastFont);
        // Log.d(TAG, "font change " +fontHolder);
        if (fontHolder != null) {
            typeface = fontHolder.getTypeface();
        } else {
            typeface = Typeface.DEFAULT;
        }
        ImageButton updateName = (ImageButton) findViewById(R.id.updateName);
        updateName.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_format_color_text)
                .color(Color.WHITE)
                .sizeDp(24));
        updateName.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                firstBack = true;
                FragmentManager fm = getSupportFragmentManager();
                if (fm.findFragmentByTag("NameDialogFragment") == null) {
                    NameDialogFragment newFragment = new NameDialogFragment();
                    Bundle args = new Bundle();
                    args.putString("name", name);

                    newFragment.setArguments(args);
                    newFragment.show(fm, "NameDialogFragment");
                    newFragment.setOnNameUpdateListener(new NameDialogFragment.OnNameUpdateListener() {
                        @Override
                        public void nameChanged(String name, Typeface typeface) {
                            HomeActivity.this.name = name;
                            Bitmap bitmap = TextUtil.genBitmap(name,
                                    typeface);
                            // Log.d(TAG, "shape=" +bitmap);
                            drawView.setShape(bitmap);

                            drawView.invalidate();
                            sharePrefs
                                    .edit()
                                    .putString("lastText", name).apply();
                        }

                        @Override
                        public void nameCancel() {

                        }

                    });
                }

            }
        });

        pickBg = (ImageButton) findViewById(R.id.pickBackground);
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
                                    drawView.setBackgroundColor(currentColor);
                                    drawView.invalidate();
                                    sharePrefs
                                            .edit()
                                            .putInt("nameColor",
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
                                            .putString("lastNameBg",
                                                    name)
                                            .apply();
                                }

                            });
                }

            }
        });

        collagePick = (ImageButton) findViewById(R.id.collagePick);
        collagePick.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_add_to_photos)
                .color(Color.WHITE)
                .sizeDp(24));
        collagePick.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                firstBack = true;
                Intent i = new Intent(HomeActivity.this, CollagePickerActivity.class);
                startActivityForResult(i, 1);

            }
        });
        SeekBar collageSize = (SeekBar) findViewById(R.id.collageSize);

        collageSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progressChanged = 1;

            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                progressChanged = progress;

            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                firstBack = true;
                drawView.setCollageSize(progressChanged);
                drawView.processImage();
                drawView.processShape();

            }
        });
        gridSize = (SeekBar) findViewById(R.id.gridSize);

        gridSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progressChanged = 1;

            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                progressChanged = progress;

            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                firstBack = true;
                Log.d(TAG, "gridSize=" + progressChanged);
                drawView.setGridSize(progressChanged);
                drawView.processShape();

            }
        });
        int gSize = 13;
        int cSize = (int) (gSize * sizeRatio);

        if (art != null) {
            if (art.getValue() instanceof NameCollage) {
                // background
                NameCollage sc = (NameCollage) art.getValue();
                if (sc.getBlackground() != null) {
                    if (sc.getBlackground().startsWith("#")) {
                        currentColor = Color.parseColor(sc.getBlackground());
                        currentBg = null;
                    } else {

                        currentBg = sc.getBlackground();
                    }
                }
                if (sc.getFont() != null) {
                    fontHolder = fontManager.loadFont(sc.getFont());
                    // Log.d(TAG, "font change " +fontHolder);
                    if (fontHolder != null) {
                        typeface = fontHolder.getTypeface();
                    } else {
                        typeface = Typeface.DEFAULT;
                    }
                }
                name = sc.getText();

                cSize = sc.getCollageSize();
                gSize = sc.getGridSize();
            } else {
                currentColor = sharePrefs.getInt("nameColor", Color.WHITE);
                currentBg = sharePrefs.getString("lastNameBg", null);
            }
        }

        if (currentBg != null) {
            Bitmap bg = bgManager.loadBgImage(currentBg);
            drawView.setBg(bg);

        } else {
            drawView.setBg(null);
            drawView.setBackgroundColor(currentColor);
        }

        for (String collage : collageManager.listFlowers()) {
            drawView.addPicture(collageManager.loadFlowerImage(collage));
        }

        Bitmap bitmap = TextUtil.genBitmap(name,
                typeface);
        drawView.setCollageSize(cSize);
        drawView.setGridSize(gSize);
        drawView.setShape(bitmap);
        drawView.processImage();
        drawView.invalidate();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.namecollage, menu);
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
        actionProvider.setShareIntent(createShareIntent());

        return true;
    }

    private void saveImageToSD() {
        String filename = "share";
        // UUID.randomUUID().toString();
        sharedfile = drawView.saveToFile(filename);
        Log.d(TAG, "saveImageToSD " + sharedfile);
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
            String filename = UUID.randomUUID().toString();
            sharedfile = drawView.saveToFile(filename);
            MediaScannerConnection.scanFile(this, new String[] { sharedfile },
                    new String[] { "image/png" }, null);
            Toast.makeText(this, "File saved " + sharedfile, Toast.LENGTH_SHORT)
                    .show();
        } else if (item.getItemId() == R.id.share) {
            actionProvider.setShareIntent(createShareIntent());

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                List<String> selected = data.getStringArrayListExtra("result");
                drawView.clearPictures();
                StringBuffer sb = new StringBuffer();
                for (String collage : selected) {
                    Log.d(TAG, "collage = " + collage);
                    drawView.addPicture(collageManager.loadFlowerImage(collage));
                    sb.append(collage);
                    sb.append(',');
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                // sharePrefs.edit().putString("lastNamePictures", sb.toString()).apply();
                drawView.processImage();

            }
            if (resultCode == RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    }

    private void showInterstitial() {
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
