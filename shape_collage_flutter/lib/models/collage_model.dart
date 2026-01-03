import 'dart:math';
import 'package:flutter/material.dart';

class CollageModel {
  Offset position; // Replaces plotX, plotY
  double angle;
  ImageProvider? imageProvider; // Replacing Bitmap for now, logic will likely need specific ImageStream handling
  Size size; // imageSize
  bool isSquare;
  double borderSize;
  Color borderColor;
  
  // Points defining the shape if applicable (from List<Point>)
  List<Offset> points;

  CollageModel({
    this.position = Offset.zero,
    this.angle = 0.0,
    this.imageProvider,
    this.size = const Size(100, 100),
    this.isSquare = false,
    this.borderSize = 4.0,
    this.borderColor = Colors.white,
    this.points = const [],
  });

  // Logic to calculate centroid
  static Offset centroid(List<Offset> knots) {
    double centroidX = 0;
    double centroidY = 0;

    for (var knot in knots) {
      centroidX += knot.dx;
      centroidY += knot.dy;
    }
    if (knots.isNotEmpty) {
      return Offset(centroidX / knots.length, centroidY / knots.length);
    } else {
      return Offset.zero;
    }
  }

  // Helper to calculate angle between two points if needed
  static double calcAngle(Offset p1, Offset p2) {
    return atan2(p2.dy - p1.dy, p2.dx - p1.dx);
  }
}
