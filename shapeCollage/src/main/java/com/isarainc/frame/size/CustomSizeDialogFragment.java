package com.isarainc.frame.size;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.isarainc.shapecollage.R;

public class CustomSizeDialogFragment extends DialogFragment {

	private static final String TAG = "CustomSizeDialogFragment";

	private Button cancel;
	private Bitmap frame;
	private Button ok;
	private OnSizeUpdateListener onSizeUpdateListener;
    private EditText width;
    private EditText height;


    interface OnSizeUpdateListener {
		void onUpdated(int width, int height);
	}

    public OnSizeUpdateListener getOnSizeUpdateListener() {
        return onSizeUpdateListener;
    }

    public void setOnSizeUpdateListener(OnSizeUpdateListener onSizeUpdateListener) {
        this.onSizeUpdateListener = onSizeUpdateListener;
    }

    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		// request a window without the title
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.f_customesize, container, false);


		width = (EditText) root.findViewById(R.id.width);
        height = (EditText) root.findViewById(R.id.height);

		ok = (Button) root.findViewById(R.id.buttonOk);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

					if(onSizeUpdateListener !=null){
                        onSizeUpdateListener.onUpdated(Integer.parseInt(width.getText().toString()),Integer.parseInt(height.getText().toString()));
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

		return root;
	}


}
