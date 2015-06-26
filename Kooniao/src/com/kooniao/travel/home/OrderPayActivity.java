package com.kooniao.travel.home;

import java.io.Serializable;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.OrderManager;
import com.kooniao.travel.manager.OrderManager.OrderPayResultCallback;
import com.kooniao.travel.mine.UserOrderDetailActivity_;
import com.kooniao.travel.model.PayResult;
import com.kooniao.travel.model.Payment;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 支付订单页
 * 
 * @author ke.wei.quan
 * @date 2015年6月16日
 * @version 1.0
 *
 */
@EActivity(R.layout.activity_order_pay)
public class OrderPayActivity extends BaseActivity {

	@ViewById(R.id.tv_order_status)
	TextView orderStatusTextView; // 订单提交状态
	@ViewById(R.id.tv_order_num)
	TextView orderNumTextView; // 订单编号
	@ViewById(R.id.iv_package_logo)
	ImageView packageCoverImageView; // 套餐封面
	@ViewById(R.id.tv_package_title)
	TextView packageTitleTextView; // 套餐名称
	@ViewById(R.id.tv_package_type)
	TextView packageTypeTextView; // 套餐类型
	@ViewById(R.id.tv_payment_type)
	TextView paymentTypeTextView; // 支付类型
	@ViewById(R.id.tv_should_pay_sum)
	TextView shouldPaySumTextView; // 应付金额
	@ViewById(R.id.iv_pay_way_logo)
	ImageView patWayImageView; // 支付方式logo
	@ViewById(R.id.tv_pay_way)
	TextView paywayTextView; // 支付方式文本

	@AfterViews
	void init() {
		initData();
		initView();
	}

	private int productId; // 产品id
	private int storeId; // 店铺id
	private String storeType; // 店铺类型
	private String productName; // 产品名
	private String productSku; // 产品编号
	private String productLogo; // 产品logo
	private long dateStamp; // 日期时间戳
	private String contactName; // 联系人名字
	private String contactMobile; // 联系人电话
	private String contactEmail; // 联系人邮箱
	private int orderId; // 订单id
	private int invoiceId; // 凭证id
	private int orderNum; // 订单编号
	private int peopleCount; // 人数
	private float productPackagePrice; // 套餐单价
	private String title; // 套餐名
	private float deposit; // 套餐总价
	private String wayReserve; // 预订方式(全额，部分)
	private List<Payment> payments; // 支付方式
	private boolean isOffline; // 支付平台标识

	/**
	 * 初始化数据
	 */
	@SuppressWarnings("unchecked")
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			productId = intent.getIntExtra(Define.PID, 0);
			storeId = intent.getIntExtra(Define.SID, 0);
			storeType = intent.getStringExtra(Define.STORE_TYPE);
			productName = intent.getStringExtra(Define.PRODUCT_NAME);
			productSku = intent.getStringExtra(Define.PRODUCT_SKU);
			productLogo = intent.getStringExtra(Define.PRODUCT_LOGO);
			dateStamp = intent.getLongExtra(Define.DATE, 0);
			contactName = intent.getStringExtra(Define.CONTACTS_NAME);
			contactMobile = intent.getStringExtra(Define.CONTACTS_MOBILE);
			contactEmail = intent.getStringExtra(Define.CONTACTS_EMAIL);
			invoiceId = intent.getIntExtra(Define.INVOICE_ID, 0);
			orderId = intent.getIntExtra(Define.ORDER_ID, 0);
			orderNum = intent.getIntExtra(Define.ORDER_CODE, 0);
			peopleCount = intent.getIntExtra(Define.PRODUCT_COUNT, 0);
			productPackagePrice = intent.getFloatExtra(Define.PACKAGE_PRICE, 0);
			title = intent.getStringExtra(Define.TITLE);
			deposit = intent.getFloatExtra(Define.DEPOSIT, 0);
			wayReserve = intent.getStringExtra(Define.WAY_RESERVE);
			payments = (List<Payment>) intent.getSerializableExtra(Define.PAYMENT_LIST);
			isOffline = intent.getBooleanExtra(Define.IS_OFFLINE, true);
		}
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 订单号
		orderNumTextView.setText(orderNumTextView.getText().toString() + orderNum);
		// 加载套餐封面
		if (productLogo != null) {
			ImageLoaderUtil.loadListCoverImg(ImageLoader.getInstance(), productLogo, packageCoverImageView);
		}
		if (productName != null) {
			// 产品名
			packageTitleTextView.setText(productName); 
		}
		// 套餐类型
		packageTypeTextView.setText(packageTypeTextView.getText().toString() + title);
		// 支付类型
		paymentTypeTextView.setText(wayReserve);
		// 应付金额
		if (wayReserve.equals(getResources().getString(R.string.full_booking))) {
			shouldPaySumTextView.setText(StringUtil.getStringFromR(R.string.rmb) + (productPackagePrice * peopleCount));
		} else {
			shouldPaySumTextView.setText(deposit + "");
		}

		if (isOffline) {
			patWayImageView.setImageResource(R.drawable.offline_pay);
			paywayTextView.setText("线下支付");
		}
	}

	/**
	 * 点击后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 点击立即支付
	 */
	@Click(R.id.tv_pay_right_now)
	void onPayRightNowClick() {
		if (isOffline) {
			orderPaySuccess();
		} else {
			showProgressDialog();
			int payType = 0;
			if (payments != null) {
				for (Payment payment : payments) {
					if (payment.getCode().equals("alipay")) {
						payType = payment.getId();
					}
				}
			}
			OrderManager.getInstance().orderPay(orderId, invoiceId, payType, new OrderPayResultCallback() {

				@Override
				public void result(String errMsg, String payInfo) {
					dissmissProgressDialog();
					if (errMsg == null) {
						callAlipay(payInfo);
					} else {
						showToast(errMsg);
					}
				}
			});
		}
	}

	/**
	 * 调用支付宝
	 * 
	 * @param payInfo
	 */
	@Background
	void callAlipay(String payInfo) {
		// 构造PayTask 对象
		PayTask alipay = new PayTask(OrderPayActivity.this);
		// 调用支付接口，获取支付结果
		String result = alipay.pay(payInfo);
		Message msg = Message.obtain();
		msg.what = SDK_PAY_FLAG;
		msg.obj = result;
		mHandler.sendMessage(msg);
	}

	// 支付宝回调
	private static final int SDK_PAY_FLAG = 1;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					orderPaySuccess();
					showToast("支付成功");
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (!TextUtils.equals(resultStatus, "8000")) {
						Intent intent = new Intent(OrderPayActivity.this, UserOrderDetailActivity_.class);
						intent.putExtra(Define.ORDER_ID, orderId);
						intent.putExtra(Define.STORE_TYPE, storeType);
						startActivity(intent);
					}
				}
				break;
			}
			default:
				break;
			}
		};
	};

	/**
	 * 支付成功
	 */
	private void orderPaySuccess() {
		Intent intent = new Intent(OrderPayActivity.this, ProductBookingCompleteActivity_.class);
		Bundle data = new Bundle();
		data.putInt(Define.PID, productId);
		data.putInt(Define.SID, storeId);
		data.putString(Define.STORE_TYPE, storeType);
		data.putString(Define.PRODUCT_NAME, productName);
		data.putString(Define.PRODUCT_SKU, productSku);
		data.putString(Define.PRODUCT_LOGO, productLogo);
		data.putLong(Define.DATE, dateStamp);
		data.putString(Define.CONTACTS_NAME, contactName);
		data.putString(Define.CONTACTS_MOBILE, contactMobile);
		data.putString(Define.CONTACTS_EMAIL, contactEmail);
		data.putInt(Define.ORDER_ID, orderId);
		data.putInt(Define.INVOICE_ID, invoiceId);
		data.putInt(Define.ORDER_CODE, orderNum);
		data.putInt(Define.PRODUCT_COUNT, peopleCount);
		data.putFloat(Define.PACKAGE_PRICE, productPackagePrice); // 套餐价格
		data.putFloat(Define.DEPOSIT, 0);
		data.putString(Define.TITLE, title);
		data.putSerializable(Define.PAYMENT_LIST, (Serializable) payments);
		if (!TextUtils.equals(wayReserve, getResources().getString(R.string.full_booking))) {
			data.putFloat(Define.DEPOSIT, Float.valueOf(deposit)); // 套餐价格
		}
		data.putString(Define.WAY_RESERVE, wayReserve);
		intent.putExtras(data);
		startActivity(intent);
		setResult(RESULT_OK);
		finish();
	}

	@Override
	protected void onDestroy() {
		mHandler.removeCallbacksAndMessages(null);
		super.onDestroy();
	}

}
