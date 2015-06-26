package com.kooniao.travel.mine;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.kooniao.travel.R;
import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.api.ApiCaller.APIStringResultCallback;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.TravelManager;
import com.kooniao.travel.manager.TravelManager.StringResultCallback;

/**
 * 订单取消
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_order_cancel)
public class OrderCancelActivity extends BaseActivity implements OnClickListener {

	@ViewById(R.id.tv_writewrong)
	TextView writewrongTextView;
	@ViewById(R.id.tv_resubmit)
	TextView resubmitTextView;
	@ViewById(R.id.tv_no_buy_le)
	TextView nobuyTextView;
	@ViewById(R.id.tv_other)
	TextView otherTextView;
	@ViewById(R.id.et_order_review_content)
	EditText contentCounTextView; //

	@AfterViews
	void init() {
		initData();
		initView();
	}

	private int orderId; // 订单id
	private String reason; // 取消理由

	/**
	 * 初始化数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			orderId = intent.getIntExtra(Define.ORDER_ID, 0);
		}
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		writewrongTextView.setOnClickListener(this);
		resubmitTextView.setOnClickListener(this);
		nobuyTextView.setOnClickListener(this);
		otherTextView.setOnClickListener(this);
	}

	@TextChange(R.id.et_order_review_content)
	void onTextChangesOnStoreNameInput(CharSequence text, TextView textView, int before, int start, int count) {
		reason = contentCounTextView.getText().toString();
	}

	/**
	 * 点击后退
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 提交取消
	 */
	@Click(R.id.bt_order_review_commit)
	void onCommitClick() {
		commitOrderReview();
	}

	/**
	 * 提交取消
	 */
	private void commitOrderReview() {
		if (reason == null || reason.replace(" ", "").equals("")) {
			showToast("请完善取消原因！");
		} else {
			ApiCaller.getInstance().cancelOrder(orderId, reason, new APIStringResultCallback() {

				@Override
				public void result(String errMsg) {
					if (errMsg == null) {
						setResult(RESULT_OK);
						finish();
						showToast("订单取消提交成功");
					} else {
						showToast(errMsg);
					}
				}
			});
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_writewrong:
			writewrongTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.circle_selected, 0);
			resubmitTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			nobuyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			otherTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			contentCounTextView.setVisibility(View.GONE);
			reason=((TextView)v).getText().toString();
			break;
		case R.id.tv_resubmit:
			writewrongTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			resubmitTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.circle_selected, 0);
			nobuyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			otherTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			contentCounTextView.setVisibility(View.GONE);
			reason=((TextView)v).getText().toString();
			break;
		case R.id.tv_no_buy_le:
			writewrongTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			resubmitTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			nobuyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.circle_selected, 0);
			otherTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			contentCounTextView.setVisibility(View.GONE);
			reason=((TextView)v).getText().toString();
			break;
		case R.id.tv_other:
			writewrongTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			resubmitTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			nobuyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			otherTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.circle_selected, 0);
			contentCounTextView.setVisibility(View.VISIBLE);
			reason=contentCounTextView.getText().toString();
			break;

		default:
			break;
		}
	}

}
