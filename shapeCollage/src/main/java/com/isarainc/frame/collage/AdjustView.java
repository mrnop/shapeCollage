package com.isarainc.frame.collage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewParent;
import android.widget.ImageView;

import com.isarainc.filters.Filter;
import com.isarainc.fonts.FontManager;
import com.isarainc.layers.Layer;
import com.isarainc.multitouch.gesturedetectors.MoveGestureDetector;
import com.isarainc.multitouch.gesturedetectors.RotateGestureDetector;
import com.isarainc.multitouch.gesturedetectors.ShoveGestureDetector;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class AdjustView extends ImageView {
	public static final float STROKE_WIDTH = 5f;
	private static final String TAG = "AdjustView";
	
	interface OnLayerChangeListener {
		void onLayerChanged(Layer activeLayer, int index);
	}

	private Matrix matrix = new Matrix();
	private Bitmap frame;
	private Bitmap background;
	private Bitmap orgBg;
	private Rect rectImage;
	private Rect rectCanvas;
	private TextPaint mTextPaint;
	private List<Layer> layers = new LinkedList<Layer>();
	private Layer activeLayer;

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
	private float scaleFactor = 1.0f;
	private float rotationDegrees = 0.f;
	private float mFocusX = 0.f;
	private float mFocusY = 0.f;

	private float imageScaleFactor = scaleFactor;
	private float imageAngle = rotationDegrees;

	private int mAlpha = 255;

	private ScaleGestureDetector mScaleDetector;
	private RotateGestureDetector mRotateDetector;
	private MoveGestureDetector mMoveDetector;
	private ShoveGestureDetector mShoveDetector;
	private GestureDetector mGestureDetector;

	private ViewParent mParent;
	// private ImageFilters imgFilter;

	private int numberOfFaceDetected;

	private boolean processing;
	private Layer drawLayer;
	private boolean dragMode;
	private int memoTextSize = 60;

	private String filter;
	private String memo="Polaroid";
	private int centerX = 300;
	private int centerY = 400;
	private FontManager fontManager;
	private StaticLayout textLayout;

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			scaleFactor *= detector.getScaleFactor(); // scale change since
														// previous event

			// Don't let the object get too small or too large.
			scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));

			return true;
		}
	}

	private class RotateListener extends
			RotateGestureDetector.SimpleOnRotateGestureListener {
		@Override
		public boolean onRotate(RotateGestureDetector detector) {
			rotationDegrees -= detector.getRotationDegreesDelta();
			return true;
		}
	}

	private class MoveListener extends
			MoveGestureDetector.SimpleOnMoveGestureListener {

		@Override
		public void onMoveEnd(MoveGestureDetector detector) {
			if (activeLayer != null) {

			} else {
				// Draw
				activeLayer = drawLayer;
			}
			super.onMoveEnd(detector);
		}

		@Override
		public boolean onMove(MoveGestureDetector detector) {
			PointF d = detector.getFocusDelta();
			mFocusX += d.x;
			mFocusY += d.y;
			//Log.d(TAG, "onMove=" + mFocusX + "," + mFocusY);
			// mFocusX = detector.getFocusX();
			// mFocusY = detector.getFocusY();

			if (activeLayer != null) {



					activeLayer.setX((int) (mFocusX));
					activeLayer.setY((int) (mFocusY));
					activeLayer.setScale(scaleFactor);
					activeLayer.setAngle((int) rotationDegrees);
					activeLayer.refresh();



			} else {
			
				centerX = (int) (mFocusX);
				centerY = (int) (mFocusY);
				imageScaleFactor = scaleFactor;
				imageAngle = rotationDegrees;
				float scaledImageCenterX = (background.getWidth() * imageScaleFactor) / 2;
				float scaledImageCenterY = (background.getHeight() * imageScaleFactor) / 2;
				matrix.reset();
				matrix.postScale(imageScaleFactor, imageScaleFactor);
				matrix.postRotate(imageAngle, scaledImageCenterX,
						scaledImageCenterY);
				matrix.postTranslate(centerX - scaledImageCenterX, centerY
						- scaledImageCenterY);

			}
			invalidate();

			return true;
		}

	}

	private class ShoveListener extends
			ShoveGestureDetector.SimpleOnShoveGestureListener {
		@Override
		public boolean onShove(ShoveGestureDetector detector) {
			mAlpha += detector.getShovePixelsDelta();
			if (mAlpha > 255)
				mAlpha = 255;
			else if (mAlpha < 0)
				mAlpha = 0;

			return true;
		}
	}

	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {
		List<Point> savePoints = new LinkedList<Point>();

		@Override
		public boolean onSingleTapUp(MotionEvent ev) {
			dragMode = false;
			setActiveLayer(null, 0);
			return true;
		}

		@Override
		public void onShowPress(MotionEvent ev) {

		}

		@Override
		public void onLongPress(MotionEvent ev) {

		}

		public List<Point> projectPoint(List<Point> points) {
			List<Point> newPoints = new LinkedList<Point>();
			// Log.d(TAG, "projectPoint " + imageWidth + "," + imageHeight + ":"
			// + CANVAS_WIDTH + "," + CANVAS_HEIGHT);

			for (Point point : points) {
				newPoints.add(new Point(point.x * imageWidth / CANVAS_WIDTH,
						point.y * imageHeight / CANVAS_HEIGHT));
			}
			// Log.d(TAG, "projectPoint " +newPoints );
			return newPoints;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (drawLayer != null) {

			}
			return true;
		}

		@Override
		public boolean onDown(MotionEvent ev) {
			if (drawLayer != null) {
				savePoints.clear();
			}
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			return true;
		}
	}

	public AdjustView(Context context) {
		super(context);
		workBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
				Bitmap.Config.ARGB_8888);
		bgCanvas = new Canvas(workBitmap);
		if (!isInEditMode()) {
			initView(context);
		}
	}

	public AdjustView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		workBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
				Bitmap.Config.ARGB_8888);
		bgCanvas = new Canvas(workBitmap);
		if (!isInEditMode()) {
			initView(context);
		}
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
			if (background != null) {
				background.recycle();
				background = null;
			}
		} catch (Exception e) {

		}
		try {
			if (orgBg != null) {
				orgBg.recycle();
				orgBg = null;
			}
		} catch (Exception e) {

		}
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

	public void addLayer(Layer layer) {


			int pos = 0;
			for (Layer l : layers) {

				pos++;
			}
			layers.add(pos, layer);
			setActiveLayer(layer, pos);

		layer.refresh();
	}

	public void removeLayer(Layer layer) {
		layers.remove(layer);
		if (!layers.isEmpty()) {
			setActiveLayer(layers.get(0), 0);
		} else {
			setActiveLayer(null, 0);
		}

	}

	public void initView(Context context) {
		// imgFilter = new ImageFilters();
		setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);

		// setFocusable(true);
		// setFocusableInTouchMode(true);
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		mRotateDetector = new RotateGestureDetector(context,
				new RotateListener());
		mMoveDetector = new MoveGestureDetector(context, new MoveListener());
		mShoveDetector = new ShoveGestureDetector(context, new ShoveListener());
		mGestureDetector = new GestureDetector(getContext(),
				new GestureListener());
		rectCanvas = new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
		rectImage = new Rect(0, 0, imageWidth, imageHeight);
		bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		
		// txtPaint.setColor(Color.BLACK);
		mTextPaint = new TextPaint();
		mTextPaint.setFilterBitmap(false);
		mTextPaint.setDither(true);
		mTextPaint.setAntiAlias(true);
		fontManager=FontManager.getInstance(context);
		mTextPaint.setTextSize(memoTextSize);
		mTextPaint.setTypeface(fontManager.loadFont("tushand.ttf").getTypeface());

		textLayout = new StaticLayout(memo, mTextPaint, 340,
				Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
		if (!layers.isEmpty()) {
			activeLayer = layers.get(0);
		}
	

	}

	public void applyFilter(int filter) {
		if (background != null) {
			// background.recycle();
			// background=null;
		}
		background = orgBg.copy(Config.ARGB_8888, true);
		switch (filter) {
		case 0:
			background = orgBg.copy(Config.ARGB_8888, true);
			break;
		case 1:
			background = Filter.loadTemplate(getContext(), "bb.json").apply(
					orgBg);
			break;
		case 2:
			background = Filter.loadTemplate(getContext(), "neel.json").apply(
					orgBg);
			break;
		case 3:
			background = Filter.loadTemplate(getContext(), "vivid.json").apply(
					orgBg);
			break;
		case 4:
			background = Filter.loadTemplate(getContext(), "1977.json").apply(
					orgBg);
			break;

		case 5:
			background = Filter.loadTemplate(getContext(), "amaro.json").apply(
					orgBg);
			break;
		case 6:
			background = Filter.loadTemplate(getContext(), "andy.json").apply(
					orgBg);
			break;
		case 7:
			background = Filter.loadTemplate(getContext(), "aqua.json").apply(
					orgBg);
			break;
		case 8:
			background = Filter.loadTemplate(getContext(), "brannan.json")
					.apply(orgBg);
			break;
		case 9:
			background = Filter.loadTemplate(getContext(), "bw.json").apply(
					orgBg);
			break;
		case 10:
			background = Filter.loadTemplate(getContext(), "earlybird.json")
					.apply(orgBg);
			break;
		case 11:
			background = Filter.loadTemplate(getContext(), "old.json").apply(
					orgBg);
			break;
		case 12:
			background = Filter.loadTemplate(getContext(), "edgewood.json")
					.apply(orgBg);
			break;
		case 13:
			background = Filter.loadTemplate(getContext(), "hefe.json").apply(
					orgBg);
			break;
		case 14:
			background = Filter.loadTemplate(getContext(), "hudson.json")
					.apply(orgBg);
			break;
		case 15:
			background = Filter.loadTemplate(getContext(), "lo_fi.json").apply(
					orgBg);
			break;
		case 16:
			background = Filter.loadTemplate(getContext(), "joe_cool.json")
					.apply(orgBg);
			break;
		case 17:
			background = Filter.loadTemplate(getContext(), "xprocess.json")
					.apply(orgBg);
			break;
		case 18:
			background = Filter.loadTemplate(getContext(), "tint.json").apply(
					orgBg);
			break;
		case 19:
			background = Filter.loadTemplate(getContext(), "sepia1.json")
					.apply(orgBg);
			break;
		case 20:
			background = Filter.loadTemplate(getContext(), "sepia2.json")
					.apply(orgBg);
			break;

		case 21:
			background = Filter.loadTemplate(getContext(), "sepia3.json")
					.apply(orgBg);
			break;
		case 22:
			background = Filter.loadTemplate(getContext(), "sepia4.json")
					.apply(orgBg);
			break;
		case 23:
			background = Filter.loadTemplate(getContext(), "sepia5.json")
					.apply(orgBg);
			break;
		case 24:
			background = Filter.loadTemplate(getContext(), "sepia6.json")
					.apply(orgBg);
			break;
		case 25:
			background = Filter.loadTemplate(getContext(), "x_pro2.json")
					.apply(orgBg);
			break;
		case 26:
			background = Filter.loadTemplate(getContext(), "walden.json")
					.apply(orgBg);
			break;
		case 27:
			background = Filter.loadTemplate(getContext(), "thresh.json")
					.apply(orgBg);
			break;
		case 28:
			background = Filter.loadTemplate(getContext(), "sierra.json")
					.apply(orgBg);
			break;
		case 29:
			background = Filter.loadTemplate(getContext(), "san_carmen.json")
					.apply(orgBg);
			break;
		case 30:
			background = Filter.loadTemplate(getContext(), "purple.json")
					.apply(orgBg);
			break;
		case 31:
			background = Filter.loadTemplate(getContext(), "nashville.json")
					.apply(orgBg);
			break;
		case 32:
			background = Filter.loadTemplate(getContext(), "lord_kelvin.json")
					.apply(orgBg);
			break;
		case 33:
			background = Filter.loadTemplate(getContext(), "toaster.json")
					.apply(orgBg);
			break;
		case 34:
			background = Filter.loadTemplate(getContext(), "valencia.json")
					.apply(orgBg);
			break;
		case 35:
			background = Filter.loadTemplate(getContext(), "invert.json")
					.apply(orgBg);
			break;
		case 36:
			background = Filter.loadTemplate(getContext(), "arizona.json")
					.apply(orgBg);
			break;
		case 37:
			background = Filter.loadTemplate(getContext(), "haas.json").apply(
					orgBg);
			break;
		case 38:
			background = Filter.loadTemplate(getContext(), "marie.json").apply(
					orgBg);
			break;
		case 39:
			background = Filter.loadTemplate(getContext(), "keylime.json")
					.apply(orgBg);
			break;
		case 40:
			background = Filter.loadTemplate(getContext(), "avenue.json")
					.apply(orgBg);
			break;
		case 41:
			background = Filter.loadTemplate(getContext(), "boardwalk.json")
					.apply(orgBg);
			break;
		case 42:
			background = Filter.loadTemplate(getContext(), "clyde.json").apply(
					orgBg);
			break;
		case 43:
			background = Filter.loadTemplate(getContext(), "cruz.json").apply(
					orgBg);
			break;
		case 44:
			background = Filter.loadTemplate(getContext(), "dean.json").apply(
					orgBg);
			break;
		case 45:
			background = Filter.loadTemplate(getContext(), "lucky.json").apply(
					orgBg);
			break;

		default:
			background = Filter.loadTemplate(getContext(), "metropolis.json")
					.apply(orgBg);
			break;
		}
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
		textLayout = new StaticLayout(memo, mTextPaint, 340,
				Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
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

	public float getScaleFactor() {
		return scaleFactor;
	}

	public void setScaleFactor(float scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	public float getRotationDegrees() {
		return rotationDegrees;
	}

	public void setRotationDegrees(float rotationDegrees) {
		this.rotationDegrees = rotationDegrees;
	}

	public int getNumberOfFaceDetected() {
		return numberOfFaceDetected;
	}

	public void setNumberOfFaceDetected(int numberOfFaceDetected) {
		this.numberOfFaceDetected = numberOfFaceDetected;
	}

	public void setBitmap(Bitmap bg) {
		System.gc();
		if(bg==null){
			return;
		}
		if (orgBg != null) {
			orgBg.recycle();
			orgBg = null;
		}
		orgBg = bg;
		
		if (background != null) {
			background.recycle();
			background = null;
		}

		background = orgBg.copy(Config.ARGB_8888, true);

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

		//	Log.d(TAG, "size=" + width + "x" + height);

//		} else {
//
//			width = (int) (height * imageWidth / imageHeight);
//
//			setMeasuredDimension(width, height);
//			CANVAS_WIDTH = width;
//			CANVAS_HEIGHT = height;
//			if (rectCanvas != null) {
//				rectCanvas.right = width;
//				rectCanvas.bottom = height;
//			}
//
//			Log.d(TAG, "size=" + width + "x" + height);
//
//		}

	}

	public List<Layer> getLayers() {
		return layers;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
		if(filter!=null){
			background = Filter.loadTemplate(getContext(), filter).apply(
					orgBg);
		}
	}

	public int getMemoTextSize() {
		return memoTextSize;
	}

	public void setMemoTextSize(int memoTextSize) {
		this.memoTextSize = memoTextSize;
		mTextPaint.setTextSize(memoTextSize);
	}

	public void setImageResolution(int w, int h) {

		imageWidth = w;
		imageHeight = h;
		// textLayout.setResolution(w, h);
		rectImage = new Rect(0, 0, imageWidth, imageHeight);
		try {
			workBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
					Bitmap.Config.ARGB_8888);
		} catch (OutOfMemoryError oom) {

			workBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
					Bitmap.Config.ARGB_8888);
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

	//
	// public void setImageWidth(int imageWidth) {
	// this.imageWidth = imageWidth;
	// }
	//
	public int getImageHeight() {
		return imageHeight;
	}

	//
	// public void setImageHeight(int imageHeight) {
	// this.imageHeight = imageHeight;
	// }

	public void onDraw(Canvas canvas) {
		//Log.d(TAG, "onDraw");
		if (!isInEditMode()) {

			if (frame != null) {

				bgCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
				bgCanvas.drawBitmap(background, matrix, null);
				bgCanvas.drawBitmap(frame, 0, 0, null);
				bgCanvas.save();
				bgCanvas.translate(20, 380);
				textLayout.draw(bgCanvas);
				bgCanvas.restore();
			}

			if (frame != null) {
				canvas.drawBitmap(workBitmap, rectImage, rectCanvas, bgPaint);
			}
		}

	}

	void drawMultiLineText(String str, float x, float y, Paint paint,
			Canvas canvas) {
		String[] lines = str.split("\n");
		float txtSize = -paint.ascent() + paint.descent();

		if (paint.getStyle() == Style.FILL_AND_STROKE
				|| paint.getStyle() == Style.STROKE) {
			txtSize += paint.getStrokeWidth(); // add stroke width to the text
												// size
		}
		float lineSpace = txtSize * 0.2f; // default line spacing

		for (int i = 0; i < lines.length; ++i) {
			canvas.drawText(lines[i], x, y + (txtSize + lineSpace) * i, paint);
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

			for (int i = layers.size() - 1; i >= 0; i--) {
				Layer layer = layers.get(i);
				if (layer.isInBound(newX, newY)) {
					//Log.d(TAG, "Change layer to " + layer);
					setActiveLayer(layer, i);

					drawLayer = null;
					dragMode = false;


					break;
				}

			}

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			// outTouchCnt++;
			dragMode = false;
		}

		mScaleDetector.onTouchEvent(event);
		mRotateDetector.onTouchEvent(event);
		mMoveDetector.onTouchEvent(event);
		mShoveDetector.onTouchEvent(event);
		mGestureDetector.onTouchEvent(event);

		processing = false;

		return true; // indicate event was handled

	}

	public void refresh() {
		setDrawingCacheEnabled(false); // clear the cache here
		invalidate();
	}


	public Layer getActiveLayer() {
		return activeLayer;
	}

	public void setActiveLayer(Layer activeLayer, int index) {
		this.activeLayer = activeLayer;
		if (activeLayer != null) {
			mFocusX = activeLayer.getX();
			mFocusY = activeLayer.getY();
			scaleFactor = activeLayer.getScale();
			rotationDegrees = activeLayer.getAngle();
		} else {
			mFocusX = centerX;
			mFocusY = centerY;
			scaleFactor = imageScaleFactor;
			rotationDegrees = imageAngle;
		}
		if (onLayerChangeListener != null) {
			onLayerChangeListener.onLayerChanged(activeLayer, index);

		}
	}

	public void init() {
		float scaledImageCenterX = ((float)background.getWidth() * scaleFactor) / 2;
		float scaledImageCenterY = background.getHeight() * scaleFactor / 2;
		matrix.reset();
		matrix.postScale(scaleFactor, scaleFactor);
		//matrix.postRotate(angle, scaledImageCenterX, scaledImageCenterY);
		matrix.postTranslate(centerX - scaledImageCenterX, centerY
				- scaledImageCenterY);
		
	}

}
