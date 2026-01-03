package com.isarainc.shapecollage.shape;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.isarainc.crop.Crop;
import com.isarainc.fonts.Font;
import com.isarainc.fonts.FontAdapter;
import com.isarainc.fonts.FontManager;
import com.isarainc.main.MyShapeAdapter;
import com.isarainc.shapecollage.HomeActivity;
import com.isarainc.shapecollage.R;
import com.isarainc.shapecollage.shape.ImageSearchDialog.OnDownloadListener;
import com.isarainc.main.MyShapeAdapter.ShapeDeleteListener;
import com.isarainc.shapecollage.text.TextPreview;
import com.isarainc.util.ImagePicker;
import com.isarainc.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

public class CustomDialog extends DialogFragment {
	public static final int PHOTO_PICKED = 0;
	private static final int PHOTO_CROPPED = 2;
	private static final int TAKE_PICTURE = 1;
	//public static final String TEMP_DOWNLOAD_FILE_NAME = "temp_download.png";
	public static final String TEMP_PHOTO_FILE_NAME = "temp_shape.png";
	private static final String TAG = "CustomDialogFragment";
	private OnShapeListener onShapePickListener;

	private List<ShapeInfo> customs;
	private ShapeManager shapeManager;
	private FontManager fontManager;
	private List<Font> fonts;
	private Spinner spinnerFont;
	private TextPreview textPreview;

	private EditText textEdit;
	private Button textOk;
	private SharedPreferences sharePrefs;
	private GridView gridViewCustom;
	private MyShapeAdapter adapterCustom;
	private ImageButton photoTakeImage;
	private ImageButton pickImage;
	private String state;
	private File mFileTemp;
	private Bitmap image;
	private ImageView imageView;
	private SeekBar radius1Size;
	private SeekBar radius2Size;
	private SeekBar photoToleranceSize;
	protected float radius2 = 8.0f;
	protected float radius1 = 0.5f;
	protected int tolerance = 40;
	private Button photoOk;
	private Button photoCancel;
	private ImageButton searchImage;
	private Button textCancel;
	private ToggleButton toggleMode;
	protected boolean shapeMode = false;
	private boolean useDefault;
	protected Font currentFont;
	private Paint paint;

	public OnShapeListener getOnShapePickListener() {
		return onShapePickListener;
	}

	public void setOnShapePickListener(OnShapeListener onTemplatePickListener) {
		this.onShapePickListener = onTemplatePickListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		sharePrefs = getActivity().getSharedPreferences("shapecollage", 0);
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		shapeManager = ShapeManager.getInstance(getActivity());
		// request a window without the title
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.f_custom, container, false);

		int margin = getResources().getDimensionPixelSize(R.dimen.margin);
		// My
		customs = shapeManager.listShapes("custom");

		gridViewCustom = (GridView) root
				.findViewById(R.id.gridView);
		paint = new Paint();
		paint.setFilterBitmap(true);
		paint.setDither(true);
		paint.setAntiAlias(true);
		if (Utils.isTablet(getActivity())) {
			gridViewCustom.setNumColumns(4);
		} else {
			gridViewCustom.setNumColumns(3);
		}
		gridViewCustom.setPadding(margin, 0, margin, 0);

		adapterCustom = new MyShapeAdapter(getActivity(), customs);
		adapterCustom.setShapeDeleteListener(new ShapeDeleteListener() {

			@Override
			public void onDeleted(ShapeInfo info) {
				customs.clear();
				List<ShapeInfo> newShapes = shapeManager.listShapes("custom");
				for (ShapeInfo si : newShapes) {
					customs.add(si);
				}
				adapterCustom.notifyDataSetChanged();
			}

		});
		gridViewCustom.setAdapter(adapterCustom);
		adapterCustom.notifyDataSetChanged();
		gridViewCustom.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// clearAllResources();
				if (onShapePickListener != null) {

					Bitmap bitmap = shapeManager.loadShapeImage(customs
							.get(position));
					onShapePickListener.onShapePicked(bitmap,
							customs.get(position), "");
				}
				dismiss();
			}

		});
		// Custom

		imageView = (ImageView) root
				.findViewById(R.id.photoImageView);


		radius1Size = (SeekBar) root.findViewById(R.id.radius1Size);

		radius1Size.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				progressChanged = progress;

			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				radius1 = progressChanged / 100f;
				if (shapeMode) {
					imageView.setImageBitmap(Sketch.applyAlpha(image,
							Color.WHITE, tolerance));
				} else {
					imageView.setImageBitmap(Sketch.filterDoG(image, radius1,
							radius2, tolerance));
				}
				imageView.invalidate();
			}
		});

		radius2Size = (SeekBar) root.findViewById(R.id.radius2Size);

		radius2Size.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				progressChanged = progress;

			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				radius2 = progressChanged / 100f;
				if (shapeMode) {
					imageView.setImageBitmap(Sketch.applyAlpha(image,
							Color.WHITE, tolerance));
				} else {
					imageView.setImageBitmap(Sketch.filterDoG(image, radius1,
							radius2, tolerance));
				}
				imageView.invalidate();
			}
		});
		photoToleranceSize = (SeekBar) root
				.findViewById(R.id.photoToleranceSize);

		photoToleranceSize
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					int progressChanged = 0;

					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						progressChanged = progress;

					}

					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					public void onStopTrackingTouch(SeekBar seekBar) {
						tolerance = progressChanged;
						if (shapeMode) {
							imageView.setImageBitmap(Sketch.applyAlpha(image,
									Color.WHITE, tolerance));
						} else {
							imageView.setImageBitmap(Sketch.filterDoG(image,
									radius1, radius2, tolerance));
						}
						imageView.invalidate();
					}
				});
		
		state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File dir = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "Android" + File.separator + "data"
					+ File.separator + getActivity().getPackageName()
					+ File.separator + "temp");

			if (!dir.exists()) {
				dir.mkdirs();
			}
			mFileTemp = new File(dir, TEMP_PHOTO_FILE_NAME);
		} else {
			mFileTemp = new File(getActivity().getFilesDir(),
					TEMP_PHOTO_FILE_NAME);
		}
		if (mFileTemp.exists()) {
			loadTempImage();
		} else {
			image = BitmapFactory.decodeResource(this.getResources(),
					R.drawable.che);
			useDefault=true;

		}
		if (image != null) {
			if(useDefault){
				imageView.setImageBitmap(Sketch.filterDoG(image, radius1,
						radius2, tolerance));
			}else{
				if (shapeMode) {
					imageView.setImageBitmap(Sketch.applyAlpha(image,
							Color.WHITE, tolerance));
					radius1Size.setVisibility(View.GONE);
					radius2Size.setVisibility(View.GONE);
				} else {
					imageView.setImageBitmap(Sketch.filterDoG(image, radius1,
							radius2, tolerance));
					radius1Size.setVisibility(View.VISIBLE);
					radius2Size.setVisibility(View.VISIBLE);
				}
			}
		}
		
		photoOk = (Button) root.findViewById(R.id.photoOk);
		photoOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Bitmap bitmap = null;
				if (shapeMode) {
					bitmap=Sketch.applyAlpha(image,
							Color.WHITE, tolerance);
				} else {
					bitmap=Sketch.filterDoG(image, radius1, radius2,
						tolerance);
				}
				Bitmap saveBitmap = null;
				try {

					saveBitmap = Bitmap.createBitmap(200, 200,
							Bitmap.Config.ARGB_8888);
				} catch (OutOfMemoryError oome) {
					System.gc();
					saveBitmap = Bitmap.createBitmap(200, 200,
							Bitmap.Config.RGB_565);
				}
				Canvas saveCanvas = new Canvas(saveBitmap);

				saveCanvas.drawBitmap(Utils.scaleBitmap(bitmap, 150, 150), 25,
						25, paint);
				OutputStream fOut = null;
				File file = getSaveFile();
				try {
					fOut = new FileOutputStream(file);
					saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
					fOut.flush();
					fOut.close();
				} catch (FileNotFoundException e) {
					Toast.makeText(getActivity(), e.getMessage(),
							Toast.LENGTH_LONG).show();
					e.printStackTrace();

				} catch (IOException e) {
					Toast.makeText(getActivity(), e.getMessage(),
							Toast.LENGTH_LONG).show();

				}
				ShapeInfo info = new ShapeInfo();
				info.setInfo(file.getName());
				info.setFolder("custom");
				info.setType(ShapeInfo.TYPE_CUSTOM);
				info.setPath(file.getAbsolutePath());
				shapeManager.addCustomShape(info);
				Bitmap shapeBitmap = shapeManager.loadShapeImage(info);
				if (onShapePickListener != null) {
					onShapePickListener.onShapePicked(shapeBitmap, info,
							"photo");
					sharePrefs
							.edit()
							.putString("lastText",
									textEdit.getText().toString()).apply();
				}
				dismiss();
			}

		});
		photoCancel = (Button) root.findViewById(R.id.photoCancel);
		photoCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dismiss();
			}

		});


		photoTakeImage = (ImageButton) root.findViewById(R.id.photoTakeImage);
		photoTakeImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				takePicture();
			}
		});

		pickImage = (ImageButton) root.findViewById(R.id.photoPickImage);
		pickImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					// Launch picker to choose photo for selected contact
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");

					startActivityForResult(intent, PHOTO_PICKED);
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		searchImage = (ImageButton) root.findViewById(R.id.photoSearchImage);
		searchImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				FragmentManager fm = getActivity()
						.getSupportFragmentManager();
				if (fm.findFragmentByTag("ImageSearchDialog") == null) {
					ImageSearchDialog newFragment = new ImageSearchDialog();
					newFragment.setOnDownloadListener(new OnDownloadListener() {

						@Override
						public void onDownload(Bitmap bitmap) {
							Log.d(TAG, "bitmapToFile=" +mFileTemp);
							
							bitmapToFile(mFileTemp, bitmap);
							Crop.of(Uri.fromFile(mFileTemp), Uri.fromFile(mFileTemp)).start((Activity)getContext(),CustomDialog.this);

						}

					});
					Bundle args = new Bundle();

					newFragment.setArguments(args);
					newFragment.show(fm, "ImageSearchDialog");
				}

			}
		});
		toggleMode = (ToggleButton) root.findViewById(R.id.toggleMode);
		toggleMode.setChecked(true);
		toggleMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shapeMode = !shapeMode;
				if (shapeMode) {
					imageView.setImageBitmap(Sketch.applyAlpha(image,
							Color.WHITE, tolerance));
					radius1Size.setVisibility(View.GONE);
					radius2Size.setVisibility(View.GONE);
				} else {
					imageView.setImageBitmap(Sketch.filterDoG(image, radius1,
							radius2, tolerance));
					radius1Size.setVisibility(View.VISIBLE);
					radius2Size.setVisibility(View.VISIBLE);
				}
				imageView.invalidate();
			}
		});
		
//		toggleMode = (ImageButton) root.findViewById(R.id.toggleMode);
//		toggleMode.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				shapeMode = !shapeMode;
//				if (shapeMode) {
//					imageView.setImageBitmap(Sketch.applyAlpha(image,
//							Color.WHITE, tolerance));
//					radius1Size.setVisibility(View.GONE);
//					radius2Size.setVisibility(View.GONE);
//				} else {
//					imageView.setImageBitmap(Sketch.filterDoG(image, radius1,
//							radius2, tolerance));
//					radius1Size.setVisibility(View.VISIBLE);
//					radius2Size.setVisibility(View.VISIBLE);
//				}
//				imageView.invalidate();
//			}
//		});

		// Text
		textPreview = (TextPreview) root.findViewById(R.id.drawView);

		fontManager = FontManager.getInstance(getActivity());
		fonts = fontManager.getAllActives();

		spinnerFont = (Spinner) root.findViewById(R.id.spinnerFont);
		spinnerFont.setAdapter(new FontAdapter(getActivity(), fonts));
		spinnerFont.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				currentFont=fonts.get(position);
				textPreview.setText(currentFont.patch(textEdit.getText().toString()));
				textPreview.setTypeface(fontManager.loadFont(
						currentFont.getName()).getTypeface());
				sharePrefs.edit()
						.putString("lastFont",currentFont.getName())
						.apply();
				textPreview.invalidate();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		textEdit = (EditText) root.findViewById(R.id.text);
		textEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if(currentFont!=null){
					textPreview.setText(currentFont.patch(textEdit.getText().toString()));
				}else{
					textPreview.setText(textEdit.getText().toString());
				}
				textPreview.invalidate();
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

		textOk = (Button) root.findViewById(R.id.textOk);
		textOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (onShapePickListener != null) {
					String filename = textPreview.saveToFile();
					String text = "";
					if (textEdit.getText() != null) {
						text = textEdit.getText().toString();
					}
					textEdit.getText();
					ShapeInfo info = new ShapeInfo();
					if (filename != null) {
						info.setInfo(filename.substring(filename
								.lastIndexOf("/")));
						info.setFolder("custom");
						info.setType(ShapeInfo.TYPE_CUSTOM);
						info.setPath(filename);
						shapeManager.addCustomShape(info);
						Bitmap bitmap = shapeManager.loadShapeImage(info);
						onShapePickListener.onShapePicked(bitmap, info, text);
						sharePrefs
								.edit()
								.putString("lastText",
										textEdit.getText().toString()).apply();
					}
				}
				dismiss();

			}

		});
		textCancel = (Button) root.findViewById(R.id.textCancel);
		textCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dismiss();
			}

		});

		String lastFont = sharePrefs.getString("lastFont", "SmarnSpecial.ttf");
		String lastText = sharePrefs.getString("lastText", "Love");
		textEdit.setText(lastText);
		int fontPosition = 0;

		for (int i = 0; i < fonts.size(); i++) {
			String f = fonts.get(i).getName();

			if (f.equals(lastFont)) {
				fontPosition = i;
				break;
			}
		}
		spinnerFont.setSelection(fontPosition);
		textPreview.setText(lastText);
		currentFont=fontManager.loadFont(lastFont).getFont();
		textPreview.setTypeface(fontManager.loadFont(lastFont).getTypeface());
		textPreview.invalidate();
		TabHost tabHost = (TabHost) root.findViewById(R.id.tabHost);

		tabHost.setup();

		TabSpec spec0 = tabHost.newTabSpec("My");
		spec0.setContent(R.id.tab0);
		spec0.setIndicator("My");

		TabSpec spec1 = tabHost.newTabSpec("Text");
		spec1.setContent(R.id.tab1);
		spec1.setIndicator("Text");

		TabSpec spec2 = tabHost.newTabSpec("Custom");
		spec2.setContent(R.id.tab2);
		spec2.setIndicator("Custom");

		if (!shapeManager.listShapes("custom").isEmpty()) {
			tabHost.addTab(spec0);
		}

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);

		return root;
	}

	public String bitmapToFile(File file, Bitmap orgin) {

		OutputStream fOut = null;
		try {
			fOut = new FileOutputStream(file);
			if (file.getName().endsWith(".png")) {
				orgin.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			} else {
				orgin.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			}
			fOut.flush();
			fOut.close();
			

		} catch (FileNotFoundException e) {

			e.printStackTrace();
			return null;
		} catch (IOException e) {

			return null;
		}
		return file.getAbsolutePath();

	}

	public String saveToFile(Bitmap bgBitmap, String filename) {
		int width = bgBitmap.getWidth();
		int height = bgBitmap.getHeight();

		int top = Integer.MAX_VALUE;
		int left = Integer.MAX_VALUE;
		int right = Integer.MIN_VALUE;
		int bottom = Integer.MIN_VALUE;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// a[i] = Color.alpha(bitmap.getPixel(i, j));
				// Log.d(TAG, "alpha= " +Color.alpha(bitmap.getPixel(i, j)));
				if (Color.alpha(bgBitmap.getPixel(i, j)) > 0) {
					if (j < top) {
						top = j;
					}
					if (j > bottom) {
						bottom = j;
					}
					if (i < left) {
						left = i;
					}
					if (i > right) {
						right = i;
					}
				}

			}

		}

		Bitmap cropBitmap = Bitmap.createBitmap((right - left), (bottom - top),
				Bitmap.Config.ARGB_8888);
		Canvas cropCanvas = new Canvas(cropBitmap);
		cropCanvas.drawBitmap(bgBitmap, ((-1) * left), ((-1) * top), paint);
		int w = 150;
		int h = 150;
		if (cropBitmap.getWidth() > cropBitmap.getHeight()) {
			w = 150;
			h = 150 * cropBitmap.getHeight() / cropBitmap.getWidth();
		} else {
			h = 150;
			w = 150 * cropBitmap.getWidth() / cropBitmap.getHeight();
		}
		Bitmap scaleBm = Utils.scaleBitmap(cropBitmap, w, h);
		Bitmap saveBitmap = null;
		try {
			saveBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
		} catch (OutOfMemoryError oome) {

		}
		Canvas saveCanvas = new Canvas(saveBitmap);

		saveCanvas.drawBitmap(scaleBm, (200 - scaleBm.getWidth()) / 2,
				(200 - scaleBm.getHeight()) / 2, paint);
		OutputStream fOut = null;
		File file = getSaveFile(filename);
		try {
			fOut = new FileOutputStream(file);
			saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG)
					.show();
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG)
					.show();
			return null;
		}
		return file.getAbsolutePath();

	}

	private File getSaveFile() {
		String filename = UUID.randomUUID().toString();
		return getSaveFile(filename);
	}

	private File getSaveFile(String filename) {
		if (Utils.isSDCARDMounted()) {
			File dir = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "Android" + File.separator + "data"
					+ File.separator + getActivity().getPackageName()
					+ File.separator + "shapes" + File.separator + "custom");

			if (!dir.exists()) {
				dir.mkdirs();
			}
			File f = new File(dir, filename + ".png");
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG)
						.show();
			}
			return f;
		} else {
			File dir = new File(Environment.getDataDirectory() + File.separator
					+ "Android" + File.separator + "data" + File.separator
					+ this.getActivity().getPackageName() + File.separator+ "shapes" + File.separator
					+ "custom");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File f = new File(dir, filename + ".png");
			try {
				f.createNewFile();
			} catch (IOException e) {

				e.printStackTrace();
				Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG)
						.show();
			}
			return f;
		}
	}

	private void takePicture() {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File dir = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "Android" + File.separator + "data"
					+ File.separator + getActivity().getPackageName()
					+ File.separator + "temp");

			if (!dir.exists()) {
				dir.mkdirs();
			}
			mFileTemp = new File(dir, TEMP_PHOTO_FILE_NAME);
		} else {
			mFileTemp = new File(getActivity().getFilesDir(),
					TEMP_PHOTO_FILE_NAME);

		}
		try {
			Uri mImageCaptureUri = null;
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				mImageCaptureUri = Uri.fromFile(mFileTemp);
			} else {
				/*
				 * The solution is taken from here:
				 * http://stackoverflow.com/questions
				 * /10042695/how-to-get-camera-result-as-a-uri-in-data-folder
				 */
				mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
			}
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					mImageCaptureUri);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, TAKE_PICTURE);
		} catch (ActivityNotFoundException e) {

			Log.d(TAG, "cannot take picture", e);
		}
	}

	public static void copyStream(InputStream input, OutputStream output)
			throws IOException {

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case PHOTO_PICKED:
			if (resultCode == Activity.RESULT_OK) {
				try {
					Uri uri = ImagePicker.getImageUriFromResult(getContext(), resultCode, data);
					Crop.of(uri, Uri.fromFile(mFileTemp)).start((Activity) getContext(),CustomDialog.this);

				} catch (Exception e) {

					Log.e(TAG, "Error while creating temp file", e);
				}
			}
			break;

		case TAKE_PICTURE:
			if (resultCode == Activity.RESULT_OK) {
				Crop.of(Uri.fromFile(mFileTemp), Uri.fromFile(mFileTemp)).start((Activity)getContext(),CustomDialog.this);
			}
			break;
		case PHOTO_CROPPED:

			if (resultCode == Activity.RESULT_OK) {

				if (data == null) {
					Log.d(TAG, "Null data, but RESULT_OK, from image picker!");
					return;
				}

				final Bundle extras = data.getExtras();
				// Log.d(TAG, "extras=" +extras);
				if (extras != null) {

					int w = 150;
					int h = 150;
					
					Bitmap cropBitmap  = HomeActivity.decodeSampledBitmapFromResource(mFileTemp.getPath(),150,150); 
					

					Log.d(TAG, "cropBitmap=" +cropBitmap.getWidth() +","+ cropBitmap.getHeight());
					if (cropBitmap.getWidth() > cropBitmap.getHeight()) {
						w = 150;
						h = 150 * cropBitmap.getHeight()
								/ cropBitmap.getWidth();
					} else {
						h = 150;
						w = 150 * cropBitmap.getWidth()
								/ cropBitmap.getHeight();
					}
					Log.d(TAG, "scaleBm=" +w +","+ h);
					Bitmap scaleBm = Utils.scaleBitmap(cropBitmap, w, h);
					Bitmap saveBitmap=Bitmap.createBitmap(150, 150,  Bitmap.Config.ARGB_8888);
					Canvas canvas = new Canvas(saveBitmap);
					
					canvas.drawBitmap(scaleBm,(150-w)/2,(150-h)/2,paint);
					Log.d(TAG, "saveBitmap=" +saveBitmap.getWidth() +","+ saveBitmap.getHeight());
					try {
						FileOutputStream fOut = new FileOutputStream(mFileTemp);
						saveBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
						fOut.flush();
						fOut.close();
					} catch (IOException e) {

						e.printStackTrace();
					}

					loadTempImage();

					// imageView.setImageBitmap(Sketch.applyAlpha(src,
					// colorToReplace, resultCode));
					if (shapeMode) {
						imageView.setImageBitmap(Sketch.applyAlpha(image,
								Color.WHITE, tolerance));
						radius1Size.setVisibility(View.GONE);
						radius2Size.setVisibility(View.GONE);
					} else {
						imageView.setImageBitmap(Sketch.filterDoG(image, radius1,
								radius2, tolerance));
						radius1Size.setVisibility(View.VISIBLE);
						radius2Size.setVisibility(View.VISIBLE);
					}
					imageView.invalidate();

				}
			}

			break;
		}
	}

	private void loadTempImage() {
		image=HomeActivity.decodeSampledBitmapFromResource(mFileTemp.getPath(),150,150); 

	}



}
