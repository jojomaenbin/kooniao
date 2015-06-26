package com.kooniao.travel.store;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.kooniao.travel.manager.StoreManager;
import com.kooniao.travel.manager.StoreManager.StringResultCallback;
import com.kooniao.travel.utils.AppSetting;

/**
 * 更改店铺名称
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_single_input)
public class UpdateStoreNameActivity extends BaseActivity {

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

	private int sid; // 店铺id
	private String type; // 店铺类型

	@AfterViews
	void init() {
		sid = AppSetting.getInstance().getIntPreferencesByKey(Define.SID);
		type = AppSetting.getInstance().getStringPreferencesByKey(Define.TYPE);

		maxInputCount = 30;
		inputCount = 0;
		inputEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxInputCount) });
		inputEditText.setHint(R.string.input_store_name);
		titleTextView.setText(R.string.edit_store_name);
		contentCountTextView.setText("0/" + maxInputCount);
	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	@StringRes(R.string.update_store_name_success)
	String updateStoreNameSuccessTips; // 更改名称成功

	/**
	 * 保存操作
	 */
	@Click(R.id.tv_save)
	void onSaveClick() {
		String name = inputEditText.getText().toString();
		if (!name.isEmpty()) {
			StoreManager.getInstance().updateStoreName(sid, type, name, new StringResultCallback() {

				@Override
				public void result(String errMsg) {
					if (errMsg == null) {
						isDataChange = true;
						onActivityFinish();
						Toast.makeText(UpdateStoreNameActivity.this, updateStoreNameSuccessTips, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(UpdateStoreNameActivity.this, errMsg, Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}

	private boolean isDataChange = false; // 数据是否发生了更改

	/**
	 * 结束当前activity
	 */
	private void onActivityFinish() {
		Intent intent = new Intent();
		intent.putExtra(Define.DATA, isDataChange);
		intent.putExtra(Define.SHOP_NAME, inputEditText.getText().toString());
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
	void onTextChangesOnStoreNameInput(CharSequence text, TextView textView, int before, int start, int count) {
		// 去掉特殊字符
		String regEx = "[^a-zA-Z0-9\u4e00-\u9fa5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(text.toString());
		String inputString = m.replaceAll("");
		String editable = inputEditText.getText().toString();
        if (!editable.equals(inputString)) {
        	text = inputString;
        	inputEditText.setText(inputString);
        	inputEditText.setSelection(inputString.length()); 
        }

		if (text.length() >= 1) {
			saveTextView.setTextColor(textSaveColor);
			saveTextView.setClickable(true);
		} else {
			saveTextView.setTextColor(textHintColor);
			saveTextView.setClickable(false);
		}
		contentCountTextView.setText(text.length() + "/" + maxInputCount);
	}

}
