package com.isarainc.frame.collage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import com.isarainc.layers.Layer;
import com.isarainc.layers.Polygon;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FrameLayer extends Layer {

    private Frame frame;
	private Bitmap bitmap;
	private int frameSize = 400;

	public static final int MODE_NORMAL = 0;
	public static final int MODE_MULTIPLY = 1;
	public static final int MODE_SCREEN = 2;
	public static final int MODE_OVERLAY = 3;
	private int mode = MODE_NORMAL;

	public FrameLayer() {
		super();

	}



	@Override
	public Rect getBound() {
		return new Rect(0, 0, (int) (bitmap.getWidth() * getScale()), (int) (bitmap.getHeight() * getScale()));
	}

	public Frame getFrame() {
		return frame;
	}

	public void setFrame(Frame frame) {
		this.frame = frame;
		this.bitmap = frame.genBitmap(getFrameSize());
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void reGen(){
		this.bitmap = frame.genBitmap(getFrameSize());
	}



	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}



	public int getFrameSize() {
		return frameSize;
	}

	public void setFrameSize(int frameSize) {
		this.frameSize = frameSize;
	}

	@Override
	public void draw(Canvas canvas) {

		if (mode == MODE_NORMAL) {
			// paint.setXfermode(new PorterDuffXfermode(Mode.ADD));
			canvas.drawBitmap(bitmap, getMatrix(), null);
		} else if (mode == MODE_SCREEN) {
			Paint xferPaint = new Paint();
			xferPaint.setFilterBitmap(true);
			xferPaint.setDither(true);
			xferPaint.setAntiAlias(true);
			xferPaint.setXfermode(new PorterDuffXfermode(Mode.SCREEN));
			canvas.drawBitmap(bitmap, getMatrix(), xferPaint);
		//} else if (mode == MODE_OVERLAY) {
		//	Paint xferPaint = new Paint();
		//	xferPaint.setXfermode(new PorterDuffXfermode(Mode.OVERLAY));
		//	canvas.drawBitmap(bitmap, getMatrix(), xferPaint);
		}

	}

	protected Polygon getPolygon() {
		Rect bounds = getBound();
		int left = getX() - bounds.width() / 2;
		int right = getX() + bounds.width() / 2;
		int top = getY() - bounds.height() / 2;
		int bottom = getY() + bounds.height() / 2;


		//Find polygon from rotate angle
		List<Point> allPoints = new ArrayList<Point>();


		transform.reset();
		transform.postScale(getScale(), getScale());
		transform.setRotate(getAngle(), getX(), getY());

		float[] pts1 = new float[2];

		// Initialize the array with our Coordinate
		pts1[0] = left;
		pts1[1] = top;
		transform.mapPoints(pts1);
		allPoints.add(new Point((int) pts1[0], (int) pts1[1]));

		float[] pts2 = new float[2];

		// Initialize the array with our Coordinate
		pts2[0] = left;
		pts2[1] = bottom;
		transform.mapPoints(pts2);
		allPoints.add(new Point((int) pts2[0], (int) pts2[1]));

		float[] pts3 = new float[2];

		// Initialize the array with our Coordinate
		pts3[0] = right;
		pts3[1] = bottom;
		transform.mapPoints(pts3);
		allPoints.add(new Point((int) pts3[0], (int) pts3[1]));

		float[] pts4 = new float[2];

		// Initialize the array with our Coordinate
		pts4[0] = right;
		pts4[1] = top;
		transform.mapPoints(pts4);
		allPoints.add(new Point((int) pts4[0], (int) pts4[1]));
		return new Polygon(allPoints);
	}

	public void drawActive(Canvas canvas) {
		Polygon polygon = getPolygon();
		draw(canvas);
		Path path = polygon.getPath();
		canvas.drawPath(path, dashPaint);


	}

	protected Polygon getDeletePolygon() {
		List<Point> allPoints = new ArrayList<Point>();
		float[] pts1 = new float[2];

		// Initialize the array with our Coordinate
		pts1[0] = deleteBound.left;
		pts1[1] = deleteBound.top;
		transform.mapPoints(pts1);
		allPoints.add(new Point((int) pts1[0], (int) pts1[1]));

		float[] pts2 = new float[2];

		// Initialize the array with our Coordinate
		pts2[0] = deleteBound.left;
		pts2[1] = deleteBound.bottom;
		transform.mapPoints(pts2);
		allPoints.add(new Point((int) pts2[0], (int) pts2[1]));

		float[] pts3 = new float[2];

		// Initialize the array with our Coordinate
		pts3[0] = deleteBound.right;
		pts3[1] = deleteBound.bottom;
		transform.mapPoints(pts3);
		allPoints.add(new Point((int) pts3[0], (int) pts3[1]));

		float[] pts4 = new float[2];

		// Initialize the array with our Coordinate
		pts4[0] = deleteBound.right;
		pts4[1] = deleteBound.top;
		transform.mapPoints(pts4);
		allPoints.add(new Point((int) pts4[0], (int) pts4[1]));
		return new Polygon(allPoints);
	}

	public void refresh() {
		// Polygon polygon = getPolygon();

		float scaledImageCenterX = (getBound().width()) / 2;
		float scaledImageCenterY = (getBound().height()) / 2;
		getMatrix().reset();
		getMatrix().postScale(getScale(), getScale());
		getMatrix().postRotate(getAngle(), scaledImageCenterX, scaledImageCenterY);

		getMatrix().postTranslate(getX() - scaledImageCenterX,
				getY() - scaledImageCenterY);
	}


	@Override
	public void recycle() {
		super.recycle();
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}

	}


	public JSONObject toJsonObject() {
		JSONObject object = new JSONObject();
		try {
			object.put("type", getClass().getSimpleName());
			object.put("centerX", getX());
			object.put("centerY", getY());
			object.put("angle", this.getAngle());
			object.put("scalefactor", this.getScale());


		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}


	public void initJson(String json) {
		JSONObject jobj;
		try {
			jobj = new JSONObject(json);
			setX(jobj.getInt("centerX"));
			setY(jobj.getInt("centerY"));
			this.setAngle(jobj.getInt("angle"));
			this.setScale((float) jobj.getDouble("scalefactor"));
			String jSticker = jobj.getString("sticker");

		} catch (JSONException e) {

			e.printStackTrace();
		}

	}

}
