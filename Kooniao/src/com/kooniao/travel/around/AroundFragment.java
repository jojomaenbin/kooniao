package com.kooniao.travel.around;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.kooniao.travel.BaiDuMapActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.BaiDuMapActivity.From;
import com.kooniao.travel.base.BaseFragment;
import com.kooniao.travel.citylist.CityListActivity_;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.customwidget.PagerSlidingTabStrip;
import com.kooniao.travel.model.Around;
import com.kooniao.travel.model.City;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.StringUtil;

/**
 * 附近主页
 * 
 * @author ke.wei.quan
 * 
 */
@EFragment(R.layout.fragment_arround)
public class AroundFragment extends BaseFragment {

	@ViewById(R.id.tv_city_title)
	TextView cityTitleTextView; // 标题城市名称
	@ViewById(R.id.tabs_arround)
	PagerSlidingTabStrip tabStrip; // 顶部切换
	@ViewById(R.id.vp_arround)
	ViewPager viewPager;

	KooniaoProgressDialog progressDialog;

	@AfterViews
	void init() {
		progressDialog = new KooniaoProgressDialog(getActivity());
		progressDialog.setMessage("正在定位");
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}

		initData();
		// 初始化百度定位服务
		initBaiDuLocationService();
		// 初始化定位信息
		initLocation();
		// 开启定位
		locationClient.start();
	}

	private String[] arroundTypes; // 周边推荐的类型
	private City city = new City();

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		// 设置切换tab分类
		arroundTypes = getResources().getStringArray(R.array.arround_type);
		// 初始化城市
		float lon = AppSetting.getInstance().getFloatPreferencesByKey(Define.CITY_NAME_AROUND_LON);
		float lat = AppSetting.getInstance().getFloatPreferencesByKey(Define.CITY_NAME_AROUND_LAT);
		city.setLon(lon);
		city.setLat(lat);
		city.setId(3544); // 默认广州

		// 设置城市标题
		String lastCityName = AppSetting.getInstance().getStringPreferencesByKey(Define.CITY_NAME_AROUND);
		lastCityName = lastCityName == null ? StringUtil.getStringFromR(R.string.default_city_gz) : lastCityName;
		cityTitleTextView.setText(lastCityName);
	}

	private AroundPagerAdapter adapter;

	/**
	 * 初始化界面
	 */
	private void initView() {
		adapter = new AroundPagerAdapter(((FragmentActivity) getActivity()).getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(5);
		// 顶部切换tab
		tabStrip.setDividerColor(getResources().getColor(R.color.transparent));
		tabStrip.setIndicatorColor(getResources().getColor(R.color.v16b8eb));
		tabStrip.setViewPager(viewPager);
		if (tabPosition != 1) {
			setPagerPosition(tabPosition);
		}
	}

	public int tabPosition = 1;

	/**
	 * 选择tab
	 * 
	 * @param position
	 */
	public void setPagerPosition(int tabPosition) {
		this.tabPosition = tabPosition;
		if (tabStrip != null) {
			tabStrip.setPagerPosition(tabPosition);
		}
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
		locationClient = new LocationClient(getActivity());
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
		// option.setScanSpan(1000);//设置发起定位请求的间隔时间为1000ms
		option.setIsNeedAddress(true);
		option.setTimeOut(10 * 1000); // 定位超时
		locationClient.setLocOption(option);
	}

	/**
	 * 实现实位回调监听
	 */
	public class KooniaoLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null) {
				String district = location.getDistrict();
				String street = location.getStreet();
				if (district != null && street != null) {
					String name = location.getDistrict() + location.getStreet();
					if (name != null) {
						city.setName(name);
						city.setLat(location.getLatitude());
						city.setLon(location.getLongitude());
						cityTitleTextView.setText(name);
						// 保存到本地
						AppSetting.getInstance().saveStringPreferencesByKey(Define.CITY_NAME_AROUND, name);
						AppSetting.getInstance().saveFloatPreferencesByKey(Define.CITY_NAME_AROUND_LON, (float) location.getLongitude());
						AppSetting.getInstance().saveFloatPreferencesByKey(Define.CITY_NAME_AROUND_LAT, (float) location.getLatitude());
					}
				}
			}

			initView();
			locationClient.stop(); // 停止定位
			progressDialog.dismiss();
		}
	}

	final int FRAGMENT_SCENIC = 0; // 景点
	final int FRAGMENT_HOTEL = 1; // 酒店
	final int FRAGMENT_FOOD = 2; // 美食
	final int FRAGMENT_SHOPPING = 3; // 购物
	final int FRAGMENT_AMUSEMENT = 4; // 娱乐

	private int currentViewIndex = -1; // 当前view的索引
	private AroundSubListFragment_ currentFragment;
	private AroundSubListFragment_ scenicFragment; // 景点
	private AroundSubListFragment_ hotelFragment; // 酒店
	private AroundSubListFragment_ foodFragment; // 美食
	private AroundSubListFragment_ shoppingFragment; // 购物
	private AroundSubListFragment_ amusementFragment; // 娱乐

	class AroundPagerAdapter extends FragmentPagerAdapter {

		public AroundPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return arroundTypes[position];
		}

		@Override
		public int getCount() {
			return arroundTypes.length;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			super.setPrimaryItem(container, position, object);
			if (currentViewIndex != position) {
				currentViewIndex = position;
				loadFragmentData();
			}
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case FRAGMENT_SCENIC: // 景点
				if (scenicFragment == null) {
					scenicFragment = AroundSubListFragment.newInstance(city);
				}
				currentFragment = scenicFragment;
				break;
			case FRAGMENT_HOTEL: // 酒店
				if (hotelFragment == null) {
					hotelFragment = AroundSubListFragment.newInstance(city);
				}
				currentFragment = hotelFragment;
				break;
			case FRAGMENT_FOOD: // 美食
				if (foodFragment == null) {
					foodFragment = AroundSubListFragment.newInstance(city);
				}
				currentFragment = foodFragment;
				break;
			case FRAGMENT_SHOPPING: // 购物
				if (shoppingFragment == null) {
					shoppingFragment = AroundSubListFragment.newInstance(city);
				}
				currentFragment = shoppingFragment;
				break;
			case FRAGMENT_AMUSEMENT: // 娱乐
				if (amusementFragment == null) {
					amusementFragment = AroundSubListFragment.newInstance(city);
				}
				currentFragment = amusementFragment;
				break;
			}
			return currentFragment;
		}
	}

	private boolean isFirstLoadScenicData = true; // 是否是第一次加载景点数据
	private boolean isFirstLoadHotelData = true; // 是否是第一次加载酒店数据
	private boolean isFirstLoadFoodData = true; // 是否是第一次加载美食数据
	private boolean isFirstLoadShoppingData = true; // 是否是第一次加载购物数据
	private boolean isFirstLoadAmusementData = true; // 是否是第一次加载娱乐数据

	/**
	 * 加载数据
	 */
	private void loadFragmentData() {
		switch (currentViewIndex) {
		case FRAGMENT_SCENIC: // 景点
			scenicFragment.setType(Define.LOCATION);
			if (isFirstLoadScenicData) {
				isFirstLoadScenicData = false;
				scenicFragment.refreshData();
			}
			break;
		case FRAGMENT_HOTEL: // 酒店
			hotelFragment.setType(Define.HOTEL);
			if (isFirstLoadHotelData) {
				isFirstLoadHotelData = false;
				hotelFragment.refreshData();
			}
			break;
		case FRAGMENT_FOOD: // 美食
			foodFragment.setType(Define.FOOD);
			if (isFirstLoadFoodData) {
				isFirstLoadFoodData = false;
				foodFragment.refreshData();
			}
			break;
		case FRAGMENT_SHOPPING: // 购物
			shoppingFragment.setType(Define.SHOPPING);
			if (isFirstLoadShoppingData) {
				isFirstLoadShoppingData = false;
				shoppingFragment.refreshData();
			}
			break;
		case FRAGMENT_AMUSEMENT: // 娱乐
			amusementFragment.setType(Define.AMUSEMENT);
			if (isFirstLoadAmusementData) {
				isFirstLoadAmusementData = false;
				amusementFragment.refreshData();
			}
			break;
		}
	}

	/**
	 * 切换城市
	 */
	@Click(R.id.tv_city_title)
	void onCityClick() {
		Intent cityIntent = new Intent(getActivity(), CityListActivity_.class);
		startActivityForResult(cityIntent, REQUEST_CODE_CITY);
	}

	/**
	 * 切换至地图模式
	 */
	@Click(R.id.iv_map_change)
	void onMapChangeClick() {
		List<Around> arounds = new ArrayList<Around>();
		switch (currentViewIndex) {
		case FRAGMENT_SCENIC: // 景点
			arounds = scenicFragment.getAroundList();
			break;
		case FRAGMENT_HOTEL: // 酒店
			arounds = hotelFragment.getAroundList();
			break;
		case FRAGMENT_FOOD: // 美食
			arounds = foodFragment.getAroundList();
			break;
		case FRAGMENT_SHOPPING: // 购物
			arounds = shoppingFragment.getAroundList();
			break;
		case FRAGMENT_AMUSEMENT: // 娱乐
			arounds = amusementFragment.getAroundList();
			break;
		}
		if (arounds != null && !arounds.isEmpty()) {
			Intent intent = new Intent(getActivity(), BaiDuMapActivity_.class);
			Bundle extras = new Bundle();
			extras.putInt(Define.FROM, From.FROM_AROUND.from);
			extras.putSerializable(Define.AROUND_LIST, (Serializable) arounds);
			intent.putExtras(extras);
			startActivity(intent);
		}
	}

	final int REQUEST_CODE_CITY = 11; // 城市

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_CITY: // 城市返回
			if (resultCode == Activity.RESULT_OK && data != null) {
				city = (City) data.getSerializableExtra(Define.DATA);
				String cityName = city.getName();
				// 保存到本地
				AppSetting.getInstance().saveStringPreferencesByKey(Define.CITY_NAME_AROUND, cityName);
				cityTitleTextView.setText(cityName);
				// 重新获取数据
				isFirstLoadScenicData = true; // 是否是第一次加载景点数据
				isFirstLoadHotelData = true; // 是否是第一次加载酒店数据
				isFirstLoadFoodData = true; // 是否是第一次加载美食数据
				isFirstLoadShoppingData = true; // 是否是第一次加载购物数据
				isFirstLoadAmusementData = true; // 是否是第一次加载娱乐数据
				currentFragment.setCity(city);
				loadFragmentData();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
