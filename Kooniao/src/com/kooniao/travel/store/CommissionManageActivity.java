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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.manager.StoreManager;
import com.kooniao.travel.manager.StoreManager.CommissionListResultCallback;
import com.kooniao.travel.model.Commission;
import com.kooniao.travel.store.CommissionManagerAdapter.ItemRequestListener;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * 佣金管理
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_commission_manage)
public class CommissionManageActivity extends BaseActivity {
	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout; // 刷新布局
	@ViewById(R.id.listview)
	ListView listView; // 数据布局
	@ViewById(R.id.layout_no_data)
	View noDataLayout; // 无数据布局

	@AfterViews
	void init() {
		initData();
		initView();
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
	}

	private String storeType = "a"; // 当前店铺类型
	private CommissionManagerAdapter adapter;
	private List<Commission> commissions = new ArrayList<Commission>();
	private ImageLoader imageLoader;
	private boolean isLoadingMore; // 是否正在加载更多
	private int currentPageNum = 1; // 当前页码
	private int pageCount = 0; // 总共的页数

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			if (intent.hasExtra(Define.STORE_TYPE)) {
				storeType = intent.getStringExtra(Define.STORE_TYPE);
			}
		}

		adapter = new CommissionManagerAdapter(CommissionManageActivity.this, storeType);
		adapter.setCommissions(commissions);
		adapter.setOnItemRequestListener(itemRequestListener);

		// 初始化图片加载器
		imageLoader = ImageLoader.getInstance();
	}

	private int paymentItemIndex = -1; // 点击支付佣金item索引位置

	@StringRes(R.string.call)
	String dialogTitle; // 对话框标题
	Dialog dialog; // 拨打电话确认对话框

	ItemRequestListener itemRequestListener = new ItemRequestListener() {

		@Override
		public void onLoadLogoListener(String logoUrl, ImageView logoImageView) {
			ImageLoaderUtil.loadAvatar(imageLoader, logoUrl, logoImageView);
		}

		// 进入佣金详细页
		@Override
		public void onItemClickListener(int position) {
			paymentItemIndex = position;
			Commission commission = commissions.get(position);
			Intent intent = new Intent(CommissionManageActivity.this, CommissionDetailActivity_.class);
			intent.putExtra(Define.STORE_TYPE, storeType);
			intent.putExtra(Define.SID, commission.getShopId());
			startActivityForResult(intent, REQUEST_CODE_PAY);
		}

		@Override
		public void onContactPhoneClickListener(final String phoneNum) {
			if (!"".equals(phoneNum)) {
				dialog = new Dialog(CommissionManageActivity.this, dialogTitle, phoneNum);
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
				Toast.makeText(CommissionManageActivity.this, R.string.customer_service_null, Toast.LENGTH_SHORT).show();
			}
		}

		// 进入佣金支付页
		@Override
		public void onPayCommissionClick(int position) {
			paymentItemIndex = position;
			Commission commission = commissions.get(position);
			Intent intent = new Intent(CommissionManageActivity.this, PayCommissionActivity_.class);
			intent.putExtra(Define.UNDEFRAYMONEY, commission.getUnDefrayMoney());
			intent.putExtra(Define.SID, commission.getShopId());
			intent.putExtra(Define.STORE_TYPE, storeType);
			intent.putExtra(Define.STORE_LOGO, commission.getShopLogo());
			intent.putExtra(Define.SHOP_NAME, commission.getShopName());
			intent.putExtra(Define.CONTACT_PHONE, commission.getMobile());
			startActivityForResult(intent, REQUEST_CODE_PAY);
		}
	};

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
							loadCommissionList();
						} else {
							handler.sendEmptyMessageDelayed(NORE_MORE_DATA, 100);
						}
					}
				}
				break;
			}
		}
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 设置下拉刷新
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true, new ListViewScrollListener()));
		// 下拉刷新配置
		MaterialHeader materialHeader = new MaterialHeader(CommissionManageActivity.this);
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
	 * 获取佣金列表
	 */
	private void loadCommissionList() {
		if (currentPageNum > 1) {
			isLoadingMore = true;
		}

		StoreManager.getInstance().loadCommissionList(storeType, currentPageNum, new CommissionListResultCallback() {

			@Override
			public void result(String errMsg, List<Commission> commissions, int pageCount) {
				isLoadingMore = false;
				loadCommissionListComplete(errMsg, commissions, pageCount);
			}
		});
	}

	/**
	 * 获取佣金列表请求完成
	 * 
	 * @param errMsg
	 * @param commissions
	 * @param pageCount
	 */
	@UiThread
	void loadCommissionListComplete(String errMsg, List<Commission> commissions, int pageCount) {
		refreshLayout.refreshComplete();
		isLoadingMore = false;
		this.pageCount = pageCount;
		if (errMsg == null && commissions != null) {
			if (currentPageNum == 1) {
				if (commissions.isEmpty()) {
					noDataLayout.setVisibility(View.VISIBLE);
				} else {
					noDataLayout.setVisibility(View.GONE);
				}
				this.commissions.clear();
				this.commissions = commissions;
			} else {
				this.commissions.addAll(commissions);
			}
			adapter.setCommissions(this.commissions);
		} else {
			Toast.makeText(getBaseContext(), errMsg, Toast.LENGTH_LONG).show();
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
					Toast.makeText(CommissionManageActivity.this, noreMoreDataTips, Toast.LENGTH_SHORT).show();
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
	 * 店铺按钮
	 */
	@Click(R.id.iv_store)
	void onStoreClick() {
		finish();
		Intent storeIntent = new Intent(CommissionManageActivity.this, BottomTabBarActivity_.class);
		storeIntent.putExtra(Define.TYPE, Define.STORE);
		startActivity(storeIntent);
	}

	/**
	 * 下拉刷新
	 */
	public void onRefresh() {
		currentPageNum = 1;
		isNeedToShowNoMoreTips = true;
		loadCommissionList();
	}

	final int REQUEST_CODE_PAY = 1; // 支付佣金

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_PAY: // 支付佣金
			if (resultCode == RESULT_OK && data != null) {
				float unDefrayMoney = data.getFloatExtra(Define.UNDEFRAYMONEY, -Integer.MAX_VALUE);
				if (unDefrayMoney != -Integer.MAX_VALUE) {
					commissions.get(paymentItemIndex).setUnDefrayMoney(unDefrayMoney);
					adapter.setCommissions(commissions);
				}
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
