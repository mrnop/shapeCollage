package com.isarainc.namecollage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.isarainc.dialog.ProcessListener;
import com.isarainc.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DrawView extends View {

    private static final String TAG = "DrawView";

    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private List<Bitmap> scaleBitmaps = new ArrayList<Bitmap>();
    Matrix matrix = new Matrix();
    private Bitmap shape;
    private int gridSize = 5;
    private int collageSize = 36;

    private int backgroundColor = Color.WHITE;
    private int mode = 0;
    private Paint paint;
    private Rect rectImage;
    private Rect rectCanvas;

    private static int CANVAS_WIDTH;
    private static int CANVAS_HEIGHT;
    private int imageWidth = 720;
    private int imageHeight = 720 * 2 /3;

    Random rnd = new Random();

    int offsetX = 0;
    int offsetY = 0;

    // private GestureDetector gestures;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    // private int activeLayer = 0;
    private Bitmap bgBitmap;
    private Canvas bgCanvas;

    private Paint bgPaint;


    private List<Picture> pictures = new ArrayList<Picture>();
    private Bitmap bg;
    private Bitmap orgBg;
    private ProcessListener processListener;
    private boolean isNew = true;


    public DrawView(Context context) {
        super(context);
        bgBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
                Bitmap.Config.ARGB_8888);
        bgCanvas = new Canvas(bgBitmap);
        if (!isInEditMode()) {
            initView(context);
        }
    }

    public DrawView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        bgBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
                Bitmap.Config.ARGB_8888);
        bgCanvas = new Canvas(bgBitmap);
        if (!isInEditMode()) {
            // TextLayout.init(context);
            // textLayout = TextLayout.randomLayout();
            initView(context);
        }
    }

    public ProcessListener getProcessListener() {
        return processListener;
    }

    public void setProcessListener(ProcessListener processListener) {
        this.processListener = processListener;
    }

    public void recycle() {
        for (int i = 0; i < bitmaps.size(); i++) {
            Bitmap pic = bitmaps.get(i);
            try {
                if (pic != null) {
                    pic.recycle();
                    pic = null;
                }
            } catch (Exception e) {

            }
        }
        try {
            if (shape != null) {
                shape.recycle();
                shape = null;
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

    }

    public void initView(Context context) {

        setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
        setFocusable(true);
        setFocusableInTouchMode(true);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
        paint.setDither(true);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setFilterBitmap(true);
        bgPaint.setAntiAlias(true);
        bgPaint.setDither(true);
        rectCanvas = new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        rectImage = new Rect(0, 0, imageWidth, imageHeight);
    }

    public void clearPictures() {
        try {
            if (bitmaps != null) {
                for (Bitmap bitmap : bitmaps) {
                    bitmap.recycle();
                }
                bitmaps.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (scaleBitmaps != null) {
                for (Bitmap bitmap : scaleBitmaps) {
                    bitmap.recycle();
                }
                scaleBitmaps.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPicture(Bitmap bm) {
        this.bitmaps.add(bm);
    }

    public void processImage() {

        if (scaleBitmaps != null) {
            for (Bitmap bitmap : scaleBitmaps) {
                bitmap.recycle();
            }
            scaleBitmaps.clear();
        }
        for (Bitmap bitmap : bitmaps) {
            try {
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, collageSize, collageSize,
                        false);
                scaleBitmaps.add(scaleBitmap);
            } catch (Exception e) {

            }
        }

    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        if (gridSize < 2) {
            gridSize = 2;
        }
        this.gridSize = gridSize;

    }

    public int getCollageSize() {
        return collageSize;
    }

    public void setCollageSize(int collageSize) {
        if (collageSize < 8) {
            collageSize = 8;
        }
        this.collageSize = collageSize;
    }

    public Bitmap getShape() {
        return shape;
    }

    public void setShape(Bitmap shape) {
        this.shape = shape;
        pictures.clear();

        processShape();

    }


    public int getBackgroundColor() {
        return backgroundColor;

    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Point centroid(List<Point> knots) {
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

    public static double getAngle(Point p1, Point p2) {
        double xDiff = p2.x - p1.x;
        double yDiff = p2.y - p1.y;
        double degree = Math.toDegrees(Math.atan2(yDiff, xDiff));
        if (degree >= 90) {
            degree = degree - 90;
        } else if (degree <= -90) {
            degree = degree + 90;
        }
        if (degree > 45) {
            degree = degree - 90;
        } else if (degree <= -45) {
            degree = degree + 90;
        }
        return degree;
    }

    public void processShape() {

        pictures.clear();
        if (shape == null) {
            return;
        }
        final int w = shape.getWidth();
        final int h = shape.getHeight();
        Log.d(TAG, "shape size=" + w + "x" + h + " grid size=" + gridSize);
        if (processListener != null) {
            processListener.processing();
        }

        Thread thread = new Thread() {
            public void run() {
                synchronized (pictures) {
                for (int y = 0; y < h; y += gridSize) {
                    List<Picture> xpics = new ArrayList<Picture>();
                    for (int x = 0; x < w; x += gridSize) {
                        boolean found = false;
                        List<Point> knots = new ArrayList<Point>();
                        for (int y1 = 0; y1 < gridSize; y1++) {
                            for (int x1 = 0; x1 < gridSize; x1++) {
                                if (x + x1 < shape.getWidth()
                                        && y + y1 < shape.getHeight()) {
                                    if (Color.alpha(shape.getPixel(x + x1, y + y1)) > 0) {
                                        found = true;
                                        knots.add(new Point(x + x1, y + y1));
                                    }

                                }
                            }
                        }

                        if (found) {
                            List<Point> intersect = new ArrayList<Point>();
                            // x axis 1 fix X as x, y vary

                            int color1 = Color.alpha(shape.getPixel(x, y));
                            for (int y1 = 0; y1 < gridSize; y1++) {
                                if (y + y1 < shape.getHeight()) {
                                    if (Color.alpha(shape.getPixel(x, y + y1)) != color1) {
                                        intersect.add(new Point(x, y + y1));
                                        break;
                                    }
                                }
                            }
                            // x axis 2 fix X as x+gridSize-1, y vary
                            if (x + gridSize - 1 < shape.getWidth()) {
                                int color2 = Color.alpha(shape.getPixel(x + gridSize
                                        - 1, y));
                                for (int y1 = 0; y1 < gridSize; y1++) {
                                    if (y + y1 < shape.getHeight()) {
                                        if (Color.alpha(shape.getPixel(
                                                x + gridSize - 1, y + y1)) != color2) {
                                            intersect.add(new Point(x + gridSize - 1, y
                                                    + y1));
                                            break;
                                        }
                                    }
                                }
                            }
                            // y axis 1 fix y , x vary

                            int color3 = Color.alpha(shape.getPixel(x, y));
                            for (int x1 = 0; x1 < gridSize; x1++) {
                                if (x + x1 < shape.getWidth()) {
                                    if (Color.alpha(shape.getPixel(x + x1, y)) != color3) {
                                        intersect.add(new Point(x + x1, y));
                                        break;
                                    }
                                }
                            }
                            // y axis 2 fix y+grid-1 , x vary
                            if (y + gridSize - 1 < shape.getHeight()) {
                                int color4 = Color.alpha(shape.getPixel(x, y + gridSize
                                        - 1));
                                for (int x1 = 0; x1 < gridSize; x1++) {
                                    if (x + x1 < shape.getWidth()) {
                                        if (Color.alpha(shape.getPixel(x + x1, y
                                                + gridSize - 1)) != color4) {
                                            intersect.add(new Point(x + x1, y
                                                    + gridSize - 1));
                                            break;
                                        }
                                    }
                                }
                            }
                            Picture pic = new Picture(knots);
                            int angle = 0;
                            if (intersect.size() == 2) {
                                angle = (int) getAngle(intersect.get(0), intersect.get(1));
                            } else {
                                angle = -30 + rnd.nextInt(60);
                            }
                            //Log.d(TAG, "angle=" + angle + ",intersect=" + intersect);
                            pic.setAngle(angle);

                            pictures.add(pic);
                            xpics.add(pic);
                        }
                    }
                    }

                }
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        invalidate();
                        if (processListener != null) {
                            processListener.processed();
                        }

                    }
                });

            }
        };
        thread.setDaemon(true);
        thread.start();

        isNew = false;
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

        int width = getMeasuredWidth();
        int height = width * imageHeight / imageWidth;

        // Log.d(TAG, "rectCanvas=" +rectCanvas);
        setMeasuredDimension(width, height);
        CANVAS_WIDTH = width;
        CANVAS_HEIGHT = height;
        if (rectCanvas != null) {
            // rectCanvas = new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
            rectCanvas.right = width;
            rectCanvas.bottom = height;
        }
    }

    public void setImageResolution(int w, int h) {
        imageWidth = w;
        imageHeight = h;
        // textLayout.setResolution(w, h);
        rectImage = new Rect(0, 0, imageWidth, imageHeight);
        rectCanvas = new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        bgBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
                Bitmap.Config.ARGB_8888);
        bgCanvas = new Canvas(bgBitmap);
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

    public Bitmap getBg() {
        return orgBg;
    }

    public void setBg(Bitmap background) {
        this.orgBg = background;
        if (orgBg != null) {
            try {
                bg = Utils.tileBitmap(new BitmapDrawable(getResources(), orgBg),
                        imageWidth, imageHeight);
            } catch (OutOfMemoryError ome) {
                bg = null;
            }
        } else {
            bg = null;
        }

    }

    public void onDraw(Canvas canvas) {
        Log.d(TAG, "bg=" + bg);
        if (!isInEditMode()) {
            Collections.shuffle(scaleBitmaps);
            Collections.shuffle(pictures);
            bgCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            if (bg != null) {
                bgCanvas.drawBitmap(bg, 0, 0, null);

            } else {
                bgCanvas.drawColor(backgroundColor);
            }
            if (scaleBitmaps.isEmpty()) {
                return;
            }
            int idx = 0;
            Collections.shuffle(scaleBitmaps);
            synchronized (pictures) {
                for (Picture p : pictures) {
                    if (idx >= scaleBitmaps.size()) {
                        idx = 0;
                        Collections.shuffle(scaleBitmaps);
                    }
                    // int idx = rnd.nextInt(scaleBitmaps.size());
                    Bitmap bm = scaleBitmaps.get(idx);
                    idx++;
                    matrix.reset();
                    matrix.postRotate(p.getAngle(), collageSize / 2, collageSize / 2);
                    // matrix.postRotate(p.getAngle(), collageSize/2, collageSize/2);
                    matrix.postTranslate(
                            (int) (p.getCenterX() * ((float) imageWidth / shape.getWidth()) - collageSize / 2),
                            (int) (p.getCenterY() * ((float) imageHeight / shape.getHeight()) - collageSize / 2));
                    // Log.d(TAG, "xypos="+ p.getPlotX() + "," + p.getPlotY() +
                    // "," + imageWidth + "," +collageSize);
                    // Log.d(TAG, "xypos="+ (p.getPlotX() *
                    // ((float)imageWidth/200) - collageSize / 2) + "," +
                    // (p.getPlotY() * ((float)imageWidth/200) - collageSize / 2));
                    bgCanvas.drawBitmap(bm, matrix, paint);
                }
            }
            // Log.d(TAG, "rectImage="+rectImage);
            // Log.d(TAG, "rectCanvas="+rectCanvas);
            canvas.drawBitmap(bgBitmap, rectImage, rectCanvas, bgPaint);

        }

    }

    public void refresh() {
        setDrawingCacheEnabled(false); // clear the cache here
        invalidate();
    }

    public String saveToFile(String filename) {
        if (isNew) {
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
        saveCanvas.drawColor(backgroundColor);

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
        //String filename = UUID.randomUUID().toString();
        if (Utils.isSDCARDMounted()) {
            File dir = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Pictures" + File.separator
                    + "shapecollage");
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

}
