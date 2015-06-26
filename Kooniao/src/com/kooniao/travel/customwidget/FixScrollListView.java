package com.kooniao.travel.customwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class FixScrollListView extends ListView {
	
	public FixScrollListView(Context context) {
		this(context, null);
	}

	public FixScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
