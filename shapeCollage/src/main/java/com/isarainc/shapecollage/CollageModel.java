package com.isarainc.shapecollage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.isarainc.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CollageModel {
    private static final String TAG = "CollageModel";
    private Paint paint;
    private int plotX;
    private int plotY;
    private int angle = 0;
    private Bitmap srcBitmap;
    private Bitmap scaleBitmap;
    private Bitmap work;

    private int imageSize;
    private boolean square;
    private int borderSize = 4;
    private int borderColor = Color.WHITE;

    private List<Point> points = new ArrayList<Point>();
    private List<Point> intersect = new ArrayList<Point>();


    private static Random rnd = new Random();

    public CollageModel() {
        angle = 0;// -30 + rnd.nextInt(60);
        paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setAntiAlias(true);
    }

    public CollageModel(List<Point> points) {
        super();
        this.points = points;
        Point centroid = centroid(points);
        plotX = centroid.x;
        plotY = centroid.y;
        angle = 0;// -30 + rnd.nextInt(60);

    }

    public Bitmap getScaleBitmap() {
        return scaleBitmap;
    }

    public void setScaleBitmap(Bitmap scaleBitmap) {
        this.scaleBitmap = scaleBitmap;
    }

    public int getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public boolean isSquare() {
        return square;
    }

    public void setSquare(boolean square) {
        this.square = square;
    }


    public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;

    }


    public Bitmap getSrcBitmap() {
        return srcBitmap;
    }

    public void setSrcBitmap(Bitmap bitmap) {
        this.srcBitmap = bitmap;
    }

    public int getPlotX() {
        return plotX;
    }

    public int getPlotY() {
        return plotY;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
        Point centroid = centroid(points);
        plotX = centroid.x;
        plotY = centroid.y;
        angle = 0;// -30 + rnd.nextInt(60);
    }

    public List<Point> getIntersect() {
        return intersect;
    }

    public void setIntersect(List<Point> intersect) {
        this.intersect = intersect;
        if (intersect.size() == 2) {
            angle = (int) calcAngle(intersect.get(0), intersect.get(1));
        } else {
            angle = -45 + rnd.nextInt(90);
        }

    }

    public static double calcAngle(Point p1, Point p2) {
        double xDiff = p2.x - p1.x;
        double yDiff = p2.y - p1.y;
        return Math.toDegrees(Math.atan2(yDiff, xDiff));
    }

    public static Point centroid(List<Point> knots) {
        int centroidX = 0;
        int centroidY = 0;

        for (Point knot : knots) {
            centroidX += knot.x;
            centroidY += knot.y;
        }
        if (knots.size() > 0) {
            return new Point(centroidX / knots.size(), centroidY / knots.size());
        } else {
            return null;
        }
    }

    public Bitmap createBitmapWithFrame() {
        return createBitmapWithFrame(imageSize);
    }

    public Bitmap createBitmapWithFrame(int imSize) {
        // Keep Original ratio
        Bitmap bmWithFrame = null;
        try {
            work.recycle();
            work = null;
        } catch (Exception e) {

        }
        if (!isSquare()) {


            if (srcBitmap.getWidth() > srcBitmap.getHeight()) {
                bmWithFrame = Bitmap
                        .createBitmap((srcBitmap.getWidth()
                                        * imSize / srcBitmap.getHeight()), imSize,
                                Bitmap.Config.ARGB_8888);

                work = Utils
                        .scaleBitmap(
                                srcBitmap,
                                (srcBitmap.getWidth() * imSize / srcBitmap
                                        .getHeight()) - borderSize, imSize - borderSize);

                Canvas canvas = new Canvas(bmWithFrame);
                canvas.drawARGB(Color.alpha(borderColor),
                        Color.red(borderColor), Color.green(borderColor),
                        Color.blue(borderColor));
                canvas.drawBitmap(work, borderSize / 2, borderSize / 2,
                        paint);

            } else {
                bmWithFrame = Bitmap
                        .createBitmap(imSize, (srcBitmap.getHeight()
                                        * imSize / srcBitmap.getWidth()),
                                Bitmap.Config.ARGB_8888);

                work = Utils
                        .scaleBitmap(
                                srcBitmap,
                                imSize - borderSize,
                                (srcBitmap.getHeight() * imSize / srcBitmap
                                        .getWidth()) - borderSize);

                Canvas canvas = new Canvas(bmWithFrame);
                canvas.drawARGB(Color.alpha(borderColor),
                        Color.red(borderColor), Color.green(borderColor),
                        Color.blue(borderColor));
                canvas.drawBitmap(work, borderSize / 2, borderSize / 2,
                        paint);
            }
        } else {
            // Square
            bmWithFrame = Bitmap.createBitmap(imSize, imSize,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmWithFrame);
            canvas.drawARGB(Color.alpha(borderColor),
                    Color.red(borderColor), Color.green(borderColor),
                    Color.blue(borderColor));

            work = Bitmap.createBitmap(imSize - borderSize,
                    imSize - borderSize, Bitmap.Config.ARGB_8888);
            Canvas canvas2 = new Canvas(work);
            Bitmap bm = null;
            if (srcBitmap.getWidth() > srcBitmap.getHeight()) {
                bm = Utils.scaleBitmap(srcBitmap, (imSize - borderSize)
                                * srcBitmap.getWidth() / srcBitmap.getHeight(),
                        (imSize - borderSize));
                // Log.d(TAG, "offset=" + bm.getWidth() / 2 * (-1));
                canvas2.drawBitmap(
                        bm,
                        Math.abs(bm.getWidth() - imSize - borderSize)
                                / 2 * (-1), 0, paint);
                try {
                    bm.recycle();
                    bm = null;
                } catch (Exception e) {

                }
            } else {
                bm = Utils.scaleBitmap(srcBitmap,
                        (imSize - borderSize),
                        (imSize - borderSize) * srcBitmap.getHeight()
                                / srcBitmap.getWidth());
                canvas2.drawBitmap(
                        bm,
                        0,
                        Math.abs(bm.getHeight() - imSize
                                - borderSize)
                                / 2 * (-1), paint);
                try {
                    bm.recycle();
                    bm = null;
                } catch (Exception e) {

                }
            }

            canvas.drawBitmap(work, borderSize / 2, borderSize / 2,
                    paint);
        }
        try {
            work.recycle();
            work = null;
        } catch (Exception e) {

        }
        return bmWithFrame;
    }

    public void processBitmap() {
        if (srcBitmap != null) {
            try {
                scaleBitmap.recycle();
                scaleBitmap = null;
            } catch (Exception e) {
                //e.printStackTrace();
            }
            scaleBitmap = createBitmapWithFrame();
        }

    }

    @Override
    public String toString() {
        return "CollageModel [centerX=" + plotX + ", centerY=" + plotY + ", angle="
                + angle + ", points=" + points + "]";
    }

    public void recycledShape() {
        try {
            work.recycle();
            work = null;
        } catch (Exception e) {

        }
        try {
            scaleBitmap.recycle();
            scaleBitmap = null;
        } catch (Exception e) {

        }

    }

    public void recycled() {
        try {
            work.recycle();
            work = null;
        } catch (Exception e) {

        }
        try {
            scaleBitmap.recycle();
            scaleBitmap = null;
        } catch (Exception e) {

        }
        try {
            srcBitmap.recycle();
            srcBitmap = null;
        } catch (Exception e) {

        }
    }
}
