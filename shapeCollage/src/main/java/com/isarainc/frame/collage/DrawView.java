package com.isarainc.frame.collage;

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
import com.isarainc.layers.Layer;
import com.isarainc.layers.OnLayerChangeListener;
import com.isarainc.layers.TextLayer;
import com.isarainc.multitouch.gesturedetectors.MoveGestureDetector;
import com.isarainc.multitouch.gesturedetectors.RotateGestureDetector;
import com.isarainc.multitouch.gesturedetectors.ShoveGestureDetector;
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
    public static final float STROKE_WIDTH = 5f;
    public static final int MODE_SHAPE = 0;
    public static final int MODE_WORD = 1;
    private static final String TAG = "DrawView";
    private static int CANVAS_WIDTH;
    private static int CANVAS_HEIGHT;
    final ThreadLocal<Matrix> matrix;
    private final Random rnd = new Random();
    int offsetX = 0;
    int offsetY = 0;
    // Remember some things for zooming
    final ThreadLocal<PointF> start;
    final ThreadLocal<PointF> mid;
    private List<Frame> frames = new ArrayList<Frame>();
    private List<Layer> layers = new LinkedList<Layer>();
    private Layer activeLayer;
    private Bitmap orgBg;
    private Bitmap bg;
    private int frameSize = 400;
    private String word;
    private int mode = 0;
    private Paint paint;
    private Rect rectImage;
    private Rect rectCanvas;
    private int imageWidth = 720;
    private int imageHeight = 720;
    private float mScaleFactor = 1.0f;
    private float mRotationDegrees = 0.f;
    private float mFocusX = 0.f;
    private float mFocusY = 0.f;
    private int mAlpha = 255;
    private ScaleGestureDetector mScaleDetector;
    private RotateGestureDetector mRotateDetector;
    private MoveGestureDetector mMoveDetector;
    private ShoveGestureDetector mShoveDetector;
    private GestureDetector mGestureDetector;
    private OnLayerChangeListener onLayerChangeListener;
    private OnFrameAdjustListener onFrameAdjustListener;
    // private int activeLayer = 0;
    private Bitmap bgBitmap;
    private Canvas bgCanvas;
    private int bgColor;
    private Paint bgPaint;
    private Layer drawLayer;

    private ViewParent mParent;
    private boolean drawMode;

    private boolean isNew = true;
    private ProcessListener processListener;

    public DrawView(Context context) {
        super(context);
        bgBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
                Bitmap.Config.ARGB_8888);
        bgCanvas = new Canvas(bgBitmap);
        if (!isInEditMode()) {
            initView(context);
        }
        mid = new ThreadLocal<>();
        mid.set(new PointF());
        start = new ThreadLocal<>();
        start.set(new PointF());

        matrix = new ThreadLocal<>();
        matrix.set(new Matrix());
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
        mid = new ThreadLocal<>();
        mid.set(new PointF());
        start = new ThreadLocal<>();
        start.set(new PointF());

        matrix = new ThreadLocal<>();
        matrix.set(new Matrix());
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

    public OnLayerChangeListener getOnLayerChangeListener() {
        return onLayerChangeListener;
    }

    public void setOnLayerChangeListener(
            OnLayerChangeListener onLayerChangeListener) {
        this.onLayerChangeListener = onLayerChangeListener;
    }

    public OnFrameAdjustListener getOnFrameAdjustListener() {
        return onFrameAdjustListener;
    }

    public void setOnFrameAdjustListener(
            OnFrameAdjustListener onFrameAdjustListener) {
        this.onFrameAdjustListener = onFrameAdjustListener;
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

    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
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

    public void initView(Context context) {

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
        bgPaint.setFilterBitmap(true);
        bgPaint.setDither(true);
        bgPaint.setAntiAlias(true);
        rectCanvas = new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        rectImage = new Rect(0, 0, imageWidth, imageHeight);
    }

    public void clearFrames() {
        frames.clear();
    }

    public void addFrame(Frame frame) {
        frames.add(frame);
    }

    private void shuffle() {
        int x = 0;
        int y = 0;
        for (Layer layer : layers) {
            try {
                x = rnd.nextInt((imageWidth - frameSize));
                y = rnd.nextInt((imageHeight - frameSize));

                // Log.d(TAG, "x=" + x + "," + y);

                layer.setAngle(-30 + rnd.nextInt(60));
                layer.setX(frameSize / 2 + x);
                layer.setY(frameSize / 2 + y);
                layer.refresh();
            } catch (Exception e) {

            }

        }
    }


    public void delete() {
        if (activeLayer != null) {
            layers.remove(activeLayer);
            invalidate();
        }
    }

    public void saveTemplate() {

    }

    public void processImage() {
        isNew = false;

        if (frames.isEmpty()) {
            return;
        }


        frameSize = (int) (Math.sqrt((double) imageWidth
                * (double) imageHeight * 0.5 / frames.size() + 2));

        frameSize = (int) (frameSize * 0.7);

        layers.clear();
        if (processListener != null) {
            processListener.processing();
        }

        Thread thread = new Thread() {
            public void run() {

                for (Frame frame : frames) {
                    FrameLayer layer = new FrameLayer();
                    layer.setFrameSize(frameSize);
                    layer.setFrame(frame);
                    //layer.setBitmap(frame.genBitmap(frameSize));
                    int x = rnd.nextInt((imageWidth - frameSize));
                    int y = rnd.nextInt((imageHeight - frameSize));

                    // Log.d(TAG, "x=" + x + "," + y);
                    layer.init(getContext());
                    layer.setAngle(-30 + rnd.nextInt(60));
                    layer.setX(frameSize / 2 + x);
                    layer.setY(frameSize / 2 + y);
                    layer.refresh();
                    layers.add(layer);
                }

                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //shuffle();
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


    }

    public void reGen(Layer layer) {
        if (layer instanceof FrameLayer) {
            FrameLayer frameLayer = (FrameLayer) layer;
            frameLayer.reGen();
            //frameLayer.setBitmap(frameLayer.getFrame().genBitmap(frameLayer.getFrameSize()));
        }
    }

    public int getImageSize() {
        return frameSize;
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

        //if (imageWidth > imageHeight) {
        if (imageWidth / imageHeight > measuredWidth / measuredHeight) {
            int width = measuredWidth;
            int height = (int) (width * imageHeight / imageWidth);

            setMeasuredDimension(width, height);
            CANVAS_WIDTH = width;
            CANVAS_HEIGHT = height;
            if (rectCanvas != null) {
                rectCanvas.right = width;
                rectCanvas.bottom = height;
            }
        } else {
            int height = getMeasuredHeight();
            int width = (int) (height * imageWidth / imageHeight);

            setMeasuredDimension(width, height);
            CANVAS_WIDTH = width;
            CANVAS_HEIGHT = height;
            if (rectCanvas != null) {
                rectCanvas.right = width;
                rectCanvas.bottom = height;
            }
        }
        //} else {

        //}

    }

    public void setImageResolution(int w, int h) {
        imageWidth = w;
        imageHeight = h;
        // textLayout.setResolution(w, h);
        rectImage = new Rect(0, 0, imageWidth, imageHeight);
        // rectCanvas = new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        try {
            bgBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
                    Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError ome) {
            System.gc();
            bgBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
                    Config.RGB_565);
        }
        bgCanvas = new Canvas(bgBitmap);
        // Adjust Background
        if (orgBg != null) {
            try {
                bg = Utils.tileBitmap(new BitmapDrawable(getResources(), orgBg),
                        imageWidth, imageHeight);
            } catch (OutOfMemoryError ome) {
                ome.printStackTrace();
                bg = null;
            }

        }
        requestLayout();
        invalidate();
    }

    public void onDraw(Canvas canvas) {
        // Log.d(TAG, "save point=" + savePoints.toString() +
        // backupPoints.toString());
        if (!isInEditMode()) {

            bgCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            if (bg != null) {

                bgCanvas.drawBitmap(bg, 0, 0, bgPaint);

            } else {
                bgCanvas.drawColor(bgColor);
            }
            if (layers.isEmpty()) {
                return;
            }
            for (int i = layers.size() - 1; i >= 0; i--) {
                Layer layer = layers.get(i);
                // Log.d(TAG, "" + i + "--> onDraw layer=" + layer);
                if (layer == activeLayer) {
                    layer.drawActive(bgCanvas);
                } else {
                    layer.draw(bgCanvas);
                }
            }

            canvas.drawBitmap(bgBitmap, rectImage, rectCanvas, bgPaint);

        }

    }

    void redraw() {
        bgCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if (bg != null) {

            bgCanvas.drawBitmap(bg, 0, 0, null);

        } else {
            bgCanvas.drawColor(bgColor);
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
        // saveCanvas.drawColor(Color.WHITE);

        saveCanvas.drawBitmap(bgBitmap, 0, 0, bgPaint);
        return saveBitmap;
    }

    public String saveToFile(String filename) {
        if (isNew) {
            return null;
        }

        redraw();
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

    public File getSaveFile(String filename) {
        //	String filename = UUID.randomUUID().toString();
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
            synchronized (layers) {
                for (int i = 0; i < layers.size(); i++) {
                    Layer layer = layers.get(i);
                    //Log.d(TAG, "" + i + "--> layer=" + layer);

                    if (layer.isInBound(newX, newY)) {
                        Log.d(TAG, "Change layer to " + layer);
                        setActiveLayer(layer, i);
                        invalidate();
                        hit = true;
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
                if (!hit && !drawMode) {
                    Log.d(TAG, "hit " + hit + ", drawMode==" + drawMode);
                    this.setActiveLayer(null, 0);
                    invalidate();

                }

            }

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            // outTouchCnt++;
            //drawMode = false;
        }
        try {
            mScaleDetector.onTouchEvent(event);
            mRotateDetector.onTouchEvent(event);
            mMoveDetector.onTouchEvent(event);
            mShoveDetector.onTouchEvent(event);
            mGestureDetector.onTouchEvent(event);
        } catch (Exception e) {

        }
        return true; // indicate event was handled

    }

    public List<Layer> getLayers() {
        return layers;
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

    public void removeLayer(Layer layer) {
        layers.remove(layer);
        if (!layers.isEmpty()) {
            setActiveLayer(layers.get(0), 0);
        } else {
            setActiveLayer(null, 0);
        }

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
            Log.d(TAG, "onRotate=" + mRotationDegrees);
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


            }

            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (activeLayer != null && activeLayer instanceof FrameLayer
                    && onFrameAdjustListener != null) {
                onFrameAdjustListener.onAdjusting(
                        ((FrameLayer) activeLayer),
                        ((FrameLayer) activeLayer).getFrame());
            }
            if (activeLayer instanceof TextLayer
                    && ((TextLayer) activeLayer).isAlongPath()) {
                drawMode = false;
            }
            if (drawMode) {
                drawMode = false;
                setActiveLayer(null, 0);
                invalidate();
            }
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
