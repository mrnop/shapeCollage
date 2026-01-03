package com.isarainc.fonts;

import android.graphics.Typeface;


public class FontHolder {
	private Font font;
	private Typeface typeface;
	
	public FontHolder(Font font, Typeface typeface) {
		super();
		this.font = font;
		this.typeface = typeface;
	}
	public Font getFont() {
		return font;
	}
	public void setFont(Font font) {
		this.font = font;
	}
	public Typeface getTypeface() {
		return typeface;
	}
	public void setTypeface(Typeface typeface) {
		this.typeface = typeface;
	}
	@Override
	public String toString() {
		return "FontHolder [font=" + font + ", typeface=" + typeface + "]";
	}
	
	
}
