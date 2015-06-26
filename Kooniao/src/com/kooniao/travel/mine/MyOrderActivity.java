package com.kooniao.travel.mine;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

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
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.OrderManager;
import com.kooniao.travel.manager.OrderManager.MyOrderListResultCallback;
import com.kooniao.travel.mine.MyOrderListAdapter.OnOrderItemClickListener;
import com.kooniao.travel.model.MyOrder;
import com.kooniao.travel.model.ParticipantInfo;
import com.kooniao.travel.store.StoreActivity_;
import com.kooniao.travel.utils.ViewUtils;

/**
 * 我的订单
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_order_check)
public class MyOrderActivity extends BaseActivity {

	@ViewById(R.id.title)
	TextView titleTextView; // 标题
	@ViewById(R.id.iv_store)
	ImageView storeImageView; // 店铺icon
	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout; // 下拉刷新布局
	@ViewById(R.id.lv_order)
	ListView listView; // 订单列表布局
	@ViewById(R.id.layout_no_data)
	LinearLayout noDataLayout; // 无数据布局

	@AfterViews
	void init() {
		initView();
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
	}

	private MyOrderListAdapter adapter;

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 设置标题
		titleTextView.setText(R.string.mine_order_form);
		// 隐藏店铺按钮
		storeImageView.setVisibility(View.INVISIBLE);
		// listview初始化
		adapter = new MyOrderListAdapter(MyOrderActivity.this);
		adapter.setOnOrderItemClickListener(onOrderItemClickListener);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new ListViewScrollListener());
		// 下拉刷新配置
		MaterialHeader materialHeader = new MaterialHeader(MyOrderActivity.this);
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
	 * 我的订单评论
	 */
	OnOrderItemClickListener onOrderItemClickListener = new OnOrderItemClickListener() {

		@Override
		public void onOrderCommentClick(int position) {
			MyOrder myOrder = myOrderList.get(position);
			Intent intent = new Intent(MyOrderActivity.this, OrderReviewActivity_.class);
			intent.putExtra(Define.PRODUCT_TYPE, myOrder.getProductType());
			intent.putExtra(Define.ORDER_ID, myOrder.getOrderId());
			startActivity(intent); 
		}

		@Override
		public void onItemClick(int position) {
			clickMyOrder = myOrderList.get(position);
			int orderId = clickMyOrder.getOrderId();
			String storeType = clickMyOrder.getShopType();
			Intent intent = new Intent(MyOrderActivity.this, UserOrderDetailActivity_.class);
			intent.putExtra(Define.ORDER_ID, orderId);
			intent.putExtra(Define.STORE_TYPE, storeType);
			intent.putExtra(Define.ORDER_CODE, "MyOrder");
			startActivityForResult(intent,REQUEST_ORDER_STATUS_CHANGE);
		}

		@Override
		public void onStoreClick(int position) {
			MyOrder myOrder = myOrderList.get(position);
			int storeId = myOrder.getShopId(); // 店铺id
			String storeType = myOrder.getShopType(); // 店铺类型
			Intent intent = new Intent(MyOrderActivity.this, StoreActivity_.class);
			intent.putExtra(Define.SID, storeId);
			intent.putExtra(Define.STORE_TYPE, storeType);
			startActivity(intent);
		}
	};

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
					Toast.makeText(MyOrderActivity.this, noreMoreDataTips, Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 下拉刷新
	 */
	public void onRefresh() {
		pageNum = 1;
		isNeedToShowNoMoreTips = true;
		loadOrderList(pageNum);
	}

	// 滑动监听
	private class ListViewScrollListener implements OnScrollListener {
		@Override
		public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			int lastItem = firstVisibleItem + visibleItemCount;
			if (lastItem == totalItemCount) {
				int childCount = listView.getChildCount();
				if (childCount > 1) {
					View lastItemView = (View) listView.getChildAt(childCount - 1);
					if ((listView.getBottom()) == lastItemView.getBottom()) {
						if (pageNum < totalPageCount) {
							pageNum++;
							loadOrderList(pageNum);
						} else {
							handler.sendEmptyMessageAtTime(NORE_MORE_DATA, 100);
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
	 * 获取订单列表
	 */
	private void loadOrderList(int pageNum) {
		OrderManager.getInstance().loadMyOrderList(pageNum, new MyOrderListResultCallback() {

			@Override
			public void result(String errMsg, List<MyOrder> myOrders, int pageCount) {
				loadOrderListComplete(errMsg, myOrders, pageCount);
			}
		});
	}

	private int pageNum = 1; // 当前页码
	private int totalPageCount; // 数据总页码
	private List<MyOrder> myOrderList; // 列表

	/**
	 * 获取订单列表请求完成
	 * 
	 * @param errMsg
	 * @param myOrders
	 * @param pageCount
	 */
	void loadOrderListComplete(String errMsg, List<MyOrder> myOrders, int pageCount) {
		totalPageCount = pageCount;
		refreshLayout.refreshComplete();
		if (errMsg == null) {
			if (myOrders != null) {
				if (myOrders.isEmpty()) {
					noDataLayout.setVisibility(View.VISIBLE);
				} else {
					if (pageNum == 1) {
						myOrderList = myOrders;
					} else {
						myOrderList.addAll(myOrders);
					}
					adapter.setOrderList(myOrderList);
				}
			}
		} else {
			Toast.makeText(MyOrderActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}
	
	private MyOrder clickMyOrder;
	
	final int REQUEST_ORDER_STATUS_CHANGE = 102; // 订单状态改变

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ORDER_STATUS_CHANGE: // 订单状态改变
			if (resultCode == RESULT_OK) {
				clickMyOrder.setOrderStatus(data.getIntExtra(Define.ORDER_STATUS, 0));
				adapter.notifyDataSetChanged();
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
