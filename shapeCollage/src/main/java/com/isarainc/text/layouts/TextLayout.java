package com.isarainc.text.layouts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Environment;
import android.widget.Toast;

import com.isarainc.fonts.FontHolder;
import com.isarainc.fonts.FontManager;
import com.isarainc.text.PathUtil;
import com.isarainc.text.layouts.calculator.CalculationHolder;
import com.isarainc.text.layouts.calculator.SerialCalculator;
import com.isarainc.text.styles.Simple;
import com.isarainc.text.styles.TextStyle;
import com.isarainc.util.Utils;

public abstract class TextLayout implements Serializable {
	private static final long serialVersionUID = -2562870851552174289L;
	protected static final String TAG = "TextLayout";
	public static final int WIDTH = 720;
	public static final int HEIGHT = 720;

	protected static List<TextLayout> freeObjects = Collections
			.synchronizedList(new LinkedList<TextLayout>());

	private String type;
	private List<TextStyle> styles = new LinkedList<TextStyle>();
	private List<Float> textSizes = new LinkedList<Float>();
	private static Context context;
	protected Paint mTextPaint;
	protected int baseline = 0;

	protected Path simpleDrawPath = new Path();
	//private int width = WIDTH;
	//private int height = HEIGHT;
	//protected Bitmap txtBitmap;
	//protected Canvas canvas;
	private float wordWidth = 0.60f; // word with in percent
	protected int wordHeight=0;
	ExecutorService executorService;
	
	public static void init(Context context) {
		TextLayout.context = context;
		FontManager.getInstance(context);
	}

	protected TextLayout() {
		super();
		executorService = Executors.newFixedThreadPool(5);
		type = this.getClass().getSimpleName();
		mTextPaint = new Paint();
		mTextPaint.setFilterBitmap(true);
		mTextPaint.setDither(true);
		mTextPaint.setAntiAlias(true);
		//txtBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		//canvas = new Canvas(txtBitmap);
	}

	protected TextLayout(Context context) {
		super();
		executorService = Executors.newFixedThreadPool(5);
		TextLayout.context = context;
		mTextPaint = new Paint();

		for (int i = 0; i < styleCount(); i++) {
			styles.add(new Simple(context));
		}
		//txtBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		//canvas = new Canvas(txtBitmap);
		type = this.getClass().getSimpleName();

	}

	public static TextLayout newTextLayout(String type) {
		//if (freeObjects.isEmpty()) {
			try {
				TextLayout tx = (TextLayout) Class.forName(
						TextLayout.class.getPackage().getName() + "." + type)
						.newInstance();
				return tx;
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
//		} else {
//			synchronized (freeObjects) {
//				for (TextLayout tx : freeObjects) {
//					if (tx.getClass().getSimpleName().equals(type)) {
//					   // Log.d(TAG, "freeObjects =" + freeObjects.size());
//						if (freeObjects.remove(tx)) {
//							 Log.d(TAG, "remove =" + freeObjects.size());
//
//							tx.simpleDrawPath.reset();
//							//if (tx.canvas != null)
//							//	tx.canvas.drawColor(Color.TRANSPARENT,
//							//			PorterDuff.Mode.CLEAR);
//							tx.styles.clear();
//							tx.textSizes.clear();
//							Log.d(TAG, "return =" + freeObjects.size());
//							return tx;
//						}
//					}
//				}
//			}
//			try {
//				TextLayout tx = (TextLayout) Class.forName(
//						TextLayout.class.getPackage().getName() + "." + type)
//						.newInstance();
//				return tx;
//			} catch (InstantiationException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			}
//
//		}
		return null;

	}

	public static void freeTextLayout(TextLayout tx) {
		synchronized (freeObjects) {
			freeObjects.add(tx);
		}
	}

	public Context getContext() {
		return context;
	}

	public void recycle() {
		TextLayout.freeTextLayout(this);
	}

	//public void setResolution(int w, int h) {
	//	if (w == width && h == height)
	//		return;
	//	width = w;
	//	height = h;
//		if (txtBitmap != null) {
//			// canvas = null;
//			txtBitmap.recycle();
//			txtBitmap = null;
//			System.gc();
//			Runtime.getRuntime().gc();
//		}
//		txtBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//		canvas = new Canvas(txtBitmap);
		
//	}

	public float getWordWidth() {
		return wordWidth;
	}

	public void setWordWidth(float wordWidth) {
		this.wordWidth = wordWidth;
	}

	public int getWordHeight() {
		return wordHeight;
	}

	public void setWordHeight(int wordHeight) {
		this.wordHeight = wordHeight;
	}

	public String getType() {
		return type;
	}

	public TextStyle getStyles(int idx) {
		if (idx < styles.size())
			return styles.get(idx);
		else
			return null;
	}

	public void replaceStyle(int idx, TextStyle newStyle) {
		try {
			styles.remove(idx);
			textSizes.remove(idx);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		styles.add(idx, newStyle);
		textSizes.add(idx, newStyle.getSize());

	}

	public float getTextSizes(int idx) {
		return textSizes.get(idx);
	}

	protected boolean valid(String text) {
		String[] words = text.split(" ");
		return words.length == styleCount();

	}

	protected void updatePath(List<Point> rawPoints) {
		PathUtil.updatePath(simpleDrawPath, rawPoints);
	}

	protected  void simplePreDraw(Canvas canvas,String text) {
		SerialCalculator calculator = new SerialCalculator();
		TextStyle style = getStyles(0);
		calculator.addHolder(CalculationHolder.newCalculationHolder(
				style.getTypeface(), text, style.getSize()));
		// Log.d(TAG, "simplePreDraw=" + style.getFont());
		// Scale down
		int difx = (int) (calculator.sumWidth() - (canvas.getWidth() * wordWidth));

		if (difx > 0) {
			// Log.d(TAG, "Scale down");
			while (difx > 0) {
				if (difx > 100) {
					calculator.decSize(30);
				} else if (difx > 10) {
					calculator.decSize(13);
				} else {
					calculator.decSize();
				}
				difx = (int) (calculator.sumWidth() - (canvas.getWidth() * wordWidth));
			}
			while (difx < -10) {
				calculator.incSize();
				difx = (int) (calculator.sumWidth() - (canvas.getWidth() * wordWidth));
			}
		} else {
			// scale up
			// Log.d(TAG, "Scale up");
			while (difx < 0) {
				if (difx < -100) {
					calculator.incSize(10);
				} else if (difx < -10) {
					calculator.incSize(1);
				} else {
					calculator.incSize();
				}
				difx = (int) (calculator.sumWidth() - (canvas.getWidth() * wordWidth));
			}
			while (difx > 10) {

				calculator.decSize();
				difx = (int) (calculator.sumWidth() - (canvas.getWidth() * wordWidth));
			}

		}
		// Log.d(TAG, "draw height " +calculator.getDrawHeight() +
		// ":"+canvas.getHeight());
		int difY = (int) Math
				.ceil(calculator.getDrawHeight() - (canvas.getHeight() * 0.70));
		int prevDifY = Integer.MAX_VALUE;
		while (difY > 0 && prevDifY != difY) {
			// Log.d(TAG, "Scale Height");
			prevDifY = difY;
			calculator.decSize();
			difY = (int) Math
					.ceil(calculator.getDrawHeight() - (canvas.getHeight() * 0.70));
		}
		// Log.d(TAG, "simplePreDraw textSize="
		// + calculator.getHolder(0).getTextSize());
		style.setSize(calculator.getHolder(0).getTextSize());
		wordHeight=calculator.getHolder(0).height();
		baseline = calculator.getBaseline();
	
		CalculationHolder.freeCalculationHolder(calculator.getHolders());
	}

	protected void simpleDraw(Canvas canvas,String text, List<Point> points) {
		// Log.d(TAG, "simpleDraw [" + text + "]");
		TextStyle style = getStyles(0);
		if (style == null) {
			style = TextStyle.random();
		}
		//Log.d(TAG, "simpleDraw " +text + ",wordHeight [" + wordHeight +"]");
		// Log.d(TAG, "simpleDraw textSize=" + style.getSize());

		updatePath(points);
		style.draw(canvas, simpleDrawPath, text);

	}
	
	public abstract int styleCount();

	public JSONObject toJsonObject() {

		JSONObject object = new JSONObject();
		try {
			object.put("type", type);
			for (int i = 0; i < this.styleCount(); i++) {
				object.put("style" + (i + 1), styles.get(i).toJson());
			}
		} catch (JSONException e) {
			// e.printStackTrace();
		}
		return object;
	
	}
	public String toJson() {
		return toJsonObject().toString();
	}

	public static TextLayout fromJson(String json) {
		TextLayout textLayout = null;
		// Log.d("fromJson", json);
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(json);
			String type = jsonObj.getString("type");
			
			textLayout = TextLayout.newTextLayout(type);
			TextStyle first = null;
			for (int i = 0; i < textLayout.styleCount(); i++) {
				try {
					
					TextStyle style = TextStyle.fromJson(jsonObj
							.getString("style" + (i + 1)));
					// Log.d(TAG, jsonObj.toString());
					if (first == null) {
						first = style;
						if (first.getFont() == null) {
							FontHolder holder = FontManager.getInstance(null)
									.loadRandom();
							first.setFont(holder.getFont().getName());
						}
						if (first.getSize() == 0) {
							first.setSize(100);
						}
					} else {
						if (style.getFont() == null) {
							style.setFont(first.getFont());
						}
						if (style.getSize() == 0) {
							style.setSize(first.getSize());
						}
					}

					style.setContext(TextLayout.context);
					textLayout.styles.add(style);
					textLayout.textSizes.add(style.getSize());
				} catch (Exception e) {
					e.printStackTrace();
					textLayout.styles.add(TextStyle.random());
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return textLayout;
	}

	/**
	 * Method to calculate and prepare data
	 * 
	 * @param canvas
	 * @param text
	 */
	protected abstract void onPreDraw(Canvas canvas,String text);

	protected abstract void onDraw(Canvas canvas,String text, List<Point> points);

	public final void preDraw(Canvas canvas,String text) {
		//Log.d(TAG, "preDraw=" +text);
		onPreDraw(canvas,text);
	}

	public final void draw(Canvas canvas,String text, List<Point> points) {
		//Log.d(TAG, "draw " +text + ",wordHeight [" + wordHeight +"]");
		draw(canvas,text, points, false);
		
	}

	public final void draw(Canvas canvas,String text, List<Point> points,
			boolean refresh) {
		//Log.d(TAG, "draw " +text + ",wordHeight [" + wordHeight +"]");
		// Log.d(TAG, "draw [" + text +"]" + lastText + ":" );
		try {
			if (text == null || "".equals(text.trim())) {
				return;
			}
			text = text.trim();
			//canvas.save();
			if (valid(text)) {
				onDraw(canvas,text, points);
			} else {
				simpleDraw(canvas,text, points);
			}
			//canvas.restore();
		} catch (Exception e) {
			// ErrorReportManager.report(context, e);
			e.printStackTrace();
		}
	}

//	public Bitmap createBitmap(String text, List<Point> points) {
//		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//		// Log.d(TAG, "createBitmap points=" +points);
//		if (points.size() > 3) {
//			draw(text, points, true);
//		}
//		return txtBitmap;
//	}

	public void random() {
		styles.clear();
		textSizes.clear();
		TextStyle first = null;
		for (int i = 0; i < styleCount(); i++) {
			TextStyle ts = TextStyle.random();
			if (first == null) {
				first = ts;
			} else {
				if (ts.getFont() == null) {
					ts.setFont(first.getFont());
				}
				if (ts.getSize() == 0) {
					ts.setSize(first.getSize());
				}
			}

			styles.add(ts);
			textSizes.add(ts.getSize());

		}
	}

	//
	// public synchronized void randomx(boolean createNew) {
	// styles.clear();
	// textSizes.clear();
	// TextStyle first = null;
	// for (int i = 0; i < styleCount(); i++) {
	// try {
	// TextStyle ts = (TextStyle) TextStyle.random().clone();
	// if (first == null) {
	// first = ts;
	// } else {
	// if (ts.getFont() == null) {
	// ts.setFont(first.getFont());
	// }
	// if (ts.getSize() == 0) {
	// ts.setSize(first.getSize());
	// }
	// }
	// styles.add(ts);
	// textSizes.add(ts.getSize());
	// } catch (CloneNotSupportedException e1) {
	// e1.printStackTrace();
	// }
	// }
	// }

	public static TextLayout simpleLayout() {
		TextLayout textLayout = TextLayout.newTextLayout("SimpleTextLayout");
		return textLayout;
	}

	public static TextLayout randomLayout() {
		int rndId = (int) (System.currentTimeMillis() % 3);
		TextLayout textLayout = null;
		switch (rndId) {
		case 0:
			textLayout = TextLayout.newTextLayout("SimpleTextLayout");
			break;
		case 1:
			textLayout = TextLayout.newTextLayout("ThreeTextLayout");
			break;
		case 2:
			textLayout = TextLayout.newTextLayout("FiveTextLayout");
			break;
		}

		textLayout.random();
		return textLayout;
	}

	public String getThumbFilename(String targetDir, String fileName) {
		if (Utils.isSDCARDMounted()) {
			File dir = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "Android" + File.separator + "data"
					+ File.separator + "com.isarainc.text"
					+ File.separator + "themes");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(dir, fileName);
			// Log.d("getThumbFilename", "file " + file.getAbsolutePath());
			return file.getAbsolutePath();
		}
		return null;
	}

	/**
	 * Save image specific size
	 * 
	 * @param text
	 * @param fileName
	 * @param width
	 * @param height
	 * @return
	 */
	public String save(String text, String fileName, int width, int height) {
		return this.save(text, fileName, width, height,
				Bitmap.CompressFormat.JPEG);
	}

	/**
	 * 
	 * @param text
	 * @param fileName
	 * @param width
	 * @param height
	 * @param format
	 * @return
	 */
	public String save(String text, String fileName, int width, int height,
			Bitmap.CompressFormat format) {
		List<Point> points = new ArrayList<Point>();

		int yPos = (int) ((width / 2) - ((mTextPaint.descent() + mTextPaint
				.ascent()) / 2));
		for (int i = 10; i < width - 10; i += 10) {
			points.add(new Point(i, yPos));
		}
		return this.save(text, fileName, width, height, points,
				Bitmap.CompressFormat.JPEG);
	}

	/*public Bitmap createBitmap(String text, int width, int height,
			List<Point> points) {
		this.setResolution(width, height);
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		draw(text, points, true);
		return txtBitmap;
	}*/

	/**
	 * 
	 * @param text
	 * @param fileName
	 * @param width
	 * @param height
	 * @param points
	 * @param format
	 * @return
	 */
	public String save(String text, String fileName, int width, int height,
			List<Point> points, Bitmap.CompressFormat format) {
		Bitmap txtBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(txtBitmap);
		String saveFile = null;
		if (Utils.isSDCARDMounted()) {
			File dir = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "Android" + File.separator + "data"
					+ File.separator + "com.isarainc.text"
					+ File.separator + "saved");

			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(dir, fileName);
			draw(canvas,text, points, true);

			// Log.d(TAG, "save new file " + file.getAbsolutePath());
			OutputStream fOut = null;

			try {
				fOut = new FileOutputStream(file);
				txtBitmap.compress(format, 100, fOut);
				fOut.flush();
				fOut.close();

			} catch (FileNotFoundException e) {
				Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG)
						.show();
				e.printStackTrace();

			} catch (IOException e) {
				Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG)
						.show();
			}
			recycle();

			saveFile = file.getAbsolutePath();
		}

		return saveFile;
	}

	public String genTheme(String text, String fileName) {
		return this.genThumb(text, "themes", fileName,
				System.currentTimeMillis());
	}

	public String genThumb(String text, String targetDir, String fileName) {
		return this.genThumb(text, targetDir, fileName, 600, 150,
				Bitmap.CompressFormat.PNG, System.currentTimeMillis());
	}

	public String genThumb(String text, String targetDir, String fileName,
			int width, int height) {
		return this.genThumb(text, targetDir, fileName, width, height,
				Bitmap.CompressFormat.PNG, System.currentTimeMillis());
	}

	public String genThumb(String text, String targetDir, String fileName,
			long lastModified) {
		return this.genThumb(text, targetDir, fileName, 600, 150,
				Bitmap.CompressFormat.PNG, lastModified);
	}

	public String genThumb(String text, String targetDir, String fileName,
			int width, int height, Bitmap.CompressFormat format,
			long lastModified) {
		//executorService.submit(task);
		//setResolution(width,height);
		Bitmap txtBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(txtBitmap);
		String saveFile = null;
		if (Utils.isSDCARDMounted()) {
			File dir = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "Android" + File.separator + "data"
					+ File.separator + "com.isarainc.text"
					+ File.separator + "themes");
			
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(dir, fileName);
			// Log.d("genThumb", "file " + file.getAbsolutePath());
			saveFile = file.getAbsolutePath();
			if (!file.exists() || file.lastModified() < lastModified) {
				List<Point> points = new ArrayList<Point>();
				for (int i = (int) (width * 0.10); i < width * 0.90; i += 10) {
					points.add(new Point(i, height * 3 / 4));
				}
				// Log.d("genThumb",
				// "re generate new file " + file.getAbsolutePath());
				preDraw(canvas,text);
				draw(canvas,text, points, true);
				OutputStream fOut = null;

				try {
					fOut = new FileOutputStream(file);
					txtBitmap.compress(format, 100, fOut);
					fOut.flush();
					fOut.close();

				} catch (FileNotFoundException e) {
					e.printStackTrace();

				} catch (IOException e) {
					e.printStackTrace();
				}
				recycle();

			}
			saveFile = file.getAbsolutePath();
		}

		return saveFile;
	}
	class ThumbGenerator implements Runnable {
		
		@Override
		public void run() {
			
			
		}
		
	}

	@Override
	public String toString() {
		return "TextLayout [type=" + type + ", styles=" + styles
				+ ", textSizes=" + textSizes + "]";
	}

}
