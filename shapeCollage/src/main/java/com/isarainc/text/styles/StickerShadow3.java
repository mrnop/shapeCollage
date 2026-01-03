package com.isarainc.text.styles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Rect;

public class StickerShadow3 extends TextStyle {
	private static final long serialVersionUID = -552769057468009262L;
	private Paint mTextPaint;
	private Paint mTextPaintSticker;
	Rect bounds = new Rect();

	public StickerShadow3() {
		super();
		init();
	}

	public StickerShadow3(Context context) {
		super(context);
		init();
	}

	public void init() {
		mTextPaint = new Paint();
		mTextPaintSticker = new Paint();
	}

	/**
	 * @param canvas
	 *            path,String text, Typeface font, float textSize, int
	 *            textColor1,int textColor2, int textColor3
	 */
	@Override
	protected void drawText(Canvas canvas, Path path, String text) {
		canvas.drawTextOnPath(text, path, 0, 1, mTextPaintSticker);
		canvas.drawTextOnPath(text, path, 0, 1, mTextPaint);

	}

	@Override
	public int colorCount() {
		return 2;
	}

	@Override
	protected void drawText(Canvas canvas, int x, int y, String text) {
		canvas.drawText(text, x, y, mTextPaintSticker);
		canvas.drawText(text, x, y, mTextPaint);

	}

	@Override
	protected void prepare(Canvas canvas, String text) {
		int textColor1 = getColor(0);
		int textColor2 = getColor(1);

		
		int textStroke1 = getStroke(0);
		int textStroke2 = getStroke(1);

		mTextPaint.setTypeface(getTypeface());
		mTextPaint.setAntiAlias(true);

		mTextPaint.setTextSize(this.getSize());
		mTextPaint.setTextAlign(Align.CENTER);
		mTextPaint.setColor(textColor1);
		mTextPaint.setStyle(Paint.Style.FILL);


		mTextPaintSticker.setTypeface(getTypeface());
		mTextPaintSticker.setAntiAlias(true);
		mTextPaintSticker.setTextSize(this.getSize());
		mTextPaintSticker.setColor(Color.BLACK);
		mTextPaintSticker.setTextAlign(Align.CENTER);
		mTextPaintSticker.setStyle(Paint.Style.STROKE);
		mTextPaintSticker.setStrokeWidth(textStroke2);
		float radius=this.getSize()/5;
		float d=this.getSize()/10;
		mTextPaintSticker.setShadowLayer(radius, d, d, textColor2);
		
		mTextPaint.getTextBounds(text + "ab", 0, text.length() + 2, bounds);
		
	}

}
