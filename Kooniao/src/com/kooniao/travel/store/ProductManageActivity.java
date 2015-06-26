package com.kooniao.travel.store;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.BlueTextShowPopup;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.home.CombineProductDetailActivity_;
import com.kooniao.travel.home.LineProductDetailActivity_;
import com.kooniao.travel.home.NonLineProductDetailActivity_;
import com.kooniao.travel.manager.ProductManager;
import com.kooniao.travel.manager.StoreManager;
import com.kooniao.travel.manager.StoreManager.StoreProductListResultCallback;
import com.kooniao.travel.manager.StoreManager.StringResultCallback;
import com.kooniao.travel.model.StoreProduct;
import com.kooniao.travel.onekeyshare.OnekeyShare;
import com.kooniao.travel.store.DistributionSetActivity.ParamsSetting;
import com.kooniao.travel.store.DistributionSetActivity.SettingWay;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.KooniaoTypeUtil;
import com.kooniao.travel.utils.StringUtil;
import com.kooniao.travel.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * 产品管理
 * 
 * @author ke.wei.quan
 * 
 */
@SuppressLint({ "HandlerLeak", "InflateParams" })
@EActivity
public class ProductManageActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		setContentView();
		findViews();
		initView();
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 300);
	}

	private int sid; // 店铺id
	private String storeType; // 店铺类型
	private AStoreProductManagerAdapter aStoreManagerAdapter;
	private CStoreProductManagerAdapter cStoreManagerAdapter;
	private ImageLoader imageLoader;

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			sid = intent.getIntExtra(Define.SID, 0);
			storeType = intent.getStringExtra(Define.STORE_TYPE);
		}

		if ("a".equals(storeType)) {
			aStoreManagerAdapter = new AStoreProductManagerAdapter(ProductManageActivity.this);
			aStoreManagerAdapter.setProducts(storeProducts);
			aStoreManagerAdapter.setOnListItemRequestListener(aItemRequestListener);
		} else {
			cStoreManagerAdapter = new CStoreProductManagerAdapter(ProductManageActivity.this);
			cStoreManagerAdapter.setOnListItemRequestListener(cItemRequestListener);
		}

		// 初始化图片加载器
		imageLoader = ImageLoader.getInstance();
	}

	/**
	 * 设置内容显示view
	 */
	private void setContentView() {
		if ("a".equals(storeType)) {
			setContentView(R.layout.activity_a_store_product_manage);
		} else {
			setContentView(R.layout.activity_c_store_product_manage);
		}
	}

	/*
	 * 公共
	 */
	@ViewById(R.id.titlebar_main)
	View titlebarLayout; // titlebar布局
	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout; // 刷新的布局
	@ViewById(R.id.lv_product)
	ListView listView; // 数据布局
	@ViewById(R.id.layout_no_data)
	View noDataLayout; // 无数据布局
	@ViewById(R.id.tv_add_product)
	TextView addProducTipsTextView;
	@ViewById(R.id.ll_add_product)
	View addProductLayout; // 添加产品
	/*
	 * A店相关
	 */

	/*
	 * C店相关
	 */
	View typeSelectLayout; // 产品分类顶部刷选布局
	View statusSelectLayout; // 产品销售状态顶部刷选布局
	TextView typeTextView; // 产品管理分类
	TextView statusTextView; // 产品管理状态

	/**
	 * 查找view
	 */
	private void findViews() {
		if ("c".equals(storeType)) {
			typeSelectLayout = findViewById(R.id.ll_product_manage_type_select);
			statusSelectLayout = findViewById(R.id.ll_product_manage_statue_select);
			typeTextView = (TextView) findViewById(R.id.tv_product_manage_type);
			statusTextView = (TextView) findViewById(R.id.tv_product_manage_status);
		}
	}

	View typeSelectPopupLayout; // 分类刷选布局
	BlueTextShowPopup typeSelectPopupWindow; // 产品分类刷选
	View statusSelectPopupLayout; // 销售状态刷选布局
	BlueTextShowPopup statusSelectPopupWindow; // 产品状态刷选

	/**
	 * 初始化界面
	 */
	private void initView() {
		if ("a".equals(storeType)) {
			listView.setAdapter(aStoreManagerAdapter);
		} else {
			// 分类刷选初始化
			initTypeSelectPopupWindow();
			// 产品销售状态刷选初始化
			initStatusSelectPopupWindow();
			listView.setAdapter(cStoreManagerAdapter);
		}

		listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true, new ListViewScrollListener()));
		// 下拉刷新配置
		MaterialHeader materialHeader = new MaterialHeader(ProductManageActivity.this);
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

	/**
	 * 分类刷选弹出窗初始化
	 */
	private void initTypeSelectPopupWindow() {
		typeSelectPopupLayout = LayoutInflater.from(this).inflate(R.layout.popup_product_type_select, null);
		// 全部分类
		typeSelectPopupLayout.findViewById(R.id.tv_type_all).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initSelectedConditionsText(typeTextView, R.string.type_all, typeSelectPopupWindow);
			}
		});
		// 线路分类
		typeSelectPopupLayout.findViewById(R.id.tv_type_line).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initSelectedConditionsText(typeTextView, R.string.line, typeSelectPopupWindow);
			}
		});
		// 酒店分类
		typeSelectPopupLayout.findViewById(R.id.tv_type_hotel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initSelectedConditionsText(typeTextView, R.string.hotel, typeSelectPopupWindow);
			}
		});
		// 景点门票分类
		typeSelectPopupLayout.findViewById(R.id.tv_type_location_ticket).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initSelectedConditionsText(typeTextView, R.string.location_ticket_type, typeSelectPopupWindow);
			}
		});
		// 休闲娱乐分类
		typeSelectPopupLayout.findViewById(R.id.tv_type_category_amusement).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initSelectedConditionsText(typeTextView, R.string.category_amusement, typeSelectPopupWindow);
			}
		});
		// 餐饮美食分类
		typeSelectPopupLayout.findViewById(R.id.tv_type_category_food).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initSelectedConditionsText(typeTextView, R.string.category_food, typeSelectPopupWindow);
			}
		});
		// 购物优惠分类
		typeSelectPopupLayout.findViewById(R.id.tv_type_category_shopping).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initSelectedConditionsText(typeTextView, R.string.category_shopping, typeSelectPopupWindow);
			}
		});
		// 组合产品分类
		typeSelectPopupLayout.findViewById(R.id.tv_type_category_group_product).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initSelectedConditionsText(typeTextView, R.string.category_group_product, typeSelectPopupWindow);
			}
		});
		typeSelectPopupWindow = new BlueTextShowPopup(typeSelectPopupLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		typeSelectPopupWindow.setFocusable(false);
	}

	/**
	 * 分类刷选弹出窗初始化
	 */
	private void initStatusSelectPopupWindow() {
		statusSelectPopupLayout = LayoutInflater.from(this).inflate(R.layout.popup_product_status_select, null);
		// 所有产品
		statusSelectPopupLayout.findViewById(R.id.tv_product_status_all).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initSelectedConditionsText(statusTextView, R.string.status_all, statusSelectPopupWindow);
			}
		});
		// 在售中
		statusSelectPopupLayout.findViewById(R.id.tv_product_status_in_the_sale).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initSelectedConditionsText(statusTextView, R.string.status_in_the_sale, statusSelectPopupWindow);
			}
		});
		// 已下架
		statusSelectPopupLayout.findViewById(R.id.tv_product_status_shelf).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initSelectedConditionsText(statusTextView, R.string.status_have_the_shelf, statusSelectPopupWindow);
			}
		});
		statusSelectPopupWindow = new BlueTextShowPopup(statusSelectPopupLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		statusSelectPopupWindow.setFocusable(false);
	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 店铺icon按钮
	 */
	@Click(R.id.iv_store)
	void onTitleBarStoreClick() {
		if ("c".equals(storeType)) {
			finish();
			Intent storeIntent = new Intent(ProductManageActivity.this, BottomTabBarActivity_.class);
			storeIntent.putExtra(Define.TYPE, Define.STORE);
			startActivity(storeIntent);
		}
	}

	PopupWindow filterPopupWindow;

	/**
	 * A店筛选条件按钮
	 */
	@Click(R.id.iv_filter)
	void onFilterClick() {
		if (filterPopupWindow == null) {
			initFilterPopupWindow();
		}

		if (filterPopupWindow.isShowing()) {
			filterPopupWindow.dismiss();
		} else {
			filterPopupWindow.showAsDropDown(titlebarLayout);
		}
	}

	TextView onSellTextView;
	TextView shelfTextView;
	TextView selloutTextView;
	TextView recommendedTextView;
	TextView notRecommendTextView;
	TextView settedTextView;
	TextView notSetTextView;

	TextView hotelTextView;
	TextView scenicTextView;
	TextView amusementTextView;
	TextView foodTextView;
	TextView groupProductSetTextView;
	TextView lineTextView;

	/**
	 * 初始化筛选弹出窗
	 */
	void initFilterPopupWindow() {
		View filterLayout = LayoutInflater.from(ProductManageActivity.this).inflate(R.layout.popup_product_manage_filter, null);
		/*
		 * 状态
		 */
		onSellTextView = (TextView) filterLayout.findViewById(R.id.tv_onsell);
		onSellTextView.setOnClickListener(new ProductFilterSubViewClickListener());
		shelfTextView = (TextView) filterLayout.findViewById(R.id.tv_shelf);
		shelfTextView.setOnClickListener(new ProductFilterSubViewClickListener());
		selloutTextView = (TextView) filterLayout.findViewById(R.id.tv_sellout);
		selloutTextView.setOnClickListener(new ProductFilterSubViewClickListener());
		/*
		 * 推荐
		 */
		recommendedTextView = (TextView) filterLayout.findViewById(R.id.tv_recommended);
		recommendedTextView.setOnClickListener(new ProductFilterSubViewClickListener());
		notRecommendTextView = (TextView) filterLayout.findViewById(R.id.tv_not_recommend);
		notRecommendTextView.setOnClickListener(new ProductFilterSubViewClickListener());
		/*
		 * 分销
		 */
		settedTextView = (TextView) filterLayout.findViewById(R.id.tv_setted);
		settedTextView.setOnClickListener(new ProductFilterSubViewClickListener());
		notSetTextView = (TextView) filterLayout.findViewById(R.id.tv_not_set);
		notSetTextView.setOnClickListener(new ProductFilterSubViewClickListener());
		/*
		 * 产品类型
		 */
		hotelTextView = (TextView) filterLayout.findViewById(R.id.tv_hotel);
		hotelTextView.setOnClickListener(new ProductFilterSubViewClickListener());
		scenicTextView = (TextView) filterLayout.findViewById(R.id.tv_scenic);
		scenicTextView.setOnClickListener(new ProductFilterSubViewClickListener());
		amusementTextView = (TextView) filterLayout.findViewById(R.id.tv_amusement);
		amusementTextView.setOnClickListener(new ProductFilterSubViewClickListener());
		foodTextView = (TextView) filterLayout.findViewById(R.id.tv_food);
		foodTextView.setOnClickListener(new ProductFilterSubViewClickListener());
		groupProductSetTextView = (TextView) filterLayout.findViewById(R.id.tv_group_product);
		groupProductSetTextView.setOnClickListener(new ProductFilterSubViewClickListener());
		lineTextView = (TextView) filterLayout.findViewById(R.id.tv_line);
		lineTextView.setOnClickListener(new ProductFilterSubViewClickListener());
		// 重置按钮
		TextView resetTextView = (TextView) filterLayout.findViewById(R.id.tv_reset);
		resetTextView.setOnClickListener(new ProductFilterSubViewClickListener());
		// 确认按钮
		View confirmView = filterLayout.findViewById(R.id.ll_confirm);
		confirmView.setOnClickListener(new ProductFilterSubViewClickListener());

		filterPopupWindow = new PopupWindow(filterLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		filterPopupWindow.setFocusable(false);
	}

	class ProductFilterSubViewClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_onsell: // 在售
				changeStatusFilter(true, false, false);
				break;

			case R.id.tv_shelf: // 已下架
				changeStatusFilter(false, true, false);
				break;

			case R.id.tv_sellout: // 已售罄
				changeStatusFilter(false, false, true);
				break;

			case R.id.tv_recommended: // 已推荐
				changeRecommendFilter(true, false);
				break;

			case R.id.tv_not_recommend: // 未推荐
				changeRecommendFilter(false, true);
				break;

			case R.id.tv_setted: // 已设置
				changeSetFilter(true, false);
				break;

			case R.id.tv_not_set: // 未设置
				changeSetFilter(false, true);
				break;

			case R.id.tv_hotel: // 酒店
				boolean isSelected = hotelTextView.isSelected();
				if (isSelected) {
					hotelTextView.setSelected(false);
				} else {
					hotelTextView.setSelected(true);
				}
				break;

			case R.id.tv_scenic: // 景点门票
				isSelected = scenicTextView.isSelected();
				if (isSelected) {
					scenicTextView.setSelected(false);
				} else {
					scenicTextView.setSelected(true);
				}
				break;

			case R.id.tv_amusement: // 休闲娱乐
				isSelected = amusementTextView.isSelected();
				if (isSelected) {
					amusementTextView.setSelected(false);
				} else {
					amusementTextView.setSelected(true);
				}
				break;

			case R.id.tv_food: // 餐饮美食
				isSelected = foodTextView.isSelected();
				if (isSelected) {
					foodTextView.setSelected(false);
				} else {
					foodTextView.setSelected(true);
				}
				break;

			case R.id.tv_group_product: // 组合产品
				isSelected = groupProductSetTextView.isSelected();
				if (isSelected) {
					groupProductSetTextView.setSelected(false);
				} else {
					groupProductSetTextView.setSelected(true);
				}
				break;

			case R.id.tv_line: // 线路
				isSelected = lineTextView.isSelected();
				if (isSelected) {
					lineTextView.setSelected(false);
				} else {
					lineTextView.setSelected(true);
				}
				break;

			case R.id.tv_reset: // 重置
				resetFilter();
				break;

			case R.id.ll_confirm: // 确认
				confirmFilter();
				break;

			default:
				break;
			}
		}

	}

	/**
	 * 更改筛选条件的状态
	 * 
	 * @param isOnSell
	 * @param isShelf
	 * @param isSellout
	 */
	private void changeStatusFilter(boolean isOnSell, boolean isShelf, boolean isSellout) {
		onSellTextView.setSelected(isOnSell);
		shelfTextView.setSelected(isShelf);
		selloutTextView.setSelected(isSellout);
	}

	/**
	 * 更改筛选条件的是否推荐
	 * 
	 * @param isRecommend
	 * @param isNotRecommend
	 */
	private void changeRecommendFilter(boolean isRecommend, boolean isNotRecommend) {
		recommendedTextView.setSelected(isRecommend);
		notRecommendTextView.setSelected(isNotRecommend);
	}

	/**
	 * 更改筛选条件的是否设置
	 * 
	 * @param isSetted
	 * @param isNotSet
	 */
	private void changeSetFilter(boolean isSetted, boolean isNotSet) {
		settedTextView.setSelected(isSetted);
		notSetTextView.setSelected(isNotSet);
	}

	/**
	 * 重置所有筛选条件
	 */
	private void resetFilter() {
		onSellTextView.setSelected(false);
		shelfTextView.setSelected(false);
		selloutTextView.setSelected(false);
		recommendedTextView.setSelected(false);
		notRecommendTextView.setSelected(false);
		settedTextView.setSelected(false);
		notSetTextView.setSelected(false);
		hotelTextView.setSelected(false);
		scenicTextView.setSelected(false);
		amusementTextView.setSelected(false);
		foodTextView.setSelected(false);
		groupProductSetTextView.setSelected(false);
		lineTextView.setSelected(false);
	}

	/**
	 * 确认筛选条件
	 */
	private void confirmFilter() {
		// 产品状态（0：全部、1:出售中、3:未出售、4、售罄）
		if (onSellTextView.isSelected()) {
			statusType = 1;
		} else if (shelfTextView.isSelected()) {
			statusType = 3;
		} else if (selloutTextView.isSelected()) {
			statusType = 4;
		} else {
			statusType = 0;
		}

		// 是否推荐（0：没推荐,1：推荐，2:全部）
		if (recommendedTextView.isSelected()) {
			recommend = 1;
		} else if (notRecommendTextView.isSelected()) {
			recommend = 0;
		} else {
			recommend = 2;
		}

		// 是否分销（0：不是，1:是，2：全部）
		if (settedTextView.isSelected()) {
			affiliateStatus = 1;
		} else if (notSetTextView.isSelected()) {
			affiliateStatus = 0;
		} else {
			affiliateStatus = 2;
		}

		// 产品类型：全部：0，线路：4，组合：2，酒店：5，美食：8，娱乐：7，景点门票：6
		if (hotelTextView.isSelected()) {
			productType = "5";
		}

		if (scenicTextView.isSelected()) {
			productType += ",6";
		}
		if (!hotelTextView.isSelected() && scenicTextView.isSelected()) {
			productType = "6";
		}

		if (amusementTextView.isSelected()) {
			productType += ",7";
		}
		if (!hotelTextView.isSelected() && !scenicTextView.isSelected() && amusementTextView.isSelected()) {
			productType = "7";
		}

		if (foodTextView.isSelected()) {
			productType += ",8";
		}
		if (!hotelTextView.isSelected() && !scenicTextView.isSelected() && !amusementTextView.isSelected() //
				&& foodTextView.isSelected()) {
			productType = "8";
		}

		if (groupProductSetTextView.isSelected()) {
			productType += ",2";
		}
		if (!hotelTextView.isSelected() && !scenicTextView.isSelected() && !amusementTextView.isSelected()//
				&& !foodTextView.isSelected() && groupProductSetTextView.isSelected()) {
			productType = "2";
		}

		if (lineTextView.isSelected()) {
			productType += ",4";
		}
		if (!hotelTextView.isSelected() && !scenicTextView.isSelected() && !amusementTextView.isSelected()//
				&& !foodTextView.isSelected() && !groupProductSetTextView.isSelected() && lineTextView.isSelected()) {
			productType = "4";
		}

		if (!hotelTextView.isSelected() && !scenicTextView.isSelected() && !amusementTextView.isSelected() && //
				!foodTextView.isSelected() && !groupProductSetTextView.isSelected() && !lineTextView.isSelected()) {
			productType = "0";
		}

		filterPopupWindow.dismiss();
		// 请求刷新数据
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 200);
	}

	/**
	 * 产品分类刷选点击
	 */
	@Click(R.id.ll_product_manage_type_select)
	void onProductTypeClick() {
		if (statusSelectPopupWindow.isShowing()) {
			statusSelectPopupWindow.dismiss();
		}

		if (typeSelectPopupWindow.isShowing()) {
			typeSelectPopupWindow.dismiss();
		} else {
			typeSelectPopupWindow.setBlueitem(typeTextView.getText().toString());
			typeSelectPopupWindow.showAsDropDown(typeSelectLayout);
		}
	}

	/**
	 * 销售状态刷选点击
	 */
	@Click(R.id.ll_product_manage_statue_select)
	void onProductStatusClick() {
		if (typeSelectPopupWindow.isShowing()) {
			typeSelectPopupWindow.dismiss();
		}

		if (statusSelectPopupWindow.isShowing()) {
			statusSelectPopupWindow.dismiss();
		} else {
			statusSelectPopupWindow.setBlueitem(statusTextView.getText().toString());
			statusSelectPopupWindow.showAsDropDown(statusSelectLayout);
		}
	}

	private int currentPageNum = 1; // 当前页码
	private boolean isNeedToShowNoMoreTips = true; // 是否需要提示没有更多数据了

	/**
	 * 下拉刷新
	 */
	public void onRefresh() {
		currentPageNum = 1;
		isNeedToShowNoMoreTips = true;
		loadProductList();
	}

	private int statusType = 0; // 产品状态（0：全部、1:出售中、3:未出售、4、售罄）
	private int recommend = 2; // 是否推荐（0：没推荐,1：推荐，2:全部）
	private int affiliateStatus = 2; // 是否分销（0：不是，1:是，2：全部）
	private String productType = "0"; // 产品类型：全部：0，线路：4，组合：2，酒店：5，美食：8，娱乐：7
	private boolean isLoadingMore; // 是否正在加载更多
	private int pageCount = 0; // 总共的页数
	List<StoreProduct> storeProducts = new ArrayList<StoreProduct>();

	/**
	 * 加载产品列表
	 */
	private void loadProductList() {
		if (currentPageNum > 1) {
			isLoadingMore = false;
		}

		StoreManager.getInstance().loadShopProductList(productType, storeType, sid, statusType, recommend, affiliateStatus, currentPageNum, new StoreProductListResultCallback() {

			@Override
			public void result(String errMsg, List<StoreProduct> storeProducts, int pageCount) {
				loadProductListComplete(errMsg, storeProducts, pageCount);
			}
		});
	}

	/**
	 * 获取店铺产品列表完成
	 * 
	 * @param errMsg
	 * @param storeProducts
	 * @param pageCount
	 */
	private void loadProductListComplete(String errMsg, List<StoreProduct> storeProducts, int pageCount) {
		isLoadingMore = false;
		refreshLayout.refreshComplete();
		this.pageCount = pageCount;
		if (errMsg == null && storeProducts != null) {
			noDataLayout.setVisibility(View.GONE);
			if (currentPageNum == 1) {
				// 是否展示无数据布局
				if (storeProducts.isEmpty()) {
					noDataLayout.setVisibility(View.VISIBLE);
				} else {
					noDataLayout.setVisibility(View.GONE);
				}

				// 填数据
				this.storeProducts.clear();
				this.storeProducts = storeProducts;
			} else {
				this.storeProducts.addAll(storeProducts);
			}

			if ("a".equals(storeType)) {
				aStoreManagerAdapter.setProducts(this.storeProducts);
			} else {
				cStoreManagerAdapter.setProducts(this.storeProducts);
			}

		} else {
			showToast(errMsg);
		}
	}

	Dialog dialog; // 产品备注信息对话框

	@StringRes(R.string.operate_delete_success)
	String operateDeleteSuccessTips; // 删除成功提示

	CStoreProductManagerAdapter.ListItemRequestListener cItemRequestListener = new CStoreProductManagerAdapter.ListItemRequestListener() {

		@Override
		public void onLoadCoverImgListener(String imgUrl, ImageView coverImageView) { // 加载图片
			ImageLoaderUtil.loadListCoverImg(imageLoader, imgUrl, coverImageView);
		}

		@Override
		public void onItemClickListener(int position) {
			onListItemClickListener(position);
		}

		@Override
		public void onDeleteListener(final int position) { // 删除
			showProgressDialog();

			StoreProduct storeProduct = storeProducts.get(position);
			StoreManager.getInstance().deleteProduct(storeProduct.getProductId(), sid, storeType, new StringResultCallback() {

				@Override
				public void result(String errMsg) {
					deleteRequestComplete(position, errMsg);
				}
			});
		}

		View dialogContentView;
		TextView commissionDescTextView; // 返佣说明
		TextView remarkTextView; // 备注

		// 点击备注
		@Override
		public void onRemarkClickListener(int position) {
			dialogContentView = LayoutInflater.from(ProductManageActivity.this).inflate(R.layout.dialog_remark_contentview, null);

			StoreProduct storeProduct = storeProducts.get(position);
			// 返佣说明
			commissionDescTextView = (TextView) dialogContentView.findViewById(R.id.tv_commission_desc);
			// 返佣类型
			int brokerageType = storeProduct.getBrokerageType();
			String commissionText = "";
			if (brokerageType == 0) {
				commissionText = StringUtil.getStringFromR(R.string.commission_desc_tips_one) + storeProduct.getBrokerage() + StringUtil.getStringFromR(R.string.commission_desc_tips_two);
			} else {
				commissionText = StringUtil.getStringFromR(R.string.commission_desc_tips) + storeProduct.getPercentage() + "%";
			}
			commissionDescTextView.setText(commissionText);
			// 备注
			remarkTextView = (TextView) dialogContentView.findViewById(R.id.tv_remark);
			String remark = storeProduct.getAffiliate_comment();
			remark = "".equals(remark) ? StringUtil.getStringFromR(R.string.none) : remark;
			remarkTextView.setText(remark);

			dialog = new Dialog(ProductManageActivity.this, View.VISIBLE, View.GONE, dialogContentView);
			dialog.setCancelable(false);
			dialog.setOnAcceptButtonClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}
	};

	AStoreProductManagerAdapter.ListItemRequestListener aItemRequestListener = new AStoreProductManagerAdapter.ListItemRequestListener() {

		@Override
		public void onLoadCoverImgListener(String imgUrl, ImageView coverImageView) {
			ImageLoaderUtil.loadListCoverImg(imageLoader, imgUrl, coverImageView);
		}

		@Override
		public void onShareClickListener(int position) { // 分享
			StoreProduct storeProduct = storeProducts.get(position);
			share(storeProduct);
		}

		@Override
		public void onRecommendClickListener(int position) { // 推荐
			changeRecommendStatus(position, 1);
		}

		@Override
		public void onRecommendedClickListener(int position) { // 已推荐
			changeRecommendStatus(position, 0);
		}

		@Override
		public void onDistributeListener(int position) { // 分销
			StoreProduct storeProduct = storeProducts.get(position);
			Intent intent = new Intent(ProductManageActivity.this, DistributionSetActivity_.class);
			int affiliateStatus = storeProduct.getAffiliateStatus();// (0:不分销,1:分销)
			if (affiliateStatus != 0) { // 有设置过分销
				String settingWay = null;
				int templateId = storeProduct.getProductCommissionTheme(); // 分销模板id
				if (templateId == 0) { // 手动设置
					settingWay = SettingWay.MANUAL.type;
					// 返佣类型(0：按金额，1：按百分比)
					int brokerageType = storeProduct.getBrokerageType();
					String paramsSetting = brokerageType == 0 ? ParamsSetting.SUM.type : ParamsSetting.PERCENT.type;
					intent.putExtra(Define.PARAMS_SETTING, paramsSetting);
					// 金额
					String paramsSettingSum = "";
					if (brokerageType == 0) {
						paramsSettingSum = storeProduct.getPercentage() + "";
					} else {
						paramsSettingSum = storeProduct.getPercentage() + "";
					}
					intent.putExtra(Define.PARAMS_SETTING_SUM, paramsSettingSum);

				} else { // 模板设置
					settingWay = SettingWay.TEMPLATE.type;
					intent.putExtra(Define.TEMPLATE_ID, templateId);
				}
				intent.putExtra(Define.SETTING_WAY, settingWay);
			}
			intent.putExtra(Define.PID, storeProduct.getProductId());
			startActivityForResult(intent, REQUEST_CODE_DISTRIBUTION);
		}

		@Override
		public void onEditListener(int position) { // 编辑
			StoreProduct storeProduct = storeProducts.get(position);
			int pid = storeProduct.getProductId();
			Intent intent = new Intent(ProductManageActivity.this, ProductEditActivity_.class);
			intent.putExtra(Define.PID, pid);
			startActivityForResult(intent,REQUEST_CODE_PRODUCT_EDIT);
		}

		@Override
		public void onShelfListener(final int position) { // 下架
			showProgressDialog();

			final StoreProduct storeProduct = storeProducts.get(position);
			StoreManager.getInstance().shelvesProduct(storeProduct.getProductId(), sid, storeType, new StringResultCallback() {

				@Override
				public void result(String errMsg) {
					soldoutRequestComplete(storeProduct, errMsg);
				}
			});
		}

		@Override
		public void onPutawayListener(final int position) { // 上架
			showProgressDialog();

			StoreProduct storeProduct = storeProducts.get(position);
			StoreManager.getInstance().addedProduct(storeProduct.getProductId(), sid, storeType, new StringResultCallback() {

				@Override
				public void result(String errMsg) {
					putawayRequestComplete(position, errMsg);
				}
			});
		}

		@Override
		public void onDeleteListener(final int position) { // 删除
			dialog = new Dialog(ProductManageActivity.this, "删除提示", "您确定要删除此产品吗?");
			if (!dialog.isShowing()) {
				dialog.show();
			}
			dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					showProgressDialog();
					StoreProduct storeProduct = storeProducts.get(position);
					StoreManager.getInstance().deleteProduct(storeProduct.getProductId(), sid, storeType, new StringResultCallback() {

						@Override
						public void result(String errMsg) {
							deleteRequestComplete(position, errMsg);
						}
					});
				}
			});
		}

		@Override
		public void onItemClickListener(int position) {
			onListItemClickListener(position);
		}

	};

	/**
	 * 点击条目
	 * 
	 * @param position
	 */
	private void onListItemClickListener(int position) {
		StoreProduct storeProduct = storeProducts.get(position);
		int productType = storeProduct.getProductType();
		Intent productDetailIntent = null;
		if (productType == 2) {
			// 组合产品详情
			productDetailIntent = new Intent(getBaseContext(), CombineProductDetailActivity_.class);
		} else {
			if (productType == 4) {
				// 线路产品详情
				productDetailIntent = new Intent(getBaseContext(), LineProductDetailActivity_.class);
			} else {
				// 非线路产品详情
				productDetailIntent = new Intent(getBaseContext(), NonLineProductDetailActivity_.class);
			}
		}

		if (productDetailIntent != null) {
			productDetailIntent.putExtra(Define.PID, storeProduct.getProductId());
			productDetailIntent.putExtra(Define.STORE_TYPE, storeType);
			productDetailIntent.putExtra(Define.TYPE, productType);
			productDetailIntent.putExtra(Define.SID, sid);
			startActivity(productDetailIntent);
		}
	}

	/**
	 * 删除产品请求完成
	 * 
	 * @param position
	 * @param errMsg
	 */
	void deleteRequestComplete(final int position, String errMsg) {
		dissmissProgressDialog();

		if (errMsg == null) {
			storeProducts.remove(position);
			if ("a".equals(storeType)) {
				aStoreManagerAdapter.setProducts(storeProducts);
			} else {
				cStoreManagerAdapter.setProducts(storeProducts);
			}
			showToast(operateDeleteSuccessTips);
		} else {
			showToast(errMsg);
		}
	}

	@StringRes(R.string.operate_added_success)
	String operateAddedSuccessTips; // 上架成功提示
	@StringRes(R.string.operate_shelves_success)
	String operateShelvesSuccessTips; // 下架成功提示

	/**
	 * 下架请求完成
	 * 
	 * @param position
	 * @param errMsg
	 */
	void soldoutRequestComplete(final StoreProduct storeProduct, String errMsg) {
		dissmissProgressDialog();

		if (errMsg == null) {
			// 1:出售中、2:删除、3:未出售
			storeProduct.setProductStatus(3);
			storeProduct.setProductRecommend(0); // 改为未推荐
			aStoreManagerAdapter.setProducts(storeProducts);
			showToast(operateShelvesSuccessTips);
		} else {
			showToast(errMsg);
		}
	}

	/**
	 * 上架请求完成
	 * 
	 * @param position
	 * @param errMsg
	 */
	void putawayRequestComplete(final int position, String errMsg) {
		dissmissProgressDialog();

		if (errMsg == null) {
			StoreProduct storeProduct = storeProducts.get(position);
			// 1:出售中、2:删除、3:未出售
			storeProduct.setProductStatus(1);
			aStoreManagerAdapter.setProducts(storeProducts);
			showToast(operateAddedSuccessTips);
		} else {
			showToast(errMsg);
		}
	}

	/**
	 * 分享
	 * 
	 * @param storeProduct
	 */
	void share(StoreProduct storeProduct) {
		OnekeyShare onekeyShare = new OnekeyShare();
		onekeyShare.setTitle(storeProduct.getProductName());
		onekeyShare.setText("我在“酷鸟”看到" + storeProduct.getProductName() + "，很不错！你也来看看~" + storeProduct.getProductShareUrl());
		onekeyShare.setUrl(storeProduct.getProductShareUrl());
		onekeyShare.setNotification(R.drawable.app_logo, "酷鸟");
		onekeyShare.setImageUrl(storeProduct.getImg());
		onekeyShare.show(ProductManageActivity.this);
	}

	/**
	 * 更改产品推荐状态
	 * 
	 * @param productId
	 * @param recommendStatus
	 *            (0:默认，1:推荐)
	 */
	void changeRecommendStatus(int position, final int recommendStatus) {
		showProgressDialog();

		final StoreProduct storeProduct = storeProducts.get(position);
		int productId = storeProduct.getProductId();
		ProductManager.getInstance().productRecommend(productId, recommendStatus, new ProductManager.StringResultCallback() {

			@Override
			public void result(String errMsg) {
				dissmissProgressDialog();
				if (errMsg == null) {
					storeProduct.setProductRecommend(recommendStatus);
					aStoreManagerAdapter.setProducts(storeProducts);
				} else {
					showToast(errMsg);
				}
			}
		});
	}

	/**
	 * 设置刷选条件
	 * 
	 * @param textView
	 * @param resId
	 * @param popupWindow
	 */
	private void initSelectedConditionsText(TextView textView, int resId, PopupWindow popupWindow) {
		popupWindow.dismiss();
		textView.setText(resId);
		productType = KooniaoTypeUtil.getTypeByString(typeTextView.getText().toString()) + "";
		statusType = KooniaoTypeUtil.getProductStatusByString(statusTextView.getText().toString());
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
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
							loadProductList();
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
	String noMoreDataTips; // 没有更多数据的提示语

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
					showToast(noMoreDataTips);
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 添加产品
	 */
	@Click(R.id.ll_add_product)
	void onAddProductClick() {
		if ("a".equals(storeType)) {
			Intent intent = new Intent(ProductManageActivity.this, ProductCategoryActivity_.class);
			startActivityForResult(intent, REQUEST_CODE_PRODUCT_ADDSUCCESS);
		} else {
			Intent intent = new Intent(ProductManageActivity.this, ProductLibraryActivity_.class);
			int productType = KooniaoTypeUtil.getTypeByString(typeTextView.getText().toString());
			intent.putExtra(Define.PRODUCT_TYPE, productType);
			startActivityForResult(intent, REQUEST_CODE_ADD_PRODUCT);
		}
	}

	final int REQUEST_CODE_ADD_PRODUCT = 1; // 添加产品
	final int REQUEST_CODE_DISTRIBUTION = 2; // 分销设置
	final int REQUEST_CODE_PRODUCT_ADDSUCCESS = 24;// 产品添加完成
	final int REQUEST_CODE_PRODUCT_EDIT = 25;// 产品添加完成

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_ADD_PRODUCT: // 添加产品
			if (resultCode == RESULT_OK && data != null) {
				boolean isAddProduct = data.getBooleanExtra(Define.DATA, false);
				if (isAddProduct) {
					handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
				}
			}
			break;

		case REQUEST_CODE_DISTRIBUTION:// 分销设置
			if (resultCode == RESULT_OK) {
				handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
			}
			break;
		case REQUEST_CODE_PRODUCT_ADDSUCCESS:// 添加产品
			if (resultCode == RESULT_OK) {
				handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
				int recommendCount = data.getIntExtra(Define.DATA, -1);
				if (recommendCount > 0) {
					final int addpid=data.getIntExtra(Define.PID, 0);
					final Dialog recommendDialog = new Dialog(ProductManageActivity.this, "推荐提示", "您确定要推荐此产品吗?\n您剩余的推荐位还有"+recommendCount+"个。");
					recommendDialog.setOnAcceptButtonClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							recommendDialog.dismiss();
							changeRecommendStatus(0,1);
						}
					});
					recommendDialog.show();
				}
			}
			break;
		case REQUEST_CODE_PRODUCT_EDIT:// 分销设置
			if (resultCode == RESULT_OK) {
				handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
