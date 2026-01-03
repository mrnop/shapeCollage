package com.isarainc.shapecollage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import com.isarainc.layers.Layer;
import com.isarainc.layers.Polygon;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PictureLayer extends Layer {
	private CollageModel picture;
	private Bitmap bitmap;
	private int imageWidth;


	public PictureLayer() {
		super();

	}

	@Override
	public Rect getBound() {
		return new Rect(0, 0, (int) (bitmap.getWidth() * getScale()), (int) (bitmap.getHeight() * getScale()));
	}

	public CollageModel getPicture() {
		return picture;
	}

	public void setPicture(CollageModel picture) {
		this.picture = picture;
		setScale(1.0f);
		setX((int) (picture.getPlotX() * ((float) imageWidth / 200.0)));
		setY((int) (picture.getPlotY() * ((float) imageWidth / 200.0)));
		setAngle(picture.getAngle());
		setBitmap(picture.getScaleBitmap());
		
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	
	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}


	@Override
	public void draw(Canvas canvas) {
		if(bitmap!=null) {
			if (getScale() > 1) {
				int imSize = (int) (picture.getImageSize() * getScale());
				setScale(1);
				picture.setImageSize(imSize);
				bitmap = picture.getScaleBitmap();
				refresh();
				canvas.drawBitmap(bitmap, getMatrix(),null);
			} else {
				refresh();
				canvas.drawBitmap(bitmap, getMatrix(),null);
			}
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
//			object.put("sticker", this.getInfo().toJsonObject());

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
//			StickerInfo info = StickerInfo.fromJson(jSticker);
			
		} catch (JSONException e) {

			e.printStackTrace();
		}

	}

}
