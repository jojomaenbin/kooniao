package com.kooniao.travel.discovery;

import java.util.ArrayList;
import java.util.List;

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
import com.kooniao.travel.model.TeamCustomer;
import com.kooniao.travel.utils.InputMethodUtils;

/**
 * 发送信息编辑页面
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_mass_texting)
public class MassTextingActivity extends BaseActivity {
	@ViewById(R.id.tv_mass_texting_contact)
	TextView contactNameTextView; // 联系人
	@ViewById(R.id.et_message_content)
	EditText messageContentEditText; // 短信内容

	@AfterViews
	void init() {
		initData();
		initView();
	}

	private List<TeamCustomer> teamCustomerList = new ArrayList<TeamCustomer>();
	private String contactName;
	private String contactPhoneNum;

	@SuppressWarnings("unchecked")
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			teamCustomerList = (List<TeamCustomer>) intent.getSerializableExtra(Define.DATA);
		}

		if (teamCustomerList != null) {
			StringBuilder contactNameBuilder = new StringBuilder();
			StringBuilder contactPhoneBuilder = new StringBuilder();
			for (TeamCustomer teamCustomer : teamCustomerList) {
				contactNameBuilder.append(teamCustomer.getName() + "、");
				contactPhoneBuilder.append(teamCustomer.getTel() + ";");
			}
			contactName = contactNameBuilder.substring(0, contactNameBuilder.length() - 1);
			contactPhoneNum = contactPhoneBuilder.substring(0, contactNameBuilder.length() - 1);
		}
	}

	private void initView() {
		contactNameTextView.setText(contactName);
	}

	/**
	 * 取消
	 */
	@Click(R.id.tv_mass_texting_cancel)
	void onCancelClick() {
		finish();
	}

	/**
	 * 添加短信模板
	 */
	@Click(R.id.iv_add_message_template)
	void onAddTemplateClick() {
		Intent intent = new Intent(MassTextingActivity.this, MessageTemplateActivity_.class);
		startActivityForResult(intent, REQUEST_CODE_MESSAGE_TEMPLATE);
	}

	/**
	 * 调用系统发送短信
	 */
	@Click(R.id.tv_send_message)
	void onSendMessageClick() {
		String messageContent = messageContentEditText.getText().toString();
		if (messageContent.trim().equals("")) {
			InputMethodUtils.showInputKeyBord(MassTextingActivity.this, messageContentEditText);
			Toast.makeText(MassTextingActivity.this, "信息内容不能为空", Toast.LENGTH_SHORT).show();
		} else {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.putExtra("address", contactPhoneNum);
			intent.putExtra("sms_body", messageContent);
			intent.setType("vnd.android-dir/mms-sms");
			startActivity(intent);
		}
	}

	final int REQUEST_CODE_MESSAGE_TEMPLATE = 1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_MESSAGE_TEMPLATE:
			if (resultCode == RESULT_OK && data != null) {
				String messageContect = data.getStringExtra(Define.DATA);
				messageContentEditText.setText(messageContect);
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
