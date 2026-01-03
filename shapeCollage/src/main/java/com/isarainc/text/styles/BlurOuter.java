package com.isarainc.text.styles;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Rect;

public class BlurOuter extends TextStyle {

	private Paint mTextPaint;
	Rect bounds = new Rect();


	public BlurOuter() {
		super();
		init();
	}

	public BlurOuter(Context context) {
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

        canvas.drawText(text, x, y, mTextPaint);

	}

	@Override
	protected void prepare(Canvas canvas, String text) {
		int textColor1 = getColor(0);

		mTextPaint.setTypeface(getTypeface());
		mTextPaint.setAntiAlias(true);

		mTextPaint.setTextSize(this.getSize());
		mTextPaint.setTextAlign(Align.CENTER);
		mTextPaint.setColor(textColor1);
		//mTextPaint.setStyle(Paint.Style.FILL);

		mTextPaint.getTextBounds(text + "ab", 0, text.length() + 2, bounds);

		float radius = getSize() / 10;
		BlurMaskFilter filter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.OUTER);
		mTextPaint.setMaskFilter(filter);
	}

}
