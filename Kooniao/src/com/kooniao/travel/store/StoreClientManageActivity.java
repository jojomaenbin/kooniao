package com.kooniao.travel.store;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.kooniao.travel.BottomTabBarActivity_;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseFragmentActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.customwidget.TitleBarTab;
import com.kooniao.travel.customwidget.TitleBarTab.onTabClickListener;
import com.kooniao.travel.manager.StoreManager;
import com.kooniao.travel.manager.StoreManager.CustomerInfoListResultCallback;
import com.kooniao.travel.model.CustomerInfo;
import com.kooniao.travel.utils.CharacterParser;
import com.kooniao.travel.utils.ClientPinyinComparator;

/**
 * 店铺客户管理页面
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_store_client_manage)
public class StoreClientManageActivity extends BaseFragmentActivity {

	@ViewById(R.id.title_bar_tab)
	TitleBarTab titleBarTab; // 顶部切换按钮
	@ViewById(R.id.fl_fragment_container)
	FrameLayout fragmentContainer;

	@AfterViews
	void init() {
		initView();
		initFragment();
		loadStoreClientList();
	}

	private int currentFragmentIndex = 0; // 当前fragment索引
	KooniaoProgressDialog progressDialog;

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 设置第三个选项不显示
		titleBarTab.setTabThreeVisibility(View.GONE);
		// 设置其余两个选项的显示数据
		titleBarTab.setTabOneText(R.string.store_client);
		titleBarTab.setTabTwoText(R.string.distributor);
		titleBarTab.setOnTabClickListener(tabClickListener);

		progressDialog = new KooniaoProgressDialog(StoreClientManageActivity.this);
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
	}

	onTabClickListener tabClickListener = new onTabClickListener() {

		@Override
		public void onClick(int tabIndex) {
			currentFragmentIndex = tabIndex;
			switch (tabIndex) {
			case 0: // 切换客户页
				type = "client";
				switchFragment(clientFragment);
				break;

			case 1: // 切换分销商页
				type = "sell";
				switchFragment(distributorFragment);
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 切换fragment
	 * 
	 * @param switchFragment
	 */
	private void switchFragment(Fragment switchFragment) {
		/**
		 * 替换fragment
		 */
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		if (switchFragment.isAdded()) {
			fragmentTransaction.hide(currentContentFragment).show(switchFragment);
		} else {
			fragmentTransaction.hide(currentContentFragment).add(R.id.fl_fragment_container, switchFragment);
		}

		currentContentFragment = switchFragment;
		fragmentTransaction.commit();

		if (!isLoadedSellCustomerInfoList) {
			isLoadedSellCustomerInfoList = true;
			if (!progressDialog.isShowing()) {
				progressDialog.show();
			}
			loadStoreClientList();
		}
	}

	private Fragment currentContentFragment; // 当前页
	private ClientFragment_ clientFragment; // 客户fragment页面
	private DistributorFragment_ distributorFragment; // 分销商fragment页

	/**
	 * 初始化fragment页
	 */
	private void initFragment() {
		clientFragment = new ClientFragment_();
		distributorFragment = new DistributorFragment_();

		// 默认选中客户页
		currentContentFragment = clientFragment;
		getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, clientFragment).commit();
	}

	private String type = "client"; // 客户类型，默认客户(类型（客户：client、分销商：sell）)
	private boolean isLoadedSellCustomerInfoList = false; // 是否已经加载过分销商列表数据

	/**
	 * 加载店铺客户列表数据
	 */
	private void loadStoreClientList() {
		StoreManager.getInstance().loadClientList(type, new CustomerInfoListResultCallback() {

			@Override
			public void result(String errMsg, List<CustomerInfo> customerInfos) {
				loadStoreClientListComplete(errMsg, customerInfos);
			}
		});
	}

	/**
	 * 获取店铺客户列表数据完成
	 * 
	 * @param errMsg
	 * @param customerInfos
	 */
	private void loadStoreClientListComplete(String errMsg, List<CustomerInfo> customerInfos) {
		progressDialog.dismiss();

		if (errMsg == null && customerInfos != null) {
			// 设置首字母
			setLetter(customerInfos);
			// 根据a-z进行排序源数据
			Collections.sort(customerInfos, new ClientPinyinComparator());

			switch (currentFragmentIndex) {
			case 0: // 客户
				clientFragment.setDatas(customerInfos);
				break;

			case 1: // 分销商
				distributorFragment.setDatas(customerInfos);
				break;

			default:
				break;
			}
		} else {
			Toast.makeText(StoreClientManageActivity.this, errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 设置侧边栏的分割字母
	 * 
	 * @param sortModelList
	 */
	private void setLetter(List<CustomerInfo> sortModelList) {
		for (int i = 0; i < sortModelList.size(); i++) {
			CustomerInfo sortModel = sortModelList.get(i);
			// 汉字转换成拼音
			String pinyin = CharacterParser.getInstance().getSelling(sortModel.getName());
			String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINA);
			sortModel.setSortLetters(sortString.toUpperCase(Locale.CHINA));
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
		Intent storeIntent = new Intent(StoreClientManageActivity.this, BottomTabBarActivity_.class);
		storeIntent.putExtra(Define.TYPE, Define.STORE);
		startActivity(storeIntent);
	}
}
