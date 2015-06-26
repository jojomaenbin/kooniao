package com.kooniao.travel.customwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SelectedImgeView extends ImageView {
	private boolean selectFlag = false;

	public SelectedImgeView(Context context) {
		this(context, null);
	}

	public SelectedImgeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSelectFlag(boolean selectFlag) {
		this.selectFlag = selectFlag;
	}

	public boolean getSelectFlag() {
		return selectFlag;
	}

}
