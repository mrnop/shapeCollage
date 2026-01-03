package com.isarainc.frame.collage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import com.isarainc.filters.Filter;
import com.isarainc.fonts.FontManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class Frame {

	private static final String TAG = "Frame";
	public static final String[] FILTERS={"x_pro2.json","1977.json","andy.json","brannan.json","old.json"
		,"edgewood.json","xprocess.json","san_carmen.json","lord_kelvin.json","toaster.json",
		 "haas.json","keylime.json","lucky.json"};
	private String filter="x_pro2.json";
	private int centerX = 0;
	private int centerY = 0;
	private float angle;
	private String frame;
	private String src;
	private Bitmap srcBitmap;
	private float scaleFactor = 1f;
	private Context context;
	private Bitmap frameBitmap;
	private String caption="Photo";
	private int captionSize=60;
	private Matrix matrix = new Matrix();
	private TextPaint mTextPaint;
	private Paint paint;
	private StaticLayout textLayout;
	private FontManager fontManager;
	private Random rnd=new Random();
	
	public Frame() {
		super();
	}

	public Frame(Context context) {
		super();
		this.context = context;
		InputStream istr;
		try {
			istr = context.getAssets().open("frame.png");
			frameBitmap = BitmapFactory.decodeStream(istr);
		} catch (Throwable t) {
		
			t.printStackTrace();
		}
		filter=FILTERS[rnd.nextInt(FILTERS.length)];
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

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
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

	public int getCenterX() {
		return centerX;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public void setCenterY(int centerY) {
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

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
		Bitmap bitmap = decodeSampledBitmapFromResource(src, frameBitmap.getWidth(), frameBitmap.getWidth());
		if(bitmap==null){
			return;
		}

		int w;
		int h;

		if (bitmap.getWidth() > bitmap.getHeight()) {
			h = frameBitmap.getWidth();
			w = bitmap.getWidth() * h / bitmap.getHeight();

		} else if (bitmap.getWidth() < bitmap.getHeight()){
			w = frameBitmap.getWidth();
			h = bitmap.getHeight() * w / bitmap.getWidth();

		}else{
			w = frameBitmap.getWidth();
			h = frameBitmap.getWidth();

		}
		if(srcBitmap!=null){
			srcBitmap.recycle();
			srcBitmap =null;
		}
		srcBitmap = Bitmap.createScaledBitmap(bitmap, w,h, false);
		Log.d(TAG, "srcBitmap=" + w + "x" +h + ", scaleFactor="+scaleFactor);
		if(bitmap!=null){
			bitmap.recycle();
			bitmap =null;
		}
		//float scaleRatio = (float)w / (float)bitmap.getWidth();

		centerX = (frameBitmap.getWidth()) /2;
		centerY =  (frameBitmap.getWidth() )/2;


	}
	public  Bitmap decodeSampledBitmapFromAsset(String strPath) {
		Bitmap ret = null;
		InputStream istr;
		try {
			istr = context.getAssets().open(strPath);
			Bitmap bitmap = BitmapFactory.decodeStream(istr);

			Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888,
					true);

			ret = mutableBitmap;

		} catch (IOException e) {

			e.printStackTrace();
		}
		return ret;

	}
	public void setSrcFromAsset(String src) {
		this.src = src;
		Bitmap bitmap = decodeSampledBitmapFromAsset(src);
		if(bitmap==null){
			return;
		}
		//matrix.reset();
		int w;
		int h;

		if (bitmap.getWidth() > bitmap.getHeight()) {
			h = frameBitmap.getWidth();
			w = bitmap.getWidth() * h / bitmap.getHeight();

		} else {
			w = frameBitmap.getWidth();
			h = bitmap.getHeight() * w / bitmap.getWidth();
		}
		if(srcBitmap!=null){
			srcBitmap.recycle();
			srcBitmap =null;
		}
		srcBitmap = Bitmap.createScaledBitmap(bitmap, w,h, false);
		if(bitmap!=null){
			bitmap.recycle();
			bitmap =null;
		}
		Log.d(TAG, "srcBitmap=" + w + "x" +h);
		centerX = (frameBitmap.getWidth()) /2;
		centerY =  (frameBitmap.getWidth())/2;
	}
	public float getScaleFactor() {
		return scaleFactor;
	}

	public void setScaleFactor(float scaleFactor) {
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

	public Bitmap getSrcBitmap() {
		return srcBitmap;
	}

	public void setSrcBitmap(Bitmap srcBitmap) {
		this.srcBitmap = srcBitmap;
	}

	public Bitmap genBitmap(int size) {

		Bitmap bmWithFrame = Bitmap.createBitmap(frameBitmap.getWidth(),
				frameBitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmWithFrame);

		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if(srcBitmap != null) {
            float scaledImageCenterX = (srcBitmap.getWidth() * scaleFactor) / 2;
			float scaledImageCenterY = (srcBitmap.getHeight() * scaleFactor) / 2;
			Log.d(TAG, "width=" + srcBitmap.getWidth() + ",height" + srcBitmap.getHeight() );
			Log.d(TAG, "center=" + centerX + "," + centerY + ": scaledImageCenter=" + scaledImageCenterX + "," + scaledImageCenterY);
			matrix.reset();
            matrix.postScale(scaleFactor, scaleFactor);
            matrix.postRotate(angle, scaledImageCenterX, scaledImageCenterY);

            matrix.postTranslate(centerX - scaledImageCenterX, centerY - scaledImageCenterY);
            if (filter != null) {
                canvas.drawBitmap(Filter.loadTemplate(context, filter)
                        .apply(srcBitmap), matrix, paint);
            } else {
                canvas.drawBitmap(srcBitmap, matrix, paint);
            }
        }
       // Log.d(TAG, "center=" + centerX + "x" + centerY );
		canvas.drawBitmap(frameBitmap, 0, 0, paint);
		canvas.save();
		canvas.translate(20, 380);
		textLayout.draw(canvas);
		canvas.restore();
		
		return Bitmap.createScaledBitmap(bmWithFrame, size,
				size * bmWithFrame.getHeight() / bmWithFrame.getWidth(), false);

	}
}
