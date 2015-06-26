package com.kooniao.travel.discovery;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.AnimationRes;
import org.androidannotations.annotations.res.StringRes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseFragment;
import com.kooniao.travel.citylist.CityListActivity_;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.discovery.TravelAdapter.ListItemRequestListener;
import com.kooniao.travel.manager.CityManager;
import com.kooniao.travel.manager.ProductManager;
import com.kooniao.travel.manager.TravelManager;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.manager.ProductManager.StringResultCallback;
import com.kooniao.travel.manager.TravelManager.TravelListCallback;
import com.kooniao.travel.model.City;
import com.kooniao.travel.model.Travel;
import com.kooniao.travel.user.LoginActivity_;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.zbar.lib.CaptureActivity;

/**
 * 发现
 * 
 * @author ke.wei.quan
 * 
 */
@SuppressLint("HandlerLeak")
@EFragment(R.layout.fragment_travel)
public class DiscoveryFragment extends BaseFragment {

	@ViewById(R.id.tv_place_starting)
	TextView startingPlaceTextView; // 出发地
	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout; // 下拉刷新布局
	@ViewById(R.id.lv_travel)
	ListView listView; // 数据列表布局
	@ViewById(R.id.layout_no_data)
	View noDataLayout; // 无数据布局

	/**
	 * 初始化
	 */
	@AfterViews
	void init() {
		initData();
		initView();
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
	}

	@StringRes(R.string.no_more_data)
	String noreMoreDataTips; // 没有更多数据的提示语

	final int REFRESH_DATA = 1; // 刷新数据

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH_DATA: // 刷新数据
				refreshLayout.autoRefresh(true);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	final int SHOW_BAR = 3; // 显示上下bar
	/**
	 * 隐藏上下bar的handler
	 */
	Handler uiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_BAR: // 显示上下bar
				notificationCallback.onHideBottomBarListener(false);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	private int cid = 3544; // 城市id，默认广州
	@StringRes(R.string.default_city_gz)
	String cityName; // 城市名，默认广州
	private ImageLoader imageLoader;

	/**
	 * 初始化页面数据
	 */
	private void initData() {
		// 获取上一次保存的城市id
		int localCid = AppSetting.getInstance().getIntPreferencesByKey(Define.CID_DISCOVERY);
		cid = localCid == 0 ? 3544 : localCid;
		City localCity = CityManager.getInstance().getCityById(cid);
		if (localCity != null) {
			cityName = localCity.getName();
		}

		// 初始化图片加载器
		imageLoader = ImageLoader.getInstance();
	}

	private TravelAdapter adapter;

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 设置出发地
		startingPlaceTextView.setText(cityName);
		adapter = new TravelAdapter(getActivity());
		adapter.setOnListItemRequestListener(listItemRequestListener);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true, new ListViewScrollListener()));
		// 下拉刷新配置
		MaterialHeader materialHeader = new MaterialHeader(getActivity());
		materialHeader.setPadding(0, ViewUtils.dpToPx(15, getResources()), 0, ViewUtils.dpToPx(15, getResources()));

		refreshLayout.setHeaderView(materialHeader);
		refreshLayout.addPtrUIHandler(materialHeader);
		refreshLayout.setPtrHandler(new PtrHandler() {
			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
				return PtrDefaultHandler.checkContentCanBePulledDown(frame, listView, header);
			}

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				onRefresh();
			}
		});
	}

	private int clickItemIndex = -1;
	Travel collectTravel; // 准备收藏的行程
	ImageView collectImageView; // 准备收藏的行程的imageview

	ListItemRequestListener listItemRequestListener = new ListItemRequestListener() {

		@Override
		public void onLoadCoverImgListener(String imgUrl, final ImageView coverImageView) {
			ImageLoaderUtil.loadListCoverImg(imageLoader, imgUrl, coverImageView);
		}

		@Override
		public void onCollectClickListener(int position, ImageView imageView) {
			collectImageView = imageView;
			collectTravel = travels.get(position);
			// 判断用户登录状态
			int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID);
			if (uid == 0) {
				Intent logIntent = new Intent(getActivity(), LoginActivity_.class);
				startActivityForResult(logIntent, RESULT_CODE_LOGIN);
			} else {
				addOrCancelToMyCollect();
			}
		}

		@Override
		public void onItemClickListener(int position) {
			clickItemIndex = position;
			Travel travel = travels.get(position);
			Intent intent = new Intent(getActivity(), TravelDetailActivity_.class);
			intent.putExtra(Define.PID, travel.getId());
			startActivityForResult(intent, REQUEST_CODE_PRODUCT_DETAIL);
		}
	};

	/**
	 * 添加或取消到我的收藏列表
	 * 
	 * @param likeId
	 * @param isKeep
	 * @param likeType
	 * 
	 *            "类型： 全部：0 ，线路：4 ，组合：2 ，酒店：5 ，美食：8， 娱乐：7 "
	 */
	private void addOrCancelToMyCollect() {
		final int isKeep = collectTravel.getCollect() == 0 ? 1 : 0;
		collectTravel.setCollect(isKeep);
		String likeType = Define.TRAVEL;
		ProductManager.getInstance().addOrCancelToMyCollect(collectTravel.getId(), isKeep, likeType, null, 0, new StringResultCallback() {

			@Override
			public void result(String errMsg) {
				addOrCancelToMyCollectComplete(isKeep, errMsg);
			}
		});
	}

	@StringRes(R.string.add_collect_success)
	String addToMyCollectSuccessTips; // 添加到我的收藏列表成功
	@StringRes(R.string.cancel_collect_success)
	String cancelMyCollectSuccessTips; // 取消我的收藏成功
	@AnimationRes(R.anim.collect_click)
	Animation collectAnimation; // 收藏动画

	/**
	 * 添加取消收藏完成
	 * 
	 * @param isKeep
	 * 
	 * @param errMsg
	 */
	@UiThread
	void addOrCancelToMyCollectComplete(int isKeep, String errMsg) {
		if (errMsg == null) {
			collectImageView.startAnimation(collectAnimation);
			UserManager.getInstance().undateCollectCount(isKeep);
			if (isKeep == 1) {
				collectImageView.setImageResource(R.drawable.collect_press);
				Toast.makeText(getActivity(), addToMyCollectSuccessTips, Toast.LENGTH_SHORT).show();
			} else {
				collectImageView.setImageResource(R.drawable.collect_normal);
				Toast.makeText(getActivity(), cancelMyCollectSuccessTips, Toast.LENGTH_SHORT).show();
			}
		} else {
			if (isKeep == 1) {
				collectImageView.setImageResource(R.drawable.collect_normal);
			} else {
				collectImageView.setImageResource(R.drawable.collect_press);
			}
			Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	// 滑动监听
	private class ListViewScrollListener implements OnScrollListener {
		@Override
		public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (!isLoadingMore) {
				int lastItem = firstVisibleItem + visibleItemCount;
				if (lastItem == totalItemCount) {
					int childCount = listView.getChildCount();
					if (childCount > 1) {
						View lastItemView = (View) listView.getChildAt(childCount - 1);
						if ((listView.getBottom()) == lastItemView.getBottom()) {
							currentPageNum++;
							loadTravelList(currentPageNum);
						}
					}
				}
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView listview, int scrollState) {
			if (scrollState == SCROLL_STATE_IDLE) {
				// 停止滚动
				uiHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						uiHandler.sendEmptyMessage(SHOW_BAR);
					}
				}, 800);

			} else if (scrollState == SCROLL_STATE_FLING) {
				// 快速滑动
				notificationCallback.onHideBottomBarListener(true);
				uiHandler.removeCallbacksAndMessages(null);

			} else if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
				// 触摸滑动
				notificationCallback.onHideBottomBarListener(true);
				uiHandler.removeCallbacksAndMessages(null);
			}
		}
	}

	private int currentPageNum = 1; // 当前页码

	/**
	 * 点击城市选择
	 */
	@Click(R.id.tv_place_starting)
	void onCitySelectClick() {
		Intent cityIntent = new Intent(getActivity(), CityListActivity_.class);
		startActivityForResult(cityIntent, REQUEST_CODE_CITY);
	}

	private boolean isLoadingMore; // 是否正在加载更多

	/**
	 * 加载行程列表
	 * 
	 * @param currentPageNum
	 */
	public void loadTravelList(int currentPageNum) {
		if (currentPageNum > 1) {
			isLoadingMore = true;
		}

		TravelManager.getInstance().loadTravelList(cid, currentPageNum, new TravelListCallback() {

			@Override
			public void result(String errMsg, List<Travel> travels, int pageCount) {
				isLoadingMore = false;
				loadTravelListComplete(errMsg, travels, pageCount);
			}
		});
	}

	List<Travel> travels = new ArrayList<Travel>();

	/**
	 * 行程列表请求完成
	 * 
	 * @param errMsg
	 * @param travels
	 * @param pageCount
	 */
	protected void loadTravelListComplete(String errMsg, List<Travel> travels, int pageCount) {
		refreshLayout.refreshComplete();
		if (errMsg == null && travels != null) {
			if (currentPageNum == 1) {
				this.travels.clear();
				this.travels = travels;
			} else {
				this.travels.addAll(travels);
			}

			adapter.setTravelList(this.travels);

			if (this.travels.isEmpty()) {
				noDataLayout.setVisibility(View.VISIBLE);
			} else {
				noDataLayout.setVisibility(View.GONE);
			}

		} else {
			Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	@Click(R.id.tv_place_starting_tip)
	void onStartingPlaceClick() {
		Intent cityIntent = new Intent(getActivity(), CityListActivity_.class);
		startActivityForResult(cityIntent, REQUEST_CODE_CITY);
	}

	/**
	 * 扫描点击
	 */
	@Click(R.id.iv_scan)
	void onScanClick() {
		Intent scanIntent = new Intent(getActivity(), CaptureActivity.class);
		startActivity(scanIntent);
	}

	// 下拉刷新
	public void onRefresh() {
		currentPageNum = 1;
		listView.setSelectionFromTop(0, 0); 
		loadTravelList(currentPageNum);
	}

	final int REQUEST_CODE_CITY = 11; // 城市
	final int REQUEST_CODE_PRODUCT_DETAIL = 12; // 详情页
	final int RESULT_CODE_LOGIN = 13; // 登录

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_CITY: // 城市返回
			if (resultCode == Activity.RESULT_OK && data != null) {
				City city = (City) data.getSerializableExtra(Define.DATA);

				cid = city.getId();
				cityName = city.getName();
				AppSetting.getInstance().saveIntPreferencesByKey(Define.CID_HOME_PAGE, cid);

				startingPlaceTextView.setText(cityName);
				currentPageNum = 1;
				handler.sendEmptyMessageDelayed(REFRESH_DATA, 300);
			}
			break;

		case REQUEST_CODE_PRODUCT_DETAIL: // 详情页
			if (resultCode == Activity.RESULT_OK && data != null) {
				int travelKeep = data.getIntExtra(Define.DATA, -1);
				if (travelKeep != -1) {
					travels.get(clickItemIndex).setCollect(travelKeep);
					adapter.setTravelList(travels);
				}
			}
			break;

		case RESULT_CODE_LOGIN: // 登录
			if (resultCode == Activity.RESULT_OK) { // 登录成功
				addOrCancelToMyCollect();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
