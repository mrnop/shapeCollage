import 'dart:ui' as ui;
import 'dart:async';
import 'package:flutter/services.dart';
// For Offset

class ShapeProcessor {
  final int gridSize;

  ShapeProcessor({this.gridSize = 13});

  Future<List<Offset>> processShape(String assetPath) async {
    final ByteData data = await rootBundle.load(assetPath);
    final ui.Codec codec = await ui.instantiateImageCodec(data.buffer.asUint8List());
    final ui.FrameInfo fi = await codec.getNextFrame();
    final ui.Image image = fi.image;

    final int width = image.width;
    final int height = image.height;
    final ByteData? byteData = await image.toByteData(format: ui.ImageByteFormat.rawRgba);

    if (byteData == null) return [];

    final List<Offset> points = [];
    final Uint8List pixels = byteData.buffer.asUint8List();

    // Scan pixels
    // RGBA format: 4 bytes per pixel
    for (int y = 0; y < height; y += gridSize) {
      for (int x = 0; x < width; x += gridSize) {
        final int index = (y * width + x) * 4;
        
        if (index + 3 < pixels.length) {
          final int alpha = pixels[index + 3];
          if (alpha > 0) {
            points.add(Offset(x.toDouble(), y.toDouble()));
          }
        }
      }
    }
    
    // Center the points? 
    // Usually we might want to normalize them relative to center
    // But for now, returning absolute coordinates is fine if we match the canvas size.

    return points;
  }
}
