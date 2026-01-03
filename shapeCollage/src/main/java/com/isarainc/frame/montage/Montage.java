package com.isarainc.frame.montage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.isarainc.fonts.FontManager;

import java.io.InputStream;
import java.util.Random;


public class Montage {

	private static final String TAG = "Montage";

	private int centerX = 0;
	private int centerY = 0;
	private float angle;
	private String frame;

	private float scaleFactor = 1f;
	private Context context;
	private Bitmap frameBitmap;
	private String caption="Photo";
	private int captionSize=60;
	private TextPaint mTextPaint;
	private Paint paint;
	private StaticLayout textLayout;
	private FontManager fontManager;
	private Random rnd=new Random();

	public Montage() {
		super();
	}

	public Montage(Context context) {
		super();
		this.context = context;
		InputStream istr;
		try {
			istr = context.getAssets().open("frame.png");
			frameBitmap = BitmapFactory.decodeStream(istr);
		} catch (Throwable t) {
		
			t.printStackTrace();
		}

		mTextPaint = new TextPaint();
		mTextPaint.setFilterBitmap(true);
		mTextPaint.setDither(true);
		mTextPaint.setAntiAlias(true);
		paint = new Paint();
		paint.setFilterBitmap(true);
		paint.setDither(true);
		paint.setAntiAlias(true);

		fontManager= FontManager.getInstance(context);
		mTextPaint.setTextSize(captionSize);
		mTextPaint.setTypeface(fontManager.loadFont("tushand.ttf").getTypeface());

		textLayout = new StaticLayout(caption, mTextPaint, 360,
				Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
		InputStream istr;
		try {
			istr = context.getAssets().open("frame.png");
			frameBitmap = BitmapFactory.decodeStream(istr);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
		textLayout = new StaticLayout(caption, mTextPaint, 360,
				Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
	}

	public int getCaptionSize() {
		return captionSize;
	}

	public void setCaptionSize(int captionSize) {
		this.captionSize = captionSize;
		mTextPaint.setTextSize(captionSize);
	}

	public int getX() {
		return centerX;
	}

	public void setX(int centerX) {
		this.centerX = centerX;
	}

	public int getY() {
		return centerY;
	}

	public void setY(int centerY) {
		this.centerY = centerY;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public String getFrame() {
		return frame;
	}

	public void setFrame(String frame) {
		this.frame = frame;
	}



	public float getScale() {
		return scaleFactor;
	}

	public void setScale(float scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(String strPath,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(strPath, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		Bitmap ret = null;
		try {
			 ret = BitmapFactory.decodeFile(strPath, options);
		}catch(OutOfMemoryError ooe){
			options.inJustDecodeBounds = false;
			options.inPreferredConfig = Bitmap.Config.RGB_565;

			try {
				ret = BitmapFactory.decodeFile(strPath, options);
			}catch(Throwable t){
				t.printStackTrace();
			}
		}
		return ret;
	}


	public Bitmap genBitmap(int size) {

		Bitmap bmWithFrame = Bitmap.createBitmap(frameBitmap.getWidth(),
				frameBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmWithFrame);

		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

		canvas.drawBitmap(frameBitmap, 0, 0, paint);
		canvas.save();
		canvas.translate(20, 380);
		textLayout.draw(canvas);
		canvas.restore();
		
		Bitmap bm = Bitmap.createScaledBitmap(bmWithFrame, size,
				size * bmWithFrame.getHeight() / bmWithFrame.getWidth(), false);
		bmWithFrame.recycle();
		bmWithFrame=null;
		return bm;

	}
}
