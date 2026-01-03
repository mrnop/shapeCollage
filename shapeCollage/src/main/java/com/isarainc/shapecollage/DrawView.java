package com.isarainc.shapecollage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewParent;
import android.widget.Toast;

import com.isarainc.dialog.ProcessListener;
import com.isarainc.layers.Layer;
import com.isarainc.layers.OnLayerChangeListener;
import com.isarainc.layers.OnLayerDeleteClick;
import com.isarainc.multitouch.gesturedetectors.MoveGestureDetector;
import com.isarainc.multitouch.gesturedetectors.RotateGestureDetector;
import com.isarainc.multitouch.gesturedetectors.ShoveGestureDetector;
import com.isarainc.shapecollage.shape.ShapeManager;
import com.isarainc.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DrawView extends View {
    public static final float STROKE_WIDTH = 5f;
    private static final String TAG = "DrawView";
    private static int CANVAS_WIDTH;
    private static int CANVAS_HEIGHT;
    private final ShapeManager shapeManager;
    public float mAlpha;
    int offsetX = 0;
    int offsetY = 0;
    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    private List<CollageModel> pictures = new ArrayList<CollageModel>();
    private List<PictureLayer> layers = new LinkedList<PictureLayer>();
    private PictureLayer activeLayer;
    private PictureLayer bkLayer;

    private int gridSize = 13;
    private int collageSize = 180;
    private int borderSize = 4;
    private int borderColor = Color.WHITE;
    private int backgroundColor = Color.WHITE;
    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private Bitmap shape;
    private Bitmap orgBg;
    private Bitmap bg;
    private Bitmap bgBitmap;

    private boolean square = true;
    private boolean grid = false;

    private Paint paint;

    // private GestureDetector gestures;
    private Rect rectImage;
    private Rect rectCanvas;
    private int imageWidth = 720;
    private int imageHeight = 720;
    // private int activeLayer = 0;

    private Canvas bgCanvas;

    // private SharedPreferences sharePrefs;
    private Paint bgPaint;
    private boolean processing;

    private int mFocusX;
    private int mFocusY;
    private float mScaleFactor;
    private float mRotationDegrees;
    private OnLayerChangeListener onLayerChangeListener;
    private OnLayerDeleteClick onLayerDeleteClick;
    private RotateGestureDetector mRotateDetector;
    private MoveGestureDetector mMoveDetector;
    private ShoveGestureDetector mShoveDetector;
    private ScaleGestureDetector mScaleDetector;
    private ViewParent mParent;
    private boolean isNew = true;
    private ProcessListener processListener;


    public DrawView(Context context) {
        super(context);
        shapeManager = ShapeManager.getInstance(context);
        bgBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
                Bitmap.Config.ARGB_8888);
        bgCanvas = new Canvas(bgBitmap);
        if (!isInEditMode()) {
            initView(context);
        }
    }

    public DrawView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        shapeManager = ShapeManager.getInstance(context);
        bgBitmap = Bitmap.createBitmap(imageWidth, imageHeight,
                Bitmap.Config.ARGB_8888);
        bgCanvas = new Canvas(bgBitmap);
        if (!isInEditMode()) {
            // TextLayout.init(context);
            // textLayout = TextLayout.randomLayout();
            initView(context);
        }
    }

    public void setOnLayerChangeListener(
            OnLayerChangeListener onLayerChangeListener) {
        this.onLayerChangeListener = onLayerChangeListener;
    }

    public void setProcessListener(ProcessListener processListener) {
        this.processListener = processListener;
    }

    public OnLayerDeleteClick getOnLayerDeleteClick() {
        return onLayerDeleteClick;
    }

    public void setOnLayerDeleteClick(OnLayerDeleteClick onLayerDeleteClick) {
        this.onLayerDeleteClick = onLayerDeleteClick;
    }


    public void recycle() {
        Log.d(TAG,"recycle");
        for(CollageModel pic :pictures){
            pic.recycledShape();
        }
        while(!bitmaps.isEmpty()){

            Bitmap bm=bitmaps.remove(0);
            bm.recycle();
            bm=null;
        }
        try {

            if (shape != null && !shape.isRecycled()) {
                shape.recycle();
                shape = null;
            }
        } catch (Exception e) {

        }
        try {
            if (bgBitmap != null && !bgBitmap.isRecycled()) {
                bgBitmap.recycle();
                bgBitmap = null;
            }
        } catch (Exception e) {

        }
        try {

            bg.recycle();
            bg = null;

        } catch (Exception e) {

        }
        try {

            orgBg.recycle();
            orgBg = null;

        } catch (Exception e) {

        }


    }

    public boolean isSquare() {
        return square;
    }

    public void setSquare(boolean square) {
        this.square = square;
    }

    public boolean isGrid() {
        return grid;
    }

    public void setGrid(boolean grid) {
        this.grid = grid;
    }

    public void initView(Context context) {

        setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mRotateDetector = new RotateGestureDetector(context,
                new RotateListener());
        mMoveDetector = new MoveGestureDetector(context, new MoveListener());
        mShoveDetector = new ShoveGestureDetector(context, new ShoveListener());

        setFocusable(true);
        setFocusableInTouchMode(true);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);

        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
        paint.setDither(true);

        // txtPaint.setColor(Color.BLACK);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setFilterBitmap(true);
        bgPaint.setAntiAlias(true);
        bgPaint.setDither(true);
        rectCanvas = new Rect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        rectImage = new Rect(0, 0, imageWidth, imageHeight);
    }

    public void clearPictures() {
        try {
            while(!bitmaps.isEmpty()){

                Bitmap bm=bitmaps.remove(0);
                bm.recycle();
                bm =null;

            }
            if (bitmaps != null) {
                bitmaps.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addPicture(Bitmap bm) {
        this.bitmaps.add(bm);
    }

    public List<Bitmap> getPictures() {
        return bitmaps;
    }

    public void processShape() {
        Log.d(TAG,"gridSize=" +gridSize + ":collageSize=" + collageSize);
        if (processing) {
            return;
        }
        processing = true;
        for (int i = pictures.size() - 1; i >= 0; i--) {
            CollageModel pic = pictures.get(i);
            pic.recycledShape();
        }
        pictures.clear();
        if (shape == null) {
            return;
        }
        final int w = shape.getWidth();
        final int h = shape.getHeight();
        // Log.d(TAG, "shape size=" + w + "x" + h + " grid size=" + gridSize);
        if (processListener != null) {
            processListener.processing();
        }

        Thread thread = new Thread() {
            public void run() {

                for (int y = 0; y < h; y += gridSize) {

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

                        Point p1 = null;
                        Point p2 = null;
                        Point p3 = null;
                        Point p4 = null;
                        if (found) {
                            List<Point> intersect = new ArrayList<Point>();
                            // x axis 1 fix X as x, y vary

                            int color1 = Color.alpha(shape.getPixel(x, y));
                            for (int y1 = 0; y1 < gridSize; y1++) {
                                if (y + y1 < shape.getHeight()) {
                                    if (Color.alpha(shape.getPixel(x, y + y1)) != color1) {
                                        p1= new Point(x, y + y1);
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
                                            p2= new Point(x + gridSize - 1, y
                                                    + y1);
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
                                        p3=new Point(x + x1, y);
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
                                            p4=new Point(x + x1, y
                                                    + gridSize - 1);
                                            break;
                                        }
                                    }
                                }
                            }
                            if(p1!=null && p2 !=null && p3 !=null && p4 !=null) {
                                intersect.add(p1);
                                intersect.add(p2);
                            }else if(p1!=null && p2 !=null && p3 !=null) {
                                intersect.add(p1);
                                intersect.add(p2);
                            }else if(p1!=null && p2 !=null && p4 !=null) {
                                intersect.add(p1);
                                intersect.add(p4);
                            }else if(p1!=null && p3 !=null && p4 !=null) {
                                intersect.add(p3);
                                intersect.add(p4);
                            }else if(p2!=null && p3 !=null && p4 !=null) {
                                intersect.add(p3);
                                intersect.add(p4);
                            }else if(p1!=null && p4 !=null){
                                intersect.add(p1);
                                intersect.add(p4);
                            }else if(p2 !=null && p3 !=null){
                                intersect.add(p3);
                                intersect.add(p2);
                            }else if(p1!=null && p3 !=null){
                                intersect.add(p1);
                                intersect.add(p3);
                            }else if(p2 !=null && p4 !=null){
                                intersect.add(p4);
                                intersect.add(p2);
                            }else if(p3 !=null && p4 !=null){
                                if(p3.x < p4.x){
                                    intersect.add(p3);
                                    intersect.add(p4);
                                }else{
                                    intersect.add(p4);
                                    intersect.add(p3);
                                }
                            }
                            CollageModel pic = new CollageModel(knots);
                            pic.setIntersect(intersect);
                            pictures.add(pic);

                        }
                    }

                }
                combineImage();
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
        processing = false;
    }

    private synchronized void combineImage() {
        Log.d(TAG,"gridSize=" +gridSize + ":collageSize=" + collageSize);
        if (bitmaps == null || bitmaps.isEmpty() || pictures == null
                || pictures.isEmpty()) {
            return;
        }
        int idx = 0;
        layers.clear();
        Collections.shuffle(pictures);
        Collections.shuffle(bitmaps);
        for (int i = 0; i < pictures.size(); i++) {
            if (i >= pictures.size() || pictures.size() == 0) {
                continue;
            }
            CollageModel p = pictures.get(i);
            if (idx >= bitmaps.size()) {
                idx = 0;
                Collections.shuffle(bitmaps);
            }

            if (idx >= bitmaps.size() || bitmaps.size() == 0) {
                continue;
            }
            Bitmap bm = bitmaps.get(idx);
            idx++;
            if (bm != null) {
                p.setSrcBitmap(bm);
                p.setImageSize(collageSize);
                p.setSquare(isSquare());
                p.setBorderColor(borderColor);
                p.setBorderSize(borderSize);
                try {
                    p.processBitmap();
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    System.gc();
                    ((Activity) getContext()).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getContext(),
                                    "Not enough memory to process",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (Throwable e) {
                    e.printStackTrace();
                    ((Activity) getContext()).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getContext(),
                                    "Error during process some image",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

                // (int) (picture.getPlotX() * ((float) imageWidth / 200.0) -
                // collageSize / 2)

                if (isGrid()) {
                    p.setAngle(0);
                }
                if(p.getScaleBitmap()!=null) {
                    PictureLayer l = new PictureLayer();
                    l.setImageWidth(imageWidth);
                    l.setPicture(p);
                    l.init(getContext());
                    addLayer(l);
                }
            }

        }

    }

    public void reShape() {
        if (processListener != null) {
            processListener.processing();
        }

        Thread thread = new Thread() {
            public void run() {
                combineImage();
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

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        if (gridSize < 4) {
            gridSize = 4;
        }
        this.gridSize = gridSize;

    }

    public int getCollageSize() {
        return collageSize;
    }

    public void setCollageSize(int collageSize) {
        if (collageSize < 10) {
            collageSize = 10;
        }
        this.collageSize = collageSize;
    }

    public Bitmap getShape() {
        return shape;
    }

    public void setShape(Bitmap shape) {
        this.shape = shape;

    }

    public int getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(int borderSize) {
        if (borderSize < 1) {
            borderSize = 1;
        }
        this.borderSize = borderSize;

    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
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

    public void onDraw(Canvas canvas) {
        // Log.d(TAG, "save point=" + savePoints.toString() +
        // backupPoints.toString());
        if (!isInEditMode()) {

            bgCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            if (bg != null) {
                bgCanvas.drawBitmap(bg, 0, 0, paint);

            } else {
                bgCanvas.drawColor(backgroundColor);
            }

            if (!layers.isEmpty()) {
                for (int i = layers.size() - 1; i >= 0; i--) {
                    if (i >= layers.size() || layers.size() == 0) {
                        continue;
                    }
                    Layer layer = layers.get(i);
                    // Log.d(TAG, "layer=" + layer);
                    if(layer!=null) {
                        if (layer == activeLayer) {
                            layer.drawActive(bgCanvas);
                        } else {
                            layer.draw(bgCanvas);
                        }
                    }
                }
            }

            canvas.drawBitmap(bgBitmap, rectImage, rectCanvas, bgPaint);

        }

    }

    public void refresh() {
        setDrawingCacheEnabled(false); // clear the cache here
        invalidate();
    }

    public void redraw(){
        bgCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if (bg != null) {
            bgCanvas.drawBitmap(bg, 0, 0, paint);

        } else {
            bgCanvas.drawColor(backgroundColor);
        }

        if (!layers.isEmpty()) {
            for (int i = layers.size() - 1; i >= 0; i--) {
                if (i >= layers.size() || layers.size() == 0) {
                    continue;
                }
                Layer layer = layers.get(i);
                // Log.d(TAG, "layer=" + layer);
                if(layer!=null) {
                    layer.draw(bgCanvas);
                }
            }
        }

    }
    public Bitmap getSaveBitmap(){
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
        } catch (final FileNotFoundException e) {
            ((Activity) getContext()).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getContext(), e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
            e.printStackTrace();
            return null;
        } catch (final IOException e) {
            ((Activity) getContext()).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getContext(), e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
            return null;
        }
        return file.getAbsolutePath();

    }

    private File getSaveFile(String filename) {
        // String filename = UUID.randomUUID().toString();
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
            } catch (final IOException e) {
                e.printStackTrace();
                ((Activity) getContext()).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getContext(), e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
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

    public void moveToTop() {
        if (activeLayer != null) {
            layers.remove(activeLayer);
            layers.add(0, activeLayer);
            invalidate();
        }

    }

    public void delete() {
        if (bkLayer != null) {
            layers.remove(bkLayer);
            bkLayer = null;
            invalidate();
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
        int newX = imageWidth * x / CANVAS_WIDTH;
        int newY = imageHeight * y / CANVAS_HEIGHT;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (activeLayer != null && activeLayer.isInDeleteBound(newX, newY)) {
                //Let caller delete this layer
                //  removeLayer(activeLayer);
                if (onLayerDeleteClick != null) {
                    onLayerDeleteClick.onLayerDeleteClick(activeLayer);
                }
            } else {
                boolean hit = false;
                synchronized (layers) {
                    for (int i = 0; i < layers.size(); i++) {
                        PictureLayer layer = layers.get(i);
                        if (layer.isInBound(newX, newY)) {
                            // Log.d(TAG, "Change layer to " + layer);
                            setActiveLayer(layer, i);
                            break;
                        } else {
                            this.setActiveLayer(null, 0);
                        }

                    }
                }
            }

        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            this.setActiveLayer(null, 0);

        }

        mScaleDetector.onTouchEvent(event);
        mRotateDetector.onTouchEvent(event);
        mMoveDetector.onTouchEvent(event);
        mShoveDetector.onTouchEvent(event);

        return true; // indicate event was handled

    }

    public List<PictureLayer> getLayers() {
        return layers;
    }

    public void addLayer(PictureLayer layer) {
        layers.add(layer);
        // setActiveLayer(layer, layers.size() - 1);
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

    public void setActiveLayer(final PictureLayer activeLayer, final int index) {
        this.activeLayer = activeLayer;
        if (activeLayer != null) {
            bkLayer = activeLayer;
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
            // Log.d(TAG, "onRotate=" + mRotationDegrees);
            return true;
        }
    }

    private class MoveListener extends
            MoveGestureDetector.SimpleOnMoveGestureListener {
        @Override
        public void onMoveEnd(MoveGestureDetector detector) {
            if (activeLayer != null) {

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

                activeLayer.setX(mFocusX);
                activeLayer.setY(mFocusY);
                activeLayer.setScale(mScaleFactor);
                activeLayer.setAngle(mRotationDegrees);
                activeLayer.refresh();

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
}
