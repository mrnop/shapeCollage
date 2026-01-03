package com.isarainc.fonts;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;


public class Font implements Comparable<Font> {
	public transient static final int TYPE_SYSTEM = 0;
	public transient static final int TYPE_BUNDLE = 1;
	public transient static final int TYPE_EXTERNAL = 2;

	public transient static final int PATCH_UPPERCASE = 1;
	public transient static final int PATCH_LAOKEYBOARD = 2;
	public transient static final int PATCH_HINDI_KRUTIDEV = 3;

	public transient static final char[] LAO_UNICODE = { '\u0e81', '\u0e82',
			'\u0e84', '\u0e87', '\u0e88', '\u0e8A', '\u0e8D', '\u0e94',
			'\u0e95', '\u0e96', '\u0e97', '\u0e99', '\u0e9A', '\u0e9B',
			'\u0e9C', '\u0e9D', '\u0e9E', '\u0e9F', '\u0eA1', '\u0eA2',
			'\u0eA3', '\u0eA5', '\u0eA7', '\u0eAA', '\u0eAB', '\u0eAD',
			'\u0eAE', '\u0eAF', '\u0eB0', '\u0eB1', '\u0eB2', '\u0eB3',
			'\u0eB4', '\u0eB5', '\u0eB6', '\u0eB7', '\u0eB8', '\u0eB9',
			'\u0eBB', '\u0eBB', '\u0eBD', '\u0eC0', '\u0eC1', '\u0eC2',
			'\u0eC3', '\u0eC4', '\u0eC6', '\u0eC8', '\u0eC9', '\u0eCA',
			'\u0eCB', '\u0eCC', '\u0eCD', '\u0eD0', '\u0eD1', '\u0eD2',
			'\u0eD3', '\u0eD4', '\u0eD5', '\u0eD6', '\u0eD7', '\u0eD8',
			'\u0eD9', '\u0eDC', '\u0eDD', '\u0eDE', '\u0eDF' };

	public transient static final char[] LAO_KEY = { 'd', '0', '7', '\'', '9',
			'-', 'p', 'f', '8', '4', 'm', 'o', '[', 'x', 'x', '/', 'r', '2',
			',', '1', 'I', ']', ';', 'l', 's', 'v', 'i', 'C', 't', 'h', 'k',
			'e', 'y', 'u', 'b', 'n', '5', '6', 'q', '^', 'P', 'g', 'c', '3',
			'.', 'w', 'M', 'j', 'h', 'H', 'J', '%', '=', 'W', '!', '@', '#',
			'$', '&', '*', '(', ')', '_', '|', '\\', '\u0eDE', '\u0eDF' };

	public transient static final String[] UNCODE_KRUTIDEV = new String[] {// ignore
																			// all
			// nuktas
			// except in
			// ड़ and ढ़
			"‘", "’", "“", "”", "(", ")", "{", "}", "=", "।", "?", "-", "µ",
			"॰", ",", ".", "् ", "०", "१", "२", "३", "४", "५", "६", "७", "८",
			"९", "x",

			"फ़्", "क़", "ख़", "ग़",
			"ज़्",
			"ज़",
			"ड़",
			"ढ़",
			"फ़",
			"य़",
			"ऱ",
			"ऩ", // one-byte nukta varNas
			"त्त्", "त्त", "क्त", "दृ", "कृ",

			"ह्न", "ह्य", "हृ", "ह्म", "ह्र", "ह्", "द्द", "क्ष्", "क्ष",
			"त्र्", "त्र", "ज्ञ", "छ्य", "ट्य", "ठ्य", "ड्य", "ढ्य", "द्य",
			"द्व", "श्र", "ट्र", "ड्र", "ढ्र", "छ्र", "क्र", "फ्र", "द्र",
			"प्र", "ग्र", "रु", "रू", "्र",

			"ओ", "औ", "आ", "अ", "ई", "इ", "उ", "ऊ", "ऐ", "ए", "ऋ",

			"क्", "क", "क्क", "ख्", "ख", "ग्", "ग", "घ्", "घ", "ङ", "चै", "च्",
			"च", "छ", "ज्", "ज", "झ्", "झ", "ञ",

			"ट्ट", "ट्ठ", "ट", "ठ", "ड्ड", "ड्ढ", "ड", "ढ", "ण्", "ण", "त्",
			"त", "थ्", "थ", "द्ध", "द", "ध्", "ध", "न्", "न",

			"प्", "प", "फ्", "फ", "ब्", "ब", "भ्", "भ", "म्", "म", "य्", "य",
			"र", "ल्", "ल", "ळ", "व्", "व", "श्", "श", "ष्", "ष", "स्", "स",
			"ह",

			"ऑ", "ॉ", "ो", "ौ", "ा", "ी", "ु", "ू", "ृ", "े", "ै", "ं", "ँ",
			"ः", "ॅ", "ऽ", "् ", "्" };
	public transient static final String[] FONT_KRUTIDEV = new String[] { "^",
			"*", "Þ", "ß", "¼", "½", "¿", "À", "¾", "A", "\\", "&", "&", "Œ",
			"]", "-", "~ ", "å", "ƒ", "„", "…", "†", "‡", "ˆ", "‰", "Š", "‹",
			"Û",

			"¶", "d", "[k", "x", "T", "t", "M+", "<+", "Q", ";", "j", "u", "Ù",
			"Ùk", "ä", "–", "—",

			"à", "á", "â", "ã", "ºz", "º", "í", "{", "{k", "«", "=", "K", "Nî",
			"Vî", "Bî", "Mî", "<î", "|", "}", "J", "Vª", "Mª", "<ªª", "Nª",
			"Ø", "Ý", "æ", "ç", "xz", "#", ":", "z",

			"vks", "vkS", "vk", "v", "bZ", "b", "m", "Å", ",s", ",", "_",

			"D", "d", "ô", "[", "[k", "X", "x", "?", "?k", "³", "pkS", "P",
			"p", "N", "T", "t", "÷", ">", "¥",

			"ê", "ë", "V", "B", "ì", "ï", "M", "<", ".", ".k", "R", "r", "F",
			"Fk", ")", "n", "/", "/k", "U", "u",

			"I", "i", "¶", "Q", "C", "c", "H", "Hk", "E", "e", "¸", ";", "j",
			"Y", "y", "G", "O", "o", "'", "'k", "\"", "\"k", "L", "l", "g",

			"v‚", "‚", "ks", "kS", "k", "h", "q", "w", "`", "s", "S", "a", "¡",
			"%", "W", "·", "~ ", "~" };

	private long id;
	private String ref;
	private String name;
	private String remoteUrl;
	private String file;
	private String languages;
	private int type = TYPE_EXTERNAL;
	private boolean active;
	private int patch;
	private Date created;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}

	public void setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public int getPatch() {
		return patch;
	}

	public void setPatch(int patch) {
		this.patch = patch;
	}

	public boolean isFileExists() {
		if (type != Font.TYPE_EXTERNAL)
			return true;
		if (file == null)
			return false;
		File f = new File(file);
		return f.exists();
	}

	public String getLanguages() {
		return languages;
	}

	public void setLanguages(String languages) {
		this.languages = languages;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public boolean isSupported(String lang) {
		if (languages != null) {
			String[] langs = languages.split(",");
			for (int i = 0; i < langs.length; i++) {
				if (lang.equals(langs[i])) {
					return true;
				}
			}
		}
		return false;
	}

	public String toJson() {
		JSONObject object = new JSONObject();

		try {
			object.put("id", id);
		} catch (JSONException e) {
			// e.printStackTrace();
		}
		try {
			object.put("name", name);
		} catch (JSONException e) {
			// e.printStackTrace();
		}
		try {
			object.put("ref", ref);
		} catch (JSONException e) {
			// e.printStackTrace();
		}

		try {
			object.put("remoteUrl", remoteUrl);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			object.put("file", file);
		} catch (JSONException e) {
			// e.printStackTrace();
		}
		try {
			object.put("languages", languages);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			object.put("type", type);
		} catch (JSONException e) {
			// e.printStackTrace();
		}
		try {
			object.put("active", active);
		} catch (JSONException e) {
			// e.printStackTrace();
		}
		try {
			object.put("patch", patch);
		} catch (JSONException e) {
			// e.printStackTrace();
		}
		return object.toString();
	}

	public static Font fromJson(String json) {
		Font font = new Font();

		JSONObject jsonObj;

		try {
			jsonObj = new JSONObject(json);
			try {
				font.setId(jsonObj.getLong("id"));
			} catch (JSONException e) {
				// e.printStackTrace();
			}
			try {
				font.setName(jsonObj.getString("name"));
			} catch (JSONException e) {
				// e.printStackTrace();
			}
			try {
				font.setRef(jsonObj.getString("ref"));
			} catch (JSONException e) {
				// e.printStackTrace();
			}
			try {
				font.setRemoteUrl(jsonObj.getString("remoteUrl"));
			} catch (JSONException e) {
				// e.printStackTrace();
			}
			try {
				font.setFile(jsonObj.getString("file"));
			} catch (JSONException e) {
				// e.printStackTrace();
			}
			try {
				font.setLanguages(jsonObj.getString("languages"));
			} catch (JSONException e) {
				// e.printStackTrace();
			}
			try {
				font.setType(jsonObj.getInt("type"));
			} catch (JSONException e) {
				// e.printStackTrace();
			}
			try {
				font.setActive(jsonObj.getBoolean("active"));
			} catch (JSONException e) {
				// e.printStackTrace();
			}

			try {
				font.setPatch(jsonObj.getInt("patch"));
			} catch (JSONException e) {
				// e.printStackTrace();
			}
		} catch (JSONException e1) {

			// e1.printStackTrace();
		}

		return font;

	}

	/**
	 * Patch if font not support CAPITAL letter
	 * 
	 * @param text
	 * @return
	 */
	public String patch(String text) {
		// Log.d(TAG, "font=" +font + ",patch="+fontManager.getPatch(font));

		// Patch Bangli

		if (patch == PATCH_UPPERCASE) {
			StringBuffer txtLower = new StringBuffer(text.toLowerCase());
			StringBuffer newtext = new StringBuffer();
			for (int i = 0; i < txtLower.length(); i++) {
				char ch = txtLower.charAt(i);
				switch (ch) {
				case 'ๆ':
					newtext.append('๙');
					break;
				case '๚':
					newtext.append('๙');
					break;
				default:
					newtext.append(ch);
					break;
				}

			}
			return newtext.toString();
		} else if (patch == PATCH_LAOKEYBOARD) {
			// Patch lao 8 bit font
			StringBuffer newtext = new StringBuffer();

			for (char c : text.toCharArray()) {
				boolean found = false;
				for (int i = 0; i < LAO_UNICODE.length; i++) {
					if (LAO_UNICODE[i] == c) {
						newtext.append(LAO_KEY[i]);
						found = true;
						break;
					}
				}
				if (!found) {
					newtext.append(c);
				}
			}
			return newtext.toString();
		} else if (patch == PATCH_HINDI_KRUTIDEV) {
			return unicode2Krutidev010(text);
		}

		return text;
	}

	@Override
	public int compareTo(Font another) {
		return name.compareTo(another.getName());
	}
	
	private String replaceSymbols(String modified_substring) {

		// if string to be converted is non-blank then no need of any
		// processing.
		if (modified_substring != "") {

			// first replace the two-byte nukta_varNa with corresponding
			// one-byte nukta varNas.

			modified_substring = modified_substring.replace("क", "क़");
			modified_substring = modified_substring.replace("ख़‌", "ख़");
			modified_substring = modified_substring.replace("ग़", "ग़");
			modified_substring = modified_substring.replace("ज़", "ज़");
			modified_substring = modified_substring.replace("ड़", "ड़");
			modified_substring = modified_substring.replace("ढ़", "ढ़");
			modified_substring = modified_substring.replace("ऩ", "ऩ");
			modified_substring = modified_substring.replace("फ़", "फ़");
			modified_substring = modified_substring.replace("य़", "य़");
			modified_substring = modified_substring.replace("ऱ", "ऱ");

			// code for replacing "ि" (chhotee ee kii maatraa) with "f" and
			// correcting its position too.

			int position_of_f = modified_substring.indexOf("ि");
			while (position_of_f != -1) // while-02
			{
				char character_left_to_f = modified_substring
						.charAt(position_of_f - 1);
				modified_substring = modified_substring.replace(
						character_left_to_f + "ि", "f" + character_left_to_f);

				position_of_f = position_of_f - 1;

				while ((modified_substring.charAt(position_of_f - 1) == ' ')
						&& (position_of_f != 0)) {
					String string_to_be_replaced = modified_substring
							.charAt(position_of_f - 2) + "्";
					modified_substring = modified_substring.replace(
							string_to_be_replaced + "f", "f"
									+ string_to_be_replaced);

					position_of_f = position_of_f - 2;
				}
				position_of_f = modified_substring.indexOf("ि",
						position_of_f + 1); // search for f ahead of the current
											// position.

			} // end of while-02 loop
			// ************************************************************
			// modified_substring = modified_substring.replace( /fर्/g , "£" ) ;
			// ************************************************************
			// Eliminating "र्" and putting Z at proper position for this.

			String set_of_matras = "ािीुूृेैोौं:ँॅ";

			modified_substring += "  "; // add two spaces after the string to
										// avoid UNDEFINED char in the following
										// code.

			int position_of_half_R = modified_substring.indexOf("र्");
			while (position_of_half_R > 0) // while-04
			{
				// "र्" is two bytes long
				int probable_position_of_Z = position_of_half_R + 2;

				int character_right_to_probable_position_of_Z = modified_substring
						.charAt(probable_position_of_Z + 1);

				// trying to find non-maatra position right to
				// probable_position_of_Z .

				while (set_of_matras
						.indexOf(character_right_to_probable_position_of_Z) != -1) {
					probable_position_of_Z = probable_position_of_Z + 1;
					character_right_to_probable_position_of_Z = modified_substring
							.charAt(probable_position_of_Z + 1);
				} // end of while-05

				String string_to_be_replaced = modified_substring.substring(
						position_of_half_R + 2, (probable_position_of_Z
								- position_of_half_R - 1));
				modified_substring = modified_substring.replace("र्"
						+ string_to_be_replaced, string_to_be_replaced + "Z");
				position_of_half_R = modified_substring.indexOf("र्");
			} // end of while-04
			//System.out.println("modified_substring=" +modified_substring);
			modified_substring = modified_substring.substring(0,
					modified_substring.length() - 2);

			// substitute array_two elements in place of corresponding array_one
			// elements

			for (int input_symbol_idx = 0; input_symbol_idx < UNCODE_KRUTIDEV.length; input_symbol_idx++) {
				int idx = 0; // index of the symbol being searched for
								// replacement

				while (idx != -1) // whie-00
				{

					modified_substring = modified_substring.replace(
							UNCODE_KRUTIDEV[input_symbol_idx],
							FONT_KRUTIDEV[input_symbol_idx]);
					idx = modified_substring
							.indexOf(UNCODE_KRUTIDEV[input_symbol_idx]);
				} // end of while-00 loop
			} // end of for loop

		} // end of IF statement meant to supress processing of blank string.

		return modified_substring;
	} // end of the function Replace_Symbols( )
	
	
	private String unicode2Krutidev010(String src) {

		// ************************************************************
		// Put "Enter chunk size:" line before "<textarea name= ..." if required
		// to be used.
		// ************************************************************
		// Enter chunk size: <input type="text" name="chunksize" value="6000"
		// size="7" maxsize="7" style="text-align:right"><br/><br/>
		// ************************************************************
		// The following two characters are to be replaced through proper
		// checking of locations:

		// "र्" (reph)
		// "Z" )

		// "ि"
		// "f" )
        String result=null;
		int array_one_length = UNCODE_KRUTIDEV.length;

		// if the input is plain text

		String modified_substring = src;

		// ****************************************************************************************
		// Break the long text into small bunches of max. max_text_size
		// characters each.
		// ****************************************************************************************
		int text_size = src.length();

		String processed_text = ""; // blank

		int sthiti1 = 0;
		int sthiti2 = 0;
		int chale_chalo = 1;

		int max_text_size = 6000;

		// ************************************************************
		// var max_text_size = chunksize;
		// alert(max_text_size);
		// ************************************************************

		while (chale_chalo == 1) {
			sthiti1 = sthiti2;
			//System.out.println("sthiti1=" +sthiti1 + ":sthiti2=" +sthiti2);
			//System.out.println("sthiti1=" +sthiti1 + ":text_size=" +text_size + ":max_text_size=" +max_text_size);
			if (sthiti2 < (text_size - max_text_size)) {
				
				sthiti2 += max_text_size;
				while (src.charAt(sthiti2) != ' ') {
					sthiti2--;
				}
			} else {
				sthiti2 = text_size;
				chale_chalo = 0;
			}

			modified_substring = src.substring(sthiti1, sthiti2);
			
			modified_substring=replaceSymbols(modified_substring);

			processed_text += modified_substring;

			// ****************************************************************************************
			// Breaking part code over
			// ****************************************************************************************
			// processed_text = processed_text.replace( /mangal/g ,
			// "Krutidev010" ) ;

			result=processed_text;
		}
		return result;
	}
	
	@Override
	public String toString() {
		return "Font [id=" + id + ", ref=" + ref + ", name=" + name
				+ ", remoteUrl=" + remoteUrl + ", file=" + file
				+ ", languages=" + languages + ", type=" + type + ", active="
				+ active + ", patch=" + patch + ", created=" + created + "]";
	}

}