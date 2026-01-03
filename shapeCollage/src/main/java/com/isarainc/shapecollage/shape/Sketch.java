package com.isarainc.shapecollage.shape;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;

import com.isarainc.filters.processor.DoGFilter;
import com.isarainc.filters.util.AndroidUtils;


public class Sketch {

	public static Bitmap applyAlpha(Bitmap src, int colorToReplace,
			int tolerance) {

		int rt = Color.red(colorToReplace);
		int gt = Color.green(colorToReplace);
		int bt = Color.blue(colorToReplace);
		int width = src.getWidth();
		int height = src.getHeight();
		int[] pixels = new int[width * height];
		// get pixel array from source
		src.getPixels(pixels, 0, width, 0, 0, width, height);

		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

		int A, R, G, B;
		int pixel;

		// iteration through pixels
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				// get current index in 2D-matrix
				int index = y * width + x;
				pixel = pixels[index];

				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				if (A < 128) {
					//Skip if alpha apply
					pixels[index] = Color.argb(0, 0, 0, 0);
				} else {
					if ((R - tolerance <= rt) && (rt <= R + tolerance)
							&& (G - tolerance <= gt) && (gt <= G + tolerance)
							&& (B - tolerance <= bt) && (bt <= B + tolerance)) {
						pixels[index] = Color.argb(0, 0, 0, 0);

					} else {
						pixels[index] = Color.argb(255, 0, 0, 0);
					}
				}
			}
		}
		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
		return bmOut;
	}

	public static final Bitmap filterDoG(Bitmap src, float radius1,
			float radius2, int tolerance) {
		//System.gc();
		if(src==null){
			return src;
		}
		//Change to White BG
		Bitmap bm=Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
		Canvas canvas=new Canvas(bm);
		canvas.drawARGB(255, 255, 255,255);
		canvas.drawBitmap(src, 0, 0, null);
		int[] mColors = AndroidUtils.bitmapToIntArray(bm);
		DoGFilter filter = new DoGFilter();
		filter.setInvert(true);
		filter.setNormalize(true);
		// 0.5f
		filter.setRadius1(radius1);
		// 8.0f
		filter.setRadius2(radius2);
		int[] ret = filter.filter(mColors, src.getWidth(), src.getHeight());
		Bitmap filterBitmap = Bitmap.createBitmap(ret, 0, src.getWidth(),
				src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
		return applyAlpha(filterBitmap, Color.WHITE, tolerance);
		// return filterBitmap;
	}

}
