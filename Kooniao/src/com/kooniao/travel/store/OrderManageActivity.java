package com.kooniao.travel.store;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.api.ApiCaller.APIStringResultCallback;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.OrderManager;
import com.kooniao.travel.manager.OrderManager.OrderListResultCallback;
import com.kooniao.travel.manager.StoreManager;
import com.kooniao.travel.model.Order;
import com.kooniao.travel.store.OrderDetailActivity.From;
import com.kooniao.travel.store.OrderListAdapter.OnOrderItemClickListener;
import com.kooniao.travel.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * 订单查看、管理页
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_order_check)
public class OrderManageActivity extends BaseActivity {

	@ViewById(R.id.title)
	TextView titleTextView; // 标题
	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout; // 下拉刷新布局
	@ViewById(R.id.lv_order)
	ListView listView; // 订单列表布局
	@ViewById(R.id.layout_no_data)
	LinearLayout noDataLayout; // 无数据布局

	@AfterViews
	void init() {
		initData();
		initView();
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
	}

	private String storeType; // 店铺类型

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
	}

	private OrderListAdapter adapter;
	private PopupWindow statusPopupWindow;

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 设置标题
		if ("a".equals(storeType)) {
			titleTextView.setText(R.string.order_manage);
		} else if ("c".equals(storeType)) {
			titleTextView.setText(R.string.order_check);
		}
		// listview初始化
		adapter = new OrderListAdapter(OrderManageActivity.this, storeType);
		adapter.setOnOrderItemClickListener(onOrderItemClickListener);
		listView.setAdapter(adapter); 
		listView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, new ListViewScrollListener()));
		// 下拉刷新配置
		MaterialHeader materialHeader = new MaterialHeader(OrderManageActivity.this);
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
		// 初始化popupwindow
		initPopupWindow();
	}

	private void initPopupWindow() {
		View orderStatusSelectLayout = LayoutInflater.from(OrderManageActivity.this).inflate(R.layout.popupwindow_update_order_status, null);
		statusPopupWindow = new PopupWindow(orderStatusSelectLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		statusPopupWindow.setFocusable(false);
		statusPopupWindow.setAnimationStyle(R.style.PopupAnimationFromBottom);
		// 关闭按钮
		orderStatusSelectLayout.findViewById(R.id.iv_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				statusPopupWindow.dismiss();
			}
		});
		// 处理中
		orderStatusSelectLayout.findViewById(R.id.tv_order_status_dealing).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateOrderStatus(0);
			}
		});
		// 已取消
		orderStatusSelectLayout.findViewById(R.id.tv_order_status_canceled).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateOrderStatus(-1);
			}
		});
		// 已确认
		orderStatusSelectLayout.findViewById(R.id.tv_order_status_confirm).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateOrderStatus(1);
			}
		});
		// 部分收款
		orderStatusSelectLayout.findViewById(R.id.tv_order_status_part_collection).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateOrderStatus(5);
			}
		});
		// 已关闭
		orderStatusSelectLayout.findViewById(R.id.tv_order_status_closed).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateOrderStatus(-2);
			}
		});
		// 已收款
		orderStatusSelectLayout.findViewById(R.id.tv_order_status_money_receipt).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateOrderStatus(2);
			}
		});
		// 已退订
		orderStatusSelectLayout.findViewById(R.id.tv_order_status_unsubcribed).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateOrderStatus(3);
			}
		});
		// 已出团
		orderStatusSelectLayout.findViewById(R.id.tv_order_status_have_a_ball).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateOrderStatus(4);
			}
		});
	}

	@StringRes(R.string.update_order_status_success)
	String updateOrderStatusSuccessTips; // 更改订单状态成功

	/**
	 * 更改订单状态 订单状态。0：未处理；1:已确认；-1：取消；-2：关闭；2：已收款；3：已退订；4：已出团；5：部分收款
	 * 
	 * @param status
	 */
	private void updateOrderStatus(final int status) {
		statusPopupWindow.dismiss();

		if (changeStatusOrderPosition != -1) {
			int id = orderList.get(changeStatusOrderPosition).getOrderId();
			StoreManager.getInstance().updateOrderStatus(id, status, new APIStringResultCallback() {

				@Override
				public void result(String errMsg) {
					if (errMsg == null) {
						orderList.get(changeStatusOrderPosition).setOrderStatus(status);
						adapter.setOrderList(orderList);
						Toast.makeText(OrderManageActivity.this, updateOrderStatusSuccessTips, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(OrderManageActivity.this, errMsg, Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}

	private int changeStatusOrderPosition = -1; // 将要更改状态的订单的位置
	private int itemClickPosition = -1; // 点击的item位置

	/**
	 * 订单条目点击
	 */
	OnOrderItemClickListener onOrderItemClickListener = new OnOrderItemClickListener() {

		@Override
		public void onOrderStatusClick(int position) {
			statusPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
			changeStatusOrderPosition = position;
		}

		@Override
		public void onItemClick(int position) {
			itemClickPosition = position;
			Order order = orderList.get(position);
			int orderId = order.getOrderId();
			Intent intent = new Intent(OrderManageActivity.this, OrderDetailActivity_.class);
			intent.putExtra(Define.ORDER_ID, orderId);
			intent.putExtra(Define.FROM, From.FROM_ORDER_MANAGE.from);
			intent.putExtra(Define.STORE_TYPE, storeType);
			startActivityForResult(intent, REQUEST_CODE_ORDER_DETAIL);
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
					Toast.makeText(OrderManageActivity.this, noreMoreDataTips, Toast.LENGTH_SHORT).show();
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
	 * 店铺icon按钮
	 */
	@Click(R.id.iv_store)
	void onTitleBarStoreClick() {
		finish();
		Intent storeIntent = new Intent(OrderManageActivity.this, BottomTabBarActivity_.class);
		storeIntent.putExtra(Define.TYPE, Define.STORE);
		startActivity(storeIntent);
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
		OrderManager.getInstance().loadOrderList(storeType, pageNum, new OrderListResultCallback() {

			@Override
			public void result(String errMsg, List<Order> orders, int pageCount) {
				loadOrderListComplete(errMsg, orders, pageCount);
			}
		});
	}

	private int pageNum = 1; // 当前页码
	private int totalPageCount; // 数据总页码
	private List<Order> orderList; // 列表

	/**
	 * 获取订单列表请求完成
	 * 
	 * @param errMsg
	 * @param orders
	 * @param pageCount
	 */
	void loadOrderListComplete(String errMsg, List<Order> orders, int pageCount) {
		refreshLayout.refreshComplete();
		totalPageCount = pageCount;
		if (errMsg == null) {
			if (orders != null) {
				if (orders.isEmpty()) {
					noDataLayout.setVisibility(View.VISIBLE);
				} else {
					if (pageNum == 1) {
						orderList = orders;
					} else {
						orderList.addAll(orders);
					}
					adapter.setOrderList(orderList);
				}
			}
		} else {
			Toast.makeText(OrderManageActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	final int REQUEST_CODE_ORDER_DETAIL = 1; // 订单详情

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case REQUEST_CODE_ORDER_DETAIL: // 订单详情
			if (resultCode == RESULT_OK && data != null) {
				int orderStatus = data.getIntExtra(Define.ORDER_STATUS, -1);
				if (orderStatus != -1) {
					orderList.get(itemClickPosition).setOrderStatus(orderStatus);
					adapter.notifyDataSetChanged();
				}
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
