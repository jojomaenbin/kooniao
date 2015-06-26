package com.kooniao.travel.store;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.text.format.Time;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.KooniaoDatePickerDialog;
import com.kooniao.travel.manager.StoreManager;
import com.kooniao.travel.manager.StoreManager.StringResultCallback;
import com.kooniao.travel.utils.DateUtil;
import com.kooniao.travel.utils.StringUtil;

/**
 * 支付佣金页面
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_pay_commission)
public class PayCommissionActivity extends BaseActivity {
	@ViewById(R.id.tv_pay_commission_date)
	TextView payCommissionDateTextView; // 支付日期
	@ViewById(R.id.tv_pay_commission_way)
	TextView payCommissionWayTextView; // 支付方式
	@ViewById(R.id.et_pay_commission_amount)
	EditText payCommissionAmountEditText; // 支付金额
	@ViewById(R.id.tv_pay_commission_residue)
	TextView payCommissionResidueTextView; // 剩余金额
	@ViewById(R.id.et_pay_commission_remark)
	EditText payCommissionRemarkEditText; // 支付备注
	@ViewById(R.id.tv_pay)
	TextView payCommissionTextView; // 支付按钮

	@AfterViews
	void init() {
		initData();
		initView();
	}

	private float brokerageCount; // 支付金额
	private int storeId; // 收款的店铺的id
	private String storeType; // 店铺类型
	
	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			brokerageCount = intent.getFloatExtra(Define.UNDEFRAYMONEY, 0);
			storeId = intent.getIntExtra(Define.SID, 0);
			storeType = intent.getStringExtra(Define.STORE_TYPE);
		}
	}

	/**
	 * 初始化界面
	 */
	@SuppressLint("SimpleDateFormat")
	private void initView() {
		// 设置日期
		SimpleDateFormat sdf = new SimpleDateFormat(Define.FORMAT_YMD);
		String date = sdf.format(new java.util.Date());
		payCommissionDateTextView.setText(date);
		// 设置剩余该支付金额
		setBrokerageCountText(brokerageCount);
	}

	/**
	 * 设置剩余该支付金额
	 */
	private void setBrokerageCountText(float brokerageCount) {
		unDefrayMoney = brokerageCount;
		String brokerageCountText = StringUtil.getStringFromR(R.string.rmb) + brokerageCount;
		payCommissionResidueTextView.setText(brokerageCountText);
	}

	private float unDefrayMoney; // 待支付金额
	private CharSequence payAmountCharSequence = "";

	/*
	 * 监听支付金额
	 */
	@TextChange(R.id.et_pay_commission_amount)
	void onPayAmountTextChanges(CharSequence text, TextView textView, int before, int start, int count) {
		payAmountCharSequence = text;
		// 设置剩余金额
		float brokerageCount = 0;
		if (text.length() > 0) {
			String input = text.toString().substring(0, 1);
			if (input.equals(".")) {
				text = payAmountCharSequence = "0" + payAmountCharSequence;
				payCommissionAmountEditText.setText(payAmountCharSequence); 
				payCommissionAmountEditText.setSelection(payAmountCharSequence.length()); 
			}
			brokerageCount = Float.parseFloat(payAmountCharSequence.toString());
			if (brokerageCount > this.brokerageCount) {
				text = text.subSequence(0, payAmountCharSequence.length() - 1);
				payAmountCharSequence = text;
				payCommissionAmountEditText.setText(text);
				payCommissionAmountEditText.setSelection(payAmountCharSequence.length());
				if (payAmountCharSequence.toString().length() > 0) {
					brokerageCount = Float.parseFloat(payAmountCharSequence.toString());
				} else {
					brokerageCount = 0;
				}
			}
			// 减数
			BigDecimal minuend = new BigDecimal(String.valueOf(this.brokerageCount));
			// 被减数
			BigDecimal subtractor = new BigDecimal(String.valueOf(brokerageCount));
			brokerageCount = minuend.subtract(subtractor).floatValue();
		} else {
			brokerageCount = this.brokerageCount;
		}
		setBrokerageCountText(brokerageCount);

		// 设置支付按钮状态
		if (payAmountCharSequence.length() > 0) {
			payCommissionTextView.setBackgroundResource(R.drawable.orange_retangle_selector);
			payCommissionTextView.setClickable(true);
		} else {
			payCommissionTextView.setBackgroundColor(getResources().getColor(R.color.vcfcfcf));
			payCommissionTextView.setClickable(false);
		}
	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		activityFinish();
	}
	
	@Override
	public void onBackPressed() {
		activityFinish();
		super.onBackPressed();
	}
	
	/**
	 * 结束当前页面
	 */
	private void activityFinish() {
		Intent intent = new Intent();
		intent.putExtra(Define.UNDEFRAYMONEY, unDefrayMoney);
		setResult(RESULT_OK, intent); 
		finish();
	}

	KooniaoDatePickerDialog datePickerDialog; // 设置日期对话框

	/**
	 * 日期选择
	 */
	@Click(R.id.tv_pay_commission_date)
	void onDateClick() {
		if (datePickerDialog == null) {
			final Time time = new Time();
			time.setToNow();
			datePickerDialog = new KooniaoDatePickerDialog(PayCommissionActivity.this, new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					String timeString = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
					payCommissionDateTextView.setText(timeString);
				}
			}, time.year, time.month, time.monthDay);
		}
		datePickerDialog.show();
	}

	@StringRes(R.string.payment_success)
	String paymentSuccessTips; // 支付成功提示

	/**
	 * 点击支付
	 */
	@Click(R.id.tv_pay)
	void onPayClick() {
		String payCommission = payCommissionAmountEditText.getText().toString();
		if (payCommission.length() > 0) {
			// 支付佣金
			float defrayMoney = Float.parseFloat(payCommission);
			// 支付备注
			String defrayRemark = payCommissionRemarkEditText.getText().toString();
			// 支付时间
			long currentDateStamp = System.currentTimeMillis() / 1000;
			String dateYMD = DateUtil.timeDistanceString(currentDateStamp, Define.FORMAT_YMD);
			long dateYMHHStamp = DateUtil.strToTimestamp(dateYMD, Define.FORMAT_YMD);
			String paymentTime = payCommissionDateTextView.getText().toString().trim();
			long paymentTimeStamp = DateUtil.strToTimestamp(paymentTime, Define.FORMAT_YMD) + (currentDateStamp - dateYMHHStamp);

			StoreManager.getInstance().brokerageDefray(storeId, defrayMoney, unDefrayMoney, defrayRemark, paymentTimeStamp, new StringResultCallback() {

				@Override
				public void result(String errMsg) {
					if (errMsg == null) {
						Intent intent = new Intent(PayCommissionActivity.this, CommissionDetailActivity_.class);
						intent.putExtra(Define.STORE_TYPE, storeType);
						intent.putExtra(Define.SID, storeId);
						startActivityForResult(intent, REQUEST_CODE_PAY); 
						Toast.makeText(PayCommissionActivity.this, paymentSuccessTips, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(PayCommissionActivity.this, errMsg, Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}
	
	final int REQUEST_CODE_PAY = 1; // 支付佣金

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_PAY: // 支付佣金
			if (resultCode == RESULT_OK && data != null) {
				float unDefrayMoney = data.getFloatExtra(Define.UNDEFRAYMONEY, -Integer.MAX_VALUE);
				if (unDefrayMoney != -Integer.MAX_VALUE) {
					this.unDefrayMoney = unDefrayMoney;
				}
				
				activityFinish();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
