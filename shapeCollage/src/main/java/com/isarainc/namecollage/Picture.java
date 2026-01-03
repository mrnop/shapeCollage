package com.isarainc.namecollage;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Picture {
	public static final int EDGE_NONE=0;
	public static final int EDGE_LEFT=1;
	public static final int EDGE_RIGHT=2;
	private int centerX;
	private int centerY;
	private int angle=0;
	private List<Point> points=new ArrayList<Point>();
	private int edge=EDGE_NONE;
	private Random rnd = new Random();
	
	
	public Picture() {
		angle=-30 + rnd.nextInt(60);

	}
	
	public Picture(List<Point> points) {
		super();
		this.points = points;
		Point centroid = centroid(points);
		centerX=centroid.x;
		centerY=centroid.y;
		angle=-30 + rnd.nextInt(60);
	}


	public int getCenterX() {
		return centerX;
	}
	
	public int getCenterY() {
		return centerY;
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
		centerX=centroid.x;
		centerY=centroid.y;
		angle=-30 + rnd.nextInt(60);
	}
	
	
	public int getEdge() {
		return edge;
	}
	public void setEdge(int edge) {
		this.edge = edge;
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
	
	
	
	@Override
	public String toString() {
		return "CollageModel [centerX=" + centerX + ", centerY=" + centerY
				+ ", angle=" + angle + ", points=" + points + ", edge=" + edge
				+ "]";
	}
	
}
