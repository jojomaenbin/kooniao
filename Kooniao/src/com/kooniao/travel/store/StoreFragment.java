package com.kooniao.travel.store;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseFragment;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.manager.StoreManager;
import com.kooniao.travel.manager.StoreManager.StoreResultCallback;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.mine.MessageActivity_;
import com.kooniao.travel.model.Store;
import com.kooniao.travel.model.UserInfo;
import com.kooniao.travel.onekeyshare.OnekeyShare;
import com.kooniao.travel.store.OrderDetailActivity.From;
import com.kooniao.travel.user.LoginActivity_;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * 店铺
 * 
 * @author ke.wei.quan
 * 
 */
@EFragment(R.layout.fragment_store)
public class StoreFragment extends BaseFragment {

	@ViewById(R.id.ll_has_no_store)
	LinearLayout noStoreLayout; // 无店铺布局覆盖层
	@ViewById(R.id.layout_store_a)
	View aStoreLayout; // A店布局
	@ViewById(R.id.layout_store_c)
	View cStoreLayout; // C店布局
	@ViewById(R.id.ll_switch_store)
	View switchStore; // 切换店铺按钮
	@ViewById(R.id.tv_store_name_top_a)
	TextView aStoreNameTopTextView; // 顶部a店店铺名
	@ViewById(R.id.tv_store_name_top_c)
	TextView cStoreNameTopTextView; // 顶部c店店铺名
	@ViewById(R.id.iv_store_logo_a)
	ImageView aStoreLogoImageView; // a店店铺logo
	@ViewById(R.id.iv_store_logo_c)
	ImageView cStoreLogoImageView; // c店店铺logo
	@ViewById(R.id.iv_store_top_bg_a)
	ImageView aStoreBgImageView; // a店店铺背景
	@ViewById(R.id.iv_store_top_bg_c)
	ImageView cStoreBgImageView; // c店店铺背景
	@ViewById(R.id.tv_store_name_a)
	TextView aStoreNameTextView; // a店item店铺名
	@ViewById(R.id.tv_store_name_c)
	TextView cStoreNameTextView; // c店item店铺名
	@ViewById(R.id.tv_store_user_name_a)
	TextView aStoreUserNameTextView; // a店店铺用户名
	@ViewById(R.id.tv_store_user_name_c)
	TextView cStoreUserNameTextView; // c店店铺用户名

	@AfterViews
	public void init() {
		if (isAttach) {
			initData();
			initView();
		}
	}

	private UserInfo userInfo; // 用户信息
	private String cStoreShareUrl; // 新开C店分享链接
	private ImageLoader imageLoader;

	/**
	 * 初始化数据
	 */
	private void initData() {
		userInfo = UserManager.getInstance().getCurrentUserInfo();
		imageLoader = ImageLoader.getInstance();
	}

	@AnimationRes(R.anim.slide_in_right)
	Animation slideInRightAnimation; // 从右边进来动画
	@AnimationRes(R.anim.slide_out_right)
	Animation slideOutRightAnimation; // 从右边出去动画
	@AnimationRes(R.anim.slide_in_left)
	Animation slideInLeftAnimation; // 从左边进来动画
	@AnimationRes(R.anim.slide_out_left)
	Animation slideOutLeftAnimation; // 从左边出去动画

	private String currentStoreType; // 当前店铺类型

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 设置动画参数
		slideInRightAnimation.setDuration(600);
		slideOutRightAnimation.setDuration(600);
		slideInLeftAnimation.setDuration(600);
		slideOutLeftAnimation.setDuration(600);

		if (userInfo != null) {
			int shopA = userInfo.getShopA(); // A店
			int shopC = userInfo.getShopC(); // C店

			if (shopA == 0 && shopC == 0) {
				switchStore.setVisibility(View.GONE);
				noStoreLayout.setVisibility(View.VISIBLE);
				aStoreLayout.setVisibility(View.GONE);
				cStoreLayout.setVisibility(View.VISIBLE);
				// 重置C店店铺信息
				resetCStoreView();
			} else {
				noStoreLayout.setVisibility(View.GONE);
				if (shopA != 0 && shopC != 0) {
					// A和C店优先显示A店
					currentStoreType = "a";
					switchStore.setVisibility(View.VISIBLE); // 显示切换店铺按钮
					aStoreLayout.setVisibility(View.VISIBLE);
					cStoreLayout.setVisibility(View.GONE);
				} else {
					switchStore.setVisibility(View.GONE);
					if (shopA != 0) {
						currentStoreType = "a";
						aStoreLayout.setVisibility(View.VISIBLE);
						cStoreLayout.setVisibility(View.GONE);
					} else {
						currentStoreType = "c";
						aStoreLayout.setVisibility(View.GONE);
						cStoreLayout.setVisibility(View.VISIBLE);
					}
				}

				// 加载店铺信息
				loadStoreInfo();
			}
		} else {
			noStoreLayout.setVisibility(View.VISIBLE);
			aStoreLayout.setVisibility(View.GONE);
			cStoreLayout.setVisibility(View.VISIBLE);
			// 重置C店店铺信息
			resetCStoreView();
		}
	}

	/**
	 * 重置C店店铺信息
	 */
	private void resetCStoreView() {
		cStoreBgImageView.setImageResource(R.drawable.default_bg);
		cStoreLogoImageView.setImageResource(R.drawable.user_default_avatar);
		cStoreNameTopTextView.setText("");
		cStoreNameTextView.setText("");
		cStoreUserNameTextView.setText("");
	}

	private int storeId = 0; // 店铺id

	/**
	 * 加载店铺信息
	 */
	private void loadStoreInfo() {
		if ("a".equals(currentStoreType)) {
			// 获取A店店铺信息
			storeId = userInfo.getShopA();
		} else if ("c".equals(currentStoreType)) {
			// 获取C店店铺信息
			storeId = userInfo.getShopC();
		}

		// 请求服务器
		StoreManager.getInstance().loadStoreInfo(storeId, currentStoreType, new StoreResultCallback() {

			@Override
			public void result(String errMsg, Store store) {
				loadStoreInfoComplete(errMsg, store);
			}
		});
	}

	/**
	 * 保存当前店铺id和类型
	 */
	private void saveCurrentStoreInfo() {
		AppSetting.getInstance().saveIntPreferencesByKey(Define.SID, storeId);
		AppSetting.getInstance().saveStringPreferencesByKey(Define.TYPE, currentStoreType);
		AppSetting.getInstance().saveStringPreferencesByKey(Define.SHOP_NAME, storeName);
	}

	private String storeName; // 当前店铺名称
	private Store store; // 当前店铺

	/**
	 * 获取店铺信息请求完成
	 * 
	 * @param errMsg
	 * @param store
	 */
	private void loadStoreInfoComplete(String errMsg, Store store) {
		if (errMsg == null && store != null) {
			// 设置店铺信息
			this.store = store;
			storeName = store.getShopName();
			// 保存当前店铺id和类型
			saveCurrentStoreInfo();
			setStoreInfo();
		} else {
			Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 设置店铺信息
	 * 
	 * @param store
	 */
	private void setStoreInfo() {
		// 店铺背景
		String storeBgUrl = store.getBgImg();
		if ("a".equals(currentStoreType)) {
			ImageLoader.getInstance().displayImage(storeBgUrl, aStoreBgImageView, new ImageLoadingListener() {

				@Override
				public void onLoadingStarted(String imageUri, View view) {
					aStoreBgImageView.setImageResource(R.drawable.default_bg);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					aStoreBgImageView.setImageResource(R.drawable.default_bg);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					if (loadedImage != null) {
						aStoreBgImageView.setImageBitmap(loadedImage);
					} else {
						aStoreBgImageView.setImageResource(R.drawable.default_bg);
					}
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					aStoreBgImageView.setImageResource(R.drawable.default_bg);
				}
			});
		} else if ("c".equals(currentStoreType)) {
			ImageLoader.getInstance().displayImage(storeBgUrl, cStoreBgImageView, new ImageLoadingListener() {

				@Override
				public void onLoadingStarted(String imageUri, View view) {
					cStoreBgImageView.setImageResource(R.drawable.default_bg);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					cStoreBgImageView.setImageResource(R.drawable.default_bg);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					if (loadedImage != null) {
						cStoreBgImageView.setImageBitmap(loadedImage);
					} else {
						cStoreBgImageView.setImageResource(R.drawable.default_bg);
					}
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					cStoreBgImageView.setImageResource(R.drawable.default_bg);
				}
			});
		}
		// 店铺logo
		String storeLogo = store.getLogo();
		if ("a".equals(currentStoreType)) {
			ImageLoaderUtil.loadAvatar(imageLoader, storeLogo, aStoreLogoImageView);
		} else if ("c".equals(currentStoreType)) {
			ImageLoaderUtil.loadAvatar(imageLoader, storeLogo, cStoreLogoImageView);
		}
		// 店铺名
		String storeName = store.getShopName();
		if ("a".equals(currentStoreType)) {
			aStoreNameTopTextView.setText(storeName);
			aStoreNameTextView.setText(storeName);
		} else if ("c".equals(currentStoreType)) {
			cStoreNameTopTextView.setText(storeName);
			cStoreNameTextView.setText(storeName);
		}
		// 店铺用户名
		UserInfo userInfo = UserManager.getInstance().getCurrentUserInfo();
		String storeUserName = userInfo.getUname();
		if ("a".equals(currentStoreType)) {
			aStoreUserNameTextView.setText(storeUserName);
		} else if ("c".equals(currentStoreType)) {
			cStoreUserNameTextView.setText(storeUserName);
		}
	}

	/**
	 * 检测登录状态是否发生了更改
	 * 
	 * @param hidden
	 */
	@Override
	public void onHiddenChanged(boolean hidden) {
		int currentUid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		// 本地用户
		UserInfo localUserInfo = UserManager.getInstance().getCurrentUserInfo();
		// 本地用户信息String
		String localUserInfoString = localUserInfo == null ? "" : localUserInfo.toString();
		// 当前用户信息String
		String currentUserInfo = userInfo == null ? "" : userInfo.toString();

		// 是否重新加载店铺信息
		if (!hidden) {
			if ((currentUid == 0 && userInfo != null) || (currentUid != 0 && userInfo == null) || !localUserInfoString.equals(currentUserInfo)) {
				String currentUserName = AppSetting.getInstance().getStringPreferencesByKey(Define.CURRENT_USER_NAME);
				if (currentUserName != null) {
					localUserInfo.setUname(currentUserName);
					UserManager.getInstance().updateUserInfo(localUserInfo);
				}
				init();
			}
		}

		if (localUserInfo != null) {
			cStoreShareUrl = Define.C_STORE_SHARE_BASE_URL + localUserInfo.getShopC();
			boolean isFirstTimeOpenStore = AppSetting.getInstance().getBooleanPreferencesByKey(Define.IS_FIRST_TIME_OPEN_STORE);
			// 是否弹出分享页
			if (isFirstTimeOpenStore) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						OnekeyShare onekeyShare = new OnekeyShare();
						onekeyShare.setTitle("酷鸟行程");
						onekeyShare.setText("我开了家店，赶快参观" + cStoreShareUrl);
						onekeyShare.setUrl(cStoreShareUrl);
						onekeyShare.setNotification(R.drawable.app_logo, "酷鸟行程");
						onekeyShare.show(getActivity());
					}
				}, 500);
				AppSetting.getInstance().saveBooleanPreferencesByKey(Define.IS_FIRST_TIME_OPEN_STORE, false);
			}
		}
		super.onHiddenChanged(hidden);
	}

	/**
	 * 点击没有店铺布局，跳转开店
	 */
	@Click(R.id.ll_has_no_store)
	void onNoStoreLayoutClick() {
		UserInfo localUserInfo = UserManager.getInstance().getCurrentUserInfo();
		if (localUserInfo == null) {
			// 用户没有登录
			Intent intent = new Intent(getActivity(), LoginActivity_.class);
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
				intent = new Intent(getActivity(), OpenStoreActivity_.class);
			} else {
				// 用户已经开过C店
				intent = new Intent(getActivity(), BottomTabBarActivity_.class);
				intent.putExtra(Define.TYPE, Define.STORE);
			}
			startActivity(intent);
		}
	}

	/**
	 * A店顶部信息
	 */
	@Click(R.id.ll_store_top_a)
	void onAStoreTopClick() {
		startStoreSettingActivity();
	}

	/**
	 * C店顶部信息
	 */
	@Click(R.id.ll_store_top_c)
	void onCStoreTopClick() {
		startStoreSettingActivity();
	}

	/**
	 * 启动店铺设置界面
	 */
	private void startStoreSettingActivity() {
		Intent storeSettingIntent = new Intent(getActivity(), StoreSettingActivity_.class);
		Bundle data = new Bundle();
		data.putSerializable(Define.DATA, store);
		storeSettingIntent.putExtras(data);
		startActivityForResult(storeSettingIntent, REQUEST_CODE_STORE_SETTING);
	}

	/**
	 * A店店铺订单item点击
	 */
	@Click(R.id.ll_store_order_a)
	void onAStoreOrderClick() {
		startOrderManageActivity();
	}

	/**
	 * C店店铺订单item点击
	 */
	@Click(R.id.ll_store_order_c)
	void onCStoreOrderClick() {
		startOrderManageActivity();
	}

	/**
	 * 启动店铺订单页
	 */
	private void startOrderManageActivity() {
		Intent storeOrderIntent = new Intent(getActivity(), OrderManageActivity_.class);
		storeOrderIntent.putExtra(Define.STORE_TYPE, currentStoreType);
		startActivity(storeOrderIntent);
	}

	/**
	 * A店销售统计item点击
	 */
	@Click(R.id.ll_store_sale_statistics_a)
	void onASaleStatisticsClick() {
		startSaleStatisticsActivity();
	}

	/**
	 * C店销售统计item点击
	 */
	@Click(R.id.ll_store_sale_statistics_c)
	void onCSaleStatisticsClick() {
		startSaleStatisticsActivity();
	}

	/**
	 * 启动销售统计页
	 */
	private void startSaleStatisticsActivity() {
		Intent intent = new Intent(getActivity(), SaleStatisticsActivity_.class);
		intent.putExtra(Define.STORE_TYPE, currentStoreType);
		startActivity(intent);
	}

	/**
	 * A店产品管理item点击
	 */
	@Click(R.id.ll_store_product_manage_a)
	void onAProductManageClick() {
		if (store.getStatus()!=4) {
			startProductManageActivity();
		}
		else {
			Toast.makeText(getActivity(), "您的店铺已冻结，如有疑问请联系020-31133579", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * C店产品管理item点击
	 */
	@Click(R.id.ll_store_product_manage_c)
	void onCProductManageClick() {
		if (store.getStatus()!=4) {
			startProductManageActivity();
		}
		else {
			Toast.makeText(getActivity(), "您的店铺已冻结，如有疑问请联系020-31133579", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 启动产品管理页
	 */
	private void startProductManageActivity() {
		Intent storeIntent = new Intent(getActivity(), ProductManageActivity_.class);
		// 当前店铺id和类型
		storeIntent.putExtra(Define.SID, storeId);
		storeIntent.putExtra(Define.STORE_TYPE, currentStoreType);
		startActivity(storeIntent);
	}

	/**
	 * A店佣金item点击
	 */
	@Click(R.id.ll_store_commission_manage_a)
	void onACommissionManageClick() {
		startCommissionManage();
	}

	/**
	 * C店佣金item点击
	 */
	@Click(R.id.ll_store_commission_manage_c)
	void onCCommissionManageClick() {
		startCommissionManage();
	}

	/**
	 * 启动佣金管理页
	 */
	private void startCommissionManage() {
		Intent intent = new Intent(getActivity(), CommissionManageActivity_.class);
		intent.putExtra(Define.STORE_TYPE, currentStoreType);
		startActivity(intent);
	}

	/**
	 * 客户item点击
	 */
	@Click(R.id.ll_store_client_manage)
	void onClientManageClick() {
		Intent intent = new Intent(getActivity(), StoreClientManageActivity_.class);
		startActivity(intent);
	}

	/**
	 * A店进入店铺item点击
	 */
	@Click(R.id.ll_store_enter_a)
	void onAStoreEnterClick() {
		if (store.getStatus()!=4) {
			startStoreActivity();
		}
		else {
			final Dialog dialog;
			dialog = new Dialog(getActivity(),View.VISIBLE, View.GONE, "提示", "您的店铺已冻结，如有疑问请联系   020-31133579");
			dialog.setCancelable(false);
			dialog.setOnAcceptButtonClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}
		
	}

	/**
	 * C店进入店铺item点击
	 */
	@Click(R.id.ll_store_enter_c)
	void onCStoreEnterClick() {
		if (store.getStatus()!=4) {
			startStoreActivity();
		}
		else {
			final Dialog dialog;
			dialog = new Dialog(getActivity(),View.VISIBLE, View.GONE, "提示", "您的店铺已冻结，如有疑问请联系   020-31133579");
			dialog.setCancelable(false);
			dialog.setOnAcceptButtonClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}
	}

	/**
	 * 启动店铺页面
	 */
	private void startStoreActivity() {
		Intent storeIntent = new Intent(getActivity(), StoreActivity_.class);
		// 当前店铺id和类型
		storeIntent.putExtra(Define.SID, storeId);
		storeIntent.putExtra(Define.STORE_TYPE, currentStoreType);
		startActivity(storeIntent);
	}

	/**
	 * A店店铺消息item点击
	 */
	@Click(R.id.ll_store_message_a)
	void onAStoreMessageClick() {
		startMessageActivity("shop", "a");
	}

	/**
	 * C店店铺消息item点击
	 */
	@Click(R.id.ll_store_message_c)
	void onCStoreMessageClick() {
		startMessageActivity("cshop", "c");
	}

	/**
	 * 启动消息界面
	 */
	private void startMessageActivity(String type, String storeType) {
		Intent intent = new Intent(getActivity(), MessageActivity_.class);
		intent.putExtra(Define.TYPE, type);
		intent.putExtra(Define.FROM, From.FROM_ORDER_MANAGE.from);
		intent.putExtra(Define.STORE_TYPE, storeType);
		startActivity(intent);
	}

	/**
	 * 切换c店铺点击
	 */
	@Click(R.id.ll_switch_store)
	void onSwitchCStoreClick() {
		if ("a".equals(currentStoreType)) {
			switchCStoreLayout();
		} else {
			switchAStoreLayout();
		}
	}

	/**
	 * 切换A店布局
	 */
	private void switchAStoreLayout() {
		currentStoreType = "a";
		aStoreLayout.setVisibility(View.VISIBLE);
		aStoreLayout.startAnimation(slideInLeftAnimation);
		cStoreLayout.setVisibility(View.GONE);
		cStoreLayout.startAnimation(slideOutRightAnimation);
		slideInLeftAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 加载店铺信息
				loadStoreInfo();
			}
		});
	}

	/**
	 * 切换C店布局
	 */
	private void switchCStoreLayout() {
		currentStoreType = "c";
		aStoreLayout.setVisibility(View.GONE);
		aStoreLayout.startAnimation(slideOutLeftAnimation);
		cStoreLayout.setVisibility(View.VISIBLE);
		cStoreLayout.startAnimation(slideInRightAnimation);

		// 加载用户信息
		slideInRightAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 加载用户信息
				loadStoreInfo();
			}
		});
	}

	final int REQUEST_CODE_STORE_SETTING = 111; // 店铺设置
	final int REQUEST_CODE_OPEN_STORE_LOGIN = 112; // 开店请求登录

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_STORE_SETTING: // 店铺设置
			if (resultCode == Activity.RESULT_OK && data != null) {
				boolean isDataChange = data.getBooleanExtra(Define.DATA, false);
				if (isDataChange) {
					loadStoreInfo();
				}
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

}
