package com.isarainc.main;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import com.google.android.material.navigation.NavigationView;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.isarainc.arts.Art;
import com.isarainc.arts.ArtManager;
import com.isarainc.arts.Collage;
import com.isarainc.arts.FrameCollage;
import com.isarainc.arts.FrameMontage;
import com.isarainc.arts.NameCollage;
import com.isarainc.arts.ShapeCollage;
import com.isarainc.crop.Crop;
import com.isarainc.polypicker.ImagePickerActivity;

import com.isarainc.shapecollage.HomeActivity;
import com.isarainc.shapecollage.R;
import com.isarainc.util.ImagePicker;
import com.isarainc.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected static final String TAG = "MainActivity";
    private static final int PICK_IMAGE = 78;
    private static final int INTENT_REQUEST_GET_IMAGES = 79;

    private final Handler mDrawerActionHandler = new Handler();
    private DrawerLayout mDrawerLayout;
    private int mNavItemId;
    private Animation animation;
    private SharedPreferences sharePrefs;

    private List<Object> mArtItems = new LinkedList<>();
    private Art selectedArt;
    private File mFileTemp;

    public interface OnAdActionListener {
        void onAdClosed();
    }

    private RecyclerView mArtsRecyclerView;
    private ArtRecyclerViewAdapter mArtAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Utils.verifyStoragePermissions(this);

        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeAsUpIndicator(R.drawable.ic_drawer);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        sharePrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        mArtsRecyclerView = (RecyclerView) findViewById(R.id.art_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.HORIZONTAL, false);
        mArtsRecyclerView.setLayoutManager(layoutManager);

        mArtItems.clear();
        List arts = ArtManager.getInstance(this).listArts();
        Collections.shuffle(arts);
        mArtItems.addAll(arts);

        mArtAdapter = new ArtRecyclerViewAdapter(this, this.mArtItems);
        mArtsRecyclerView.setAdapter(mArtAdapter);
        mArtAdapter.setOnPictureClickListener(new ArtRecyclerViewAdapter.OnArtClickListener() {
            @Override
            public void onClick(Art art) {
                selectedArt = art;

                if (art.getValue() != null) {
                    if (Collage.TYPE_MULTIPLE_PICTURE.equals(art.getValue().getInputType())) {
                        Intent intent = new Intent(MainActivity.this, ImagePickerActivity.class);
                        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
                    } else if (Collage.TYPE_PICTURE.equals(art.getValue().getInputType())) {
                        Intent chooseImageIntent = ImagePicker.getChooseImageIntent(MainActivity.this);
                        startActivityForResult(chooseImageIntent, PICK_IMAGE);
                    } else {
                        startNextActivity();
                    }
                }
            }
        });

        // Ads removed
        final NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        // Ads removed logic used to be here

        Utils.verifyStoragePermissions(this);

        mFileTemp = new File(getWorkDir(), com.isarainc.frame.montage.HomeActivity.TEMP_PHOTO_FILE_NAME);

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        menuItem.setChecked(true);
                        // mDrawerLayout.closeDrawers();

                        mNavItemId = menuItem.getItemId();

                        // allow some time after closing the drawer before performing real navigation
                        // so the user can see what is happening
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                        mDrawerActionHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                navigate(menuItem.getItemId());
                            }
                        }, 250);
                        return true;
                    }
                });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "requestCode=" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == RESULT_OK && selectedArt != null) {
                    Uri uri = ImagePicker.getImageUriFromResult(this, resultCode, data);
                    if (selectedArt.getValue() != null) {
                        if (selectedArt.getValue().getWidth() != null && selectedArt.getValue().getHeight() != null) {
                            Uri outputUri = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                outputUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", mFileTemp);
                            } else {
                                outputUri = Uri.fromFile(mFileTemp);
                            }
                            Crop.of(uri, outputUri)
                                    .withAspectRatio(selectedArt.getValue().getWidth(),
                                            selectedArt.getValue().getHeight())
                                    .start(MainActivity.this);
                        } else if (selectedArt.getValue().isCrop()) {
                            Uri outputUri = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                outputUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", mFileTemp);
                            } else {
                                outputUri = Uri.fromFile(mFileTemp);
                            }
                            Crop.of(uri, outputUri).start(MainActivity.this);
                        } else {
                            Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                            OutputStream fOut = null;
                            try {
                                fOut = new FileOutputStream(mFileTemp);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                fOut.flush();
                                fOut.close();
                            } catch (Throwable t) {
                                Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG)
                                        .show();
                            }
                            startNextActivity();
                        }
                    }
                }
                break;
            case INTENT_REQUEST_GET_IMAGES:
                if (resultCode == RESULT_OK && selectedArt != null) {
                    Parcelable[] parcelableUris = data.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
                    if (parcelableUris == null) {
                        return;
                    }
                    Uri[] uris = new Uri[parcelableUris.length];
                    System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);
                    StringBuffer sb = new StringBuffer();
                    for (Uri uri : uris) {
                        sb.append(uri.toString());
                        sb.append(',');

                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    if (selectedArt.getValue() instanceof ShapeCollage) {
                        sharePrefs.edit().putString("lastPictures", sb.toString()).apply();

                    } else {
                        sharePrefs.edit().putString("lastFramePictures", sb.toString()).apply();
                    }
                    startNextActivity();
                }
                break;
            case Crop.REQUEST_CROP:
                startNextActivity();
                break;
        }
    }

    private void startNextActivity() {
        showInterstitial(new OnAdActionListener() {
            @Override
            public void onAdClosed() {
                if (selectedArt.getValue() instanceof ShapeCollage) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("ART", selectedArt.getPath());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (selectedArt.getValue() instanceof NameCollage) {
                    Intent intent = new Intent(MainActivity.this, com.isarainc.namecollage.HomeActivity.class);
                    intent.putExtra("ART", selectedArt.getPath());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (selectedArt.getValue() instanceof FrameCollage) {
                    Intent intent = new Intent(MainActivity.this, com.isarainc.frame.collage.HomeActivity.class);
                    intent.putExtra("ART", selectedArt.getPath());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (selectedArt.getValue() instanceof FrameMontage) {
                    Intent intent = new Intent(MainActivity.this, com.isarainc.frame.montage.HomeActivity.class);
                    intent.putExtra("ART", selectedArt.getPath());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Log.d(TAG, "onMenuItemClick=" + item.getItemId());
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return true;
    }

    private void navigate(final int itemId) {
        // perform the actual navigation logic, updating the main content fragment etc
        Log.d(TAG, "navigate " + itemId);
        if (itemId == R.id.nav_share) {
            try {
                String playStoreLink = "https://play.google.com/store/apps/details?id="
                        + getPackageName();

                String msg = getResources().getString(R.string.share_msg) + "\n"
                        + playStoreLink;
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                sendIntent.setType("text/plain");

                sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(sendIntent);
            } catch (Exception e) {

            }
        } else if (itemId == R.id.nav_rate) {
            try {
                Intent rateIntent = new Intent();
                rateIntent.setData(Uri.parse("market://details?id="
                        + getPackageName()));

                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(rateIntent);

            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Could not launch Play Store!",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (itemId == R.id.nav_more) {
            try {
                Intent moreIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://search?q=pub:\"Isara Inc\""));
                moreIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                moreIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(moreIntent);
            } catch (Exception e) {

            }
        } else if (itemId == R.id.nav_privacy) {
            try {
                // GitHub URL for Privacy Policy
                String url = "https://github.com/mrnop/shapeCollage/blob/main/PRIVACY_POLICY.md";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(browserIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Could not open browser!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        // //adView.resume();
        super.onResume();

    }

    private void showInterstitial(final OnAdActionListener listener) {
        listener.onAdClosed();
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {

            new AlertDialog.Builder(this)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.quit)
                    .setMessage(R.string.really_quit)
                    .setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    MainActivity.this.finish();

                                }

                            })
                    .setNegativeButton(R.string.no, null).show();

        } else {
            super.onBackPressed();
        }
    }

}
