package com.kooniao.travel.store;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseFragmentActivity;
import com.kooniao.travel.citylist.AreaListActivity_;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.CityManager;
import com.kooniao.travel.model.AreaParent;
import com.kooniao.travel.model.SubArea;
import com.kooniao.travel.utils.AppSetting;

/**
 * 线路引用
 * 
 * @author ke.wei.quan
 *
 */
@SuppressLint({ "HandlerLeak", "InflateParams" })
@EActivity(R.layout.activity_reference_line)
public class LineReferenceActivity extends BaseFragmentActivity {

	@ViewById(R.id.tv_line_self)
	TextView lineSelfTextView; // 个人线路tab
	@ViewById(R.id.v_line_selected)
	View lineSelfUnderLineView; // 个人线路tab底下蓝色指示条
	@ViewById(R.id.tv_public_line)
	TextView publicLineTextView; // 公共线路tab
	@ViewById(R.id.v_public_line_selected)
	View publicLineUnderLineView; // 公共线路tab底下蓝色指示条
	@ViewById(R.id.titlebar_main)
	View titleBar;

	@AfterViews
	void init() {
		initBaiDuLocationService();
		initData();
		initFragment();
		saveSubAreaList();
	}

	int cityId = 3544; // 城市id
	String locatingFailTips; // 定位失败提示信息
	String locatingTips; // 正在定位提示信息

	/**
	 * 初始化数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			cityId = intent.getIntExtra(Define.CID, 0);
		}
		
		locatingFailTips = getResources().getString(R.string.locating_fail);
		locatingTips = getResources().getString(R.string.locating);
	}

	Fragment currentFragment; // 当前fragment
	ReferenceListFragment_ selfLineFragment; // 个人线路
	ReferenceListFragment_ publicLineFragment; // 公共线路

	/**
	 * 初始化fragment
	 */
	private void initFragment() {
		selfLineFragment = new ReferenceListFragment_();
		publicLineFragment = new ReferenceListFragment_();

		Bundle selfLineData = new Bundle();
		selfLineData.putString(Define.REFERENCE_TYPE, "line");
		selfLineData.putString(Define.LINE_TYPE, "self");
		selfLineData.putInt(Define.CID, cityId);
		selfLineFragment.setArguments(selfLineData);

		Bundle publicLineData = new Bundle();
		publicLineData.putString(Define.REFERENCE_TYPE, "line");
		publicLineData.putString(Define.LINE_TYPE, "public");
		publicLineData.putInt(Define.CID, cityId);
		publicLineFragment.setArguments(publicLineData);

		currentFragment = selfLineFragment;
		getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, selfLineFragment).commit();
	}

	/**
	 * 定位参数
	 */
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor = "gcj02";
	public LocationClient locationClient; // 百度定位
	public KooniaoLocationListener locationListener; // 定位监听

	/**
	 * 初始化百度定位服务
	 */
	private void initBaiDuLocationService() {
		locationClient = new LocationClient(this.getApplicationContext());
		locationListener = new KooniaoLocationListener();
		locationClient.registerLocationListener(locationListener);
	}

	/**
	 * 初始化定位参数
	 */
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);// 设置定位模式
		option.setCoorType(tempcoor);// 返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1000ms
		option.setIsNeedAddress(true);
		option.setTimeOut(10 * 1000); // 定位超时
		locationClient.setLocOption(option);
	}
	
	private int cityIdTemp;

	/**
	 * 实现实位回调监听
	 */
	public class KooniaoLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			String city = location.getCity();
			if (city.length() > 1) {
				city = city.substring(0, city.length() - 1);
				SubArea subArea = CityManager.getInstance().getSubAreaByName(city);
				if (subArea != null) {
					cityIdTemp = Integer.parseInt(subArea.getArea_id());
					String cityName = subArea.getArea_name();
					locationAreaTextView.setText(cityName);
				}
			}
			onLocateFinish();
		}
	}

	/**
	 * 定位结束
	 */
	@UiThread
	void onLocateFinish() {
		if (locationClient != null) {
			locationClient.stop();
		}
		reLocattingImageView.clearAnimation(); // 停止动画
	}

	/**
	 * 保存所有子地区
	 */
	private void saveSubAreaList() {
		boolean isFirstTimeSave = AppSetting.getInstance().getBooleanPreferencesByKey(Define.IS_FIRST_TIME_SAVE_SUB_AREA, true);
		if (isFirstTimeSave) {
			AppSetting.getInstance().saveBooleanPreferencesByKey(Define.IS_FIRST_TIME_SAVE_SUB_AREA, false);
			List<AreaParent> areaParents = CityManager.getInstance().getAreaList();
			if (areaParents != null) {
				List<SubArea> subAreas = new ArrayList<>();
				for (AreaParent areaParent : areaParents) {
					List<SubArea> subAreasTemp = areaParent.getCityList();
					for (SubArea subArea : subAreasTemp) {
						subArea.setParent_id(areaParent.getArea_id());
						subArea.setParent_name(areaParent.getArea_name());
					}
					subAreas.addAll(subAreasTemp);
				}

				CityManager.getInstance().saveSubAreaList(subAreas);
			}
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
	 * 点击筛选按钮
	 */
	@Click(R.id.iv_filter)
	void onFilterClick() {
		if (filterPopupWindow == null) {
			initFilterLayout();
		} 
		
		if (!filterPopupWindow.isShowing()) {
			filterPopupWindow.showAsDropDown(titleBar); 
		} else {
			filterPopupWindow.dismiss();
		}
	}

	PopupWindow filterPopupWindow;
	TextView startCityTextView;
	TextView locationAreaTextView;
	ImageView reLocattingImageView;

	/**
	 * 初始化筛选条件布局
	 */
	private void initFilterLayout() {
		View contentView = LayoutInflater.from(LineReferenceActivity.this).inflate(R.layout.popup_reference_line_filter, null);
		filterPopupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		filterPopupWindow.setFocusable(false);
		startCityTextView = (TextView) contentView.findViewById(R.id.tv_start_city);
		startCityTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LineReferenceActivity.this, AreaListActivity_.class);
				intent.putExtra(Define.FROM, LineReferenceActivity.class.getSimpleName());
				startActivityForResult(intent, REQUESTCODE_SELECT_CITY);
			}
		});
		// 旋转动画
		final RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(1000);
		animation.setRepeatCount(Integer.MAX_VALUE);
		locationAreaTextView = (TextView) contentView.findViewById(R.id.tv_locating_area);
		locationAreaTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String locationString = locationAreaTextView.getText().toString();
				if (!locationString.equals(locatingTips) && !locationString.equals(locatingFailTips)) {
					startCityTextView.setText(locationAreaTextView.getText());
					cityId = cityIdTemp;
					filterPopupWindow.dismiss();
					((ReferenceListFragment_)currentFragment).refresh();
				}
			}
		});
		reLocattingImageView = (ImageView) contentView.findViewById(R.id.iv_relocating);
		TextView reLocattingTextView = (TextView) contentView.findViewById(R.id.tv_relocating);
		reLocattingTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (countDownTimer != null) {
					countDownTimer.cancel();
				}
				locationAreaTextView.setText(locatingTips);
				reLocattingImageView.clearAnimation();
				reLocattingImageView.startAnimation(animation);
				startLocating();
			}
		});
		reLocattingTextView.performClick();
	}

	CountDownTimer countDownTimer;

	/**
	 * 开始定位
	 */
	@UiThread
	void startLocating() {
		initLocation();
		locationClient.start();
		/**
		 * 定位倒计时，10s后停止定位
		 */
		countDownTimer = new CountDownTimer(10 * 1000, 1 * 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {
				if (locationClient != null) {
					locationClient.stop();
				}

				String locateString = locationAreaTextView.getText().toString();
				if (locateString.equals(locatingTips)) {
					// 设置定位失败
					locationAreaTextView.setText(locatingFailTips);
					reLocattingImageView.clearAnimation(); // 停止动画
				}
			}
		};
		countDownTimer.start();
	}

	/**
	 * 点击个人线路tab
	 */
	@Click(R.id.tv_line_self)
	void onLineSelfClick() {
		lineSelfTextView.setTextColor(getResources().getColor(R.color.v16b8eb));
		lineSelfUnderLineView.setVisibility(View.VISIBLE);

		publicLineTextView.setTextColor(getResources().getColor(R.color.v020202));
		publicLineUnderLineView.setVisibility(View.INVISIBLE);

		if (selfLineFragment != null) {
			startLineBottomLineAnimation();
			switchFragment(selfLineFragment);
		}
	}

	/**
	 * 开启个人线路底部蓝色条动画
	 */
	private void startLineBottomLineAnimation() {
		publicLineUnderLineView.setVisibility(View.INVISIBLE);
		Animation leftOutAnimation = AnimationUtils.loadAnimation(LineReferenceActivity.this, R.anim.move_left_out);
		leftOutAnimation.setFillAfter(false);
		publicLineUnderLineView.setAnimation(leftOutAnimation);
		// ///
		lineSelfUnderLineView.setVisibility(View.VISIBLE);
		Animation leftInAnimation = AnimationUtils.loadAnimation(LineReferenceActivity.this, R.anim.move_left_in);
		leftInAnimation.setFillAfter(true);
		lineSelfUnderLineView.setAnimation(leftInAnimation);
	}

	/**
	 * 点击公共线路tab
	 */
	@Click(R.id.tv_public_line)
	void onPublicLineClick() {
		lineSelfTextView.setTextColor(getResources().getColor(R.color.v020202));
		lineSelfUnderLineView.setVisibility(View.INVISIBLE);

		publicLineTextView.setTextColor(getResources().getColor(R.color.v16b8eb));
		publicLineUnderLineView.setVisibility(View.VISIBLE);

		if (publicLineFragment != null) {
			startPublicLineBottomLineAnimation();
			switchFragment(publicLineFragment);
		}
	}

	/**
	 * 开启公共线路底部蓝色条动画
	 */
	private void startPublicLineBottomLineAnimation() {
		publicLineUnderLineView.setVisibility(View.VISIBLE);
		Animation rightInAnimation = AnimationUtils.loadAnimation(LineReferenceActivity.this, R.anim.move_right_in);
		rightInAnimation.setFillAfter(true);
		publicLineUnderLineView.setAnimation(rightInAnimation);
		// ////
		lineSelfUnderLineView.setVisibility(View.INVISIBLE);
		Animation rightOutAnimation = AnimationUtils.loadAnimation(LineReferenceActivity.this, R.anim.move_right_out);
		rightOutAnimation.setFillAfter(false);
		lineSelfUnderLineView.setAnimation(rightOutAnimation);
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

	final int REQUESTCODE_SELECT_CITY = 1; // 选择城市

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUESTCODE_SELECT_CITY:
			if (resultCode == RESULT_OK && data != null) {
				String cityName = data.getStringExtra(Define.SELECTED_AREA_STRING);
				startCityTextView.setText(cityName);
				cityId = data.getIntExtra(Define.SELECTED_SUB_AREA_ID, 0);
				filterPopupWindow.dismiss();
				((ReferenceListFragment)currentFragment).refresh(cityId);
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
