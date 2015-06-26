package com.kooniao.travel.home;

import java.io.Serializable;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.StoreManager;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.manager.StoreManager.ReserveLineProductResultCallback;
import com.kooniao.travel.model.ParticipantInfo;
import com.kooniao.travel.model.Payment;
import com.kooniao.travel.model.ProductDetail;
import com.kooniao.travel.model.UserInfo;
import com.kooniao.travel.user.LoginActivity_;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.DateUtil;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.JsonTools;
import com.kooniao.travel.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 产品预订
 * 
 * @author ke.wei.quan
 * 
 */
@SuppressLint("InflateParams")
@EActivity(R.layout.activity_product_booking)
public class ProductBookingActivity extends BaseActivity {

	@ViewById(R.id.tv_product_name)
	TextView productNameTextView; // 产品名
	@ViewById(R.id.tv_store_name)
	TextView storeNameTextView; // 来源（店铺名）
	@ViewById(R.id.tv_product_code)
	TextView productCodeTextView; // 产品编号
	@ViewById(R.id.tv_starting)
	TextView startingTextView; // 出发日期
	@ViewById(R.id.tv_num_of_travel)
	TextView numOfTravelTextView; // 出游人数
	@ViewById(R.id.tv_order_count)
	TextView orderCountTextView; // 订单数量
	@ViewById(R.id.tv_order_price)
	TextView orderPriceTextView; // 订单价格
	@ViewById(R.id.et_name)
	EditText contactsNameEditText; // 联系人姓名
	@ViewById(R.id.et_mobile)
	EditText contactsMobileEditText; // 联系人电话
	@ViewById(R.id.et_email)
	EditText contactsEmailEditText; // 联系人邮箱
	@ViewById(R.id.ll_participant_info)
	LinearLayout participantInfoLayoutContainer;
	@ViewById(R.id.tv_total_money)
	TextView totalMoneyTextView; // 总价
	@ViewById(R.id.sv_product_booking_info)
	ScrollView scrollView;
	@ViewById(R.id.tv_contact)
	TextView participanTextView; // 参与人
	@ViewById(R.id.tv_input_participant)
	TextView inputParticipanTextView; // 填写参与人信息
	@ViewById(R.id.tv_order_type)
	TextView orderTypeTextView; // 预定类型
	@ViewById(R.id.product_total_money)
	TextView orderMoneyTextView; // 预定类型
	@ViewById(R.id.tv_combo_name)
	TextView combonameTextView; // 套餐名称
	@ViewById(R.id.shop_bottom)
	TextView shopbottomTextView; // 店铺名字
	@ViewById(R.id.shop_bottom_logo)
	ImageView shopbottomlogoView; // 店铺底栏logo
	@ViewById(R.id.shop_bottom_layout)
	LinearLayout shopbottomlayout; // 店铺底栏
	@ViewById(R.id.tv_way_reserve)
	TextView reserveWayTextView; // 预定方式
	@ViewById(R.id.tv_combo_name)
	TextView tvcombonameTextView; // 套餐名称
	@ViewById(R.id.tv_order_price)
	TextView tvcombopriceTextView; // 套餐价格
	@ViewById(R.id.iv_product_cover_img)
	ImageView productCoverImg; // 产品封面
	@ViewById(R.id.seller_message)
	EditText sellermessage; // 产品封面
	

	@AfterViews
	void init() {
		initData();
		initView();
	}

	private UserInfo currentUserInfo; // 当前登录用户
	private ProductDetail productDetail; // 产品详情
	private long dateStamp; // 日期时间戳
	private int adultCount; // 成人数量
	private int childCount = 0; // 儿童数量
	private int storeId; // 店铺id
	private int productId; // 产品id
	private String storeType; // 店铺类型
	private int priceId;// 套餐id
	private String deposit;// 定金金额
	private int isPayDeposit; // 是否是定金支付
	private float productPackagePrice;// 套餐单价
	private String title;
	private String message;//卖家留言

	/**
	 * 初始化数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			productDetail = (ProductDetail) intent.getSerializableExtra(Define.DATA);
			dateStamp = intent.getLongExtra(Define.DATE, 0);
			adultCount = intent.getIntExtra(Define.ADULT_COUNT, 0);// 选择的套餐数
			storeId = intent.getIntExtra(Define.SID, 0); // 店铺id
			productId = intent.getIntExtra(Define.PID, 0);// 产品id
			storeType = intent.getStringExtra(Define.STORE_TYPE);// 店铺类型
			priceId = intent.getIntExtra(Define.PRICE_ID, 0);
			deposit = intent.getStringExtra(Define.DEPOSIT);
			isPayDeposit = intent.getIntExtra(Define.IS_PAYDEPOSIT, 0);
			productPackagePrice = intent.getFloatExtra(Define.PACKAGE_PRICE, 0);
			title = intent.getStringExtra(Define.TITLE);
		}

		currentUserInfo = UserManager.getInstance().getCurrentUserInfo();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		if (productDetail != null) {
			// 套餐信息
			tvcombonameTextView.setText(title);
			tvcombopriceTextView.setText(StringUtil.getStringFromR(R.string.rmb) + productPackagePrice);
			// 产品名
			String productName = productDetail.getProductName();
			productNameTextView.setText(productName);

			// 订单价格
			String priceString = StringUtil.getStringFromR(R.string.rmb) + productPackagePrice;
			orderPriceTextView.setText(priceString);
			if (isPayDeposit == 0) {
				String priceString2 = StringUtil.getStringFromR(R.string.rmb) + productPackagePrice * adultCount;
				totalMoneyTextView.setText(priceString2);
			}
			if (isPayDeposit == 1) {
				String priceString2 = StringUtil.getStringFromR(R.string.rmb) + Float.valueOf(deposit);
				totalMoneyTextView.setText(priceString2);
			}

			// 产品套餐封面
			String coverImgUrl = productDetail.getImg();
			ImageLoaderUtil.loadListCoverImg(ImageLoader.getInstance(), coverImgUrl, productCoverImg);
			// 设置店铺logo和来源
			if (productDetail.getShopLogo() != null) {
				ImageLoaderUtil.loadAvatar(ImageLoader.getInstance(), productDetail.getShopLogo(), shopbottomlogoView);
			}
			String shopName = productDetail.getShopName();
			shopbottomTextView.setText(shopName);
		}

		// 出发日期
		if (dateStamp != 0) {
			String starting = DateUtil.timestampToStr(dateStamp, Define.FORMAT_YMD);
			startingTextView.setText(starting);
		} else {
			((LinearLayout) startingTextView.getParent()).setVisibility(View.GONE);
		}
		// 产品预订数量
		String productCount = "x" + (adultCount + childCount);
		orderCountTextView.setText(productCount);

		if (isPayDeposit == 0) {
			orderTypeTextView.setText(R.string.full_booking_colon);
			orderMoneyTextView.setText(R.string.full_booking_colon);
		} else {
			orderTypeTextView.setText(R.string.reservation_colon);
			orderMoneyTextView.setText(R.string.reservation_colon);
		}

		// 如果用户已经登录，则自动填写预订人姓名和手机
		if (currentUserInfo != null) {
			String phoneNum = currentUserInfo.getMobile();
			contactsMobileEditText.setText(phoneNum);
			String email = currentUserInfo.getEmail();
			contactsEmailEditText.setText(email);
			contactsNameEditText.requestFocus();
		}
	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 点击填写参与人信息
	 */
	@Click(R.id.tv_input_participant)
	void onInputParticipant() {
		Intent intent = new Intent(ProductBookingActivity.this, ParticipantInfoActivity_.class);
		Bundle data = new Bundle();
		data.putSerializable(Define.PARTICIPANTINFO, (Serializable) participantInfoList);
		data.putInt(Define.ADULT_COUNT, adultCount);
		intent.putExtras(data);
		startActivityForResult(intent, REQUEST_CODE_PARTICIPANT);
	}

	private String contactName; // 联系人名
	private String contactMobile; // 联系人电话
	private String contactEmail; // 联系人邮箱
	private String participantInfoJson; // 参与人信息
	private List<ParticipantInfo> participantInfoList;// 参与人信息

	/**
	 * 提交订单
	 */
	@Click(R.id.lr_submit_order)
	void onOrderSubmitClick() {
		participantInfoJson = JsonTools.listToJson(participantInfoList);
		if (productDetail != null) {
			contactName = contactsNameEditText.getText().toString();
			contactMobile = contactsMobileEditText.getText().toString();
			contactEmail = contactsEmailEditText.getText().toString();
			message=sellermessage.getText().toString();
			if ("".equals(contactName)) {
				Toast.makeText(ProductBookingActivity.this, R.string.please_input_contact_name, Toast.LENGTH_SHORT).show();
			} else if ("".equals(contactMobile)) {
				Toast.makeText(ProductBookingActivity.this, R.string.please_input_contact_mobile, Toast.LENGTH_SHORT).show();
			} else if (contactMobile.length() != 11) {
				Toast.makeText(ProductBookingActivity.this, R.string.phone_num_form_wrong, Toast.LENGTH_SHORT).show();
			} else if (!StringUtil.isEmail(contactEmail)) {
				Toast.makeText(ProductBookingActivity.this, R.string.please_input_correct_email_address, Toast.LENGTH_SHORT).show();
			} else {
				checkLoginState();
			}
		}
	}

	/**
	 * 检查用户登录状态
	 */
	private void checkLoginState() {
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		if (uid != 0) {
			submitOrder();
		} else {
			Intent logIntent = new Intent(ProductBookingActivity.this, LoginActivity_.class);
			startActivityForResult(logIntent, REQUEST_CODE_LOGIN_BOOKING_PRODUCT);
		}
	}

	/**
	 * 提交订单
	 */
	private void submitOrder() {
		showProgressDialog();
 
		int shopId = productDetail.getShopId();
		String salePrice = productPackagePrice + "";
		float totalPrice = 0;
		if (isPayDeposit == 1) {
			totalPrice = Float.valueOf(deposit);
		}
		
		StoreManager.getInstance().reserveProduct(message, priceId, title, isPayDeposit, totalPrice, adultCount, childCount, dateStamp, contactName, contactMobile, contactEmail, participantInfoJson, storeType, shopId, productId, salePrice, new ReserveLineProductResultCallback() {
			
			@Override
			public void result(String errMsg, int orderId, int orderNum, int invoiceId, int isOffline, String shopLogo, String productLogo, List<Payment> payments) {
				submitOrderComplete(errMsg, orderId, orderNum, invoiceId, isOffline, shopLogo, productLogo, payments); 
			}
		});
	}

	/**
	 * 提交订单完成
	 * 
	 * @param errMsg
	 * @param orderId
	 * @param orderNum
	 */
	@UiThread
	void submitOrderComplete(String errMsg, int orderId, int orderNum, int invoiceId, int isOffline, String shopLogo, String productLogo, List<Payment> payments) {
		dissmissProgressDialog();

		if (errMsg == null) {
			Intent intent = null;
			Bundle data = new Bundle();
			intent = new Intent(ProductBookingActivity.this, OrderPayActivity_.class);
			if (isOffline == 1) {
				// 线下支付
				data.putBoolean(Define.IS_OFFLINE, true);
			} else { 
				// 线上支付
				data.putBoolean(Define.IS_OFFLINE, false);
			}
			
			data.putInt(Define.PID, productId);
			data.putInt(Define.SID, storeId);
			data.putString(Define.STORE_TYPE, storeType);
			String productName = productDetail.getProductName();
			data.putString(Define.PRODUCT_NAME, productName);
			String sku = productDetail.getSku();
			data.putString(Define.PRODUCT_SKU, sku);
			data.putString(Define.PRODUCT_LOGO, productLogo);
			data.putLong(Define.DATE, dateStamp);
			data.putInt(Define.ADULT_COUNT, adultCount);
			data.putInt(Define.CHILD_COUNT, childCount);
			data.putString(Define.CONTACTS_NAME, contactName);
			data.putString(Define.CONTACTS_MOBILE, contactMobile);
			data.putString(Define.CONTACTS_EMAIL, contactEmail);
			data.putInt(Define.ORDER_ID, orderId);
			data.putInt(Define.INVOICE_ID, invoiceId);
			data.putInt(Define.ORDER_CODE, orderNum);
			data.putInt(Define.PRODUCT_COUNT, (childCount + adultCount));
			data.putFloat(Define.PACKAGE_PRICE, productPackagePrice); // 套餐价格
			data.putFloat(Define.DEPOSIT, 0);
			data.putString(Define.TITLE, title);
			data.putSerializable(Define.PAYMENT_LIST, (Serializable) payments);  
			String wayReserve = "";
			if (isPayDeposit == 0) {
				wayReserve = getResources().getString(R.string.full_booking);
			} else {
				wayReserve = getResources().getString(R.string.reservation);
				data.putFloat(Define.DEPOSIT, Float.valueOf(deposit)); // 套餐价格
			}
			data.putString(Define.WAY_RESERVE, wayReserve);
			intent.putExtras(data);
			startActivity(intent);
			setResult(RESULT_OK);
			finish();
		} else {
			showToast(errMsg); 
		}
	}

	final int REQUEST_CODE_LOGIN_BOOKING_PRODUCT = 101; // 产品预订请求登录
	final int REQUEST_CODE_PARTICIPANT = 102; // 参与人名单

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_LOGIN_BOOKING_PRODUCT: // 产品预订请求登录
			if (resultCode == RESULT_OK) {
				// 延迟提交，避免一闪而过
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						submitOrder();
					}
				}, 500);
			}
			break;
		case REQUEST_CODE_PARTICIPANT:
			if (resultCode == RESULT_OK) {
				participantInfoList = (List<ParticipantInfo>) data.getSerializableExtra(Define.PARTICIPANTINFO);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
