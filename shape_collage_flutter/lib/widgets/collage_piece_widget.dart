import 'package:flutter/material.dart';
import '../models/collage_model.dart';


class CollagePieceWidget extends StatelessWidget {
  final CollageModel model;
  final VoidCallback? onTap;
  final Function(ScaleUpdateDetails)? onScaleUpdate;

  const CollagePieceWidget({
    super.key,
    required this.model,
    this.onTap,
    this.onScaleUpdate,
  });

  @override
  Widget build(BuildContext context) {
    return Positioned(
      left: model.position.dx,
      top: model.position.dy,
      child: GestureDetector(
        onScaleUpdate: onScaleUpdate,
        onTap: onTap,
        child: Transform.rotate(
          angle: model.angle,
          child: Container(
            padding: EdgeInsets.all(model.borderSize),
            decoration: BoxDecoration(
              color: model.borderColor,
              border: Border.all(
                color: model.borderColor,
                width: 0, // Border is handled by padding + color background usually in collage apps to look like a frame
              ),
            ),
            child: SizedBox(
              width: model.size.width,
              height: model.size.height,
              child: model.imageProvider != null
                  ? Image(image: model.imageProvider!, fit: BoxFit.cover)
                  : const Placeholder(),
            ),
          ),
        ),
      ),
    );
  }
}
