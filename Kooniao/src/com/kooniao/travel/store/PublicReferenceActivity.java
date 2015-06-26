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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

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
 * 公共节点引用
 * 
 * @author ke.wei.quan
 *
 */
@SuppressLint({ "HandlerLeak", "InflateParams" })
@EActivity(R.layout.activity_reference_public)
public class PublicReferenceActivity extends BaseFragmentActivity {
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
			referenceType=intent.getStringExtra(Define.REFERENCE_TYPE);
		}
		
		locatingFailTips = getResources().getString(R.string.locating_fail);
		locatingTips = getResources().getString(R.string.locating); 
	}

	ReferenceListFragment_ publicLineFragment; // 列表页

	/**
	 * 初始化fragment
	 */
	private void initFragment() {
		publicLineFragment = new ReferenceListFragment_();

		Bundle publicLineData = new Bundle();
		publicLineData.putString(Define.REFERENCE_TYPE, referenceType);
		publicLineData.putInt(Define.CID, cityId);
		publicLineFragment.setArguments(publicLineData);

		getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, publicLineFragment).commit();
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
	TextView destinationTextView;
	TextView locationAreaTextView;
	ImageView reLocattingImageView;
	TextView nodeTypeTextView;
	TextView hotelTextView;
	TextView scenicTextView;
	TextView lifeStyleTextView;

	/**
	 * 初始化筛选条件布局
	 */
	private void initFilterLayout() {
		View contentView = LayoutInflater.from(PublicReferenceActivity.this).inflate(R.layout.popup_reference_public_filter, null);
		filterPopupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		filterPopupWindow.setFocusable(false);
		destinationTextView = (TextView) contentView.findViewById(R.id.tv_destination);
		destinationTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PublicReferenceActivity.this, AreaListActivity_.class);
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
					destinationTextView.setText(locationAreaTextView.getText());
					cityId = cityIdTemp;
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
		nodeTypeTextView = (TextView) contentView.findViewById(R.id.tv_node_type);

		hotelTextView = (TextView) contentView.findViewById(R.id.tv_hotel);
		hotelTextView.setOnClickListener(new OnFilterItemClickListener());
		scenicTextView = (TextView) contentView.findViewById(R.id.tv_scenic);
		scenicTextView.setSelected(true); 
		scenicTextView.setOnClickListener(new OnFilterItemClickListener());
		lifeStyleTextView = (TextView) contentView.findViewById(R.id.tv_lifestyle);
		lifeStyleTextView.setOnClickListener(new OnFilterItemClickListener());
		if(referenceType!=null){
			if (referenceType.equals("hotel")) {
				contentView.findViewById(R.id.tv_hotel).performClick();
			}
			if (referenceType.equals("scenic")) {
				contentView.findViewById(R.id.tv_scenic).performClick();
			}
			if (referenceType.equals("lifestyle")) {
				contentView.findViewById(R.id.tv_lifestyle).performClick();
			}
		}
		TextView confirmTextView = (TextView) contentView.findViewById(R.id.tv_confirm);
		confirmTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cityId = cityIdTemp;
				filterPopupWindow.dismiss();
				publicLineFragment.refresh(cityId, referenceType); 
			}
		});
	}
	
	private String referenceType="scenic";

	class OnFilterItemClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_hotel: // 酒店
				referenceType = "hotel";
				nodeTypeTextView.setText(hotelTextView.getText());
				changeSelectNodeType(true, false, false);
				break;

			case R.id.tv_scenic: // 景点
				referenceType = "scenic";
				nodeTypeTextView.setText(scenicTextView.getText());
				changeSelectNodeType(false, true, false);
				break;

			case R.id.tv_lifestyle: // 吃喝玩乐
				referenceType = "lifestyle";
				nodeTypeTextView.setText(lifeStyleTextView.getText());
				changeSelectNodeType(false, false, true);
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 更改选择节点的类型
	 */
	private void changeSelectNodeType(boolean isSelectHotel, boolean isSelectScenic, boolean isSelectLifeStyle) {
		hotelTextView.setSelected(isSelectHotel);
		scenicTextView.setSelected(isSelectScenic);
		lifeStyleTextView.setSelected(isSelectLifeStyle);
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

	final int REQUESTCODE_SELECT_CITY = 1; // 选择城市

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUESTCODE_SELECT_CITY:
			if (resultCode == RESULT_OK && data != null) {
				String cityName = data.getStringExtra(Define.SELECTED_AREA_STRING);
				destinationTextView.setText(cityName);
				cityIdTemp = data.getIntExtra(Define.SELECTED_SUB_AREA_ID, 0);
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
