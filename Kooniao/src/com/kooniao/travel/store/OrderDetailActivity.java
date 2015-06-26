package com.kooniao.travel.store;

import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.api.ApiCaller.APIStringResultCallback;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.home.CombineProductDetailActivity_;
import com.kooniao.travel.home.LineProductDetailActivity_;
import com.kooniao.travel.home.NonLineProductDetailActivity_;
import com.kooniao.travel.manager.OrderManager;
import com.kooniao.travel.manager.StoreManager;
import com.kooniao.travel.manager.OrderManager.OrderDetailResultCallback;
import com.kooniao.travel.model.OrderDetail;
import com.kooniao.travel.model.ParticipantInfo;
import com.kooniao.travel.utils.OrderUtil;
import com.kooniao.travel.utils.StringUtil;

/**
 * 订单详情
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_order_detail)
public class OrderDetailActivity extends BaseActivity {

	public static enum From {
		FROM_ORDER_MANAGE(0), // 来自订单管理
		FROM_MY_ORDER(1); // 来自我的订单

		public int from;

		From(int from) {
			this.from = from;
		}
	}

	@ViewById(R.id.rl_title_bar)
	View titleBarView;
	@ViewById(R.id.iv_store)
	ImageView storeIconImageView; // 店铺icon
	@ViewById(R.id.tv_order_status)
	TextView orderStatusTextView; // 订单状态
	@ViewById(R.id.iv_order_edit)
	ImageView orderEditImageView; // 订单编辑图标
	@ViewById(R.id.sv_order_detail)
	ScrollView scrollView;
	@ViewById(R.id.tv_order_status_tips)
	TextView orderStatusTipsTextView; // 订单处理状态相关提示
	@ViewById(R.id.ll_order_mobile)
	LinearLayout mobileInfoLayout; // 电话信息相关布局
	@ViewById(R.id.tv_order_contact_phone)
	TextView contactPhoneTextView; // 订单联系电话
	@ViewById(R.id.tv_order_name)
	TextView productNameTextView; // 订单名称
	@ViewById(R.id.tv_order_resource)
	TextView orderResourceTextView; // 订单来源
	@ViewById(R.id.tv_order_count)
	TextView orderCountTextView; // 订单数量
	@ViewById(R.id.tv_order_price)
	TextView orderPriceTextView; // 订单价格
	@ViewById(R.id.tv_order_product_code)
	TextView productCodeTextView; // 产品编号
	@ViewById(R.id.tv_order_num)
	TextView orderNumTextView; // 订单号
	@ViewById(R.id.tv_order_buy_time)
	TextView purchasingDateTextView; // 购买时间
	@ViewById(R.id.ll_order_start_date)
	LinearLayout orderStartDateLayout; // 出发日期布局
	@ViewById(R.id.tv_order_start_date)
	TextView startDateTextView; // 出发日期
	@ViewById(R.id.ll_order_people_count)
	LinearLayout peopleCountLayout; // 出游人数布局
	@ViewById(R.id.tv_order_people_count)
	TextView pepopleCountTextView; // 出游人数
	@ViewById(R.id.ll_order_contact_info)
	LinearLayout contactInfoLayout; // 联系人信息布局
	@ViewById(R.id.tv_contact)
	TextView contactNameTextView; // 联系人姓名
	@ViewById(R.id.tv_mobile)
	TextView contactMobileTextView; // 联系人电话
	@ViewById(R.id.ll_email)
	LinearLayout contactEmailLayout; // 联系人邮件布局
	@ViewById(R.id.tv_email)
	TextView contactEmailTextView; // 联系人邮箱
	@ViewById(R.id.ll_participant)
	LinearLayout participantLayout; // 参与人布局
	@ViewById(R.id.tv_participant)
	TextView participanTextView; // 参与人信息bar
	@ViewById(R.id.ll_participant_info)
	LinearLayout participantInfoLayout; // 参与人信息布局
	@ViewById(R.id.tv_order_type)
	TextView orderTypeTextView; // 预定方式
	@ViewById(R.id.tv_combo_name)
	TextView combonameTextView; // 套餐名称
	@ViewById(R.id.tv_combo_price)
	TextView combopriceTextView; // 套餐单价

	/**
	 * 初始化
	 */
	@AfterViews
	void init() {
		initData();
		initPopupWindow();
		loadOrderDetail();
	}

	private int from; // 来自于
	private int orderId; // 订单id
	private String storeType; // 店铺类型

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			from = intent.getIntExtra(Define.FROM, From.FROM_ORDER_MANAGE.from);
			orderId = intent.getIntExtra(Define.ORDER_ID, 0);
			storeType = intent.getStringExtra(Define.STORE_TYPE);
		}
	}

	/**
	 * 加载订单详情
	 */
	private void loadOrderDetail() {
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
		} else {
			Toast.makeText(OrderDetailActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 设置界面信息
	 */
	private void setViewInfo() {
		// 去掉参与人信息箭头
		participanTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		// 默认显示参与人信息布局
		participantInfoLayout.setVisibility(View.VISIBLE);
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
		if (from == From.FROM_MY_ORDER.from) { // 来自于我的订单
			// 隐藏店铺icon
			storeIconImageView.setVisibility(View.INVISIBLE);
			// 隐藏订单编辑按钮
			orderEditImageView.setVisibility(View.INVISIBLE);
			// 显示头部电话信息布局
			mobileInfoLayout.setVisibility(View.VISIBLE);
			// 订单处理相关信息
			orderStatusTipsTextView.setVisibility(View.VISIBLE);
			String orderStatusTips = OrderUtil.getStatusTipsText(orderStatus);
			orderStatusTipsTextView.setText(orderStatusTips);
		} else {
			if ("c".equals(storeType)) { // 来自于c店
				// 隐藏联系人信息布局
				contactInfoLayout.setVisibility(View.GONE);
				// 隐藏参与人信息布局
				participantLayout.setVisibility(View.GONE);
				// 隐藏订单编辑按钮
				orderEditImageView.setVisibility(View.INVISIBLE);
				// 显示头部电话信息布局
				mobileInfoLayout.setVisibility(View.VISIBLE);
				// 订单处理相关信息
				orderStatusTipsTextView.setVisibility(View.VISIBLE);
				String orderStatusTips = OrderUtil.getStatusTipsText(orderStatus);
				orderStatusTipsTextView.setText(orderStatusTips);
			} else { // 来自于a店
				// 显示订单编辑按钮
				orderEditImageView.setVisibility(View.VISIBLE);
				// 隐藏头部电话信息布局
				mobileInfoLayout.setVisibility(View.GONE);
				int cShopId = orderDetail.getcShopId();
				if (cShopId != 0) { // 分销
					// 订单处理相关信息
					orderStatusTipsTextView.setVisibility(View.VISIBLE);
					String cShopName = orderDetail.getcShopName();
					orderStatusTipsString = StringUtil.getStringFromR(R.string.order_come_from) + cShopName + StringUtil.getStringFromR(R.string.spread_shop);
					// 订单状态处理信息
					SpannableString spannableString = new SpannableString(orderStatusTipsString);
					ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.v16b8eb));
					spannableString.setSpan(foregroundColorSpan, 5, 5 + cShopName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					orderStatusTipsTextView.setText(spannableString);
				} else { // 自销
					// 订单处理相关信息
					orderStatusTipsTextView.setVisibility(View.GONE);
				}
			}
		}

		// 订单状态
		String orderStatusString = StringUtil.getStringFromR(R.string.order) + OrderUtil.getStatusText(orderStatus);
		orderStatusTextView.setText(orderStatusString);
		// 联系电话
		String mobile = orderDetail.getaShopMobile();
		contactPhoneTextView.setText(mobile);
		// 产品名
		String productName = orderDetail.getProductName();
		productNameTextView.setText(productName);
		// 来源
		String resource = orderDetail.getaShopName();
		orderResourceTextView.setText(resource);
		// 数量
		int count = orderDetail.getProductCount();
		orderCountTextView.setText("x" + count);
		// 预定方式
		orderTypeTextView.setText(orderDetail.getBookMethodes());
		// 订单价格
		float orderPrice = orderDetail.getDepositNum();
		if (orderPrice==0) {
			orderPrice=orderDetail.getOrderCount();
			orderPriceTextView.setText(StringUtil.getStringFromR(R.string.rmb) + orderPrice);
			orderPrice=orderPrice/count;
		}
		else {
			orderPriceTextView.setText("全额："+StringUtil.getStringFromR(R.string.rmb) +orderDetail.getOrderCount()+" 预付订金："+StringUtil.getStringFromR(R.string.rmb) + orderPrice);
		}
		// 产品编号
		String productSku = orderDetail.getProductSku();
		if (productSku != null && !productSku.equals("")) {
			productCodeTextView.setText(productSku);
		} else {
			((LinearLayout) productCodeTextView.getParent()).setVisibility(View.GONE);
		}
		// 订单号
		String orderNum = orderDetail.getOrderSn();
		orderNumTextView.setText(orderNum);
		//套餐类型与价格
		combonameTextView.setText("套餐类型："+orderDetail.getPackageName());
		combopriceTextView.setText(StringUtil.getStringFromR(R.string.rmb) + orderDetail.getSalePrice());
		// 购买时间
		String buyDate = orderDetail.getOrderTime();
		purchasingDateTextView.setText(buyDate);
		peopleCountLayout.setVisibility(View.GONE);// 出游人数布局
		if (orderDetail.getStartTime()==null||orderDetail.getStartTime().equals("")) { // 非线路产品
			// 出发日期布局
			orderStartDateLayout.setVisibility(View.GONE);
		} else {
			// 出发日期
			String startingDate = orderDetail.getStartTime();
			if (startingDate.length() > 10) {
				startingDate = startingDate.substring(0, 10);
			}
			startDateTextView.setText(startingDate);
		}

	}

	/**
	 * 初始化联系人信息布局
	 */
	private void initContactInfoLayout() {
		// 联系人姓名
		String contactName = orderDetail.getConractName();
		contactNameTextView.setText(contactName);
		// 联系人电话
		String contactMobile = orderDetail.getConractMobile();
		contactMobileTextView.setText(contactMobile);
		// 联系人邮箱
		String contactEmail = orderDetail.getConractEmail();
		if ("".equals(contactEmail)) {
			contactEmailLayout.setVisibility(View.GONE);
		} else {
			contactEmailLayout.setVisibility(View.VISIBLE);
			contactEmailTextView.setText(contactEmail);
		}
	}

	private LayoutParams params; // 布局参数

	/**
	 * 初始化参与人信息布局
	 */
	private void initParticipantInfoLayout() {
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		List<ParticipantInfo> participantInfos = orderDetail.getJoinerList();
		if (participantInfos.isEmpty() || participantInfos.toString().equals("")) {
			participantLayout.setVisibility(View.GONE);
		} else {
			// 参与人信息是否可见
			int participantLayoutVisiable = 0;
			for (int i = 0; i < participantInfos.size(); i++) {
				ParticipantInfo participantInfo = participantInfos.get(i);
				final View participantAdultInfoLayout = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.sub_participant_info, null);
				// 姓名
				View nameLayout = participantAdultInfoLayout.findViewById(R.id.ll_contact);
				TextView nameTextView = (TextView) participantAdultInfoLayout.findViewById(R.id.tv_contact);
				String name = participantInfo.getName();
				if (name.equals("")) {
					nameLayout.setVisibility(View.GONE);
				}
				nameTextView.setText(name);
				// 手机
				View mobileLayout = participantAdultInfoLayout.findViewById(R.id.ll_mobile);
				TextView mobileTextView = (TextView) participantAdultInfoLayout.findViewById(R.id.tv_mobile);
				String mobile = participantInfo.getMobile();
				if (mobile.equals("")) {
					mobileLayout.setVisibility(View.GONE);
				}
				mobileTextView.setText(mobile);
				// 证件类型
				View certificationLayout = participantAdultInfoLayout.findViewById(R.id.ll_certification);
				TextView typeTextView = (TextView) participantAdultInfoLayout.findViewById(R.id.tv_certificate_type);
				// 设置图标不可见
				int type = Integer.parseInt(participantInfo.getIdCardType());
				String certificateType = OrderUtil.getCertificateByType(type);
				typeTextView.setText(certificateType);
				// 证件号码
				TextView numTextView = (TextView) participantAdultInfoLayout.findViewById(R.id.tv_certificate_num);
				String certificateNum = participantInfo.getIdCard();
				if (certificateNum.equals("")) {
					certificationLayout.setVisibility(View.GONE);
				}
				numTextView.setText(certificateNum);

				if (name.equals("") && mobile.equals("") && certificateNum.equals("")) {
					participantLayoutVisiable++;
				}
				// 添加到参与人信息布局
				participantInfoLayout.addView(participantAdultInfoLayout, i, params);
			}

			// 设置是否可见
			if (participantLayoutVisiable == participantInfos.size()) {
				participantLayout.setVisibility(View.GONE);
			} else {
				participantLayout.setVisibility(View.VISIBLE);
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
		finish();
		Intent intent = new Intent();
		intent.putExtra(Define.ORDER_STATUS, orderChangeStatus);
		setResult(RESULT_OK, intent);
	}

	/**
	 * 店铺icon
	 */
	@Click(R.id.iv_store)
	void onStoreIconClick() {
		finish();
		Intent storeIntent = new Intent(OrderDetailActivity.this, BottomTabBarActivity_.class);
		storeIntent.putExtra(Define.TYPE, Define.STORE);
		startActivity(storeIntent);
	}

	@StringRes(R.string.call)
	String dialogTitle; // 对话框标题
	Dialog dialog; // 拨打电话确认对话框

	/**
	 * 点击拨打电话
	 */
	@Click(R.id.ll_order_mobile)
	void onMobileClick() {
		final String phoneNumber = orderDetail.getaShopMobile();
		if (!"".equals(phoneNumber)) {
			dialog = new Dialog(OrderDetailActivity.this, dialogTitle, phoneNumber);
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
			Toast.makeText(OrderDetailActivity.this, R.string.customer_service_null, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 点击产品名
	 */
	@Click(R.id.tv_order_name)
	void onProductNameClick() {
		// 产品id
		int pid = orderDetail.getProductId();
		// 产品类型
		int productType = orderDetail.getProductType();
		Intent productDetailIntent = null;
		if (productType == 2) {
			// 组合产品详情
			productDetailIntent = new Intent(OrderDetailActivity.this, CombineProductDetailActivity_.class);
		} else {
			if (productType == 4) {
				// 线路产品详情
				productDetailIntent = new Intent(OrderDetailActivity.this, LineProductDetailActivity_.class);
			} else {
				// 非线路产品详情
				productDetailIntent = new Intent(OrderDetailActivity.this, NonLineProductDetailActivity_.class);
			}
		}

		if (productDetailIntent != null) {
			productDetailIntent.putExtra(Define.PID, pid);
			productDetailIntent.putExtra(Define.TYPE, productType);
			startActivity(productDetailIntent);
		}
	}

	/**
	 * 点击店铺名
	 */
	@Click(R.id.tv_order_resource)
	void onStoreNameClick() {
		// 店铺id
		int sid = orderDetail.getaShopId();
		String storeType = "a";
		Intent intent = new Intent(OrderDetailActivity.this, StoreActivity_.class);
		intent.putExtra(Define.SID, sid);
		intent.putExtra(Define.STORE_TYPE, storeType);
		startActivity(intent);
	}

	private PopupWindow statusPopupWindow;

	/**
	 * 初始化订单状态修改弹出窗口
	 */
	private void initPopupWindow() {
		View orderStatusSelectLayout = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.popupwindow_update_order_status, null);
		statusPopupWindow = new PopupWindow(orderStatusSelectLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		statusPopupWindow.setFocusable(false);
		statusPopupWindow.setAnimationStyle(R.style.PopupAnimationFromBottom);
		// 关闭按钮
		orderStatusSelectLayout.findViewById(R.id.iv_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				statusPopupWindow.dismiss();
			}
		});
		// 处理中
		orderStatusSelectLayout.findViewById(R.id.tv_order_status_dealing).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateOrderStatus(0);
			}
		});
		// 已取消
		orderStatusSelectLayout.findViewById(R.id.tv_order_status_canceled).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateOrderStatus(-1);
			}
		});
		// 已确认
		orderStatusSelectLayout.findViewById(R.id.tv_order_status_confirm).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateOrderStatus(1);
			}
		});
		// 部分收款
		orderStatusSelectLayout.findViewById(R.id.tv_order_status_part_collection).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateOrderStatus(5);
			}
		});
		// 已关闭
		orderStatusSelectLayout.findViewById(R.id.tv_order_status_closed).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateOrderStatus(-2);
			}
		});
		// 已收款
		orderStatusSelectLayout.findViewById(R.id.tv_order_status_money_receipt).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateOrderStatus(2);
			}
		});
		// 已退订
		orderStatusSelectLayout.findViewById(R.id.tv_order_status_unsubcribed).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateOrderStatus(3);
			}
		});
		// 已出团
		orderStatusSelectLayout.findViewById(R.id.tv_order_status_have_a_ball).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateOrderStatus(4);
			}
		});
	}

	private int orderChangeStatus = -1; // 更改的订单状态
	@StringRes(R.string.update_order_status_success)
	String updateOrderStatusSuccessTips; // 更改订单状态成功

	/**
	 * 更改订单状态 订单状态。0：未处理；1:已确认；-1：取消；-2：关闭；2：已收款；3：已退订；4：已出团；5：部分收款
	 * 
	 * @param status
	 */
	private void updateOrderStatus(final int status) {
		statusPopupWindow.dismiss();

		StoreManager.getInstance().updateOrderStatus(orderId, status, new APIStringResultCallback() {

			@Override
			public void result(String errMsg) {
				if (errMsg == null) {
					orderChangeStatus = status;
					orderDetail.setOrderStatus(status);
					String orderStatusString = StringUtil.getStringFromR(R.string.order) + OrderUtil.getStatusText(status);
					orderStatusTextView.setText(orderStatusString);
					Toast.makeText(OrderDetailActivity.this, updateOrderStatusSuccessTips, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(OrderDetailActivity.this, errMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 点击更改订单状态
	 */
	@Click(R.id.iv_order_edit)
	void onOrderStatusEditClick() {
		statusPopupWindow.showAtLocation(titleBarView, Gravity.CENTER, 0, 0);
	}

}
