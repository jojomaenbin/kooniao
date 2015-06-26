package com.kooniao.travel.store;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.WebBrowserActivity_;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.StoreManager;
import com.kooniao.travel.manager.StoreManager.StringResultCallback;
import com.kooniao.travel.utils.AppSetting;

/**
 * 开店页面
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_open_store)
public class OpenStoreActivity extends BaseActivity {

	@ViewById(R.id.et_store_name)
	EditText storeNameEditText; // 店铺名称输入框
	@ViewById(R.id.et_store_contact_phone)
	EditText storeContactPhoneEditText; // 店铺联系电话输入框
	@ViewById(R.id.iv_check_box)
	ImageView checkAgreementImageView;
	@ViewById(R.id.tv_open_store)
	TextView openStoreTextView; // 开店按钮

	private int othersShopId; // 需要复制的店铺的id
	private String storeType = "a";

	/**
	 * 初始化界面数据
	 */
	@AfterViews
	void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			othersShopId = intent.getIntExtra(Define.SID, 0);
			storeType = intent.getStringExtra(Define.STORE_TYPE);
		}
	}

	/**
	 * 后退
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 点击开店
	 */
	@Click(R.id.tv_open_store)
	void onOpenStoreClick() {
		if (canOpenStore) {
			// 店铺名称
			String storeName = storeNameEditText.getText().toString().trim();
			// 店铺联系方式
			String contactPhone = storeContactPhoneEditText.getText().toString();
			if ("".equals(storeName)) {
				Toast.makeText(OpenStoreActivity.this, R.string.please_input_store_name, Toast.LENGTH_SHORT).show();
			} else if (contactPhone.length() != 11) {
				Toast.makeText(OpenStoreActivity.this, R.string.please_input_store_contact_phone, Toast.LENGTH_SHORT).show();
			} else {
				StoreManager.getInstance().openAStore(storeType, othersShopId, storeName, contactPhone, new StringResultCallback() {

					@Override
					public void result(String errMsg) {
						openStoreComplete(errMsg);
					}
				});
			}
		}
	}

	/**
	 * 开店请求完成
	 * 
	 * @param errMsg
	 */
	private void openStoreComplete(String errMsg) {
		if (errMsg == null) {
			AppSetting.getInstance().saveBooleanPreferencesByKey(Define.IS_FIRST_TIME_OPEN_STORE, true);
			Intent intent = new Intent(OpenStoreActivity.this, BottomTabBarActivity_.class);
			intent.putExtra(Define.TYPE, Define.STORE);
			startActivity(intent);
			Toast.makeText(OpenStoreActivity.this, R.string.open_store_success, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(OpenStoreActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	@TextChange(R.id.et_store_name)
	void onTextChangesOnStoreNameInput(CharSequence text, TextView textView, int before, int start, int count) {
		// 去掉特殊字符
			String regEx = "[^a-zA-Z0-9\u4e00-\u9fa5]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(text.toString());
			String inputString = m.replaceAll("");
			String editable = storeNameEditText.getText().toString();
	        if (!editable.equals(inputString)) {
	        	text = inputString;
	        	storeNameEditText.setText(inputString);
	        	storeNameEditText.setSelection(inputString.length()); 
	        }

		String phone = storeContactPhoneEditText.getText().toString(); // 店铺联系电话
		if (text.length() >= 1 && phone.length() == 11 ) {
			setOpenStoreButton(true);
		} else {
			setOpenStoreButton(false);
		}
			
	}

	@TextChange(R.id.et_store_contact_phone)
	void onTextChangesOnPhoneInput(CharSequence text, TextView textView, int before, int start, int count) {
		String storeName = storeNameEditText.getText().toString(); // 店铺名称
		if (text.length() == 11 && storeName.length() >= 1) {
			setOpenStoreButton(true);
		} else {
			setOpenStoreButton(false);
		}
	}

	private boolean isChecked = false;

	@Click(R.id.iv_check_box)
	void onAgreementCheckBoxClick() {
		if (isChecked) {
			isChecked = false;
			checkAgreementImageView.setImageResource(R.drawable.check_box_enable);
		} else {
			isChecked = true;
			checkAgreementImageView.setImageResource(R.drawable.check_box_able);
		}

		String storeName = storeNameEditText.getText().toString(); // 店铺名称
		String phone = storeContactPhoneEditText.getText().toString(); // 店铺联系电话
		if (storeName.length() >= 1 && phone.length() == 11 && isChecked) {
			setOpenStoreButton(true);
		} else {
			setOpenStoreButton(false);
		}
	}

	@Click(R.id.tv_open_store_agreement)
	void onAgreementClick() {
		Intent localWebIntent = new Intent(OpenStoreActivity.this, WebBrowserActivity_.class);
		localWebIntent.putExtra(Define.TITLE, "酷鸟店铺注册协议"); // 酷鸟店铺注册协议
		localWebIntent.putExtra(Define.TYPE, 11); // 酷鸟店铺注册协议
		startActivity(localWebIntent);
	}

	private boolean canOpenStore = false;

	/**
	 * 设置是否允许开店
	 * 
	 * @param isAble
	 */
	private void setOpenStoreButton(boolean isAble) {
		if (isAble) {
			canOpenStore = true;
			openStoreTextView.setBackgroundResource(R.drawable.green_retancle_button_selector);
		} else {
			canOpenStore = false;
			openStoreTextView.setBackgroundColor(getResources().getColor(R.color.vd0d0d0));
		}
	}
}
