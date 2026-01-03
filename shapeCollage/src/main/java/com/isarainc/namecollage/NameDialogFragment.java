package com.isarainc.namecollage;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.isarainc.fonts.Font;
import com.isarainc.fonts.FontAdapter;
import com.isarainc.fonts.FontHolder;
import com.isarainc.fonts.FontManager;
import com.isarainc.shapecollage.R;

import java.util.List;

public class NameDialogFragment extends DialogFragment {

	private EditText text;
	private FontManager fontManager;
	private List<Font> fonts;
	private Typeface typeface;
	private SharedPreferences sharePrefs;

	public interface OnNameUpdateListener{
		public void nameChanged(String name,Typeface typeface);
		public void nameCancel();
		
	}

	private OnNameUpdateListener onNameUpdateListener;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		// request a window without the title
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	public OnNameUpdateListener getOnNameUpdateListener() {
		return onNameUpdateListener;
	}

	public void setOnNameUpdateListener(OnNameUpdateListener onNameUpdateListener) {
		this.onNameUpdateListener = onNameUpdateListener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.f_name, container);
		String name= getArguments().getString("name");


		text = (EditText)view.findViewById(R.id.text);
		text.setText(name);
		fontManager = FontManager.getInstance(getActivity());
		fonts = fontManager.getAllActives();

		//Log.d(TAG, "fonts="+fonts);
		Spinner spinnerFont = (Spinner) view.findViewById(R.id.spinnerFont);
		spinnerFont.setAdapter(new FontAdapter(getActivity(), fonts));
		sharePrefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		String lastFont = sharePrefs.getString("lastFont", "ColabLig.otf");

		int fontPosition = 0;

		for (int i = 0; i < fonts.size(); i++) {
			String f = fonts.get(i).getName();

			if (f.equals(lastFont)) {
				fontPosition = i;
				break;
			}
		}
		FontHolder fontHolder = fontManager.loadFont(lastFont);
		//Log.d(TAG, "font change " +fontHolder);
		if (fontHolder != null) {
			typeface = fontHolder.getTypeface();
		}else{
			typeface = Typeface.DEFAULT;
		}
		text.setTypeface(typeface);
		text.invalidate();
		spinnerFont.setSelection(fontPosition);
		spinnerFont.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				FontHolder fontHolder = fontManager.loadFont(fonts
						.get(position).getName());
				//Log.d(TAG, "font change " +fontHolder);
				if (fontHolder != null) {
					typeface = fontHolder.getTypeface();

					if (typeface != null && text.getText() != null
							&& !"".equals(text.getText().toString().trim())) {
						text.setTypeface(typeface);
						text.invalidate();
						sharePrefs
								.edit()
								.putString("lastFont",
										fonts.get(position).getName()).apply();
					}
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});


		Button ok = (Button) view.findViewById(R.id.ok);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onNameUpdateListener != null) {
					onNameUpdateListener.nameChanged(text.getText().toString(),typeface);
				}
				NameDialogFragment.this.dismiss();

			}
		});
		Button cancel = (Button) view.findViewById(R.id.cancel);

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onNameUpdateListener != null) {
					onNameUpdateListener.nameCancel();
				}
				NameDialogFragment.this.dismiss();

			}
		});
		return view;
	}



}
