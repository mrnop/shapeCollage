package com.isarainc.text.layouts;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;

public class SimpleTextLayout extends TextLayout {
	private static final long serialVersionUID = 3278239446102434860L;

	protected SimpleTextLayout() {
		super();

	}

	protected SimpleTextLayout(Context context) {
		super(context);
	}

	/**
	 * Predraw to calculate properly text size;
	 * 
	 * @param canvas
	 * @param text
	 */
	public void onPreDraw(Canvas canvas,String text) {
		super.simplePreDraw(canvas,text);
	}



	@Override
	public void onDraw(Canvas canvas,String text, List<Point> mpoints) {
		super.simpleDraw(canvas,text, mpoints);

	}

	@Override
	public int styleCount() {
		return 1;
	}

}
