package com.kooniao.travel.mine;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.res.StringRes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kooniao.travel.BottomTabBarActivity;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseFragment;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.manager.UserManager.UserInfoResultCallback;
import com.kooniao.travel.model.UserInfo;
import com.kooniao.travel.store.OrderDetailActivity.From;
import com.kooniao.travel.user.LoginActivity_;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 我的
 * 
 * @author ke.wei.quan
 * 
 */
@EFragment(R.layout.fragment_mine)
public class MineFragment extends BaseFragment {

	@ViewById(R.id.iv_mine_top_suspend)
	ImageView suspendImageView; // 高斯模糊层
	@ViewById(R.id.iv_mine_top_bg)
	ImageView topBgImageView; // 顶部背景
	@ViewById(R.id.iv_mine_avatar)
	ImageView userAvatarImageView; // 用户头像
	@ViewById(R.id.tv_mine_top_user_name)
	TextView userNameTextView; // 用户名
	@ViewById(R.id.tv_mine_top_user_email)
	TextView emailTextView; // 邮箱
	@ViewById(R.id.tv_mine_top_user_phone)
	TextView userPhoneTextView; // 用户电话

	@ViewById(R.id.tv_mine_order_form)
	TextView mineOrderFormTextView; // 我的订单
	@ViewById(R.id.tv_mine_message)
	TextView messageTextView; // 消息
	@ViewById(R.id.tv_mine_setting)
	TextView settingTextView; // 设置

	/**
	 * 初始化
	 */
	@AfterViews
	public void init() {
		if (isAttach) {
			loadUserInfo();
		}
	}

	private UserInfo userInfo; // 当前用户信息

	/**
	 * 加载用户信息
	 */
	private void loadUserInfo() {
		// 当前用户信息
		UserManager.getInstance().loadUserDetailInfo(new UserInfoResultCallback() {

			@Override
			public void result(String errMsg, UserInfo userInfo) {
				MineFragment.this.userInfo = userInfo;
				setCurrentViewUserInfo();
			}
		});
	}

	@DrawableRes(R.drawable.default_bg)
	Drawable defaultTopBackgrounDrawable; // 头部默认背景
	@DrawableRes(R.drawable.user_default_avatar)
	Drawable defaultUserAvatarDrawable; // 用户默认头像
	@StringRes(R.string.click_to_login)
	String defaultEmailString; // 默认邮箱显示

	/**
	 * 设置当前页面用户信息
	 */
	@UiThread
	void setCurrentViewUserInfo() {
		if (userInfo == null) {
			initNoLoginViewState();
		} else {
			loadCurrentViewUserInfo();
		}
	}

	/**
	 * 初始化未登录界面状态
	 */
	private void initNoLoginViewState() {
		userNameTextView.setVisibility(View.VISIBLE);
		emailTextView.setVisibility(View.VISIBLE);
		userPhoneTextView.setVisibility(View.VISIBLE);
		// 用户头像
		userAvatarImageView.setImageDrawable(defaultUserAvatarDrawable);
		// 用户名
		userNameTextView.setText("");
		// 邮箱
		emailTextView.setText(defaultEmailString);
		// 联系电话
		userPhoneTextView.setText("");
	}

	/**
	 * 加载当前页面用户信息
	 */
	private void loadCurrentViewUserInfo() {
		// 用户头像URL
		String userAvatarUrl = userInfo.getFace();
		if (isDataChange) {
			isDataChange = false;
			clearBitMapCache();
		}
		ImageLoaderUtil.loadAvatar(ImageLoader.getInstance(), userAvatarUrl, userAvatarImageView);
		// 用户名
		String userName = userInfo.getUname();
		userNameTextView.setText(userName);
		// 邮箱
		String emailAddress = userInfo.getEmail();
		String emailPrefix = StringUtil.getStringFromR(R.string.email_add);
		emailAddress = emailAddress == null ? "" : emailPrefix + emailAddress;
		if (emailAddress.equals(emailPrefix)) {
			emailTextView.setVisibility(View.GONE);
		} else {
			emailTextView.setVisibility(View.VISIBLE);
			emailTextView.setText(emailAddress);
		}
		// 联系电话
		String userPhone = userInfo.getMobile();
		String phonePrefix = StringUtil.getStringFromR(R.string.phone);
		userPhone = userPhone == null ? "" : phonePrefix + userPhone;
		if (userPhone.equals(phonePrefix)) {
			userPhoneTextView.setVisibility(View.GONE);
		} else {
			userPhoneTextView.setVisibility(View.VISIBLE);
			userPhoneTextView.setText(userPhone);
		}
	}

	/**
	 * 清理本地缓存
	 */
	private void clearBitMapCache() {
		ImageLoader.getInstance().clearDiscCache();
		ImageLoader.getInstance().clearMemoryCache();
		ImageLoader.getInstance().getDiscCache().clear();
	}

	/**
	 * 头部布局点击
	 */
	@Click(R.id.rl_mine_user_info)
	void onTopLayoutClick() {
		if (userInfo == null) {
			Intent loginIntent = new Intent(getActivity(), LoginActivity_.class);
			startActivityForResult(loginIntent, REQUEST_CODE_LOGIN);
		} else {
			Intent personalDataIntent = new Intent(getActivity(), PersonalDataActivity_.class);
			startActivityForResult(personalDataIntent, REQUEST_CODE_PERSONAL_DATA);
		}
	}

	/**
	 * 行程条目点击
	 */
	@Click(R.id.lr_mine_travel)
	void onTravelItemClick() {
		if (userInfo == null) {
			Intent loginIntent = new Intent(getActivity(), LoginActivity_.class);
			startActivityForResult(loginIntent, REQUEST_CODE_LOGIN_MY_TRAVEL);
		} else {
			startMyTravelActivity();
		}
	}

	/**
	 * 跳转我的行程界面
	 */
	private void startMyTravelActivity() {
		Intent travelIntent = new Intent(getActivity(), MyTravelActivity_.class);
		travelIntent.putExtra(Define.TRAVEL_COUNT, userInfo.getPlanNum());
		startActivityForResult(travelIntent, REQUEST_CODE_MY_TRAVEL); 
	}

	/**
	 * 收藏条目点击
	 */
	@Click(R.id.lr_mine_collect)
	void onCollectItemClick() {
		if (userInfo == null) {
			Intent loginIntent = new Intent(getActivity(), LoginActivity_.class);
			startActivityForResult(loginIntent, REQUEST_CODE_LOGIN_MY_COLLECT);
		} else {
			startMyCollectActivity();
		}
	}

	/**
	 * 跳转我的收藏页
	 */
	private void startMyCollectActivity() {
		Intent intent = new Intent(getActivity(), MyCollectActivity_.class);
		startActivity(intent);
	}

	/**
	 * 离线条目点击
	 */
	@Click(R.id.lr_mine_offline)
	void onOfflineItemClick() {
		Intent intent = new Intent(getActivity(), OfflineActivity_.class);
		startActivity(intent);
	}

	/**
	 * 我的订单条目点击
	 */
	@Click(R.id.lr_mine_order_form)
	void onOrderFormItemClick() {
		if (userInfo == null) {
			Intent loginIntent = new Intent(getActivity(), LoginActivity_.class);
			startActivityForResult(loginIntent, REQUEST_CODE_LOGIN_MY_ORDER);
		} else {
			startMyOrderActivity();
		}
	}

	/**
	 * 跳转我的订单界面
	 */
	private void startMyOrderActivity() {
		Intent intent = new Intent(getActivity(), MyOrderActivity_.class);
		startActivity(intent);
	}

	/**
	 * 消息条目点击
	 */
	@Click(R.id.lr_mine_message)
	void onMessageItemClick() {
		if (userInfo == null) {
			Intent loginIntent = new Intent(getActivity(), LoginActivity_.class);
			startActivityForResult(loginIntent, REQUEST_CODE_LOGIN_MY_NEWS);
		} else {
			startMessageActivity();
		}
	}

	/**
	 * 启动消息界面
	 */
	private void startMessageActivity() {
		Intent intent = new Intent(getActivity(), MessageActivity_.class);
		intent.putExtra(Define.TYPE, "personal");
		intent.putExtra(Define.FROM, From.FROM_MY_ORDER.from);
		startActivity(intent);
	}

	/**
	 * 检测登录状态是否发生了更改
	 * 
	 * @param hidden
	 */
	@Override
	public void onHiddenChanged(boolean hidden) {
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		if (!hidden) {
			if ((uid == 0 && userInfo != null) || (uid != 0 && userInfo == null)) {
				init();
			} else if (uid != 0 && userInfo != null) {
				UserInfo localUserInfo = UserManager.getInstance().getCurrentUserInfo();
				if (localUserInfo != null) {
					if (localUserInfo.getUname() != null) {
						userInfo = localUserInfo;
						loadCurrentViewUserInfo();
					} else {
						loadUserInfo();
					}
				} else {
					loadUserInfo();
				}
			}
		}
		super.onHiddenChanged(hidden);
	}

	/**
	 * 设置条目点击
	 */
	@Click(R.id.lr_mine_setting)
	void onSettingItemClick() {
		Intent settingIntent = new Intent(getActivity(), SettingActivity_.class);
		startActivityForResult(settingIntent, REQUEST_CODE_SETTING);
	}

	private boolean isDataChange;

	final int REQUEST_CODE_LOGIN = 11; // 登录
	final int REQUEST_CODE_LOGIN_MY_TRAVEL = 12; // 点击我的行程请求登录
	final int REQUEST_CODE_LOGIN_MY_COLLECT = 13; // 点击我的收藏请求登录
	final int REQUEST_CODE_LOGIN_MY_ORDER = 14; // 点击我的订单请求登录
	final int REQUEST_CODE_LOGIN_MY_NEWS = 15; // 点击消息请求登录
	final int REQUEST_CODE_SETTING = 16; // 设置
	final int REQUEST_CODE_PERSONAL_DATA = 17; // 个人资料页
	final int REQUEST_CODE_MESSAGE = 18; // 消息
	final int REQUEST_CODE_MY_TRAVEL = 19; // 我的行程

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_LOGIN: // 登录
			if (resultCode == Activity.RESULT_OK && data != null) {
				loadLoginResult(data);
				((BottomTabBarActivity)getActivity()).loadCurrentTravel();
			}
			break;

		case REQUEST_CODE_LOGIN_MY_TRAVEL: // 点击我的行程请求登录
			if (resultCode == Activity.RESULT_OK && data != null) {
				loadLoginResult(data);
				startMyTravelActivity();
			}
			break;

		case REQUEST_CODE_LOGIN_MY_COLLECT: // 点击我的收藏请求登录
			if (resultCode == Activity.RESULT_OK && data != null) {
				loadLoginResult(data);
				startMyCollectActivity();
			}
			break;

		case REQUEST_CODE_LOGIN_MY_ORDER: // 点击我的订单请求登录
			if (resultCode == Activity.RESULT_OK && data != null) {
				loadLoginResult(data);
				startMyOrderActivity();
			}
			break;

		case REQUEST_CODE_LOGIN_MY_NEWS: // 点击消息请求登录
			if (resultCode == Activity.RESULT_OK && data != null) {
				loadLoginResult(data);
				startMessageActivity();
			}
			break;

		case REQUEST_CODE_SETTING: // 设置
		case REQUEST_CODE_PERSONAL_DATA: // 个人资料页
			if (resultCode == Activity.RESULT_OK && data != null) {
				isDataChange = data.getBooleanExtra(Define.DATA, false);
				if (isDataChange) { // 界面数据改变
					loadUserInfo();
					((BottomTabBarActivity)getActivity()).loadCurrentTravel();
				}
			}
			break;

		case REQUEST_CODE_MESSAGE: // 消息请求登录
			if (resultCode == Activity.RESULT_OK && data != null) {
				loadLoginResult(data);
				startMessageActivity();
			}
			break;
			
		case REQUEST_CODE_MY_TRAVEL: // 我的行程
			if (resultCode == Activity.RESULT_OK && data != null) {
				boolean isCurrentTravelChange = data.getBooleanExtra(Define.CURRENT_TRAVEL_CHANGE, false);
				if (isCurrentTravelChange) {
					((BottomTabBarActivity)getActivity()).loadCurrentTravel();
				}
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 加载登录返回消息
	 * 
	 * @param data
	 */
	private void loadLoginResult(Intent data) {
		// 登录成功后返回的用户信息
		userInfo = (UserInfo) data.getSerializableExtra(Define.DATA);
		int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
		/**
		 * 加载当前页面信息
		 */
		if (userInfo != null) {
			setCurrentViewUserInfo();
		} else if (uid != 0) {
			loadUserInfo();
		}
	}

}
