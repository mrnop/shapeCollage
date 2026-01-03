package com.isarainc.text.layouts;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.util.Log;

import com.isarainc.text.layouts.calculator.CalculationHolder;
import com.isarainc.text.layouts.calculator.Calculator;
import com.isarainc.text.layouts.calculator.SerialCalculator;
import com.isarainc.text.styles.TextStyle;

public abstract class SerialTextLayout extends TextLayout {
	private static final long serialVersionUID = 3278239446102434860L;
	private SharedPreferences sharePrefs;
	int overlapCharSpace=1;

	
	protected SerialTextLayout() {
		super();
	}

	protected SerialTextLayout(Context context) {
		super(context);

	}

	@Override
	protected boolean valid(String text) {
		String[] words = text.split(" ");
		return words.length <= styleCount();
	}

	/**
	 * Predraw to calculate properly text size;
	 * 
	 * @param canvas
	 * @param text
	 */
	public void onPreDraw(Canvas canvas,String text) {
		Log.d(TAG, "onPreDraw [" + text +"]");
		SerialCalculator calculator = new SerialCalculator();

		String[] words = text.split(" ");
		if(words.length==0) return;
		// stylecount == n
		for (int i = 0; i < words.length && i<styleCount() ; i++) {
			TextStyle style = getStyles(i);
			calculator.addHolder(CalculationHolder.newCalculationHolder(style.getTypeface(),
					words[i], style.getSize()));
		}
		for (int i=styleCount(); i < words.length; i++) {
			TextStyle style=TextStyle.random();
			calculator.addHolder(CalculationHolder.newCalculationHolder(style.getTypeface(),
					words[i], style.getSize()));
		}
		// Cal size
		// Scale down
		int dif = (int) (calculator.sumWidth() - (canvas.getWidth() * getWordWidth()));
		if (dif > 0) {
			//Log.d(TAG, "Scale down");
			while (dif > 0) {
				if (dif > 100) {
					calculator.decSize(100 / words.length);
				} else if (dif > 10) {
					calculator.decSize(10 / words.length);
				} else {
					calculator.decSize();
				}
				dif = (int) (calculator.sumWidth() - (canvas.getWidth() * getWordWidth()));
			}
			while (dif < -10) {
				calculator.incSize();
				dif = (int) (calculator.sumWidth() - (canvas.getWidth() * getWordWidth()));
			}
		} else {
			// scale up
			//Log.d(TAG, "Scale up");
			while (dif < 0) {
				if (dif < -100) {
					calculator.incSize(100 / words.length);
				} else if (dif < -10) {
					calculator.incSize(10 / words.length);
				} else {
					calculator.incSize();
				}
				dif = (int)(calculator.sumWidth() - (canvas.getWidth() * getWordWidth()));
			}
			while (dif > 10) {
				calculator.decSize();
				dif = (int) (calculator.sumWidth() - (canvas.getWidth() * getWordWidth()));
			}

		}
		// Set properly text size to style
		for (int i = 0; i < words.length && i<styleCount(); i++) {
			TextStyle style = getStyles(i);
			style.setSize(calculator.getHolder(i).getTextSize());
		}
		wordHeight=calculator.getHolder(0).height();
		baseline = calculator.getBaseline();
		Log.d(TAG, "wordHeight [" + wordHeight +"]");
		if (getContext() != null) {
			sharePrefs = PreferenceManager
					.getDefaultSharedPreferences(getContext());
			Editor edit = sharePrefs.edit();
			edit.putInt("text_max_height", calculator.getDrawHeight());
			edit.commit();
		}
		CalculationHolder.freeCalculationHolder(calculator.getHolders());
	}
	@Override
	public void onDraw(Canvas canvas,String text, List<Point> mpoints) {
		String[] word = text.split(" ");
		//Log.d(TAG, "onDraw [" + text +"]");
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
	
			points.add(new Point((int)aCoordinates[0], (int)aCoordinates[1]));
			counter++;
			distance = distance + speed;
		}

		Calculator calculator = new SerialCalculator();
		for (int i = 0; i < word.length; i++) {
			TextStyle style = getStyles(i);
			calculator.addHolder(CalculationHolder.newCalculationHolder(style.getTypeface(),
					word[i], style.getSize()));
		}

		calculator.processPath(points);
		for (int i = 0; i < word.length; i++) {
			TextStyle style = getStyles(i);
			if(calculator.getHolder(i).getPoints().size()>3){
				style.draw(canvas, calculator.getHolder(i).getPath(), word[i]);
			}
		}
		CalculationHolder.freeCalculationHolder(calculator.getHolders());
	}

}
