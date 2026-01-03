package com.isarainc.shapecollage.shape;

import android.graphics.Bitmap;

public interface OnShapeListener {
	void onShapePicked(Bitmap shape, ShapeInfo info, String extra);
}
