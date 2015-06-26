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
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.BlueTextShowPopup;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.manager.StoreManager;
import com.kooniao.travel.manager.StoreManager.ProductDistributionSalesStatisticsResultCallback;
import com.kooniao.travel.manager.StoreManager.ProductSalesStatisticsResultCallback;
import com.kooniao.travel.manager.StoreManager.SelfLockingSalesStatisticsResultCallback;
import com.kooniao.travel.manager.StoreManager.StoreDistributionSalesStatisticsResultCallback;
import com.kooniao.travel.manager.StoreManager.StoreSalesStatisticsResultCallback;
import com.kooniao.travel.manager.StoreManager.StoreTotalSalesStatisticsResultCallback;
import com.kooniao.travel.model.ProductDistributionSalesStatistics;
import com.kooniao.travel.model.ProductSalesStatistics;
import com.kooniao.travel.model.SelfLockingSalesStatistics;
import com.kooniao.travel.model.StoreDistributionSalesStatistics;
import com.kooniao.travel.model.StoreSalesStatistics;
import com.kooniao.travel.model.StoreTotalSalesStatistics;
import com.kooniao.travel.store.SaleStatisticsAdapter.ListItemRequestListener;
import com.kooniao.travel.store.SaleStatisticsAdapter.StatisticsType;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.KooniaoTypeUtil;
import com.kooniao.travel.utils.StringUtil;
import com.kooniao.travel.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * 销售统计
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_statistics)
public class SaleStatisticsActivity extends BaseActivity {

	@ViewById(R.id.title_bar_tab)
	View titleBarTabView; // 标题栏
	@ViewById(R.id.title)
	TextView titleTextView;
	@ViewById(R.id.count_title)
	TextView orderCountTitleTextView; // 订单总计标题
	@ViewById(R.id.count)
	TextView orderCountTextView; // 订单总计
	@ViewById(R.id.money_title)
	TextView commissionCountTitleTextView; // 佣金总计标题
	@ViewById(R.id.money)
	TextView commissionCountTextView; // 佣金总计
	@ViewById(R.id.ll_product_manage_type_select)
	LinearLayout typeSelectLayout; // 类型选择
	@ViewById(R.id.tv_statistics_type)
	TextView statisticsTypeTextView; // 统计类型
	@ViewById(R.id.ll_date_select)
	LinearLayout dateSelectLayout; // 日期选择
	@ViewById(R.id.tv_statistics_date)
	TextView statisticsDateTextView; // 统计日期
	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout; // 刷新布局
	@ViewById(R.id.lv_item)
	ListView listView;
	@ViewById(R.id.layout_no_data)
	View noDataLayout; // 无数据布局

	/**
	 * 初始化
	 */
	@AfterViews
	void init() {
		initData();
		initView();
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 500);
	}

	private int currentPageNum = 1; // 当前页面
	private String storeType = ""; // 店铺类型
	private ImageLoader imageLoader;

	/**
	 * 初始化数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			if (intent.hasExtra(Define.STORE_TYPE)) {
				storeType = intent.getStringExtra(Define.STORE_TYPE);
			}
		}

		// 初始化图片加载器
		imageLoader = ImageLoader.getInstance();
	}

	private SaleStatisticsAdapter adapter;

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 订单数和成交金额默认为0
		resetSaleStatisticsCount();
		adapter = new SaleStatisticsAdapter(SaleStatisticsActivity.this);
		adapter.setOnListItemBaseRequestListener(itemRequestListener);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true, new ListViewScrollListener()));
		// 下拉刷新配置
		MaterialHeader materialHeader = new MaterialHeader(SaleStatisticsActivity.this);
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
		// 初始化默认配置
		if ("c".equals(storeType)) { // C店默认
			adapter.setStatisticsType(StatisticsType.STATISTICS_STORESALESTATISTICS);
			titleTextView.setText(R.string.store_in_sale_statistics);
			statisticsTypeTextView.setTextColor(getResources().getColor(R.color.v909090));
			typeSelectLayout.setClickable(false);
			initCStoreStatisticsTypePopupWindow();
		} else if ("a".equals(storeType)) { // A店默认
			adapter.setStatisticsType(StatisticsType.STATISTICS_SELFSALESTATISTICS);
			titleTextView.setText(R.string.self_statistics);
			initAStoreStatisticsTypePopupWindow();
		}

		initTypePopupWindow();
		initDatePopupWindow();
	}

	@StringRes(R.string.call)
	String dialogTitle; // 对话框标题
	Dialog dialog; // 拨打电话确认对话框

	ListItemRequestListener itemRequestListener = new ListItemRequestListener() {

		@Override
		public void onStoreClick(int storeId) {
			Intent intent = new Intent(getBaseContext(), StoreActivity_.class);
			intent.putExtra(Define.SID, storeId);
			if ("a".equals(storeType)) {
				intent.putExtra(Define.STORE_TYPE, "c");
			} else {
				intent.putExtra(Define.STORE_TYPE, "a");
			}
			startActivity(intent);
		}

		@Override
		public void onPhoneClick(final String phoneNum) {
			if (!"".equals(phoneNum)) {
				dialog = new Dialog(SaleStatisticsActivity.this, dialogTitle, phoneNum);
				dialog.setCancelable(false);
				dialog.setOnAcceptButtonClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNum));
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				});
				dialog.setOnCancelButtonClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.show();
			} else {
				Toast.makeText(SaleStatisticsActivity.this, R.string.customer_service_null, Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onLoadImgListener(String imgUrl, ImageView coverImageView, boolean isAvatar) {
			if (isAvatar) {
				ImageLoaderUtil.loadAvatar(imageLoader, imgUrl, coverImageView);
			} else {
				ImageLoaderUtil.loadListCoverImg(imageLoader, imgUrl, coverImageView);
			}
		}

	};

	private BlueTextShowPopup cStoreStatisticsTypePopupWindow; // C店店铺类型弹出窗
	private View cStoreStatisticsTypeLayout; // C店店铺类型弹出窗布局

	/**
	 * 初始化C店统计类型弹出窗
	 */
	private void initCStoreStatisticsTypePopupWindow() {
		cStoreStatisticsTypeLayout = LayoutInflater.from(getBaseContext()).inflate(R.layout.popup_statistics_type_c, null);
		cStoreStatisticsTypePopupWindow = new BlueTextShowPopup(cStoreStatisticsTypeLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		cStoreStatisticsTypePopupWindow.setFocusable(false);
		// 店铺销售统计
		cStoreStatisticsTypeLayout.findViewById(R.id.tv_store_sale_statistics).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				adapter.setStatisticsType(StatisticsType.STATISTICS_STORESALESTATISTICS);
				initSelectView(cStoreStatisticsTypePopupWindow, R.string.store_in_sale_statistics, R.color.v909090, false);
			}
		});
		// 产品销售统计
		cStoreStatisticsTypeLayout.findViewById(R.id.tv_product_sale_statistics).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				adapter.setStatisticsType(StatisticsType.STATISTICS_PRODUCTSALESTATISTICS);
				initSelectView(cStoreStatisticsTypePopupWindow, R.string.product_in_sale_statistics, R.color.v020202, true);
			}
		});
	}

	private BlueTextShowPopup aStoreStatisticsTypePopupWindow; // A店店铺类型弹出窗
	private View aStoreStatisticsTypeLayout; // A店店铺类型弹出窗布局

	/**
	 * 初始化A店统计类型弹出窗
	 */
	private void initAStoreStatisticsTypePopupWindow() {
		aStoreStatisticsTypeLayout = LayoutInflater.from(getBaseContext()).inflate(R.layout.popup_statistics_type_a, null);
		aStoreStatisticsTypePopupWindow = new BlueTextShowPopup(aStoreStatisticsTypeLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		aStoreStatisticsTypePopupWindow.setFocusable(false);
		// 自销统计
		aStoreStatisticsTypeLayout.findViewById(R.id.tv_self_statistics).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				adapter.setStatisticsType(StatisticsType.STATISTICS_SELFSALESTATISTICS);
				initSelectView(aStoreStatisticsTypePopupWindow, R.string.self_statistics, R.color.v020202, true);
			}
		});
		// 店铺总销
		aStoreStatisticsTypeLayout.findViewById(R.id.tv_store_statistics_total).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				adapter.setStatisticsType(StatisticsType.STATISTICS_STORETOTALSALESTATISTICS);
				initSelectView(aStoreStatisticsTypePopupWindow, R.string.store_statistics_total, R.color.v020202, true);
			}
		});
		// 分销店铺
		aStoreStatisticsTypeLayout.findViewById(R.id.tv_store_distribution).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				adapter.setStatisticsType(StatisticsType.STATISTICS_STOREDISTRIBUTIONSALESTATISTICS);
				initSelectView(aStoreStatisticsTypePopupWindow, R.string.store_distribution, R.color.v909090, false);
			}
		});
		// 分销产品
		aStoreStatisticsTypeLayout.findViewById(R.id.tv_product_distribution).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				adapter.setStatisticsType(StatisticsType.STATISTICS_PRODUCTDISTRIBUTIONSALESTATISTICSTYPE);
				initSelectView(aStoreStatisticsTypePopupWindow, R.string.product_distribution, R.color.v020202, true);
			}
		});
	}

	/**
	 * 初始化页面可选择弹出view的状态
	 * 
	 * @param popupWindow
	 * @param titleResId
	 * @param typeTextColor
	 * @param typeClickable
	 */
	private void initSelectView(BlueTextShowPopup popupWindow, int titleResId, int typeTextColor, boolean typeClickable) {
		popupWindow.dismiss();
		titleTextView.setText(titleResId);
		statisticsTypeTextView.setText(R.string.type_all);
		statisticsDateTextView.setText(R.string.today);
		statisticsTypeTextView.setTextColor(getResources().getColor(typeTextColor));
		typeSelectLayout.setClickable(typeClickable);
		refreshLayout.autoRefresh(true);
	}

	private View typePopupLayout;
	private BlueTextShowPopup typePopupWindow;

	/**
	 * 初始化类型弹出窗
	 */
	private void initTypePopupWindow() {
		typePopupLayout = LayoutInflater.from(getBaseContext()).inflate(R.layout.popup_product_type_select, null);
		typePopupWindow = new BlueTextShowPopup(typePopupLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		typePopupWindow.setFocusable(false);
		// 全部分类
		typePopupLayout.findViewById(R.id.tv_type_all).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initFactorPopupLayout(typePopupWindow, statisticsTypeTextView, R.string.type_all);
			}

		});
		// 线路
		typePopupLayout.findViewById(R.id.tv_type_line).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initFactorPopupLayout(typePopupWindow, statisticsTypeTextView, R.string.line);
			}
		});
		// 酒店
		typePopupLayout.findViewById(R.id.tv_type_hotel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initFactorPopupLayout(typePopupWindow, statisticsTypeTextView, R.string.hotel);
			}
		});
		// 景点门票
		typePopupLayout.findViewById(R.id.tv_type_location_ticket).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initFactorPopupLayout(typePopupWindow, statisticsTypeTextView, R.string.location_ticket_type);
			}
		});
		// 休闲娱乐
		typePopupLayout.findViewById(R.id.tv_type_category_amusement).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initFactorPopupLayout(typePopupWindow, statisticsTypeTextView, R.string.category_amusement);
			}
		});
		// 餐饮美食
		typePopupLayout.findViewById(R.id.tv_type_category_food).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initFactorPopupLayout(typePopupWindow, statisticsTypeTextView, R.string.category_food);
			}
		});
		// 交通
		typePopupLayout.findViewById(R.id.tv_type_category_shopping).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initFactorPopupLayout(typePopupWindow, statisticsTypeTextView, R.string.category_traffic);
			}
		});
		// 组合产品
		typePopupLayout.findViewById(R.id.tv_type_category_group_product).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initFactorPopupLayout(typePopupWindow, statisticsTypeTextView, R.string.category_group_product);
			}
		});
	}

	private View datePopupLayout;
	private BlueTextShowPopup datePopupWindow;

	/**
	 * 初始化日期弹出窗
	 */
	private void initDatePopupWindow() {
		datePopupLayout = LayoutInflater.from(getBaseContext()).inflate(R.layout.popup_statistics_date_select, null);
		datePopupWindow = new BlueTextShowPopup(datePopupLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		datePopupWindow.setFocusable(false);
		// 今天
		datePopupLayout.findViewById(R.id.tv_date_today).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initFactorPopupLayout(datePopupWindow, statisticsDateTextView, R.string.today);
			}
		});
		// 7天
		datePopupLayout.findViewById(R.id.tv_date_seven).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initFactorPopupLayout(datePopupWindow, statisticsDateTextView, R.string.seven_day);
			}
		});
		// 30天
		datePopupLayout.findViewById(R.id.tv_date_thirty).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initFactorPopupLayout(datePopupWindow, statisticsDateTextView, R.string.thirty_day);
			}
		});
		// 全部
		datePopupLayout.findViewById(R.id.tv_date_all).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initFactorPopupLayout(datePopupWindow, statisticsDateTextView, R.string.all);
			}
		});
	}

	/**
	 * 设置条件刷选弹出窗
	 * 
	 * @param popupWindow
	 * @param factorTextView
	 * @param textResId
	 */
	private void initFactorPopupLayout(BlueTextShowPopup popupWindow, TextView factorTextView, int textResId) {
		popupWindow.dismiss();
		factorTextView.setText(textResId);
		refreshLayout.autoRefresh(true);
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
						if (currentPageNum < pageCount) {
							currentPageNum++;
							isLoadingMore = true;
							loadSatisticsList();
						} else {
							handler.sendEmptyMessageDelayed(NORE_MORE_DATA, 100);
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
					Toast.makeText(SaleStatisticsActivity.this, noreMoreDataTips, Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 重置订单数和成交金额
	 */
	private void resetSaleStatisticsCount() {
		// 订单总计
		orderCountTextView.setText("0");
		// 佣金总计
		commissionCountTextView.setText("0");
	}

	/**
	 * 加载数据
	 */
	private void loadSatisticsList() {
		if (currentPageNum > 1) {
			isLoadingMore = true;
		}

		// 统计类型
		String selectType = statisticsTypeTextView.getText().toString().trim();
		int productType = KooniaoTypeUtil.getTypeByString(selectType);
		// 统计天数
		int day = 1;
		String statisticsDay = statisticsDateTextView.getText().toString();
		if (statisticsDay.equals(StringUtil.getStringFromR(R.string.today))) { // 今天
			day = 1;
		} else if (statisticsDay.equals(StringUtil.getStringFromR(R.string.seven_day))) { // 7天
			day = 7;
		} else if (statisticsDay.equals(StringUtil.getStringFromR(R.string.thirty_day))) { // 30天
			day = 30;
		} else if (statisticsDay.equals(StringUtil.getStringFromR(R.string.all))) { // 全部
			day = Integer.MAX_VALUE;
		}

		// 当前类型
		String titleType = titleTextView.getText().toString().trim();
		if ("c".equals(storeType)) {
			// 店铺销售统计
			String storeSaleStatisticsType = StringUtil.getStringFromR(R.string.store_in_sale_statistics);
			if (titleType.equals(storeSaleStatisticsType)) {
				StoreManager.getInstance().loadStoreSalesStatistics(currentPageNum, day, storeSalesStatisticsResultCallback);
			}
			// 产品销售统计
			String productSaleStatisticsType = StringUtil.getStringFromR(R.string.product_in_sale_statistics);
			if (titleType.equals(productSaleStatisticsType)) {
				StoreManager.getInstance().loadProductSalesStatistics(currentPageNum, productType, day, productSalesStatisticsResultCallback);
			}
		} else if ("a".equals(storeType)) {
			// 共成交订单标题
			orderCountTitleTextView.setText(R.string.order_turnover);
			// 共成交金额标题
			commissionCountTitleTextView.setText(R.string.commission_turnover);

			// 自销统计
			String selfSaleStatisticsType = StringUtil.getStringFromR(R.string.self_statistics);
			if (titleType.equals(selfSaleStatisticsType)) {
				StoreManager.getInstance().loadSelfLockingSalesStatistics(currentPageNum, productType, day, selfLockingSalesStatisticsResultCallback);
			}
			// 店铺总销
			String storeTotalSaleStatisticsType = StringUtil.getStringFromR(R.string.store_statistics_total);
			if (titleType.equals(storeTotalSaleStatisticsType)) {
				StoreManager.getInstance().loadStoreTotalSalesStatistics(currentPageNum, productType, day, storeTotalSalesStatisticsResultCallback);
			}
			// 店铺分销统计
			String storeDistributionSaleStatisticsType = StringUtil.getStringFromR(R.string.store_distribution);
			if (titleType.equals(storeDistributionSaleStatisticsType)) {
				StoreManager.getInstance().loadStoreDistributionSalesStatistics(currentPageNum, productType, day, storeDistributionSalesStatisticsResultCallback);
			}
			// 产品分销统计
			String productDistributionSaleStatisticsType = StringUtil.getStringFromR(R.string.product_distribution);
			if (titleType.equals(productDistributionSaleStatisticsType)) {
				StoreManager.getInstance().loadProductDistributionSalesStatistics(currentPageNum, productType, day, productDistributionSalesStatisticsResultCallback);
			}
		}
	}

	private int pageCount; // 总共页码

	private StoreSalesStatistics storeSalesStatistics = new StoreSalesStatistics();
	private List<com.kooniao.travel.model.StoreSalesStatistics.Data> storeSalesStatisticsDataList = new ArrayList<StoreSalesStatistics.Data>();

	/**
	 * 店铺销售统计接口回调
	 */
	StoreSalesStatisticsResultCallback storeSalesStatisticsResultCallback = new StoreSalesStatisticsResultCallback() {

		@Override
		public void result(String errMsg, StoreSalesStatistics storeSalesStatistics, int pageCount) {
			isLoadingMore = false;
			refreshLayout.refreshComplete();
			SaleStatisticsActivity.this.pageCount = pageCount;
			if (errMsg == null && storeSalesStatistics != null) {
				if (storeSalesStatistics.getDataList().isEmpty()) {
					if (currentPageNum == 1) {
						listView.setVisibility(View.GONE);
						noDataLayout.setVisibility(View.VISIBLE);
					} else {
						listView.setVisibility(View.VISIBLE);
						noDataLayout.setVisibility(View.GONE);
					}
					resetSaleStatisticsCount();
				} else {
					if (currentPageNum == 1) {
						listView.setVisibility(View.VISIBLE);
						noDataLayout.setVisibility(View.GONE);
						SaleStatisticsActivity.this.storeSalesStatisticsDataList.clear();
						SaleStatisticsActivity.this.storeSalesStatisticsDataList = storeSalesStatistics.getDataList();
					} else {
						SaleStatisticsActivity.this.storeSalesStatisticsDataList.addAll(storeSalesStatistics.getDataList());
					}

					// 订单总计
					orderCountTextView.setText(String.valueOf(storeSalesStatistics.getOrderTotal()));
					// 佣金总计
					String actualTurnover = StringUtil.getStringFromR(R.string.rmb) + storeSalesStatistics.getCommissionTotal();
					commissionCountTextView.setText(actualTurnover);
					SaleStatisticsActivity.this.storeSalesStatistics.setDataList(SaleStatisticsActivity.this.storeSalesStatisticsDataList);
					adapter.setStoreSalesStatistics(SaleStatisticsActivity.this.storeSalesStatistics);
				}

			} else {
				Toast.makeText(SaleStatisticsActivity.this, errMsg, Toast.LENGTH_SHORT).show();
			}
		}
	};

	private ProductSalesStatistics productSalesStatistics = new ProductSalesStatistics();
	private List<com.kooniao.travel.model.ProductSalesStatistics.Data> productSalesStatisticsDataList = new ArrayList<ProductSalesStatistics.Data>();

	/**
	 * 产品销售统计接口回调
	 */
	ProductSalesStatisticsResultCallback productSalesStatisticsResultCallback = new ProductSalesStatisticsResultCallback() {

		@Override
		public void result(String errMsg, ProductSalesStatistics productSalesStatistics, int pageCount) {
			isLoadingMore = false;
			refreshLayout.refreshComplete();
			SaleStatisticsActivity.this.pageCount = pageCount;
			if (errMsg == null && productSalesStatistics != null) {
				if (productSalesStatistics.getDataList().isEmpty()) {
					if (currentPageNum == 1) {
						listView.setVisibility(View.GONE);
						noDataLayout.setVisibility(View.VISIBLE);
					} else {
						listView.setVisibility(View.VISIBLE);
						noDataLayout.setVisibility(View.GONE);
					}
					resetSaleStatisticsCount();
				} else {
					if (currentPageNum == 1) {
						listView.setVisibility(View.VISIBLE);
						noDataLayout.setVisibility(View.GONE);
						SaleStatisticsActivity.this.productSalesStatisticsDataList.clear();
						SaleStatisticsActivity.this.productSalesStatisticsDataList = productSalesStatistics.getDataList();
					} else {
						SaleStatisticsActivity.this.productSalesStatisticsDataList.addAll(productSalesStatistics.getDataList());
					}

					// 订单总计
					orderCountTextView.setText(String.valueOf(productSalesStatistics.getOrderTotal()));
					// 佣金总计
					String actualTurnover = StringUtil.getStringFromR(R.string.rmb) + productSalesStatistics.getCommissionTotal();
					commissionCountTextView.setText(actualTurnover);
					SaleStatisticsActivity.this.productSalesStatistics.setDataList(SaleStatisticsActivity.this.productSalesStatisticsDataList);
					adapter.setProductSalesStatistics(SaleStatisticsActivity.this.productSalesStatistics);
				}

			} else {
				Toast.makeText(SaleStatisticsActivity.this, errMsg, Toast.LENGTH_SHORT).show();
			}
		}
	};

	private SelfLockingSalesStatistics selfLockingSalesStatistics = new SelfLockingSalesStatistics();
	private List<com.kooniao.travel.model.SelfLockingSalesStatistics.Data> selfLockingSalesStatisticsDataList = new ArrayList<SelfLockingSalesStatistics.Data>();
	/**
	 * 自销统计接口回调
	 */
	SelfLockingSalesStatisticsResultCallback selfLockingSalesStatisticsResultCallback = new SelfLockingSalesStatisticsResultCallback() {

		@Override
		public void result(String errMsg, SelfLockingSalesStatistics selfLockingSalesStatistics, int pageCount) {
			isLoadingMore = false;
			refreshLayout.refreshComplete();
			SaleStatisticsActivity.this.pageCount = pageCount;
			if (errMsg == null && selfLockingSalesStatistics != null) {
				if (selfLockingSalesStatistics.getDataList().isEmpty()) {
					if (currentPageNum == 1) {
						listView.setVisibility(View.GONE);
						noDataLayout.setVisibility(View.VISIBLE);
					} else {
						listView.setVisibility(View.VISIBLE);
						noDataLayout.setVisibility(View.GONE);
					}
					resetSaleStatisticsCount();
				} else {
					if (currentPageNum == 1) {
						listView.setVisibility(View.VISIBLE);
						noDataLayout.setVisibility(View.GONE);
						SaleStatisticsActivity.this.selfLockingSalesStatisticsDataList.clear();
						SaleStatisticsActivity.this.selfLockingSalesStatisticsDataList = selfLockingSalesStatistics.getDataList();
					} else {
						SaleStatisticsActivity.this.selfLockingSalesStatisticsDataList.addAll(selfLockingSalesStatistics.getDataList());
					}

					// 订单总计
					orderCountTextView.setText(String.valueOf(selfLockingSalesStatistics.getOrderTotal()));
					// 佣金总计
					String actualTurnover = StringUtil.getStringFromR(R.string.rmb) + selfLockingSalesStatistics.getTurnoverTotal();
					commissionCountTextView.setText(actualTurnover);
					SaleStatisticsActivity.this.selfLockingSalesStatistics.setDataList(SaleStatisticsActivity.this.selfLockingSalesStatisticsDataList);
					adapter.setSelfLockingSalesStatistics(SaleStatisticsActivity.this.selfLockingSalesStatistics);
				}

			} else {
				Toast.makeText(SaleStatisticsActivity.this, errMsg, Toast.LENGTH_SHORT).show();
			}
		}
	};

	private StoreTotalSalesStatistics storeTotalSalesStatistics = new StoreTotalSalesStatistics();
	private List<com.kooniao.travel.model.StoreTotalSalesStatistics.Data> storeTotalSalesStatisticsDataList = new ArrayList<StoreTotalSalesStatistics.Data>();
	/**
	 * 店铺总销接口回调
	 */
	StoreTotalSalesStatisticsResultCallback storeTotalSalesStatisticsResultCallback = new StoreTotalSalesStatisticsResultCallback() {

		@Override
		public void result(String errMsg, StoreTotalSalesStatistics storeTotalSalesStatistics, int pageCount) {
			isLoadingMore = false;
			refreshLayout.refreshComplete();
			SaleStatisticsActivity.this.pageCount = pageCount;
			if (errMsg == null && storeTotalSalesStatistics != null) {
				if (storeTotalSalesStatistics.getDataList().isEmpty()) {
					if (currentPageNum == 1) {
						listView.setVisibility(View.GONE);
						noDataLayout.setVisibility(View.VISIBLE);
					} else {
						listView.setVisibility(View.VISIBLE);
						noDataLayout.setVisibility(View.GONE);
					}
					resetSaleStatisticsCount();
				} else {
					if (currentPageNum == 1) {
						listView.setVisibility(View.VISIBLE);
						noDataLayout.setVisibility(View.GONE);
						SaleStatisticsActivity.this.storeTotalSalesStatisticsDataList.clear();
						SaleStatisticsActivity.this.storeTotalSalesStatisticsDataList = storeTotalSalesStatistics.getDataList();
					} else {
						SaleStatisticsActivity.this.storeTotalSalesStatisticsDataList.addAll(storeTotalSalesStatistics.getDataList());
					}

					// 订单总计
					orderCountTextView.setText(String.valueOf(storeTotalSalesStatistics.getOrderTotal()));
					// 佣金总计
					String actualTurnover = StringUtil.getStringFromR(R.string.rmb) + storeTotalSalesStatistics.getTurnoverTotal();
					commissionCountTextView.setText(actualTurnover);
					SaleStatisticsActivity.this.storeTotalSalesStatistics.setDataList(SaleStatisticsActivity.this.storeTotalSalesStatisticsDataList);
					adapter.setStoreTotalSalesStatistics(SaleStatisticsActivity.this.storeTotalSalesStatistics);
				}

			} else {
				Toast.makeText(SaleStatisticsActivity.this, errMsg, Toast.LENGTH_SHORT).show();
			}
		}
	};

	private StoreDistributionSalesStatistics storeDistributionSalesStatistics = new StoreDistributionSalesStatistics();
	private List<com.kooniao.travel.model.StoreDistributionSalesStatistics.Data> storeDistributionSalesStatisticsDataList = new ArrayList<StoreDistributionSalesStatistics.Data>();
	/**
	 * 店铺分销统计接口回调
	 */
	StoreDistributionSalesStatisticsResultCallback storeDistributionSalesStatisticsResultCallback = new StoreDistributionSalesStatisticsResultCallback() {

		@Override
		public void result(String errMsg, StoreDistributionSalesStatistics storeDistributionSalesStatistics, int pageCount) {
			isLoadingMore = false;
			refreshLayout.refreshComplete();
			SaleStatisticsActivity.this.pageCount = pageCount;
			if (errMsg == null && storeDistributionSalesStatistics != null) {
				if (storeDistributionSalesStatistics.getDataList().isEmpty()) {
					if (currentPageNum == 1) {
						listView.setVisibility(View.GONE);
						noDataLayout.setVisibility(View.VISIBLE);
					} else {
						listView.setVisibility(View.VISIBLE);
						noDataLayout.setVisibility(View.GONE);
					}
					resetSaleStatisticsCount();
				} else {
					if (currentPageNum == 1) {
						listView.setVisibility(View.VISIBLE);
						noDataLayout.setVisibility(View.GONE);
						SaleStatisticsActivity.this.storeDistributionSalesStatisticsDataList.clear();
						SaleStatisticsActivity.this.storeDistributionSalesStatisticsDataList = storeDistributionSalesStatistics.getDataList();
					} else {
						SaleStatisticsActivity.this.storeDistributionSalesStatisticsDataList.addAll(storeDistributionSalesStatistics.getDataList());
					}

					// 订单总计
					orderCountTextView.setText(String.valueOf(storeDistributionSalesStatistics.getOrderTotal()));
					// 佣金总计
					String actualTurnover = StringUtil.getStringFromR(R.string.rmb) + storeDistributionSalesStatistics.getTurnoverTotal();
					commissionCountTextView.setText(actualTurnover);
					SaleStatisticsActivity.this.storeDistributionSalesStatistics.setDataList(SaleStatisticsActivity.this.storeDistributionSalesStatisticsDataList);
					adapter.setStoreDistributionSalesStatistics(SaleStatisticsActivity.this.storeDistributionSalesStatistics);
				}

			} else {
				Toast.makeText(SaleStatisticsActivity.this, errMsg, Toast.LENGTH_SHORT).show();
			}
		}
	};

	private ProductDistributionSalesStatistics productDistributionSalesStatistics = new ProductDistributionSalesStatistics();
	private List<com.kooniao.travel.model.ProductDistributionSalesStatistics.Data> productDistributionSalesStatisticsDataList = new ArrayList<ProductDistributionSalesStatistics.Data>();
	ProductDistributionSalesStatisticsResultCallback productDistributionSalesStatisticsResultCallback = new ProductDistributionSalesStatisticsResultCallback() {

		@Override
		public void result(String errMsg, ProductDistributionSalesStatistics productDistributionSalesStatistics, int pageCount) {
			isLoadingMore = false;
			refreshLayout.refreshComplete();
			SaleStatisticsActivity.this.pageCount = pageCount;
			if (errMsg == null && productDistributionSalesStatistics != null) {
				if (productDistributionSalesStatistics.getDataList().isEmpty()) {
					if (currentPageNum == 1) {
						listView.setVisibility(View.GONE);
						noDataLayout.setVisibility(View.VISIBLE);
					} else {
						listView.setVisibility(View.VISIBLE);
						noDataLayout.setVisibility(View.GONE);
					}
					resetSaleStatisticsCount();
				} else {
					if (currentPageNum == 1) {
						listView.setVisibility(View.VISIBLE);
						noDataLayout.setVisibility(View.GONE);
						SaleStatisticsActivity.this.productDistributionSalesStatisticsDataList.clear();
						SaleStatisticsActivity.this.productDistributionSalesStatisticsDataList = productDistributionSalesStatistics.getDataList();
					} else {
						SaleStatisticsActivity.this.productDistributionSalesStatisticsDataList.addAll(productDistributionSalesStatistics.getDataList());
					}

					// 订单总计
					orderCountTextView.setText(String.valueOf(productDistributionSalesStatistics.getOrderTotal()));
					// 佣金总计
					String actualTurnover = StringUtil.getStringFromR(R.string.rmb) + String.valueOf(productDistributionSalesStatistics.getTurnoverTotal());
					commissionCountTextView.setText(actualTurnover);
					SaleStatisticsActivity.this.productDistributionSalesStatistics.setDataList(SaleStatisticsActivity.this.productDistributionSalesStatisticsDataList);
					adapter.setProductDistributionSalesStatistics(SaleStatisticsActivity.this.productDistributionSalesStatistics);
				}

			} else {
				Toast.makeText(SaleStatisticsActivity.this, errMsg, Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * 产品分销统计接口回调
	 */

	/**
	 * 后退按钮点击
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
		typePopupWindow.dismiss();
		datePopupWindow.dismiss();

		if ("c".equals(storeType)) {
			if (!cStoreStatisticsTypePopupWindow.isShowing()) {
				cStoreStatisticsTypePopupWindow.setBlueitem(titleTextView.getText().toString()); 
				cStoreStatisticsTypePopupWindow.showAsDropDown(titleBarTabView, 0, 0);
			} else {
				cStoreStatisticsTypePopupWindow.dismiss();
			}
		} else if ("a".equals(storeType)) {
			if (!aStoreStatisticsTypePopupWindow.isShowing()) {
				aStoreStatisticsTypePopupWindow.setBlueitem(titleTextView.getText().toString()); 
				aStoreStatisticsTypePopupWindow.showAsDropDown(titleBarTabView, 0, 0);
			} else {
				aStoreStatisticsTypePopupWindow.dismiss();
			}
		}
	}

	/**
	 * 点击店铺icon
	 */
	@Click(R.id.iv_store)
	void onStoreClick() {
		finish();
		Intent intent = new Intent(getBaseContext(), BottomTabBarActivity_.class);
		intent.putExtra(Define.TYPE, Define.STORE);
		startActivity(intent);
	}

	/**
	 * 点击选择类型
	 */
	@Click(R.id.ll_product_manage_type_select)
	void onTypeSelectClick() {
		if (!typePopupWindow.isShowing()) {
			datePopupWindow.dismiss();
			typePopupWindow.setBlueitem(statisticsTypeTextView.getText().toString()); 
			typePopupWindow.showAsDropDown(typeSelectLayout, 0, 0);
		} else {
			typePopupWindow.dismiss();
		}
	}

	/**
	 * 点击选择日期
	 */
	@Click(R.id.ll_date_select)
	void onDateSelectClick() {
		if (!datePopupWindow.isShowing()) {
			typePopupWindow.dismiss();
			datePopupWindow.setBlueitem(statisticsDateTextView.getText().toString());
			datePopupWindow.showAsDropDown(dateSelectLayout, 0, 0);
		} else {
			datePopupWindow.dismiss();
		}
	}

	/**
	 * 下拉刷新
	 */
	public void onRefresh() {
		currentPageNum = 1;
		isNeedToShowNoMoreTips = true;
		loadSatisticsList();
	}

}
