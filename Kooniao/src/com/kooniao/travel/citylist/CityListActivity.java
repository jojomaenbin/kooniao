package com.kooniao.travel.citylist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.SideBar;
import com.kooniao.travel.customwidget.SideBar.OnTouchingLetterChangedListener;
import com.kooniao.travel.manager.CityManager;
import com.kooniao.travel.manager.CityManager.CityListResultCallback;
import com.kooniao.travel.model.City;
import com.kooniao.travel.utils.CharacterParser;
import com.kooniao.travel.utils.CityPinyinComparator;

/**
 * 城市列表
 * 
 * @author ke.wei.quan
 * 
 */
@SuppressLint("InflateParams")
@EActivity(R.layout.activity_city_list)
public class CityListActivity extends BaseActivity {

	@ViewById(R.id.lv_city)
	ListView listView;
	@ViewById(R.id.tv_city_selected_tips)
	TextView tipsTextView; // 选择城市提示
	@ViewById(R.id.sb_right)
	SideBar sideBar; // 侧边栏

	@AfterViews
	void init() {
		initData();
		initBaiDuLocationService();
		initView();
		loadCityList();
	}

	private SortAdapter adapter;
	private String from = ""; // 来自哪
	String locationTips = "正在定位";
	String locationErrTips = "定位失败";
	String locateCityName; // 定位城市名
	City locateCity; // 定位的城市

	/**
	 * 初始化数据
	 */
	private void initData() {
		showProgressDialog();
		Intent intent = getIntent();
		if (intent != null) {
			if (intent.hasExtra(Define.FROM)) {
				from = intent.getStringExtra(Define.FROM);
			}
		}

		/**
		 * 定位倒计时，10s后停止定位
		 */
		new CountDownTimer(10 * 1000, 1 * 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {
				if (locationClient != null) {
					locationClient.stop();
				}

				if (locateCity != null && locateCity.getName().equals(locationTips)) {
					locateCity.setName(locationErrTips);
				}

				adapter.notifyDataSetChanged();
			}
		}.start();
	}

	/**
	 * 定位参数
	 */
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor = "gcj02";
	public LocationClient locationClient; // 百度定位
	public KooniaoLocationListener locationListener; // 定位监听

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

	/**
	 * 初始化百度定位服务
	 */
	private void initBaiDuLocationService() {
		locationClient = new LocationClient(this.getApplicationContext());
		locationListener = new KooniaoLocationListener();
		locationClient.registerLocationListener(locationListener);
	}

	/**
	 * 实现实位回调监听
	 */
	public class KooniaoLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			locateCityName = location.getCity();
			if (locateCityName != null) {
				if (locateCityName.length() > 1) {
					locateCityName = locateCityName.substring(0, locateCityName.length() - 1);
					if (Define.AROUND.equals(from)) { // 如果是来自附近页
						String locationName = location.getDistrict() + location.getStreet();
						locateCity.setName(locationName);
					} else {
						locateCity.setName(locateCityName);
					}

					onLocateFinish();
				}
			}

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
		adapter.notifyDataSetChanged();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		sideBar.setTextView(tipsTextView);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					listView.setSelection(position);
				}

			}
		});
		adapter = new SortAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				City city = ((City) adapter.getItem(position));
				if (!city.getName().equals(locationTips) && !city.getName().equals(locationErrTips)) {
					// 返回数据
					Intent data = new Intent();
					Bundle extras = new Bundle();
					if (locateCityName != null) {
						if (city.isLocateCity()) {
							City localCity = CityManager.getInstance().getCityByName(locateCityName);
							city.setId(localCity.getId());
						}
					}
					extras.putSerializable(Define.DATA, city);
					data.putExtras(extras);
					setResult(Activity.RESULT_OK, data);
					finish();
				}
			}
		});
	}

	private List<City> cityDatas = new ArrayList<City>(); // 城市列表数据

	/**
	 * 加载城市列表
	 */
	private void loadCityList() {
		CityManager.getInstance().loadHotCities(new CityListResultCallback() {

			@Override
			public void result(String errMsg, List<City> hotCities, List<City> allCities) {
				dissmissProgressDialog();
				if (hotCities == null) {
					cityDatas.add(initLocateCity());
					cityDatas.addAll(allCities);
					setLetter(cityDatas);
					// 根据a-z进行排序源数据
					Collections.sort(cityDatas, new CityPinyinComparator());
					startLocating();
				} else { 
					cityDatas.add(initLocateCity());
					cityDatas.addAll(hotCities);
					cityDatas.addAll(allCities);
					setLetter(cityDatas);
					// 根据a-z进行排序源数据
					Collections.sort(cityDatas, new CityPinyinComparator());
					startLocating();
				}
				
				if (errMsg != null) {
					showToast(errMsg);
				}
				adapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * 开始定位
	 */
	@UiThread
	void startLocating() {
		initLocation();
		locationClient.start();
	}

	/**
	 * 定位城市
	 * 
	 * @return
	 */
	private City initLocateCity() {
		locateCity = new City();
		locateCity.setHotCity(false);
		locateCity.setLocateCity(true);
		locateCity.setName(locationTips);
		locateCity.setSortLetters("定");
		return locateCity;
	}

	/**
	 * 设置侧边栏的分割字母
	 * 
	 * @param sortModelList
	 */
	private void setLetter(List<City> sortModelList) {
		for (int i = 0; i < sortModelList.size(); i++) {
			City sortModel = sortModelList.get(i);
			if (sortModel.isLocateCity()) {
				sortModel.setSortLetters("定");
			} else {
				if (!sortModel.isHotCity()) {
					// 汉字转换成拼音
					String pinyin = CharacterParser.getInstance().getSelling(sortModel.getName());
					String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINA);
					sortModel.setSortLetters(sortString.toUpperCase(Locale.CHINA));
				} else {
					sortModel.setSortLetters("热");
				}
			}
		}
	}

	/**
	 * 后退按钮的点击
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	class SortAdapter extends BaseAdapter implements SectionIndexer {

		public SortAdapter() {
		}

		public int getCount() {
			return cityDatas == null ? 0 : cityDatas.size();
		}

		public Object getItem(int position) {
			return cityDatas == null ? null : cityDatas.get(position);
		}

		public long getItemId(int position) {
			return cityDatas == null ? 0 : position;
		}

		public View getView(final int position, View view, ViewGroup arg2) {
			ViewHolder viewHolder = null;
			final City city = cityDatas.get(position);
			if (view == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(CityListActivity.this).inflate(R.layout.item_sortletter, null);
				viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_city_title);
				viewHolder.tvLetter = (TextView) view.findViewById(R.id.tv_city_selected_category);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			// 根据position获取分类的首字母的Char ascii值
			int section = getSectionForPosition(position);

			// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
			if (position == getPositionForSection(section)) {
				viewHolder.tvLetter.setVisibility(View.VISIBLE);
				String sortLetter = city.getSortLetters();
				if (sortLetter.equals("定")) {
					sortLetter = "定位城市";
				} else if (sortLetter.equals("热")) {
					sortLetter = "热门城市";
				}
				viewHolder.tvLetter.setText(sortLetter);
			} else {
				viewHolder.tvLetter.setVisibility(View.GONE);
			}

			viewHolder.tvTitle.setText(city.getName());

			return view;
		}

		/**
		 * 根据ListView的当前位置获取分类的首字母的Char ascii值
		 */
		@Override
		public int getSectionForPosition(int position) {
			return cityDatas == null ? 0 : cityDatas.get(position).getSortLetters().charAt(0);
		}

		/**
		 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
		 */
		@Override
		public int getPositionForSection(int section) {
			for (int i = 0; i < getCount(); i++) {
				String sortStr = cityDatas.get(i).getSortLetters();
				char firstChar = sortStr.toUpperCase(Locale.CHINA).charAt(0);
				if (firstChar == section) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public Object[] getSections() {
			return null;
		}
	}

	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
	}

	@Override
	protected void onStop() {
		if (locationClient != null) {
			locationClient.stop();
		}
		super.onStop();
	}

}
