package com.isarainc.layers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import com.isarainc.text.PathUtil;
import com.isarainc.text.styles.TextStyle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TextLayer extends Layer {
    private static final String TAG = "TextLayer";
    private final Paint dashPaint;
    private String text = "New Text";
    private TextStyle style;
    private List<Point> savePoints = new LinkedList<Point>();
    private int size = 100;
    private int orgSize = size;
    private boolean alongPath = false;
    protected Path path = new Path();
    private Paint paint = new Paint();

    public TextLayer() {
        super();
        dashPaint = new Paint();
        dashPaint.setAntiAlias(true);
        dashPaint.setDither(true);
        dashPaint.setColor(Color.RED);
        //dashPaint.setARGB(255, 0, 0, 0);
        //dashPaint.setAlpha(120);
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setStrokeCap(Paint.Cap.ROUND);
        dashPaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 1));
        dashPaint.setStrokeWidth(4);
    }

    public TextLayer(String text) {
        this();
        this.text = text;
        this.style = TextStyle.random();

    }

    public TextLayer(String text, TextStyle style) {
        this();
        this.text = text;
        this.style = style;

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TextStyle getStyle() {
        return style;
    }

    public void setStyle(TextStyle style) {
        this.style = style;
    }

    public int getSize() {
        return size;
    }

    public boolean isAlongPath() {
        return alongPath;
    }

    public void setAlongPath(boolean alongPath) {
        this.alongPath = alongPath;

        Point point = getPolygon().centroid();
        if (point != null) {
            this.setX(point.x);
            this.setY(point.y);
        }

    }

    public void setSize(int size) {
        this.size = size;
        orgSize = size;
    }

    public List<Point> getSavePoints() {
        return savePoints;
    }

    public void setSavePoints(List<Point> savePoints) {
        this.savePoints = savePoints;
    }

    public Polygon getPolygon() {
        if (alongPath) {
            List<Point> allPoints = new ArrayList<Point>();
            int r = style.getBound().height() / 2;
            for (int i = 0; i < savePoints.size(); i ++) {
                Point p = savePoints.get(i);
                allPoints.add(new Point(p.x - r, p.y - r - r));
                allPoints.add(new Point(p.x + r, p.y - r - r));
            }
            for (int i = savePoints.size() - 1; i >= 0; i --) {
                Point p = savePoints.get(i);
                allPoints.add(new Point(p.x + r, p.y + r));
                allPoints.add(new Point(p.x - r, p.y + r));
            }
            List<Point> points = PathUtil.smoothPoints(allPoints, 100);
            points = PathUtil.smoothPoints(points, 50);
            Polygon drawPoly = new Polygon(points);
            return drawPoly;
        } else {
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);

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
    }

    @Override
    public String toString() {
        return "TextLayer [text=" + text + ", style=" + style + ", savePoints="
                + savePoints + "]";
    }

    @Override
    public Rect getBound() {
        Rect bounds = new Rect();
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(orgSize * super.getScale());
        paint.getTextBounds(getText(), 0, getText().length(), bounds);

        return bounds;
    }

    @Override
    public boolean isInBound(int x, int y) {
        if (this.isAlongPath()) {
            return getPolygon().contains(x, y);
        } else {
            return super.isInBound(x, y);
        }
    }

    public void drawActive(Canvas canvas) {
        Polygon polygon = getPolygon();
        draw(canvas);
        if(!alongPath) {
            Path path = polygon.getPath();
            canvas.drawPath(path, dashPaint);


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
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (alongPath) {
            paint.setTextSize(orgSize * super.getScale());
            paint.setTextAlign(Align.CENTER);
            canvas.save();
            canvas.rotate(getAngle(), getX(),
                    getY() - ((paint.descent() + paint.ascent()) / 2));
            PathUtil.updatePath(path, getSavePoints());
            style.setSize(orgSize * super.getScale());
            style.draw(canvas, path, text);
            canvas.restore();
        } else {
            // paint.setColor(getColor());
            paint.setTextSize(orgSize * super.getScale());
            paint.setTextAlign(Align.CENTER);
            // int xPos = (canvas.getWidth() / 2);
            // int yPos = (int) ((canvas.getHeight() / 2) -
            // ((txtPaint.descent() + txtPaint.ascent()) / 2)) ;
            canvas.save();
            canvas.rotate(getAngle(), getX(),
                    getY() - ((paint.descent() + paint.ascent()) / 2));
            if (style != null) {
                style.setSize(orgSize * super.getScale());
                // style.setFont("TS-Somtum-AItalic-NP.ttf");
                style.draw(canvas, getX(), (int) (getY() - ((paint
                        .descent() + paint.ascent()) / 2)), getText());
            }

            canvas.restore();
        }
    }


    public JSONObject toJsonObject() {
        JSONObject object = new JSONObject();
        try {
            object.put("type", getClass().getSimpleName());
            object.put("centerX", getX());
            object.put("centerY", getY());
            object.put("angle", this.getAngle());
            object.put("alongPath", alongPath);
            object.put("scalefactor", this.getScale());
            object.put("size", orgSize);
            object.put("text", text);
            object.put("style", style.toJsonObject());
            JSONArray jpoints = new JSONArray();
            object.put("points", jpoints);
            for (Point p : savePoints) {
                JSONObject jpoint = new JSONObject();
                jpoint.put("x", p.x);
                jpoint.put("y", p.y);
                jpoints.put(jpoint);
            }

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
            this.setSize(jobj.getInt("size"));
            this.setText(jobj.getString("text"));
            String jStyle = jobj.getString("style");
            TextStyle style = TextStyle.fromJson(jStyle);
            setStyle(style);
            this.setAlongPath(jobj.getBoolean("alongPath"));
            JSONArray jpoints = jobj.getJSONArray("points");
            getSavePoints().clear();
            for (int i = 0; i < jpoints.length(); i++) {
                JSONObject jpoint = jpoints.getJSONObject(i);
                Point point = new Point(jpoint.getInt("x"), jpoint.getInt("y"));
                this.getSavePoints().add(point);
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

    }

}
