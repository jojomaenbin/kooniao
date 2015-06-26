package com.kooniao.travel.user;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.utils.StringUtil;

/**
 * 忘记密码
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_email_and_phone_input)
public class ForgetPasswordActivity extends BaseActivity {
	@ViewById(R.id.et_input)
	EditText inputEditText; // 输入框

	KooniaoProgressDialog progressDialog;

	@AfterViews
	void initView() {
		progressDialog = new KooniaoProgressDialog(ForgetPasswordActivity.this);
	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 清除输入框按钮
	 */
	@Click(R.id.iv_clear)
	void onClearClick() {
		inputEditText.setText("");
	}

	/**
	 * 点击下一步
	 */
	@Click(R.id.tv_next_step)
	void onNextStepClick() {
		// 输入内容
		String input = inputEditText.getText().toString().trim();
		if (!StringUtil.isEmpty(input)) {
			if (!progressDialog.isShowing()) {
				progressDialog.show();
			}

			if (StringUtil.isEmail(input)) {
				// 输入的内容是邮箱
				UserManager.getInstance().forgetPasswordByEmail(input, stingResultCallback);
			} else {
				// 输入的内容是电话号码
				UserManager.getInstance().getFindPasswdVerificationCode(input, getVerificationCodeResultCallback);
			}
		}
	}

	/**
	 * 邮箱接口回调
	 */
	UserManager.StringResultCallback stingResultCallback = new UserManager.StringResultCallback() {

		@Override
		public void result(String errMsg) {
			progressDialog.dismiss();
			if (errMsg == null) {
				Intent intent = new Intent(ForgetPasswordActivity.this, EmailResultActivity_.class);
				String email = inputEditText.getText().toString().trim();
				intent.putExtra(Define.EMAIL_ADDRESS, email);
				intent.putExtra(Define.TYPE, EmailResultActivity.Type.FORGET_PASSWORD.type);
				startActivity(intent);
				finish();
			} else {
				Toast.makeText(ForgetPasswordActivity.this, errMsg, Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * 手机接口回调
	 */
	UserManager.GetVerificationCodeResultCallback getVerificationCodeResultCallback = new UserManager.GetVerificationCodeResultCallback() {

		@Override
		public void result(String errMsg, int resultCode) {
			progressDialog.dismiss();
			if (errMsg == null) {
				Intent intent = new Intent(ForgetPasswordActivity.this, ForgetPasswordByPhoneActivity_.class);
				String phone = inputEditText.getText().toString().trim();
				intent.putExtra(Define.PHONE, phone);
				startActivityForResult(intent, REQUEST_CODE_FORGET_PASSWORD_PHONE); 
			} else {
				Toast.makeText(ForgetPasswordActivity.this, errMsg, Toast.LENGTH_SHORT).show();
			}
		}
	};

	final int REQUEST_CODE_FORGET_PASSWORD_PHONE = 11; // 手机忘记密码 
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			finish(); 
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
