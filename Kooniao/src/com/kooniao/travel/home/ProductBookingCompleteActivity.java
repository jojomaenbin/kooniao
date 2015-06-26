package com.kooniao.travel.home;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;

import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.store.OrderDetailActivity_;
import com.kooniao.travel.store.StoreActivity_;
import com.kooniao.travel.utils.DateUtil;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 产品预订提交完成
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_product_booking_complete)
public class ProductBookingCompleteActivity extends BaseActivity {

	@ViewById(R.id.tv_product_book_complete)
	TextView productBookingCompleteTextView; // 预订已提交
	@ViewById(R.id.tv_order_number)
	TextView orderNumTextView; // 订单号
	@ViewById(R.id.tv_product_name)
	TextView productNameTextView; // 产品名
	@ViewById(R.id.tv_order_count)
	TextView orderCountTextView; // 订单数量
	@ViewById(R.id.tv_product_code)
	TextView productCodeTextView; // 产品编号
	@ViewById(R.id.ll_selected_date)
	LinearLayout selectedDateLayout; // 所选日期的布局
	@ViewById(R.id.tv_selected_date)
	TextView selectedDateTextView; // 所选日期
	@ViewById(R.id.tv_contact)
	TextView contactTextView; // 联系人姓名
	@ViewById(R.id.tv_mobile)
	TextView mobileTextView; // 联系人手机号
	@ViewById(R.id.tv_order_price)
	TextView orderPriceTextView; // 订单价格
	@ViewById(R.id.tv_email)
	TextView emailTextView; // 联系人邮箱
	@ViewById(R.id.tv_way_reserve)
	TextView reserveWayTextView; // 预定方式
	@ViewById(R.id.tv_combo_name)
	TextView tvcombonameTextView; // 套餐名称
	@ViewById(R.id.tv_combo_price)
	TextView tvcombopriceTextView; // 套餐价格
	@ViewById(R.id.iv_product_cover_img)
	ImageView productCoverImg; // 产品封面

	/**
	 * 初始化
	 */
	@AfterViews
	void init() {
		initData();
		initView();
	}

	private int storeId; // 店铺id
	private int productId; // 产品id
	private int productType; // 产品类型
	private String storeType; // 店铺类型
	private int orderId; // 订单id
	private int productCount; // 产品数量
	private int orderNum; // 订单号
	private String productName; // 产品名
	private String productSku; // 产品编号
	private String productLogo; // 产品logo
	private long dateStamp; // 日期时间戳
	private String contactName; // 联系人名
	private String contactMobile; // 联系人电话
	private String email; // 联系人邮箱
	private String reserveWay; // 预定方式
	private float productPackagePrice;// 套餐单价
	private float deposit;// 定金金额
	private String title;// 套餐名称

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			storeId = intent.getIntExtra(Define.SID, 0);
			productId = intent.getIntExtra(Define.PID, 0);
			productType = intent.getIntExtra(Define.PRODUCT_TYPE, 0);
			storeType = intent.getStringExtra(Define.STORE_TYPE);
			orderId = intent.getIntExtra(Define.ORDER_ID, 0);
			productCount = intent.getIntExtra(Define.PRODUCT_COUNT, 1);
			orderNum = intent.getIntExtra(Define.ORDER_CODE, 0);
			productName = intent.getStringExtra(Define.PRODUCT_NAME);
			productSku = intent.getStringExtra(Define.PRODUCT_SKU);
			productLogo = intent.getStringExtra(Define.PRODUCT_LOGO);
			dateStamp = intent.getLongExtra(Define.DATE, 0);
			contactName = intent.getStringExtra(Define.CONTACTS_NAME);
			contactMobile = intent.getStringExtra(Define.CONTACTS_MOBILE);
			email = intent.getStringExtra(Define.CONTACTS_EMAIL);
			reserveWay = intent.getStringExtra(Define.WAY_RESERVE);
			productPackagePrice = intent.getFloatExtra(Define.PACKAGE_PRICE, 0);
			deposit = intent.getFloatExtra(Define.DEPOSIT, 0);
			title = intent.getStringExtra(Define.TITLE);
		}
	}

	@AnimationRes(R.anim.slide_in_from_top_with_overshoot_interpolator)
	Animation slideInFromTopAnimation; // 顶部进来动画

	/**
	 * 初始化界面
	 */
	private void initView() {
		productBookingCompleteTextView.setAnimation(slideInFromTopAnimation);

		// 订单号
		orderNumTextView.setText(String.valueOf(orderNum));
		// 订单数量
		orderCountTextView.setText("x" + productCount);
		// 所选日期
		if (dateStamp == 0) {
			selectedDateLayout.setVisibility(View.GONE);
		} else {
			String starting = DateUtil.timestampToStr(dateStamp, Define.FORMAT_YMD);
			selectedDateTextView.setText(starting);
		}
		// 联系人名
		contactTextView.setText(contactName);
		// 联系人电话
		mobileTextView.setText(contactMobile);
		// 联系人邮箱
		emailTextView.setText(email);
		// 套餐信息
		tvcombonameTextView.setText(title);
		tvcombopriceTextView.setText(StringUtil.getStringFromR(R.string.rmb) + productPackagePrice);
		// 详情相关
		if (productName != null) {
			// 产品名
			productNameTextView.setText(productName);
		}
		// 产品编号
		if (productSku != null && !productSku.equals("")) {
			productCodeTextView.setText(productSku);
		} else {
			((LinearLayout) productCodeTextView.getParent()).setVisibility(View.GONE);
		}

		// 订单价格
		if (reserveWay.equals(getResources().getString(R.string.full_booking))) {
			orderPriceTextView.setText(StringUtil.getStringFromR(R.string.rmb) + (productPackagePrice * productCount));
		} else {
			orderPriceTextView.setText("全额：" + StringUtil.getStringFromR(R.string.rmb) + productPackagePrice * productCount + " 预付订金：" + StringUtil.getStringFromR(R.string.rmb) + deposit);
		}

		// 产品套餐封面
		if (productLogo != null) {
			ImageLoaderUtil.loadListCoverImg(ImageLoader.getInstance(), productLogo, productCoverImg);
		}
		// 预定方式
		reserveWayTextView.setText(reserveWay);
	}

	/**
	 * 点击返回主页
	 */
	@Click(R.id.lr_go_back_home_page)
	void onGoBackHomePageClick() {
		backHomePage();
	}

	@Override
	public void onBackPressed() {
		backHomePage();
	}

	/**
	 * 返回主页
	 */
	private void backHomePage() {
		finish();
		Intent storeIntent = new Intent(ProductBookingCompleteActivity.this, BottomTabBarActivity_.class);
		storeIntent.putExtra(Define.TYPE, Define.HOME_PAGE);
		startActivity(storeIntent);
	}

	/**
	 * 返回个人中心
	 */
	private void backPersonalCenter() {
		finish();
		Intent storeIntent = new Intent(ProductBookingCompleteActivity.this, BottomTabBarActivity_.class);
		storeIntent.putExtra(Define.TYPE, Define.MINE);
		startActivity(storeIntent);
	}

	/**
	 * 查看订单详情
	 */
	@Click(R.id.lr_see_order_detail)
	void onSeeOrderDetailClick() {
		Intent intent = new Intent(ProductBookingCompleteActivity.this, OrderDetailActivity_.class);
		intent.putExtra(Define.STORE_TYPE, storeType);
		intent.putExtra(Define.ORDER_ID, orderId);
		intent.putExtra(Define.FROM, com.kooniao.travel.store.OrderDetailActivity.From.FROM_MY_ORDER.from);
		startActivityForResult(intent, REQUEST_CODE_ORDER_DETAIL);
	}

	/**
	 * 查看店铺详情
	 */
	@Click(R.id.visit_store)
	void onVisitStoreDetailClick() {
		Intent storeIntent = new Intent(ProductBookingCompleteActivity.this, StoreActivity_.class);
		// 当前店铺id和类型
		storeIntent.putExtra(Define.SID, storeId);
		storeIntent.putExtra(Define.STORE_TYPE, storeType);
		startActivityForResult(storeIntent, REQUEST_CODE_SHOP_DETAIL);
	}

	@Click(R.id.rl_product_combo)
	void onProductItemClick() {
		Intent productDetailIntent = null;
		if (productType == 2) {
			// 组合产品详情
			productDetailIntent = new Intent(ProductBookingCompleteActivity.this, CombineProductDetailActivity_.class);
		} else {
			if (productType == 4) {
				// 线路产品详情
				productDetailIntent = new Intent(ProductBookingCompleteActivity.this, LineProductDetailActivity_.class);
			} else {
				// 非线路产品详情
				productDetailIntent = new Intent(ProductBookingCompleteActivity.this, NonLineProductDetailActivity_.class);
			}
		}

		if (productDetailIntent != null) {
			productDetailIntent.putExtra(Define.PID, productId);
			productDetailIntent.putExtra(Define.STORE_TYPE, storeType);
			productDetailIntent.putExtra(Define.SID, storeId);
			startActivity(productDetailIntent);
		}
	}

	final int REQUEST_CODE_ORDER_DETAIL = 1;
	final int REQUEST_CODE_SHOP_DETAIL = 2; // 查看店铺详情

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_ORDER_DETAIL:
			backPersonalCenter();
			break;

		case REQUEST_CODE_SHOP_DETAIL:
			backHomePage();
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
