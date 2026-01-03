package com.isarainc.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.isarainc.arts.Art;
import com.isarainc.shapecollage.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


public class ArtRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ART_VIEW_TYPE = 0;
    private static final String TAG = "ArtRecycler";
    private final Context mContext;
    private final List<Object> mRecyclerViewItems;
    private OnArtClickListener onPictureClickListener;

    public ArtRecyclerViewAdapter(Context context, List<Object> recyclerViewItems) {
        this.mContext = context;
        this.mRecyclerViewItems = recyclerViewItems;
    }

    public OnArtClickListener getOnPictureClickListener() {
        return onPictureClickListener;
    }

    public void setOnPictureClickListener(OnArtClickListener onPictureClickListener) {
        this.onPictureClickListener = onPictureClickListener;
    }

    @Override
    public int getItemCount() {
        return mRecyclerViewItems.size();
    }

    /**
     * Determines the view type for the given position.
     */
    @Override
    public int getItemViewType(int position) {
        //Object item = mRecyclerViewItems.get(position);
        return ART_VIEW_TYPE;
    }

    /**
     * Creates a new view for a menu item view or a Native Express ad view
     * based on the viewType. This method is invoked by the layout manager.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case ART_VIEW_TYPE:
                View pictureView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.i_art, viewGroup, false);
                return new SavedViewHolder(pictureView);

            default:
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.i_art, viewGroup, false);
                return new SavedViewHolder(view);
        }

    }

    protected Bitmap loadBitmap(File file) {
        Log.d(TAG, "loadBitmap = " + file);
        if (file != null && file.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            try {
                return BitmapFactory.decodeFile(file.getPath(),
                        options);
            } catch (OutOfMemoryError oome) {
                oome.printStackTrace();
            }

        }
        return null;
    }
    /**
     * Replaces the content in the views that make up the menu item view and the
     * Native Express ad view. This method is invoked by the layout manager.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ART_VIEW_TYPE:
                SavedViewHolder pictureViewHolder = (SavedViewHolder) holder;
                final Art art = (Art) mRecyclerViewItems.get(position);

                if ( art != null) {



                   //File file = new File(ArtManager.getInstance(mContext).getWorkDir(),
                   //         save.getName() + File.separator + "1" + File.separator + TEMP_PHOTO_FILE_NAME);
                  //  Log.d(TAG, "load " + file);

                    String imageFilePath = "file:///android_asset/arts/" + art.getPath() + "/" + art.getIcon();

                   // Picasso.get().load(file).resize(200,200)
                   //         .placeholder(ContextCompat.getDrawable(mContext, R.drawable.logo)).into(pictureViewHolder.preview);

                   // pictureViewHolder.title.setText(art.getName());


                    Picasso.get().load(imageFilePath)
                            .placeholder(ContextCompat.getDrawable(mContext, R.mipmap.ic_launcher)).into(pictureViewHolder.preview);
                }


                pictureViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onPictureClickListener != null) {
                            onPictureClickListener.onClick(art);
                        }
                    }
                });

                break;

        }
    }

    public static interface OnArtClickListener {
        public void onClick(Art art);
    }

    public class SavedViewHolder extends RecyclerView.ViewHolder {
        //private TextView title;
        private ImageView preview;


        SavedViewHolder(View view) {
            super(view);
            preview = (ImageView) view.findViewById(R.id.preview);
           // title = (TextView) view.findViewById(R.id.title);
        }
    }



}
