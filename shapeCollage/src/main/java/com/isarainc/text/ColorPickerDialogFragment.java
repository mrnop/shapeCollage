package com.isarainc.text;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.isarainc.shapecollage.R;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;


public class ColorPickerDialogFragment extends DialogFragment implements OnColorChangedListener  {

	private OnColorChangedListener onColorChangedListener;
	private ColorPicker picker;
	private SVBar svBar;
	private OpacityBar opacityBar;
	private Button ok;
	private int color;
	private int oldColor;
	private Button cancel;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		// request a window without the title
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	public OnColorChangedListener getOnColorChangedListener() {
		return onColorChangedListener;
	}

	public void setOnColorChangedListener(
			OnColorChangedListener onColorChangedListener) {
		this.onColorChangedListener = onColorChangedListener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.f_colorpicker, container);
		oldColor= getArguments().getInt("oldColor");
		color=oldColor;
		
		picker = (ColorPicker) view.findViewById(R.id.picker);

		svBar = (SVBar) view.findViewById(R.id.svbar);
		opacityBar = (OpacityBar) view.findViewById(R.id.opacitybar);
		
		
		picker.addSVBar(svBar);
		picker.addOpacityBar(opacityBar);
		picker.setColor(color);
		if(onColorChangedListener!=null)
		picker.setOnColorChangedListener(this);
		ok = (Button) view.findViewById(R.id.ok);

		ok.setBackgroundColor(color);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(onColorChangedListener!=null){
					onColorChangedListener.onColorChanged(color);
				}
				ColorPickerDialogFragment.this.dismiss();
				
			}
		});
		cancel = (Button) view.findViewById(R.id.cancel);

		cancel.setBackgroundColor(oldColor);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(onColorChangedListener!=null){
					onColorChangedListener.onColorChanged(oldColor);
				}
				ColorPickerDialogFragment.this.dismiss();
				
			}
		});
		return view;
	}

	@Override
	public void onColorChanged(int color) {
		this.color=color;
		ok.setBackgroundColor(color);
	}

}
