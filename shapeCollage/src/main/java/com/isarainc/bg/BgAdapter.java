package com.isarainc.bg;

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

public class BgAdapter extends BaseAdapter {

	// private static final String TAG = "ItemAdapter";
	private Context context;
	private List<String> templates;
	protected int finalHeight;
	protected int finalWidth;
	private BgManager shapeManager;

	public BgAdapter(Context context, List<String> pictures) {
		super();
		this.context = context;
		this.templates = pictures;
		shapeManager = BgManager.getInstance(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		final String shape = (String) getItem(position);
		if (convertView == null) {
			LayoutInflater layoutInflator = LayoutInflater.from(context);
			convertView = layoutInflator.inflate(R.layout.i_item, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.imageView);

			// holder.profilePic = (ImageView) convertView
			// .findViewById(R.id.profilePic);
			
			holder.position = position;

			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();
		holder.position = position;

		if (holder.sign != null) {
			holder.sign.recycle();
		}

		holder.sign = shapeManager.loadThumbImage(shape);
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
		return templates.size();
	}

	@Override
	public Object getItem(int position) {
		return templates.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
