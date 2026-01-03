package com.isarainc.stickers;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.isarainc.shapecollage.R;
import com.isarainc.util.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class StickerFragment extends Fragment {

    private List<StickerInfo> infos = new LinkedList<StickerInfo>();
    private RecyclerView recyclerView;
    private StickerAdapter mAdapter;
    private StickerPickerDialog.OnImagePickListener onImagePickListener;

    public StickerFragment() {

    }

    public StickerPickerDialog.OnImagePickListener getOnImagePickListener() {
        return onImagePickListener;
    }

    public void setOnImagePickListener(StickerPickerDialog.OnImagePickListener onImagePickListener) {
        this.onImagePickListener = onImagePickListener;
    }

    public List<StickerInfo> getInfos() {
        return infos;
    }

    public void setInfos(List<StickerInfo> infos) {
        this.infos = infos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.f_sticker, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.stickerRecyclerView);
        mAdapter = new StickerAdapter(getContext());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(Utils.isTablet(getActivity())?6:4,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

        private static final String TAG = "StickerAdapter";
        private Context context;
        private StickerManager stickerManager;

        // Provide a reference to the views for each tasks item
        // Complex tasks items may need more than one view per item, and
        // you provide access to all the views for a tasks item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;


            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public StickerAdapter(Context context) {
            this.context = context;
            stickerManager = StickerManager.getInstance(context);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public StickerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.i_sticker, parent, false);
            StickerAdapter.ViewHolder vh = new StickerAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final StickerAdapter.ViewHolder holder, final int position) {
            final StickerInfo info=infos.get(position);
           // String path=info.getPath();
           // Bitmap bitmap = null;
            View.OnClickListener onClickListener =new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onImagePickListener!=null){
                        Bitmap bitmap = stickerManager.getStickerBitmap(info);
                        onImagePickListener.onImagePicked(bitmap,info);
                    }
                }
            };

            holder.itemView.setOnClickListener(onClickListener);
            holder.itemView.setOnClickListener(onClickListener);
            if(StickerInfo.TYPE_BUNDLE.equals(info.getType())){
                String imageFilePath = "file:///android_asset/stickers/" + info.getFolder() + "/" + info.getPath();
                Picasso.get().load(imageFilePath)
                        .placeholder(ContextCompat.getDrawable(context, R.drawable.logo)).into(holder.imageView);
            }else {
                File picture = new File(info.getPath());
                Picasso.get().load(picture)
                        .placeholder(ContextCompat.getDrawable(context, R.drawable.logo)).into(holder.imageView);
            }

        }

        @Override
        public int getItemCount() {
            return infos.size();
        }
    }


}
