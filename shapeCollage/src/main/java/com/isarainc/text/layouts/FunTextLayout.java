package com.isarainc.text.layouts;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;

import com.isarainc.text.layouts.calculator.Calculator;
import com.isarainc.text.styles.TextStyle;

public class FunTextLayout extends TextLayout {
	private static final long serialVersionUID = -8575656406748445452L;

	protected FunTextLayout() {
		super();
	}

	protected FunTextLayout(Context context) {
		super(context);
	}

	@Override
	public int styleCount() {
		return 3;
	}

	@Override
	public void onDraw(Canvas canvas,String text, List<Point> mpoints) {
		String[] word = text.split(" ");
		if (word.length == 3) {
			TextStyle style1 = getStyles(0);
			TextStyle style2 = getStyles(1);
			TextStyle style3 = getStyles(2);
			updatePath(mpoints);
			PathMeasure pm = new PathMeasure(simpleDrawPath, false);
			// Extract 40 points on path

			List<Point> points = new ArrayList<Point>();
			float length = pm.getLength();
			float distance = 0f;
			float speed = length / 40;
			int counter = 0;
			float[] aCoordinates = new float[2];

			while ((distance < length) && (counter < 40)) {
				// get point from the path
				pm.getPosTan(distance, aCoordinates, null);
				points.add(new Point((int) aCoordinates[0],
						(int) aCoordinates[1]));
				counter++;
				distance = distance + speed;
			}

			Paint mTextPaint = new Paint();
			mTextPaint.setTypeface(style1.getTypeface());
			mTextPaint.setAntiAlias(true);
			mTextPaint.setTextSize(getTextSizes(0));
			mTextPaint.setTextAlign(Align.CENTER);
			mTextPaint.setStyle(Paint.Style.FILL);

			// Recalculate whole font Size

			Rect bounds = new Rect();
			float textSize = getTextSizes(0);
			float oldTextSize = getTextSizes(0);
			mTextPaint.getTextBounds(text, 0, text.length(), bounds);
			while (bounds.width() > canvas.getWidth() * 0.80) {
				textSize = textSize - 4;
				mTextPaint.setTextSize(textSize);
				mTextPaint.getTextBounds(text, 0, text.length(), bounds);
			}
			style1.setSize(textSize);
			// Log.d("TextSize", "getStyle1 "+style1.getSize() + " txt Size="
			// +textSize);
			if (style2 != null) {
				style2.setSize(textSize
						+ ((getTextSizes(1) - oldTextSize) * textSize / oldTextSize));
				// Log.d("TextSize", "getStyle2 "+style2.getSize());
			}
			if (style3 != null) {
				style3.setSize(textSize
						+ ((getTextSizes(2) - oldTextSize) * textSize / oldTextSize));
				// Log.d("TextSize", "getStyle3 "+style3.getSize());
			}

			// Find word 1 size
			Rect bound1 = new Rect();
			mTextPaint.setTextSize(style1.getSize());
			mTextPaint.setTypeface(style1.getTypeface());
			mTextPaint.getTextBounds(word[0] + "  ", 0, word[0].length() + 2,
					bound1);
			Rect bound2 = new Rect();
			mTextPaint.setTextSize(style2.getSize());
			mTextPaint.setTypeface(style2.getTypeface());
			mTextPaint.getTextBounds(word[1] + "  ", 0, word[1].length() + 2,
					bound2);
			Rect bound3 = new Rect();
			mTextPaint.setTextSize(style3.getSize());
			mTextPaint.setTypeface(style3.getTypeface());
			mTextPaint.getTextBounds(word[2] + "  ", 0, word[2].length() + 2,
					bound3);
			// Extract Point for particular path
			List<Point> points1 = new ArrayList<Point>();
			List<Point> points2 = new ArrayList<Point>();
			List<Point> points3 = new ArrayList<Point>();
			Point firstPoint3 = points.get(points.size() - 1);
			Point firstPoint2 = firstPoint3;
			Point firstPoint1 = firstPoint2;
			int space1 = 0;
			int space2 = 0;
			int space3 = 0;
			for (int i = points.size() - 1; i > 0; i--) {
				Point point = points.get(i);
				if (Calculator.distance(point, firstPoint3) < bound3.width()) {
					points3.add(0, point);
					firstPoint2 = point;
				} else if (Calculator.distance(point, firstPoint2) < bound2
						.width()) {
					// Append space between word
					if (space1 < 2) {
						points3.add(0, point);
						firstPoint2 = point;
						space1++;
					} else {
						points2.add(0, point);
						firstPoint1 = point;
					}

				} else if (Calculator.distance(point, firstPoint1) < bound1
						.width()) {
					// Append space between word
					if (space2 < 2) {
						points2.add(0, point);
						firstPoint1 = point;
						space2++;
					} else {
						points1.add(0, point);
					}
				} else {
					if (space3 < 2) {
						points1.add(0, point);
						space3++;
					}
				}
			}
			// Cal current Angle
			if (points2.size() > 2) {
				Point pointFirst = points2.get(0);
				Point pointLast = points2.get(points2.size() - 1);
				float deltaX = pointLast.x - pointFirst.x;
				float deltaY = pointLast.y - pointFirst.y;
				float angle = (float) Math
						.toDegrees(Math.atan2(deltaY, deltaX));
				if (angle < 0) {
					angle += 360;
				}
				angle = angle - 80;
				// Log.d("angle", ""+angle);
				for (Point point : points2) {
					point.x = (int) (point.x + (bound2.height())
							* Math.cos(angle));
					point.y = (int) (point.y + (bound2.height())
							* Math.sin(angle));
				}
			}
			// Path path1 = PathHelper.quadDraw(points1);
			// Path path2 = PathHelper.quadDraw(points2);
			// Path path3 = PathHelper.quadDraw(points3);

			// style1.draw(canvas, path1, word[0]);
			// style3.draw(canvas, path3, word[2]);
			// style2.draw(canvas, path2, word[1]);

		} else {
			// Path path = PathHelper.quadDraw(mpoints);
			// getStyles(0).draw(canvas, path, text);
		}

	}

	@Override
	protected void onPreDraw(Canvas canvas, String text) {
		// TODO Auto-generated method stub
		
	}

}
