package com.kooniao.travel.around;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.kooniao.travel.R;
import com.kooniao.travel.around.AroundListAdapter.ItemRequestListener;
import com.kooniao.travel.base.BaseFragment;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.TravelManager;
import com.kooniao.travel.manager.TravelManager.AroundListResultCallback;
import com.kooniao.travel.model.Around;
import com.kooniao.travel.model.City;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * 附近分类子页面
 * 
 * @author ke.wei.quan
 * 
 */
@SuppressLint("HandlerLeak")
@EFragment(R.layout.fragment_pager_item_view)
public class AroundSubListFragment extends BaseFragment {

	public static AroundSubListFragment_ newInstance(City city) {
		AroundSubListFragment.city = city;
		return new AroundSubListFragment_();
	}

	@StringRes(R.string.no_more_data)
	String noMoreDataTips; // 没有更多数据的提示语

	final int AUTO_REFRESH_DATA = 0; // 自动刷新列表
	final int NORE_MORE_DATA = 1; // 没有更多数据了

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AUTO_REFRESH_DATA: // 自动刷新列表
				refreshLayout.autoRefresh(true);
				break;

			case NORE_MORE_DATA: // 没有更多数据了
				// Toast.makeText(getActivity(), noMoreDataTips,
				// Toast.LENGTH_SHORT).show();
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

	@ViewById(R.id.listview)
	ListView listView;
	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout;
	@ViewById(R.id.layout_no_data)
	View noDataLayout; // 无数据布局

	@AfterViews
	void init() {
		initData();
		initView();
	}

	private ImageLoader imageLoader;
	private AroundListAdapter adapter;

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		adapter = new AroundListAdapter(getActivity());
		adapter.setOnItemRequestListener(itemRequestListener);
		// 初始化图片加载器
		imageLoader = ImageLoader.getInstance();
	}

	ItemRequestListener itemRequestListener = new ItemRequestListener() {

		@Override
		public void onLoadCoverImgListener(String imgUrl, ImageView coverImageView) {
			ImageLoaderUtil.loadListCoverImg(imageLoader, imgUrl, coverImageView);
		}

		@Override
		public void onItemClickListener(int position) {
			Around around = arounds.get(position);
			Intent intent = new Intent(getActivity(), AroundDetailActivity_.class);
			intent.putExtra(Define.ID, around.getId());
			intent.putExtra(Define.TYPE, around.getType());
			startActivity(intent);
		}
	};

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 列表
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

	private String type = Define.LOCATION; // 类型

	public void setType(String type) {
		this.type = type;
	}

	private static City city = new City();

	public void setCity(City city) {
		AroundSubListFragment.city = city;
	}

	private List<Around> arounds = new ArrayList<Around>();
	private int currentPageNum = 1; // 当前页码
	private int totalPage; // 景点总页码

	/**
	 * 刷新列表
	 */
	protected void refreshData() {
		handler.sendEmptyMessageDelayed(AUTO_REFRESH_DATA, 100);
	}

	/**
	 * 请求获取附近列表数据
	 * 
	 * @param City
	 */
	private void loadAroundList() {
		int cid = 0;
		double lon = city.getLon();
		double lat = city.getLat();
		if (lon == 0.0 && lat == 0.0) {
			cid = city.getId();
		}
		TravelManager.getInstance().loadAroundList(lon, lat, cid, type, currentPageNum, new AroundListResultCallback() {

			@Override
			public void result(String errMsg, List<Around> arounds, int pageCount) {
				loadAroundListComplete(errMsg, type, arounds, pageCount);
			}
		});
	}

	private List<Around> currentArounds = new ArrayList<Around>();
	
	/**
	 * 获取附近列表数据完成
	 * 
	 * @param errMsg
	 * @param type
	 * @param arounds
	 * @param pageCount
	 */
	void loadAroundListComplete(String errMsg, String type, List<Around> arounds, int pageCount) {
		totalPage = pageCount;
		isLoadingMore = false;
		refreshLayout.refreshComplete();
		if (errMsg == null && arounds != null) {
			if (currentPageNum == 1) {
				if (arounds.isEmpty()) {
					noDataLayout.setVisibility(View.VISIBLE);
				} else {
					this.arounds.clear();
					this.arounds = arounds;
				}
			} else {
				this.arounds.addAll(arounds);
			}

			if (arounds.size() >= 10) { 
				currentArounds = new ArrayList<>(arounds.subList(0, 10));
			} else {
				currentArounds = arounds;
			}
			adapter.setArounds(this.arounds);
			if (currentPageNum == 1) {
				listView.setSelection(0);
			}
		} else {
			Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 获取列表数据
	 * 
	 * @return
	 */
	public List<Around> getAroundList() {
		return currentArounds;
	}

	/**
	 * 下拉刷新
	 */
	public void onRefresh() {
		currentPageNum = 1;
		listView.setSelectionFromTop(0, 0); 
		loadAroundList();
	}

	private boolean isLoadingMore; // 是否正在加载更多

	// 滑动监听
	private class ListViewScrollListener implements OnScrollListener {
		@Override
		public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		}

		@Override
		public void onScrollStateChanged(AbsListView listview, int scrollState) {
			switch (scrollState) {
			// 当不滚动时
			case OnScrollListener.SCROLL_STATE_IDLE:
				// 停止滚动
				uiHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						uiHandler.sendEmptyMessage(SHOW_BAR);
					}
				}, 800);

				// 判断滚动到底部
				if (!isLoadingMore) {
					if (listview.getLastVisiblePosition() == (listview.getCount() - 1)) {
						if (currentPageNum < totalPage) {
							currentPageNum++;
							isLoadingMore = true;
							loadAroundList();
						} else {
							handler.sendEmptyMessageDelayed(NORE_MORE_DATA, 100);
						}
					}
				}
				break;

			case SCROLL_STATE_FLING:
				// 快速滑动
				notificationCallback.onHideBottomBarListener(true);
				uiHandler.removeCallbacksAndMessages(null);
				break;

			case SCROLL_STATE_TOUCH_SCROLL:
				// 触摸滑动
				notificationCallback.onHideBottomBarListener(true);
				uiHandler.removeCallbacksAndMessages(null);
				break;
			}
		}
	}

}
