package com.isarainc.text.styles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Rect;

public class Shadow extends TextStyle {
	private static final long serialVersionUID = -552769057468009262L;
	private Paint mTextPaint;
	Rect bounds = new Rect();
	
	public Shadow() {
		super();
		init();
	}

	public Shadow(Context context) {
		super(context);
		init();
	}
	public void init() {
		mTextPaint = new Paint();
		
	}
	/**
	 * @param canvas
	 * @param Path
	 *            path,String text, Typeface font, float textSize, int
	 *            textColor1,int textColor2, int textColor3
	 */
	@Override
	protected void drawText(Canvas canvas, Path path, String text) {
		
		canvas.drawTextOnPath(text, path, 0, 1, mTextPaint);


	}

	@Override
	public int colorCount() {
		return 2;
	}

	@Override
	protected void drawText(Canvas canvas, int x, int y, String text) {
		canvas.drawText(text, x,y, mTextPaint);
		
	}

	@Override
	protected void prepare(Canvas canvas, String text) {
		int textColor1 = getColor(0);
		int textColor2 = getColor(1);
		
		mTextPaint.setTypeface(getTypeface());
		mTextPaint.setAntiAlias(true);
	

		mTextPaint.setTextSize(this.getSize());
		mTextPaint.setTextAlign(Align.CENTER);
		mTextPaint.setColor(textColor1);
		mTextPaint.setStyle(Paint.Style.FILL);
		float radius=this.getSize()/5;
		float d=this.getSize()/10;
		mTextPaint.setShadowLayer(radius, d, d, textColor2);
		
		mTextPaint.getTextBounds(text + "ab", 0, text.length() + 2,
				bounds);

	}

}
