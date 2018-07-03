package com.wedevgroup.weflyhelperfordriver.utils.design;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class IconsEditTextView extends android.support.v7.widget.AppCompatTextView {

	private static Typeface sMaterialDesignIcons;

	public IconsEditTextView(Context context) {
		this(context, null);
	}

	public IconsEditTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public IconsEditTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (isInEditMode()) return;//Won't work in Eclipse graphical layout
		setTypeface();
	}
	
	private void setTypeface() {
		if (sMaterialDesignIcons == null) {
			sMaterialDesignIcons = Typeface.createFromAsset(getContext().getAssets(), "fonts/fontelloEdit.ttf");
		}
		setTypeface(sMaterialDesignIcons);
	}
}
