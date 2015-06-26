package com.kooniao.travel.mine;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.manager.AppManager;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.manager.UserManager.StringResultCallback;
import com.kooniao.travel.model.UserInfo;
import com.kooniao.travel.user.LoginActivity_;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.StringUtil;

/**
 * 设置界面
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_setting)
public class SettingActivity extends BaseActivity {

	@ViewById(R.id.tv_setting_username)
	TextView loginTextView; // 登录状态
	@ViewById(R.id.tv_setting_cache_size)
	TextView cacheSizeTextView; // 缓存大小
	@ViewById(R.id.ll_setting_log_out)
	LinearLayout logoutLayout; // 退出登录布局

	/**
	 * 初始化
	 */
	@AfterViews
	void init() {
		initData();
		initView();
	}

	@StringRes(R.string.has_no_login)
	String loginStateText; // 默认登录状态
	@StringRes(R.string.default_cache_size)
	String defaultCacheSize; // 默认缓存大小
	UserInfo userInfo; // 用户信息

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 缓存大小
		defaultCacheSize = AppManager.getInstance().getCacheSize();
		// 用户信息
		userInfo = UserManager.getInstance().getCurrentUserInfo();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 登录状态
		loginStateText = userInfo == null ? loginStateText : userInfo.getUname();
		loginTextView.setText(loginStateText);
		// 缓存大小
		cacheSizeTextView.setText(defaultCacheSize);
		// 是否展示退出登录布局
		int logoutVisibility = userInfo == null ? View.GONE : View.VISIBLE;
		logoutLayout.setVisibility(logoutVisibility);
	}

	@StringRes(R.string.clear_cache)
	String dialogClearCacheTitle; // 清理缓存对话框标题
	@StringRes(R.string.accept_to_clear_cache)
	String dialogClearCacheMessage; // 清理缓存对话框内容
	Dialog dialog; // 清理缓存确认对话框

	/**
	 * 清理缓存条目点击
	 */
	@Click(R.id.lr_setting_clear_cache)
	void onClearCacheItemClick() {
		dialog = new Dialog(SettingActivity.this, dialogClearCacheTitle, dialogClearCacheMessage);
		dialog.setCancelable(false);
		dialog.setOnAcceptButtonClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				clearAppCache(); // 清理缓存
				resetAppCache(); // 重置缓存大小
			}
		});
		dialog.setOnCancelButtonClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * 清理缓存
	 */
	@Background
	void clearAppCache() {
		AppManager.getInstance().clearCache();
	}

	/**
	 * 重置缓存大小
	 */
	@UiThread
	void resetAppCache() {
		defaultCacheSize = getResources().getString(R.string.default_cache_size);
		cacheSizeTextView.setText(defaultCacheSize);
	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		activityFinish();
	}

	/**
	 * 个人资料条目点击
	 */
	@Click(R.id.lr_setting_personal_data)
	void onPersonalDataItemClick() {
		if (userInfo == null) {
			Intent loginIntent = new Intent(SettingActivity.this, LoginActivity_.class);
			startActivityForResult(loginIntent, REQUEST_CODE_LOGIN);
		} else {
			Intent personalDataIntent = new Intent(SettingActivity.this, PersonalDataActivity_.class);
			startActivityForResult(personalDataIntent, REQUEST_CODE_PERSONAL_DATA);
		}
	}

	/**
	 * 软件评分条目点击
	 */
	@Click(R.id.lr_setting_sofware_rating)
	void onSofwareRatingItemClick() {
		// 跳转市场进行软件评分
		Uri uri = Uri.parse("market://details?id=" + getPackageName());
		Intent sofwareIntent = new Intent(Intent.ACTION_VIEW, uri);
		sofwareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(sofwareIntent);
	}

	/**
	 * 关于酷鸟条目点击
	 */
	@Click(R.id.lr_setting_about_kooniao)
	void onAboutItemClick() {
		Intent aboutIntent = new Intent(SettingActivity.this, AboutKooniaoActivity_.class);
		startActivity(aboutIntent);
	}

	@StringRes(R.string.logout)
	String dialogLogoutTitle; // 退出登录对话框标题
	@StringRes(R.string.logout_tips)
	String dialogLogoutMessage; // 退出登录对话框内容

	/**
	 * 退出登录条目点击
	 */
	@Click(R.id.lr_setting_log_out)
	void onLogoutItemClick() {
		dialog = new Dialog(SettingActivity.this, dialogLogoutTitle, dialogLogoutMessage);
		dialog.setCancelable(false);
		dialog.setOnAcceptButtonClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				userLogout();
			}
		});
		dialog.setOnCancelButtonClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private KooniaoProgressDialog progressDialog; 
	
	/**
	 * 退出登录
	 */
	@UiThread
	void userLogout() {
		if (progressDialog == null) {
			progressDialog = new KooniaoProgressDialog(SettingActivity.this);
		}
		
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
		
		UserManager.getInstance().userLogout(new StringResultCallback() {

			@Override
			public void result(String errMsg) {
				userLogoutComplete(errMsg);
			}
		});
	}

	boolean isDataChange = false; // 页面数据是否更改
	@StringRes(R.string.logout_success)
	String userLogoutSuccessTips; // 退出登录成功提示

	/**
	 * 退出登录完成
	 * 
	 * @param errMsg
	 */
	@UiThread
	void userLogoutComplete(String errMsg) {
		progressDialog.dismiss();
		
		if (errMsg == null) {
			userInfo = null;
			isDataChange = true;
			loginStateText = StringUtil.getStringFromR(R.string.has_no_login);
			loginTextView.setText(loginStateText);
			logoutLayout.setVisibility(View.GONE);
			Toast.makeText(getBaseContext(), userLogoutSuccessTips, Toast.LENGTH_SHORT).show();
			// 返回首页
			Intent intent = new Intent(SettingActivity.this, BottomTabBarActivity_.class);
			intent.putExtra(Define.TYPE, Define.HOME_PAGE);
			intent.putExtra(Define.LOGIN_CHANGE, true);
			startActivity(intent); 
			activityFinish();
		} else {
			isDataChange = false;
			Toast.makeText(getBaseContext(), errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed() {
		activityFinish();
	}

	/**
	 * 结束当前activity
	 */
	private void activityFinish() {
		Intent data = new Intent();
		data.putExtra(Define.DATA, isDataChange);
		setResult(RESULT_OK, data);
		finish();
	}

	final int REQUEST_CODE_LOGIN = 11; // 登录
	final int REQUEST_CODE_PERSONAL_DATA = 12; // 个人资料

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_LOGIN: // 登录
			if (resultCode == Activity.RESULT_OK && data != null) {
				isDataChange = true;
				// 登录成功后返回的用户信息
				userInfo = (UserInfo) data.getSerializableExtra(Define.DATA);
				if (userInfo == null) {
					userInfo = new UserInfo();
					String userName = AppSetting.getInstance().getStringPreferencesByKey(Define.CURRENT_USER_NAME);
					userInfo.setUname(userName);
				}
				/**
				 * 加载当前页面信息
				 */
				initView();
			}
			break;
			
		case REQUEST_CODE_PERSONAL_DATA: // 个人资料
			if (resultCode == Activity.RESULT_OK && data != null) {
				if (!isDataChange) {
					isDataChange = data.getBooleanExtra(Define.DATA, false);
				}
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
