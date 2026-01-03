package com.isarainc.text.styles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;

public class Gradient2 extends TextStyle {
	private static final long serialVersionUID = -552769057468009262L;
	private Paint mTextPaint = new Paint();
	private Paint mTextPaintOutline = new Paint();
	Rect bounds = new Rect();

	public Gradient2() {
		super();
		init();
	}

	public Gradient2(Context context) {
		super(context);
		init();
	}

	public void init() {

	}

	@Override
	public int colorCount() {
		return 3;
	}

	/**
	 * @param canvas
	 * @param Path
	 *            path,String text, Typeface font, float textSize, int
	 *            textColor1,int textColor2, int textColor3
	 */

	protected void drawText(Canvas canvas, Path path, String text) {

		canvas.drawTextOnPath(text, path, 0, 1, mTextPaintOutline);
		canvas.drawTextOnPath(text, path, 0, 1, mTextPaint);

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
		int textColor3 = getColor(2);

		int textStroke = getStroke(0);

		mTextPaint.setTypeface(getTypeface());
		mTextPaint.setAntiAlias(true);

		mTextPaint.setTextSize(getSize());
		mTextPaint.setTextAlign(Align.CENTER);
		mTextPaint.setColor(textColor1);
		mTextPaint.setStyle(Paint.Style.FILL);

		mTextPaintOutline.setTypeface(getTypeface());
		mTextPaintOutline.setAntiAlias(true);
		mTextPaintOutline.setTextSize(getSize());
		mTextPaintOutline.setColor(textColor3);
		mTextPaintOutline.setTextAlign(Align.CENTER);
		mTextPaintOutline.setStyle(Paint.Style.STROKE);
		mTextPaintOutline.setStrokeWidth(textStroke);

		mTextPaint.getTextBounds(text + "ab", 0, text.length() + 2, bounds);

		// Draw Gradient
		Shader textShader = new LinearGradient(0, 0, 0, bounds.height(),
				textColor1, textColor2, Shader.TileMode.MIRROR);
		mTextPaint.setShader(textShader);

	}

}
