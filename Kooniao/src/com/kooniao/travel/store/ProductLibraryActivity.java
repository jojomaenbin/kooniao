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
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.home.CombineProductDetailActivity_;
import com.kooniao.travel.home.LineProductDetailActivity_;
import com.kooniao.travel.home.NonLineProductDetailActivity_;
import com.kooniao.travel.manager.ProductManager;
import com.kooniao.travel.manager.ProductManager.ProductListResultCallback;
import com.kooniao.travel.manager.ProductManager.StringResultCallback;
import com.kooniao.travel.model.Product;
import com.kooniao.travel.store.ProductLibraryAdapter.ListItemRequestListener;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.JsonTools;
import com.kooniao.travel.utils.StringUtil;
import com.kooniao.travel.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * 产品库
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_product_library)
public class ProductLibraryActivity extends BaseActivity {

	@ViewById(R.id.iv_search_icon)
	ImageView searchIconImageView; // 搜索图标
	@ViewById(R.id.et_search)
	EditText searchEditText; // 搜索框
	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout; // 下拉刷新布局
	@ViewById(R.id.lv_product)
	ListView listView; // 列表数据布局
	@ViewById(R.id.tv_save_count)
	TextView addedCounTextView; // 添加的产品数目

	/**
	 * 搜索相关
	 */
	@ViewById(R.id.search_topbar)
	View searchTipLayout;
	@ViewById(R.id.tv_quick_search_line)
	TextView lineTipsTextView; // 线路
	@ViewById(R.id.tv_quick_search_hotel)
	TextView hotelTipsTextView; // 酒店
	@ViewById(R.id.tv_quick_search_location_ticket)
	TextView locationTicketTipsTextView; // 景点门票
	@ViewById(R.id.tv_quick_search_amusement)
	TextView amusementTipsTextView; // 休闲娱乐
	@ViewById(R.id.tv_quick_search_food)
	TextView foodTipsTextView; // 餐饮美食
	@ViewById(R.id.tv_quick_search_shopping)
	TextView shoppingTipsTextView; // 购物优惠
	@ViewById(R.id.tv_quick_search_group_product)
	TextView groupProductTipsTextView; // 组合产品线路

	@ViewById(R.id.iv_clear)
	ImageView clearImageView; // 清除按钮
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

	private int type = 0; // 搜索类型
	private List<Product> products = new ArrayList<Product>();
	private ProductLibraryAdapter adapter;
	private ImageLoader imageLoader;
	private boolean isLoadingMore = false; // 是否正在加载更多
	private String keyWord; // 搜索关键字

	/**
	 * 初始化数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			type = intent.getIntExtra(Define.PRODUCT_TYPE, 0);
		}

		adapter = new ProductLibraryAdapter(ProductLibraryActivity.this);
		adapter.setProducts(products);
		adapter.setOnListItemRequestListener(itemRequestListener);

		// 初始化图片加载器
		imageLoader = ImageLoader.getInstance();
	}

	ListItemRequestListener itemRequestListener = new ListItemRequestListener() {

		@Override
		public void onLoadCoverImgListener(String imgUrl, ImageView coverImageView) {
			ImageLoaderUtil.loadListCoverImg(imageLoader, imgUrl, coverImageView);
		}

		@Override
		public void onItemClickListener(int position) {
			Product product = products.get(position);
			int productType = product.getType();
			Intent productDetailIntent = null;
			if (productType == 2) {
				// 组合产品详情
				productDetailIntent = new Intent(ProductLibraryActivity.this, CombineProductDetailActivity_.class);
			} else {
				if (productType == 4) {
					// 线路产品详情
					productDetailIntent = new Intent(ProductLibraryActivity.this, LineProductDetailActivity_.class);
				} else {
					// 非线路产品详情
					productDetailIntent = new Intent(ProductLibraryActivity.this, NonLineProductDetailActivity_.class);
				}
			}

			if (productDetailIntent != null) {
				productDetailIntent.putExtra(Define.PID, product.getProductId());
				productDetailIntent.putExtra(Define.TYPE, productType);
				startActivity(productDetailIntent);
			}
		}

		@Override
		public void onAddListener(List<Product> addedProducts) {
			int addedSize = addedProducts.size();
			String text = "(" + addedSize + ")";
			addedCounTextView.setText(text);
		}

		@Override
		public void onStoreClickListener(int position) {
			Product product = products.get(position);
			int sid = product.getShopId();
			Intent intent = new Intent(ProductLibraryActivity.this, StoreActivity_.class);
			intent.putExtra(Define.SID, sid);
			intent.putExtra(Define.STORE_TYPE, "a");
			startActivity(intent);
		}

		View dialogContentView;
		TextView commissionDescTextView; // 返佣说明
		TextView remarkTextView; // 备注

		// 点击备注
		@Override
		public void onRemarkClickListener(int position) {
			dialogContentView = LayoutInflater.from(ProductLibraryActivity.this).inflate(R.layout.dialog_remark_contentview, null);

			Product product = products.get(position);
			// 返佣说明
			commissionDescTextView = (TextView) dialogContentView.findViewById(R.id.tv_commission_desc);
			// 返佣类型
			int brokerageType = product.getBrokerageType();
			String commissionText = "";
			if (brokerageType == 0) {
				commissionText = StringUtil.getStringFromR(R.string.commission_desc_tips_one) + product.getBrokerage() + StringUtil.getStringFromR(R.string.commission_desc_tips_two);
			} else {
				commissionText = StringUtil.getStringFromR(R.string.commission_desc_tips) + product.getPercentage() + "%";
			}
			commissionDescTextView.setText(commissionText);
			// 备注
			remarkTextView = (TextView) dialogContentView.findViewById(R.id.tv_remark);
			String remark = product.getAffiliate_comment();
			remark = "".equals(remark) ? StringUtil.getStringFromR(R.string.none) : remark;
			remarkTextView.setText(remark);

			dialog = new Dialog(ProductLibraryActivity.this, View.VISIBLE, View.GONE, dialogContentView);
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

	KooniaoProgressDialog progressDialog;

	/**
	 * 初始化界面
	 */
	private void initView() {
		progressDialog = new KooniaoProgressDialog(ProductLibraryActivity.this);
		// 配置listview
		listView.setAdapter(adapter);
		// 设置滑动监听
		listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true, new ListViewScrollListener()));
		// 下拉刷新配置
		MaterialHeader materialHeader = new MaterialHeader(ProductLibraryActivity.this);
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
		// 输入法搜索
		searchEditText.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					type = 0;
					onSearchTipClick(type);
				}
				return false;
			}
		});

		// 焦点监听
		searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					searchIconImageView.setVisibility(View.GONE);
				} else {
					searchIconImageView.setVisibility(View.VISIBLE);
				}
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
							loadProductLibrary();
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

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH_DATA: // 刷新数据
				refreshLayout.autoRefresh(true); 
				break;

			case NORE_MORE_DATA: // 没有更多数据
				// Toast.makeText(ProductLibraryActivity.this, noreMoreDataTips,
				// Toast.LENGTH_SHORT).show();
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
	 * 加载产品列表
	 */
	private void loadProductLibrary() {
		if (currentPageNum > 1) {
			isLoadingMore = true;
		}

		ProductManager.getInstance().loadProductLibrary(type, keyWord, currentPageNum, new ProductListResultCallback() {

			@Override
			public void result(String errMsg, List<Product> products, int pageCount) {
				loadProductLibraryComplete(errMsg, products, pageCount);
			}
		});
	}

	/**
	 * 加载产品列表请求完成
	 * 
	 * @param errMsg
	 * @param products
	 * @param pageCount2
	 */
	@UiThread
	void loadProductLibraryComplete(String errMsg, List<Product> products, int pageCount) {
		isLoadingMore = false;
		progressDialog.dismiss();
		refreshLayout.refreshComplete();
		if (errMsg == null && products != null) {
			this.pageCount = pageCount;
			if (currentPageNum == 1) {
				if (products.isEmpty()) {
					noDataLayout.setVisibility(View.VISIBLE);
					listView.setVisibility(View.INVISIBLE);
				} else {
					noDataLayout.setVisibility(View.GONE);
					listView.setVisibility(View.VISIBLE);
				}

				this.products.clear();
				this.products = products;
			} else {
				this.products.addAll(products);
			}

			adapter.setProducts(this.products);
		} else {
			Toast.makeText(ProductLibraryActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * 搜索框
	 */
	@TextChange(R.id.et_search)
	void onTextChangesOnHelloTextView(CharSequence text, TextView textView, int before, int start, int count) {
		if (text.length() > 0) {
			clearImageView.setVisibility(View.VISIBLE);
			noDataLayout.setVisibility(View.GONE);
			searchTipLayout.setVisibility(View.VISIBLE);

			lineTipsTextView.setText(text);
			hotelTipsTextView.setText(text);
			locationTicketTipsTextView.setText(text);
			amusementTipsTextView.setText(text);
			foodTipsTextView.setText(text);
			shoppingTipsTextView.setText(text);
			groupProductTipsTextView.setText(text);
		} else if (text.length() <= 0) {
			clearImageView.setVisibility(View.GONE);
			searchTipLayout.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 点击搜索线路产品
	 */
	@Click(R.id.lr_quick_search_line)
	void onLineItemClick() {
		type = 4;
		onSearchTipClick(type);
	}

	/**
	 * 点击搜索酒店产品
	 */
	@Click(R.id.lr_quick_search_hotel)
	void onHotelItemClick() {
		type = 5;
		onSearchTipClick(type);
	}

	/**
	 * 点击搜索景点门票产品
	 */
	@Click(R.id.lr_quick_search_location_ticket)
	void onLocationTicketItemClick() {
		type = 6;
		onSearchTipClick(type);
	}

	/**
	 * 点击搜索休闲娱乐产品
	 */
	@Click(R.id.lr_quick_search_amusement)
	void onAmusementItemClick() {
		type = 7;
		onSearchTipClick(type);
	}

	/**
	 * 点击搜索餐饮美食产品
	 */
	@Click(R.id.lr_quick_search_food)
	void onFoodItemClick() {
		type = 8;
		onSearchTipClick(type);
	}

	/**
	 * 点击搜索购物优惠产品
	 */
	@Click(R.id.lr_quick_search_shopping)
	void onShoppingItemClick() {
		type = 9;
		onSearchTipClick(type);
	}

	/**
	 * 点击搜索组合产品
	 */
	@Click(R.id.lr_quick_search_group_product)
	void onGroupProductItemClick() {
		type = 2;
		onSearchTipClick(type);
	}

	/**
	 * 关闭输入法
	 */
	private void closeInputMethod() {
		// 关闭输入法
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	}

	/**
	 * 点击搜索快速提示进行搜索
	 * 
	 * @param type
	 */
	private void onSearchTipClick(int type) {
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}

		currentPageNum = 1;
		this.type = type;
		closeInputMethod();
		listView.setVisibility(View.VISIBLE);
		searchTipLayout.setVisibility(View.GONE);

		keyWord = searchEditText.getText().toString();
		loadProductLibrary();
	}

	/**
	 * 清除按钮（获取全部数据）
	 */
	@Click(R.id.iv_clear)
	void onClearClick() {
		type = 0;
		keyWord = null;
		searchEditText.clearFocus();
		searchEditText.setText("");
		closeInputMethod();
		listView.setVisibility(View.VISIBLE);
		searchTipLayout.setVisibility(View.GONE);
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
	}

	/**
	 * 点击后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		if (!adapter.getAddedProducts().isEmpty()) {
			showDialog();
		} else {
			activityFinish();
		}
	}

	@Override
	public void onBackPressed() {
		if (!adapter.getAddedProducts().isEmpty()) {
			showDialog();
		} else {
			activityFinish();
		}
	}

	@StringRes(R.string.save_product_title)
	String dialogTitle; // 对话框标题
	@StringRes(R.string.save_product_tips)
	String dialogMessage; // 对话框提示内容
	Dialog dialog; // 返回上一级是否保存产品对话框

	private void showDialog() {
		dialog = new Dialog(ProductLibraryActivity.this, dialogTitle, dialogMessage);
		dialog.setCancelable(false);
		dialog.setOnAcceptButtonClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				saveProduct(true);
			}
		});
		dialog.setOnCancelButtonClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				activityFinish();
			}
		});
		dialog.show();
	}

	/**
	 * 结束当前界面
	 */
	private void activityFinish() {
		Intent intent = new Intent();
		intent.putExtra(Define.DATA, isAddProduct);
		setResult(RESULT_OK, intent);
		finish();
	}

	private boolean isAddProduct = false; // 是否添加了产品

	@StringRes(R.string.add_product_success)
	String addProductSuccessTips; // 添加产品成功提示

	/**
	 * 保存按钮
	 */
	@Click(R.id.ll_save)
	void onSaveClick() {
		saveProduct(false);
	}

	/**
	 * 保存产品
	 * 
	 * @param isFinishActivity
	 *            是否结束当前页面
	 */
	private void saveProduct(final boolean isFinishActivity) {
		if (!adapter.getAddedProducts().isEmpty()) {
			List<Integer> idList = new ArrayList<Integer>();
			for (Product product : adapter.getAddedProducts()) {
				idList.add(product.getProductId());
			}
			String addedProductIdArray = JsonTools.listToJson(idList);

			// 提交到服务器
			ProductManager.getInstance().addProduct(addedProductIdArray, new StringResultCallback() {

				@Override
				public void result(String errMsg) {
					if (errMsg == null) {
						isAddProduct = true;
						adapter.clearAddedProducts();
						addedCounTextView.setText(R.string.save_count_zero);
						if (isFinishActivity) {
							activityFinish();
						} else {
							handler.sendEmptyMessageDelayed(REFRESH_DATA, 500);
						}
						Toast.makeText(getBaseContext(), addProductSuccessTips, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getBaseContext(), errMsg, Toast.LENGTH_SHORT).show();
					}
				}
			});
		} else {
			Toast.makeText(getBaseContext(), R.string.added_product_empty, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 下拉刷新
	 */
	public void onRefresh() {
		currentPageNum = 1;
		loadProductLibrary();
	}

}
