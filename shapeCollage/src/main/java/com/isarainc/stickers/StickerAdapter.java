package com.isarainc.stickers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.isarainc.shapecollage.R;

import java.io.File;
import java.util.List;

public class StickerAdapter extends BaseAdapter {

	private static final String TAG = "StickerAdapter";

	private Context context;
	private List<StickerInfo> images;

	private StickerManager stickerManager;

	public StickerAdapter(Context context,
			List<StickerInfo> pictures) {
		super();
		this.context = context;
		this.images = pictures;
		stickerManager = StickerManager.getInstance(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		//final StickerInfo pic = (StickerInfo) getItem(position);
		if (convertView == null) {
			LayoutInflater layoutInflator = LayoutInflater.from(context);
			convertView = layoutInflator.inflate(R.layout.i_sticker, parent,false);
			holder = new ViewHolder();
			holder.imageView = convertView
					.findViewById(R.id.imageView);

			// holder.profilePic = (ImageView) convertView
			// .findViewById(R.id.profilePic);
		
			holder.position = position;

			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();
		holder.position = position;
		StickerInfo info=images.get(position);
		String path=info.getPath();
		Bitmap bitmap =null;
		if(StickerInfo.TYPE_BUNDLE.equals(info.getType())){
			bitmap = stickerManager.getStickerFromAssets(info.getFolder(),path);
		}else {
			File picture = new File(path);
			if (picture.exists()) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = false;
				options.inPreferredConfig = Config.RGB_565;
				try {
					bitmap = BitmapFactory.decodeFile(
							picture.getAbsolutePath(), options);
				
				} catch (OutOfMemoryError oome) {
					System.gc();
					try {
					 bitmap = BitmapFactory.decodeFile(
								picture.getAbsolutePath(), options);
					
					} catch (OutOfMemoryError oome2) {
						System.gc();
						options.inSampleSize = 3;
						try {
						 bitmap = BitmapFactory.decodeFile(
									picture.getAbsolutePath(), options);
							
						} catch (Exception e) {

						}
					}
				}

			}
		}
		if(bitmap!=null){
			//Log.d(TAG, "getView=" + folder+"/"+image);
			holder.imageView.setImageBitmap(bitmap);
			double ratio=((double)bitmap.getHeight())/((double)bitmap.getWidth());
			//holder.imageView.setHeightRatio(ratio);

		}
		return convertView;
	}

	@Override
	public void notifyDataSetChanged() {
		// System.gc();
		super.notifyDataSetChanged();
	}

	static class ViewHolder {
		ImageView imageView;

		int position;
	}

	@Override
	public int getCount() {
		return images.size();
	}

	@Override
	public Object getItem(int position) {
		return images.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
