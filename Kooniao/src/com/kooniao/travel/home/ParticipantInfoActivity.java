package com.kooniao.travel.home;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseFragmentActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.model.ParticipantInfo;
import com.kooniao.travel.utils.StringUtil;

/**
 * 参与人信息
 * 
 * @author zheng.zong.di
 * @date 2015年5月20日
 * @version 1.0
 *
 */
@SuppressLint("InflateParams")
@EActivity(R.layout.activity_paricipant_info)
public class ParticipantInfoActivity extends BaseFragmentActivity {
	@ViewById(R.id.ll_participant_info)
	LinearLayout participantInfoLayoutContainer;

	private PopupWindow certificateTypePopupWindow; // 证件类型
	private View certificateTypeLayout; // 证件类型
	private int currentparticipantAdultInfoIndex = 0; // 当前点击的参与人item位置
	private List<ParticipantInfo> participantInfoList;
	private int adultCount;

	@AfterViews
	void init() {
		initData();
		initParticipantInfoLayout();
		initPopupWindow();
	}

	/**
	 * 初始化数据
	 */
	@SuppressWarnings("unchecked")
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			participantInfoList = (List<ParticipantInfo>) intent.getSerializableExtra(Define.PARTICIPANTINFO);
			adultCount = intent.getIntExtra(Define.ADULT_COUNT, 0);
		}
	}

	private LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);; // 布局参数

	/**
	 * 初始化参与人信息布局
	 */
	private void initParticipantInfoLayout() {
		for (int i = 0; i < adultCount; i++) {

			final View participantAdultInfoLayout = LayoutInflater.from(ParticipantInfoActivity.this).inflate(R.layout.sub_participant_adult_info, null);
			participantAdultInfoLayout.setTag(i);
			TextView numText =  (TextView) participantAdultInfoLayout.findViewById(R.id.participant_num);
			numText.setText("参与人"+(i+1));
			participantAdultInfoLayout.findViewById(R.id.tv_type).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					currentparticipantAdultInfoIndex = (Integer) participantAdultInfoLayout.getTag();
					certificateTypePopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
				}
			});
			if (participantInfoList != null) {
				if (participantInfoList.get(i) != null) {
					// 参与人姓名
					EditText namEditText = (EditText) participantAdultInfoLayout.findViewById(R.id.et_participant_adult_name);
					namEditText.setText(participantInfoList.get(i).getName());
					// 手机号
					EditText mobileEditText = (EditText) participantAdultInfoLayout.findViewById(R.id.et_participant_adult_mobile);
					mobileEditText.setText(participantInfoList.get(i).getMobile());
					// 证件类型
					TextView typeTextView = (TextView) participantAdultInfoLayout.findViewById(R.id.tv_type);
					if (participantInfoList.get(i).getIdCardType() != null) {
						if (participantInfoList.get(i).equals("1")) {
							typeTextView.setText(R.string.certificate_type_id_card);
						}
						if (participantInfoList.get(i).equals("2")) {
							typeTextView.setText(R.string.certificate_type_passport);
						}
						if (participantInfoList.get(i).equals("3")) {
							typeTextView.setText(R.string.certificate_type_other);
						}

					}
					// 证件号码
					EditText certificateNumEditText = (EditText) participantAdultInfoLayout.findViewById(R.id.et_participant_certificate_num);
					certificateNumEditText.setText(participantInfoList.get(i).getIdCard());
				}
			}
			participantInfoLayoutContainer.addView(participantAdultInfoLayout, i, params);
		}

	}

	private void initPopupWindow() {
		certificateTypeLayout = LayoutInflater.from(ParticipantInfoActivity.this).inflate(R.layout.popup_update_certificate_type_select, null);
		certificateTypePopupWindow = new PopupWindow(certificateTypeLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		certificateTypePopupWindow.setFocusable(false);
		certificateTypePopupWindow.setAnimationStyle(R.style.PopupAnimationFromBottom);
		// 选择身份证
		certificateTypeLayout.findViewById(R.id.tv_certificate_type_id_card).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				certificateTypePopupWindow.dismiss();
				View participantAdultInfoLayout = participantInfoLayoutContainer.findViewWithTag(currentparticipantAdultInfoIndex);
				TextView typeTextView = (TextView) participantAdultInfoLayout.findViewById(R.id.tv_type);
				typeTextView.setText(R.string.certificate_type_id_card);
			}
		});
		// 选择护照
		certificateTypeLayout.findViewById(R.id.tv_certificate_type_passport).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				certificateTypePopupWindow.dismiss();
				View participantAdultInfoLayout = participantInfoLayoutContainer.findViewWithTag(currentparticipantAdultInfoIndex);
				TextView typeTextView = (TextView) participantAdultInfoLayout.findViewById(R.id.tv_type);
				typeTextView.setText(R.string.certificate_type_passport);
			}
		});
		// 选择其他
		certificateTypeLayout.findViewById(R.id.tv_certificate_type_other).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				certificateTypePopupWindow.dismiss();
				View participantAdultInfoLayout = participantInfoLayoutContainer.findViewWithTag(currentparticipantAdultInfoIndex);
				TextView typeTextView = (TextView) participantAdultInfoLayout.findViewById(R.id.tv_type);
				typeTextView.setText(R.string.certificate_type_other);
			}
		});
		// 关闭
		certificateTypeLayout.findViewById(R.id.iv_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				certificateTypePopupWindow.dismiss();
			}
		});
	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 确定按钮
	 */
	@Click(R.id.bt_accept)
	void onAcceptClick() {
		participantInfoList = new ArrayList<ParticipantInfo>();

		for (int i = 0; i < adultCount; i++) {
			ParticipantInfo adultInfo = new ParticipantInfo();
			View adultInfoView = participantInfoLayoutContainer.findViewWithTag(i);
			// 参与人姓名
			EditText namEditText = (EditText) adultInfoView.findViewById(R.id.et_participant_adult_name);
			String participantName = namEditText.getText().toString();
			adultInfo.setName(participantName);
			// 手机号
			EditText mobileEditText = (EditText) adultInfoView.findViewById(R.id.et_participant_adult_mobile);
			String participantMobile = mobileEditText.getText().toString();
			adultInfo.setMobile(participantMobile);
			// 证件类型
			TextView typeTextView = (TextView) adultInfoView.findViewById(R.id.tv_type);
			String type = typeTextView.getText().toString();
			String idCard = StringUtil.getStringFromR(R.string.certificate_type_id_card); // 身份证
			String passport = StringUtil.getStringFromR(R.string.certificate_type_passport); // 护照
			String others = StringUtil.getStringFromR(R.string.certificate_type_other); // 其他
			if (type.equals(idCard)) {
				type = "1";
			} else if (type.equals(passport)) {
				type = "2";
			} else if (type.equals(others)) {
				type = "3";
			}
			adultInfo.setIdCardType(type);
			// 证件号码
			EditText certificateNumEditText = (EditText) adultInfoView.findViewById(R.id.et_participant_certificate_num);
			String certificateNum = certificateNumEditText.getText().toString();
			adultInfo.setIdCard(certificateNum);
			if (!"".equals(adultInfo.toString())) {
				participantInfoList.add(adultInfo);
			}
		}
		Intent data = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable(Define.PARTICIPANTINFO, (Serializable) participantInfoList);
		data.putExtras(bundle);
		setResult(RESULT_OK, data);
		finish();

	}

}
