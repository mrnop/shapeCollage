package com.isarainc.frame.montage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewParent;
import android.widget.Toast;

import com.isarainc.dialog.ProcessListener;
import com.isarainc.filters.Filter;
import com.isarainc.layers.Layer;
import com.isarainc.layers.OnLayerChangeListener;
import com.isarainc.layers.TextLayer;
import com.isarainc.multitouch.gesturedetectors.MoveGestureDetector;
import com.isarainc.multitouch.gesturedetectors.RotateGestureDetector;
import com.isarainc.multitouch.gesturedetectors.ShoveGestureDetector;
import com.isarainc.shapecollage.CollageModel;
import com.isarainc.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DrawView extends View {
	private static final String TAG = "DrawView";


	private static int CANVAS_WIDTH;
	private static int CANVAS_HEIGHT;
	Random rnd = new Random();

	private List<Layer> layers = new LinkedList<Layer>();
	private Layer activeLayer;
	private int bgColor;
	private int mode = 0;
	private Paint paint;
	private Rect rectImage;
	private Rect rectCanvas;
	private float mScaleFactor = 1.0f;
	private float mRotationDegrees = 0.f;
	private float mFocusX = 0.f;
	private float mFocusY = 0.f;
	private int mAlpha = 255;
	private List<CollageModel> pictures = new ArrayList<CollageModel>();
	private ScaleGestureDetector mScaleDetector;
	private RotateGestureDetector mRotateDetector;
	private MoveGestureDetector mMoveDetector;
	private ShoveGestureDetector mShoveDetector;
	private GestureDetector mGestureDetector;

	private OnLayerChangeListener onLayerChangeListener;



	private Bitmap bgBitmap;
	private Bitmap workPhoto;
	private Bitmap orgBg;
	private Bitmap bg;
	private Bitmap orgPhoto;

	private Canvas bgCanvas;
	private Canvas photoCanvas;

	private Paint bgPaint;

	private ViewParent mParent;

	private int frameCount = 5;
    private int imageWidth = 720;
    private int imageHeight = 720;
	private boolean drawMode;
	private Layer drawLayer;
	private ProcessListener processListener;
	private boolean isNew = true;
	private int imageSize;


	public DrawView(Context context) {
		super(context);
		if (!isInEditMode()) {
			initView(context);
		}
	}

	public DrawView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);

		if (!isInEditMode()) {
			initView(context);
		}
	}

	public static double getAngle(Point p1, Point p2) {
		double xDiff = p2.x - p1.x;
		double yDiff = p2.y - p1.y;
        return Math.toDegrees(Math.atan2(yDiff, xDiff));
	}

	public OnLayerChangeListener getOnLayerChangeListener() {
		return onLayerChangeListener;
	}

	public void setOnLayerChangeListener(
			OnLayerChangeListener onLayerChangeListener) {
		this.onLayerChangeListener = onLayerChangeListener;
	}

	public ProcessListener getProcessListener() {
		return processListener;
	}

	public void setProcessListener(ProcessListener processListener) {
		this.processListener = processListener;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public int getFrameCount() {
		return frameCount;
	}

	public void setFrameCount(int frameCount) {
		this.frameCount = frameCount;


		if (imageWidth < imageHeight) {
			imageSize = (int) (Math
					.sqrt((double) imageWidth * (double) imageWidth * 0.5
							/ frameCount + 1));
		} else {
			imageSize = (int) (Math
					.sqrt((double) imageHeight * (double) imageHeight * 0.5
							/ frameCount + 1));
		}
		for (int i = 0; i < frameCount; i++) {
			Montage frame = new Montage(getContext());


			int x = rnd.nextInt((imageWidth - imageSize));
			int y = rnd.nextInt((imageHeight - imageSize));

			//Log.d(TAG, "x=" + x + "," + y);
			frame.setAngle(-30 + rnd.nextInt(60));
			frame.setX(imageSize / 2 + x);
			frame.setY(imageSize / 2 + y);
			//frame.refresh();
			MontageLayer layer = new MontageLayer();
			layer.init(getContext());
			layer.setFrameSize(imageSize);
			layer.setFrame(frame);
			addLayer(layer);
		}

	}

	public void recycle() {

		try {
			if (orgBg != null) {
				orgBg.recycle();
				orgBg = null;
			}
		} catch (Exception e) {

		}
		try {
			if (bgBitmap != null) {
				bgBitmap.recycle();
				bgBitmap = null;
			}
		} catch (Exception e) {

		}
		try {
			if (bg != null) {
				bg.recycle();
				bg = null;
			}
		} catch (Exception e) {

		}
		try {
			if (workPhoto != null) {
				workPhoto.recycle();
				workPhoto = null;
			}
		} catch (Exception e) {

		}
		try {
			if (orgPhoto != null) {
				orgPhoto.recycle();
				orgPhoto = null;
			}
		} catch (Exception e) {

		}
	}

	public void applyFilter(final String filter) {
		isNew = false;

		if("none".equals(filter)) {
			if (workPhoto != null) {
				workPhoto.recycle();
				workPhoto = null;
			}
			workPhoto = orgPhoto.copy(Bitmap.Config.ARGB_8888, true);
		}else {

			processListener.processing();
			Log.d(TAG, "mProgressDialog show " +filter);
			Thread thread = new Thread() {
				public void run() {
					if (workPhoto != null) {
						workPhoto.recycle();
						workPhoto = null;
					}
					workPhoto = Filter.loadTemplate(getContext(), filter + ".json").apply(
							orgPhoto);


					((Activity) getContext()).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							invalidate();
							processListener.processed();

						}
					});


					// Log.d(TAG, "mProgressDialog dismiss");
				}
			};
			thread.setDaemon(true);
			thread.start();
		}
	}

	public void addLayer(Layer layer) {
		if (layer instanceof TextLayer) {
			drawMode = true;

			layers.add(layer);
			setActiveLayer(layer, layers.size() - 1);
		} else {
			int pos = 0;
			for (Layer l : layers) {
				if (l instanceof TextLayer) {
					break;
				}
				pos++;
			}
			layers.add(pos, layer);
			setActiveLayer(layer, pos);
		}

		layer.refresh();
	}

	public void delete() {
		if (activeLayer != null) {
			layers.remove(activeLayer);
			invalidate();
		}
	}

	public void reGen(Layer layer) {
		if(layer instanceof  MontageLayer) {
			MontageLayer montageLayer = (MontageLayer)layer;
			montageLayer.reGen();
		}
	}

	public int getBgColor() {
		return bgColor;
	}

	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
        try{

            bg = Bitmap.createBitmap(imageWidth, imageHeight,
                  Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bg);
            Paint paint = new Paint();
			paint.setFilterBitmap(true);
			paint.setDither(true);
			paint.setAntiAlias(true);
            paint.setColor(bgColor);

           canvas.drawRect(0,0,imageWidth,imageHeight,paint);
        }catch(OutOfMemoryError ome){
            bg = null;
        }
	}


	public void setPhoto(Bitmap bitmap) {

		if (workPhoto != null) {
			workPhoto.recycle();
			workPhoto = null;
		}
		if (orgPhoto != null) {
			orgPhoto.recycle();
			orgPhoto = null;
		}


		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		// Shall we change target image size along with background
		Log.d(TAG, "(" + w + ":" + h + ") (" + imageWidth + ":" + imageHeight
				+ ")");
		// Detect landscape
		if (bitmap.getWidth() > bitmap.getHeight()) {
			h = imageHeight;
			w = bitmap.getWidth() * imageHeight / bitmap.getHeight();

		} else {
			w = imageWidth;
			h = bitmap.getHeight() * imageWidth / bitmap.getWidth();
		}

		imageWidth = w;
		imageHeight = h;
		// textLayout.setResolution(w, h);
		rectImage = new Rect(0, 0, imageWidth, imageHeight);

		orgPhoto = createScaledBitmap(bitmap, w, h);
		workPhoto = orgPhoto.copy(Bitmap.Config.ARGB_8888, true);

		//bgCanvas = new Canvas(workPhoto);

		bgBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
				Bitmap.Config.ARGB_8888);
		bgCanvas = new Canvas(bgBitmap);

		// Adjust Background
		if (orgBg != null) {
			try{
				bg = Utils.tileBitmap(new BitmapDrawable(getResources(), orgBg),
						imageWidth, imageHeight);
			}catch(OutOfMemoryError ome) {
				bg = null;
			}
		}else{
			try{

				bg = Bitmap.createBitmap(imageWidth, imageHeight,
						Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bg);
				Paint paint = new Paint();
				paint.setFilterBitmap(true);
				paint.setDither(true);
				paint.setAntiAlias(true);
				paint.setColor(bgColor);

				canvas.drawRect(0,0,imageWidth,imageHeight,paint);
			}catch(OutOfMemoryError ome){
				bg = null;
			}
		}
		requestLayout();
		invalidate();
	}

	public static Bitmap createScaledBitmap(Bitmap bitmap, int newWidth,
											int newHeight) {
		Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight,
				Bitmap.Config.ARGB_8888);

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
	public Bitmap getBg() {
		return orgBg;
	}

	public void setBg(Bitmap background) {
        this.orgBg = background;
        if (orgBg != null) {
            try{
                bg = Utils.tileBitmap(new BitmapDrawable(getResources(), orgBg),
                        imageWidth, imageHeight);
            }catch(OutOfMemoryError ome){
                bg = null;
            }
        } else {
            bg = null;
        }

	}

	public void initView(Context context) {
		imageWidth = Utils.getHeight(context);

		if (imageWidth <= 720) {
			imageWidth = 720;
		}
		imageHeight = imageWidth;

		setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);

		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		mRotateDetector = new RotateGestureDetector(context,
				new RotateListener());
		mMoveDetector = new MoveGestureDetector(context, new MoveListener());
		mShoveDetector = new ShoveGestureDetector(context, new ShoveListener());
		mGestureDetector = new GestureDetector(context, new GestureListener());

		setFocusable(true);
		setFocusableInTouchMode(true);

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.STROKE);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		paint.setAntiAlias(true);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		paint.setAntiAlias(true);
        rectCanvas = new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        rectImage = new Rect(0, 0, imageWidth, imageHeight);
	}

	public void clearMontages() {
		layers.clear();
	}






	public void moveToTop() {
		if (activeLayer != null) {
			layers.remove(activeLayer);
			layers.add(0, activeLayer);
			invalidate();
		}
	}


	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int measuredWidth = getMeasuredWidth();
		int measuredHeight = getMeasuredHeight();

		//ifilter (imageWidth > imageHeight) {
		if (imageWidth / imageHeight > measuredWidth / measuredHeight) {
			int width = measuredWidth;
			int height = width * imageHeight / imageWidth;

			setMeasuredDimension(width, height);
			CANVAS_WIDTH = width;
			CANVAS_HEIGHT = height;
			if (rectCanvas != null) {
				rectCanvas.right = width;
				rectCanvas.bottom = height;
			}
		} else {
			int height = getMeasuredHeight();
			int width = height * imageWidth / imageHeight;

			setMeasuredDimension(width, height);
			CANVAS_WIDTH = width;
			CANVAS_HEIGHT = height;
			if (rectCanvas != null) {
				rectCanvas.right = width;
				rectCanvas.bottom = height;
			}
		}

    }


	public void onDraw(Canvas canvas) {
		// Log.d(TAG, "save point=" + savePoints.toString() +
		// backupPoints.toString());
		if (!isInEditMode()) {
			if(orgPhoto!=null) {
				bgCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
				if (bg != null) {
					bgCanvas.drawBitmap(bg, 0, 0, bgPaint);
				}
				if (layers.isEmpty()) {
					return;
				}
				for (int i = layers.size() - 1; i >= 0; i--) {
					Layer layer = layers.get(i);
					if (layer == activeLayer) {
						layer.drawActive(bgCanvas);
					} else {
						layer.draw(bgCanvas);
					}

				}

				canvas.drawBitmap(workPhoto, rectImage, rectCanvas, bgPaint);

				canvas.drawBitmap(bgBitmap, rectImage, rectCanvas, bgPaint);
			}
		}
	}

	void redraw(){
		bgCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		if (bg != null) {
			bgCanvas.drawBitmap(bg, 0, 0, bgPaint);
		}
		if (layers.isEmpty()) {
			return;
		}
		for (int i = layers.size() - 1; i >= 0; i--) {
			Layer layer = layers.get(i);
			layer.draw(bgCanvas);

		}
	}

	public void refresh() {
		setDrawingCacheEnabled(false); // clear the cache here
		invalidate();
	}

	public Bitmap getSaveBitmap() {
		redraw();
		Bitmap saveBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
				Bitmap.Config.ARGB_8888);
		Canvas saveCanvas = new Canvas(saveBitmap);
		saveCanvas.drawBitmap(workPhoto, rectImage, rectCanvas, bgPaint);
		saveCanvas.drawBitmap(bgBitmap, rectImage, rectCanvas, bgPaint);
		return saveBitmap;
	}

	public String saveToFile(String filename) {
		if(isNew){
			return null;
		}
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

		saveCanvas.drawBitmap(workPhoto, 0,0, bgPaint);

		saveCanvas.drawBitmap(bgBitmap, 0, 0, bgPaint);
		OutputStream fOut = null;
		File file = getSaveFile(filename);
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
		return file.getAbsolutePath();

	}

	private File getSaveFile(String filename) {

		if (Utils.isSDCARDMounted()) {
			File dir = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "Pictures" + File.separator + "shapecollage");
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
					+ "Pictures" + File.separator + "shapecollage");
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

	private void attemptClaimDrag() {
		mParent = getParent();
		if (mParent != null) {
			mParent.requestDisallowInterceptTouchEvent(true);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {


		attemptClaimDrag();


		int x = (int) event.getX(); // or getRawX();
		int y = (int) event.getY();
		// Log.d(TAG, "event " + event);

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// this.setActiveLayer(null);
			int newX = imageWidth * x / CANVAS_WIDTH;
			int newY = imageHeight * y / CANVAS_HEIGHT;
			// Log.d(TAG, "ACTION_DOWN " + newX +"," +newY);
			boolean hit = false;
			synchronized (layers){
				for (int i = 0; i < layers.size(); i++) {
					Layer layer = layers.get(i);
					//Log.d(TAG, "" + i + "--> layer=" + layer);

					if (layer.isInBound( newX, newY)) {
						Log.d(TAG, "Change layer to " + layer);
						setActiveLayer(layer, i);
						invalidate();
						hit= true;
						if (layer instanceof TextLayer) {
							drawMode = true;
							drawLayer = activeLayer;
						} else {
							drawLayer = null;
							drawMode = false;
						}

						break;
					} else {
                   /* if (drawMode
                            || (activeLayer instanceof TextLayer && !((TextLayer) activeLayer)
                            .isAlongPath())) {
                        mFocusX = offsetX;
                        mFocusY = offsetY;
                        Log.d(TAG, "hit " +hit +", drawMode==" +drawMode );
                        this.setActiveLayer(null, 0);
						invalidate();
                    }*/
					}
					//
					//if (layer.isInBound(paint, newX, newY)) {
					//	Log.d(TAG, "Change layer to " + layer);
					//	setActiveLayer(layer, i);
					//	break;
					//}
				}
				if(!hit && !drawMode){
					Log.d(TAG, "hit " + hit + ", drawMode==" + drawMode);
					this.setActiveLayer(null, 0);
					invalidate();

				}

			}

		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			// outTouchCnt++;
			//drawMode = false;
		}

		mScaleDetector.onTouchEvent(event);
		mRotateDetector.onTouchEvent(event);
		mMoveDetector.onTouchEvent(event);
		mShoveDetector.onTouchEvent(event);
		mGestureDetector.onTouchEvent(event);

		return true; // indicate event was handled


	}

	public Layer getActiveLayer() {
		return activeLayer;
	}

	public void setActiveLayer(Layer activeLayer, int index) {
		this.activeLayer = activeLayer;
		if (activeLayer != null) {
			mFocusX = activeLayer.getX();
			mFocusY = activeLayer.getY();
			mScaleFactor = activeLayer.getScale();
			mRotationDegrees = activeLayer.getAngle();
			// Move to Top
			if (layers.size() > 1) {

				try {
					layers.remove(activeLayer);
					layers.add(0, activeLayer);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (onLayerChangeListener != null) {
			onLayerChangeListener.onLayerChanged(activeLayer, index);

		}

	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor(); // scale change since
														// previous event

			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

			return true;
		}
	}

	private class RotateListener extends
			RotateGestureDetector.SimpleOnRotateGestureListener {
		@Override
		public boolean onRotate(RotateGestureDetector detector) {
			mRotationDegrees -= detector.getRotationDegreesDelta();
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
			// Log.d(TAG, "onMove=" + mFocusX + "," + mFocusY);
			// mFocusX = detector.getFocusX();
			// mFocusY = detector.getFocusY();

			if (activeLayer != null) {

				if (activeLayer instanceof TextLayer) {
					if (((TextLayer) activeLayer).isAlongPath()) {
						for (Point p : ((TextLayer) activeLayer)
								.getSavePoints()) {
							p.x += d.x;
							p.y += d.y;
						}
						activeLayer.setAngle(0);
						activeLayer.setScale(mScaleFactor);
					} else {

						//float scaledImageCenterX = (activeLayer.getBound().width() * mScaleFactor) / 2;
						//float scaledImageCenterY = (activeLayer.getBound().height() * mScaleFactor) / 2;

						activeLayer.setX((int) (mFocusX));
						activeLayer.setY((int) (mFocusY));
						activeLayer.setScale(mScaleFactor);
						activeLayer.setAngle((int) mRotationDegrees);
						activeLayer.refresh();
						/*
						Move draw function on Layer class
						activeLayer.getMatrix().reset();
						activeLayer.getMatrix().postScale(mScaleFactor,
								mScaleFactor);
						activeLayer.getMatrix().postRotate(mRotationDegrees,
								scaledImageCenterX, scaledImageCenterY);
						activeLayer.getMatrix().postTranslate(
								mFocusX - scaledImageCenterX,
								mFocusY - scaledImageCenterY);
								*/

					}
				} else {

					activeLayer.setX((int) (mFocusX));
					activeLayer.setY((int) (mFocusY));
					activeLayer.setScale(mScaleFactor);
					activeLayer.setAngle((int) mRotationDegrees);
					activeLayer.refresh();

				}
				invalidate();

			}

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

			if (activeLayer instanceof TextLayer) {
				/*Log.d(TAG, "onSingleTapUp drawMode= " + drawMode + " tabCount=" +tabCount);
                //setActiveLayer(null, 0);
                if(tabCount>0){
                    tabCount = 0;
                    drawMode = false;
                }
                if(drawMode){
                    tabCount++;
                    drawMode = true;
                }*/

			}

			return true;
		}
		/*
		@Override
		public void onLongPress(MotionEvent e) {
			if (activeLayer != null && activeLayer instanceof MontageLayer
					&& onMontageAdjustListener != null) {
				onMontageAdjustListener.onAdjusting(activeLayer,
						((MontageLayer) activeLayer).getFrame());
			}
			if (activeLayer instanceof TextLayer
					&& ((TextLayer) activeLayer).isAlongPath()) {
				drawMode = false;
			}
			if(drawMode){
				drawMode = false;
				setActiveLayer(null,0);
				invalidate();
			}
		}
*/
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
				if (activeLayer instanceof TextLayer
						&& ((TextLayer) activeLayer).isAlongPath()) {
					if (drawMode) {

						// Log.d(TAG, "onScroll " + mode);
						for (int i = 0; i < e2.getHistorySize(); i++) {
							Point point = new Point();
							point.x = (int) e2.getHistoricalX(i);
							point.y = (int) e2.getHistoricalY(i);
							savePoints.add(point);

						}
						if (savePoints.size() > 5) {

							((TextLayer) drawLayer).getSavePoints().clear();
							((TextLayer) drawLayer).getSavePoints().addAll(
									projectPoint(savePoints));
							invalidate();
						}
					}
				}
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
}
