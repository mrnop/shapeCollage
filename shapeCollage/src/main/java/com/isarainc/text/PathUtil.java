package com.isarainc.text;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Path;
import android.graphics.Point;

import com.isarainc.text.layouts.calculator.Calculator;

public class PathUtil {

	public static List<Point> smoothPoints(List<Point> rawPoints){

		return smoothPoints(rawPoints,25);
	}

	public static List<Point> smoothPoints(List<Point> rawPoints,int threshold){
		List<Point> points=new ArrayList<Point>();
		if(!rawPoints.isEmpty()){
			Point prevPoint = rawPoints.get(0);
			points.add(prevPoint);
			for(Point point:rawPoints){
				if(!point.equals(prevPoint)){
					if(Calculator.distance(point,prevPoint)>threshold){
						points.add(point);
						prevPoint=point;
					}
				}
			}
		}
		return points;
	}
	public static void updatePath(Path path,List<Point> rawPoints) {
		//Smooth point
		List<Point> points=smoothPoints(rawPoints);
		path.reset();

		if (points.size() > 1) {
			Point prevPoint = null;
			for (int i = 0; i < points.size(); i++) {
				Point point = points.get(i);

				if (i == 0) {
					path.moveTo(point.x, point.y);
				} else {
					float midX = (prevPoint.x + point.x) / 2;
					float midY = (prevPoint.y + point.y) / 2;

					if (i == 1) {
						path.lineTo(midX, midY);
					} else {
						path.quadTo(prevPoint.x, prevPoint.y, midX, midY);
					}
				}
				prevPoint = point;
			}
			path.lineTo(prevPoint.x, prevPoint.y);
		}
	}
}
