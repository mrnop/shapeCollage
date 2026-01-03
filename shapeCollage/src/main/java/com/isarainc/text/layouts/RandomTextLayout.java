package com.isarainc.text.layouts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;

import com.isarainc.text.styles.TextStyle;

public class RandomTextLayout extends TextLayout {
	private static final long serialVersionUID = 3278239446102434860L;
	private static Map<String, TextLayout> layoutMap = new HashMap<String, TextLayout>();

	private TextLayout textLayout;
	static {
		layoutMap.put("SimpleTextLayout", new SimpleTextLayout());
		// layoutMap.put("ThreeTextLayout", new ThreeTextLayout());
		// layoutMap.put("FunTextLayout", new FunTextLayout());

	}

	protected RandomTextLayout() {
		super();
		Random rnd = new Random();
		Object[] ts = layoutMap.values().toArray();
		textLayout = (TextLayout) ts[rnd.nextInt(ts.length)];
		textLayout.random();
	}

	protected RandomTextLayout(Context context) {
		super(context);
		Random rnd = new Random();
		Object[] ts = layoutMap.values().toArray();
		textLayout = (TextLayout) ts[rnd.nextInt(ts.length)];
		textLayout.random();
	}

	@Override
	public int styleCount() {
		return 1;
	}

	@Override
	public void onDraw(Canvas canvas,String text, List<Point> points) {
		textLayout.onDraw(canvas,text, points);
	}

	@Override
	protected void onPreDraw(Canvas canvas,String text) {
		textLayout.onPreDraw(canvas,text);
	}

	@Override
	public void recycle() {
		super.recycle();
		textLayout.recycle();
	}
//
//	@Override
//	public void setResolution(int w, int h) {
//		super.setResolution(w, h);
//		textLayout.setResolution(w, h);
//	}

	@Override
	public float getWordWidth() {
		return textLayout.getWordWidth();
	}

	@Override
	public void setWordWidth(float wordWidth) {

		textLayout.setWordWidth(wordWidth);
	}

	public String getWorkingType() {
		return textLayout.getType();
	}

	@Override
	public TextStyle getStyles(int idx) {
		return textLayout.getStyles(idx);
	}

	@Override
	public synchronized void replaceStyle(int idx, TextStyle newStyle) {
		textLayout.replaceStyle(idx, newStyle);
	}

	@Override
	public float getTextSizes(int idx) {
		return textLayout.getTextSizes(idx);
	}

	@Override
	protected boolean valid(String text) {
		return textLayout.valid(text);
	}

	@Override
	protected void updatePath(List<Point> rawPoints) {
		textLayout.updatePath(rawPoints);
	}

	@Override
	protected synchronized void simplePreDraw(Canvas canvas,String text) {
		textLayout.simplePreDraw(canvas,text);
	}

	@Override
	protected void simpleDraw(Canvas canvas,String text, List<Point> points) {
		textLayout.simpleDraw(canvas,text, points);
	}

	@Override
	public String toJson() {
		return textLayout.toJson();
	}

//	@Override
//	public Bitmap createBitmap(String text, List<Point> points) {
//		return textLayout.createBitmap(text, points);
//	}

	@Override
	public synchronized void random() {
		textLayout.random();
	}
//
//	@Override
//	public synchronized void random(boolean createNew) {
//		textLayout.random(createNew);
//	}

	@Override
	public String getThumbFilename(String targetDir,
			String fileName) {
		return textLayout.getThumbFilename(targetDir, fileName);
	}

	@Override
	public String save(String text, String fileName, int width, int height) {
		return textLayout.save(text, fileName, width, height);
	}

	@Override
	public String save(String text, String fileName, int width, int height,
			CompressFormat format) {
		return textLayout.save(text, fileName, width, height, format);
	}
//
//	@Override
//	public Bitmap createBitmap(String text, int width, int height,
//			List<Point> points) {
//		return textLayout.createBitmap(text, width, height, points);
//	}

	@Override
	public String save(String text, String fileName, int width, int height,
			List<Point> points, CompressFormat format) {
		return textLayout.save(text, fileName, width, height, points, format);
	}

	@Override
	public String genTheme(String text, String fileName) {
		return textLayout.genTheme(text, fileName);
	}

	@Override
	public String genThumb(String text, String targetDir, String fileName) {
		return textLayout.genThumb(text, targetDir, fileName);
	}

	@Override
	public String genThumb(String text, String targetDir, String fileName,
			int width, int height) {
		return textLayout.genThumb(text, targetDir, fileName, width, height);
	}

	@Override
	public String genThumb(String text, String targetDir, String fileName,
			long lastModified) {
		return textLayout.genThumb(text, targetDir, fileName, lastModified);
	}

	@Override
	public String genThumb(String text, String targetDir, String fileName,
			int width, int height, CompressFormat format, long lastModified) {
		return textLayout.genThumb(text, targetDir, fileName, width, height, format,
				lastModified);
	}


}
