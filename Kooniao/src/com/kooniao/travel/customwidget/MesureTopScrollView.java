package com.kooniao.travel.customwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 监听距离顶部距离ScrollView
 * 
 * @author ke.wei.quan
 * 
 */
public class MesureTopScrollView extends ScrollView {

	public MesureTopScrollView(Context context) {
		this(context, null);
	}

	public MesureTopScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MesureTopScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_UP:
			if (currentTop >= bottom && currentTop != 0 && bottom != 0) {
				onScrollListener.scrollToBottom();
			}
			break;

		default:
			break;
		}
		return super.onTouchEvent(ev);
	}
	
	private OnScrollListener onScrollListener;

	/**
	 * 设置滚动接口
	 * 
	 * @param onScrollListener
	 */
	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
	}

	private int currentTop = 0;
	private int bottom = 0;
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (onScrollListener != null) {
			onScrollListener.onScroll(t);
			currentTop = getScrollY() + getHeight();
			bottom = computeVerticalScrollRange();
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}

	/**
	 * 滚动的回调接口
	 * 
	 * @author ke.wei.quan
	 * 
	 */
	public interface OnScrollListener {
		void onScroll(int scrollY);

		void scrollToBottom();
	}
}
