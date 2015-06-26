package com.kooniao.travel.home;

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
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.home.HomeProductListAdapter.ListItemBaseRequestListener;
import com.kooniao.travel.manager.ProductManager;
import com.kooniao.travel.manager.ProductManager.ProductListResultCallback;
import com.kooniao.travel.model.Product;
import com.kooniao.travel.store.StoreActivity_;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * 产品列表页
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.data_listview)
public class HomeProductListActivity extends BaseActivity implements ListItemBaseRequestListener {

	@ViewById(R.id.title)
	TextView titleBarTextView; // title文字
	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout; // 下拉刷新
	@ViewById(R.id.listview)
	ListView listView; // 数据列表
	@ViewById(R.id.layout_no_data)
	LinearLayout noDataLayout; // 无数据布局

	/**
	 * 初始化
	 */
	@AfterViews
	void init() {
		initData();
		initView();
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
	}

	private String title; // 标题
	private int type; // 类型
	private int cid; // 城市id
	private ImageLoader imageLoader;

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			title = intent.getStringExtra(Define.DATA);
			title = title == null ? getResources().getString(R.string.line) : title;
			type = intent.getIntExtra(Define.TYPE, 4);
			cid = intent.getIntExtra(Define.CID_HOME_PAGE, 3544);
		}

		// 初始化图片加载器
		imageLoader = ImageLoader.getInstance();
	}

	private HomeProductListAdapter adapter;

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 设置标题
		titleBarTextView.setText(title);
		// 配置listview
		adapter = new HomeProductListAdapter(HomeProductListActivity.this);
		adapter.setOnListItemBaseRequestListener(this);
		listView.setAdapter(adapter);
		// 设置滑动监听
		listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true, new ListViewScrollListener()));
		// 下拉刷新配置
		MaterialHeader materialHeader = new MaterialHeader(HomeProductListActivity.this);
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
			if (!isLoadingMore) {
				int lastItem = firstVisibleItem + visibleItemCount;
				if (lastItem == totalItemCount) {
					int childCount = listView.getChildCount();
					if (childCount > 1) {
						View lastItemView = (View) listView.getChildAt(childCount - 1);
						if ((listView.getBottom()) == lastItemView.getBottom()) {
							if (currentPageNum < pageCount) {
								currentPageNum++;
								loadProductList(currentPageNum);
							} else {
								handler.sendEmptyMessageAtTime(NORE_MORE_DATA, 100);
							}
						}
					}
				}
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView listview, int scrollState) {
		}
	}

	/**
	 * list条目点击 类型： 全部：0 ，线路：4 ，组合：2 ，酒店：5 ，美食：8 ，娱乐：7
	 */
	@Override
	public void onListItemClick(int position) {
		Product product = products.get(position);
		int productType = product.getType();
		Intent productDetailIntent = null;
		if (productType == 2) {
			// 组合产品详情
			productDetailIntent = new Intent(HomeProductListActivity.this, CombineProductDetailActivity_.class);
		} else {
			if (productType == 4) {
				// 线路产品详情
				productDetailIntent = new Intent(HomeProductListActivity.this, LineProductDetailActivity_.class);
			} else {
				// 非线路产品详情
				productDetailIntent = new Intent(HomeProductListActivity.this, NonLineProductDetailActivity_.class);
			}
		}

		if (productDetailIntent != null) {
			productDetailIntent.putExtra(Define.PID, product.getProductId());
			productDetailIntent.putExtra(Define.STORE_TYPE, "a");
			productDetailIntent.putExtra(Define.SID, product.getShopId());
			startActivityForResult(productDetailIntent, REQUEST_CODE_PRODUCT_DETAIL);
		}
	}

	/**
	 * 店铺点击  
	 */
	@Override
	public void onStoreClick(int position) {
		Product product = products.get(position);
		Intent storeIntent = new Intent(HomeProductListActivity.this, StoreActivity_.class);
		// 当前店铺id和类型
		int storeId = product.getShopId();
		storeIntent.putExtra(Define.SID, storeId);
		String currentStoreType = "a";
		storeIntent.putExtra(Define.STORE_TYPE, currentStoreType);
		startActivity(storeIntent);
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
					Toast.makeText(HomeProductListActivity.this, noreMoreDataTips, Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		onActivityFinish();
	}

	private boolean isDataChange = false; // 数据是否发生了改变

	/**
	 * 结束当前activity
	 */
	private void onActivityFinish() {
		Intent data = new Intent();
		data.putExtra(Define.DATA, isDataChange);
		setResult(RESULT_OK, data);
		finish();
	}

	List<Product> products = new ArrayList<Product>();
	private boolean isLoadingMore; // 是否正在加载更多

	/**
	 * 获取产品列表
	 */
	private void loadProductList(final int pageNum) {
		if (pageNum > 1) {
			isLoadingMore = true;
		}

		ProductManager.getInstance().loadProductList(cid, type, pageNum, new ProductListResultCallback() {

			@Override
			public void result(final String errMsg, final List<Product> products, final int pageCount) {
				isLoadingMore = false;
				loadProductListComplete(errMsg, pageCount, products);
			}
		});
	}

	private int currentPageNum = 1; // 当前页码
	private int pageCount = 0; // 总共的页数

	/**
	 * 获取产品列表完成
	 */
	@UiThread
	void loadProductListComplete(String errMsg, int pageCount, List<Product> products) {
		this.pageCount = pageCount;
		refreshLayout.refreshComplete();
		if (errMsg == null && products != null) {
			if (currentPageNum == 1) {
				this.products.clear();
				this.products = products;
			} else {
				this.products.addAll(products);
			}

			if (this.products.isEmpty()) {
				noDataLayout.setVisibility(View.VISIBLE);
			} else {
				adapter.setProducts(this.products);
			}
		} else {
			Toast.makeText(HomeProductListActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	// 下拉刷新
	public void onRefresh() {
		currentPageNum = 1;
		isNeedToShowNoMoreTips = true;
		loadProductList(currentPageNum);
	}

	final int REQUEST_CODE_PRODUCT_DETAIL = 11; // 详情页

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_PRODUCT_DETAIL: // 详情页
			if (resultCode == Activity.RESULT_OK && data != null) {
				isDataChange = data.getBooleanExtra(Define.DATA, false);
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onLoadCoverImgListener(String imgUrl, ImageView coverImageView) {
		ImageLoaderUtil.loadListCoverImg(imageLoader, imgUrl, coverImageView);
	}

}
