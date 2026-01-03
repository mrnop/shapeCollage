package com.isarainc.frame.size;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;


import com.isarainc.shapecollage.R;
import com.isarainc.util.Utils;

import java.util.ArrayList;
import java.util.List;


public class SizePickerDialogFragment extends DialogFragment {

    private OnSizeListener onSizeListener;
    private SizeAdapter adapter;
    private List<Size> sizes = new ArrayList<Size>();
    private GridView gridView;
    //public static String[] SIZE_ARRAY={"square","land4x6","port6x4","fbcover"};


    public interface OnSizeListener {
        void onSizePicked(int index, Size size);
    }


    public OnSizeListener getOnSizeListener() {
        return onSizeListener;
    }

    public void setOnSizeListener(OnSizeListener onSizeListener) {
        this.onSizeListener = onSizeListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        sizes.add(new Size("square", true, 1, 1));
        sizes.add(new Size("land6x4", true, 6, 4));
        sizes.add(new Size("land1024x500", false, 1024, 500));
        sizes.add(new Size("port4x6", true, 4, 6));
        sizes.add(new Size("fbcover", false, 851, 315));
        sizes.add(new Size("custom", false, 720, 720));
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.f_item, container, false);


        gridView = (GridView) root
                .findViewById(R.id.gridView);
        int margin = getResources().getDimensionPixelSize(R.dimen.margin);

        gridView.setNumColumns(3);
        gridView.setPadding(margin, 0, margin, 0);

        adapter = new SizeAdapter(getActivity(), sizes);
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                final Size size = sizes.get(position);


                if (onSizeListener != null) {

                    if ("custom".equals(size.getName())) {
                        //onSizeListener.onSizePicked(position,false, size.getWidth(), size.getHeight());
                        //Popup Dialog Size
                        FragmentManager fm = getChildFragmentManager();
                        if (fm.findFragmentByTag("CustomSizeDialogFragment") == null) {
                            CustomSizeDialogFragment newFragment = new CustomSizeDialogFragment();
                            newFragment.setOnSizeUpdateListener(new CustomSizeDialogFragment.OnSizeUpdateListener() {
                                @Override
                                public void onUpdated(int width, int height) {
                                    size.setWidth(width);
                                    size.setHeight(height);
                                    onSizeListener.onSizePicked(position, size);
                                    dismiss();
                                }
                            });
                            Bundle args = new Bundle();

                            newFragment.setArguments(args);
                            newFragment.show(fm, "CustomSizeDialogFragment");

                        }
                    } else {

                        onSizeListener.onSizePicked(position, size);
                        dismiss();
                    }
                }

            }

        });

        return root;
    }

}
