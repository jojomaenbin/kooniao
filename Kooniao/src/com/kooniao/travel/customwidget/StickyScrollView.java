package com.kooniao.travel.customwidget;

import java.util.LinkedList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class StickyScrollView extends ScrollView {
	private static final String STICKY = "sticky";
	private View currentStickyView;
	private List<View> stickyViews;
	private int stickyViewTopOffset;
	private boolean redirectTouchToStickyView;

	/**
	 * 当点击Sticky的时候，实现某些背景的渐变
	 */
	private Runnable mInvalidataRunnable = new Runnable() {

		@Override
		public void run() {
			if (currentStickyView != null) {
				int left = currentStickyView.getLeft();
				int top = currentStickyView.getTop();
				int right = currentStickyView.getRight();
				int bottom = getScrollY() + (currentStickyView.getHeight() + stickyViewTopOffset);

				invalidate(left, top, right, bottom);
			}

			postDelayed(this, 16);
		}
	};

	public StickyScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public StickyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		stickyViews = new LinkedList<View>();
	}

	/**
	 * 找到设置tag的View
	 * 
	 * @param viewGroup
	 */
	private void findViewByStickyTag(ViewGroup viewGroup) {
		int childCount = ((ViewGroup) viewGroup).getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = viewGroup.getChildAt(i);

			if (getStringTagForView(child).contains(STICKY)) {
				stickyViews.add(child);
			}

			if (child instanceof ViewGroup) {
				findViewByStickyTag((ViewGroup) child);
			}
		}

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			findViewByStickyTag((ViewGroup) getChildAt(0));
		}
		showStickyView();
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		showStickyView();
	}

	/**
	 * 显示头部
	 */
	private void showStickyView() {
		View curStickyView = null;
		View nextStickyView = null;

		for (View v : stickyViews) {
			int topOffset = v.getTop() - getScrollY();

			if (topOffset <= 0) {
				if (curStickyView == null || topOffset > curStickyView.getTop() - getScrollY()) {
					curStickyView = v;
				}
			} else {
				if (nextStickyView == null || topOffset < nextStickyView.getTop() - getScrollY()) {
					nextStickyView = v;
				}
			}
		}

		if (curStickyView != null) {
			stickyViewTopOffset = nextStickyView == null ? 0 : Math.min(0, nextStickyView.getTop() - getScrollY() - curStickyView.getHeight());
			currentStickyView = curStickyView;
			post(mInvalidataRunnable);
		} else {
			currentStickyView = null;
			removeCallbacks(mInvalidataRunnable);

		}

	}

	private String getStringTagForView(View v) {
		Object tag = v.getTag();
		return String.valueOf(tag);
	}

	/**
	 * 将sticky画出来
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (currentStickyView != null) {
			// 先保存起来
			canvas.save();
			canvas.translate(0, getScrollY() + stickyViewTopOffset);
			canvas.clipRect(0, stickyViewTopOffset, currentStickyView.getWidth(), currentStickyView.getHeight());
			currentStickyView.draw(canvas);
			// 重置坐标原点参数
			canvas.restore();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			redirectTouchToStickyView = true;
		}

		if (redirectTouchToStickyView) {
			redirectTouchToStickyView = currentStickyView != null;

			if (redirectTouchToStickyView) {
				redirectTouchToStickyView = ev.getY() <= (currentStickyView.getHeight() + stickyViewTopOffset) && ev.getX() >= currentStickyView.getLeft() && ev.getX() <= currentStickyView.getRight();
			}
		}

		if (redirectTouchToStickyView) {
			ev.offsetLocation(0, -1 * ((getScrollY() + stickyViewTopOffset) - currentStickyView.getTop()));
		}
		
		return super.dispatchTouchEvent(ev);
	}

	private boolean hasNotDoneActionDown = true;

	@SuppressLint("Recycle")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (redirectTouchToStickyView) {
			ev.offsetLocation(0, ((getScrollY() + stickyViewTopOffset) - currentStickyView.getTop()));
		}

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			hasNotDoneActionDown = false;
		}

		if (hasNotDoneActionDown) {
			MotionEvent down = MotionEvent.obtain(ev);
			down.setAction(MotionEvent.ACTION_DOWN);
			super.onTouchEvent(down);
			hasNotDoneActionDown = false;
		}

		if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
			hasNotDoneActionDown = true;
		}
		return super.onTouchEvent(ev);
	}

}
