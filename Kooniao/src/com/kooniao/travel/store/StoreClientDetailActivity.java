package com.kooniao.travel.store;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.manager.UserManager.CustomerDetailResultCallback;
import com.kooniao.travel.model.CustomerDetail;
import com.kooniao.travel.model.CustomerInfo;
import com.kooniao.travel.model.CustomerDetail.Order;
import com.kooniao.travel.store.OrderDetailActivity.From;
import com.kooniao.travel.store.StoreClientOrderListAdapter.OnOrderItemClickListener;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * 客户详细页
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_store_client_detail)
public class StoreClientDetailActivity extends BaseActivity {

	@ViewById(R.id.tv_client_name)
	TextView nameTextView; // 客户名
	@ViewById(R.id.tv_client_contact_phone)
	TextView contactPhoneTextView; // 客户联系电话
	@ViewById(R.id.ll_email)
	View emailLayout;
	@ViewById(R.id.tv_client_contact_email)
	TextView contactEmailTextView; // 客户联系邮箱
	@ViewById(R.id.listview)
	ListView listView;

	@AfterViews
	void init() {
		initData();
		initView();
		loadClientDetailInfo();
	}

	private int clientId; // 客户id
	private CustomerInfo customerInfo;
	private StoreClientOrderListAdapter adapter;
	private ImageLoader imageLoader;

	/**
	 * 初始化数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			customerInfo = (CustomerInfo) intent.getSerializableExtra(Define.CLIENT);
		}
		
		if (customerInfo != null) {
			clientId = customerInfo.getId();
		}

		// 初始化图片加载器
		imageLoader = ImageLoader.getInstance();

		adapter = new StoreClientOrderListAdapter(StoreClientDetailActivity.this);
		adapter.setOrderList(orderList);
		adapter.setOnOrderItemClickListener(listener);
	}

	OnOrderItemClickListener listener = new OnOrderItemClickListener() {

		@Override
		public void onItemClick(Order order) {
			Intent intent = new Intent(StoreClientDetailActivity.this, OrderDetailActivity_.class);
			intent.putExtra(Define.FROM, From.FROM_ORDER_MANAGE.from);
			intent.putExtra(Define.ORDER_ID, order.getId());
			intent.putExtra(Define.STORE_TYPE, "a"); 
			startActivity(intent);
		}

		@Override
		public void onLoadCoverImgListener(String imgUrl, ImageView coverImageView) {
			ImageLoaderUtil.loadListCoverImg(imageLoader, imgUrl, coverImageView);
		}
	};

	KooniaoProgressDialog progressDialog;

	/**
	 * 初始化界面
	 */
	private void initView() {
		progressDialog = new KooniaoProgressDialog(StoreClientDetailActivity.this);
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}

		listView.setAdapter(adapter);
		listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true, new ListViewScrollListener()));
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
							loadClientDetailInfo();
						}
					}
				}
				break;
			}
		}
	}

	private boolean isLoadingMore; // 是否正在加载更多
	private int currentPageNum = 1; // 当前页码
	private int pageCount; // 总共页码

	/**
	 * 获取客户详细信息
	 */
	private void loadClientDetailInfo() {
		UserManager.getInstance().loadStoreClientDetail(currentPageNum, clientId, new CustomerDetailResultCallback() {

			@Override
			public void result(String errMsg, CustomerDetail customerDetail, int pageCount) {
				loadClientDetailInfoComplete(errMsg, customerDetail, pageCount);
			}
		});
	}

	private List<Order> orderList = new ArrayList<CustomerDetail.Order>();

	/**
	 * 获取客户详细信息完成
	 * 
	 * @param errMsg
	 * @param customerDetail
	 * @param pageCount
	 */
	private void loadClientDetailInfoComplete(String errMsg, CustomerDetail customerDetail, int pageCount) {
		progressDialog.dismiss();

		if (errMsg == null && customerDetail != null) {
			this.pageCount = pageCount;
			if (currentPageNum == 1 && customerDetail != null) {
				setClientInfo();
				orderList.clear();
				orderList = customerDetail.getOrderList();
			} else {
				orderList.addAll(customerDetail.getOrderList());
			}

			adapter.setOrderList(orderList);

		} else {
			Toast.makeText(StoreClientDetailActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 设置客户信息
	 * 
	 * @param customerDetail
	 */
	private void setClientInfo() {
		// 用户名
		nameTextView.setText(customerInfo.getName());
		// 电话
		contactPhoneTextView.setText(customerInfo.getMobile());
		// 邮箱
		String email = customerInfo.getEmail();
		if ("".equals(email)) {
			emailLayout.setVisibility(View.GONE);
		} else {
			contactEmailTextView.setText(email);
		}
	}

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
		Intent storeIntent = new Intent(StoreClientDetailActivity.this, BottomTabBarActivity_.class);
		storeIntent.putExtra(Define.TYPE, Define.STORE);
		startActivity(storeIntent);
	}

}
