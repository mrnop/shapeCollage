package com.isarainc.text.styles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;

public class OutlineEmboss extends TextStyle {

	private Paint mTextPaint;

	public OutlineEmboss() {
		super();
		init();
	}

	public OutlineEmboss(Context context) {
		super(context);
		init();
	}
	public void init() {
		mTextPaint = new Paint();
	}

	/**
	 *
	 * @param canvas
	 * @param path
	 * @param text
	 */
	@Override
	protected void drawText(Canvas canvas, Path path, String text) {
	

		canvas.drawTextOnPath(text, path, 0, 1, mTextPaint);

	}

	@Override
	public int colorCount() {

		return 1;
	}

	@Override
	protected void drawText(Canvas canvas, int x, int y, String text) {
	

		canvas.drawText(text, x,y, mTextPaint);
		
	}

	@Override
	protected void prepare(Canvas canvas, String text) {
		int textColor1 = getColor(0);

		int textStroke1 = getStroke(0);

		
		mTextPaint.setTypeface(getTypeface());
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(getSize());
		mTextPaint.setTextAlign(Align.CENTER);
		mTextPaint.setColor(textColor1);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setStyle(Paint.Style.STROKE);
		mTextPaint.setStrokeWidth(textStroke1);
		EmbossMaskFilter filter = new EmbossMaskFilter(
				new float[] { 0f, 1f, 0.5f }, 0.8f, 3f, 3f);
		mTextPaint.setMaskFilter(filter);

	}

}
