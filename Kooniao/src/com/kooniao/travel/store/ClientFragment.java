package com.kooniao.travel.store;

import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseFragment;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.customwidget.SideBar;
import com.kooniao.travel.customwidget.SideBar.OnTouchingLetterChangedListener;
import com.kooniao.travel.model.CustomerInfo;
import com.kooniao.travel.store.StoreClientAdapter.ListItemRequestListener;

/**
 * 客户fragment页
 * 
 * @author ke.wei.quan
 * 
 */
@EFragment(R.layout.fragment_store_client_manage)
public class ClientFragment extends BaseFragment {
	@ViewById(R.id.listview)
	ListView listView;
	@ViewById(R.id.tv_client_selected_tips)
	TextView tipsTextView; // 选择城市提示
	@ViewById(R.id.sb_right)
	SideBar sideBar; // 侧边栏
	@ViewById(R.id.layout_no_data)
	View noDataLayout;

	/**
	 * 设置界面数据
	 * 
	 * @param customerInfos
	 */
	protected void setDatas(List<CustomerInfo> customerInfos) {
		if (customerInfos != null) {
			if (customerInfos.isEmpty()) {
				noDataLayout.setVisibility(View.VISIBLE);
			} else {
				adapter.setCustomerInfos(customerInfos);
			}
		}
	}

	@AfterViews
	void init() {
		initData();
		initView();
	}

	private StoreClientAdapter adapter;

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		adapter = new StoreClientAdapter(getActivity());
		adapter.setOnListItemRequestListener(listener);
	}

	@StringRes(R.string.call)
	String dialogTitle; // 对话框标题
	Dialog dialog; // 拨打电话确认对话框

	ListItemRequestListener listener = new ListItemRequestListener() {

		@Override
		public void onPhoneCallClick(final String phoneNum) {
			dialog = new Dialog(getActivity(), dialogTitle, phoneNum);
			dialog.setCancelable(false);
			dialog.setOnAcceptButtonClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri
							.parse("tel:" + phoneNum));
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

		@Override
		public void onItemClick(CustomerInfo customerInfo) {
			Intent intent = new Intent(getActivity(),
					StoreClientDetailActivity_.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(Define.CLIENT, customerInfo);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};

	/**
	 * 初始化界面
	 */
	private void initView() {
		sideBar.setTextView(tipsTextView);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					listView.setSelection(position);
				}
			}
		});
		listView.setAdapter(adapter);
	}

}
