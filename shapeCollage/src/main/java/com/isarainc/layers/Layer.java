package com.isarainc.layers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import com.isarainc.shapecollage.R;

import java.util.ArrayList;
import java.util.List;

public abstract class Layer {
    protected Paint dashPaint;
    protected int x;
    protected int y;
    protected float angle = 0.0f;
    protected float scale = 1.0f;
    protected Matrix matrix = new Matrix();
    protected Matrix transform = new Matrix();
    protected Context context;
    protected Bitmap deleteBitmap;
    protected Rect deleteBound = new Rect();

    public Layer() {

        dashPaint = new Paint();
        dashPaint.setAntiAlias(true);
        dashPaint.setDither(true);
        dashPaint.setColor(Color.RED);
        // dashPaint.setARGB(255, 0, 0, 0);
        // dashPaint.setAlpha(120);
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setStrokeCap(Paint.Cap.ROUND);
        dashPaint.setPathEffect(new DashPathEffect(new float[] { 5, 5 }, 1));
        dashPaint.setStrokeWidth(4);

    }

    public void init(Context context) {
        this.context = context;
        deleteBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_trash);
        // getMatrix().postTranslate(getX(), getY());
    }

    public void invalidate() {

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public boolean isInBound(int x, int y) {
        Polygon polygon = getPolygon();
        // return x >= left && x < right && y <= bottom && y > top;
        return polygon.contains(x, y);
    }

    public boolean isInDeleteBound(int x, int y) {

        Polygon polygon = getDeletePolygon();
        return polygon.contains(x, y);
    }

    protected Polygon getPolygon() {
        Rect bounds = getBound();
        int left = x;
        int right = x + bounds.width();
        int top = y;
        int bottom = y + bounds.height();
        // Log.d("bounds", x+ ":" + y +" Layer " + this.toString() + "bound="+ bounds +"
        // " +left + "," +top +":" + right+ "," + bottom);

        // Find polygon from rotate angle
        List<Point> allPoints = new ArrayList<Point>();

        transform.reset();
        transform.postScale(scale, scale);
        transform.setRotate(angle, 0, 0);

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
        Polygon polygon = getPolygon();

        float scaledImageCenterX = (getBound().width()) / 2;
        float scaledImageCenterY = (getBound().height()) / 2;
        getMatrix().reset();
        getMatrix().postScale(getScale(), getScale());

        getMatrix().postRotate(getAngle(), scaledImageCenterX, scaledImageCenterY);

        getMatrix().postTranslate(getX(), getY());
    }

    public void drawActive(Canvas canvas) {
        Polygon polygon = getPolygon();
        draw(canvas);
        Path path = polygon.getPath();
        canvas.drawPath(path, dashPaint);

        if (getX() + getBound().width() + deleteBitmap.getWidth() / 2 < 1024) {
            deleteBound.left = getX() + getBound().width() - deleteBitmap.getWidth() / 2;
            deleteBound.right = getX() + getBound().width() + deleteBitmap.getWidth() / 2;
        } else {
            deleteBound.left = getX() - deleteBitmap.getWidth() / 2;
            deleteBound.right = getX() + deleteBitmap.getWidth() / 2;
        }
        if (getY() - getBound().height() - deleteBitmap.getHeight() / 2 < 0) {
            deleteBound.top = getY() + getBound().height() - deleteBitmap.getHeight() / 2;
            deleteBound.bottom = getY() + getBound().height() + deleteBitmap.getHeight() / 2;
        } else {
            deleteBound.top = getY() - deleteBitmap.getHeight() / 2;
            deleteBound.bottom = getY() + deleteBitmap.getHeight() / 2;
        }

        canvas.drawBitmap(deleteBitmap, null, deleteBound, null);

    }

    public abstract void draw(Canvas canvas);

    public abstract Rect getBound();

    public void recycle() {
        if (deleteBitmap != null) {
            deleteBitmap.recycle();
            deleteBitmap = null;
        }
    }

    @Override
    public String toString() {
        return "Layer{" +
                ", x=" + x +
                ", y=" + y +
                ", angle=" + angle +
                ", scale=" + scale +
                '}';
    }
}
