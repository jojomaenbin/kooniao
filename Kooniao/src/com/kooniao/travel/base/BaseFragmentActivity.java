package com.kooniao.travel.base;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.base.BaseFragment.NotificationCallback;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;

public class BaseFragmentActivity extends FragmentActivity implements NotificationCallback {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onHideBottomBarListener(boolean isNeedToHide) {
	}

	@SuppressLint("HandlerLeak")
	Handler uiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			onHideBottomBarListener(false);
		}

	};

	@Override
	public boolean equals(Object o) {
		uiHandler.removeMessages(0);
		uiHandler.sendEmptyMessageDelayed(0, 500);
		return super.equals(o);
	}
	
	/**
	 * 土司提示
	 * 
	 * @param tips
	 */
	public void showToast(String tips) {
		Toast.makeText(KooniaoApplication.getInstance(), tips, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 土司提示
	 * 
	 * @param tips
	 */
	public void showToast(CharSequence tips) {
		Toast.makeText(KooniaoApplication.getInstance(), tips, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 土司提示
	 * 
	 * @param tips
	 */
	public void showToast(int tips) {
		Toast.makeText(KooniaoApplication.getInstance(), tips, Toast.LENGTH_SHORT).show();
	}

	private KooniaoProgressDialog mProgressDialog;

	/**
	 * 显示酷鸟进度框
	 */
	public void showProgressDialog() {
		mProgressDialog = new KooniaoProgressDialog(this);
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}
	}

	/**
	 * 隐藏酷鸟进度框
	 */
	public void dissmissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}
}
