package com.kooniao.travel.customwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class BottomLayout extends LinearLayout {

	private int mhight = 0;

	public int getmHight() {
		return mhight;
	}

	public BottomLayout(Context context) {
		super(context);
		post(new Runnable() {

			@Override
			public void run() {
				if (mhight == 0) {
					mhight = getHeight();
				}
			}
		});
	}

	public BottomLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		post(new Runnable() {

			@Override
			public void run() {
				if (mhight == 0) {
					mhight = getHeight();
				}
			}
		});

	}

	public BottomLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		post(new Runnable() {

			@Override
			public void run() {
				if (mhight == 0) {
					mhight = getHeight();
				}
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}
}
