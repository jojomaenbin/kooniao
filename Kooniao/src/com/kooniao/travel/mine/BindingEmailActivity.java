package com.kooniao.travel.mine;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.user.EmailResultActivity;
import com.kooniao.travel.user.EmailResultActivity_;
import com.kooniao.travel.utils.StringUtil;

/**
 * 绑定邮箱
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_email_and_phone_input)
public class BindingEmailActivity extends BaseActivity {
	@ViewById(R.id.title)
	TextView titleTextView; // 标题
	@ViewById(R.id.et_input)
	EditText inputEditText; // 输入框

	KooniaoProgressDialog progressDialog;

	@AfterViews
	void initView() {
		progressDialog = new KooniaoProgressDialog(BindingEmailActivity.this);
		titleTextView.setText(R.string.bound_email);
		inputEditText.setHint(R.string.hint_input_email);
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
			if (StringUtil.isEmail(input)) {
				if (!progressDialog.isShowing()) {
					progressDialog.show();
				}
				// 输入的内容是邮箱
				UserManager.getInstance().bindingEmail(input, stingResultCallback); 
			} else {
				Toast.makeText(BindingEmailActivity.this, R.string.email_format_wrong, Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(BindingEmailActivity.this, R.string.email_empty, Toast.LENGTH_SHORT).show();
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
				Intent intent = new Intent(BindingEmailActivity.this, EmailResultActivity_.class);
				String email = inputEditText.getText().toString().trim();
				intent.putExtra(Define.EMAIL_ADDRESS, email);
				intent.putExtra(Define.TYPE, EmailResultActivity.Type.BINDING_EMAIL.type);
				startActivity(intent);
				setResult(RESULT_OK);
				finish();
			} else {
				Toast.makeText(BindingEmailActivity.this, errMsg, Toast.LENGTH_SHORT).show();
			}
		}
	};

}
