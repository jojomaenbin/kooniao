package com.kooniao.travel.mine;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.StringRes;

import android.content.Intent;
import android.text.InputFilter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.manager.UserManager.StringResultCallback;

/**
 * 更改用户名称
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_single_input)
public class UpdateUserNameActivity extends BaseActivity {

	@ViewById(R.id.title)
	TextView titleTextView; // 标题
	@ViewById(R.id.tv_save)
	TextView saveTextView; // 保存
	@ViewById(R.id.et_input)
	EditText inputEditText; // 内容输入框
	@ViewById(R.id.tv_content_count)
	TextView contentCountTextView; // 内容字数统计
	@ColorRes(R.color.v909090)
	int textHintColor;
	@ColorRes(R.color.v16b8eb)
	int textSaveColor;

	@AfterViews
	void init() {
		initData();
		initView();
	}
	
	private String sexString; // 用户性别

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		maxInputCount = 10;
		inputCount = 0;
		Intent intent = getIntent();
		if (intent != null) {
			sexString = intent.getStringExtra(Define.SEX); 
		}
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		inputEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxInputCount) });
		inputEditText.setHint(R.string.input_nickname);
		titleTextView.setText(R.string.update_nickname);
		contentCountTextView.setText("0/" + maxInputCount);
	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	@StringRes(R.string.update_nickname_success)
	String updateNickNameSuccessTips; // 更改昵称成功

	private String name;

	/**
	 * 保存操作
	 */
	@Click(R.id.tv_save)
	void onSaveClick() {
		name = inputEditText.getText().toString();
		if (!name.isEmpty()) {
			UserManager.getInstance().updateNickName(name, sexString, new StringResultCallback() {

				@Override
				public void result(String errMsg) {
					if (errMsg == null) {
						activityFinish();
						Toast.makeText(UpdateUserNameActivity.this, updateNickNameSuccessTips, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(UpdateUserNameActivity.this, errMsg, Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}

	/**
	 * 结束当前activity
	 */
	private void activityFinish() {
		Intent intent = new Intent();
		intent.putExtra(Define.DATA, name);
		setResult(RESULT_OK, intent);
		finish();
	}

	private int maxInputCount; // 最大输入内容字数
	private int inputCount; // 已经输入内容字数

	/**
	 * 清除输入框内容
	 */
	@Click(R.id.iv_clear)
	void onInputClearClick() {
		inputEditText.setText("");
		inputCount = 0;
		contentCountTextView.setText(inputCount + "/" + maxInputCount);
	}

	@TextChange(R.id.et_input)
	void onTextChangesOnHelloTextView(CharSequence text, TextView textView, int before, int start, int count) {
		if (text.length() > 0) {
			saveTextView.setTextColor(textSaveColor);
			saveTextView.setClickable(true);
		} else {
			saveTextView.setTextColor(textHintColor);
			saveTextView.setClickable(false);
		}
		contentCountTextView.setText(text.length() + "/" + maxInputCount);
	}

}
