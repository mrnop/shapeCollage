package com.isarainc.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.isarainc.shapecollage.R;
import com.isarainc.shapecollage.shape.ShapeInfo;
import com.isarainc.shapecollage.shape.ShapeManager;

import java.util.List;

public class MyShapeAdapter extends BaseAdapter {

    public interface ShapeDeleteListener {
        void onDeleted(ShapeInfo info);
    }

    // private static final String TAG = "ItemAdapter";
    private Context context;
    private List<ShapeInfo> shapes;
    protected int finalHeight;
    protected int finalWidth;
    private ShapeManager shapeManager;

    private ShapeDeleteListener shapeDeleteListener;

    public MyShapeAdapter(Context context, List<ShapeInfo> shapes) {
        super();
        this.context = context;
        this.shapes = shapes;
        shapeManager = ShapeManager.getInstance(context);
    }

    public ShapeDeleteListener getShapeDeleteListener() {
        return shapeDeleteListener;
    }

    public void setShapeDeleteListener(ShapeDeleteListener shapeDeleteListener) {
        this.shapeDeleteListener = shapeDeleteListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        final ShapeInfo info = (ShapeInfo) getItem(position);
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(context);
            convertView = layoutInflator.inflate(R.layout.i_myshape, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView
                    .findViewById(R.id.imageView);

            holder.delete = (ImageButton) convertView.findViewById(R.id.delete);

            holder.position = position;

            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();
        holder.position = position;

        if (holder.sign != null) {
            holder.sign.recycle();
        }
        holder.info = info;
        holder.sign = shapeManager.loadShapeImage(info);
        if (holder.sign != null) {

            holder.imageView.setImageBitmap(holder.sign);


        }
        holder.delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                shapeManager.delete(info);
                if (shapeDeleteListener != null) {
                    shapeDeleteListener.onDeleted(info);
                }
            }

        });

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        // System.gc();
        super.notifyDataSetChanged();
    }

    static class ViewHolder {
        ShapeInfo info;
        Bitmap sign;
        ImageView imageView;
        ImageButton delete;
        ProgressBar progress;

        int position;
    }

    @Override
    public int getCount() {
        return shapes.size();
    }

    @Override
    public Object getItem(int position) {
        return shapes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
