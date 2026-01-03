package com.isarainc.frame.size;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.isarainc.shapecollage.R;

import java.util.List;


public class SizeAdapter extends BaseAdapter {

	private Context context;
	private List<Size> sizes;
	protected int finalHeight;
	protected int finalWidth;

	public SizeAdapter(Context context, List<Size> pictures) {
		super();
		this.context = context;
		this.sizes = pictures;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		final Size size = (Size) getItem(position);
		if (convertView == null) {
			LayoutInflater layoutInflator = LayoutInflater.from(context);
			convertView = layoutInflator.inflate(R.layout.i_item, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.imageView);
			holder.position = position;

			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();
		holder.position = position;

		holder.size = size;

		int resID = context.getResources().getIdentifier(holder.size.getName(), "drawable", context.getPackageName());
		holder.imageView.setImageBitmap(BitmapFactory.decodeResource( context.getResources(),resID));



		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		// System.gc();
		super.notifyDataSetChanged();
	}

	static class ViewHolder {

		ImageView imageView;
		ProgressBar progress;
		Size size;
		int position;
	}

	@Override
	public int getCount() {
		return sizes.size();
	}

	@Override
	public Object getItem(int position) {
		return sizes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
