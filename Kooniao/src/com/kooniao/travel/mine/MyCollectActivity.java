package com.kooniao.travel.mine;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.kooniao.travel.R;
import com.kooniao.travel.around.AroundDetailActivity_;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.customwidget.SwipeListView;
import com.kooniao.travel.customwidget.SwipeListView.SideSlipOptionCallback;
import com.kooniao.travel.discovery.TravelDetailActivity_;
import com.kooniao.travel.home.CombineProductDetailActivity_;
import com.kooniao.travel.home.LineProductDetailActivity_;
import com.kooniao.travel.home.NonLineProductDetailActivity_;
import com.kooniao.travel.manager.CollectManage;
import com.kooniao.travel.manager.CollectManage.CollectListResultCallback;
import com.kooniao.travel.manager.ProductManager;
import com.kooniao.travel.manager.ProductManager.StringResultCallback;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.mine.CollectListAdapter.ListItemRequestListener;
import com.kooniao.travel.model.Collect;
import com.kooniao.travel.store.StoreActivity_;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.KooniaoTypeUtil;
import com.kooniao.travel.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * 收藏
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_collect)
public class MyCollectActivity extends BaseActivity implements SideSlipOptionCallback, ListItemRequestListener {

	@ViewById(R.id.title)
	TextView titleTextView; // 标题
	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout; // 下拉刷新
	@ViewById(R.id.listview)
	SwipeListView swipeListView;
	@ViewById(R.id.layout_no_data)
	LinearLayout noDataLayout; // 无数据布局

	@AfterViews
	void init() {
		initData();
		initView();
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
	}

	private ImageLoader imageLoader;

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		// 初始化图片加载器
		imageLoader = ImageLoader.getInstance();
	}

	private CollectListAdapter adapter;

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 设置标题
		titleTextView.setText(R.string.my_collect);
		// 设置侧滑
		swipeListView.setBackViewOffSet(Define.widthPx * 1 / 4);
		swipeListView.setSideSlipOptionCallback(this);
		// 配置listview
		adapter = new CollectListAdapter(MyCollectActivity.this);
		adapter.setOnListItemRequestListener(this);
		swipeListView.setAdapter(adapter);
		// 设置滑动监听
		swipeListView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true, new ListViewScrollListener()));
		swipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position = position - swipeListView.getHeaderViewsCount();
				itemClick(position);
			}
		});
		// 下拉刷新配置
		MaterialHeader materialHeader = new MaterialHeader(this);
		materialHeader.setPadding(0, ViewUtils.dpToPx(15, getResources()), 0, ViewUtils.dpToPx(15, getResources()));

		refreshLayout.setHeaderView(materialHeader);
		refreshLayout.addPtrUIHandler(materialHeader);
		refreshLayout.setPtrHandler(new PtrHandler() {
			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
				return PtrDefaultHandler.checkContentCanBePulledDown(frame, swipeListView, header);
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
							loadCollectList();
						} else {
							handler.sendEmptyMessageAtTime(NORE_MORE_DATA, 100);
						}
					}
				}
				break;
			}
		}
	}

	private boolean isNeedToShowNoMoreTips = true; // 是否需要提示没有更多数据了
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
				if (isNeedToShowNoMoreTips) {
					isNeedToShowNoMoreTips = false;
					Toast.makeText(MyCollectActivity.this, noreMoreDataTips, Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	List<Collect> collects = new ArrayList<Collect>();
	private boolean isLoadingMore; // 是否正在加载更多

	/**
	 * 获取收藏列表
	 */
	private void loadCollectList() {
		if (currentPageNum > 1) {
			isLoadingMore = true;
		}

		CollectManage.getInstance().loadMyCollects(currentPageNum, new CollectListResultCallback() {

			@Override
			public void result(final String errMsg, final List<Collect> collects, final int pageCount) {
				isLoadingMore = false;
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						loadMyCollectsComplete(errMsg, pageCount, collects);
					}
				}, 1000);
			}
		});
	}

	private int currentPageNum = 1; // 当前页码
	private int pageCount = 0; // 总共的页数

	/**
	 * 获取收藏列表完成
	 * 
	 * @param errMsg
	 * @param pageCount
	 * @param collects
	 */
	@UiThread
	void loadMyCollectsComplete(String errMsg, int pageCount, List<Collect> collects) {
		swipeListView.resetMenu();
		refreshLayout.refreshComplete();
		if (currentPageNum > 1) {
			isLoadingMore = false;
		}
		if (errMsg == null && collects != null) {
			this.pageCount = pageCount;
			if (currentPageNum == 1) {
				if (collects.isEmpty()) {
					noDataLayout.setVisibility(View.VISIBLE);
				} else {
					this.collects.clear();
					this.collects = collects;
				}
			} else {
				this.collects.addAll(collects);
			}

			adapter.setCollectList(this.collects);
		} else {
			Toast.makeText(MyCollectActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		activityFinish();
	}

	@Override
	public void onBackPressed() {
		activityFinish();
		super.onBackPressed();
	}

	/**
	 * 结束当前页面
	 */
	private void activityFinish() {
		Intent intent = new Intent();
		intent.putExtra(Define.DATA, isDataChange);
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * 下拉刷新
	 */
	private void onRefresh() {
		currentPageNum = 1;
		isNeedToShowNoMoreTips = true;
		loadCollectList();
	}

	/**
	 * 加载封面
	 */
	@Override
	public void onLoadCoverImgListener(String imgUrl, ImageView coverImageView) {
		ImageLoaderUtil.loadListCoverImg(imageLoader, imgUrl, coverImageView);
	}

	/**
	 * 点击店铺
	 */
	@SuppressLint("DefaultLocale")
	@Override
	public void onStoreClick(int position) {
		Collect collect = collects.get(position);
		int sid = collect.getShopId();
		String likeSubType = collect.getShopType();
		likeSubType = likeSubType.toLowerCase();
		Intent intent = new Intent(MyCollectActivity.this, StoreActivity_.class);
		intent.putExtra(Define.SID, sid);
		intent.putExtra(Define.STORE_TYPE, likeSubType);
		startActivity(intent);
	}

	/**
	 * list条目点击 类型： 线路：4 ，组合：2 ，酒店：5 ，美食：8 ，娱乐：7
	 */
	private void itemClick(int position) {
		Collect collect = collects.get(position);
		String module = collect.getModule(); // 收藏类型

		int productType = KooniaoTypeUtil.getTypeByModule(module);
		int productId = collect.getProductId();
		int nonProductId = collect.getId();
		int id = productId == 0 ? nonProductId : productId;
		Intent detailIntent = null;
		if (productId != 0) {
			// 产品类型
			if (productType == 2) {
				// 组合产品详情
				detailIntent = new Intent(MyCollectActivity.this, CombineProductDetailActivity_.class);
			} else {
				if (productType == 4) {
					// 线路产品详情
					detailIntent = new Intent(MyCollectActivity.this, LineProductDetailActivity_.class);
				} else {
					// 非线路产品详情
					detailIntent = new Intent(MyCollectActivity.this, NonLineProductDetailActivity_.class);
				}
			}
		} else {
			// 非产品类型
			if (Define.TRAVEL.equals(module)) {
				// 行程
				detailIntent = new Intent(MyCollectActivity.this, TravelDetailActivity_.class);
			} else {
				detailIntent = new Intent(MyCollectActivity.this, AroundDetailActivity_.class);
			}
		}

		if (detailIntent != null) {
			if (productType == -1) {
				// 资讯
				detailIntent.putExtra(Define.ID, id);
				detailIntent.putExtra(Define.TYPE, module);
			} else {
				// 行程&产品
				detailIntent.putExtra(Define.TYPE, productType);
			}
			detailIntent.putExtra(Define.PID, id);
			startActivity(detailIntent);
		}
	}

	@StringRes(R.string.my_collect)
	String dialogTitle; // 对话框标题
	@StringRes(R.string.sure_delete_collect)
	String dialogMessage; // 对话框内容
	Dialog dialog; // 删除收藏确认对话框

	@Override
	public void onSideSlipOptionSelected(int menuType, final int position) {
		if (menuType == 3) {
			dialog = new Dialog(MyCollectActivity.this, dialogTitle, dialogMessage);
			dialog.setCancelable(false);
			dialog.setOnAcceptButtonClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					deleteCollect(position);
				}
			});
			dialog.setOnCancelButtonClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}
	}

	private boolean isDataChange = false; // 是否更改数据

	/**
	 * 删除收藏
	 * 
	 * @param position
	 */
	private void deleteCollect(final int position) {
		Collect collect = collects.get(position);
		int id = collect.getId();
		int productId = collect.getProductId();
		int likeId = id == 0 ? productId : id;
		String likeType = collect.getModule();
		int fromStoreId = collect.getShopId();
		ProductManager.getInstance().addOrCancelToMyCollect(likeId, 0, likeType, null, fromStoreId, new StringResultCallback() {

			@Override
			public void result(String errMsg) {
				if (errMsg == null) {
					isDataChange = true;
					// 更新本地收藏数量
					UserManager.getInstance().undateCollectCount(0);
					collects.remove(position);
					adapter.setCollectList(collects);
				} else {
					Toast.makeText(MyCollectActivity.this, errMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

}
