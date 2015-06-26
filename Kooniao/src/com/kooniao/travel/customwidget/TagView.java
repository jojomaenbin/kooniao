package com.kooniao.travel.customwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import com.kooniao.travel.R;

public class TagView extends ToggleButton {

	public TagView(Context paramContext) {
		super(paramContext);
		init();
	}

	public TagView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init();
	}

	public TagView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, 0);
		init();
	}

	private void init() {
		setTextOn(null);
		setTextOff(null);
		setText("");
		setBackgroundResource(R.drawable.tag_normal_bg);
	}

	public void changeNormalBackground() {
		setTextColor(getResources().getColor(R.color.v909090));
		setBackgroundResource(R.drawable.tag_normal_bg);
	}

	public void changeSelectedBackground() {
		setTextColor(getResources().getColor(R.color.white));
		setBackgroundResource(R.drawable.tag_selected_bg);
	}

}
