package com.kooniao.travel.user;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.SelectedImgeView;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.manager.UserManager.GetVerificationCodeResultCallback;
import com.kooniao.travel.utils.InputMethodUtils;
import com.kooniao.travel.utils.StringUtil;

/**
 * 电话忘记密码
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_forget_password_or_bound_phone)
public class ForgetPasswordByPhoneActivity extends BaseActivity { 
	@ViewById(R.id.et_phone_num)
	EditText phoneEditText; // 电话输入框
	@ViewById(R.id.et_verification_code)
	EditText verificationCodeEditText; // 验证码输入框
	@ViewById(R.id.tv_get_verification_code)
	TextView getVerificationCodeTextView; // 获取手机验证码
	@ViewById(R.id.et_phone_password)
	EditText passwordEditText; // 密码输入框
	@ViewById(R.id.iv_phone_key_control)
	SelectedImgeView selectedImgeView; // 是否显示明文密码

	@AfterViews
	void init() {
		initData();
		initView();
	}

	private String phoneNum; // 电话号码

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			phoneNum = intent.getStringExtra(Define.PHONE);
		}

		if (phoneNum != null) {
			startCountDownTimer();
		}
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		if (phoneNum != null) {
			phoneEditText.setText(phoneNum); 
			phoneEditText.setSelection(phoneNum.length()); 
			InputMethodUtils.showInputKeyBord(ForgetPasswordByPhoneActivity.this, verificationCodeEditText);
		}
	}

	final int intervalTime = 60 * 1000; // 获取验证码的间隔时间
	final int reduceTime = 1 * 1000; // 倒计时
	private CountDownTimer timer; // 倒计时

	/**
	 * 点击获取手机验证码
	 */
	@Click(R.id.tv_get_verification_code)
	void onGetVerificationCodeClick() {
		String mobile = phoneEditText.getText().toString().trim();
		String toastTips = "";
		if (mobile.length() == 0) {
			toastTips = StringUtil.getStringFromR(R.string.phone_num_empty); // 手机号为空
			InputMethodUtils.showInputKeyBord(ForgetPasswordByPhoneActivity.this, phoneEditText);
		} else if (mobile.length() != 11) {
			toastTips = StringUtil.getStringFromR(R.string.phone_num_form_wrong); // 手机号格式错误
			InputMethodUtils.showInputKeyBord(ForgetPasswordByPhoneActivity.this, phoneEditText);
		}

		if ("".equals(toastTips)) {
			// 开启倒计时
			startCountDownTimer();
			// 获取手机验证码
			getVerificationCode(mobile);
		} else {
			Toast.makeText(ForgetPasswordByPhoneActivity.this, toastTips, Toast.LENGTH_SHORT).show();
		}
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
			Toast.makeText(ForgetPasswordByPhoneActivity.this, R.string.phone_num_form_wrong, Toast.LENGTH_SHORT).show();
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
				timer.cancel();
			}
			Toast.makeText(ForgetPasswordByPhoneActivity.this, errMsg, Toast.LENGTH_SHORT).show();
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
	 * 手机注册显示与隐藏密码
	 */
	@Click(R.id.iv_register_phone_key_control)
	void onPhonePwdVisibilityControllerClick() {
		if (selectedImgeView.getSelectFlag()) {
			selectedImgeView.setSelectFlag(false);
			passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			selectedImgeView.setImageResource(R.drawable.login_key_invisiable);
		} else {
			selectedImgeView.setSelectFlag(true);
			passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			selectedImgeView.setImageResource(R.drawable.login_key_visiable);
		}

		String password = passwordEditText.getText().toString().trim();
		if (!StringUtil.isEmpty(password)) {
			passwordEditText.setSelection(password.length());
		}
	}

	/**
	 * 点击后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 点击完成按钮
	 */
	@Click(R.id.bt_finish)
	void onFinishButtonClick() {
		// 电话号码
		phoneNum = phoneEditText.getText().toString().trim();
		// 验证码
		String verificationCode = verificationCodeEditText.getText().toString().trim();
		// 密码
		String password = passwordEditText.getText().toString().trim();
		/**
		 * 检查输入的信息格式
		 */
		String toastTips = "";
		if (phoneNum.length() == 0) {
			toastTips = StringUtil.getStringFromR(R.string.phone_num_empty); // 手机号为空
			InputMethodUtils.showInputKeyBord(ForgetPasswordByPhoneActivity.this, phoneEditText);
		} else if (phoneNum.length() != 11) {
			toastTips = StringUtil.getStringFromR(R.string.phone_num_form_wrong); // 手机号格式错误
			InputMethodUtils.showInputKeyBord(ForgetPasswordByPhoneActivity.this, phoneEditText);
		} else if ("".equals(verificationCode)) {
			toastTips = StringUtil.getStringFromR(R.string.verification_code_empty); // 手机验证码为空
			InputMethodUtils.showInputKeyBord(ForgetPasswordByPhoneActivity.this, verificationCodeEditText);
		} else if (password.length() == 0) {
			toastTips = StringUtil.getStringFromR(R.string.password_empty); // 密码为空
			InputMethodUtils.showInputKeyBord(ForgetPasswordByPhoneActivity.this, passwordEditText);
		} else if (password.length() < 6) {
			toastTips = StringUtil.getStringFromR(R.string.password_short); // 密码太短了
			InputMethodUtils.showInputKeyBord(ForgetPasswordByPhoneActivity.this, passwordEditText);
		}
		if (!"".equals(toastTips)) {
			Toast.makeText(ForgetPasswordByPhoneActivity.this, toastTips, Toast.LENGTH_SHORT).show();
		} else {
			UserManager.getInstance().forgetPasswordByPhone(phoneNum, verificationCode, password, new UserManager.StringResultCallback() {

				@Override
				public void result(String errMsg) {
					if (errMsg == null) {
						setResult(RESULT_OK); 
						finish();
					} else {
						Toast.makeText(ForgetPasswordByPhoneActivity.this, errMsg, Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}

}
