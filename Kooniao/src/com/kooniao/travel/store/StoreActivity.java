package com.kooniao.travel.store;

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

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.home.CombineProductDetailActivity_;
import com.kooniao.travel.home.LineProductDetailActivity_;
import com.kooniao.travel.home.NonLineProductDetailActivity_;
import com.kooniao.travel.manager.StoreManager;
import com.kooniao.travel.manager.StoreManager.StoreAndProductListCallback;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.model.Product;
import com.kooniao.travel.model.Store;
import com.kooniao.travel.model.UserInfo;
import com.kooniao.travel.onekeyshare.OnekeyShare;
import com.kooniao.travel.store.StoreProductListAdapter.ItemRequestListener;
import com.kooniao.travel.user.LoginActivity_;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * 店铺页面
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_store)
public class StoreActivity extends BaseActivity implements ItemRequestListener {

	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout; // 下拉刷新
	@ViewById(R.id.sticky_lv_product)
	StickyListHeadersListView stickyListHeadersListView; // 数据列表布局
	@ViewById(R.id.layout_no_data)
	View noDataLayout; // 无数据布局
	@ViewById(R.id.layout_no_data_a)
	View aNoDataLayout; // A店无数据布局
	@ViewById(R.id.iv_open_a_store)
	ImageView openCStoreImageView; // 我要开店按钮

	@AfterViews
	void init() {
		initData();
		initView();
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
	}

	private int storeId; // 店铺id
	private String storeType; // 店铺类型
	private UserInfo userInfo; // 当前用户信息
	private boolean isMyStore; // 是否是我的店铺
	private ImageLoader imageLoader;

	/**
	 * 初始化数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			storeId = intent.getIntExtra(Define.SID, 0);
			storeType = intent.getStringExtra(Define.STORE_TYPE);
		}
		// 加载用户信息
		userInfo = UserManager.getInstance().getCurrentUserInfo();
		isMyStore = false;
		if (userInfo != null) {
			int shopA = userInfo.getShopA();
			int shopC = userInfo.getShopC();
			if (storeId == shopA || storeId == shopC) {
				isMyStore = true;
			}
		}

		// 初始化图片加载器
		imageLoader = ImageLoader.getInstance();
	}

	private View storeHeadView; // 店铺头部
	private View stickHeaderView;
	private View pauseStatusLayout; // 暂停营业布局
	private TextView pauseStoreNameTextView; // 暂停营业店铺名称
	private TextView pauseReasonTextView; // 暂停营业原因
	private ImageView storeLogoImageView; // 店铺logo
	private ImageView storeBgImageView; // 店铺背景
	private TextView storeNameTextView; // 店铺名
	private TextView storeUserNameTextView; // 店铺用户名
	private ImageView storeImageView; // 顶部店铺按钮

	/**
	 * 初始化界面
	 */
	@SuppressLint("InflateParams")
	private void initView() {
		// 店铺头部
		storeHeadView = LayoutInflater.from(StoreActivity.this).inflate(R.layout.store_header, null);
		stickHeaderView = storeHeadView.findViewById(R.id.rl_stick_header);
		pauseStatusLayout = storeHeadView.findViewById(R.id.rl_store_status_pause);
		pauseStoreNameTextView = (TextView) storeHeadView.findViewById(R.id.tv_store_name_pause);
		pauseReasonTextView = (TextView) storeHeadView.findViewById(R.id.tv_store_pause_reason);
		// 后退按钮
		storeHeadView.findViewById(R.id.iv_go_back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 分享按钮
		storeHeadView.findViewById(R.id.iv_share).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (pauseStatusLayout.getVisibility() == View.GONE) {
					OnekeyShare onekeyShare = new OnekeyShare();
					String title = store.getShopName();
					onekeyShare.setTitle(title);
					if (isMyStore) {
						onekeyShare.setText("我开了家店，赶快参观" + store.getShareUrl());
					} else {
						onekeyShare.setText("刚在" + title + "，发现不错的东西，你也来看看吧！" + store.getShareUrl());
					}
					onekeyShare.setUrl(store.getShareUrl());
					onekeyShare.setNotification(R.drawable.app_logo, "酷鸟");
					onekeyShare.setImageUrl(store.getLogo());
					onekeyShare.show(StoreActivity.this);
				}
			}
		});
		// 店铺按钮
		storeImageView = (ImageView) storeHeadView.findViewById(R.id.iv_store);
		storeImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (pauseStatusLayout.getVisibility() == View.GONE) {
					finish();
					Intent storeIntent = new Intent(StoreActivity.this, BottomTabBarActivity_.class);
					storeIntent.putExtra(Define.TYPE, Define.STORE);
					startActivity(storeIntent);
				}
			}
		});
		// 店铺logo
		storeLogoImageView = (ImageView) storeHeadView.findViewById(R.id.iv_store_logo);
		// 店铺背景
		storeBgImageView = (ImageView) storeHeadView.findViewById(R.id.iv_store_bg);
		// 店铺名
		storeNameTextView = (TextView) storeHeadView.findViewById(R.id.tv_store_name);
		// 店铺用户名
		storeUserNameTextView = (TextView) storeHeadView.findViewById(R.id.tv_store_user_name);
		storeUserNameTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isMyStore) {
					Intent intent = new Intent(StoreActivity.this, BottomTabBarActivity_.class);
					intent.putExtra(Define.TYPE, Define.MINE);
					startActivity(intent);
				}
			}
		});
		// 头部固定listview
		stickyListHeadersListView.setAreHeadersSticky(true);
		stickyListHeadersListView.setDrawingListUnderStickyHeader(true);
		stickyListHeadersListView.addHeaderView(storeHeadView);
		stickyListHeadersListView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true, new ListViewScrollListener()));

		// 下拉刷新配置
		MaterialHeader materialHeader = new MaterialHeader(StoreActivity.this);
		materialHeader.setPadding(0, ViewUtils.dpToPx(15, getResources()), 0, ViewUtils.dpToPx(15, getResources()));
		refreshLayout.setHeaderView(materialHeader);
		refreshLayout.addPtrUIHandler(materialHeader);
		refreshLayout.setPtrHandler(new PtrHandler() {
			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
				return PtrDefaultHandler.checkContentCanBePulledDown(frame, stickyListHeadersListView, header);
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
								loadStore();
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

	private boolean isLoadingMore; // 是否正在加载更多

	/**
	 * 获取店铺
	 * 
	 * @param currentPageNum
	 */
	private void loadStore() {
		if (currentPageNum > 1) {
			isLoadingMore = true;
		}

		StoreManager.getInstance().loadStore(currentPageNum, isMyStore, storeId, storeType, 0, new StoreAndProductListCallback() {

			@Override
			public void result(String errMsg, Store store, List<Product> products, int pageCount) {
				loadStoreComplete(errMsg, store, products, pageCount);
			}
		});
	}

	private List<Product> products = new ArrayList<Product>(); // 产品列表

	/**
	 * 请求店铺完成
	 * 
	 * @param errMsg
	 * @param store
	 * @param products
	 * @param pageCount2
	 */
	@UiThread
	void loadStoreComplete(String errMsg, Store store, List<Product> products, int pageCount) {
		isLoadingMore = false;
		this.pageCount = pageCount;
		refreshLayout.refreshComplete();
		if (errMsg == null) {
			if (currentPageNum == 1) {
				this.store = store;
				setStoreTopInfo();
			}
			if (products != null) {
				if (currentPageNum == 1) {
					this.products.clear();
					this.products = products;
				} else {
					this.products.addAll(products);
				}

				setProductAdapter();
			}
			if (store.getStatus()==4) {
				UserInfo userInfo = UserManager.getInstance().getCurrentUserInfo();
				String storeUserName = userInfo.getUname();
				if (store.getUserName().equals(storeUserName)) {
					final Dialog dialog;
					dialog = new Dialog(StoreActivity.this,View.VISIBLE, View.GONE, "提示", "您的店铺已冻结，如有疑问请联系   020-31133579");
					dialog.setCancelable(false);
					dialog.setOnAcceptButtonClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							finish();
						}
					});
					dialog.show();
				}
				else {
					final Dialog dialog;
					dialog = new Dialog(StoreActivity.this,View.VISIBLE, View.GONE, "提示", "该店铺已被冻结，如有疑问请联系   "+store.getMobile());
					dialog.setCancelable(false);
					dialog.setOnAcceptButtonClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							finish();
						}
					});
					dialog.show();
				}
				
			}
		} else {
			Toast.makeText(StoreActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	private StoreProductListAdapter storeProductListAdapter;

	private void setProductAdapter() {
		if (storeProductListAdapter == null) {
			storeProductListAdapter = new StoreProductListAdapter(StoreActivity.this);
			storeProductListAdapter.setOnItemRequestListener(this);
			storeProductListAdapter.setProducts(products);
			stickyListHeadersListView.setAdapter(storeProductListAdapter);
		} else {
			storeProductListAdapter.setProducts(products);
		}

		if (products.isEmpty()) {
			if ("c".equals(storeType)) {
				noDataLayout.setVisibility(View.VISIBLE);
			} else {
				aNoDataLayout.setVisibility(View.VISIBLE);
			}
			stickHeaderView.setVisibility(View.VISIBLE);
		} else {
			stickHeaderView.setVisibility(View.GONE);
			noDataLayout.setVisibility(View.GONE);
			aNoDataLayout.setVisibility(View.GONE);
		}
	}

	private Store store;

	/**
	 * 设置店铺头部信息
	 */
	private void setStoreTopInfo() {
		if (store != null) {
			// 设置界面信息
			if (!isMyStore) {
				// 如果是他人店铺则显示“我要开店按钮”
				storeImageView.setVisibility(View.GONE);
				openCStoreImageView.setVisibility(View.VISIBLE);
			}
			// 店铺logo
			String storeLogoUrl = store.getLogo();
			ImageLoaderUtil.loadAvatar(imageLoader, storeLogoUrl, storeLogoImageView);
			// 店铺背景
			String storeBgUrl = store.getBgImg();
			ImageLoaderUtil.loadListCoverImg(imageLoader, storeBgUrl, storeBgImageView, R.drawable.default_bg);
			// 店铺名
			String storeName = store.getShopName();
			storeNameTextView.setText(storeName);
			// 店铺用户名
			String storeUserName = store.getUserName();
			storeUserNameTextView.setText(storeUserName);
			if (userInfo != null) {
				int shopC = userInfo.getShopC();
				// 如果用户已经有了C店就不显示“我要开店”按钮
				if (shopC != 0) {
					storeImageView.setVisibility(View.VISIBLE);
					openCStoreImageView.setVisibility(View.GONE);
				}
			}

			/**
			 * 暂停营业布局信息
			 */
			pauseStoreNameTextView.setText(storeName);
			if ("a".equals(storeType)) {
				// A店
				String stopPrompt = store.getStopPrompt(); // 暂停营业原因
				pauseReasonTextView.setText(stopPrompt);
				if (store.getStatus() != 1 && !isMyStore) {
					openCStoreImageView.setImageResource(R.drawable.i_want_to_open_a_store_enable);
					pauseStatusLayout.setVisibility(View.VISIBLE);
				} else {
					pauseStatusLayout.setVisibility(View.GONE);
				}
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
					Toast.makeText(StoreActivity.this, noreMoreDataTips, Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private int currentPageNum = 1; // 当前页码
	private int pageCount = 0; // 总共的页数

	/**
	 * 下拉刷新
	 */
	public void onRefresh() {
		currentPageNum = 1;
		isNeedToShowNoMoreTips = true;
		loadStore();
	}

	/**
	 * 线路分类点击
	 */
	@Override
	public void onCategoryLineClick(View v) {
		startProductListActivity(v, 4);
	}

	/**
	 * 酒店分类点击
	 */
	@Override
	public void onCategoryHotelClick(View v) {
		startProductListActivity(v, 5);
	}

	/**
	 * 景点门票分类点击
	 */
	@Override
	public void onCategoryScenicClick(View v) {
		startProductListActivity(v, 6);
	}

	/**
	 * 购物优惠分类点击
	 */
	@Override
	public void onCategoryShoppingClick(View v) {
		startProductListActivity(v, 9);
	}

	/**
	 * 娱乐分类点击
	 */
	@Override
	public void onCategoryAmusementClick(View v) {
		startProductListActivity(v, 7);
	}

	/**
	 * 美食分类点击
	 */
	@Override
	public void onCategoryFoodClick(View v) {
		startProductListActivity(v, 8);
	}

	/**
	 * 组合产品分类点击
	 */
	@Override
	public void onCategoryGroupProductClick(View v) {
		startProductListActivity(v, 2);
	}
	
	@Override
	public void onCategoryTrafficClick(View v) {
		// TODO 店铺页面的交通分类
	}

	/**
	 * 启动产品列表页
	 * 
	 * @param v
	 */
	private void startProductListActivity(View v, int productype) {
		Intent productListIntent = new Intent(StoreActivity.this, StoreProductListActivity_.class);
		productListIntent.putExtra(Define.DATA, ((TextView) v).getText().toString());
		productListIntent.putExtra(Define.PRODUCT_TYPE, productype);
		productListIntent.putExtra(Define.SID, storeId);
		productListIntent.putExtra(Define.STORE_TYPE, storeType);
		productListIntent.putExtra(Define.IS_MY_STORE, isMyStore);
		startActivity(productListIntent);
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
			productDetailIntent = new Intent(StoreActivity.this, CombineProductDetailActivity_.class);
		} else {
			if (productType == 4) {
				// 线路产品详情
				productDetailIntent = new Intent(StoreActivity.this, LineProductDetailActivity_.class);
			} else {
				// 非线路产品详情
				productDetailIntent = new Intent(StoreActivity.this, NonLineProductDetailActivity_.class);
			}
		}

		if (productDetailIntent != null) {
			productDetailIntent.putExtra(Define.SID, product.getShopId());
			productDetailIntent.putExtra(Define.PID, product.getProductId());
			productDetailIntent.putExtra(Define.TYPE, productType);
			productDetailIntent.putExtra(Define.STORE_TYPE, storeType);
			if ("c".equals(storeType)) {
				productDetailIntent.putExtra(Define.SID, storeId);
			}
			startActivity(productDetailIntent);
		}
	}

	/**
	 * 加载列表图片
	 */
	@Override
	public void onLoadCoverImgListener(String imgUrl, ImageView coverImageView) {
		ImageLoaderUtil.loadListCoverImg(imageLoader, imgUrl, coverImageView);
	}

	@Override
	public void onStoreClick(int position) {
	}

	/**
	 * 点击我要开店
	 */
	@Click(R.id.iv_open_a_store)
	void onOpenAStoreClick() {
		UserInfo localUserInfo = UserManager.getInstance().getCurrentUserInfo();
		if (localUserInfo == null) {
			// 用户没有登录
			Intent intent = new Intent(StoreActivity.this, LoginActivity_.class);
			startActivityForResult(intent, REQUEST_CODE_OPEN_STORE_LOGIN);
		} else {
			// 用户已经登录
			openStore(localUserInfo);
		}
	}

	/**
	 * 开店
	 */
	void openStore(UserInfo localUserInfo) {
		if (localUserInfo != null) {
			// 用户已经登录
			int shopC = localUserInfo.getShopC();
			Intent intent = null;
			if (shopC == 0) {
				// 用户没开过C店
				intent = new Intent(StoreActivity.this, OpenStoreActivity_.class);
				intent.putExtra(Define.SID, storeId);
				intent.putExtra(Define.STORE_TYPE, storeType);
			} else {
				// 用户已经开过C店
				intent = new Intent(StoreActivity.this, BottomTabBarActivity_.class);
				intent.putExtra(Define.TYPE, Define.STORE);
			}
			startActivity(intent);
		}
	}

	/**
	 * 添加产品
	 */
	@Click(R.id.tv_add_product)
	void onAddProductClick() {
		Intent intent = new Intent(StoreActivity.this, ProductLibraryActivity_.class);
		startActivityForResult(intent, REQUEST_CODE_ADD_PRODUCT);
	}

	final int REQUEST_CODE_OPEN_STORE_LOGIN = 11; // 开店请求登录
	final int REQUEST_CODE_ADD_PRODUCT = 12; // 添加产品

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_OPEN_STORE_LOGIN: // 开店
			if (resultCode == Activity.RESULT_OK) {
				// 延时跳转，避免黑屏一闪而过
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						UserInfo localUserInfo = UserManager.getInstance().getCurrentUserInfo();
						openStore(localUserInfo);
					}
				}, 500);
			}
			break;

		case REQUEST_CODE_ADD_PRODUCT: // 添加产品
			if (resultCode == RESULT_OK && data != null) {
				boolean isAddProduct = data.getBooleanExtra(Define.DATA, false);
				if (isAddProduct) {
					handler.sendEmptyMessageDelayed(REFRESH_DATA, 200);
				}
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
