package com.kooniao.travel;

import java.io.Serializable;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;
import org.androidannotations.annotations.res.StringRes;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.BaiDuMapActivity.From;
import com.kooniao.travel.around.AroundFragment_;
import com.kooniao.travel.base.BaseFragmentActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.BottomLayout;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.discovery.DiscoveryFragment_;
import com.kooniao.travel.discovery.TravelDetailActivity_;
import com.kooniao.travel.home.HomePageFragment_;
import com.kooniao.travel.manager.AppManager;
import com.kooniao.travel.manager.AppManager.StringResultCallback;
import com.kooniao.travel.manager.TravelAlarmManager;
import com.kooniao.travel.manager.TravelManager;
import com.kooniao.travel.manager.AppManager.CheckVersionResultCallback;
import com.kooniao.travel.manager.TravelManager.CurrentTravelResultCallback;
import com.kooniao.travel.manager.TravelManager.MyTravelResultCallback;
import com.kooniao.travel.mine.MineFragment_;
import com.kooniao.travel.model.CurrentTravel;
import com.kooniao.travel.model.MyTravel;
import com.kooniao.travel.model.UserTravel;
import com.kooniao.travel.store.StoreFragment_;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.DateUtil;
import com.kooniao.travel.utils.FileUtil;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;

/**
 * 一级目录底部bar
 * 
 * @since 2014.11.25
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_bottom_bar)
public class BottomTabBarActivity extends BaseFragmentActivity {

	@ViewById(R.id.fl_fragment_container)
	FrameLayout fragmentContainer; // fragment布局填充

	@ViewById(R.id.layout_bottom_travel_remind)
	View travelRemindLayout; // 当前行程布局
	@ViewById(R.id.tv_current_travel_month)
	TextView travelMonthTextView; // 行程月
	@ViewById(R.id.tv_current_travel_day)
	TextView travelDayTextView; // 行程日
	@ViewById(R.id.tv_current_travel_name)
	TextView travelNameTextView; // 行程名

	@ViewById(R.id.layout_bottom_bar)
	BottomLayout bottomBarLayout; // 底部栏
	@ViewById(R.id.layout_bottom)
	BottomLayout allbottomBarLayout; // 底部全部内容
	@ViewById(R.id.rg_bottom_bar)
	RadioGroup radioGroup;
	@ViewById(R.id.rb_bottom_bar_home)
	RadioButton homePageRadioButton; // 首页按钮
	@ViewById(R.id.rb_bottom_bar_store)
	RadioButton storeRadioButton; // 店铺按钮
	@ViewById(R.id.rb_bottom_bar_discovery)
	RadioButton discoveryRadioButton; // 发现按钮
	@ViewById(R.id.rb_bottom_bar_around)
	RadioButton aroundRadioButton; // 附近按钮
	@ViewById(R.id.rb_bottom_bar_mine)
	RadioButton mineRadioButton; // 我的按钮

	Fragment currentContentFragment; // 当前内容页
	HomePageFragment_ homePageFragment; // 首页
	StoreFragment_ storeFragment; // 店铺
	DiscoveryFragment_ discoveryFragment;// 发现
	AroundFragment_ aroundFragment; // 附近
	MineFragment_ mineFragment; // 我的

	@AfterViews
	void init() {
		checkAppUpdate();
		initFragment();
		loadCurrentTravel();
		uploadLogFile();
	}

	/**
	 * 检查版本更新
	 */
	private void checkAppUpdate() {
		AppManager.getInstance().checkLastVersion(new CheckVersionResultCallback() {

			@Override
			public void result(String errMsg, boolean isNeedForceUpdate, String appDownloadUrl) {
				checkVersionFinish(errMsg, isNeedForceUpdate, appDownloadUrl);
			}

		});
	}

	/**
	 * 请求版本更新完成
	 * 
	 * @param errMsg
	 * @param isNeedForceUpdate
	 * @param appDownloadUrl
	 */
	private void checkVersionFinish(String errMsg, boolean isNeedForceUpdate, final String appDownloadUrl) {
		if (errMsg == null) {
			if (isNeedForceUpdate) { // 强制更新
				final Dialog dialog = new Dialog(BottomTabBarActivity.this, View.VISIBLE, View.GONE, "版本更新", "有新版本，请马上更新！");
				dialog.setCancelable(false);

				dialog.setOnAcceptButtonClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						skipWebViewToUpdate(appDownloadUrl);
						finish();
					}
				});
				dialog.show();
			}
		}
	}

	/**
	 * 跳转浏览器下载更新
	 */
	private void skipWebViewToUpdate(String url) {
		Uri webUri = Uri.parse(url);
		Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
		startActivity(webIntent);
	}

	/**
	 * 初始化Fragment
	 */
	private void initFragment() {
		homePageFragment = new HomePageFragment_();
		storeFragment = new StoreFragment_();
		discoveryFragment = new DiscoveryFragment_();
		aroundFragment = new AroundFragment_();
		mineFragment = new MineFragment_();

		/**
		 * 默认选中首页
		 */
		currentContentFragment = homePageFragment;
		getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, homePageFragment).commit();
	}

	/**
	 * 加载当前行程
	 */
	public void loadCurrentTravel() {
		long currentTravelLastDate = AppSetting.getInstance().getLongPreferencesByKey(Define.CURRENT_TRAVEL_LAST_DATE);
		long currentTimestamp = System.currentTimeMillis() / 1000;
		if (currentTravelLastDate == 0 || currentTimestamp > currentTravelLastDate) {
			setCurrentTravel(null);
		}
		TravelManager.getInstance().getCurrentTravel(new CurrentTravelResultCallback() {

			@Override
			public void result(String errMsg, CurrentTravel currentTravel) {
				if (errMsg == null) {
					setCurrentTravel(currentTravel);
					initTravelAlarmManager();
				}
			}
		});
	}

	private String logFileName;

	/**
	 * 上传app奔溃日志
	 */
	private void uploadLogFile() {
		logFileName = AppSetting.getInstance().getStringPreferencesByKey(Define.LAST_LOG_NAME);
		if (logFileName != null) {
			logFileName = KooniaoApplication.getInstance().getLogDir() + logFileName;
			AppManager.getInstance().uploadLogFile(logFileName, new StringResultCallback() {

				@Override
				public void result(String errMsg) {
					if (errMsg == null) {
						FileUtil.delFile(logFileName);
						AppSetting.getInstance().removePreferenceByKey(Define.LAST_LOG_NAME);
					}
				}
			});
		}
	}

	/**
	 * 设置三天行程闹钟
	 */
	public void initTravelAlarmManager() {
		TravelManager.getInstance().loadThreeDaysTravelList(new MyTravelResultCallback() {

			@Override
			public void result(String errMsg, List<MyTravel> allTravels, List<MyTravel> teamTravels, int pageCount) {
				if (errMsg == null) {
					if (allTravels != null) {
						for (MyTravel t : allTravels) {
							TravelAlarmManager.getInstance().setBeforeTravelAlarm(BottomTabBarActivity.this, t);
						}
					}

				}
			}
		});
	}

	/**
	 * 点击当前行程布局
	 */
	@Click(R.id.layout_bottom_travel_remind)
	void onTravelRemindClick() {
		if (currentTravel != null) {
			Intent intent = new Intent(BottomTabBarActivity.this, TravelDetailActivity_.class);
			intent.putExtra(Define.PID, currentTravel.getId());
			startActivity(intent);
		}
	}

	/**
	 * 点击导航
	 */
	@Click(R.id.iv_travel_remind_navigation)
	void onNavigationClick() {
		if (currentTravel != null) {
			Intent intent = new Intent(BottomTabBarActivity.this, BaiDuMapActivity_.class);
			Bundle extras = new Bundle();
			extras.putInt(Define.FROM, From.FROM_TRAVEL_DETAIL.from);
			extras.putSerializable(Define.DAY_LIST, (Serializable) currentTravel.getDayList());
			intent.putExtras(extras);
			startActivity(intent);
		}
	}

	private CurrentTravel currentTravel; // 当前行程

	/**
	 * 设置当前行程
	 * 
	 * @param currentTravel
	 */
	private void setCurrentTravel(CurrentTravel currentTravel) {
		this.currentTravel = currentTravel;
		if (currentTravel == null) {
			travelRemindLayout.setVisibility(View.GONE);
		} else {
			travelRemindLayout.setVisibility(View.VISIBLE);
			// 行程时间
			long timeStamp = currentTravel.getTime();
			String time = DateUtil.timeDistanceString(timeStamp, Define.FORMAT_YMD);
			String month = time.substring(time.indexOf("-") + 1, time.lastIndexOf("-")) + "月";
			String day = time.substring(time.lastIndexOf("-") + 1);
			travelMonthTextView.setText(month);
			travelDayTextView.setText(day);
			// 行程名
			String travelName = currentTravel.getTitle();
			travelNameTextView.setText(travelName);
			TravelAlarmManager.getInstance().setOnTravelAlarm(BottomTabBarActivity.this, currentTravel);
			DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
			UserTravel usertravel = db.queryById(AppSetting.getInstance().getIntPreferencesByKey(Define.UID), UserTravel.class);
			if (usertravel != null) {
				if (usertravel.getTravelid() != currentTravel.getId()) {
					usertravel.setReceiveTravelRemind(true);
				}
				usertravel.setTravelable(true);
				usertravel.setTravelid(currentTravel.getId());
				usertravel.setTravelbengin(currentTravel.getTime());
				usertravel.setTravelend(currentTravel.getTime() + currentTravel.getDayList().size() * 60 * 60 * 24);
				db.update(usertravel);
			} else {
				usertravel = new UserTravel();
				usertravel.setTravelable(true);
				usertravel.setUserid(AppSetting.getInstance().getIntPreferencesByKey(Define.UID));
				usertravel.setTravelid(currentTravel.getId());
				usertravel.setTravelbengin(currentTravel.getTime());
				usertravel.setTravelend(currentTravel.getTime() + currentTravel.getDayList().size() * 60 * 60 * 24);
				usertravel.setReceiveTravelRemind(true);
				db.save(usertravel);
			}
		}
	}

	/**
	 * 首页按钮点击
	 */
	@Click(R.id.rb_bottom_bar_home)
	void onHomePageButtonClick() {
		switchFragment(homePageFragment);
	}

	/**
	 * 店铺按钮点击
	 */
	@Click(R.id.rb_bottom_bar_store)
	void onStoreButtonClick() {
		switchFragment(storeFragment);
	}

	/**
	 * 发现按钮点击
	 */
	@Click(R.id.rb_bottom_bar_discovery)
	void onDiscoveryButtonClick() {
		switchFragment(discoveryFragment);
	}

	/**
	 * 附近按钮点击
	 */
	@Click(R.id.rb_bottom_bar_around)
	void onAroundButtonClick() {
		switchFragment(aroundFragment);
	}

	/**
	 * 我的按钮点击
	 */
	@Click(R.id.rb_bottom_bar_mine)
	void onMineButtonClick() {
		switchFragment(mineFragment);
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
			fragmentTransaction.hide(currentContentFragment).show(switchFragment);
		} else {
			fragmentTransaction.hide(currentContentFragment).add(R.id.fl_fragment_container, switchFragment);
		}

		currentContentFragment = switchFragment;
		fragmentTransaction.commitAllowingStateLoss();
	}

	private int tabPosition = 1;

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent != null) {
			String type = intent.getStringExtra(Define.TYPE);
			boolean loginChange = intent.getBooleanExtra(Define.LOGIN_CHANGE, false);
			if (Define.HOME_PAGE.equals(type)) {
				homePageRadioButton.setChecked(true);
				switchFragment(homePageFragment);
				if (loginChange) {
					loadCurrentTravel();
				}
			} else if (Define.STORE.equals(type)) {
				storeRadioButton.setChecked(true);
				switchFragment(storeFragment);
			} else if (Define.DISCOVERY.equals(type)) {
				discoveryRadioButton.setChecked(true);
				switchFragment(discoveryFragment);
			} else if (Define.AROUND.equals(type)) {
				tabPosition = intent.getIntExtra(Define.TAB_POSITION, 1);
				aroundRadioButton.setChecked(true);
				switchFragment(aroundFragment);
				aroundFragment.setPagerPosition(tabPosition);
			} else if (Define.MINE.equals(type)) {
				mineRadioButton.setChecked(true);
				switchFragment(mineFragment);
			}
		}
		super.onNewIntent(intent);
	}

	@AnimationRes(R.anim.slide_out_to_bottom)
	Animation slideOutToBottomAnimation; // 从底部滑出动画
	@AnimationRes(R.anim.slide_in_from_bottom)
	Animation slideInFromBottomAnimation; // 从底部进来动画

	private ObjectAnimator mOutToBottom1, mInToBottom1;

	/**
	 * 是否需要隐藏底部栏
	 */
	@Override
	public void onHideBottomBarListener(boolean isNeedToHide) {
		if (mOutToBottom1 == null) {
			mOutToBottom1 = ObjectAnimator.ofFloat(allbottomBarLayout, "translationY", bottomBarLayout.getmHight());
			mInToBottom1 = ObjectAnimator.ofFloat(allbottomBarLayout, "translationY", 0);
			mInToBottom1.setDuration(500);
		}
		if (isNeedToHide) {
			if (mOutToBottom1 != null && !mOutToBottom1.isRunning()) {
				mOutToBottom1 = ObjectAnimator.ofFloat(allbottomBarLayout, "translationY", allbottomBarLayout.getTranslationY(), bottomBarLayout.getmHight());
				mOutToBottom1.setDuration(500);
				mOutToBottom1.start();
			}
			if (mInToBottom1.isRunning()) {
				mInToBottom1.end();
			}

		} else {
			mInToBottom1.start();

		}
		super.onHideBottomBarListener(isNeedToHide);
	}

	final long INTERVAL_EXIT_TIME = 2000; // 双击需要退出的时间间隔
	long lastClickBackTime = 0; // 上一次点击后退按钮
	@StringRes(R.string.double_click_to_exit)
	String exitTips; // 双击退出APP的提示

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			long intervalTime = System.currentTimeMillis() - lastClickBackTime;
			if (intervalTime > INTERVAL_EXIT_TIME) {
				lastClickBackTime = System.currentTimeMillis();
				Toast.makeText(BottomTabBarActivity.this, exitTips, Toast.LENGTH_SHORT).show();
			} else {
				finish();
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
