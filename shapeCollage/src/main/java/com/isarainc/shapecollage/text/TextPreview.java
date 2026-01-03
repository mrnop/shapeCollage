package com.isarainc.shapecollage.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Environment;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.isarainc.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class TextPreview extends View {

	private static final String TAG = "TextPreview";
	private Typeface typeface = null;
	private String text = "";
	Rect bound = new Rect();
	private TextPaint mTextPaint;
	private Bitmap bgBitmap;
	private Canvas bgCanvas;
	private int imageWidth = 200;
	private int imageHeight = 200;
	private int textSize = 100;
	private Rect rectImage;
	private Rect rectCanvas;
	private Paint bgPaint;
	private static int CANVAS_WIDTH;
	private static int CANVAS_HEIGHT;

	public TextPreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mTextPaint = new TextPaint();
		mTextPaint.setFilterBitmap(false);
		mTextPaint.setDither(true);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(textSize);
		mTextPaint.setTextAlign(Align.CENTER);
		bgBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
				Bitmap.Config.ARGB_8888);
		bgCanvas = new Canvas(bgBitmap);
		bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		rectCanvas = new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
		rectImage = new Rect(0, 0, imageWidth, imageHeight);
	}

	public TextPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTextPaint = new TextPaint();
		mTextPaint.setFilterBitmap(false);
		mTextPaint.setDither(true);
		mTextPaint.setAntiAlias(true);

		bgBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
				Bitmap.Config.ARGB_8888);
		bgCanvas = new Canvas(bgBitmap);
		bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		rectCanvas = new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
		rectImage = new Rect(0, 0, imageWidth, imageHeight);
	}

	public TextPreview(Context context) {
		super(context);
		mTextPaint = new TextPaint();
		mTextPaint.setFilterBitmap(false);
		mTextPaint.setDither(true);
		mTextPaint.setAntiAlias(true);

		bgBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
				Bitmap.Config.ARGB_8888);
		bgCanvas = new Canvas(bgBitmap);
		bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		rectCanvas = new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
		rectImage = new Rect(0, 0, imageWidth, imageHeight);
	}

	private File getSaveFile() {
		String filename = UUID.randomUUID().toString();
		if (Utils.isSDCARDMounted()) {
			File dir = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "Android" + File.separator + "data"
					+ File.separator + getContext().getPackageName()
					+ File.separator + "shapes"+ File.separator + "custom");

			if (!dir.exists()) {
				dir.mkdirs();
			}
			File f = new File(dir, filename + ".png");
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG)
						.show();
			}
			return f;
		} else {
			File dir = new File(Environment.getDataDirectory() + File.separator
					+ "Android" + File.separator + "data" + File.separator
					+getContext().getPackageName() +File.separator + "shapes"+ File.separator + "custom");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File f = new File(dir, filename + ".png");
			try {
				f.createNewFile();
			} catch (IOException e) {

				e.printStackTrace();
				Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG)
						.show();
			}
			return f;
		}
	}

	public String saveToFile() {
		int width = bgBitmap.getWidth();
		int height = bgBitmap.getHeight();

		int top = Integer.MAX_VALUE;
		int left = Integer.MAX_VALUE;
		int right = Integer.MIN_VALUE;
		int bottom = Integer.MIN_VALUE;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// a[i] = Color.alpha(bitmap.getPixel(i, j));
				// Log.d(TAG, "alpha= " +Color.alpha(bitmap.getPixel(i, j)));
				if (Color.alpha(bgBitmap.getPixel(i, j)) > 0) {
					if (j < top) {
						top = j;
					}
					if (j > bottom) {
						bottom = j;
					}
					if (i < left) {
						left = i;
					}
					if (i > right) {
						right = i;
					}
				}

			}

		}

		Bitmap cropBitmap = Bitmap.createBitmap((right - left),
				(bottom - top), Bitmap.Config.ARGB_8888);
		Canvas cropCanvas = new Canvas(cropBitmap);
		cropCanvas.drawBitmap(bgBitmap, ((-1) * left),
				((-1) * top), bgPaint);
		
		Bitmap saveBitmap = null;
		try {
			saveBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
					Bitmap.Config.ARGB_8888);
		} catch (OutOfMemoryError oome) {
			System.gc();
			saveBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
					Bitmap.Config.RGB_565);
		}
		Canvas saveCanvas = new Canvas(saveBitmap);
	


		saveCanvas.drawBitmap(cropBitmap, (imageWidth-cropBitmap.getWidth())/2, (imageHeight-cropBitmap.getHeight())/2, bgPaint);
		OutputStream fOut = null;
		File file = getSaveFile();
		try {
			fOut = new FileOutputStream(file);
			saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG)
					.show();
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG)
					.show();
			return null;
		}
		cropBitmap.recycle();
		bgBitmap.recycle();
		cropBitmap.recycle();
		return file.getAbsolutePath();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = getMeasuredWidth();
		int height = width * imageHeight / imageWidth;

		setMeasuredDimension(width, height);
		CANVAS_WIDTH = width;
		CANVAS_HEIGHT = height;
		if (rectCanvas != null) {
			rectCanvas.right = width;
			rectCanvas.bottom = height;
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		if (typeface != null) {
			mTextPaint.setTypeface(typeface);
			if (text!=null && !TextUtils.isEmpty(text.trim()) ) {
				mTextPaint.getTextBounds(text, 0, text.length(), bound);
				if (bound.width() < 150 && bound.height() < 150) {
					while (bound.width() < 150 && bound.height() < 150) {
						textSize += 1;
						mTextPaint.setTextSize(textSize);
						mTextPaint.getTextBounds(text, 0, text.length(), bound);
						//Log.d(TAG, "textSize=" +textSize + " bound=" + bound.width()+"x"+ bound.height());
					}
				}
				
				if (bound.width() > 150 || bound.height() > 150) {
					while (bound.width() > 150 || bound.height() > 150) {
						textSize -= 1;
						mTextPaint.setTextSize(textSize);
						mTextPaint.getTextBounds(text, 0, text.length(), bound);
						//Log.d(TAG, "textSize=" +textSize + " bound=" + bound.width()+"x"+ bound.height());
					}
				}
			} else {
				textSize = 100;
				mTextPaint.setTextSize(textSize);
			}
		}
	}

	public Typeface getTypeface() {
		return typeface;
	}

	public void setTypeface(Typeface typeface) {
		textSize = 50;
		this.typeface = typeface;
		if (typeface != null) {
			mTextPaint.setTextSize(textSize);
			mTextPaint.setTypeface(typeface);
			if (text!=null && !TextUtils.isEmpty(text.trim()) ) {
				mTextPaint.getTextBounds(text, 0, text.length(), bound);
				if (bound.width() < 150 && bound.height() < 150) {
					while (bound.width() < 150 && bound.height() < 150) {
						textSize += 1;
						mTextPaint.setTextSize(textSize);
						mTextPaint.getTextBounds(text, 0, text.length(), bound);
						//Log.d(TAG, "textSize=" +textSize + " bound=" + bound.width()+"x"+ bound.height());
					}
				}
				mTextPaint.getTextBounds(text, 0, 1, bound);
				if (bound.width() > 150 || bound.height() > 150) {
					while (bound.width() > 150 || bound.height() > 150) {
						textSize -= 1;
						mTextPaint.setTextSize(textSize);
						mTextPaint.getTextBounds(text, 0, text.length(), bound);
						//Log.d(TAG, "textSize=" +textSize + " bound=" + bound.width()+"x"+ bound.height());
					}
				}
			} else {
				textSize = 100;
				mTextPaint.setTextSize(textSize);
			}
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		bgCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		mTextPaint.setTextAlign(Align.CENTER);
		bgCanvas.drawText(text, 100, 100- ((mTextPaint.descent() + mTextPaint.ascent()) / 2), mTextPaint);
		canvas.drawBitmap(bgBitmap, rectImage, rectCanvas, bgPaint);
	}

}
