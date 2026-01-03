package com.isarainc.polypicker;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.isarainc.shapecollage.R;
import com.isarainc.util.ImageInternalFetcher;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class ImagePickerActivity extends AppCompatActivity {

    private static final String TAG = ImagePickerActivity.class.getSimpleName();

    /**
     * Key to persist the list when saving the state of the activity.
     */
    private static final String KEY_LIST = "com.isarainc.picker.savedinstance.key.list";

    /**
     * Returns the parcelled image uris in the intent with this extra.
     */
    public static final String EXTRA_IMAGE_URIS = "com.isarainc.picker.extra.selected_image_uris";

    /**
     * Integer extra to limit the number of images that can be selected. By default the user can
     * select infinite number of images.
     */
    public static final String EXTRA_SELECTION_LIMIT = "com.isarainc.picker.extra.selection_limit";

    private Set<Image> mSelectedImages;
    private LinearLayout mSelectedImagesContainer;
    private TextView mSelectedImageEmptyMessage;

    public ImageInternalFetcher mImageFetcher;

    private int mMaxSelectionsAllowed = Integer.MAX_VALUE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_polypicker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.select_pictures);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_close)
                .color(Color.WHITE)
                .sizeDp(24));
        mSelectedImagesContainer = (LinearLayout) findViewById(R.id.selected_photos_container);
        mSelectedImageEmptyMessage = (TextView) findViewById(R.id.selected_photos_empty);
        //  main = (FrameLayout) findViewById(R.id.main);

        getSupportFragmentManager().beginTransaction().add(R.id.main, new GalleryFragment()).commit();


        mSelectedImages = new HashSet<Image>();
        mImageFetcher = new ImageInternalFetcher(this, 500);


        mMaxSelectionsAllowed = getIntent().getIntExtra(EXTRA_SELECTION_LIMIT, Integer.MAX_VALUE);


        if (savedInstanceState != null) {
            populateUi(savedInstanceState);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            finish();

            return true;
        } else if (id == R.id.action_check) {


            Uri[] uris = new Uri[mSelectedImages.size()];
            int i = 0;
            for (Image img : mSelectedImages) {
                uris[i++] = img.mUri;
            }
            Intent intent = new Intent();
            intent.putExtra(EXTRA_IMAGE_URIS, uris);
            setResult(Activity.RESULT_OK, intent);

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateUi(Bundle savedInstanceState) {
        ArrayList<Image> list = savedInstanceState.getParcelableArrayList(KEY_LIST);

        if (list != null) {
            for (Image image : list) {
                addImage(image);
            }
        }
    }

    public boolean addImage(Image image) {

        if (mSelectedImages == null) {
            // this condition may arise when the activity is being
            // restored when sufficient memory is available. onRestoreState()
            // will be called.
            mSelectedImages = new HashSet<Image>();
        }

        if (mSelectedImages.size() == mMaxSelectionsAllowed) {
            Toast.makeText(this, mMaxSelectionsAllowed + " images selected already", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (mSelectedImages.add(image)) {
                View rootView = LayoutInflater.from(ImagePickerActivity.this).inflate(R.layout.list_item_selected_thumbnail, null);
                ImageView thumbnail = (ImageView) rootView.findViewById(R.id.selected_photo);
                rootView.setTag(image.mUri);
                mImageFetcher.loadImage(image.mUri, thumbnail);
                mSelectedImagesContainer.addView(rootView, 0);

                int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
                thumbnail.setLayoutParams(new FrameLayout.LayoutParams(px, px));

                if (mSelectedImages.size() >= 1) {
                    mSelectedImagesContainer.setVisibility(View.VISIBLE);
                    mSelectedImageEmptyMessage.setVisibility(View.GONE);
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.picker, menu);

        MenuItem checkItem = menu.findItem(R.id.action_check);
        // set your desired icon here based on a flag if you like
        checkItem.setIcon(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_check)
                .color(Color.WHITE)
                .sizeDp(24));
        return true;
    }



    public boolean removeImage(Image image) {
        if (mSelectedImages.remove(image)) {
            for (int i = 0; i < mSelectedImagesContainer.getChildCount(); i++) {
                View childView = mSelectedImagesContainer.getChildAt(i);
                if (childView.getTag().equals(image.mUri)) {
                    mSelectedImagesContainer.removeViewAt(i);
                    break;
                }
            }

            if (mSelectedImages.size() == 0) {
                mSelectedImagesContainer.setVisibility(View.GONE);
                mSelectedImageEmptyMessage.setVisibility(View.VISIBLE);
            }
            return true;
        }
        return false;
    }

    public boolean containsImage(Image image) {
        return mSelectedImages.contains(image);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // preserve already taken images on configuration changes like
        // screen rotation or activity run out of memory.
        // HashSet cannot be saved, so convert to list and then save.
        ArrayList<Image> list = new ArrayList<Image>(mSelectedImages);
        outState.putParcelableArrayList(KEY_LIST, list);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        populateUi(savedInstanceState);
    }


}