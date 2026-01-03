package com.isarainc.frame.montage;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.isarainc.shapecollage.R;

import java.io.InputStream;
import java.util.Random;

public class AdjustDialogFragment extends DialogFragment {

	private static final String TAG = "AdjustDialogFragment";
	private Montage pframe;
	private AdjustView adjustView;
	private Button cancel;
	private Bitmap frame;
	private Button ok;
	private OnMontageUpdateListener onFrameUpdateListener;
	private SeekBar memoSizeControl;
	private EditText memo;
	private ImageButton changeStyle;
	private Random rnd=new Random();

	public interface OnMontageUpdateListener {
		public void onUpdated(Montage frame);
	}
	
	public OnMontageUpdateListener getOnFrameUpdateListener() {
		return onFrameUpdateListener;
	}

	public void setOnFrameUpdateListener(
            OnMontageUpdateListener onFrameUpdateListener) {
		this.onFrameUpdateListener = onFrameUpdateListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		// request a window without the title
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		InputStream istr;
		try {
			istr = getActivity().getAssets().open("frame.png");
			frame = BitmapFactory.decodeStream(istr);
			Log.d(TAG, "frame =" +frame.getWidth() + "x" + frame.getHeight());
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.f_motageadjust, container, false);
		adjustView = (AdjustView) root.findViewById(R.id.adjustView);

	

		memoSizeControl = (SeekBar) root.findViewById(R.id.memoSize);
		
		memoSizeControl
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					int progressChanged = 0;

					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						progressChanged = progress;
						adjustView.setCaptionSize(progressChanged);
						adjustView.invalidate();
					}

					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					public void onStopTrackingTouch(SeekBar seekBar) {

					}
				});

		memo = (EditText) root.findViewById(R.id.memo);
	
		memo.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				adjustView.setCaption(s.toString());
				adjustView.invalidate();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

		});
		changeStyle = (ImageButton) root.findViewById(R.id.changeStyle);
		;
		ok = (Button) root.findViewById(R.id.buttonOk);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(pframe !=null){
					pframe.setCaption(adjustView.getCaption());
					pframe.setCaptionSize(adjustView.getCaptionSize());

					if(onFrameUpdateListener !=null){
						onFrameUpdateListener.onUpdated(pframe);
					}
				}
				dismiss();
			}

		});
		cancel = (Button) root.findViewById(R.id.buttonCancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				dismiss();
			}

		});
		
		if(pframe !=null){
			memo.setText(pframe.getCaption());
			memoSizeControl.setProgress(pframe.getCaptionSize());
			adjustView.setCaption(pframe.getCaption());
			
			adjustView.setCaptionSize(pframe.getCaptionSize());
			adjustView.setFrame(frame);

			adjustView.setCenterX(pframe.getX());
			adjustView.setCenterY(pframe.getY());

			adjustView.invalidate();
		}
		return root;
	}

	public Montage getPframe() {
		return pframe;
	}

	public void setPframe(Montage pframe) {
		this.pframe = pframe;
		if(adjustView!=null){
			adjustView.setCaption(pframe.getCaption());
		
			adjustView.setCaptionSize(pframe.getCaptionSize());
			adjustView.setFrame(frame);

			adjustView.setCenterX(pframe.getX());
			adjustView.setCenterY(pframe.getY());

			adjustView.invalidate();
		}
	}

}
