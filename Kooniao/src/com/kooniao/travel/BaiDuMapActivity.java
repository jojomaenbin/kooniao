package com.kooniao.travel;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.kooniao.travel.around.AroundDetailActivity_;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.model.Around;
import com.kooniao.travel.model.DayList;
import com.kooniao.travel.model.DayList.NodeList;

/**
 * 百度地图
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_map)
public class BaiDuMapActivity extends BaseActivity {

	public static enum From {
		FROM_TRAVEL_DETAIL(0), // 来自行程详情页
		FROM_AROUND(1), // 来自附近页
		FROM_OTHER(2); // 其他

		public int from;

		From(int from) {
			this.from = from;
		}
	}

	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor bitmapDescriptor = null;
	BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.location);
	BitmapDescriptor bd0 = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark_pt);
	BitmapDescriptor bd1 = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
	BitmapDescriptor bd2 = BitmapDescriptorFactory.fromResource(R.drawable.icon_markb);
	BitmapDescriptor bd3 = BitmapDescriptorFactory.fromResource(R.drawable.icon_markc);
	BitmapDescriptor bd4 = BitmapDescriptorFactory.fromResource(R.drawable.icon_markd);
	BitmapDescriptor bd5 = BitmapDescriptorFactory.fromResource(R.drawable.icon_marke);
	BitmapDescriptor bd6 = BitmapDescriptorFactory.fromResource(R.drawable.icon_markf);
	BitmapDescriptor bd7 = BitmapDescriptorFactory.fromResource(R.drawable.icon_markg);
	BitmapDescriptor bd8 = BitmapDescriptorFactory.fromResource(R.drawable.icon_markh);
	BitmapDescriptor bd9 = BitmapDescriptorFactory.fromResource(R.drawable.icon_marki);
	BitmapDescriptor bd10 = BitmapDescriptorFactory.fromResource(R.drawable.icon_markj);

	@ViewById(R.id.rl_title_bar)
	View titleBarLayout;
	@ViewById(R.id.title)
	TextView titleTextView; // 标题
	@ViewById(R.id.fl_map_container)
	FrameLayout mapContainer; // 地图填充布局容器

	@AfterViews
	void init() {
		initData();
		initView();
	}

	private LatLng startLatLng;
	private boolean isFirstLoc = true;// 是否首次定位
	private BaiduMapOptions mapOptions;
	private MapLocationListenner mapLocationListenner;

	private int from; // 来自哪里
	private List<DayList> dayLists; // 行程详情节点列表
	private List<Around> arounds; // 附近列表数据
	private double initLat = 0;
	private double initLon = 0;
	List<String> titleList = new ArrayList<String>();

	/**
	 * 初始化界面数据
	 */
	@SuppressWarnings("unchecked")
	private void initData() {
		mapLocationListenner = new MapLocationListenner();
		mapOptions = new BaiduMapOptions();
		mapOptions.zoomControlsEnabled(false);
		mapOptions.scaleControlEnabled(false);

		Intent intent = getIntent();
		if (intent != null) {
			dayLists = (List<DayList>) intent.getSerializableExtra(Define.DAY_LIST);
			arounds = (List<Around>) intent.getSerializableExtra(Define.AROUND_LIST);
			initLat = intent.getDoubleExtra(Define.LAT, 0);
			initLon = intent.getDoubleExtra(Define.LON, 0);
			from = intent.getIntExtra(Define.FROM, From.FROM_TRAVEL_DETAIL.from);
		}

		titlebarAdapter = new TitlebarAdapter();
	}

	/*
	 * 界面
	 */
	View titleListLayout;
	PopupWindow titleListPopupWindow;
	ListView titleListView;
	TitlebarAdapter titlebarAdapter;
	LatLng latLng;

	/*
	 * 地图
	 */
	MapView mapView;
	BaiduMap baiduMap;
	LocationClient locationClient; // 定位

	/**
	 * 初始化界面
	 */
	private void initView() {
		if (from == From.FROM_TRAVEL_DETAIL.from) {
			// 来自行程详情
			if (dayLists != null) {
				if (!dayLists.isEmpty()) {
					String firstDayName = dayLists.get(0).getDayTitle();
					titleTextView.setText(firstDayName);
				}
			}

			// 界面数据
			for (DayList day : dayLists) {
				titleList.add(day.getDayTitle());
			}
			titlebarAdapter.notifyDataSetChanged();

			// 添加地图
			mapOptions.mapStatus(new MapStatus.Builder().zoom(14.0f).build());
			mapView = new MapView(this, mapOptions);
			baiduMap = mapView.getMap();
			// 初始化折线
			DayList travelParentNode = dayLists.get(0);
			List<NodeList> travelSubNodes = travelParentNode.getNodeList();
			initPolyLine(travelParentNode.getDayTitle(), travelSubNodes);
			// marker点击事件
			showInfoWindow(travelSubNodes);

		} else if (from == From.FROM_OTHER.from) {
			titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			// 初始化地图基本信息
			latLng = new LatLng(initLat, initLon);
			mapOptions.mapStatus(new MapStatus.Builder().target(latLng).zoom(12.0f).build());
			// 添加地图
			mapView = new MapView(this, mapOptions);
			baiduMap = mapView.getMap();
			// 初始化坐标
			initBitmapDescriptor(0, initLat, initLon);
		} else {
			// 来自附近页
			titleTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			// 初始化地图基本信息
			latLng = new LatLng(arounds.get(0).getLat(), arounds.get(0).getLon());
			mapOptions.mapStatus(new MapStatus.Builder().target(latLng).zoom(12.0f).build());
			// 添加地图
			mapView = new MapView(this, mapOptions);
			baiduMap = mapView.getMap();
			// 初始化坐标
			if (arounds != null) {
				for (int i = 0; i < arounds.size(); i++) {
					Around around = arounds.get(i);
					initBitmapDescriptor(i, around.getLat(), around.getLon());
				}
			}
			// marker点击事件
			baiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

				boolean isInfoWindowShow = false;

				@Override
				public boolean onMarkerClick(final Marker marker) {
					String title = marker.getTitle();
					if (!title.equals("定位")) {
						if (!isInfoWindowShow) {
							final int position = Integer.parseInt(title);
							final Around around = arounds.get(position);
							LinearLayout popTipsLayout = (LinearLayout) LayoutInflater.from(BaiDuMapActivity.this).inflate(R.layout.pop_tips, null);
							TextView textView = (TextView) popTipsLayout.findViewById(R.id.tv_content);
							textView.setText(around.getName());
							RatingBar ratingBar = (RatingBar) popTipsLayout.findViewById(R.id.rb_small_travel_rating);
							ratingBar.setRating(around.getRank());
							// 点击查看详情
							popTipsLayout.findViewById(R.id.ll_detail).setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									Around around = arounds.get(position);
									Intent intent = new Intent(BaiDuMapActivity.this, AroundDetailActivity_.class);
									intent.putExtra(Define.TYPE, around.getType());
									intent.putExtra(Define.ID, around.getId());
									startActivity(intent);
								}
							});
							// 导航图标点击
							popTipsLayout.findViewById(R.id.iv_nacigation).setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									Uri mUri = Uri.parse("geo:" + around.getLat() + "," + around.getLon() + "?q=" + around.getName());
									Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
									PackageManager pm = getPackageManager();
									List<ResolveInfo> mapAppInfos = pm.queryIntentActivities(mIntent, 0);
									if (mapAppInfos.size() > 0) {
										startActivity(mIntent);
									}
								}
							});

							LatLng ll = marker.getPosition();
							InfoWindow infoWindow = new InfoWindow(popTipsLayout, ll, -50);
							baiduMap.showInfoWindow(infoWindow);
							isInfoWindowShow = true;
						} else {
							isInfoWindowShow = false;
							baiduMap.hideInfoWindow();
						}
					}
					return true;
				}
			});
		}

		/*
		 * 初始化标题布局
		 */
		titleListLayout = LayoutInflater.from(this).inflate(R.layout.popupwindow_map_title_list, null);
		titleListView = (ListView) titleListLayout.findViewById(R.id.listview);
		titleListView.setAdapter(titlebarAdapter);
		titleListPopupWindow = new PopupWindow(titleListLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		titleListPopupWindow.setFocusable(false);

		// 添加地图
		mapContainer.addView(mapView);
		// 定位图标
		ImageView locatingImageView = new ImageView(BaiDuMapActivity.this);
		locatingImageView.setImageResource(R.drawable.locating_selector);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (44 * Define.DENSITY), (int) (44 * Define.DENSITY));
		layoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
		locatingImageView.setLayoutParams(layoutParams);
		locatingImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				locationClient.start();
				if (!isFirstLoc) {
					MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(startLatLng);
					baiduMap.animateMapStatus(mapStatusUpdate);
				} else {
					MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(LocationMode.COMPASS, true, null);
					baiduMap.setMyLocationConfigeration(myLocationConfiguration);
				}
				Toast.makeText(BaiDuMapActivity.this, "正在定位", Toast.LENGTH_SHORT).show();
			}
		});
		mapContainer.addView(locatingImageView);

		// 定位初始化
		locationClient = new LocationClient(this);
		locationClient.registerLocationListener(mapLocationListenner);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		locationClient.setLocOption(option);
	}

	class TitlebarAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return titleList.size();
		}

		@Override
		public Object getItem(int position) {
			return titleList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View converView, ViewGroup viewGroup) {
			View view = converView;
			ViewHolder viewHolder = null;
			if (view == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(BaiDuMapActivity.this).inflate(R.layout.item_map_title, null);
				viewHolder.titleTextView = (TextView) view.findViewById(R.id.tv_map_title);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.titleTextView.setText("D" + (position + 1) + "-" + titleList.get(position));
			viewHolder.titleTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String title = titleList.get(position);
					titleTextView.setText(title);
					titleListPopupWindow.dismiss();

					DayList travelParentNode = dayLists.get(position);
					List<NodeList> travelSubNodes = travelParentNode.getNodeList();
					initPolyLine(travelParentNode.getDayTitle(), travelSubNodes);
				}
			});
			return view;
		}
	}

	static class ViewHolder {
		TextView titleTextView;
	}

	/**
	 * 初始化大头钉
	 * 
	 * @param index
	 * @param lat
	 * @param lon
	 */
	private void initBitmapDescriptor(int index, double lat, double lon) {
		LatLng point;
		OverlayOptions overlayOptions;
		point = new LatLng(lat, lon);
		if (index == 0) {
			bitmapDescriptor = bd1;
		} else if (index == 1) {
			bitmapDescriptor = bd2;
		} else if (index == 2) {
			bitmapDescriptor = bd3;
		} else if (index == 3) {
			bitmapDescriptor = bd4;
		} else if (index == 4) {
			bitmapDescriptor = bd5;
		} else if (index == 5) {
			bitmapDescriptor = bd6;
		} else if (index == 6) {
			bitmapDescriptor = bd7;
		} else if (index == 7) {
			bitmapDescriptor = bd8;
		} else if (index == 8) {
			bitmapDescriptor = bd9;
		} else if (index == 9) {
			bitmapDescriptor = bd10;
		} else {
			bitmapDescriptor = bd0;
		}
		overlayOptions = new MarkerOptions().position(point).icon(bitmapDescriptor).title(String.valueOf(index)).zIndex(18);
		baiduMap.addOverlay(overlayOptions);
	}

	/**
	 * 初始化连线
	 * 
	 * @param travelParentNode
	 * @param travelSubNodes
	 */
	private void initPolyLine(String title, List<NodeList> travelSubNodes) {
		baiduMap.clear();
		if (travelSubNodes.size() >= 2) {
			List<LatLng> points = new ArrayList<LatLng>();
			for (int i = 0; i < travelSubNodes.size(); i++) {
				NodeList travelSubNode = travelSubNodes.get(i);
				float lat = (float) travelSubNode.getNodeLat();
				float lon = (float) travelSubNode.getNodeLon();
				initBitmapDescriptor(i, lat, lon);
				LatLng p1 = new LatLng(lat, lon);
				points.add(p1);
			}
			// 添加折线
			OverlayOptions ooPolyline = new PolylineOptions().width(5).color(0xFF16B8EB).points(points);
			baiduMap.addOverlay(ooPolyline);
		} else {
			initBitmapDescriptor(0, (float) travelSubNodes.get(0).getNodeLat(), (float) travelSubNodes.get(0).getNodeLon());
		}
		LatLng firstLatLng = new LatLng(travelSubNodes.get(0).getNodeLat(), travelSubNodes.get(0).getNodeLon());
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(firstLatLng);
		baiduMap.animateMapStatus(mapStatusUpdate);
	}

	/**
	 * 显示marker(行程详情页)
	 * 
	 * @param travelSubNodes
	 */
	private void showInfoWindow(final List<NodeList> travelSubNodes) {
		baiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			boolean isInfoWindowShow = false;

			@Override
			public boolean onMarkerClick(final Marker marker) {
				String title = marker.getTitle();
				if (!title.equals("定位")) {
					if (!isInfoWindowShow) {
						final int position = Integer.parseInt(title);
						final NodeList travelSubNode = travelSubNodes.get(position);
						LinearLayout popTipsLayout = (LinearLayout) LayoutInflater.from(BaiDuMapActivity.this).inflate(R.layout.pop_tips, null);
						TextView textView = (TextView) popTipsLayout.findViewById(R.id.tv_content);
						textView.setText(travelSubNode.getNodeName());
						RatingBar ratingBar = (RatingBar) popTipsLayout.findViewById(R.id.rb_small_travel_rating);
						ratingBar.setVisibility(View.GONE);
						// 导航图标点击
						popTipsLayout.findViewById(R.id.iv_nacigation).setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Uri mUri = Uri.parse("geo:" + travelSubNode.getNodeLat() + "," + travelSubNode.getNodeLon() + "?q=" + travelSubNode.getNodeName());
								Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
								PackageManager pm = getPackageManager();
								List<ResolveInfo> mapAppInfos = pm.queryIntentActivities(mIntent, PackageManager.MATCH_DEFAULT_ONLY);
								if (mapAppInfos.size() > 0) {
									startActivity(mIntent);
								}
							}
						});

						LatLng ll = marker.getPosition();
						InfoWindow infoWindow = new InfoWindow(popTipsLayout, ll, -50);
						baiduMap.showInfoWindow(infoWindow);
						isInfoWindowShow = true;
					} else {
						isInfoWindowShow = false;
						baiduMap.hideInfoWindow();
					}
				}
				return true;
			}
		});
	}

	/**
	 * 点击后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 点击标题
	 */
	@Click(R.id.title)
	void onTitleClick() {
		if (!titleListPopupWindow.isShowing()) {
			titleListPopupWindow.showAsDropDown(titleBarLayout);
		} else {
			titleListPopupWindow.dismiss();
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MapLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
			// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			baiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				startLatLng = new LatLng(location.getLatitude(), location.getLongitude());
				// 定位成功后设置改位置为起点
				MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(startLatLng);
				baiduMap.animateMapStatus(mapStatusUpdate);
				OverlayOptions overlayOptions = new MarkerOptions().position(startLatLng).icon(bd).title("定位").zIndex(18);
				baiduMap.addOverlay(overlayOptions);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

}
