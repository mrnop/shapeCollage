package com.isarainc.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class TextUtil {



	public static Bitmap genBitmap(String text, Typeface typeface) {
		int imageWidth = 200;
		int imageHeight = 200;
		//Rect bound = new Rect();
		TextPaint mTextPaint = new TextPaint();
		mTextPaint.setFilterBitmap(false);
		mTextPaint.setDither(true);
		mTextPaint.setAntiAlias(true);

		Bitmap bgBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
				Bitmap.Config.ARGB_8888);
		Canvas bgCanvas = new Canvas(bgBitmap);
		int textSize = 100;
		if (typeface != null) {
			Rect bound = new Rect();
			mTextPaint.setTypeface(typeface);
			if (text!=null && !TextUtils.isEmpty(text.trim()) ) {
				mTextPaint.getTextBounds(text, 0, text.length(), bound);
				if (bound.width() < 150 && bound.height() < 150) {
					while (bound.width() < 150 && bound.height() < 150) {
						textSize += 1;
						mTextPaint.setTextSize(textSize);
						mTextPaint.getTextBounds(text, 0, text.length(), bound);
						//Log.d(TAG, "textSize=" +textSize + " bound=" + bound.width()+"x"+ bound.height());
					}
				}

				if (bound.width() > 150 || bound.height() > 150) {
					while (bound.width() > 150 || bound.height() > 150) {
						textSize -= 1;
						mTextPaint.setTextSize(textSize);
						mTextPaint.getTextBounds(text, 0, text.length(), bound);
						//Log.d(TAG, "textSize=" +textSize + " bound=" + bound.width()+"x"+ bound.height());
					}
				}
			} else {
				textSize = 100;
				mTextPaint.setTextSize(textSize);
			}
		}

		bgCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		mTextPaint.setTextAlign(Align.CENTER);
		bgCanvas.drawText(text, imageWidth/2,
				imageHeight/2 - ((mTextPaint.descent() + mTextPaint.ascent()) / 2),
				mTextPaint);
		int width = bgBitmap.getWidth();
		int height = bgBitmap.getHeight();

		int top = Integer.MAX_VALUE;
		int left = Integer.MAX_VALUE;
		int right = Integer.MIN_VALUE;
		int bottom = Integer.MIN_VALUE;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// a[i] = Color.alpha(bitmap.getPixel(i, j));
				// Log.d(TAG, "alpha= " +Color.alpha(bitmap.getPixel(i, j)));
				if (Color.alpha(bgBitmap.getPixel(i, j)) > 0) {
					if (j < top) {
						top = j;
					}
					if (j > bottom) {
						bottom = j;
					}
					if (i < left) {
						left = i;
					}
					if (i > right) {
						right = i;
					}
				}

			}

		}
        int cropW=(right - left);
        int cropH=(bottom - top);
        if(cropW<=0 || cropH <=0){
            cropW=imageWidth;
            cropH= imageHeight;
        }
		Bitmap cropBitmap = Bitmap.createBitmap(cropW,cropH
				, Bitmap.Config.ARGB_8888);
		Canvas cropCanvas = new Canvas(cropBitmap);
		cropCanvas.drawBitmap(bgBitmap, ((-1) * left),
				((-1) * top), null);
		
		Bitmap saveBitmap = null;
		try {
			saveBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
					Bitmap.Config.ARGB_8888);
		} catch (OutOfMemoryError oome) {
			System.gc();
			saveBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
					Bitmap.Config.RGB_565);
		}
		Canvas saveCanvas = new Canvas(saveBitmap);

		saveCanvas.drawBitmap(cropBitmap, (imageWidth-cropBitmap.getWidth())/2, (imageHeight-cropBitmap.getHeight())/2, null);
		OutputStream fOut = null;
		File file = getSaveFile();
		try {
			fOut = new FileOutputStream(file);
			saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
			return null;
		} catch (IOException e) {
			
			return null;
		}
		cropBitmap.recycle();
		bgBitmap.recycle();
		return saveBitmap;
	}
	
	/**
	 * 
	 * @return
	 */
	public static File getSaveFile() {
		String filename ="temp";
		if (Utils.isSDCARDMounted()) {
			File dir = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "Android" + File.separator + "data"
					+ File.separator + "com.isarainc.shapecollage"
					+ File.separator + "temp");

			if (!dir.exists()) {
				dir.mkdirs();
			}
			File f = new File(dir, filename + ".png");
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				
			}
			return f;
		} else {
			File dir = new File(Environment.getDataDirectory() + File.separator
					+ "Android" + File.separator + "data" + File.separator
					+ "com.isarainc.namecollage" + "temp");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File f = new File(dir, filename + ".png");
			try {
				f.createNewFile();
			} catch (IOException e) {

				e.printStackTrace();
			
			}
			return f;
		}
	}
}
