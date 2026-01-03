package com.isarainc.main;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isarainc.filters.Filter;
import com.isarainc.shapecollage.R;
import com.isarainc.util.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by mrnop on 10/9/2015 AD.
 */
public class GenPreviewTask extends AsyncTask<Bitmap, Void, Void> {
    private static final String TAG = "GenPreviewTask";
    public static final int INITIAL_ITEMS_COUNT = 5;

    public interface OnPreviewListener {
        void generating();

        void generated();

        void picked(Filter filter);
    }

    private Activity activity;
    private ViewGroup container;
    private OnPreviewListener onPreviewListener;
    private Filter[] filters;

    public GenPreviewTask(Activity activity, ViewGroup container) {
        this.activity = activity;
        this.container = container;
        List<Filter> filterList = Filter.listFilters(activity);
        filters = filterList.toArray(new Filter[]{});
        loadThumb();
    }

    public OnPreviewListener getOnPreviewListener() {
        return onPreviewListener;
    }

    public void setOnPreviewListener(OnPreviewListener onPreviewListener) {
        this.onPreviewListener = onPreviewListener;
    }

    protected Void doInBackground(Bitmap... thumbs) {
        if (onPreviewListener != null) {
            onPreviewListener.generating();
        }
        Bitmap thumb = thumbs[0];


        for (int i = 0; i < filters.length; i++) {
            Log.d(TAG, "filter= " + filters[i].getFilename());
            //Bitmap saveBitmap = Bitmap.createBitmap(150, 40,
            //		Bitmap.Config.ARGB_8888);
            //Canvas saveCanvas = new Canvas(saveBitmap);
            Filter filter = filters[i];
            try {
                Bitmap thumbFilter = filter.apply(
                        thumb);
                OutputStream fOut = null;
                File file = getSaveFile(filter.getFilename());

                fOut = new FileOutputStream(file);
                thumbFilter.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                thumbFilter.recycle();
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        return null;

    }

    private File getSaveFile(String filename) {

        if (Utils.isSDCARDMounted()) {
            File dir = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Android" + File.separator + "data"
                    + File.separator + activity.getPackageName() + File.separator
                    + "filters");

            if (!dir.exists()) {
                dir.mkdirs();
            }
            File f = new File(dir, filename + ".png");
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();

            }
            return f;
        } else {
            File dir = new File(Environment.getDataDirectory()
                    + File.separator + File.separator + "Android"
                    + File.separator + "data" + File.separator
                    + activity.getPackageName() + File.separator + "filters");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File f = new File(dir, filename + ".png");
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();

            }
            return f;
        }
    }

    protected void onProgressUpdate(Void... progress) {

    }

    protected void onPostExecute(Void result) {
        if (onPreviewListener != null) {
            onPreviewListener.generated();
        }
        loadThumb();
    }

    public void loadThumb() {
        container.removeAllViews();
        // Compute the width of a carousel item based on the screen width and number of initial items.
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final int imageWidth = displayMetrics.widthPixels / INITIAL_ITEMS_COUNT;
        Log.d(TAG,"displayMetrics.widthPixels=" +displayMetrics.widthPixels+" , imageWidth="+imageWidth);




        // Populate the carousel with items
        ImageView imageItem;
        for (int i = 0; i < filters.length; ++i) {
            // Create new ImageView
            imageItem = new ImageView(activity);



            // Set the shadow background
            imageItem.setBackgroundResource(R.drawable.shadow);


            // Set the image view resource
            Bitmap image = null;
            if (Utils.isSDCARDMounted()) {
                File dir = new File(Environment.getExternalStorageDirectory()
                        + File.separator + "Android" + File.separator + "data"
                        + File.separator + activity.getPackageName() + File.separator
                        + "filters");
                File f = new File(dir, filters[i].getFilename() + ".png");
                image = BitmapFactory.decodeFile(f.getAbsolutePath());
                Picasso.get().load(f).placeholder(R.mipmap.ic_launcher).into(imageItem);
            } else {
                File dir = new File(Environment.getDataDirectory()
                        + File.separator + File.separator + "Android"
                        + File.separator + "data" + File.separator
                        + activity.getPackageName() + File.separator + "filters");
                File f = new File(dir, filters[i].getFilename() + ".png");
                image = BitmapFactory.decodeFile(f.getAbsolutePath());
            }
            LinearLayout linearLayout = new LinearLayout(activity);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            if (image != null) {

                imageItem.setImageBitmap(image);
                // imageItem.setImageResource(puppyResourcesTypedArray.getResourceId(i, -1));

                // Set the size of the image view to the previously computed value
                imageItem.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth * image.getHeight() / image.getWidth()));
                final int idx = i;
                imageItem.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "select filter " + filters[idx].getFilename());
                        if (onPreviewListener != null) {
                            onPreviewListener.picked(filters[idx]);
                        }
                    }


                });
            }
            // Defining the layout parameters of the TextView
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                    imageWidth,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            imageItem.setLayoutParams(lp1);
            imageItem.setAdjustViewBounds(true);

            linearLayout.addView(imageItem);
            // Creating a new TextView
            TextView tv = new TextView(activity);
            tv.setText(filters[i].getIdentifier() != null ? filters[i].getIdentifier() : filters[i].getFilename());
            tv.setTextSize(8);
            //tv.setShadowLayer(2,1,1, Color.GRAY);

            // Defining the layout parameters of the TextView
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            // Setting the parameters on the TextView
            tv.setLayoutParams(lp);

            // Adding the TextView to the RelativeLayout as a child
            linearLayout.addView(tv);
            /// Add image view to the carousel container
            container.addView(linearLayout);

        }
    }

}
