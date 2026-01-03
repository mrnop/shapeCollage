package com.isarainc.shapecollage.shape;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.isarainc.shapecollage.R;
import com.isarainc.util.Utils;

import java.util.List;

public class HbdPickerDialog extends DialogFragment {

    private static final String TAG = "HbdPickerDialogFragment";
    private OnShapeListener onShapePickListener;

    private ShapeManager shapeManager;

    protected float radius2 = 8.0f;
    protected float radius1 = 0.5f;
    protected int tolerance = 40;
    private GridView gridView;

    public OnShapeListener getOnShapePickListener() {
        return onShapePickListener;
    }

    public void setOnShapePickListener(OnShapeListener onTemplatePickListener) {
        this.onShapePickListener = onTemplatePickListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        shapeManager = ShapeManager.getInstance(getActivity());
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.f_quickshape, container, false);
        final int margin = getResources().getDimensionPixelSize(R.dimen.margin);
        gridView = (GridView) root
                .findViewById(R.id.gridView);


        if (Utils.isTablet(getActivity())) {
            gridView.setNumColumns(6);
        } else {
            gridView.setNumColumns(4);
        }
        gridView.setPadding(margin, 0, margin, 0);
        final List<ShapeInfo> shapes = shapeManager.listShapes("hbd");
        ShapeAdapter shapeAdapter = new ShapeAdapter(
                getActivity(), shapes);
        gridView.setAdapter(shapeAdapter);
        shapeAdapter.notifyDataSetChanged();

        gridView
                .setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view,
                            int position, long id) {
                        // clearAllResources();
                        if (onShapePickListener != null) {
                            Bitmap bitmap = shapeManager
                                    .loadShapeImage(shapes
                                            .get(position));

                            onShapePickListener.onShapePicked(
                                    bitmap,
                                    shapes.get(position), "");
                        }

                        dismiss();
                    }

                });


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
