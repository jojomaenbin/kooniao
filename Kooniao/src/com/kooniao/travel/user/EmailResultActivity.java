package com.kooniao.travel.user;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;

/**
 * 邮箱绑定或重设密码界面
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_reset_passwd_email)
public class EmailResultActivity extends BaseActivity {
	@ViewById(R.id.tv_register_email_tips)
	TextView emailTipsTextView;
	@ViewById(R.id.title)
	TextView titlebarTextView;
	@ViewById(R.id.tv_register_email)
	TextView emailAddressTextView; // 邮箱地址

	public static enum Type {
		FORGET_PASSWORD(0), BINDING_EMAIL(1);

		public int type;

		Type(int type) {
			this.type = type;
		}
	}

	@AfterViews
	void init() {
		initData();
		initView();
	}

	int type; // 展示的是哪个界面
	private String emailAddress; // 邮箱地址

	/**
	 * 邮箱重设密码界面数据初始化
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			emailAddress = intent.getStringExtra(Define.EMAIL_ADDRESS);
			type = intent.getIntExtra(Define.TYPE, Type.FORGET_PASSWORD.type);
		}
	}

	@StringRes(R.string.bound_email_tips_one)
	String boundEmailTips;
	@StringRes(R.string.reset_password_email_tips_one)
	String forgetPasswordTips;
	@StringRes(R.string.bound_accound)
	String boundEmailTitleBar;
	@StringRes(R.string.reset_password_email)
	String forgetPasswordTitleBar;

	/**
	 * 初始化界面
	 */
	private void initView() {
		emailAddressTextView.setText(emailAddress);
		if (type == Type.FORGET_PASSWORD.type) {
			// 忘记密码
			emailTipsTextView.setText(forgetPasswordTips);
			titlebarTextView.setText(forgetPasswordTitleBar);
		} else {
			emailTipsTextView.setText(boundEmailTips);
			titlebarTextView.setText(boundEmailTitleBar);
		}
	}

	/**
	 * 后退按钮
	 * 
	 */
	@Click(R.id.iv_go_back)
	public void onBackwardClick(View view) {
		finish();
	}

	/**
	 * 点击邮箱登陆
	 * 
	 */
	@Click(R.id.bt_finish)
	public void onFinishButtonClick() {
		finish();
	}

}
