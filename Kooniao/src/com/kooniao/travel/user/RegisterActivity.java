package com.kooniao.travel.user;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.WebBrowserActivity_;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.SelectedImgeView;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.manager.UserManager.GetVerificationCodeResultCallback;
import com.kooniao.travel.manager.UserManager.StringResultCallback;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.InputMethodUtils;
import com.kooniao.travel.utils.StringUtil;

/**
 * 注册界面
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_register)
public class RegisterActivity extends BaseActivity {

	/**
	 * 邮箱注册相关
	 */
	@ViewById(R.id.layout_email_register)
	View emailRegisterLayout; // 邮箱注册布局
	@ViewById(R.id.v_register_email_selected)
	View emailDividerLineView; // 邮箱注册底部蓝条
	@ViewById(R.id.tv_register_email)
	TextView emailRegisterTextView; // 顶部邮箱注册
	@ViewById(R.id.et_register_email_nickname)
	EditText emailNickNamEditText; // 邮箱注册昵称
	@ViewById(R.id.et_register_email)
	EditText emailEditText; // 邮箱地址
	@ViewById(R.id.et_register_email_password)
	EditText emailRegisterPdEditText; // 邮箱注册密码
	@ViewById(R.id.iv_register_email_key_control)
	SelectedImgeView emailPasswdVisibilityController; // 邮箱密码是否显示控制按钮

	/**
	 * 手机注册相关
	 */
	@ViewById(R.id.sublayout_phone_register)
	View phoneRegisterLayout; // 手机注册布局
	@ViewById(R.id.v_register_phone_selected)
	View phoneDividerLineView; // 手机注册底部蓝条
	@ViewById(R.id.tv_register_phone)
	TextView phoneRegisterTextView; // 顶部手机注册
	@ViewById(R.id.et_register_phone_nickname)
	EditText phoneNickNamEditText; // 手机注册昵称
	@ViewById(R.id.et_register_phone_num)
	EditText phoneNumEditText; // 手机号
	@ViewById(R.id.et_register_validate_code)
	EditText verificationCodeEditText; // 手机验证码
	@ViewById(R.id.et_register_phone_password)
	EditText phoneRegisterPdEditText; // 手机注册密码
	@ViewById(R.id.iv_register_phone_key_control)
	SelectedImgeView phonePasswdVisibilityController; // 手机密码是否显示控制
	@ViewById(R.id.tv_register_get_verification_code)
	TextView getVerificationCodeTextView; // 获取手机验证码

	private boolean isEmailRegister; // 是否是邮箱注册

	/**
	 * 初始化界面数据
	 */
	@AfterViews
	void initData() {
		isEmailRegister = false; // 默认邮箱注册
	}

	/**
	 * 后退按钮的点击
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 登录按钮的点击
	 */
	@Click(R.id.tv_register_login)
	void onLoginClick() {
		onBackPressed(); 
	}

	/**
	 * 切换至邮箱注册布局
	 */
	@Click(R.id.tv_register_email)
	void onSwitchEmailRegisterLayoutClick() {
		isEmailRegister = true;
		emailRegisterLayout.setVisibility(View.VISIBLE);
		emailRegisterTextView.setTextColor(getResources().getColor(R.color.v16b8eb));
		phoneRegisterLayout.setVisibility(View.GONE);
		phoneRegisterTextView.setTextColor(getResources().getColor(R.color.v020202));
		startEmailBottomLineAnimation();
	}

	/**
	 * 切换至手机注册布局
	 */
	@Click(R.id.tv_register_phone)
	void onSwitchPhoneRegisterLayoutClick() {
		isEmailRegister = false;
		phoneRegisterLayout.setVisibility(View.VISIBLE);
		phoneRegisterTextView.setTextColor(getResources().getColor(R.color.v16b8eb));
		emailRegisterLayout.setVisibility(View.GONE);
		emailRegisterTextView.setTextColor(getResources().getColor(R.color.v020202));
		startPhoneBottomLineAnimation();
	}

	/**
	 * 开启邮箱注册底部蓝色条动画
	 */
	private void startEmailBottomLineAnimation() {
		emailDividerLineView.setVisibility(View.VISIBLE);
		Animation rightInAnimation = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.move_right_in);
		rightInAnimation.setFillAfter(true);
		emailDividerLineView.setAnimation(rightInAnimation);
		// ////
		phoneDividerLineView.setVisibility(View.INVISIBLE);
		Animation rightOutAnimation = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.move_right_out);
		rightOutAnimation.setFillAfter(false);
		phoneDividerLineView.setAnimation(rightOutAnimation);
	}

	/**
	 * 开启手机注册底部蓝色条动画
	 */
	private void startPhoneBottomLineAnimation() {
		emailDividerLineView.setVisibility(View.INVISIBLE);
		Animation leftOutAnimation = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.move_left_out);
		leftOutAnimation.setFillAfter(false);
		emailDividerLineView.setAnimation(leftOutAnimation);
		// ///
		phoneDividerLineView.setVisibility(View.VISIBLE);
		Animation leftInAnimation = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.move_left_in);
		leftInAnimation.setFillAfter(true);
		phoneDividerLineView.setAnimation(leftInAnimation);
	}

	final int intervalTime = 60 * 1000; // 获取验证码的间隔时间
	final int reduceTime = 1 * 1000; // 倒计时
	private CountDownTimer timer; // 倒计时

	/**
	 * 点击获取手机验证码
	 */
	@Click(R.id.tv_register_get_verification_code)
	void onGetVerificationCodeClick() {
		String mobile = phoneNumEditText.getText().toString().trim();
		String toastTips = "";
		if (mobile.length() == 0) {
			toastTips = StringUtil.getStringFromR(R.string.phone_num_empty); // 手机号为空
			InputMethodUtils.showInputKeyBord(RegisterActivity.this, phoneNumEditText);
		} else if (mobile.length() != 11) {
			toastTips = StringUtil.getStringFromR(R.string.phone_num_form_wrong); // 手机号格式错误
			InputMethodUtils.showInputKeyBord(RegisterActivity.this, phoneNumEditText);
		}

		if ("".equals(toastTips)) {
			// 开启倒计时
			startCountDownTimer();
			// 获取手机验证码
			getVerificationCode(mobile);
		} else {
			Toast.makeText(RegisterActivity.this, toastTips, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 开启倒计时
	 */
	private void startCountDownTimer() {
		getVerificationCodeTextView.setClickable(false); // 设为不可点击
		getVerificationCodeTextView.setTextColor(getResources().getColor(R.color.v909090));
		getVerificationCodeTextView.setText(String.valueOf(intervalTime));
		timer = new CountDownTimer(intervalTime, reduceTime) {

			@Override
			public void onTick(long millisUntilFinished) {
				getVerificationCodeTextView.setText(String.valueOf(millisUntilFinished / reduceTime));
			}

			@Override
			public void onFinish() {
				// 重新设为可点击状态
				getVerificationCodeTextView.setClickable(true);
				getVerificationCodeTextView.setTextColor(getResources().getColor(R.color.v16b8eb));
				getVerificationCodeTextView.setText(getResources().getString(R.string.get_validate_code));
			}
		};
		timer.start();
	}

	/**
	 * 获取手机验证码
	 */
	private void getVerificationCode(String mobile) {
		if (mobile.length() == 11) {
			UserManager.getInstance().getRegisterVerificationCode(mobile, new GetVerificationCodeResultCallback() {

				@Override
				public void result(String errMsg, int resultCode) {
					getVerificationCodeComplete(errMsg, resultCode);
				}
			});
		} else {
			Toast.makeText(RegisterActivity.this, R.string.phone_num_form_wrong, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 获取手机验证码请求完成
	 * 
	 * @param errMsg
	 * @param resultCode
	 */
	@UiThread
	void getVerificationCodeComplete(String errMsg, int resultCode) {
		if (errMsg != null) {
			if (resultCode == Define.SMS_ERR_CODE_OVER_SEND_COUNT || resultCode == Define.SMS_ERR_CODE_REGISTERED) {
				getVerificationCodeTextView.setClickable(true);
				getVerificationCodeTextView.setTextColor(getResources().getColor(R.color.v16b8eb));
				getVerificationCodeTextView.setText(getResources().getString(R.string.get_validate_code));
				timer.cancel();
			}
			Toast.makeText(RegisterActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 邮箱注册显示与隐藏密码
	 */
	@Click(R.id.iv_register_email_key_control)
	void onEmailPwdVisibilityControllerClick() {
		if (emailPasswdVisibilityController.getSelectFlag()) {
			emailPasswdVisibilityController.setSelectFlag(false);
			emailRegisterPdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			emailPasswdVisibilityController.setImageResource(R.drawable.login_key_invisiable);
		} else {
			emailPasswdVisibilityController.setSelectFlag(true);
			emailRegisterPdEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			emailPasswdVisibilityController.setImageResource(R.drawable.login_key_visiable);
		}

		String password = emailRegisterPdEditText.getText().toString().trim();
		if (!StringUtil.isEmpty(password)) {
			emailRegisterPdEditText.setSelection(password.length());
		}
	}

	/**
	 * 手机注册显示与隐藏密码
	 */
	@Click(R.id.iv_register_phone_key_control)
	void onPhonePwdVisibilityControllerClick() {
		if (phonePasswdVisibilityController.getSelectFlag()) {
			phonePasswdVisibilityController.setSelectFlag(false);
			phoneRegisterPdEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			phonePasswdVisibilityController.setImageResource(R.drawable.login_key_invisiable);
		} else {
			phonePasswdVisibilityController.setSelectFlag(true);
			phoneRegisterPdEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			phonePasswdVisibilityController.setImageResource(R.drawable.login_key_visiable);
		}

		String password = phoneRegisterPdEditText.getText().toString().trim();
		if (!StringUtil.isEmpty(password)) {
			phoneRegisterPdEditText.setSelection(password.length());
		}
	}

	/**
	 * 点击注册
	 */
	@Click(R.id.bt_register)
	void onRegisterClick() {
		int type = 0;
		String nickName = null; // 昵称
		String emailAddress = null; // 邮箱地址
		String password = null; // 密码
		String mobile = null; // 手机号码
		String verficationCode = null; // 手机验证码
		String toastTips = ""; // 提示信息

		if (isEmailRegister) {// 如果是邮箱注册
			// 昵称
			nickName = emailNickNamEditText.getText().toString().trim();
			// 邮箱地址
			emailAddress = emailEditText.getText().toString().trim();
			// 密码
			password = emailRegisterPdEditText.getText().toString().trim();

			/**
			 * 检查输入的信息格式
			 */
			if ("".equals(nickName)) {
				toastTips = StringUtil.getStringFromR(R.string.nickname_empty); // 昵称为空
				InputMethodUtils.showInputKeyBord(RegisterActivity.this, emailNickNamEditText);
			} else if (nickName.length() < 3) {
				toastTips = StringUtil.getStringFromR(R.string.nickname_length_too_short); // 昵称太短了
				InputMethodUtils.showInputKeyBord(RegisterActivity.this, emailNickNamEditText);
			} else if (!StringUtil.isEmail(emailAddress)) {
				toastTips = StringUtil.getStringFromR(R.string.email_format_wrong); // 邮箱格式错误
				InputMethodUtils.showInputKeyBord(RegisterActivity.this, emailEditText);
			} else if (password.length() == 0) {
				toastTips = StringUtil.getStringFromR(R.string.password_empty); // 密码为空
				InputMethodUtils.showInputKeyBord(RegisterActivity.this, emailRegisterPdEditText);
			} else if (password.length() < 6) {
				toastTips = StringUtil.getStringFromR(R.string.password_short); // 密码太短了
				InputMethodUtils.showInputKeyBord(RegisterActivity.this, emailRegisterPdEditText);
			}

		} else {// 如果是手机注册
			type = 1;
			// 昵称
			nickName = phoneNickNamEditText.getText().toString().trim();
			// 手机号码
			mobile = phoneNumEditText.getText().toString().trim();
			// 验证码
			verficationCode = verificationCodeEditText.getText().toString().trim();
			// 密码
			password = phoneRegisterPdEditText.getText().toString().trim();

			/**
			 * 检查输入的信息格式
			 */
			if ("".equals(nickName)) {
				toastTips = StringUtil.getStringFromR(R.string.nickname_empty); // 昵称为空
				InputMethodUtils.showInputKeyBord(RegisterActivity.this, phoneNickNamEditText);
			} else if (nickName.length() < 3) {
				toastTips = StringUtil.getStringFromR(R.string.nickname_length_too_short); // 昵称太短了
				InputMethodUtils.showInputKeyBord(RegisterActivity.this, phoneNickNamEditText);
			} else if (mobile.length() == 0) {
				toastTips = StringUtil.getStringFromR(R.string.phone_num_empty); // 手机号为空
				InputMethodUtils.showInputKeyBord(RegisterActivity.this, phoneNumEditText);
			} else if (mobile.length() != 11) {
				toastTips = StringUtil.getStringFromR(R.string.phone_num_form_wrong); // 手机号格式错误
				InputMethodUtils.showInputKeyBord(RegisterActivity.this, phoneNumEditText);
			} else if ("".equals(verficationCode)) {
				toastTips = StringUtil.getStringFromR(R.string.verification_code_empty); // 手机验证码为空
				InputMethodUtils.showInputKeyBord(RegisterActivity.this, verificationCodeEditText);
			} else if (password.length() == 0) {
				toastTips = StringUtil.getStringFromR(R.string.password_empty); // 密码为空
				InputMethodUtils.showInputKeyBord(RegisterActivity.this, phoneRegisterPdEditText);
			} else if (password.length() < 6) {
				toastTips = StringUtil.getStringFromR(R.string.password_short); // 密码太短了
				InputMethodUtils.showInputKeyBord(RegisterActivity.this, phoneRegisterPdEditText);
			}
		}

		if (!"".equals(toastTips)) {
			Toast.makeText(RegisterActivity.this, toastTips, Toast.LENGTH_SHORT).show();
		} else {
			UserManager.getInstance().userRegister(type, emailAddress, nickName, password, mobile, verficationCode, new StringResultCallback() {

				@Override
				public void result(String errMsg) {
					userRegisterComplete(errMsg);
				}
			});
		}
	}

	/**
	 * 用户注册请求完成
	 * 
	 * @param errMsg
	 */
	private void userRegisterComplete(String errMsg) {
		if (errMsg == null) {
			// 保存昵称到本地
			String userName = "";
			if (isEmailRegister) {
				userName = emailNickNamEditText.getText().toString().trim();
			} else {
				userName = phoneNickNamEditText.getText().toString().trim();
			}
			AppSetting.getInstance().saveStringPreferencesByKey(Define.CURRENT_USER_NAME, userName);

			/**
			 * 结束当前界面和跳转完善个人资料页
			 */
			Intent intent = new Intent(RegisterActivity.this, ImprovePersonDataActivity_.class);
			startActivity(intent);
			setResult(RESULT_OK);
			finish();
			Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(RegisterActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 点击用户协议
	 */
	@Click(R.id.tv_user_aggrement)
	void onRegisterAgreementClick() {
		Intent localWebIntent = new Intent(RegisterActivity.this, WebBrowserActivity_.class);
		localWebIntent.putExtra(Define.TITLE, "用户注册协议"); // 用户注册协议
		localWebIntent.putExtra(Define.TYPE, 3); // 用户注册协议
		startActivity(localWebIntent);
	}

}
