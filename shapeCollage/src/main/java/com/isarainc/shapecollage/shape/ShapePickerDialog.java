package com.isarainc.shapecollage.shape;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.isarainc.shapecollage.R;
import com.isarainc.util.Utils;

import java.util.List;

public class ShapePickerDialog extends DialogFragment {

    private static final String TAG = "ShapePickerDialogFragment";
    private OnShapeListener onShapePickListener;

    private ShapeManager shapeManager;

    protected float radius2 = 8.0f;
    protected float radius1 = 0.5f;
    protected int tolerance = 40;

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
        View root = inflater.inflate(R.layout.f_shape, container, false);
        final int margin = getResources().getDimensionPixelSize(R.dimen.margin);
        TabHost tabHost = (TabHost) root.findViewById(R.id.tabHost);
        tabHost.setup();

        final List<ShapeInfo> recents = shapeManager.getRecents();
        if (recents != null && !recents.isEmpty()) {
            final TabSpec ourSpec = tabHost.newTabSpec("recent");

            try {
                ourSpec.setIndicator("", ContextCompat.getDrawable(getContext(), R.drawable.ic_recent));

            } catch (Throwable t) {
                t.printStackTrace();
                ourSpec.setIndicator("recent");
            }


            // ourSpec.setContent(intent)
            ourSpec.setContent(new TabHost.TabContentFactory() {
                public View createTabContent(String tag) {
                    LinearLayout content = new LinearLayout(getActivity());
                    content.setOrientation(LinearLayout.VERTICAL);
                    LayoutParams params = new LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT);
                    params.setMargins(0, 160, 0, 0);
                    content.setLayoutParams(params);

                    GridView stickersGridView = new GridView(
                            getActivity());

                    if (Utils.isTablet(getActivity())) {
                        stickersGridView.setNumColumns(6);
                    } else {
                        stickersGridView.setNumColumns(4);
                    }
                    stickersGridView.setPadding(margin, 0, margin, 0);

                    ShapeAdapter shapeAdapter = new ShapeAdapter(
                            getActivity(), recents);
                    stickersGridView.setAdapter(shapeAdapter);

                    shapeAdapter.notifyDataSetChanged();

                    stickersGridView
                            .setOnItemClickListener(new OnItemClickListener() {

                                @Override
                                public void onItemClick(
                                        AdapterView<?> parent, View view,
                                        int position, long id) {
                                    // clearAllResources();
                                    if (onShapePickListener != null) {
                                        Bitmap bitmap = shapeManager
                                                .loadShapeImage(recents
                                                        .get(position));

                                        onShapePickListener.onShapePicked(
                                                bitmap,
                                                recents.get(position), "");
                                    }

                                    dismiss();
                                }

                            });
                    content.addView(stickersGridView);
                    return content;
                }
            });

            tabHost.addTab(ourSpec);

        }
        // Add Bundle Sticker
        List<String> shapeSets = shapeManager.getShapeSets();
        for (int i = 0; i < shapeSets.size(); i++) {

            final String tabName = shapeSets.get(i);

            final List<ShapeInfo> shapes = shapeManager.listShapes(tabName);
            if (shapes != null && !shapes.isEmpty()) {
                final TabSpec ourSpec = tabHost.newTabSpec(tabName);

                try {
                    Bitmap bitmap = shapeManager.loadShapeImage(shapes.get(0));
                    Drawable d = new BitmapDrawable(getResources(), bitmap);
                    ourSpec.setIndicator("", d);

                    // View view = LayoutInflater.from(getActivity()).inflate(
                    // R.layout.indicator_tab, null);
                    // ImageView imv = (ImageView) view.findViewById(R.id.icon);
                    // imv.setImageBitmap(bitmap);
                    // ourSpec.setIndicator(view);
                } catch (Throwable t) {
                    t.printStackTrace();
                    ourSpec.setIndicator(tabName);
                }

                // ourSpec.setContent(intent)
                ourSpec.setContent(new TabHost.TabContentFactory() {
                    public View createTabContent(String tag) {
                        LinearLayout content = new LinearLayout(getActivity());
                        content.setOrientation(LinearLayout.VERTICAL);
                        LayoutParams params = new LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.MATCH_PARENT);
                        params.setMargins(0, 160, 0, 0);
                        content.setLayoutParams(params);

                        GridView stickersGridView = new GridView(
                                getActivity());

                        if (Utils.isTablet(getActivity())) {
                            stickersGridView.setNumColumns(6);
                        } else {
                            stickersGridView.setNumColumns(4);
                        }
                        stickersGridView.setPadding(margin, 0, margin, 0);

                        ShapeAdapter shapeAdapter = new ShapeAdapter(
                                getActivity(), shapes);
                        stickersGridView.setAdapter(shapeAdapter);

                        shapeAdapter.notifyDataSetChanged();

                        stickersGridView
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
                                            shapeManager.update(shapes
                                                    .get(position));
                                            onShapePickListener.onShapePicked(
                                                    bitmap,
                                                    shapes.get(position), "");
                                        }

                                        dismiss();
                                    }

                                });
                        content.addView(stickersGridView);
                        return content;
                    }
                });

                tabHost.addTab(ourSpec);
            }
        }


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
