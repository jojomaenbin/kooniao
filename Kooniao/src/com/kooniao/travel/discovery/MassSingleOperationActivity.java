package com.kooniao.travel.discovery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseFragmentActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.TravelManager;
import com.kooniao.travel.model.RollCallDetail;
import com.kooniao.travel.model.TeamCustomer;
import com.kooniao.travel.model.RollCallDetail.Tourist;
import com.kooniao.travel.utils.AppSetting;

/**
 * 团单操作界面
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_mass_single_operation)
public class MassSingleOperationActivity extends BaseFragmentActivity { 
	@ViewById(R.id.fl_fragment_container)
	FrameLayout fragmentContainer; 
	@ViewById(R.id.iv_add)
	ImageView addCallNameImageView; // 添加点名名单按钮
	@ViewById(R.id.tv_single_operation_contact)
	TextView contacTextView; // 联系人
	@ViewById(R.id.tv_single_operation_call_name)
	TextView callNameTextView; // 点名
	@ViewById(R.id.tv_single_operation_group_info)
	TextView groupinfoTextView; // 团信息
	
	@AfterViews
	void init() {
		initData();
		initView();
		initFragment();
	}
	
	private int travelId; // 行程id
	
	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			travelId = intent.getIntExtra(Define.PID, 0);
		}
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		
	}
	
	/**
	 * 内容页
	 */
	SingleOperationContactFragment_ contactFragment; // 联系界面
	SingleOperationCallNameFragment_ callNameFragment; // 点名界面
	SingleOperationGroupInfoFragment_ groupInfoFragment; // 团信息界面
	Fragment contentFragment; // 当前内容页
	
	/**
	 * 初始化fragment界面
	 */
	private void initFragment() {
		// 默认选中联系界面
		contactFragment = new SingleOperationContactFragment_();
		Bundle bundle = new Bundle();
		bundle.putInt(Define.PID, travelId);
		contactFragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, contactFragment).commit();
		changeItem(true, false, false); 
		contentFragment = contactFragment;

		// 初始化其他界面
		callNameFragment = new SingleOperationCallNameFragment_();
		groupInfoFragment = new SingleOperationGroupInfoFragment_();
		groupInfoFragment.setArguments(bundle);
	}
	
	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}
	
	/**
	 * 添加点名名单按钮
	 */
	@Click(R.id.iv_add)
	void onAddCallNameClick() {
		List<TeamCustomer> teamCustomers = TravelManager.getInstance().getTeamCustomerListByTravelId(travelId);
		List<Tourist> touristList = new ArrayList<RollCallDetail.Tourist>();
		for (TeamCustomer teamCustomer : teamCustomers) {
			Tourist tourist = new Tourist();
			tourist.setName(teamCustomer.getName());
			tourist.setState(0);
			tourist.setTel(teamCustomer.getTel());
			touristList.add(tourist);
		}
		Intent intent = new Intent(MassSingleOperationActivity.this, RollCallDetailActivity_.class);
		Bundle extras = new Bundle();
		int teamId = AppSetting.getInstance().getIntPreferencesByKey(Define.TEAM_ID);
		extras.putSerializable(Define.TEAM_ID, teamId);
		extras.putSerializable(Define.DATA, (Serializable) touristList);
		intent.putExtras(extras);
		startActivityForResult(intent, REQUEST_CODE_ROLLCALL_DETAIL); 
	}
	
	/**
	 * 头部联系人选择
	 */
	@Click(R.id.tv_single_operation_contact)
	void onContactClick() {
		changeItem(true, false, false); 
		switchFragment(contactFragment); 
	}
	
	/**
	 * 头部点名选择
	 */
	@Click(R.id.tv_single_operation_call_name)
	void onCallNameClick() {
		changeItem(false, true, false); 
		switchFragment(callNameFragment);
	}
	
	/**
	 * 头部信息选择
	 */
	@Click(R.id.tv_single_operation_group_info)
	void onGroupInfoClick() {
		changeItem(false, false, true); 
		switchFragment(groupInfoFragment); 
	}

	/**
	 * 切换界面
	 * 
	 * @param fragment
	 *            准备要切换的界面
	 */
	private void switchFragment(Fragment fragment) {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		if (fragment.isAdded()) {
			fragmentTransaction.hide(contentFragment).show(fragment);
		} else {
			fragmentTransaction.hide(contentFragment).add(R.id.fl_fragment_container, fragment);
		}
		contentFragment = fragment;
		fragmentTransaction.commit();
	}
	
	/**
	 * 改变选择
	 * 
	 * @param contactSelected
	 * @param callNameSelected
	 * @param messageSelected
	 */
	private void changeItem(boolean contactSelected, boolean callNameSelected, boolean groupinfoSelected) {
		if (callNameSelected) {
			addCallNameImageView.setVisibility(View.VISIBLE);
		} else {
			addCallNameImageView.setVisibility(View.INVISIBLE);
		}
		contacTextView.setSelected(contactSelected);
		callNameTextView.setSelected(callNameSelected);
		groupinfoTextView.setSelected(groupinfoSelected);
	}
	
	final int REQUEST_CODE_ROLLCALL_DETAIL = 1; // 点名详情

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_ROLLCALL_DETAIL: // 点名详情
			callNameFragment.onActivityResult(requestCode, resultCode, data); 
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
