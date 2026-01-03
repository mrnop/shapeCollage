package com.isarainc.layers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import com.isarainc.stickers.StickerInfo;
import com.isarainc.stickers.StickerManager;

import java.util.ArrayList;
import java.util.List;


public class StickerLayer extends Layer {
    private Bitmap bitmap;
    private StickerInfo info;
    private StickerManager stickerManager;
    private int width;
    private int height;

    public StickerLayer() {
    }


    @Override
    public void init(Context context) {
        super.init(context);
        stickerManager = StickerManager.getInstance(context);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, getMatrix(), null);
    }

    @Override
    public Rect getBound() {
        return new Rect(0, 0, (int) (bitmap.getWidth() * getScale()), (int) (bitmap.getHeight() * getScale()));
    }

    public StickerInfo getInfo() {
        return info;
    }

    public void setInfo(StickerInfo info) {
        this.info = info;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
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
/*
        if (getX() + getBound().width() / 2 + deleteBitmap.getWidth() / 2 < 1024) {
            deleteBound.left = getX() + getBound().width() / 2 - deleteBitmap.getWidth() / 2;
            deleteBound.right = getX() + getBound().width() / 2 + deleteBitmap.getWidth() / 2;
        } else {
            deleteBound.left = getX() - getBound().width() / 2 - deleteBitmap.getWidth() / 2;
            deleteBound.right = getX() - getBound().width() / 2 + deleteBitmap.getWidth() / 2;
        }
        if (getY() - getBound().height() / 2 - deleteBitmap.getHeight() / 2 < 0) {
            deleteBound.top = getY() + getBound().height() / 2 - deleteBitmap.getHeight() / 2;
            deleteBound.bottom = getY() + getBound().height() / 2 + deleteBitmap.getHeight() / 2;
        } else {
            deleteBound.top = getY() - getBound().height() / 2 - deleteBitmap.getHeight() / 2;
            deleteBound.bottom = getY() - getBound().height() / 2 + deleteBitmap.getHeight() / 2;
        }
        Polygon deletePolygon = getDeletePolygon();
        //Path deletePath = deletePolygon.getPath();

        Point point = deletePolygon.centroid();
        canvas.drawBitmap(deleteBitmap, point.x - deleteBitmap.getWidth() / 2, point.y - deleteBitmap.getHeight() / 2, null);
        // canvas.drawPath(deletePath, dashPaint);
        */
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
}
