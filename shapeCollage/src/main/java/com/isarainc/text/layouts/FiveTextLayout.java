package com.isarainc.text.layouts;

import android.content.Context;

public class FiveTextLayout extends SerialTextLayout {
	private static final long serialVersionUID = 3278239446102434860L;

	protected FiveTextLayout() {
		super();
	}

	protected FiveTextLayout(Context context) {
		super(context);
	}

	@Override
	public int styleCount() {
		return 10;
	}

}
