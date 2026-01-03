package com.isarainc.text.layouts;

import android.content.Context;

public class ThreeTextLayout extends SerialTextLayout {
	private static final long serialVersionUID = 3278239446102434860L;

	protected ThreeTextLayout() {
		super();
	}

	protected ThreeTextLayout(Context context) {
		super(context);
	}

	@Override
	public int styleCount() {
		return 3;
	}

}
