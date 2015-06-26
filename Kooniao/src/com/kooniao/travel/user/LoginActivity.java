package com.kooniao.travel.user;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.customwidget.SelectedImgeView;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.manager.UserManager.UserInfoResultCallback;
import com.kooniao.travel.model.UserInfo;
import com.kooniao.travel.utils.StringUtil;

/**
 * 登录界面
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

	@ViewById(R.id.et_login_user_key)
	EditText loginKeyEditText; // 登录名
	@ViewById(R.id.et_login_password)
	EditText loginPasswordEditText; // 登录密码
	@ViewById(R.id.iv_login_key_control)
	SelectedImgeView passwdVisiableController; // 显示与隐藏密码控制按钮

	/**
	 * 后退按钮的点击
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 注册按钮的点击
	 */
	@Click(R.id.tv_login_register)
	void onRegisterClick() {
		Intent intent = new Intent(LoginActivity.this, RegisterActivity_.class);
		startActivityForResult(intent, REQUEST_CODE_REGISTER);
	}

	/**
	 * 显示与隐藏密码控制按钮的点击
	 */
	@Click(R.id.iv_login_key_control)
	void onPasswordControlClick() {
		if (passwdVisiableController.getSelectFlag()) {
			passwdVisiableController.setSelectFlag(false);
			loginPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			passwdVisiableController.setImageResource(R.drawable.login_key_invisiable);
		} else {
			passwdVisiableController.setSelectFlag(true);
			loginPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			passwdVisiableController.setImageResource(R.drawable.login_key_visiable);
		}

		String password = loginPasswordEditText.getText().toString().trim();
		if (!StringUtil.isEmpty(password)) {
			loginPasswordEditText.setSelection(password.length());
		}
	}

	KooniaoProgressDialog progressDialog;

	/**
	 * 登录按钮的点击
	 */
	@Click(R.id.lr_login)
	void onLoginClick() {
		String loginKey = loginKeyEditText.getText().toString(); // 登录名
		String loginKeyVerifyTips = UserManager.getInstance().verifyLoginKey(loginKey);
		if (loginKeyVerifyTips == null) {
			String loginPassword = loginPasswordEditText.getText().toString(); // 登录密码
			if (!"".equals(loginPassword)) { 
				closeInputKeyboard(); // 关闭键盘
				if (progressDialog == null) {
					progressDialog = new KooniaoProgressDialog(LoginActivity.this);
				}
				progressDialog.show();

				UserManager.getInstance().userLogin(loginKey, loginPassword, new UserInfoResultCallback() {

					@Override
					public void result(String errMsg, UserInfo userInfo) {
						onLoginComplete(errMsg, userInfo);
					}
				});
			} else {
				Toast.makeText(LoginActivity.this, R.string.password_empty, Toast.LENGTH_SHORT).show();
				showInputKeyboard(loginPasswordEditText);
			}
		} else {
			showInputKeyboard(loginKeyEditText);
			Toast.makeText(getBaseContext(), loginKeyVerifyTips, Toast.LENGTH_SHORT).show();
		}
	}

	@StringRes(R.string.login_success)
	String loginSuccessTips; // 登录成功

	/**
	 * 登录请求完成
	 * 
	 * @param errMsg
	 * @param userInfo
	 */
	@UiThread
	void onLoginComplete(String errMsg, UserInfo userInfo) {
		progressDialog.dismiss();

		if (errMsg == null) {
			Toast.makeText(getBaseContext(), loginSuccessTips, Toast.LENGTH_SHORT).show();
			activityFinish(userInfo);
		} else {
			Toast.makeText(getBaseContext(), errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 结束当前页面
	 * 
	 * @param userInfo
	 */
	private void activityFinish(UserInfo userInfo) {
	    boolean relogin=false;
	    if ( getIntent()!=null&&getIntent().getExtras()!=null) {
	    	relogin=(Boolean) getIntent().getExtras().get("relogin");
		}
	    if (relogin) {
	    	ApiCaller.getInstance().repost();
		}
	    else {
			Intent data = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable(Define.DATA, userInfo);
		data.putExtras(bundle);
		setResult(RESULT_OK, data);
		}
	    finish();
		
	}

	/**
	 * 弹出输入键盘
	 * 
	 * @param view
	 */
	private void showInputKeyboard(View remindView) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		remindView.requestFocus();
		imm.showSoftInput(remindView, 0);
	}

	/**
	 * 关闭输入键盘
	 */
	private void closeInputKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	}

	/**
	 * 忘记密码按钮的点击
	 */
	@Click(R.id.tv_forget_password)
	void onForgetPasswordClick() {
		Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity_.class);
		startActivity(intent);
	}

	final int REQUEST_CODE_REGISTER = 111; // 注册

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_REGISTER: // 注册
			if (resultCode == RESULT_OK) {
				activityFinish(null);
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
