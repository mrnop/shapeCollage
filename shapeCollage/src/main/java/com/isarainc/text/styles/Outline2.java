package com.isarainc.text.styles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;

public class Outline2 extends TextStyle {
	private static final long serialVersionUID = -552769057468009262L;
	private Paint mTextPaint;
	private Paint mTextPaintOutline;

	public Outline2() {
		super();
		init();
	}

	public Outline2(Context context) {
		super(context);
		init();
	}

	public void init() {
		mTextPaint = new Paint();
		mTextPaintOutline = new Paint();
		new Paint();
	}

	protected void loadDefaultStroke() {
		strokes.add(3);
		strokes.add(6);

	}

	/**
	 * @param canvas
	 * @param Path
	 *            path,String text, Typeface font, float textSize, int
	 *            textColor1,int textColor2, int textColor3
	 */
	@Override
	protected void drawText(Canvas canvas, Path path, String text) {


		canvas.drawTextOnPath(text, path, 0, 1, mTextPaintOutline);
		canvas.drawTextOnPath(text, path, 0, 1, mTextPaint);

	}

	@Override
	public int colorCount() {

		return 2;
	}

	@Override
	protected void drawText(Canvas canvas, int x, int y, String text) {
		canvas.drawText(text, x, y, mTextPaintOutline);
		canvas.drawText(text, x, y, mTextPaint);

	}

	@Override
	protected void prepare(Canvas canvas, String text) {
		int textColor1 = getColor(0);
		int textColor2 = getColor(1);

		int textStroke1 = getStroke(0);
		int textStroke2 = getStroke(1);

		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(getSize());
		mTextPaint.setTypeface(getTypeface());
		mTextPaint.setTextAlign(Align.CENTER);
		mTextPaint.setColor(textColor1);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setStyle(Paint.Style.STROKE);
		mTextPaint.setStrokeWidth(textStroke1);

		mTextPaintOutline.setTypeface(getTypeface());
		mTextPaintOutline.setAntiAlias(true);
		mTextPaintOutline.setTextSize(this.getSize());
		mTextPaintOutline.setColor(textColor2);
		mTextPaintOutline.setTextAlign(Align.CENTER);
		mTextPaintOutline.setStyle(Paint.Style.STROKE);
		mTextPaintOutline.setStrokeWidth(textStroke2);
		
	}

}
