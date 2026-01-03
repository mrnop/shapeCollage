package com.isarainc.text.layouts.calculator;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Point;

public class OverlapCalculator extends Calculator {

	public CalculationHolder getHolder(int idx) {
		return holders.get(idx);
	}

	public List<CalculationHolder> getHolders() {
		return holders;
	}

	public int sumWidth() {
		int sum = 0;
		for (int i = 0; i < holders.size(); i++) {
			CalculationHolder holder = holders.get(i);
			if (i % 2 == 0) {
				sum += holder.width() / 2;
			} else {
				sum += holder.width();
			}
		}
		return sum;
	}

	public boolean processPath(List<Point> points) {

		int ih = holders.size() - 1;
		List<Point> suffixPoints = new LinkedList<Point>();
		List<Point> prefixPoints = new LinkedList<Point>();

		Point firstPoint = points.get(points.size() - 1);
		Point nextPoint = firstPoint;

		int offset = 0;
		int oneCharWidth = getHolder(0).getOneCharWidth();

		for (int i = points.size() - 1; i > 0; i--) {
			Point point = points.get(i);
			if (distance(point, firstPoint) < oneCharWidth * 2) {
				suffixPoints.add(point);
				nextPoint = point;
				offset = i;
			} else {
				break;
			}
		}
		firstPoint = nextPoint;

		for (int i = offset; i > 0; i--) {
			// Keep suffix point
			Point point = points.get(i);

			if (ih >= 0) {
				if (ih % 2 == 0) {
					getHolder(ih).addPoint(point);
				}
				if (distance(point, firstPoint) < getHolder(ih).width()
						+ oneCharWidth) {
					// get point for space at end of word
					if (distance(point, firstPoint) < (getHolder(ih).width())) {
						if (ih % 2 == 1) {
							getHolder(ih).addPoint(point);
						}
						nextPoint = point;
					} else {
						// get point for space at begin of word
						if (ih % 2 == 1) {
							getHolder(ih).addPoint(point);
						}
						nextPoint = point;

					}
				} else {
					firstPoint = nextPoint;
					ih--;
				}
			} else {
				if (distance(point, firstPoint) < oneCharWidth * 2) {
					prefixPoints.add(point);
				}

			}
		}

		// Append Overlap space to avoid text trim when save to file
		if (holders.size() > 1) {
			List<Point> curPoints = new LinkedList<Point>();
			List<Point> prevPoints = new LinkedList<Point>();
			for (int i = 0; i < holders.size(); i++) {
				curPoints.clear();
				for (Point p : getHolder(i).getPoints()) {
					curPoints.add(p);
				}
				if (i % 2 == 0) {

				} else {
					if (i == holders.size() - 1) {
						for (Point suf : suffixPoints) {
							getHolder(i).appendPoint(suf);
						}

						for (int ip = 0; ip < suffixPoints.size(); ip++) {
							if (ip < prevPoints.size()) {
								getHolder(i).addPoint(
										prevPoints.get(prevPoints.size() - ip
												- 1));
							}
						}
					} else if (i == 0) {
						for (int ip = 0; ip < suffixPoints.size(); ip++) {
							if (ip < getHolder(i + 1).getPoints().size()) {
								getHolder(i).appendPoint(
										getHolder(i + 1).getPoints().get(ip));
							}
						}
						for (Point pre : prefixPoints) {
							getHolder(i).addPoint(pre);
						}
						prevPoints.addAll(curPoints);

					} else {
						for (int ip = 0; ip < suffixPoints.size() * 2; ip++) {
							if (ip < getHolder(i + 1).getPoints().size()) {
								getHolder(i).appendPoint(
										getHolder(i + 1).getPoints().get(ip));
							}
						}
						for (int ip = 0; ip < suffixPoints.size(); ip++) {
							if (ip < prevPoints.size()) {
								getHolder(i).addPoint(
										prevPoints.get(prevPoints.size() - ip
												- 1));
							}
						}
						prevPoints.addAll(curPoints);
					}
				}
			}
		}
		return true;
	}

	@Override
	public int getDrawHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBaseline() {
		// TODO Auto-generated method stub
		return 0;
	}

}
