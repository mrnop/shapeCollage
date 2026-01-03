package com.isarainc.shapecollage.shape;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.isarainc.shapecollage.R;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class ImageSearchDialog extends DialogFragment {

	private static final String TAG = "ImageSeach";
	private WebView webView;
	protected long enqueue;
	private ImageButton downloadBtn;
	protected String imageUrl;

	private OnDownloadListener onDownloadListener;
	private SharedPreferences sharePrefs;
	private Target target;
	private MaterialDialog mProgressDialog;

	public interface OnDownloadListener {
		void onDownload(Bitmap bitmap);
	}

	public OnDownloadListener getOnDownloadListener() {
		return onDownloadListener;
	}

	public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
		this.onDownloadListener = onDownloadListener;
	}

	class MyJavaScriptInterface {
		public void showHTML(String html) {
			// process the html as needed by the app
			Log.d("html", html);
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		sharePrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		// request a window without the title
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	@SuppressLint("JavascriptInterface")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.f_search, container, false);
		final Context context = getActivity();
		downloadBtn = (ImageButton) root.findViewById(R.id.downloadButton);
		downloadBtn.setImageDrawable(new IconicsDrawable(getContext())
				.icon(GoogleMaterial.Icon.gmd_save)
				.color(Color.BLACK)
				.sizeDp(24));
		downloadBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (imageUrl != null) {
					// Download
					if (mProgressDialog != null) {
						mProgressDialog.dismiss();
					}
					mProgressDialog = new MaterialDialog.Builder(getContext())
							.title(R.string.downloading_image)
							.content(R.string.wait)
							.progress(true, 0).build();

					mProgressDialog.show();

					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							target = new Target() {
								@Override
								public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
									if (onDownloadListener != null) {
										onDownloadListener
												.onDownload(bitmap);
									}
									mProgressDialog.dismiss();
									dismiss();
								}

								@Override
								public void onBitmapFailed(Exception e, Drawable errorDrawable) {
									mProgressDialog.dismiss();
									dismiss();
								}

								@Override
								public void onPrepareLoad(Drawable placeHolderDrawable) {

								}
							};

							Picasso.get().load(imageUrl).into(target);

						}

					}, 0L);

				} else {
					Toast.makeText(getActivity(), R.string.no_image, Toast.LENGTH_LONG).show();
				}
			}
		});
		webView = (WebView) root.findViewById(R.id.webView);
		webView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient() {

			public void onPageFinished(WebView view, String url) {
				webView.loadUrl(
						"javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
			}

			// you tell the webclient you want to catch when a url is about to
			// load
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// if(!url.contains("www.google.com.ua/")){
				// Log.d("shouldOverrideUrlLoading", url);
				// view.loadUrl(url);
				// return true;
				// }else{
				// Log.d("shouldOverrideUrlLoading", url);
				return false;
				// }

			}

			// here you get all links to pictures
			@Override
			public void onLoadResource(WebView view, String url) {

				Log.d("web link", url);
				if (url.toLowerCase().endsWith(".jpg")
						|| url.toLowerCase().endsWith(".png")) {
					imageUrl = url;
					downloadBtn.setVisibility(View.VISIBLE);
				} else if (url.contains("/imgrc?")) {
					imageUrl = null;
					downloadBtn.setVisibility(View.INVISIBLE);

					if (mProgressDialog != null) {
						mProgressDialog.dismiss();
					}
					mProgressDialog = new MaterialDialog.Builder(getContext())
							.title(R.string.downloading_image)
							.content(R.string.wait)
							.progress(true, 0).build();

					mProgressDialog.show();

					Map<String, String> params = extractQueryString(url);

					if (params.get("imgurl") != null) {
						try {
							imageUrl = URLDecoder.decode(params.get("imgurl"), "UTF-8");
							downloadBtn.setVisibility(View.VISIBLE);
							new Handler().postDelayed(new Runnable() {

								@Override
								public void run() {
									target = new Target() {
										@Override
										public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

											if (onDownloadListener != null) {
												onDownloadListener
														.onDownload(bitmap);
											}
											if (isAdded()) {

												getActivity().runOnUiThread(new Runnable() {
													@Override
													public void run() {
														try {
															mProgressDialog.dismiss();
														} catch (Throwable t) {

														}
													}// public void run() {
												});
											}

											dismiss();
										}

										@Override
										public void onBitmapFailed(Exception e, Drawable errorDrawable) {

											mProgressDialog.dismiss();
											dismiss();
										}

										@Override
										public void onPrepareLoad(Drawable placeHolderDrawable) {

										}
									};
									Log.d(TAG, "imageUrl=" + imageUrl);
									if (isAdded()) {
										Picasso.get().load(imageUrl).into(target);
									}

								}

							}, 0L);
						} catch (UnsupportedEncodingException e) {
							imageUrl = null;
							downloadBtn.setVisibility(View.INVISIBLE);
							e.printStackTrace();
						}

					}
				}
				if (url.contains("/search?")) {
					imageUrl = null;
					downloadBtn.setVisibility(View.INVISIBLE);
					Map<String, String> params = extractQueryString(url);
					if (params.get("q") != null) {
						sharePrefs.edit().putString("q", params.get("q")).apply();
					}
				}
				// if (url.compareTo(query) != 0) {
				// images.add(url);
				// }
			}

		});
		String q = sharePrefs.getString("q", "Celebrity");
		webView.loadUrl("https://www.google.co.th/search?q=" + q + "&tbm=isch");
		return root;
	}

	public Map<String, String> extractQueryString(String url) {
		Map<String, String> params = new HashMap<String, String>();

		try {
			String queryString = url.substring(url.indexOf('?') + 1);

			String[] queries = queryString.split("&");

			// Convert the array of strings into an object
			int l = queries.length;
			for (int i = 0; i < queries.length; i++) {
				String[] temp = queries[i].split("=");
				params.put(temp[0], temp[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}

	@Override
	public void onStart() {

		super.onStart();
	}

}
