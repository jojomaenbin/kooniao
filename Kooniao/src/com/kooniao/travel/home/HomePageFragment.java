package com.kooniao.travel.home;

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

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseFragment;
import com.kooniao.travel.citylist.CityListActivity_;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.AutoScrollViewPager;
import com.kooniao.travel.customwidget.EmptyFooterView;
import com.kooniao.travel.home.HomeProductListAdapter.ItemRequestListener;
import com.kooniao.travel.manager.AdManager;
import com.kooniao.travel.manager.AdManager.AdListResultCallback;
import com.kooniao.travel.manager.CityManager;
import com.kooniao.travel.manager.ProductManager;
import com.kooniao.travel.manager.ProductManager.ProductListResultCallback;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.model.Ad;
import com.kooniao.travel.model.City;
import com.kooniao.travel.model.Product;
import com.kooniao.travel.model.UserInfo;
import com.kooniao.travel.onekeyshare.OnekeyShare;
import com.kooniao.travel.store.OpenStoreActivity_;
import com.kooniao.travel.store.StoreActivity_;
import com.kooniao.travel.user.LoginActivity_;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.zbar.lib.CaptureActivity;

/**
 * 首页
 * 
 * @author ke.wei.quan
 * 
 */
@SuppressLint({ "HandlerLeak", "InflateParams" })
@EFragment(R.layout.fragment_home_page)
public class HomePageFragment extends BaseFragment implements ItemRequestListener {

	@ViewById(R.id.tv_top_bar_city)
	TextView cityTextView; // 城市选择按钮
	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout;
	@ViewById(R.id.sticky_lv_product)
	StickyListHeadersListView stickyListHeadersListView; // 数据列表布局
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

	private int cid = 3544; // 城市id，默认广州
	@StringRes(R.string.default_city_gz)
	String cityName; // 城市名，默认广州
	private ImageLoader imageLoader;

	/**
	 * 初始化页面数据
	 */
	private void initData() {
		// 获取上一次保存的城市id
		int localCid = AppSetting.getInstance().getIntPreferencesByKey(Define.CID_HOME_PAGE);
		cid = localCid == 0 ? 3544 : localCid;
		AppSetting.getInstance().saveIntPreferencesByKey(Define.CID_HOME_PAGE, cid);
		City localCity = CityManager.getInstance().getCityById(cid);
		if (localCity != null) {
			cityName = localCity.getName();
		}

		// 初始化图片加载器
		imageLoader = ImageLoader.getInstance();
	}

	/**
	 * 初始化界面数据
	 */
	private void loadData() {
		loadAdList();
		loadProductList();
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
				if (stickyListHeadersListView.getFooterViewsCount() == 0) {
					stickyListHeadersListView.addFooterView(emptyFooterView);
				}
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

	/**
	 * 获取广告列表
	 */
	private void loadAdList() {
		AdManager.getInstance().loadAdList(cid, new AdListResultCallback() {

			@Override
			public void result(final String errMsg, final List<Ad> ads) {
				loadAdListComplete(errMsg, ads);
			}
		});
	}

	/**
	 * 获取广告列表数据请求完成
	 * 
	 * @param errMsg
	 * @param ads
	 */
	@UiThread
	void loadAdListComplete(String errMsg, List<Ad> ads) {
		if (errMsg == null) {
			this.ads = ads;
			setAdAdapter();
			viewPagerBgLayout.setVisibility(View.GONE);
			viewpagerIndicatorContainer.setVisibility(View.VISIBLE);
		} else {
			viewPagerBgLayout.setVisibility(View.VISIBLE);
			viewpagerIndicatorContainer.setVisibility(View.GONE);
			Toast.makeText(getNotNullActivity(), errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	AdListPagerAdapter adListPagerAdapter;
	List<Ad> ads = new ArrayList<Ad>();
	@AnimationRes(R.anim.slide_in_from_right)
	Animation rightInAnimation; // 右边进来动画

	/**
	 * 初始化广告列表适配器
	 */
	private void setAdAdapter() {
		if (adListPagerAdapter == null) {
			adListPagerAdapter = new AdListPagerAdapter(getNotNullActivity());
			adListPagerAdapter.setAdList(ads);
			autoScrollViewPager.setAdapter(adListPagerAdapter);
			autoScrollViewPager.setInterval(Define.AUTO_SCROLL_INTERVAL_TIME);
			autoScrollViewPager.setScrollDurationFactor(5.0);
			autoScrollViewPager.setAnimation(rightInAnimation);
			autoScrollViewPager.setOffscreenPageLimit(10);
			/**
			 * 滑动监听
			 */
			autoScrollViewPager.setOnPageChangeListener(new ViewPagerChangeListener());

			/**
			 * 设置页面指示点
			 */
			for (int i = 0; i < ads.size(); i++) {
				LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				// 设置每个小圆点距离左边的间距
				margin.setMargins(10, 0, 0, 0);
				ImageView imageView = new ImageView(getNotNullActivity());
				// 设置每个小圆点的宽高
				imageView.setLayoutParams(new LayoutParams(15, 15));
				imageView.setBackgroundResource(R.drawable.point_white);
				if (i == 0) {
					imageView.setBackgroundResource(R.drawable.point_blue);
				}
				viewpagerIndicatorContainer.addView(imageView, margin);
			}

		} else {
			adListPagerAdapter.setAdList(ads);
			adListPagerAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 轮播滑动监听
	 * 
	 * @author ke.wei.quan
	 * 
	 */
	private class ViewPagerChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int position) {
			position = position % ads.size();
			for (int i = 0; i < ads.size(); i++) {
				ImageView pointImageView = (ImageView) viewpagerIndicatorContainer.getChildAt(i);
				if (i == position) {
					pointImageView.setImageResource(R.drawable.point_blue);
				} else {
					pointImageView.setImageResource(R.drawable.point_white);
				}
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	}

	private boolean isLoadingMore; // 是否正在加载更多
	List<Product> products = new ArrayList<Product>();

	/**
	 * 获取产品列表
	 */
	private void loadProductList() {
		isLoadingMore = true;
		ProductManager.getInstance().loadProductList(cid, 0, currentPageNum, new ProductListResultCallback() {

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
		refreshLayout.refreshComplete();
		stickyListHeadersListView.setSelection(0);
		if (stickyListHeadersListView.getFooterViewsCount() > 0) {
			stickyListHeadersListView.removeFooterView(emptyFooterView);
		}

		if (errMsg == null && products != null) {
			this.pageCount = pageCount;
			if (currentPageNum == 1) {
				this.products.clear();
				this.products = products;
			} else {
				this.products.addAll(products);
			}
			setProductAdapter();
		} else {
			Toast.makeText(getNotNullActivity(), errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	private HomeProductListAdapter productListAdapter;

	private void setProductAdapter() {
		if (productListAdapter == null) {
			productListAdapter = new HomeProductListAdapter(getNotNullActivity());
			productListAdapter.setOnItemRequestListener(this);
			productListAdapter.setProducts(products);
			stickyListHeadersListView.setAdapter(productListAdapter);
		} else {
			productListAdapter.setProducts(products);
		}

		if (products.isEmpty()) {
			noDataLayout.setVisibility(View.VISIBLE);
		} else {
			noDataLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onResume() {
		if (autoScrollViewPager != null) {
			autoScrollViewPager.startAutoScroll();
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		if (autoScrollViewPager != null) {
			autoScrollViewPager.stopAutoScroll();
		}
		super.onPause();
	}

	private View moreLayout; // “更多”布局
	private PopupWindow morePopupWindow; // “更多” popupWindow

	private View listViewHeadView; // 列表头部(轮播广告布局)
	View viewPagerBgLayout; // viewpager默认背景
	AutoScrollViewPager autoScrollViewPager; // 自动轮播布局
	LinearLayout viewpagerIndicatorContainer; // viewpager页面指示器
	EmptyFooterView emptyFooterView;

	/**
	 * 初始化view
	 */
	private void initView() {
		// 默认城市广州
		cityTextView.setText(cityName);

		// 初始化PopupWindow
		moreLayout = LayoutInflater.from(getNotNullActivity()).inflate(R.layout.home_page_more, null);
		setMoreLayoutItemClick(); // 设置“更多”布局里面的item点击事件
		morePopupWindow = new PopupWindow(moreLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		morePopupWindow.setFocusable(false);

		// 列表头部(轮播广告布局)
		listViewHeadView = LayoutInflater.from(getNotNullActivity()).inflate(R.layout.home_list_header, null);
		viewPagerBgLayout = listViewHeadView.findViewById(R.id.iv_viewpager_bg);
		autoScrollViewPager = (AutoScrollViewPager) listViewHeadView.findViewById(R.id.avp_home_page_ad);
		viewpagerIndicatorContainer = (LinearLayout) listViewHeadView.findViewById(R.id.ll_viewpager_indicator);

		// 头部固定listview
		stickyListHeadersListView.setAreHeadersSticky(true);
		stickyListHeadersListView.setDrawingListUnderStickyHeader(true);
		stickyListHeadersListView.addHeaderView(listViewHeadView);
		stickyListHeadersListView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true, new ListViewScrollListener()));

		// 底部空白view
		emptyFooterView = new EmptyFooterView(getNotNullActivity());

		// 下拉刷新配置
		MaterialHeader materialHeader = new MaterialHeader(getNotNullActivity());
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
								loadData();
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
			switch (scrollState) {
			case SCROLL_STATE_IDLE:
				// 停止滚动
				uiHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						uiHandler.sendEmptyMessage(SHOW_BAR);
					}
				}, 500);

				// 滚动到底部
				if (!isLoadingMore) {
					if (listview.getLastVisiblePosition() == (listview.getCount() - 1)) {
						if (currentPageNum < pageCount) {
							currentPageNum++;
							loadProductList();
						} else {
							handler.sendEmptyMessageDelayed(NORE_MORE_DATA, 100);
						}
					}
				}
				break;

			case SCROLL_STATE_TOUCH_SCROLL:// 触摸滑动
				notificationCallback.onHideBottomBarListener(true);
				uiHandler.removeCallbacksAndMessages(null);
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 搜索
	 */
	@Click(R.id.iv_top_bar_search)
	void onSearchClick() {
		Intent searchIntent = new Intent(getNotNullActivity(), SearchActivity_.class);
		startActivity(searchIntent);
	}

	/**
	 * 城市点击
	 */
	@Click(R.id.tv_top_bar_city)
	void onCityClick() {
		Intent cityIntent = new Intent(getNotNullActivity(), CityListActivity_.class);
		startActivityForResult(cityIntent, REQUEST_CODE_CITY);
	}

	@ViewById(R.id.titlebar_main)
	View titlebarLayout; // titlebar布局

	/**
	 * 更多
	 */
	@Click(R.id.iv_top_bar_more)
	void onMoreClick() {
		if (morePopupWindow.isShowing()) {
			morePopupWindow.dismiss();
		} else {
			morePopupWindow.showAsDropDown(titlebarLayout);
		}
	}

	/**
	 * “更多”布局里面的item点击事件
	 */
	private void setMoreLayoutItemClick() {
		moreLayout.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				morePopupWindow.dismiss();
				return true;
			}
		});
		// 扫描按钮
		moreLayout.findViewById(R.id.iv_scan).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onScanClick();
			}
		});
		// 开店按钮
		moreLayout.findViewById(R.id.iv_set_up_store).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onOpenAStoreClick();
			}
		});
		// 分享按钮
		moreLayout.findViewById(R.id.iv_share).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onShareClick();
			}
		});
	}

	/**
	 * “更多”布局--扫描按钮点击
	 */
	void onScanClick() {
		morePopupWindow.dismiss();
		Intent scanIntent = new Intent(getNotNullActivity(), CaptureActivity.class);
		startActivity(scanIntent);
	}

	/**
	 * “更多”布局--我要开店按钮点击
	 */
	void onOpenAStoreClick() {
		morePopupWindow.dismiss();
		UserInfo localUserInfo = UserManager.getInstance().getCurrentUserInfo();
		if (localUserInfo == null) {
			// 用户没有登录
			Intent intent = new Intent(getNotNullActivity(), LoginActivity_.class);
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
				intent = new Intent(getNotNullActivity(), OpenStoreActivity_.class);
			} else {
				// 用户已经开过C店
				intent = new Intent(getNotNullActivity(), BottomTabBarActivity_.class);
				intent.putExtra(Define.TYPE, Define.STORE);
			}
			startActivity(intent);
		}
	}

	OnekeyShare onekeyShare; // 一键分享
	final String appDownloadUrl = "http://www.kooniao.com/app/"; // 分享链接

	/**
	 * “更多”布局--分享按钮点击
	 */
	void onShareClick() {
		morePopupWindow.dismiss();
		// 分享
		if (onekeyShare == null) {
			onekeyShare = new OnekeyShare();
			onekeyShare.setTitle("酷鸟行程");
			onekeyShare.setText("酷鸟行程app下载地址:" + appDownloadUrl);
			onekeyShare.setUrl(appDownloadUrl);
			onekeyShare.setNotification(R.drawable.app_logo, "酷鸟行程");
		}
		onekeyShare.show(getNotNullActivity());
	}

	// 下拉刷新
	private void onRefresh() {
		currentPageNum = 1;
		stickyListHeadersListView.setSelectionFromTop(0, 0);
		loadData();
	}

	final int REQUEST_CODE_CITY = 11; // 城市
	final int REQUEST_CODE_OPEN_STORE_LOGIN = 12; // 开店请求登录

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_CITY: // 城市返回
			if (resultCode == Activity.RESULT_OK && data != null) {
				City city = (City) data.getSerializableExtra(Define.DATA);

				cid = city.getId();
				cityName = city.getName();
				AppSetting.getInstance().saveIntPreferencesByKey(Define.CID_HOME_PAGE, cid);

				cityTextView.setText(cityName);
				handler.sendEmptyMessageDelayed(REFRESH_DATA, 200);
			}
			break;

		case REQUEST_CODE_OPEN_STORE_LOGIN: // 开店
			if (resultCode == Activity.RESULT_OK) {
				UserInfo localUserInfo = UserManager.getInstance().getCurrentUserInfo();
				openStore(localUserInfo);
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

	/**
	 * 线路分类点击
	 * 
	 * @param v
	 */
	@Click(R.id.tv_category_line)
	void onLineClick(View v) {
		onCategoryClick(v, 4);
	}

	/**
	 * 线路分类点击
	 */
	@Override
	public void onCategoryLineClick(View v) {
		onCategoryClick(v, 4);
	}

	/**
	 * 酒店分类点击
	 */
	@Click(R.id.tv_category_hotel)
	public void onHotelClick(View v) {
		onCategoryClick(v, 5);
	}

	/**
	 * 酒店分类点击
	 */
	@Override
	public void onCategoryHotelClick(View v) {
		onCategoryClick(v, 5);
	}

	/**
	 * 景点门票分类点击
	 */
	@Click(R.id.tv_category_scenic)
	public void onScenicClick(View v) {
		onCategoryClick(v, 6);
	}

	/**
	 * 景点门票分类点击
	 */
	@Override
	public void onCategoryScenicClick(View v) {
		onCategoryClick(v, 6);
	}

	/**
	 * 购物优惠分类点击
	 */
	@Click(R.id.tv_category_shopping)
	public void onShoppingClick(View v) {
		onCategoryClick(v, 9);
	}

	/**
	 * 购物优惠分类点击
	 */
	@Override
	public void onCategoryShoppingClick(View v) {
		onCategoryClick(v, 9);
	}

	/**
	 * 娱乐分类点击
	 */
	@Click(R.id.tv_category_amusement)
	public void onAmusementClick(View v) {
		onCategoryClick(v, 7);
	}

	/**
	 * 娱乐分类点击
	 */
	@Override
	public void onCategoryAmusementClick(View v) {
		onCategoryClick(v, 7);
	}

	/**
	 * 餐饮美食分类点击
	 */
	@Click(R.id.tv_category_food)
	public void onFoodClick(View v) {
		onCategoryClick(v, 8);
	}

	/**
	 * 餐饮美食分类点击
	 */
	@Override
	public void onCategoryFoodClick(View v) {
		onCategoryClick(v, 8);
	}

	/**
	 * 交通分类点击
	 */
	@Override
	public void onCategoryTrafficClick(View v) {
	}

	/**
	 * 组合产品分类点击
	 */
	@Click(R.id.tv_category_group_product)
	public void onGroupProductClick(View v) {
		onCategoryClick(v, 2);
	}

	/**
	 * 组合产品分类点击
	 */
	@Override
	public void onCategoryGroupProductClick(View v) {
		onCategoryClick(v, 2);
	}

	/**
	 * 点击顶部分类
	 * 
	 * @param v
	 * @param type
	 */
	private void onCategoryClick(View v, int type) {
		Intent productListIntent = new Intent(getNotNullActivity(), HomeProductListActivity_.class);
		productListIntent.putExtra(Define.DATA, ((TextView) v).getText());
		productListIntent.putExtra(Define.TYPE, type);
		productListIntent.putExtra(Define.CID_HOME_PAGE, cid);
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
			productDetailIntent = new Intent(getNotNullActivity(), CombineProductDetailActivity_.class);
		} else {
			if (productType == 4) {
				// 线路产品详情
				productDetailIntent = new Intent(getNotNullActivity(), LineProductDetailActivity_.class);
			} else {
				// 非线路产品详情
				productDetailIntent = new Intent(getNotNullActivity(), NonLineProductDetailActivity_.class);
			}
		}

		if (productDetailIntent != null) {
			productDetailIntent.putExtra(Define.PID, product.getProductId());
			productDetailIntent.putExtra(Define.TYPE, productType);
			productDetailIntent.putExtra(Define.SID, product.getShopId());
			productDetailIntent.putExtra(Define.STORE_TYPE, "a");
			startActivity(productDetailIntent);
		}
	}

	/**
	 * 点击店铺
	 */
	@Override
	public void onStoreClick(int position) {
		Product product = products.get(position);
		Intent storeIntent = new Intent(getNotNullActivity(), StoreActivity_.class);
		// 当前店铺id和类型
		int storeId = product.getShopId();
		storeIntent.putExtra(Define.SID, storeId);
		String currentStoreType = "a";
		storeIntent.putExtra(Define.STORE_TYPE, currentStoreType);
		startActivity(storeIntent);
	}

	public <T> Context getNotNullActivity() {
		FragmentActivity activity = getActivity();
		if (activity != null) {
			return activity;
		} else {
			return KooniaoApplication.getInstance();
		}
	}

}
