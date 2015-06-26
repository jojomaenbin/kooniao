package com.kooniao.travel.store;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.StringRes;

import android.content.Intent;
import android.text.InputType;
import android.view.View;
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
 * 更改店铺联系方式
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_single_input)
public class UpdateStoreContactPhoneActivity extends BaseActivity {

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

		contentCountTextView.setVisibility(View.INVISIBLE);
		inputEditText.setHint(R.string.input_store_contact_phone);
		inputEditText.setInputType(InputType.TYPE_CLASS_PHONE);
		titleTextView.setText(R.string.edit_contact_phone);
	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	@StringRes(R.string.update_store_contact_phone_success)
	String updateStoreContactPhoneSuccessTips; // 更改联系方式成功

	/**
	 * 保存操作
	 */
	@Click(R.id.tv_save)
	void onSaveClick() {
		String phone = inputEditText.getText().toString();
		if (!phone.isEmpty()) {
			StoreManager.getInstance().updateStoreContactPhone(sid, type, phone, new StringResultCallback() {

				@Override
				public void result(String errMsg) {
					if (errMsg == null) {
						isDataChange = true;
						onActivityFinish();
						Toast.makeText(UpdateStoreContactPhoneActivity.this, updateStoreContactPhoneSuccessTips, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(UpdateStoreContactPhoneActivity.this, errMsg, Toast.LENGTH_SHORT).show();
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
		intent.putExtra(Define.CONTACT_PHONE, inputEditText.getText().toString());
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * 清除输入框内容
	 */
	@Click(R.id.iv_clear)
	void onInputClearClick() {
		inputEditText.setText("");
	}

	@TextChange(R.id.et_input)
	void onTextChangesOnHelloTextView(CharSequence text, TextView textView, int before, int start, int count) {
		if (text.length() == 11) {
			saveTextView.setTextColor(textSaveColor);
			saveTextView.setClickable(true);
		} else {
			saveTextView.setTextColor(textHintColor);
			saveTextView.setClickable(false);
		}
	}

}
