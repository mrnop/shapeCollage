package com.isarainc.shapecollage.shape;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.isarainc.shapecollage.R;

import java.util.List;

public class ShapeAdapter extends BaseAdapter {

    // private static final String TAG = "ItemAdapter";
    private Context context;
    private List<ShapeInfo> shapes;
    private ShapeManager shapeManager;

    public ShapeAdapter(Context context, List<ShapeInfo> shapes) {
        super();
        this.context = context;
        this.shapes = shapes;
        shapeManager = ShapeManager.getInstance(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        final ShapeInfo info = (ShapeInfo) getItem(position);
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(context);
            convertView = layoutInflator.inflate(R.layout.i_shape, parent,false);

            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView
                    .findViewById(R.id.imageView);


            holder.position = position;

            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();
        holder.position = position;

        if (holder.sign != null) {
            holder.sign.recycle();
        }

        holder.sign = shapeManager.loadShapeImage(info);
        if (holder.sign != null) {

            holder.imageView.setImageBitmap(holder.sign);


        }
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        // System.gc();
        super.notifyDataSetChanged();
    }

    static class ViewHolder {
        Bitmap sign;
        ImageView imageView;

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
