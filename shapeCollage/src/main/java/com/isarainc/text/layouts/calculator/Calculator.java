package com.isarainc.text.layouts.calculator;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Point;

public abstract class Calculator {
	protected List<CalculationHolder> holders = new LinkedList<CalculationHolder>();

	public void addHolder(CalculationHolder holder) {
		holders.add(holder);
	}

	public CalculationHolder getHolder(int idx) {
		return holders.get(idx);
	}

	public List<CalculationHolder> getHolders() {
		return holders;
	}

	public void setHolders(List<CalculationHolder> holders) {
		this.holders = holders;
	}

	public void incSize() {
		incSize(1.0f);
	}

	public void incSize(float step) {
		if (!holders.isEmpty()) {
			float baseSize = holders.get(0).getTextSize();
			float newSize = holders.get(0).getTextSize() + step;

			for (CalculationHolder holder : holders) {
				holder.setTextSize(newSize + (holder.getTextSize() - baseSize)
						* newSize / baseSize);
			}
		}

	}

	public void decSize() {
		decSize(1.0f);
	}

	public void decSize(float step) {
		if (!holders.isEmpty()) {
			float baseSize = holders.get(0).getTextSize();
			float newSize = holders.get(0).getTextSize() - step;
			for (CalculationHolder holder : holders) {
				holder.setTextSize(newSize + (holder.getTextSize() - baseSize)
						* newSize / baseSize);
			}
		}
	}

	public static float distance(Point p, Point q) {
		float dx = p.x - q.x; // horizontal difference
		float dy = p.y - q.y; // vertical difference
		float dist = (float) Math.sqrt(dx * dx + dy * dy); // distance using
															// Pythagoras
															// theorem
		return dist;
	}
	

	public abstract int sumWidth();
	
	public abstract int getDrawHeight();
		
	public abstract int getBaseline();

	public abstract boolean processPath(List<Point> points);
	
	

}
