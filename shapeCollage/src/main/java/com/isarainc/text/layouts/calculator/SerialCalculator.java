package com.isarainc.text.layouts.calculator;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Point;

public class SerialCalculator extends Calculator {

	private static final String TAG = "SerialCalculator";
	private int overlapCharSpace = 1;
	private int maxHeight;
	private int baseline;

	public CalculationHolder getHolder(int idx) {
		return holders.get(idx);
	}

	public List<CalculationHolder> getHolders() {
		return holders;
	}

	public int sumWidth() {
		int sum = 0;
		int height = 0;
		int base = 0;
		for (CalculationHolder holder : holders) {
			// Log.d(TAG,"holder.width()="+holder.width()) ;
			sum += holder.width();
			if (height < holder.height()) {
				height = holder.height();
			}

			if (base < holder.baseline) {
				base = holder.baseline;
			}
		}
		maxHeight = height;
		baseline = base;
		return sum;
	}

	@Override
	public int getDrawHeight() {
		return maxHeight;
	}

	@Override
	public int getBaseline() {
		return baseline;
	}

	public boolean processPath(List<Point> points) {
		boolean pass = false;
		if (points.isEmpty()) {
			return pass;
		}
		int ih = holders.size() - 1;
		List<Point> suffixPoints = new LinkedList<Point>();
		List<Point> prefixPoints = new LinkedList<Point>();

		Point firstPoint = points.get(points.size() - 1);
		Point nextPoint = firstPoint;

		int offset = 0;
		int oneCharWidth = getHolder(0).getOneCharWidth();
		int spaceSize = 0;
		// Log.d(TAG, "oneCharWidth= " +oneCharWidth);
		for (int i = points.size() - 1; i > 0; i--) {
			Point point = points.get(i);
			// Spare point for 2 spaces
			if (distance(point, firstPoint) < oneCharWidth * overlapCharSpace) {
				suffixPoints.add(point);
				nextPoint = point;
				offset = i;
				// Count number of point for space size
				if (distance(point, firstPoint) < oneCharWidth
						* overlapCharSpace) {
					spaceSize++;
				}
			} else {
				break;
			}
		}
		// firstPoint = nextPoint;

		for (int i = offset; i > 0; i--) {
			// Keep suffix point
			Point point = points.get(i);

			if (ih >= 0) {
				if (distance(point, firstPoint) < getHolder(ih).width()
						+ oneCharWidth) {
					// get point for space at end of word
					if (distance(point, firstPoint) < (getHolder(ih).width())) {
						getHolder(ih).addPoint(point);
						nextPoint = point;
					} else {
						// get point for space at begin of word
						getHolder(ih).addPoint(point);
						nextPoint = point;

					}
				} else {
					firstPoint = nextPoint;
					ih--;
				}
			} else {
				if (distance(point, firstPoint) < oneCharWidth
						* overlapCharSpace) {
					prefixPoints.add(point);
					pass = true;
				}

			}
		}

		if (holders.size() > 1) {
			for (int i = 0; i < holders.size(); i++) {
				if (i == holders.size() - 1) {
					// skip
				} else if (i == 0) {
					for (int ip = 0; ip < spaceSize * 2; ip++) {
						if (ip < getHolder(i + 1).getPoints().size()) {
							getHolder(i).appendPoint(
									getHolder(i + 1).getPoints().get(ip));
						}
					}

				} else {
					for (int ip = 0; ip < spaceSize * overlapCharSpace; ip++) {
						if (ip < getHolder(i + 1).getPoints().size()) {
							getHolder(i).appendPoint(
									getHolder(i + 1).getPoints().get(ip));
						}
					}
				}
			}
		}
		return pass;
	}

}
