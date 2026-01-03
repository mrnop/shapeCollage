package com.isarainc.text.layouts;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;

import com.isarainc.text.layouts.calculator.CalculationHolder;
import com.isarainc.text.layouts.calculator.Calculator;
import com.isarainc.text.layouts.calculator.OverlapCalculator;
import com.isarainc.text.layouts.calculator.SerialCalculator;
import com.isarainc.text.styles.TextStyle;

public class TwoLineTextLayout extends TextLayout {

	private static final long serialVersionUID = -6979299139770442310L;

	protected TwoLineTextLayout() {
		super();
	}

	protected TwoLineTextLayout(Context context) {
		super(context);
	}

	@Override
	public int styleCount() {
		return 2;
	}

	@Override
	public void onPreDraw(Canvas canvas,String text) {
		Calculator calculator = new OverlapCalculator();

		String[] words = text.split(" ");

		// stylecount == n
		for (int i = 0; i < words.length; i++) {
			TextStyle style = getStyles(i);
			calculator.addHolder(CalculationHolder.newCalculationHolder(
					style.getTypeface(), words[i], style.getSize()));
		}
		// Cal size
		// Scale down
		int dif = (int) (calculator.sumWidth() - (canvas.getWidth() * 0.70));
		if (dif > 0) {
			while (dif > 0) {
				if (dif > 100) {
					calculator.decSize(100 / words.length);
				} else if (dif > 10) {
					calculator.decSize(10 / words.length);
				} else {
					calculator.decSize();
				}
				dif = (int) (calculator.sumWidth() - (canvas.getWidth() * 0.70));
			}
			while (dif < -10) {
				calculator.incSize();
				dif = (int) (calculator.sumWidth() - (canvas.getWidth() * 0.70));
			}
		} else {
			// scale up
			while (dif < 0) {
				if (dif < -100) {
					calculator.incSize(100 / words.length);
				} else if (dif < -10) {
					calculator.incSize(10 / words.length);
				} else {
					calculator.incSize();
				}
				dif = (int) (calculator.sumWidth() - (canvas.getWidth() * 0.70));
			}
			while (dif > 10) {
				calculator.decSize();
				dif = (int) (calculator.sumWidth() - (canvas.getWidth() * 0.70));
			}

		}
		// Set properly text size to style
		for (int i = 0; i < words.length; i++) {
			TextStyle style = getStyles(i);
			style.setSize(calculator.getHolder(i).getTextSize());
		}
		wordHeight=calculator.getHolder(0).height();
		baseline = calculator.getBaseline();
		CalculationHolder.freeCalculationHolder(calculator.getHolders());
	}

	@Override
	public void onDraw(Canvas canvas,String text, List<Point> mpoints) {
		String[] word = text.split(" ");

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
			points.add(new Point((int) aCoordinates[0], (int) aCoordinates[1]));
			counter++;
			distance = distance + speed;
		}

		Calculator calculator = new SerialCalculator();
		for (int i = 0; i < word.length; i++) {

			TextStyle style = getStyles(i);
			calculator.addHolder(CalculationHolder.newCalculationHolder(
					style.getTypeface(), word[i], style.getSize()));
		}

		calculator.processPath(points);
		for (int i = 0; i < word.length; i++) {
			TextStyle style = getStyles(i);
			if (i % 2 == 0) {
				Path p = calculator.getHolder(i).getPath();
				Matrix translateMatrix = new Matrix();
				translateMatrix.setTranslate(
						calculator.getHolder(i).width() / 2, -100);
				p.transform(translateMatrix);
				style.draw(canvas, p, word[i]);
			} else {
				style.draw(canvas, calculator.getHolder(i).getPath(), word[i]);
			}
		}
		CalculationHolder.freeCalculationHolder(calculator.getHolders());
	}

}
