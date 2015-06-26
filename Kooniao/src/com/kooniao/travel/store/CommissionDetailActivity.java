package com.kooniao.travel.store;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.manager.StoreManager;
import com.kooniao.travel.manager.StoreManager.CommissionDetailResultCallback;
import com.kooniao.travel.model.CommissionDetail;
import com.kooniao.travel.model.CommissionDetail.CommissionRecord;
import com.kooniao.travel.model.CommissionDetail.ShopInfo;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 佣金详细页
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_commission_detail)
public class CommissionDetailActivity extends BaseActivity {

	@ViewById(R.id.listview)
	ListView listView;
	@ViewById(R.id.tv_pay_commission_residue)
	TextView commissionResidueTextView; // 待付金额
	@ViewById(R.id.rl_commission_detail_pay)
	View commissionDetailPayLayout; // 支付剩余佣金布局

	@AfterViews
	void init() {
		initData();
		initView();
		loadCommissionDetail();
	}

	private CommissionDetailAdapter adapter;
	private List<CommissionRecord> commissionRecords = new ArrayList<CommissionDetail.CommissionRecord>();
	private int currentPageNum = 1; // 当前页码
	private int pageCount; // 总共页码
	private String storeType; // 店铺类型
	private int storeId; // 收款的店铺的id

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			storeType = intent.getStringExtra(Define.STORE_TYPE);
			storeId = intent.getIntExtra(Define.SID, 0);
		}

		adapter = new CommissionDetailAdapter(CommissionDetailActivity.this);
		adapter.setCommissionRecords(commissionRecords);
	}

	KooniaoProgressDialog progressDialog;
	private View listHeaderView; // 头部信息布局
	private ImageView storeLogoImageView; // 店铺logo
	private TextView storeNameTextView; // 店铺名称
	private TextView storeContactTextView; // 店铺联系方式
	private TextView orderCountTextView; // 共销出订单数
	private TextView totalCommissionTextView; // 合计佣金
	private TextView paiedTextView; // 已结算佣金
	private TextView unpaiedTextView; // 待结算佣金

	/**
	 * 初始化界面
	 */
	private void initView() {
		progressDialog = new KooniaoProgressDialog(CommissionDetailActivity.this);
		progressDialog.show();

		// listview头部信息
		listHeaderView = LayoutInflater.from(CommissionDetailActivity.this).inflate(R.layout.sub_commission_detail_header, null);
		storeLogoImageView = (ImageView) listHeaderView.findViewById(R.id.iv_store_logo);
		storeNameTextView = (TextView) listHeaderView.findViewById(R.id.tv_store_name);
		storeContactTextView = (TextView) listHeaderView.findViewById(R.id.tv_store_contact_phone);
		orderCountTextView = (TextView) listHeaderView.findViewById(R.id.tv_order_sell_total);
		totalCommissionTextView = (TextView) listHeaderView.findViewById(R.id.tv_commission_total);
		paiedTextView = (TextView) listHeaderView.findViewById(R.id.tv_closecontract);
		unpaiedTextView = (TextView) listHeaderView.findViewById(R.id.tv_for_the_account);

		// listview相关设置
		listView.addHeaderView(listHeaderView);
		listView.setAdapter(adapter);
		listView.setOnScrollListener(new ListViewScrollListener());
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
							loadCommissionDetail();
						}
					}
				}
				break;
			}
		}
	}

	private boolean isLoadingMore; // 是否正在加载更多

	/**
	 * 获取佣金详情
	 */
	private void loadCommissionDetail() {
		StoreManager.getInstance().loadCommissionDetail(currentPageNum, storeId, storeType, new CommissionDetailResultCallback() {

			@Override
			public void result(String errMsg, CommissionDetail commissionDetail, int pageCount) {
				loadCommissionDetailComplete(errMsg, commissionDetail, pageCount);
			}
		});
	}

	private CommissionDetail commissionDetail;

	/**
	 * 获取佣金详情请求完成
	 * 
	 * @param errMsg
	 * @param commissionDetail
	 * @param pageCount
	 */
	@UiThread
	void loadCommissionDetailComplete(String errMsg, CommissionDetail commissionDetail, int pageCount) {
		progressDialog.dismiss();
		isLoadingMore = false;

		if (errMsg == null && commissionDetail != null) {
			this.pageCount = pageCount;
			this.commissionDetail = commissionDetail;
			setHeaderViewInfo();

			if (currentPageNum == 1) {
				commissionRecords.clear();
				commissionRecords = commissionDetail.getCommissionList();
			} else {
				commissionRecords.addAll(commissionDetail.getCommissionList());
			}

			adapter.setCommissionRecords(commissionRecords);

		} else {
			Toast.makeText(CommissionDetailActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	@StringRes(R.string.call)
	String dialogTitle; // 对话框标题
	Dialog dialog; // 拨打电话确认对话框

	private float unpaied; // 待结算佣金

	/**
	 * 设置头部信息
	 * 
	 * @param commissionDetail
	 */
	@UiThread
	void setHeaderViewInfo() {
		final ShopInfo shopInfo = commissionDetail.getShopInfo();
		// 加载店铺logo
		ImageLoaderUtil.loadAvatar(ImageLoader.getInstance(), shopInfo.getLogo(), storeLogoImageView);
		// 设置店铺名称
		storeNameTextView.setText(shopInfo.getName());
		// 设置店铺联系方式
		storeContactTextView.setText(shopInfo.getMobile());
		// 点击拨打电话
		storeContactTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog = new Dialog(CommissionDetailActivity.this, dialogTitle, shopInfo.getMobile());
				dialog.setCancelable(false);
				dialog.setOnAcceptButtonClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + shopInfo.getMobile()));
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
			}
		});
		// 共销出订单数
		int orderCount = commissionDetail.getCommissionInfo().getOrderCount();
		orderCountTextView.setText(String.valueOf(orderCount));
		// 合计佣金
		float totalCommission = commissionDetail.getCommissionInfo().getBrokerageCount();
		String totalCommissionString = StringUtil.getStringFromR(R.string.rmb) + totalCommission;
		totalCommissionTextView.setText(totalCommissionString);
		// 已结算
		float paied = commissionDetail.getCommissionInfo().getPaied();
		String paiedString = StringUtil.getStringFromR(R.string.rmb) + paied;
		paiedTextView.setText(paiedString);
		// 待结算
		unpaied = commissionDetail.getCommissionInfo().getUnpaied();
		String unpaiedString = StringUtil.getStringFromR(R.string.rmb) + unpaied;
		unpaiedTextView.setText(unpaiedString);
		if (unpaied == 0 || "c".equals(storeType)) {
			commissionDetailPayLayout.setVisibility(View.GONE);
		} else if (unpaied > 0) {
			// 支付佣金
			commissionResidueTextView.setText(unpaiedString);
		}
	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		activityFinish();
	}

	@Override
	public void onBackPressed() {
		activityFinish();
		super.onBackPressed();
	}

	/**
	 * 结束当前页面
	 */
	private void activityFinish() {
		Intent intent = new Intent();
		intent.putExtra(Define.UNDEFRAYMONEY, unpaied);
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * 店铺按钮
	 */
	@Click(R.id.iv_store)
	void onStoreClick() {
		finish();
		Intent storeIntent = new Intent(CommissionDetailActivity.this, BottomTabBarActivity_.class);
		storeIntent.putExtra(Define.TYPE, Define.STORE);
		startActivity(storeIntent);
	}

	@StringRes(R.string.payment_success)
	String paymentSuccessTips; // 支付成功提示

	/**
	 * 点击支付佣金
	 */
	@Click(R.id.tv_pay_commission)
	void onPayCommissionClick() {
		Intent intent = new Intent(CommissionDetailActivity.this, PayCommissionActivity_.class);
		intent.putExtra(Define.UNDEFRAYMONEY, unpaied);
		intent.putExtra(Define.SID, storeId);
		intent.putExtra(Define.STORE_TYPE, storeType);
		startActivityForResult(intent, REQUEST_CODE_PAY);
	}

	final int REQUEST_CODE_PAY = 1; // 支付佣金

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_PAY: // 支付佣金
			if (resultCode == RESULT_OK && data != null) {
				float unDefrayMoney = data.getFloatExtra(Define.UNDEFRAYMONEY, -Integer.MAX_VALUE);
				if (unDefrayMoney != -Integer.MAX_VALUE) {
					unpaied = unDefrayMoney;
				}

				activityFinish();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
