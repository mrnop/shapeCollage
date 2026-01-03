package com.isarainc.text.layouts.calculator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.isarainc.text.PathUtil;

public class CalculationHolder {
	protected static List<CalculationHolder> freeObjects = new LinkedList<CalculationHolder>();

	Rect bound = new Rect();
	Paint paint = new Paint();
	Typeface typeface;
	String text;
	float textSize;
	int oneCharWidth;
	int width = 0;
	int height = 0;
	int baseline = 0;
	private String txtForCal = text;
	List<Point> points = new ArrayList<Point>();
	private Path path = new Path();

	protected CalculationHolder(Typeface typeface, String text, float textSize) {
		super();
		this.setTypeface(typeface);
		this.setText(text);
		this.setTextSize(textSize);
		// this.typeface = typeface;
		// this.text = text;
		// this.textSize = textSize;
		// txtForCal = text + "a";
		// paint.setTypeface(typeface);
		// paint.setTextSize(textSize);
		//
		// paint.getTextBounds("a", 0, 1, bound);
		// oneCharWidth = bound.width();
		//
		// paint.getTextBounds(txtForCal, 0, txtForCal.length(), bound);
		// width = bound.width();
		// baseline = (int) ((paint.descent() + paint.ascent()) / 2);
	}

	protected CalculationHolder(Typeface typeface, String text) {
		super();
		this.setTypeface(typeface);
		this.setText(text);
		this.setTextSize(textSize);

		// paint.setTypeface(typeface);
		// paint.setTextSize(textSize);
		//
		// paint.getTextBounds("a", 0, 1, bound);
		// oneCharWidth = bound.width();
		//
		// paint.getTextBounds(txtForCal, 0, txtForCal.length(), bound);
		// width = bound.width();
		// baseline = (int) ((paint.descent() + paint.ascent()) / 2);

	}

	public static synchronized CalculationHolder newCalculationHolder(
			Typeface typeface, String text, float textSize) {
		if (freeObjects.isEmpty()) {
			CalculationHolder holder = new CalculationHolder(typeface, text,
					textSize);
			return holder;
		} else {
			CalculationHolder holder = freeObjects.remove(0);
			holder.setTypeface(typeface);
			holder.setText(text);
			holder.setTextSize(textSize);
			return holder;
		}

	}

	public static synchronized void freeCalculationHolder(
			CalculationHolder holder) {
		holder.points.clear();
		holder.path.reset();
		holder.width = 0;
		holder.height = 0;
		holder.baseline = 0;
		freeObjects.add(holder);
	}

	public static synchronized void freeCalculationHolder(
			List<CalculationHolder> holders) {
		for (CalculationHolder holder : holders) {
			holder.points.clear();
			holder.path.reset();
			holder.width = 0;
			holder.height = 0;
			holder.baseline = 0;
			freeObjects.add(holder);
		}

	}

	public Typeface getTypeface() {
		return typeface;
	}

	public synchronized void setTypeface(Typeface typeface) {
		this.typeface = typeface;
		paint.setTypeface(typeface);
		if (text != null) {
			paint.setTextSize(textSize);

			paint.getTextBounds("a", 0, 1, bound);
			oneCharWidth = bound.width();

			paint.getTextBounds(txtForCal, 0, txtForCal.length(), bound);
			width = bound.width();
			height = bound.height();
			baseline = (int) ((paint.descent() + paint.ascent()) / 2);
		}
	}

	public String getText() {
		return text;
	}

	public synchronized void setText(String text) {
		this.text = text;
		paint.setTextSize(textSize);
		txtForCal = text + "a";

		paint.getTextBounds("a", 0, 1, bound);
		oneCharWidth = bound.width();

		paint.getTextBounds(txtForCal, 0, txtForCal.length(), bound);
		width = bound.width();
		height = bound.height();
		baseline = (int) ((paint.descent() + paint.ascent()) / 2);
	}

	public float getTextSize() {
		return textSize;
	}

	public synchronized void setTextSize(float textSize) {
		this.textSize = textSize;
		// recal width
		paint.setTextSize(textSize);

		paint.getTextBounds("a", 0, 1, bound);
		oneCharWidth = bound.width();

		paint.getTextBounds(txtForCal, 0, txtForCal.length(), bound);
		width = bound.width();
		height = bound.height();
		baseline = (int) ((paint.descent() + paint.ascent()) / 2);
	}

	public int getOneCharWidth() {
		return oneCharWidth;
	}

	public Rect getBound() {
		return bound;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public void addPoint(Point point) {
		points.add(0, point);
	}

	public void appendPoint(Point point) {
		points.add(point);
	}

	public List<Point> getPoints() {
		return points;
	}

	public Path getPath() {
		PathUtil.updatePath(path, points);
		return path;
	}

}
