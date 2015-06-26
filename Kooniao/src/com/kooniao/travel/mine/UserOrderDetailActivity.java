package com.kooniao.travel.mine;

import java.io.Serializable;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.api.ApiCaller.APIRemainPayResultCallback;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.home.CombineProductDetailActivity_;
import com.kooniao.travel.home.LineProductDetailActivity_;
import com.kooniao.travel.home.NonLineProductDetailActivity_;
import com.kooniao.travel.home.OrderPayActivity_;
import com.kooniao.travel.manager.OrderManager;
import com.kooniao.travel.manager.OrderManager.OrderDetailResultCallback;
import com.kooniao.travel.model.OrderDetail;
import com.kooniao.travel.model.ParticipantInfo;
import com.kooniao.travel.model.OrderDetail.PayMethod;
import com.kooniao.travel.store.StoreActivity_;
import com.kooniao.travel.utils.DateUtil;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.OrderUtil;
import com.kooniao.travel.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 订单详情页
 * 
 * @author ke.wei.quan
 * @date 2015年6月16日
 * @version 1.0
 *
 */
@EActivity(R.layout.activity_user_order_detail)
public class UserOrderDetailActivity extends BaseActivity {
	@ViewById(R.id.iv_store)
	ImageView storeIconImageView; // 店铺icon
	@ViewById(R.id.tv_order_status)
	TextView orderStatusTextView; // 订单状态
	@ViewById(R.id.sv_order_detail)
	ScrollView scrollView;
	@ViewById(R.id.tv_store_name)
	TextView orderResourceTextView; // 订单来源
	@ViewById(R.id.tv_order_count)
	TextView orderCountTextView; // 订单数量
	@ViewById(R.id.tv_order_price)
	TextView orderPriceTextView; // 订单价格
	@ViewById(R.id.tv_order_product_code)
	TextView productCodeTextView; // 产品编号
	@ViewById(R.id.tv_order_buy_time)
	TextView purchasingDateTextView; // 购买时间
	@ViewById(R.id.ll_order_people_count)
	LinearLayout peopleCountLayout; // 参与人布局
	@ViewById(R.id.tv_order_people_count)
	TextView pepopleCountTextView; // 参与人人数
	@ViewById(R.id.tv_contact)
	TextView contactNameTextView; // 联系人姓名
	@ViewById(R.id.tv_order_type)
	TextView orderTypeTextView; // 预定方式
	@ViewById(R.id.tv_combo_name)
	TextView combonameTextView; // 套餐名称
	@ViewById(R.id.tv_combo_price)
	TextView combopriceTextView; // 套餐单价
	@ViewById(R.id.iv_package_logo)
	ImageView comboImageView; // 套餐图片
	@ViewById(R.id.tv_combo_type)
	TextView combotypeTextView; // 套餐类型
	@ViewById(R.id.tv_store_name_tip)
	TextView storenameTextView; // 订单来源
	@ViewById(R.id.tv_pay_right_now)
	TextView payRightTextView; // 立即支付
	@ViewById(R.id.tv_buy_again)
	TextView buyAgainTextView; // 再次购买
	@ViewById(R.id.tv_pay_remain_now)
	TextView remainTextView; // 支付尾款
	@ViewById(R.id.tv_cancel_order)
	TextView cancelTextView; // 取消订单
	@ViewById(R.id.tv_evaluate)
	TextView evaluateTextView; // 评价

	/**
	 * 初始化
	 */
	@AfterViews
	void init() {
		initData();
		loadOrderDetail();
		initBottom();
	}

	private int orderId; // 订单id
	private String storeType; // 店铺类型
	boolean fromMyOrder;

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			orderId = intent.getIntExtra(Define.ORDER_ID, 0);
			storeType = intent.getStringExtra(Define.STORE_TYPE);
			fromMyOrder = intent.hasExtra(Define.ORDER_CODE);
		}
	}

	/**
	 * 加载订单详情
	 */
	private void loadOrderDetail() {
		showProgressDialog();
		OrderManager.getInstance().loadOrderDetail(orderId, new OrderDetailResultCallback() {

			@Override
			public void result(String errMsg, OrderDetail orderDetail) {
				loadOrderDetailComplete(errMsg, orderDetail);
			}
		});
	}

	private OrderDetail orderDetail; // 订单详细

	/**
	 * 加载订单详情请求完成
	 */
	@UiThread
	void loadOrderDetailComplete(String errMsg, OrderDetail orderDetail) {
		if (errMsg == null && orderDetail != null) {
			this.orderDetail = orderDetail;
			setViewInfo();
			initBottom();
			dissmissProgressDialog();
		} else {
			Toast.makeText(UserOrderDetailActivity.this, errMsg, Toast.LENGTH_SHORT).show();
			dissmissProgressDialog();
		}
	}

	/**
	 * 设置界面信息
	 */
	private void setViewInfo() {
		// 订单状态
		int orderStatus = orderDetail.getOrderStatus();
		// 订单状态处理信息
		String orderStatusTipsString = OrderUtil.getStatusTipsText(orderStatus);
		// 初始化联系人信息布局
		initContactInfoLayout();
		// 参与人信息
		initParticipantInfoLayout();
		/**
		 * 设置界面的显示与隐藏
		 */
		if ("c".equals(storeType)) { // 来自于c店
		} else { // 来自于a店
			int cShopId = orderDetail.getcShopId();
			if (cShopId != 0) { // 分销
				String cShopName = orderDetail.getcShopName();
				orderStatusTipsString = StringUtil.getStringFromR(R.string.order_come_from) + cShopName + StringUtil.getStringFromR(R.string.spread_shop);
				// 订单状态处理信息
				SpannableString spannableString = new SpannableString(orderStatusTipsString);
				ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.v16b8eb));
				spannableString.setSpan(foregroundColorSpan, 5, 5 + cShopName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else { // 自销
			}
		}

		// // 订单状态
		// String orderStatusString = StringUtil.getStringFromR(R.string.order)
		// + OrderUtil.getStatusTipsText(orderStatus);
		// orderStatusTextView.setText(orderStatusString);
		// 店铺logo
		ImageLoaderUtil.loadAvatar(ImageLoader.getInstance(), orderDetail.getaShopLogo(), storeIconImageView);
		// 产品图片
		ImageLoader.getInstance().displayImage(orderDetail.getProductLogo(), comboImageView);
		// 来源
		String resource = orderDetail.getaShopName();
		orderResourceTextView.setText(resource);
		storenameTextView.setText(resource);
		// 数量
		int count = orderDetail.getProductCount();
		orderCountTextView.setText("x" + count + "");
		// 预定方式
		orderTypeTextView.setText(orderDetail.getBookMethodes());
		// 订单价格
		float orderPrice = orderDetail.getDepositNum();
		if (orderPrice == 0) {
			orderPrice = orderDetail.getOrderCount();
			orderPriceTextView.setText(StringUtil.getStringFromR(R.string.rmb) + orderPrice);
			orderPrice = orderPrice / count;
		} else {
			orderPriceTextView.setText("全额：" + StringUtil.getStringFromR(R.string.rmb) + orderDetail.getOrderCount() + " 预付订金：" + StringUtil.getStringFromR(R.string.rmb) + orderPrice);
		}
		// 产品编号
		String productSku = orderDetail.getProductSku();
		if (productSku != null && !productSku.equals("")) {
			productCodeTextView.setText(productSku);
		}
		// else {
		// ((LinearLayout)
		// productCodeTextView.getParent()).setVisibility(View.GONE);
		// }
		// 套餐类型与价格
		combonameTextView.setText(orderDetail.getProductName());
		combopriceTextView.setText(StringUtil.getStringFromR(R.string.rmb) + orderDetail.getSalePrice());
		// 套餐类型
		combotypeTextView.setText(orderDetail.getPackageName());
		// 购买时间
		String buyDate = orderDetail.getOrderTime();
		purchasingDateTextView.setText(buyDate);
		// peopleCountLayout.setVisibility(View.GONE);// 出游人数布局
	}

	/**
	 * 初始化联系人信息布局
	 */
	private void initContactInfoLayout() {
		// 联系人姓名
		String contactName = orderDetail.getConractName();
		String conractMobile = orderDetail.getConractMobile();
		contactNameTextView.setText(contactName + "    " + conractMobile);
	}

	int participantLayoutVisiable;

	/**
	 * 初始化参与人信息布局
	 */
	private void initParticipantInfoLayout() {
		participantLayoutVisiable = 0;
		List<ParticipantInfo> participantInfos = orderDetail.getJoinerList();
		if (participantInfos.isEmpty() || participantInfos.toString().equals("")) {
			peopleCountLayout.setVisibility(View.GONE);
		} else {
			// 参与人信息是否可见
			for (int i = 0; i < participantInfos.size(); i++) {
				ParticipantInfo participantInfo = participantInfos.get(i);
				String name = participantInfo.getName();
				String mobile = participantInfo.getMobile();
				String certificateNum = participantInfo.getIdCard();
				if (name.equals("") && mobile.equals("") && certificateNum.equals("")) {
				} else {
					participantLayoutVisiable++;
				}
			}
			pepopleCountTextView.setText(participantLayoutVisiable + "人");
			if (participantLayoutVisiable == 0) {
				peopleCountLayout.setVisibility(View.GONE);
			} else {
				peopleCountLayout.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 后退
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
		if (!fromMyOrder) {
			Intent storeIntent = new Intent(UserOrderDetailActivity.this, BottomTabBarActivity_.class);
			storeIntent.putExtra(Define.TYPE, Define.HOME_PAGE);
			startActivity(storeIntent);
		} else {
			Intent intent = new Intent();
			intent.putExtra(Define.ORDER_STATUS, orderDetail.getOrderStatus());
			setResult(RESULT_OK, intent);
		}
		finish();
	}

	/**
	 * 卖家店铺
	 */
	@Click(R.id.ll_sell_store)
	void onSellStoreClick() {
		Intent storeIntent = new Intent(UserOrderDetailActivity.this, StoreActivity_.class);
		// 当前店铺id和类型
		int storeId = orderDetail.getaShopId();
		storeIntent.putExtra(Define.SID, storeId);
		storeIntent.putExtra(Define.STORE_TYPE, storeType);
		startActivity(storeIntent);
	}

	private List<ParticipantInfo> participantInfoList;// 参与人信息

	/**
	 * 参与人点击
	 */
	@Click(R.id.ll_order_people_count)
	void onPeopleClick() {
		participantInfoList = orderDetail.getJoinerList();
		Intent intent = new Intent(UserOrderDetailActivity.this, ParticipantInfoShowActivity_.class);
		Bundle data = new Bundle();
		data.putSerializable(Define.PARTICIPANTINFO, (Serializable) participantInfoList);
		data.putInt(Define.ADULT_COUNT, participantLayoutVisiable);
		intent.putExtras(data);
		startActivity(intent);
	}

	@StringRes(R.string.call)
	String dialogTitle; // 对话框标题
	Dialog dialog; // 拨打电话确认对话框

	/**
	 * 点击拨打电话
	 */
	@Click(R.id.ll_contact)
	void onMobileClick() {
		final String phoneNumber = orderDetail.getaShopMobile();
		if (!"".equals(phoneNumber)) {
			dialog = new Dialog(UserOrderDetailActivity.this, dialogTitle, phoneNumber);
			dialog.setCancelable(false);
			dialog.setOnAcceptButtonClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
			});
			dialog.setOnCancelButtonClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
		} else {
			Toast.makeText(UserOrderDetailActivity.this, R.string.customer_service_null, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 点击套餐信息
	 */
	@Click(R.id.rl_product_combo)
	void onComboClick() {
		int productType = orderDetail.getProductType();
		Intent productDetailIntent = null;
		if (productType == 2) {
			// 组合产品详情
			productDetailIntent = new Intent(UserOrderDetailActivity.this, CombineProductDetailActivity_.class);
		} else {
			if (productType == 4) {
				// 线路产品详情
				productDetailIntent = new Intent(UserOrderDetailActivity.this, LineProductDetailActivity_.class);
			} else {
				// 非线路产品详情
				productDetailIntent = new Intent(UserOrderDetailActivity.this, NonLineProductDetailActivity_.class);
			}
		}

		if (productDetailIntent != null) {
			productDetailIntent.putExtra(Define.PID, orderDetail.getProductId());
			productDetailIntent.putExtra(Define.STORE_TYPE, "a");
			productDetailIntent.putExtra(Define.SID, orderDetail.getaShopId());
			startActivity(productDetailIntent);
		}
	}

	/**
	 * 点击立即支付
	 */
	@Click(R.id.tv_pay_right_now)
	void onPayRightClick() {
		remainPay(REQUEST_CODE_PAY_RIGHT_NOW);
	}

	/**
	 * 点击再次购买
	 */
	@Click(R.id.tv_buy_again)
	void onBuyAgainClick() {
		onComboClick();
	}

	/**
	 * 点击支付尾款
	 */
	@Click(R.id.tv_pay_remain_now)
	void onPayRemainClick() {
		remainPay(REQUEST_CODE_PAY_BALANCE_NOW);
	}

	/**
	 * 点击取消订单
	 */
	@Click(R.id.tv_cancel_order)
	void onCancelClick() {
		Intent intent = new Intent(UserOrderDetailActivity.this, OrderCancelActivity_.class);
		intent.putExtra(Define.ORDER_ID, orderId);
		startActivityForResult(intent, REQUEST_CODE_CANCEL_ORDER);

	}

	/**
	 * 点击评价
	 */
	@Click(R.id.tv_evaluate)
	void onEvaluateClick() {
		Intent intent = new Intent(UserOrderDetailActivity.this, OrderReviewActivity_.class);
		intent.putExtra(Define.PRODUCT_TYPE, orderDetail.getProductType());
		intent.putExtra(Define.ORDER_ID, orderId);
		startActivity(intent); 
	}

	/**
	 * 更新支付凭证
	 */
	private void remainPay(final int requestCode) {
		showProgressDialog();
		OrderManager.getInstance().remainPay(orderId, new APIRemainPayResultCallback() {

			@Override
			public void result(String errMsg, int orderId, int invoiceId, float price) {
				dissmissProgressDialog();
				if (errMsg == null) {
					startOrderPayActivity(invoiceId, orderId, price, requestCode);
				} else {
					showToast(errMsg);
				}
			}
		});
	}

	/**
	 * 启动跳转到支付页面
	 * 
	 * @param invoiceId
	 * @param orderId
	 * @param price
	 * @param requestCode
	 */
	private void startOrderPayActivity(int invoiceId, int orderId, float price, int requestCode) {
		if (orderDetail != null) {
			Intent intent = new Intent(UserOrderDetailActivity.this, OrderPayActivity_.class);
			Bundle data = new Bundle();
			int isOffline = 1;
			List<PayMethod> payments = orderDetail.getPay_method();
			if (payments != null && !payments.isEmpty()) {
				if (payments.get(0).getId() == 2) {
					isOffline = 0;
				}
			}
			if (isOffline == 1) {
				// 线下支付
				data.putBoolean(Define.IS_OFFLINE, true);
			} else {
				// 线上支付
				data.putBoolean(Define.IS_OFFLINE, false);
			}
			data.putInt(Define.PID, orderDetail.getProductId());
			data.putInt(Define.SID, orderDetail.getaShopId());
			data.putString(Define.STORE_TYPE, storeType);
			String productName = orderDetail.getProductName();
			data.putString(Define.PRODUCT_NAME, productName);
			String sku = orderDetail.getProductSku();
			data.putString(Define.PRODUCT_SKU, sku);
			String productLogo = orderDetail.getProductLogo();
			data.putString(Define.PRODUCT_LOGO, productLogo);
			long dateStamp = DateUtil.strToTimestamp(orderDetail.getOrderTime(), Define.FORMAT_YMDHM);
			data.putLong(Define.DATE, dateStamp);
			String contactName = orderDetail.getConractName();
			data.putString(Define.CONTACTS_NAME, contactName);
			String contactMobile = orderDetail.getConractMobile();
			data.putString(Define.CONTACTS_MOBILE, contactMobile);
			String contactEmail = orderDetail.getConractEmail();
			data.putString(Define.CONTACTS_EMAIL, contactEmail);
			data.putInt(Define.ORDER_ID, orderId);
			data.putInt(Define.INVOICE_ID, invoiceId);
			int orderNum = Integer.parseInt(orderDetail.getOrderSn());
			data.putInt(Define.ORDER_CODE, orderNum);
			int productCount = Integer.parseInt(orderDetail.getTripsNumber());
			data.putInt(Define.PRODUCT_COUNT, productCount);
			float productPackagePrice = orderDetail.getSalePrice();
			data.putFloat(Define.PACKAGE_PRICE, productPackagePrice); // 套餐价格
			data.putFloat(Define.DEPOSIT, 0);
			String title = orderDetail.getPackageName();
			data.putString(Define.TITLE, title);
			data.putSerializable(Define.PAYMENT_LIST, (Serializable) payments);
			String wayReserve = orderDetail.getBookMethodes();
			if (wayReserve.equals(getResources().getString(R.string.reservation))) {
				data.putFloat(Define.DEPOSIT, price);
			}
			data.putString(Define.WAY_RESERVE, wayReserve);
			intent.putExtras(data);
			startActivityForResult(intent, requestCode);
		}
	}

	/**
	 * 底部显示判断
	 */
	private void initBottom() {
		payRightTextView.setVisibility(View.GONE);
		buyAgainTextView.setVisibility(View.GONE);
		remainTextView.setVisibility(View.GONE);
		cancelTextView.setVisibility(View.GONE);
		evaluateTextView.setVisibility(View.GONE);
		if (orderDetail != null) {
			if (orderDetail.getOrderStatus() == -1) {
				orderStatusTextView.setText("订单已取消");
				buyAgainTextView.setVisibility(View.VISIBLE);
			} else {
				if (orderDetail.getOrder_status() == 1) {
					if (fromMyOrder) {
						
					}
					orderStatusTextView.setText("订单未付款");
					payRightTextView.setVisibility(View.VISIBLE);
					cancelTextView.setVisibility(View.VISIBLE);
				} else if (orderDetail.getOrderStatus() == -2) {
					orderStatusTextView.setText("订单已关闭");
					buyAgainTextView.setVisibility(View.VISIBLE);
					evaluateTextView.setVisibility(View.VISIBLE);
				} else {
					orderStatusTextView.setText("订单已付款");
					if (orderDetail.getOrder_status() == 3) {
						buyAgainTextView.setVisibility(View.VISIBLE);
						cancelTextView.setVisibility(View.VISIBLE);
						remainTextView.setVisibility(View.VISIBLE);
					} else {
						buyAgainTextView.setVisibility(View.VISIBLE);
						cancelTextView.setVisibility(View.VISIBLE);
					}
				}
			}

		}
	}


	final int REQUEST_CODE_PAY_RIGHT_NOW = 1; // 立即支付
	final int REQUEST_CODE_PAY_BALANCE_NOW = 2; // 尾款支付
	final int REQUEST_CODE_CANCEL_ORDER = 11; // 取消订单

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_PAY_RIGHT_NOW:// 立即支付
				loadOrderDetail();
				break;
			case REQUEST_CODE_PAY_BALANCE_NOW:// 尾款支付
				loadOrderDetail();
				break;
			case REQUEST_CODE_CANCEL_ORDER:// 取消订单
				orderDetail.setOrderStatus(-1);
				initBottom();
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
