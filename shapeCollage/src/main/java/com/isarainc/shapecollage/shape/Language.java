package com.isarainc.shapecollage.shape;

import android.content.Context;

import com.isarainc.shapecollage.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * POJO
 * 
 */
public class Language implements Comparable<Language> {
	private String code;
	private String name;
	private static Map<String, Language> langs = new HashMap<String, Language>();
	private static boolean init = false;

	public static void init(Context context) {
		if(init) return;
		try {
			String allLanguagesString = readFileAsString(context);
			JSONObject jsonObject = new JSONObject(allLanguagesString);
			Iterator<?> keys = jsonObject.keys();

			// Add the data to all countries list
			while (keys.hasNext()) {
				String key = (String) keys.next();
				Language lang = new Language();
				lang.setCode(key);
				lang.setName(jsonObject.getString(key));
				langs.put(key, lang);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		init = true;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(Language another) {

		return code.compareTo(another.code);
	}

	public static String getLanguage(String code) {
		if (!init)
			throw new RuntimeException("Not init yet");
		Language l = langs.get(code);
		if (l != null) {
			return l.getName();
		} else {
			return null;
		}

	}

	public static List<Language> getAvailables() {
		if (!init)
			throw new RuntimeException("Not init yet");

		List<Language> l = new ArrayList<Language>();
		l.addAll(langs.values());
		Collections.sort(l);
		return l;
	}

	private static String readFileAsString(Context context)
			throws java.io.IOException {
		InputStream inputStream = context.getResources().openRawResource(
				R.raw.languages);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		StringBuffer result = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}
		reader.close();
		return result.toString();
	}

	@Override
	public String toString() {
		return code + " : " + name;
	}

}