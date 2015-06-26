package com.kooniao.travel.store;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.StoreManager;
import com.kooniao.travel.manager.StoreManager.DistributionTemplateResultCallback;
import com.kooniao.travel.model.DistributionTemplate;
import com.kooniao.travel.view.materialdesign.SwitchButton;

/**
 * 添加模板页
 * 
 * @author ke.wei.quan
 * @date 2015年5月27日
 * @lastModifyDate
 * @version 1.0
 *
 */
@EActivity(R.layout.activity_add_distribution_template)
public class AddDistributionActivity extends BaseActivity {
	@ViewById(R.id.et_template_title)
	EditText templateTitleEditText; // 模板标题输入框
	@ViewById(R.id.ll_according_money)
	View accordingMoneyView; // 按金额
	@ViewById(R.id.tv_according_money)
	TextView accordingMoneyTextView; // 按金额
	@ViewById(R.id.ll_according_percent)
	View accordingPercentView; // 按百分比
	@ViewById(R.id.tv_according_percent)
	TextView accordingPercentTextView; // 按百分比
	@ViewById(R.id.et_commission_sum)
	EditText commissionSumEditText; // 佣金金额
	@ViewById(R.id.sb_default_template)
	SwitchButton defaultTemplateSwitchButton; // 默认模板开关
	@ViewById(R.id.et_commission_remark)
	EditText commissionRemarkEditText; // 备注信息
	
	@AfterViews
	void initView() {
		// 默认选中按金额
		accordingMoneyView.performClick();
	}
	
	/**
	 * 点击后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGobackClick() {
		finish();
	}
	
	/**
	 * 点击提交按钮
	 */
	@Click(R.id.tv_finish)
	void onCommitClick() {
		// 标题
		final String title = templateTitleEditText.getText().toString();
		// 佣金类型(0:按金额，1:按比例)
		final int type = accordingMoneyView.isSelected() ? 0 : 1;
		// 产品佣金金额
		final String value = commissionSumEditText.getText().toString();
		// 默认模板
		final int defaultTemplate = defaultTemplateSwitchButton.isChecked() ? 1 : 0;
		// 备注信息
		final String remark = commissionRemarkEditText.getText().toString();
		
		if (title.equals("")) { // 模板标题为空
			showToast(templateTitleEditText.getHint());
		} else if (value.equals("")) { // 佣金金额为空
			showToast(commissionSumEditText.getHint());
		} else {
			showProgressDialog();
			StoreManager.getInstance().addDistributionTemplate(title, type, value, defaultTemplate, remark, new DistributionTemplateResultCallback() {
				
				@Override
				public void result(String errMsg, int templateId) {
					dissmissProgressDialog();
					if (errMsg == null) {
						DistributionTemplate template = new DistributionTemplate();
						template.setDefaultTemplate(defaultTemplate); 
						template.setId(templateId);
						template.setTitle(title);
						template.setType(type);
						template.setValue(Float.valueOf(value));   
						
						// 回调数据
						Intent data = new Intent();
						Bundle extras = new Bundle();
						extras.putSerializable(Define.DATA, template); 
						data.putExtras(extras);
						setResult(RESULT_OK, data);
						finish();
					} else {
						showToast(errMsg);
					}
				}
			}); 
		}
	}
	
	/**
	 * 点击按金额
	 */
	@Click(R.id.ll_according_money)
	void onAccordingMoneyClick() {
		accordingMoneyTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_blue, 0, 0, 0);
		accordingMoneyView.setSelected(true);
		accordingMoneyTextView.setTextColor(getResources().getColor(R.color.v16b8eb));

		accordingPercentTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		accordingPercentView.setSelected(false);
		accordingPercentTextView.setTextColor(getResources().getColor(R.color.vd0d0d0));
	}

	/**
	 * 点击按比例
	 */
	@Click(R.id.ll_according_percent)
	void onAccordingPercentClick() {
		accordingMoneyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		accordingMoneyView.setSelected(false);
		accordingMoneyTextView.setTextColor(getResources().getColor(R.color.vd0d0d0));

		accordingPercentTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_blue, 0, 0, 0);
		accordingPercentView.setSelected(true);
		accordingPercentTextView.setTextColor(getResources().getColor(R.color.v16b8eb));
	}
}
