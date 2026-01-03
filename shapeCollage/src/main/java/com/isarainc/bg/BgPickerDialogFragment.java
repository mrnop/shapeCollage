package com.isarainc.bg;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;


import com.isarainc.shapecollage.R;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import java.util.List;

public class BgPickerDialogFragment extends DialogFragment {

	private OnBgSelectListener onBgSelectListener;
	private ColorPicker bgPicker;
	private SVBar bgSvBar;
	private OpacityBar bgOpacityBar;
	private int bgColor;
	private int oldBgColor;
	private Button bgOk1;
	private Button bgCancel1;
	private GridView gridView;
	private BgManager bgManager;
	private List<String> bgs;
	private BgAdapter adapter;

	public interface OnBgSelectListener {
		void onBgPicked(Bitmap bitmap, String name);
		void onColorChanged(int bgColor);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		bgManager = BgManager.getInstance(getActivity());
		// request a window without the title
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	public OnBgSelectListener getOnBgSelectListener() {
		return onBgSelectListener;
	}

	public void setOnBgSelectListener(
			OnBgSelectListener onBgSelectListener) {
		this.onBgSelectListener = onBgSelectListener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.f_bgpicker, container);
		bgs = bgManager.listBgs();
		gridView = (GridView) view
				.findViewById(R.id.gridView);
		int margin = getResources().getDimensionPixelSize(R.dimen.margin);

		gridView.setNumColumns(3);
		gridView.setPadding(margin, 0, margin, 0);

		adapter = new BgAdapter(getActivity(), bgs);
		gridView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// clearAllResources();
				if (onBgSelectListener != null) {

					Bitmap bitmap = bgManager.loadBgImage(bgs
							.get(position));
					onBgSelectListener.onBgPicked(bitmap,
							bgs.get(position));
				}
				dismiss();
			}

		});
		oldBgColor = getArguments().getInt("oldColor");
		bgColor = oldBgColor;
		

		bgPicker = (ColorPicker) view.findViewById(R.id.bgPicker);

		


		bgSvBar = (SVBar) view.findViewById(R.id.bgSvbar);
		bgOpacityBar = (OpacityBar) view.findViewById(R.id.bgOpacitybar);

	

		bgPicker.addSVBar(bgSvBar);
		bgPicker.addOpacityBar(bgOpacityBar);


		bgOk1 = (Button) view.findViewById(R.id.bgOk1);

	
	
		bgOk1.setBackgroundColor(bgColor);
		bgOk1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onBgSelectListener != null) {
					onBgSelectListener.onColorChanged(bgColor);
				}
				BgPickerDialogFragment.this.dismiss();

			}
		});
		

		

	
		bgCancel1 = (Button) view.findViewById(R.id.bgCancel1);

		bgCancel1.setBackgroundColor(oldBgColor);
		bgCancel1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onBgSelectListener != null) {
					onBgSelectListener.onColorChanged(oldBgColor);
				}
				BgPickerDialogFragment.this.dismiss();

			}
		});
		
		bgPicker.setColor(bgColor);
		
		bgPicker.setOnColorChangedListener(new OnColorChangedListener() {

			@Override
			public void onColorChanged(int color) {
				bgColor = color;
				bgOk1.setBackgroundColor(color);
	

			}

		});
		
		
		TabHost tabHost = (TabHost) view.findViewById(R.id.tabHost);

		tabHost.setup();
		TabSpec spec0 = tabHost.newTabSpec("Background");
		spec0.setContent(R.id.tab0);
		spec0.setIndicator(getActivity().getResources().getString(R.string.background));

		TabSpec spec1 = tabHost.newTabSpec("Color");
		spec1.setContent(R.id.tab1);
		spec1.setIndicator(getActivity().getResources().getString(R.string.background_color));

		tabHost.addTab(spec0);
		tabHost.addTab(spec1);
		
		return view;
	}

}
