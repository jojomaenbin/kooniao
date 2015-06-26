package com.kooniao.travel.around;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import java.util.ArrayList;
import java.util.List;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import com.kooniao.travel.R;
import com.kooniao.travel.around.AroundListAdapter.ItemRequestListener;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.TravelManager;
import com.kooniao.travel.manager.TravelManager.AroundListResultCallback;
import com.kooniao.travel.model.Around;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.KooniaoTypeUtil;
import com.kooniao.travel.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * 附近列表资讯页
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.data_listview)
public class AroundListActivity extends BaseActivity {

	@ViewById(R.id.title)
	TextView titleTextView; // 标题
	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout; // 下拉刷新
	@ViewById(R.id.listview)
	ListView listView; // 数据列表布局
	@ViewById(R.id.layout_no_data)
	View noDataLayout; // 无数据布局

	@AfterViews
	void init() {
		initData();
		initView();
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
	}

	private float lon; // 经度
	private float lat; // 纬度
	private String type; // 附近的类型
	private ImageLoader imageLoader;
	private AroundListAdapter adapter;

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			lon = intent.getFloatExtra(Define.CITY_NAME_AROUND_LON, 0);
			lat = intent.getFloatExtra(Define.CITY_NAME_AROUND_LAT, 0);
			type = intent.getStringExtra(Define.TYPE);
		}

		adapter = new AroundListAdapter(AroundListActivity.this);
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
			Intent intent = new Intent(AroundListActivity.this, AroundDetailActivity_.class);
			intent.putExtra(Define.ID, around.getId());
			intent.putExtra(Define.TYPE, around.getType());
			startActivity(intent);
		}
	};

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 设置标题
		if (type != null) {
			String title = KooniaoTypeUtil.getAroundTitleByType(type);
			titleTextView.setText(title);
		}
		// 列表
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true, new ListViewScrollListener()));
		// 下拉刷新配置
		MaterialHeader materialHeader = new MaterialHeader(AroundListActivity.this);
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

	@StringRes(R.string.no_more_data)
	String noreMoreDataTips; // 没有更多数据的提示语

	final int REFRESH_DATA = 1; // 刷新数据
	final int NORE_MORE_DATA = 2; // 没有更多数据

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH_DATA: // 刷新数据
				refreshLayout.autoRefresh(true);
				break;

			case NORE_MORE_DATA: // 没有更多数据
				Toast.makeText(AroundListActivity.this, noreMoreDataTips, Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private List<Around> arounds = new ArrayList<Around>();
	private int currentPageNum = 1; // 当前页码
	private int totalPage; // 景点总页码

	/**
	 * 请求获取附近列表数据
	 * 
	 * @param city
	 */
	private void loadAroundList() {
		TravelManager.getInstance().loadAroundList(lon, lat, 0, type, currentPageNum, new AroundListResultCallback() {

			@Override
			public void result(String errMsg, List<Around> arounds, int pageCount) {
				loadAroundListComplete(errMsg, type, arounds, pageCount);
			}
		});
	}

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

			adapter.setArounds(this.arounds);
			if (currentPageNum == 1) {
				listView.setSelection(0);
			}
		} else {
			Toast.makeText(AroundListActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 下拉刷新
	 */
	public void onRefresh() {
		currentPageNum = 1;
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

}
