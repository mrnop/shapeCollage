package com.isarainc.frame.montage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.ImageView;

import com.isarainc.filters.Filter;
import com.isarainc.fonts.FontManager;
import com.isarainc.layers.Layer;

import java.util.Random;


public class AdjustView extends ImageView {
	public static final float STROKE_WIDTH = 5f;
	private static final String TAG = "AdjustView";
	private TextPaint mTextPaint;

	interface OnLayerChangeListener {
		void onLayerChanged(Layer activeLayer, int index);
	}

	private Bitmap frame;

	private Rect rectImage;
	private Rect rectCanvas;


	private static int CANVAS_WIDTH;
	private static int CANVAS_HEIGHT;

	private int imageWidth = 400;

	private int imageHeight = 484;

	private OnLayerChangeListener onLayerChangeListener;

	Random rnd = new Random();

	float oldDist = 1f;
	private Bitmap workBitmap;
	private Canvas bgCanvas;
	private Paint bgPaint;
	private String caption;
	private int captionSize=60;

	private float mFocusX = 0.f;
	private float mFocusY = 0.f;



	private ViewParent mParent;


	private boolean processing;



	private int centerX = 300;
	private int centerY = 400;




	public AdjustView(Context context) {
		super(context);
		workBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
				Config.ARGB_8888);
		bgCanvas = new Canvas(workBitmap);
		if (!isInEditMode()) {
			initView(context);
		}
	}

	public AdjustView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		workBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
				Config.ARGB_8888);
		bgCanvas = new Canvas(workBitmap);
		if (!isInEditMode()) {
			initView(context);
		}
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;

	}

	public int getCaptionSize() {
		return captionSize;
	}

	public void setCaptionSize(int captionSize) {
		this.captionSize = captionSize;
		bgPaint.setTextSize(captionSize);

	}

	public Bitmap getFrame() {
		return frame;
	}

	public void setFrame(Bitmap org) {

		this.frame = org;
		imageWidth=org.getWidth();
		imageHeight=org.getHeight();
	}

	public void recycle() {

		try {
			if (workBitmap != null) {
				workBitmap.recycle();
				workBitmap = null;
			}
		} catch (Exception e) {

		}
		try {
			if (frame != null) {
				frame.recycle();
				frame = null;
			}
		} catch (Exception e) {

		}

	}



	public void initView(Context context) {
		// imgFilter = new ImageFilters();
		setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);


		rectCanvas = new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
		rectImage = new Rect(0, 0, imageWidth, imageHeight);
		bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		bgPaint.setFilterBitmap(true);
		bgPaint.setDither(true);

		mTextPaint = new TextPaint();
		mTextPaint.setFilterBitmap(false);
		mTextPaint.setDither(true);
		mTextPaint.setAntiAlias(true);
		FontManager fontManager = FontManager.getInstance(context);
		mTextPaint.setTextSize(captionSize);
		mTextPaint.setTypeface(fontManager.loadFont("tushand.ttf").getTypeface());


	}


	public int getCenterX() {
		return centerX;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
		mFocusX = centerX;
	}

	public int getCenterY() {
		return centerY;

	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;

		mFocusY = centerY;
	}



	public static Bitmap createScaledBitmap(Bitmap bitmap, int newWidth,
			int newHeight) {
		Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight,
				Config.ARGB_8888);

		float ratioX = newWidth / (float) bitmap.getWidth();
		float ratioY = newHeight / (float) bitmap.getHeight();
		float middleX = newWidth / 2.0f;
		float middleY = newHeight / 2.0f;

		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY
				- bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
		return scaledBitmap;
	}

	public OnLayerChangeListener getOnLayerChangeListener() {
		return onLayerChangeListener;
	}

	public void setOnLayerChangeListener(
			OnLayerChangeListener onLayerChangeListener) {
		this.onLayerChangeListener = onLayerChangeListener;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
	//	Log.d(TAG, "width=" +width + "x" +height);
		//if (imageWidth > imageHeight) {

			// int width = getMeasuredWidth();
			height = width * imageHeight / imageWidth;

			setMeasuredDimension(width, height);
			CANVAS_WIDTH = width;
			CANVAS_HEIGHT = height;
			if (rectCanvas != null) {
				rectCanvas.right = width;
				rectCanvas.bottom = height;
			}


	}




	public void setImageResolution(int w, int h) {

		imageWidth = w;
		imageHeight = h;
		// textLayout.setResolution(w, h);
		rectImage = new Rect(0, 0, imageWidth, imageHeight);
		try {
			workBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
					Config.ARGB_8888);
		} catch (OutOfMemoryError oom) {

			workBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
					Config.ARGB_8888);
		}
		bgCanvas = new Canvas(workBitmap);
		requestLayout();
		invalidate();

	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		int width = drawable.getIntrinsicWidth();
		width = width > 0 ? width : 1;
		int height = drawable.getIntrinsicHeight();
		height = height > 0 ? height : 1;

		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	public int getImageWidth() {
		return imageWidth;
	}


	public int getImageHeight() {
		return imageHeight;
	}


	public void onDraw(Canvas canvas) {
		//Log.d(TAG, "onDraw");
		if (!isInEditMode()) {

			if (frame != null) {

				bgCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

				bgCanvas.drawBitmap(frame, 0, 0, bgPaint);

				bgCanvas.drawText(caption,20,420,mTextPaint);

			}

			if (frame != null) {
				canvas.drawBitmap(workBitmap, rectImage, rectCanvas, bgPaint);
			}
		}

	}



	private void attemptClaimDrag() {
		mParent = getParent();
		if (mParent != null) {
			mParent.requestDisallowInterceptTouchEvent(true);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (processing) {
			return false;
		}
		processing = true;

		attemptClaimDrag();
		int x = (int) event.getX(); // or getRawX();
		int y = (int) event.getY();
		// Log.d(TAG, "event " + event);

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// this.setActiveLayer(null);
			int newX = imageWidth * x / CANVAS_WIDTH;
			int newY = imageHeight * y / CANVAS_HEIGHT;
			// Log.d(TAG, "ACTION_DOWN " + newX +"," +newY);


		} else if (event.getAction() == MotionEvent.ACTION_UP) {

		}


		processing = false;

		return true; // indicate event was handled

	}

	public void refresh() {
		setDrawingCacheEnabled(false); // clear the cache here
		invalidate();
	}





}
