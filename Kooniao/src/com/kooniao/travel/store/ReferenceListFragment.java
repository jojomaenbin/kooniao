package com.kooniao.travel.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseFragment;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.TravelManager;
import com.kooniao.travel.manager.TravelManager.AroundListResultCallback;
import com.kooniao.travel.model.Around;
import com.kooniao.travel.store.ReferenceListAdapter.ItemRequestListener;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * 引用数据列表页面
 * 
 * @author ke.wei.quan
 *
 */
@SuppressLint("HandlerLeak")
@EFragment(R.layout.fragment_refresh)
public class ReferenceListFragment extends BaseFragment {
	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout; // 下拉刷新布局
	@ViewById(R.id.lv_reference_list)
	ListView listView;
	@ViewById(R.id.layout_no_data)
	View noDataLayout; // 无数据布局

	String referenceType; // 引用的类型
	String lineType; // 线路类型(个人、公共)
	int cityId = 3544; // 城市id

	@Override
	public void onAttach(Activity activity) {
		referenceType = getArguments().getString(Define.REFERENCE_TYPE);
		lineType = getArguments().getString(Define.LINE_TYPE);
		cityId = getArguments().getInt(Define.CID);
		super.onAttach(activity);
	}

	@AfterViews
	void init() {
		initData();
		initView();
		refresh();
	}

	/**
	 * 刷新数据
	 */
	public void refresh() {
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
	}

	/**
	 * 刷新数据
	 * 
	 * @param cityId 城市id
	 * @param referenceType
	 *            引用的类型(线路：line, 酒店：hotel, 景点：scenic, 吃喝玩乐：lifestyle)
	 */
	public void refresh(int cityId, String referenceType) {
		this.cityId = cityId;
		this.referenceType = referenceType;
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
	}
	
	/**
	 * 刷新数据
	 * 
	 * @param cityId 城市id
	 */
	public void refresh(int cityId) {
		this.cityId = cityId;
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
	}

	ImageLoader imageLoader;
	ReferenceListAdapter adapter;

	private void initData() {
		imageLoader = ImageLoader.getInstance();
		adapter = new ReferenceListAdapter(getActivity());
		adapter.setOnItemRequestListener(new ItemRequestListener() {

			@Override
			public void onLoadCoverImgListener(String imgUrl, ImageView coverImageView) {
				ImageLoaderUtil.loadListCoverImg(imageLoader, imgUrl, coverImageView);
			}

			@Override
			public void onItemClickListener(int position) {
				seleceedAround = referenceList.get(position);
				finishCurrentView();
			}
		});
	}

	private void initView() {
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
				// 判断滚动到底部
				if (!isLoadingMore) {
					if (listview.getLastVisiblePosition() == (listview.getCount() - 1)) {
						if (currentPageNum < pageCount) {
							currentPageNum++;
							isLoadingMore = true;
							loadReferenceList();
						} else {
							handler.sendEmptyMessageDelayed(NORE_MORE_DATA, 100);
						}
					}
				}
				break;
			}
		}
	}

	@StringRes(R.string.no_more_data)
	String noreMoreDataTips; // 没有更多数据的提示语

	final int REFRESH_DATA = 1; // 刷新数据
	final int NORE_MORE_DATA = 2; // 没有更多数据

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH_DATA: // 刷新数据
				refreshLayout.autoRefresh(true);
				break;

			case NORE_MORE_DATA: // 没有更多数据
				if (isNeedToShowNoMoreTips) {
					isNeedToShowNoMoreTips = false;
					Toast.makeText(getActivity(), noreMoreDataTips, Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private int currentPageNum = 1; // 当前页码
	private boolean isNeedToShowNoMoreTips = true; // 是否需要提示没有更多数据了

	/**
	 * 下拉刷新
	 */
	public void onRefresh() {
		currentPageNum = 1;
		isNeedToShowNoMoreTips = true;
		loadReferenceList();
	}

	private boolean isLoadingMore; // 是否正在加载更多
	private int pageCount = 0; // 总共的页数

	/**
	 * 加载产品列表
	 */
	private void loadReferenceList() {
		if (currentPageNum > 1) {
			isLoadingMore = false;
		}

		TravelManager.getInstance().loadReferenceList(cityId, referenceType, lineType, currentPageNum, new AroundListResultCallback() {

			@Override
			public void result(String errMsg, List<Around> references, int pageCount) {
				loadProductResourceComplete(errMsg, references, pageCount);
			}
		});
	}

	private List<Around> referenceList = new ArrayList<>(); // 列表数据

	/*
	 * 加载产品资源列表完成
	 */
	protected void loadProductResourceComplete(String errMsg, List<Around> references, int pageCount) {
		isLoadingMore = false;
		refreshLayout.refreshComplete();
		this.pageCount = pageCount;
		if (errMsg == null && references != null) {
			noDataLayout.setVisibility(View.GONE);
			if (currentPageNum == 1) {
				// 是否展示无数据布局
				if (references.isEmpty()) {
					noDataLayout.setVisibility(View.VISIBLE);
				} else {
					noDataLayout.setVisibility(View.GONE);
				}

				// 填数据
				referenceList.clear();
				referenceList = references;
			} else {
				referenceList.addAll(references);
			}

			adapter.setReferences(referenceList);
			adapter.notifyDataSetChanged();
		} else {
			Toast.makeText(getActivity(), errMsg, Toast.LENGTH_LONG).show();
		}
	}

	Around seleceedAround; // 选择的引用

	/**
	 * 结束当前界面
	 */
	private void finishCurrentView() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable(Define.DATA, (Serializable) seleceedAround);
		intent.putExtras(bundle);
		getActivity().setResult(Activity.RESULT_OK, intent);
		getActivity().finish();
	}
}
