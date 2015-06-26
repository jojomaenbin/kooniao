package com.kooniao.travel.customwidget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

public class BlueTextShowPopup extends PopupWindow {

	public BlueTextShowPopup(Context context) {
		this(context, null);
	}

	public BlueTextShowPopup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BlueTextShowPopup(View contentView, int width, int height) {
		super(contentView, width, height);
	}

	public void setBlueitem(String string) {
		checkTextView(string, getContentView());
	}

	private void checkTextView(String string, View view) {
		if (view instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) view;
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				checkTextView(string, viewGroup.getChildAt(i));
			}
		} else {
			if (view instanceof TextView) {
				if (((TextView) view).getText().equals(string)) {
					((TextView) view).setTextColor(Color.parseColor("#16B8EB"));
				} else {
					((TextView) view).setTextColor(Color.BLACK);
				}
			}
		}
	}
}
