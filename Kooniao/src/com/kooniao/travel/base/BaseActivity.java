package com.kooniao.travel.base;

import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

public class BaseActivity extends Activity {

	public static BaseActivity frontActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		frontActivity = this;
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
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

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void finish() {
		super.finish();
	}

}
