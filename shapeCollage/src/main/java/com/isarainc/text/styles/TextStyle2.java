package com.isarainc.text.styles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import com.isarainc.fonts.FontHolder;
import com.isarainc.fonts.FontManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public abstract class TextStyle2 implements Serializable, Cloneable {
	private static final long serialVersionUID = -4561912706699377502L;

	private static final String TAG = "TextStyle2";
	private String type;

	//Font
	private String font;
	private Typeface typeface = null;
	private float size = 100;

	//Gradient
	private boolean useGradient;
	//When use gradient (more than 2 color) This case Rainbow
	private int[] getGradientColors() {
		return new int[] {Color.RED,
				Color.YELLOW,
				Color.GREEN,
				Color.BLUE,
				Color.MAGENTA
		};
	}
	//When not use gradient
	private int gradientStartColor = Color.YELLOW;
	private int gradientEndColor = Color.RED;

	private boolean gradientHorizontal = false;

	//Outline
	private int outlineColor = Color.BLACK;
	private int outlineWidth = 3;

	//Shadow
	private int shadowType; // No Shadow , Sharp ,Light Blur , Mid Blur , Hard Blur
	private int shadowOffsetX = 8;
	private int shadowOffsetY = 8;
	private int shadowColor = Color.BLACK;
	private int shadowOpacity = 50; // % opacity



	protected List<Integer> colors = new LinkedList<Integer>();
	protected List<Integer> strokes = new LinkedList<Integer>();

	private static Context context;
	private FontManager fontManager;
	private Paint paint = new Paint();
	private Rect bound = new Rect();




	public TextStyle2() {
		super();
		type = this.getClass().getSimpleName();
		fontManager = FontManager.getInstance(context);
		loadDefaultColor();
		loadDefaultStroke();
	}

	public TextStyle2(Context context) {
		super();
		type = this.getClass().getSimpleName();
		TextStyle2.context = context;
		fontManager = FontManager.getInstance(context);
		loadDefaultColor();
		loadDefaultStroke();
	}
	public static void init(Context ctx){
		context = ctx;
		FontManager.getInstance(context);

	}

	protected void loadDefaultStroke() {
		strokes.add(4);
		strokes.add(15);
	}

	protected void loadDefaultColor() {
		colors.add(Color.MAGENTA);
		colors.add(Color.YELLOW);
		colors.add(Color.BLUE);
		colors.add(Color.GREEN);
		colors.add(Color.RED);
		colors.add(Color.WHITE);
		colors.add(Color.CYAN);
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		TextStyle2.context = context;
	}

	public Rect getBound() {
		return bound;
	}

	public void setBound(Rect bound) {
		this.bound = bound;
	}

	public String getType() {
		return type;
	}

	public Typeface getTypeface() {
		if (typeface == null) {

		}
		return typeface;
	}

	public void setTypeface(Typeface typeface) {
		this.typeface = typeface;
		paint.setTypeface(typeface);
		paint.setTextSize(size);
		paint.getTextBounds("A", 0, 1, bound);
	}

	public String getFont() {
		return font;
	}

	

	public void setFont(String font) {
		this.font = font;
		try {
			Log.d(TAG, "font=" + font);
			typeface = fontManager.loadFont(font).getTypeface();
		} catch (Exception e) {
			e.printStackTrace();
			// typeface = fontManager.loadRandom().getTypeface();
		}

	}

	public void removeFont() {
		font = null;
		typeface = null;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
		paint.setTypeface(typeface);
		paint.setTextSize(size);
		paint.getTextBounds("A", 0, 1, bound);
	}

	public List<Integer> getColors() {
		return colors;
	}

	public void setColors(List<Integer> colors) {
		this.colors = colors;
	}

	public int getColor(int idx) {
		return colors.get(idx);

	}

	public void setColor(int idx, Integer color) {
		colors.remove(idx);
		colors.add(idx, color);

	}

	public List<Integer> getStrokes() {
		return strokes;
	}

	public void setStrokes(List<Integer> strokes) {
		this.strokes = strokes;
	}

	public int getStroke(int idx) {
		if (idx < strokes.size()) {
			return strokes.get(idx);
		} else {
			return 4;
		}
	}

	public void randomColor() {
		Random rnd = new Random();
		try {
			colors.clear();
		} catch (UnsupportedOperationException uoe) {
			colors = new LinkedList<Integer>();
		}
		colors.add(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
				rnd.nextInt(256)));

		colors.add(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
				rnd.nextInt(256)));

		colors.add(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
				rnd.nextInt(256)));

		colors.add(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
				rnd.nextInt(256)));

		colors.add(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
				rnd.nextInt(256)));

		colors.add(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256),
				rnd.nextInt(256)));
	}

	public abstract int colorCount();

	protected abstract void prepare(Canvas canvas, String text);

	public final void draw(Canvas canvas, Path path, String text) {
		FontHolder holder = fontManager.loadFont(font);
		if (holder != null) {
			text = holder.getFont().patch(text);
		}
		prepare(canvas, text);
		drawText(canvas, path, text);
	}

	protected abstract void drawText(Canvas canvas, Path path, String text);

	public final void draw(Canvas canvas, int x, int y, String text) {
		FontHolder holder = fontManager.loadFont(font);
		if (holder != null) {
			text = holder.getFont().patch(text);
		}
		prepare(canvas, text);
		drawText(canvas, x, y, text);
	}

	protected abstract void drawText(Canvas canvas, int x, int y, String text);

	@Override
	public String toString() {
		return "TextStyle " + this.hashCode() + ":[type=" + type + ", font="
				+ font + ", size=" + size + ", colors=" + colors + "]";
	}

	public JSONObject toJsonObject() {

		JSONObject object = new JSONObject();
		try {
			object.put("type", type);
			if (font != null) {
				object.put("font", font);
			}
			object.put("size", size);
			JSONArray jsonColor = new JSONArray();

			for (Integer color : colors) {
				jsonColor.put(color);
			}
			object.put("colors", jsonColor);
			JSONArray jsonStroke = new JSONArray();

			for (Integer stroke : strokes) {
				jsonStroke.put(stroke);
			}
			object.put("strokes", jsonStroke);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	
	}
	
	public String toJson() {
		return toJsonObject().toString();
	}

	public static TextStyle2 fromJson(String json) {

		TextStyle2 textStyle = null;
		try {
			// Log.d("fromJson", json);
			JSONObject jsonObj = new JSONObject(json);
			String type;
			type = jsonObj.getString("type");
			if (type.startsWith("TextStyle")) {
				type = type.substring("TextStyle".length());
			}
			Log.d(TAG, "type=" + type);
			textStyle = (TextStyle2) Class.forName(
					TextStyle2.class.getPackage().getName() + "." + type)
					.newInstance();

			try {
				String font = jsonObj.getString("font");

				if ("BLK-BangLi-Ko-Sa-Na.ttf".equals(font)) {
					font = "Bangli Ko Sa Na.ttf";
				}
				textStyle.setFont(font);
			} catch (Exception e) {
				// Let's set font by layout
				FontHolder holder = FontManager.getInstance(null).loadFont(
						"SmarnSpecial.ttf");
				if (holder.getFont() != null) {
					textStyle.setFont(holder.getFont().getName());
				} else {
					textStyle.setFont("default");
				}
			}

			try {
				textStyle.setSize((float) jsonObj.getDouble("size"));
			} catch (JSONException e) {

				// e.printStackTrace();
			}
			JSONArray colorArr;
			try {
				colorArr = jsonObj.getJSONArray("colors");
				List<Integer> cs = new LinkedList<Integer>();
				for (int i = 0; i < colorArr.length(); i++) {
					cs.add(colorArr.getInt(i));
				}
				textStyle.setColors(cs);
			} catch (JSONException e) {
				// e.printStackTrace();
			}

			JSONArray strokeArr;
			try {
				strokeArr = jsonObj.getJSONArray("strokes");
				List<Integer> ss = new LinkedList<Integer>();
				for (int i = 0; i < strokeArr.length(); i++) {
					ss.add(strokeArr.getInt(i));
				}
				textStyle.setStrokes(ss);
			} catch (JSONException e) {

				// e.printStackTrace();
			}

		} catch (JSONException e) {

			// e.printStackTrace();
		} catch (InstantiationException e) {
			// e.printStackTrace();
		} catch (IllegalAccessException e) {
			// e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
		}

		return textStyle;
	}




	@Override
	public Object clone() throws CloneNotSupportedException {
		TextStyle2 ts = (TextStyle2) super.clone();
		ts.setColors(new LinkedList<Integer>());
		ts.loadDefaultColor();
		for (int i = 0; i < colors.size(); i++) {
			ts.setColor(i, colors.get(i).intValue());
		}
		if (font != null)
			ts.setFont(font);
		return ts;
	}

}
