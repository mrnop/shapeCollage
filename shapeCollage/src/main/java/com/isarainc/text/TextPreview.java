package com.isarainc.text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.isarainc.shapecollage.R;
import com.isarainc.text.styles.TextStyle;
import com.isarainc.util.Utils;

public class TextPreview extends View {

	private static final String TAG = "TextPreview";
	private TextStyle style;
	private String text;
	
	public TextPreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TextPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TextPreview(Context context) {
		super(context);
	}

	public TextStyle getStyle() {
		return style;
	}

	public void setStyle(TextStyle style) {
		String font=null;
		float size=20;
		Integer color1=null;
		Integer color2=null;
		Integer color3=null;
		Integer color4=null;
		Integer color5=null;
		boolean hasOld=false;
		if(this.style!=null){
			hasOld=true;
			font=this.style.getFont();
			size=this.style.getSize();
			color1=this.style.getColor(0);
			color2=this.style.getColor(1);
			color3=this.style.getColor(2);
			color4=this.style.getColor(3);
			color5=this.style.getColor(4);
		}
		this.style = style;
		if(hasOld){
			this.style.setFont(font);
			this.style.setSize(size);
			this.style.setColor(0, color1);
			this.style.setColor(1, color2);
			this.style.setColor(2, color3);
			this.style.setColor(3, color4);
			this.style.setColor(4, color5);
		}
		
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	private File getSaveFile(String filename) {

		if (Utils.isSDCARDMounted()) {
			File dir = new File(
					Environment.getExternalStorageDirectory()
							+ File.separator + "Android" + File.separator
							+ "data" + File.separator
							+ "com.ngone.text" + File.separator + "styles");
			
		
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File f = new File(dir, filename + ".png");
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG)
						.show();
			}
			return f;
		} else {
			File dir = new File(Environment.getDataDirectory() + File.separator
					+ File.separator + "Android" + File.separator
					+ "data" + File.separator
					+ "com.ngone.text" + File.separator + "styles");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File f = new File(dir, filename + ".png");
			try {
				f.createNewFile();
			} catch (IOException e) {

				e.printStackTrace();
				Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG)
						.show();
			}
			return f;
		}
	}
	public void save(){
		//Log.d(TAG, "style=" +style + ",text=" +text);
		Bitmap saveBitmap = Bitmap.createBitmap(120, 40,
				Bitmap.Config.ARGB_8888);
		Canvas saveCanvas = new Canvas(saveBitmap);
		//saveCanvas.drawColor(Color.WHITE);
		if(style!=null){
			style.setSize(20);
			style.draw(saveCanvas, 60, 25, text);
			OutputStream fOut = null;
			File file = getSaveFile(style.getType().toLowerCase());
			try {
				fOut = new FileOutputStream(file);
				saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
				fOut.flush();
				fOut.close();
			} catch (FileNotFoundException e) {
				Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG)
						.show();
				e.printStackTrace();
			
			} catch (IOException e) {
				Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG)
						.show();
				
			}
		}
		
		
	}
	@Override
	protected void onDraw(Canvas canvas) {
		//Log.d(TAG, "style=" +style + ",text=" +text);
		
		
		if(style!=null){
			style.setSize(getResources().getDimension(R.dimen.text_size));
			style.draw(canvas, this.getWidth()/2, this.getHeight()/2, text);
		}
	}

	
}
