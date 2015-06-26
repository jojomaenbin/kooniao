package com.kooniao.travel.citylist;

import java.io.Serializable;
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
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

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
import com.kooniao.travel.model.AreaParent;
import com.kooniao.travel.model.SubArea;
import com.kooniao.travel.store.LineReferenceActivity;
import com.kooniao.travel.store.ProductResourceLibActivity;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.AreaParentPinyinComparator;

/**
 * 地区列表
 * 
 * @author ke.wei.quan
 * @date 2015年5月21日
 * @description 
 *              可通过intent传递子地区(Define.SUB_AREA，整个页面返回所选地区id(Define.SELECTED_AREA_ID
 *              )，子地区id(Define.SELECTED_SUB_AREA_ID)，地区名称+子地区名称(Define.
 *              SELECTED_AREA_STRING))
 *
 */
@SuppressLint("InflateParams")
@EActivity(R.layout.activity_area_list)
public class AreaListActivity extends BaseActivity {
	@ViewById(R.id.lv_area)
	ListView listView;
	@ViewById(R.id.tv_area_selected_tips)
	TextView tipsTextView; // 选择城市提示
	@ViewById(R.id.sb_right)
	SideBar sideBar; // 侧边栏

	@AfterViews
	void init() {
		initData();
		initBaiDuLocationService();
		initView();
		initCityList();
	}

	String from; // 来自哪个activity
	String areaSelectedString; // 已经选择的地区名称
	String subAreaString; // 上个页面传递过来的子地区
	String chinaString; // 中国String
	String currentLocationString; // 当前位置String
	String allareaString; // 全部地区String
	private SortAdapter adapter;
	String locationTips = "正在定位";
	String locationErrTips = "定位失败";
	String locateAreaName = ""; // 定位地区名
	AreaParent locateArea; // 定位的地区

	/**
	 * 初始化数据
	 */
	private void initData() {
		showProgressDialog();
		Intent intent = getIntent();
		if (intent != null) {
			subAreaString = intent.getStringExtra(Define.SUB_AREA);
			from = intent.getStringExtra(Define.FROM);
		}
		chinaString = getResources().getString(R.string.china);
		currentLocationString = getResources().getString(R.string.current_location);
		allareaString = getResources().getString(R.string.all_area);
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

				if (locateArea != null && locateArea.getArea_name().equals(locationTips)) {
					locateArea.setArea_name(locationErrTips);
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
			String city = location.getCity();
			if (city.length() > 1) {
				city = city.substring(0, city.length() - 1);
				locateAreaName = chinaString + " " + city;
				locateArea.setArea_name(locateAreaName);
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
		adapter.notifyDataSetChanged();
	}

	private int lastClickItemIndex = 0; // 上次点击的item位置

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
				AreaParent areaParent = ((AreaParent) adapter.getItem(position));
				List<SubArea> areaList = areaParents.get(position).getCityList();
				if (!areaParent.getArea_name().equals(locationTips) && !areaParent.getArea_name().equals(locationErrTips)) {
					// 返回数据
					if (position == 0) {
						onLocatingItemClick(areaParent);
					} else {
						lastClickItemIndex = position;
						Intent data = new Intent(AreaListActivity.this, SubAreaListActivity_.class);
						Bundle extras = new Bundle();
						extras.putSerializable(Define.DATA, (Serializable) areaList);
						data.putExtras(extras);
						startActivityForResult(data, REQUEST_CODE_SUB_AREA);
					}
				}
			}
		});
	}

	/**
	 * 点击定位的item
	 */
	private void onLocatingItemClick(AreaParent areaParent) {
		if (!areaParent.getArea_name().equals(locationTips) && !areaParent.getArea_name().equals(locationErrTips)) {
			String subAreaName = selectedAreaString = areaParent.getArea_name();
			int index = subAreaName.lastIndexOf(" ");
			subAreaName = subAreaName.substring(index + 1, areaParent.getArea_name().length()); 
			if (ProductResourceLibActivity.class.getSimpleName().equals(from) || LineReferenceActivity.class.getSimpleName().equals(from)) {
				selectedAreaString = subAreaName;
			}
			SubArea subArea = CityManager.getInstance().getSubAreaByName(subAreaName);
			selectedAreaId = 3237; // 中国
			selectedSubAreaId = Integer.parseInt(subArea.getArea_id());
			finishCurrentView();
		}
	}

	private List<AreaParent> areaParents = new ArrayList<AreaParent>(); // 地区列表数据

	/**
	 * 初始化城市列表
	 */
	private void initCityList() {
		dissmissProgressDialog();
		areaParents = CityManager.getInstance().getAreaList();
		areaParents.add(initLocateArea());
		setLetter(areaParents);
		// 根据a-z进行排序源数据
		Collections.sort(areaParents, new AreaParentPinyinComparator());
		areaParents.get(1).setCategory(allareaString);
		saveSubAreaList();
		startLocating();
		if (subAreaString != null) {
			areaSelectedString = CityManager.getInstance().getAreaParentNameBySubAreaName(subAreaString);
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * 保存所有子地区
	 */
	private void saveSubAreaList() {
		boolean isFirstTimeSave = AppSetting.getInstance().getBooleanPreferencesByKey(Define.IS_FIRST_TIME_SAVE_SUB_AREA, true);
		if (isFirstTimeSave) {
			AppSetting.getInstance().saveBooleanPreferencesByKey(Define.IS_FIRST_TIME_SAVE_SUB_AREA, false);
			List<AreaParent> areas = new ArrayList<AreaParent>();
			areas.addAll(areaParents);
			areas.remove(0);
			List<SubArea> subAreas = new ArrayList<>();
			for (AreaParent areaParent : areas) {
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
	private AreaParent initLocateArea() {
		locateArea = new AreaParent();
		locateArea.setArea_name(locationTips);
		locateArea.setCategory(currentLocationString);
		locateArea.setShort_spell_name("#");
		locateArea.setSort_letter("#");
		return locateArea;
	}

	/**
	 * 设置侧边栏的分割字母
	 * 
	 * @param sortModelList
	 */
	private void setLetter(List<AreaParent> sortModelList) {
		for (int i = 0; i < sortModelList.size(); i++) {
			AreaParent sortModel = sortModelList.get(i);
			// 汉字转换成英文字母
			String pinyin = sortModel.getShort_spell_name();
			String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINA);
			sortModel.setSort_letter(sortString.toUpperCase(Locale.CHINA));
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
			return areaParents == null ? 0 : areaParents.size();
		}

		public Object getItem(int position) {
			return areaParents == null ? null : areaParents.get(position);
		}

		public long getItemId(int position) {
			return areaParents == null ? 0 : position;
		}

		public View getView(final int position, View view, ViewGroup arg2) {
			ViewHolder viewHolder = null;
			final AreaParent area = areaParents.get(position);
			if (view == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(AreaListActivity.this).inflate(R.layout.item_area, null);
				viewHolder.locatingImageView = (ImageView) view.findViewById(R.id.iv_area_locating);
				viewHolder.titleTextView = (TextView) view.findViewById(R.id.tv_area_title);
				viewHolder.selectedTitleTextView = (TextView) view.findViewById(R.id.tv_area_selected_title);
				viewHolder.categoryLayout = view.findViewById(R.id.ll_area_category);
				viewHolder.categoryTextView = (TextView) view.findViewById(R.id.tv_area_category);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			String category = area.getCategory();
			if (currentLocationString.equals(category)) {
				viewHolder.categoryTextView.setVisibility(View.VISIBLE);
				viewHolder.categoryTextView.setText(currentLocationString);
				viewHolder.categoryLayout.setVisibility(View.VISIBLE);
				viewHolder.locatingImageView.setVisibility(View.VISIBLE);
			} else if (allareaString.equals(category)) {
				viewHolder.categoryTextView.setVisibility(View.VISIBLE);
				viewHolder.categoryTextView.setText(allareaString);
				viewHolder.categoryLayout.setVisibility(View.VISIBLE);
				viewHolder.locatingImageView.setVisibility(View.GONE);
			} else {
				viewHolder.categoryLayout.setVisibility(View.GONE);
				viewHolder.locatingImageView.setVisibility(View.GONE);
			}

			if (area.getArea_name().equals(areaSelectedString)) {
				area.setSelected(true);
			} else {
				area.setSelected(false);
			}

			if (area.isSelected()) {
				viewHolder.selectedTitleTextView.setText(R.string.selected);
			} else {
				viewHolder.selectedTitleTextView.setText("");
			}

			String title = area.getArea_name();
			viewHolder.titleTextView.setText(title);

			return view;
		}

		/**
		 * 根据ListView的当前位置获取分类的首字母的Char ascii值
		 */
		@Override
		public int getSectionForPosition(int position) {
			return areaParents == null ? 0 : areaParents.get(position).getSort_letter().charAt(0);
		}

		/**
		 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
		 */
		@Override
		public int getPositionForSection(int section) {
			for (int i = 0; i < getCount(); i++) {
				String sortStr = areaParents.get(i).getSort_letter();
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
		ImageView locatingImageView;
		TextView titleTextView;
		TextView selectedTitleTextView;
		View categoryLayout;
		TextView categoryTextView;
	}

	@Override
	protected void onStop() {
		if (locationClient != null) {
			locationClient.stop();
		}
		super.onStop();
	}

	private String selectedAreaString; // 所选择的地区
	private int selectedAreaId; // 所选地区的id
	private int selectedSubAreaId; // 所选地区的子地区id

	/**
	 * 结束当前页面
	 */
	private void finishCurrentView() {
		Intent intent = new Intent();
		intent.putExtra(Define.SELECTED_AREA_STRING, selectedAreaString);
		intent.putExtra(Define.SELECTED_AREA_ID, selectedAreaId);
		intent.putExtra(Define.SELECTED_SUB_AREA_ID, selectedSubAreaId);
		setResult(RESULT_OK, intent);
		finish();
	}

	final int REQUEST_CODE_SUB_AREA = 1; // 启动子地区选择列表

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_SUB_AREA:
			if (resultCode == RESULT_OK && data != null) {
				SubArea subArea = (SubArea) data.getSerializableExtra(Define.SELECTED_SUB_AREA);
				AreaParent areaParent = areaParents.get(lastClickItemIndex);
				selectedAreaId = Integer.parseInt(areaParent.getArea_id());
				selectedSubAreaId = Integer.parseInt(subArea.getArea_id());
				if (ProductResourceLibActivity.class.getSimpleName().equals(from)) {
					selectedAreaString = subArea.getArea_name();
				} else {
					selectedAreaString = areaParent.getArea_name() + " " + subArea.getArea_name();
				}
				finishCurrentView();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
