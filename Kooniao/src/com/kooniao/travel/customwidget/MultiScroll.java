package com.kooniao.travel.customwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 处理滑动跟点击事件冲突
 * @author ke.wei.quan
 *
 */
public class MultiScroll extends ScrollView {
	
	public MultiScroll(Context context) {
		this(context, null);
	}
	
	public MultiScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean canScrollVertically(int direction) {
		return true;
	}

}
