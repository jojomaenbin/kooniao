package com.kooniao.travel.home;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.model.ProductDetail;

/**
 * 选择定金类型
 * 
 * @author ke.wei.quan
 * @date 2015年4月29日
 *
 */
@EActivity(R.layout.activity_select_deposit_type)
public class DepositTypeActivity extends BaseActivity {

	@ViewById(R.id.ll_reservation)
	View reservationLayout;
	@ViewById(R.id.tv_reservation)
	TextView reservationTextView;
	@ViewById(R.id.tv_reservation_money)
	TextView reservationMoneyTextView; // 定金预定金额
	@ViewById(R.id.tv_reservation_tips)
	TextView reservationTipsTextView;

	@ViewById(R.id.ll_full)
	View fullLayout;
	@ViewById(R.id.tv_full)
	TextView fullTextView;
	@ViewById(R.id.tv_full_money)
	TextView fullMoneyTextView; // 全额预定金额
	@ViewById(R.id.tv_full_tips)
	TextView fullTipsTextView;

	private ProductDetail productDetail;
	private long dateStamp;
	private int count;
	private int productType;
	private int productId;
	private int storeId;
	private int priceId;
	private String storeType;
	private float productPackagePrice;
	private String deposit;
	private String title;
	private int depositType; // 定金类型
	private int depositPercent; // 定金百分比
	private int isPayDeposit = 0; // 是否是定金支付

	@AfterViews
	void initData() {
		Intent intent = getIntent();
		productDetail = (ProductDetail) intent.getSerializableExtra(Define.DATA);
		dateStamp = intent.getLongExtra(Define.DATE, 0);
		count = intent.getIntExtra(Define.ADULT_COUNT, 1);
		storeId = intent.getIntExtra(Define.SID, 0);
		productType = intent.getIntExtra(Define.PRODUCT_TYPE, 0);
		productId = intent.getIntExtra(Define.PID, 0);
		priceId = intent.getIntExtra(Define.PRICE_ID, 0);
		storeType = intent.getStringExtra(Define.STORE_TYPE);
		productPackagePrice = intent.getFloatExtra(Define.PACKAGE_PRICE, 0);
		deposit = intent.getStringExtra(Define.DEPOSIT);
		depositType = intent.getIntExtra(Define.DEPOSIT_TYPE, 1); // 定金类型(0:关闭,不设置定金类型
																	// 1:按金额启用
																	// 2:按百分比启用)
		depositPercent = intent.getIntExtra(Define.DEPOSIT_PERCENT, 100); // 定金百分比
		title = intent.getStringExtra(Define.TITLE);

		String depositStr = getResources().getString(R.string.rmb);
		// 点击预订金额
		if (depositType == 1) {
			reservationMoneyTextView.setText(depositStr + deposit);
		} else if (depositType == 2) {
			float deposiMoney = (productPackagePrice * count) * depositPercent / 100;
			deposit = String.valueOf(deposiMoney);
			reservationMoneyTextView.setText(depositStr + deposit);
		}

		// 全额预订金额
		String productPackagePriceStr = getResources().getString(R.string.rmb) + productPackagePrice * count;
		fullMoneyTextView.setText(productPackagePriceStr);
	}

	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 点击定金支付
	 */
	@Click(R.id.ll_reservation)
	void onReservationItemClick() {
		isPayDeposit = 1;

		// 清除之前的点击item效果
		fullTextView.setTextColor(getResources().getColor(R.color.book_text_selector));
		fullMoneyTextView.setTextColor(getResources().getColor(R.color.book_text_selector));
		fullTipsTextView.setTextColor(getResources().getColor(R.color.book_text_selector));
		fullLayout.setBackgroundResource(R.drawable.book_item_selector);

		// 设置按下
		reservationTextView.setTextColor(getResources().getColor(R.color.white));
		reservationMoneyTextView.setTextColor(getResources().getColor(R.color.white));
		reservationTipsTextView.setTextColor(getResources().getColor(R.color.white));
		reservationLayout.setBackgroundResource(R.drawable.blue_round_press_bg);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				submitSelectedProductPackage();
			}
		}, 200);
	}

	/**
	 * 点击全额支付
	 */
	@Click(R.id.ll_full)
	void onFullItemClick() {
		isPayDeposit = 0;

		// 清除之前的点击item效果
		reservationTextView.setTextColor(getResources().getColor(R.color.book_text_selector));
		reservationMoneyTextView.setTextColor(getResources().getColor(R.color.book_text_selector));
		reservationTipsTextView.setTextColor(getResources().getColor(R.color.book_text_selector));
		reservationLayout.setBackgroundResource(R.drawable.book_item_selector);

		// 设置按下
		fullTextView.setTextColor(getResources().getColor(R.color.white));
		fullMoneyTextView.setTextColor(getResources().getColor(R.color.white));
		fullTipsTextView.setTextColor(getResources().getColor(R.color.white));
		fullLayout.setBackgroundResource(R.drawable.blue_round_press_bg);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				submitSelectedProductPackage();
			}
		}, 200);
	}

	/**
	 * 提交套餐选择
	 * 
	 * @param count
	 */
	private void submitSelectedProductPackage() {
		Intent intent = null;
		intent = new Intent(getApplicationContext(), ProductBookingActivity_.class);
		intent.putExtra(Define.DATA, productDetail);
		intent.putExtra(Define.DATE, dateStamp); // 选择的日期
		intent.putExtra(Define.ADULT_COUNT, count); // 选择的套餐数
		intent.putExtra(Define.PID, productId); // 产品id
		intent.putExtra(Define.SID, storeId); // 店铺id
		intent.putExtra(Define.PRICE_ID, priceId); // 套餐id
		intent.putExtra(Define.STORE_TYPE, storeType); // 店铺类型
		intent.putExtra(Define.PACKAGE_PRICE, productPackagePrice); // 套餐价格
		intent.putExtra(Define.DEPOSIT, deposit); // 定金金额
		intent.putExtra(Define.TITLE, title); // 套餐名称
		intent.putExtra(Define.IS_PAYDEPOSIT, isPayDeposit); // 是否定金支付
		intent.putExtra(Define.PRODUCT_TYPE, productType); // 产品类型
		startActivityForResult(intent, REQUEST_CODE_SELECT_DEPOSIT_TYPE);
	}

	final int REQUEST_CODE_SELECT_DEPOSIT_TYPE = 11;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_SELECT_DEPOSIT_TYPE:
			if (resultCode == RESULT_OK) {
				setResult(RESULT_OK);
				finish();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
