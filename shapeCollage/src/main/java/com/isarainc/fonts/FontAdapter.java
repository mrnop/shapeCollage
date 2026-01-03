package com.isarainc.fonts;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.isarainc.shapecollage.R;
import com.isarainc.shapecollage.shape.Language;

import java.util.List;


public class FontAdapter extends BaseAdapter {
	private static final String TAG = "FontAdapter";
	private static Context context;
	private List<Font> fonts;
	private FontListViewHolder viewHolder;
	//private ImageLoader loader;
	
	private FontManager fontManager;
	private static LayoutInflater inflater = null;

	public FontAdapter(Context context, List<Font> fonts) {
		FontAdapter.context = context;
		Language.init(context);
		fontManager = FontManager.getInstance(context);
		this.fonts = fonts;
		inflater = ((Activity)context).getLayoutInflater();
		
		
	}

	@Override
	public int getCount() {
		return fonts.size();
	}

	@Override
	public Object getItem(int position) {
		return fonts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Font font = fonts.get(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.i_font, null);
			viewHolder = new FontListViewHolder();
			viewHolder.view = convertView
					.findViewById(R.id.textView1);
		
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (FontListViewHolder) convertView.getTag();
		}

		viewHolder.position = position;

		if(font.getName().endsWith(".ttf")){
			viewHolder.view.setText(font.patch(font.getName().substring(0,font.getName().lastIndexOf(".ttf"))));
		}else if(font.getName().endsWith(".otf")){
			viewHolder.view.setText(font.patch(font.getName().substring(0,font.getName().lastIndexOf(".otf"))));
			
		}else{
			viewHolder.view.setText(font.patch(font.getName()));
		}
		FontHolder holder = fontManager.loadFont(font.getName());
		String[] langs = font.getLanguages().split(",");
		
		
		String word = Language.getLanguage(langs[0]);
		
		
		Log.d(TAG, "font=" +font + ":" +word + ":" +langs);
		if(holder!=null){
			if(holder.getFont().getPatch()==2){
				if(word!=null){
					viewHolder.view.setText(font.patch(word));
					viewHolder.view.setTypeface(holder.getTypeface());
				}
			}
			viewHolder.view.setTypeface(holder.getTypeface());
			
		
		}
		return convertView;
	}

	static class FontListViewHolder {
		public TextView view;
		
		public int position;

		public FontListViewHolder() {

		}
	}

}
