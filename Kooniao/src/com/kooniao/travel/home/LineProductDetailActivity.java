package com.kooniao.travel.home;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;
import org.androidannotations.annotations.res.StringRes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.ImageBrowseActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseFragmentActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.customwidget.TitleBarTab;
import com.kooniao.travel.customwidget.TitleBarTab.onTabClickListener;
import com.kooniao.travel.manager.ProductManager;
import com.kooniao.travel.manager.ProductManager.ProductDetailResultCallback;
import com.kooniao.travel.manager.ProductManager.StringResultCallback;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.model.DayList;
import com.kooniao.travel.model.ProductDetail;
import com.kooniao.travel.model.UserInfo;
import com.kooniao.travel.onekeyshare.OnekeyShare;
import com.kooniao.travel.store.OpenStoreActivity_;
import com.kooniao.travel.store.StoreActivity_;
import com.kooniao.travel.user.LoginActivity_;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.ColorUtil;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.KooniaoTypeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 线路产品详情页
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_product_detail)
public class LineProductDetailActivity extends BaseFragmentActivity {

	@ViewById(R.id.iv_set_up_store)
	ImageView setUpStoreImageView; // 开店按钮
	@ViewById(R.id.iv_collect)
	ImageView collectImageView; // 收藏按钮
	@ViewById(R.id.tv_product_registration_number)
	TextView registrationTextView; // 报名人数
	@ViewById(R.id.tv_product_inventory)
	TextView inventoryTextView; // 库存
	@ViewById(R.id.tv_product_price)
	TextView priceTextView; // 产品价格
	@ViewById(R.id.iv_product_cover_img)
	ImageView coverImageView; // 封面图
	@ViewById(R.id.tv_product_name)
	TextView nameTextView; // 产品名
	@ViewById(R.id.title_bar_tab)
	TitleBarTab titleBarTab; // 分类栏
	@ViewById(R.id.fl_fragment_container)
	FrameLayout fragmentContainer; // fragment布局
	@ViewById(R.id.iv_contact_customer_service)
	ImageView customerServiceImageView; // 联系客服
	@ViewById(R.id.iv_product_booking)
	TextView productBookingImageView; // 产品预订
	@ViewById(R.id.tv_price_left)
	TextView priceleftTextView; // 产品价格左边
	@ViewById(R.id.tv_price_right)
	TextView pricerightTextView; // 产品价格右边
	@ViewById(R.id.tv_price_mark)
	TextView pricemarkTextView; // 市场价格
	@ViewById(R.id.product_select)
	TextView productselectTextView; // 套餐栏
	@ViewById(R.id.shop_bottom)
	TextView shopbottomTextView; // 店铺名字
	@ViewById(R.id.shop_center)
	TextView shopcenterTextView; // 店铺名字
	@ViewById(R.id.shop_bottom_logo)
	ImageView shopbottomlogoView; // 店铺底栏logo
	@ViewById(R.id.shop_center_logo)
	ImageView shopcenterlogoView; // 店铺中栏logo
	@ViewById(R.id.shop_bottom_layout)
	LinearLayout shopbottomlayout; // 店铺底栏
	@ViewById(R.id.shop_center_layout)
	LinearLayout shopcenterlayout; // 店铺中栏

	@AfterViews
	void init() {
		initData();
		loadProductDetail();
	}

	KooniaoProgressDialog progressDialog;
	private int pid; // 产品id
	private int sid; // 店铺id
	private int cid; // C店id
	private int type; // 产品类型
	private String storeType = "a";
	private UserInfo userInfo; // 用户信息

	/**
	 * 初始化数据
	 */
	private void initData() {
		if (progressDialog == null) {
			progressDialog = new KooniaoProgressDialog(LineProductDetailActivity.this);
		}

		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}

		Intent intent = getIntent();
		pid = intent.getIntExtra(Define.PID, 0);
		sid = intent.getIntExtra(Define.SID, 0);
		if (intent.hasExtra(Define.STORE_TYPE)) {
			storeType = intent.getStringExtra(Define.STORE_TYPE);
		}
		if ("c".equals(storeType)) {
			cid = sid;
		}

		userInfo = UserManager.getInstance().getCurrentUserInfo();
		userInfo = userInfo == null ? new UserInfo() : userInfo;
	}

	/**
	 * 获取产品详情
	 */
	private void loadProductDetail() {
		ProductManager.getInstance().loadProductDetail(pid, cid, new ProductDetailResultCallback() {

			@Override
			public void result(String errMsg, ProductDetail productDetail) {
				loadProductDetailComplete(errMsg, productDetail);
			}
		});
	}

	private ProductDetail productDetail;

	/**
	 * 获取产品详情完成
	 * 
	 * @param errMsg
	 * @param productDetail
	 */
	@UiThread
	void loadProductDetailComplete(String errMsg, ProductDetail productDetail) {
		progressDialog.dismiss();

		if (isNeedToInit) {
			if (errMsg == null) {
				this.productDetail = productDetail;
				String productType = productDetail.getProductType();
				if (!productType.contains("product_")) {
					productType = "product_" + productType;
				}
				type = KooniaoTypeUtil.getTypeByModule(productType);
				initView();
				initFragment();
			} else {
				Toast.makeText(LineProductDetailActivity.this, errMsg, Toast.LENGTH_SHORT).show();
			}
		}
	}

	@AnimationRes(R.anim.slide_in_from_bottom_overshoot)
	Animation slideFromBottomAnimation; // 从底部进来动画

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 动态设置fragment内容区域高度，防止切换fragment时候顶部高度乱跳
		final int fragmentMinHeight = Define.heightPx - titleBarTab.getHeight() * 4;
		// fragmentContainer.setMinimumHeight(fragmentMinHeight);
		fragmentContainer.addOnLayoutChangeListener(new OnLayoutChangeListener() {

			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				if (bottom - top > fragmentMinHeight) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							shopbottomlayout.setVisibility(View.VISIBLE);
							shopcenterlayout.setVisibility(View.GONE);
							
						}
					});

				} else {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							shopbottomlayout.setVisibility(View.GONE);
							shopcenterlayout.setVisibility(View.VISIBLE);
						}
					});

				}
			}
		});

		// 设置店铺logo和来源
		if (productDetail.getShopLogo() != null) {
			ImageLoaderUtil.loadAvatar(ImageLoader.getInstance(), productDetail.getShopLogo(), shopbottomlogoView);
			ImageLoaderUtil.loadAvatar(ImageLoader.getInstance(), productDetail.getShopLogo(), shopcenterlogoView);
		}
		String shopName = productDetail.getShopName();
		shopbottomTextView.setText(shopName);
		shopcenterTextView.setText(shopName);

		/**
		 * 设置头部信息
		 */
		// 是否显示开店按钮
		int shopC = userInfo.getShopC();
		if (shopC != 0) {
			setUpStoreImageView.setVisibility(View.INVISIBLE);
		} else {
			setUpStoreImageView.setVisibility(View.VISIBLE);
		}
		// 是否收藏
		int collect = productDetail.getCollect();
		if (collect == 0) {
			collectImageView.setImageResource(R.drawable.collect_blue_normal);
		} else {
			collectImageView.setImageResource(R.drawable.collect_blue_press);
		}
		// 报名人数
		int orderCount = productDetail.getOrderCount();
		registrationTextView.setText(String.valueOf(orderCount));
		// 库存
		int stock = productDetail.getStock();
		inventoryTextView.setText(String.valueOf(stock));

		// resourceTextView.setText(shopName);
		// resourceTextView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent storeIntent = new Intent(LineProductDetailActivity.this,
		// StoreActivity_.class);
		// // 当前店铺id和类型
		// int storeId = productDetail.getShopId();
		// storeIntent.putExtra(Define.SID, storeId);
		// String currentStoreType = "a";
		// storeIntent.putExtra(Define.STORE_TYPE, currentStoreType);
		// startActivity(storeIntent);
		// }
		// });
		
		// 价格
		String price = productDetail.getPrice();
		priceTextView.setText(price);
		priceTextView.getPaint().setFakeBoldText(true);
		// 市场价
		String pricemark = productDetail.getMarketPrice();
		if (pricemark != null) {
			pricemarkTextView.setText("市场价：" + pricemark);
			pricemarkTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); // 中间横线
		}
		// 价格左
		String priceleft = productDetail.getPriceColumnLeft();
		priceleftTextView.setText(priceleft);
		// 价格右
		String priceright = productDetail.getPriceColumnRight();
		pricerightTextView.setText(priceright);
		// 封面
		String coverUrl = productDetail.getImg();
		coverImageView.setBackgroundColor(ColorUtil.getRandomColorRes());
		ImageLoader.getInstance().displayImage(coverUrl, coverImageView);
		// 产品名
		String name = productDetail.getProductName();
		nameTextView.setText(name);

		/**
		 * tab切换
		 */
		titleBarTab.setOnTabClickListener(new TabClickListener());

		// 底部客服和产品预订的显示与隐藏
		// 状态 (1:出售中 2:删除 3:未出售,也指下架)
		int productStatus = productDetail.getProductStatus();
		int shopStatus = productDetail.getShopStatus();// (0:新提交)
		int isPublic = productDetail.getIsPublic();
		if (productStatus != 1 || shopStatus != 1 || isPublic != 1) {
			customerServiceImageView.setClickable(false);
			// productBookingImageView.setImageResource(R.drawable.product_booking_unable);
			productselectTextView.setVisibility(View.GONE);
		}
	}

	class TabClickListener implements onTabClickListener {
		@Override
		public void onClick(int tabIndex) {
			switch (tabIndex) {
			case 0:
				onLineTabClick();
				break;

			case 1:
				onBookingInfomationTabClick();
				break;

			case 2:
				onCommentTabClick();
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 线路tab点击
	 */
	void onLineTabClick() {
		List<DayList> dayLists = productDetail.getDayList();
		if (dayLists == null || dayLists.isEmpty()) {
			fragmentContainer.setBackgroundColor(getResources().getColor(R.color.white));
		} else {
			fragmentContainer.setBackgroundColor(getResources().getColor(R.color.main_bg));
		}
		if (lineFragment != null) {
			switchFragment(lineFragment);
		}

	}

	/**
	 * 预订须知tab点击
	 */
	void onBookingInfomationTabClick() {
		fragmentContainer.setBackgroundColor(getResources().getColor(R.color.white));
		switchFragment(productIntroFragment);
	}

	/**
	 * 评价tab点击
	 */
	void onCommentTabClick() {
		fragmentContainer.setBackgroundColor(getResources().getColor(R.color.white));
		switchFragment(productCommentFragment);
	}

	private Fragment currentFragment; // 当前fragment
	private LineFragment_ lineFragment; // 线路
	private ProductIntroFragment_ productIntroFragment; // 预订须知
	private ProductCommentFragment_ productCommentFragment; // 评价

	/**
	 * 初始化fragment
	 */
	private void initFragment() {
		lineFragment = new LineFragment_();
		productIntroFragment = new ProductIntroFragment_();
		productCommentFragment = new ProductCommentFragment_();
		/**
		 * 默认选中线路
		 */
		currentFragment = lineFragment;
		List<DayList> dayLists = productDetail.getDayList();
		if (dayLists != null) {
			if (dayLists.isEmpty()) {
				fragmentContainer.setBackgroundColor(getResources().getColor(R.color.white));
			} else {
				fragmentContainer.setBackgroundColor(getResources().getColor(R.color.main_bg));
			}
		}

		// 把线路详情设置给lineFragment
		Bundle lineData = new Bundle();
		lineData.putSerializable(Define.DATA, productDetail);
		lineFragment.setArguments(lineData);

		// 把预订须知信息设置给bookingInfomationFragment
		Bundle infomationData = new Bundle();
		infomationData.putString(Define.DATA, productDetail.getIntroduction());
		productIntroFragment.setArguments(infomationData);

		// 把评论列表数据设置给productCommentFragment
		Bundle commentData = new Bundle();
		commentData.putInt(Define.PID, pid);
		commentData.putInt(Define.TYPE, type);
		productCommentFragment.setArguments(commentData);

		getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, lineFragment).commit();
		titleBarTab.checkTab(productDetail.getTag());
	}

	/**
	 * 切换fragment
	 * 
	 * @param switchFragment
	 */
	private void switchFragment(Fragment switchFragment) {
		/**
		 * 替换fragment
		 */
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		if (switchFragment.isAdded()) {
			fragmentTransaction.hide(currentFragment).show(switchFragment);
		} else {
			fragmentTransaction.hide(currentFragment).add(R.id.fl_fragment_container, switchFragment);
		}

		currentFragment = switchFragment;
		fragmentTransaction.commit();
	}

	/**
	 * 返回按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		activityFinish();
	}

	/**
	 * 开店
	 */
	@Click(R.id.iv_set_up_store)
	void onSetUpStoreClick() {
		UserInfo localUserInfo = UserManager.getInstance().getCurrentUserInfo();
		if (localUserInfo == null) {
			// 用户没有登录
			Intent intent = new Intent(LineProductDetailActivity.this, LoginActivity_.class);
			startActivityForResult(intent, REQUEST_CODE_OPEN_STORE_LOGIN);
		} else {
			// 用户已经登录
			openStore(localUserInfo);
		}
	}

	/**
	 * 开店
	 */
	void openStore(UserInfo localUserInfo) {
		if (localUserInfo != null) {
			// 用户已经登录
			int shopC = localUserInfo.getShopC();
			Intent intent = null;
			if (shopC == 0) {
				// 用户没开过C店
				int storeId = productDetail.getShopId();
				intent = new Intent(LineProductDetailActivity.this, OpenStoreActivity_.class);
				intent.putExtra(Define.SID, storeId);
				intent.putExtra(Define.STORE_TYPE, storeType);
			} else {
				// 用户已经开过C店
				intent = new Intent(LineProductDetailActivity.this, BottomTabBarActivity_.class);
				intent.putExtra(Define.TYPE, Define.STORE);
			}
			startActivity(intent);
		}
	}

	/**
	 * 分享
	 */
	@Click(R.id.iv_share)
	void onShareClick() {
		OnekeyShare onekeyShare = new OnekeyShare();
		onekeyShare.setTitle(productDetail.getProductName());
		onekeyShare.setText("我在“酷鸟”看到" + productDetail.getProductName() + "，很不错！你也来看看~" + productDetail.getShareUrl());
		onekeyShare.setUrl(productDetail.getShareUrl());
		onekeyShare.setNotification(R.drawable.app_logo, "酷鸟");
		onekeyShare.setImageUrl(productDetail.getImg());
		onekeyShare.show(LineProductDetailActivity.this);
	}

	/**
	 * 收藏
	 */
	@Click(R.id.iv_collect)
	void onCollectClick() {
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		if (uid == 0) {
			Intent logIntent = new Intent(LineProductDetailActivity.this, LoginActivity_.class);
			startActivityForResult(logIntent, RESULT_CODE_LOGIN_ADD_TO_MY_COLLECT);
		} else {
			addOrCancelToMyCollect();
		}
	}

	@StringRes(R.string.call_customer_service)
	String dialogTitle; // 拨打客服电话
	Dialog dialog; // 拨打客服电话确认对话框

	/**
	 * 联系客服
	 */
	@Click(R.id.iv_contact_customer_service)
	void onCustomerServiceClick() {
		final String mobile = productDetail.getMobile();
		if (mobile != null && !"".equals(mobile)) {
			dialog = new Dialog(LineProductDetailActivity.this, dialogTitle, mobile);
			dialog.setCancelable(false);
			dialog.setOnAcceptButtonClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_DIAL);
					intent.setData(Uri.parse("tel:" + mobile));
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
			Toast.makeText(LineProductDetailActivity.this, R.string.customer_service_null, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 产品预订
	 */
	@Click(R.id.iv_product_booking)
	void onProductBookingClick() {
		int productStatus = productDetail.getProductStatus();
		int shopStatus = productDetail.getShopStatus();
		if (productStatus == 1 && shopStatus == 1) {
			bookingProduct();
		}
	}

	/**
	 * 产品预订
	 */
	private void bookingProduct() {
		Intent productBookingIntent = new Intent(LineProductDetailActivity.this, SelectProductPackageActivity_.class);
		Bundle data = new Bundle();
		data.putSerializable(Define.DATA, productDetail);
		data.putInt(Define.PID, pid);
		data.putString(Define.STORE_TYPE, storeType);
		productBookingIntent.putExtras(data);
		startActivity(productBookingIntent);
	}

	/**
	 * 添加或取消到我的收藏列表
	 * 
	 * @param likeId
	 * @param isKeep
	 * @param likeSubType
	 * @param likeType
	 * 
	 *            "类型： 全部：0 ，线路：4 ，组合：2 ，酒店：5 ，美食：8， 娱乐：7 "
	 */
	private void addOrCancelToMyCollect() {
		final int isKeep = productDetail.getCollect() == 0 ? 1 : 0;
		productDetail.setCollect(isKeep);
		String likeType = KooniaoTypeUtil.getModuleByType(type);
		int aShopId = productDetail.getShopId();
		int fromStoreId = cid == 0 ? aShopId : cid;
		ProductManager.getInstance().addOrCancelToMyCollect(pid, isKeep, likeType, storeType, fromStoreId, new StringResultCallback() {

			@Override
			public void result(String errMsg) {
				addOrCancelToMyCollectComplete(isKeep, errMsg);
			}
		});
	}

	@StringRes(R.string.add_collect_success)
	String addToMyCollectSuccessTips; // 添加到我的收藏列表成功
	@StringRes(R.string.cancel_collect_success)
	String cancelMyCollectSuccessTips; // 取消我的收藏成功
	@AnimationRes(R.anim.collect_click)
	Animation collectAnimation; // 收藏动画

	/**
	 * 添加取消收藏完成
	 * 
	 * @param isKeep
	 * 
	 * @param errMsg
	 */
	@UiThread
	void addOrCancelToMyCollectComplete(int isKeep, String errMsg) {
		if (errMsg == null) {
			collectImageView.startAnimation(collectAnimation);
			UserManager.getInstance().undateCollectCount(isKeep);
			if (isKeep == 1) {
				collectImageView.setImageResource(R.drawable.collect_blue_press);
				Toast.makeText(LineProductDetailActivity.this, addToMyCollectSuccessTips, Toast.LENGTH_SHORT).show();
			} else {
				collectImageView.setImageResource(R.drawable.collect_blue_normal);
				Toast.makeText(LineProductDetailActivity.this, cancelMyCollectSuccessTips, Toast.LENGTH_SHORT).show();
			}
		} else {
			if (isKeep == 1) {
				collectImageView.setImageResource(R.drawable.collect_blue_normal);
			} else {
				collectImageView.setImageResource(R.drawable.collect_blue_press);
			}
			Toast.makeText(LineProductDetailActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	final int RESULT_CODE_LOGIN_ADD_TO_MY_COLLECT = 111; // 添加收藏请求登录
	final int REQUEST_CODE_OPEN_STORE_LOGIN = 112; // 开店请求登录

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case RESULT_CODE_LOGIN_ADD_TO_MY_COLLECT: // 添加收藏
			if (resultCode == RESULT_OK) { // 登录成功
				addOrCancelToMyCollect();
			}
			break;

		case REQUEST_CODE_OPEN_STORE_LOGIN: // 开店
			if (resultCode == Activity.RESULT_OK) {
				// 延时跳转，避免黑屏一闪而过
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						UserInfo localUserInfo = UserManager.getInstance().getCurrentUserInfo();
						openStore(localUserInfo);
					}
				}, 500);
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		activityFinish();
		super.onBackPressed();
	}

	private boolean isNeedToInit = true; // 是否需要初始化,防止没请求结束就退出当前页面

	/**
	 * 结束当前activity
	 */
	private void activityFinish() {
		isNeedToInit = false;
		finish();
	}

	/**
	 * 点击打开大图浏览
	 */
	@Click(R.id.iv_product_cover_img)
	void onCoverImgClick() {
		ProductManager.getInstance().loadProductLargeImageList(pid, new ProductManager.StringListCallback() {

			@Override
			public void result(String errMsg, List<String> imgList) {
				loadTravelLargeImageListComplete(errMsg, imgList);
			}
		});
	}

	/**
	 * 获取大图列表完成
	 * 
	 * @param errMsg
	 * @param imgList
	 */
	@UiThread
	void loadTravelLargeImageListComplete(String errMsg, List<String> imgList) {
		if (errMsg == null) {
			if (imgList.isEmpty()) {
				Toast.makeText(getBaseContext(), R.string.no_big_img, Toast.LENGTH_SHORT).show();
			} else {
				Intent intent = new Intent(LineProductDetailActivity.this, ImageBrowseActivity_.class);
				intent.putStringArrayListExtra(Define.IMG_LIST, (ArrayList<String>) imgList);
				startActivity(intent);
			}
		} else {
			Toast.makeText(getBaseContext(), errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 套餐选择点击
	 */
	@Click(R.id.product_select)
	void onAProductSelectClick() {
		onProductBookingClick();
	}

	/**
	 * 店铺点击
	 */
	@Click(R.id.shop_bottom_layout)
	void onShopbSelectClick() {
		Intent storeIntent = new Intent(LineProductDetailActivity.this, StoreActivity_.class);
		// 当前店铺id和类型
		int storeId = productDetail.getShopId();
		storeIntent.putExtra(Define.SID, storeId);
		String currentStoreType = "a";
		storeIntent.putExtra(Define.STORE_TYPE, currentStoreType);
		startActivity(storeIntent);
	}

	@Click(R.id.shop_center_layout)
	void onShopcSelectClick() {
		Intent storeIntent = new Intent(LineProductDetailActivity.this, StoreActivity_.class);
		// 当前店铺id和类型
		int storeId = productDetail.getShopId();
		storeIntent.putExtra(Define.SID, storeId);
		String currentStoreType = "a";
		storeIntent.putExtra(Define.STORE_TYPE, currentStoreType);
		startActivity(storeIntent);
	}

}
