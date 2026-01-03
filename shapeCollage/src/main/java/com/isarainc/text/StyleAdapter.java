package com.isarainc.text;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.isarainc.shapecollage.R;
import com.isarainc.text.styles.TextStyle;
import com.isarainc.util.Utils;

import java.io.File;
import java.util.List;


public class StyleAdapter extends BaseAdapter {
	private static final String TAG = "StyleAdapter";
	private static Context context;
	private List<TextStyle> styles;
	private FontListViewHolder viewHolder;
	private String text;
	private String font;
	private int color1;
	private int color2;
	private int color3;
	private int color4;
	private int color5;

	private static LayoutInflater inflater = null;

	public StyleAdapter(Context context,String text, List<TextStyle> styles) {
		StyleAdapter.context = context;
		this.text = text;
		this.styles = styles;
		inflater = ((Activity)context).getLayoutInflater();

	}

	@Override
	public int getCount() {
		return styles.size();
	}

	@Override
	public Object getItem(int position) {
		return styles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final TextStyle style = styles.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.i_style, null);
			viewHolder = new FontListViewHolder();
			// viewHolder.textView = (TextView) convertView
			// .findViewById(R.id.textView1);
			viewHolder.imageView = convertView
					.findViewById(R.id.imageView);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (FontListViewHolder) convertView.getTag();
		}

		viewHolder.position = position;
/*
		Bitmap styleBitmap = Bitmap.createBitmap(150, 60,
				Bitmap.Config.ARGB_8888);
		Canvas styleCanvas = new Canvas(styleBitmap);

		if (text != null && text.length() > 12) {
			style.setSize(18);
		} else {
			style.setSize(20);
		}
		style.setColor(0, color1);
		style.setColor(1, color2);
		style.setColor(2, color3);
		style.setColor(3, color4);
		style.setColor(4, color5);

		style.setFont(font);
		style.draw(styleCanvas, 75, 25, text);
		*/
		File f = null;
		if (Utils.isSDCARDMounted()) {
			File dir = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "Android" + File.separator + "data"
					+ File.separator + context.getPackageName() + File.separator
					+ "styles");

			if (!dir.exists()) {
				dir.mkdirs();
			}
			f = new File(dir, style.getType().toLowerCase() + ".png");

		} else {
			File dir = new File(Environment.getDataDirectory() + File.separator
					+ File.separator + "Android" + File.separator + "data"
					+ File.separator + context.getPackageName() + File.separator
					+ "styles");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			f = new File(dir, style.getType().toLowerCase() + ".png");
		}
		if (f != null && f.exists()) {
			viewHolder.imageView.setImageBitmap(BitmapFactory.decodeFile(f
					.getAbsolutePath()));
		} else {
			viewHolder.imageView.setImageBitmap(BitmapFactory.decodeResource(
					context.getResources(),
					context.getResources().getIdentifier(
							style.getType().toLowerCase(), "drawable",
							context.getPackageName())));

		}
		//viewHolder.imageView.setImageBitmap(styleBitmap);
		return convertView;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public int getColor1() {
		return color1;
	}

	public void setColor1(int color1) {
		this.color1 = color1;
	}

	public int getColor2() {
		return color2;
	}

	public void setColor2(int color2) {
		this.color2 = color2;
	}

	public int getColor3() {
		return color3;
	}

	public void setColor3(int color3) {
		this.color3 = color3;
	}

	public int getColor4() {
		return color4;
	}

	public void setColor4(int color4) {
		this.color4 = color4;
	}

	public int getColor5() {
		return color5;
	}

	public void setColor5(int color5) {
		this.color5 = color5;
	}

	static class FontListViewHolder {
		// public TextView textView;
		public ImageView imageView;
		public int position;

		public FontListViewHolder() {

		}
	}

}
