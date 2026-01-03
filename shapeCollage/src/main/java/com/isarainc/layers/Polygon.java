package com.isarainc.layers;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.Arrays;
import java.util.List;

public class Polygon {
    // Polygon coodinates.
    private final int[] polyY, polyX;

    // Number of sides in the polygon.
    private final int polySides;
    private Point centroid;
    private Rect bounds;

    public Polygon(Point[] points) {
        polySides = points.length;
        polyY = new int[polySides];
        polyX = new int[polySides];

        for (int i = 0; i < polySides; i++) {
            polyY[i] = points[i].y;
            polyX[i] = points[i].x;
        }
        process();
    }

    public Polygon(List<Point> points) {
        polySides = points.size();
        polyY = new int[polySides];
        polyX = new int[polySides];

        for (int i = 0; i < polySides; i++) {
            polyY[i] = points.get(i).y;
            polyX[i] = points.get(i).x;
        }
        process();
    }

    /**
     * Default constructor.
     *
     * @param px Polygon y coods.
     * @param py Polygon x coods.
     * @param ps Polygon sides count.
     */
    public Polygon(final int[] px, final int[] py, final int ps) {
        polyX = px;
        polyY = py;
        polySides = ps;
        process();
    }
    private void process(){
        //find new width height
        int minX = Integer.MAX_VALUE;
        int maxX = -Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = -Integer.MAX_VALUE;

        if (polyX.length == 0 || polyY.length == 0) {
            return;
        }
        int sumX = 0;
        int sumY = 0;

        for (int i = 0; i < polyX.length; i++) {

            int x = polyX[i];
            int y = polyY[i];
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);

            sumX += polyX[i];
            sumY += polyY[i];
        }
        centroid = new Point(sumX / polyX.length, sumY / polyX.length);

        bounds = new Rect(minX,minY,maxX,maxY);
    }

    public Path getPath() {
        Path path = new Path();
        int endX = 0;
        int endY = 0;
        for (int i = 0; i < polySides; i++) {
            if (i == 0) {
                path.moveTo(polyX[i], polyY[i]);
                endX = polyX[i];
                endY = polyY[i];
            } else {
                path.lineTo(polyX[i], polyY[i]);
            }
        }
        path.lineTo(endX, endY);
        return path;
    }

    /**
     * Checks ifilter the Polygon contains a point.
     *
     * @param x Point horizontal pos.
     * @param y Point vertical pos.
     * @return Point is in Poly flag.
     * @see "http://alienryderflex.com/polygon/"
     */
    public boolean contains(final float x, final float y) {

        boolean oddTransitions = false;
        for (int i = 0, j = polySides - 1; i < polySides; j = i++) {
            if ((polyY[i] < y && polyY[j] >= y) || (polyY[j] < y && polyY[i] >= y)) {
                if (polyX[i] + (y - polyY[i]) / (polyY[j] - polyY[i]) * (polyX[j] - polyX[i]) < x) {
                    oddTransitions = !oddTransitions;
                }
            }
        }
        return oddTransitions;
    }

    public Point centroid() {
       return centroid;

    }


    public Rect getBounds() {
        return bounds;
    }

    @Override
    public String toString() {
        return "Polygon [polyY=" + Arrays.toString(polyY) + ", polyX="
                + Arrays.toString(polyX) + ", polySides=" + polySides + "]";
    }

// Usage Guide
//
//	public boolean isTouched(final float X, final float Y){
//		   final Polygon p = new Polygon(points);
//		      return p.contains(X, Y);
//		}
}
