package com.isarainc.shapecollage;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

public class BorderSettingDialog extends DialogFragment {

	private OnBorderChangedListener onBorderChangedListener;

	private int borderColor;
	private int borderSize;
	private int oldBorderColor;
	private int oldBorderSize;
	
	private ColorPicker borderPicker;
	private SVBar borderSvBar;
	private OpacityBar borderOpacityBar;
	private Button bgOk1;
	private Button bgCancel1;

	private SeekBar size;




	public interface OnBorderChangedListener {
		void onBorderChanged(int bgColor, int size);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		// request a window without the title
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}



	public OnBorderChangedListener getOnBorderChangedListener() {
		return onBorderChangedListener;
	}



	public void setOnBorderChangedListener(
			OnBorderChangedListener onBorderChangedListener) {
		this.onBorderChangedListener = onBorderChangedListener;
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.f_bordersetting, container);
		
		oldBorderColor = getArguments().getInt("oldBorderColor");
		oldBorderSize = getArguments().getInt("oldBorderSize");
		borderColor = oldBorderColor;
		borderSize = oldBorderSize;
		final FrameLayout frameOk1 = (FrameLayout) view.findViewById(R.id.frameOk1);
		FrameLayout frameCancel1 = (FrameLayout) view.findViewById(R.id.frameCancel1);
		borderPicker = (ColorPicker) view.findViewById(R.id.borderPicker);

		borderSvBar = (SVBar) view.findViewById(R.id.borderSvbar);
		borderOpacityBar = (OpacityBar) view
				.findViewById(R.id.borderOpacitybar);

		

		borderPicker.addSVBar(borderSvBar);
		borderPicker.addOpacityBar(borderOpacityBar);
		final TextView sizeVal = (TextView) view.findViewById(R.id.sizeValue);
		sizeVal.setText(getString(R.string.textsize) + " " + borderSize);
		size = (SeekBar) view.findViewById(R.id.size);
		size.setProgress(oldBorderSize);

		size.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 1;

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				progressChanged = progress;

			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				borderSize=progressChanged;
				sizeVal.setText(getString(R.string.textsize) + "=" + borderSize);

			}
		});
		
		bgOk1 = (Button) view.findViewById(R.id.bgOk1);


		frameOk1.setBackgroundColor(borderColor);
		bgOk1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onBorderChangedListener != null) {
					onBorderChangedListener.onBorderChanged(borderColor, borderSize);
				}
				BorderSettingDialog.this.dismiss();

			}
		});
		

		
		
		
		bgCancel1 = (Button) view.findViewById(R.id.bgCancel1);

		frameOk1.setBackgroundColor(oldBorderColor);
		bgCancel1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onBorderChangedListener != null) {
					onBorderChangedListener.onBorderChanged(oldBorderColor,
							oldBorderSize);
				}
				BorderSettingDialog.this.dismiss();

			}
		});
		
		borderPicker.setColor(borderColor);
		
		borderPicker.setOnColorChangedListener(new OnColorChangedListener() {

			@Override
			public void onColorChanged(int color) {
				borderColor = color;
				frameOk1.setBackgroundColor(color);

			}

		});
		
		
		return view;
	}

}
