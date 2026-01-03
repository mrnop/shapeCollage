package com.isarainc.fonts;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Environment;
import android.preference.PreferenceManager;
import androidx.collection.LruCache;
import android.util.Log;

import com.isarainc.util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontManager {
	//private static final String TAG = "FontManager";
	public static final String USE_SYSTEM_FONT = "use_system_font";
	public static final String USE_BUNDLE_FONT = "use_bundle_font";

	private Context context;
	private SharedPreferences sharePrefs;

	private static LruCache<String, FontHolder> fontCache = new LruCache<String, FontHolder>(
			100);

	private static FontManager instance;

	public static FontManager getInstance(Context context) {
		if (instance == null) {
			instance = new FontManager(context);
		}
		return instance;
	}

	private FontManager(Context context) {
		super();
		this.context = context;

		sharePrefs = PreferenceManager.getDefaultSharedPreferences(context);

		// Load from System
		loadSystemFont("default", Typeface.DEFAULT);
		loadSystemFont("defaultBold", Typeface.DEFAULT_BOLD);
		loadSystemFont("monospace", Typeface.MONOSPACE);
		loadSystemFont("sansSerif", Typeface.SANS_SERIF);
		loadSystemFont("serif", Typeface.SERIF);

		loadBundleFont();

		
		// TODO load manual install font
		if (Utils.isSDCARDMounted()) {
			File dir = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "Android" + File.separator + "data"
					+ File.separator + "com.isarainc.fonts");
			
		
			File[] files = dir.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String filename) {
					return filename.toLowerCase().endsWith(".ttf");
				}

			});
			Date now = new Date();
			if (files != null) {
				for (File file : files) {
					try {

						
						Font font = new Font();

							font.setActive(true);
							font.setLanguages("all");
							font.setCreated(now);
							font.setRef("0");
							font.setType(Font.TYPE_EXTERNAL);
							font.setName(file.getName());
							font.setFile(file.getAbsolutePath());
						
							Typeface typeface = Typeface.createFromFile(file);
							FontHolder holder = new FontHolder(font, typeface);
							fontCache.put(file.getName(), holder);
						
					} catch (Exception e) {
						e.printStackTrace();
						try {
							file.delete();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}
	}


	private void loadSystemFont(String name, Typeface typeface) {
		Date now = new Date();
		
		Font font = new Font();
			font.setActive(sharePrefs.getBoolean("use_system_font", true));
			font.setLanguages("all");
			font.setCreated(now);
			font.setRef("0");
			font.setType(Font.TYPE_SYSTEM);
			font.setName(name);
		
		// Log.d(TAG, font.toJson());
		FontHolder holder = new FontHolder(font, typeface);
		fontCache.put(name, holder);
	}

	private void loadBundleFont() {
		Map<String, Font> patches = new HashMap<String, Font>();
		InputStream is;
		try {
			is = context.getAssets().open("fonts.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			String line = br.readLine();
			while (line != null) {
				Font font = Font.fromJson(line);
				patches.put(font.getName(), font);
				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    //Log.d(TAG, "patches=" + patches);
		try {
			String list[] = context.getAssets().list("fonts");
			if (list != null)
				for (int i = 0; i < list.length; i++) {
					Font patch = patches.get(list[i]);
					Typeface typeface = Typeface.createFromAsset(
							context.getAssets(), "fonts/" + list[i]);
					//Log.d(TAG, "list=" + list[i]);

					Date now = new Date();

					
					Font font = new Font();

						font.setActive(sharePrefs.getBoolean("use_bundle_font",
								true));
						if (patch != null) {
							String country = Utils.getCountryCode(context);
							if ("th".equalsIgnoreCase(country)) {
								if (patch.getLanguages().contains("th")) {
									font.setActive(true);
								}else{
									font.setActive(false);
								}
							}else if ("la".equalsIgnoreCase(country)) {
								if (patch.getLanguages().contains("lo")) {
									font.setActive(true);
								}
							}
							font.setActive(true);
							font.setLanguages(patch.getLanguages());
							font.setPatch(patch.getPatch());
						} else {
							font.setLanguages("th,en");
						}
						font.setType(Font.TYPE_BUNDLE);
						font.setCreated(now);
						font.setRef("1");
						font.setName(list[i]);
					
					FontHolder holder = new FontHolder(font, typeface);
					fontCache.put(list[i], holder);
				}
		} catch (Exception e) {
			//Log.v(TAG, "List error: can't list", e);

		}

		
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public List<Font> getAllAvailables() {
		List<Font> fonts = new ArrayList<Font>();
		for (FontHolder fh : fontCache.snapshot().values()) {
			fonts.add(fh.getFont());
		}
		Collections.sort(fonts);
		return fonts;
	}

	public List<Font> getAllActives() {
		List<Font> fonts = new ArrayList<Font>();
		for (FontHolder fh : fontCache.snapshot().values()) {
			if (fh.getFont().isActive()) {
				fonts.add(fh.getFont());
			}
		}
		//Log.d(TAG, "getAllActives=" + fonts);
		Collections.sort(fonts);
		return fonts;
	}

	/**
	 * 
	 * @return
	 */
	public FontHolder loadRandom() {
		List<Font> activeFonts = new ArrayList<Font>();
		for (FontHolder fh : fontCache.snapshot().values()) {
			if (fh.getFont().isActive()) {
				activeFonts.add(fh.getFont());
			}
		}
		if (activeFonts.isEmpty()) {
			return loadFont("default");
		}
		int rndId = (int) (System.currentTimeMillis() % activeFonts.size());
		Font font = activeFonts.get(rndId);
		return loadFont(font.getName());
	}

	public FontHolder loadFont(String font) {
		return loadFont(font, true);
	}

	/**
	 * 
	 * @param context
	 * @param font
	 * @return
	 */
	public FontHolder loadFont(String font, boolean activeOnly) {
		//Log.d(TAG, "loadFont="+font);
		if (font == null) {
			Log.wtf("Font", "Not found font " + font);
			return fontCache.get("default");
		}
		// Find from Cache
		FontHolder holder = fontCache.get(font);
		if (holder != null) {
			if (activeOnly && !holder.getFont().isActive()) {
				return null;
			}
		}
		return holder;
	}

	public void displayFiles(AssetManager mgr, String path, int level) {

		//Log.v(TAG, "enter displayFiles(" + path + ")");
		try {
			String list[] = mgr.list(path);
		//	Log.v(TAG, "L" + level + ": list:" + Arrays.asList(list));

			if (list != null)
				for (int i = 0; i < list.length; ++i) {
					if (level >= 1) {
						displayFiles(mgr, path + "/" + list[i], level + 1);
					} else {
						displayFiles(mgr, list[i], level + 1);
					}
				}
		} catch (IOException e) {
			//Log.v(TAG, "List error: can't list" + path);
		}
		// final AssetManager mgr = applicationContext.getAssets();
		// displayFiles(mgr, "",0);
	}


	

}
