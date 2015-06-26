package com.kooniao.travel.customwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * 为了适配ScrollView的自定义ExpandListview
 * @author ke.wei.quan
 *
 */
public class FixExpandListview extends ExpandableListView {

	public FixExpandListview(Context context) {
		this(context, null);
	}

	public FixExpandListview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
