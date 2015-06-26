package com.kooniao.travel.home;

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
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.home.SearchProductListAdapter.ListItemRequestListener;
import com.kooniao.travel.manager.ProductManager;
import com.kooniao.travel.manager.ProductManager.ProductListResultCallback;
import com.kooniao.travel.model.Product;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

@EActivity(R.layout.activity_search)
public class SearchActivity extends BaseActivity {

	private boolean isNeedToShowNoMoreTips = true; // 是否需要提示没有更多数据了
	@StringRes(R.string.no_more_data)
	String noMoreDataTips; // 没有更多数据的提示语

	final int NORE_MORE_DATA = 0; // 没有更多数据了

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NORE_MORE_DATA: // 没有更多数据了
				if (isNeedToShowNoMoreTips) {
					isNeedToShowNoMoreTips = false;
					Toast.makeText(SearchActivity.this, noMoreDataTips, Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@ViewById(R.id.iv_search_icon)
	ImageView searchIconImageView; // 搜索图标
	@ViewById(R.id.et_search)
	EditText inputEditText; // 输入框
	@ViewById(R.id.layout_no_data)
	LinearLayout noDataLayout; // 无数据布局
	@ViewById(R.id.lv_product)
	ListView listView; // 列表数据布局

	@AfterViews
	void init() {
		initData();
		initView();
	}

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

	private int cid; // 城市id
	private SearchProductListAdapter adapter;
	private List<Product> products = new ArrayList<Product>(); // 搜索产品列表
	private ImageLoader imageLoader;

	/**
	 * 初始化数据
	 */
	private void initData() {
		cid = AppSetting.getInstance().getIntPreferencesByKey(Define.CID_HOME_PAGE);
		cid = cid == 0 ? 3544 : cid; // 默认广州

		// 初始化图片加载器
		imageLoader = ImageLoader.getInstance();

		adapter = new SearchProductListAdapter(SearchActivity.this);
		adapter.setProductList(products);
		adapter.setOnListItemRequestListener(listItemRequestListener);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true, new ListViewScrollListener()));
	}

	ListItemRequestListener listItemRequestListener = new ListItemRequestListener() {

		@Override
		public void onStoreClick(int position) {
			onListItemClick(position);
		}

		@Override
		public void onLoadCoverImgListener(String imgUrl, ImageView coverImageView) {
			ImageLoaderUtil.loadListCoverImg(imageLoader, imgUrl, coverImageView);
		}

		@Override
		public void onItemClick(int position) {
			onListItemClick(position);
		}
	};

	KooniaoProgressDialog progressDialog;

	/**
	 * 初始化界面
	 */
	private void initView() {
		progressDialog = new KooniaoProgressDialog(SearchActivity.this);
		inputEditText.setHint(R.string.product_search_hint);
		// 输入框焦点监听
		inputEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

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

	/**
	 * list条目点击 类型： 全部：0 ，线路：4 ，组合：2 ，酒店：5 ，美食：8 ，娱乐：7
	 */
	private void onListItemClick(int position) {
		Product product = products.get(position);
		int productType = product.getType();
		Intent productDetailIntent = null;
		if (productType == 2) {
			// 组合产品详情
			productDetailIntent = new Intent(SearchActivity.this, CombineProductDetailActivity_.class);
		} else {
			if (productType == 4) {
				// 线路产品详情
				productDetailIntent = new Intent(SearchActivity.this, LineProductDetailActivity_.class);
			} else {
				// 非线路产品详情
				productDetailIntent = new Intent(SearchActivity.this, NonLineProductDetailActivity_.class);
			}
		}

		if (productDetailIntent != null) {
			productDetailIntent.putExtra(Define.PID, product.getProductId());
			productDetailIntent.putExtra(Define.TYPE, productType);
			startActivity(productDetailIntent);
		}
	}

	@TextChange(R.id.et_search)
	void onSearchTextChanges(CharSequence text, TextView textView, int before, int start, int count) {
		text = text.toString().trim();
		if (text.length() > 0) {
			noDataLayout.setVisibility(View.GONE);
			searchTipLayout.setVisibility(View.VISIBLE);

			if (!text.toString().equals("")) {
				lineTipsTextView.setText(text);
				hotelTipsTextView.setText(text);
				locationTicketTipsTextView.setText(text);
				amusementTipsTextView.setText(text);
				foodTipsTextView.setText(text);
				shoppingTipsTextView.setText(text);
				groupProductTipsTextView.setText(text);
			}
		} else if (text.length() <= 0) {
			searchTipLayout.setVisibility(View.GONE);
			listView.setVisibility(View.GONE);
		}

		/**
		 * 清空列表数据
		 */
		products.clear();
		adapter.notifyDataSetChanged();
	}

	private int currentPageNum = 1; // 当前页码
	private int type = 0; // 当前搜索类型
	private String keyWord = null; // 当前关键字

	/**
	 * 点击搜索线路产品
	 */
	@Click(R.id.lr_quick_search_line)
	void onLineItemClick() {
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}

		currentPageNum = 1;
		type = 4;
		closeInputMethod();
		searchTipLayout.setVisibility(View.GONE);

		loadProductList(currentPageNum);
	}

	/**
	 * 点击搜索酒店产品
	 */
	@Click(R.id.lr_quick_search_hotel)
	void onHotelItemClick() {
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}

		currentPageNum = 1;
		type = 5;
		closeInputMethod();
		searchTipLayout.setVisibility(View.GONE);

		loadProductList(currentPageNum);
	}

	/**
	 * 点击搜索景点产品
	 */
	@Click(R.id.lr_quick_search_location_ticket)
	void onLocationTicketItemClick() {
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}

		currentPageNum = 1;
		type = 6;
		closeInputMethod();
		searchTipLayout.setVisibility(View.GONE);

		loadProductList(currentPageNum);
	}

	/**
	 * 点击搜索休闲娱乐产品
	 */
	@Click(R.id.lr_quick_search_amusement)
	void onAmusementItemClick() {
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}

		currentPageNum = 1;
		type = 7;
		closeInputMethod();
		searchTipLayout.setVisibility(View.GONE);

		loadProductList(currentPageNum);
	}

	/**
	 * 点击搜索餐饮美食产品
	 */
	@Click(R.id.lr_quick_search_food)
	void onFoodItemClick() {
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}

		currentPageNum = 1;
		type = 8;
		closeInputMethod();
		searchTipLayout.setVisibility(View.GONE);

		loadProductList(currentPageNum);
	}

	/**
	 * 点击搜索购物优惠产品
	 */
	@Click(R.id.lr_quick_search_shopping)
	void onShoppingItemClick() {
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}

		currentPageNum = 1;
		type = 9;
		closeInputMethod();
		searchTipLayout.setVisibility(View.GONE);

		loadProductList(currentPageNum);
	}

	/**
	 * 点击搜索组合产品
	 */
	@Click(R.id.lr_quick_search_group_product)
	void onGroupProductItemClick() {
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}

		currentPageNum = 1;
		type = 2;
		closeInputMethod();
		searchTipLayout.setVisibility(View.GONE);

		loadProductList(currentPageNum);
	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 搜索按钮
	 */
	@Click(R.id.tv_search)
	void onSearchClick() {
		keyWord = inputEditText.getText().toString().trim();
		if (keyWord.length() > 0) {
			if (!progressDialog.isShowing()) {
				progressDialog.show();
			}

			currentPageNum = 1;
			type = 0;
			closeInputMethod();
			searchTipLayout.setVisibility(View.GONE);

			loadProductList(currentPageNum);
		} else {
			Toast.makeText(SearchActivity.this, R.string.please_input_search_keywords, Toast.LENGTH_SHORT).show();
		}
	}

	private boolean isLoadingMore; // 是否正在加载更多

	/**
	 * 获取产品列表
	 */
	private void loadProductList(final int pageNum) {
		if (pageNum > 1) {
			isLoadingMore = true;
		}

		keyWord = inputEditText.getText().toString().trim();
		ProductManager.getInstance().productSearch(cid, keyWord, type, currentPageNum, new ProductListResultCallback() {

			@Override
			public void result(String errMsg, List<Product> products, int pageCount) {
				isLoadingMore = false;
				searchProductComplete(errMsg, products, pageCount);
			}
		});
	}

	/**
	 * 关闭输入法
	 */
	private void closeInputMethod() {
		// 关闭输入法
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	}

	private int pageCount = 0; // 总共有多少页的数据

	/**
	 * 搜索产品列表完成
	 * 
	 * @param errMsg
	 * @param products
	 * @param pageCount
	 */
	@UiThread
	void searchProductComplete(String errMsg, List<Product> products, int pageCount) {
		progressDialog.dismiss();
		isNeedToShowNoMoreTips = true;
		this.pageCount = pageCount;
		noDataLayout.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);

		if (errMsg == null && products != null) {
			if (currentPageNum == 1 && products.isEmpty()) {
				noDataLayout.setVisibility(View.VISIBLE);
			} else {
				if (currentPageNum == 1) {
					this.products.clear();
					this.products = products;
				} else {
					this.products.addAll(products);
				}
				adapter.setProductList(this.products);
			}

		} else {
			Toast.makeText(SearchActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
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
							if (keyWord != null) {
								isLoadingMore = true;
								loadProductList(currentPageNum);
							}
						} else {
							handler.sendEmptyMessageDelayed(NORE_MORE_DATA, 100);
						}
					}
				}
				break;
			}
		}
	}

}
