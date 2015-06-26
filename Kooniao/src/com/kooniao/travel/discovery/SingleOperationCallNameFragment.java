package com.kooniao.travel.discovery;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseFragment;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.customwidget.SwipeListView;
import com.kooniao.travel.customwidget.SwipeListView.SideSlipOptionCallback;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.manager.UserManager.RollCallListResultCallback;
import com.kooniao.travel.manager.UserManager.StringResultCallback;
import com.kooniao.travel.model.RollCall;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.DateUtil;
import com.kooniao.travel.utils.ViewUtils;

/**
 * 团单操作点名界面
 * 
 * @author ke.wei.quan
 * 
 */
@EFragment(R.layout.fragment_mass_single_operation_call_name)
public class SingleOperationCallNameFragment extends BaseFragment implements SideSlipOptionCallback {
	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout; // 下拉刷新
	@ViewById(R.id.listview)
	SwipeListView swipeListView;
	@ViewById(R.id.layout_no_data)
	LinearLayout noDataLayout; // 无数据布局

	@AfterViews
	void init() {
		initView();
		initData();
	}

	private int teamId;

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		teamId = AppSetting.getInstance().getIntPreferencesByKey(Define.TEAM_ID);
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
	}

	private RollCallAdapter adapter;

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 设置侧滑
		swipeListView.setBackViewOffSet((int) (Define.DENSITY * 80));
		swipeListView.setSideSlipOptionCallback(this);
		// 配置listview
		adapter = new RollCallAdapter();
		swipeListView.setAdapter(adapter);
		// 设置滑动监听
		swipeListView.setOnScrollListener(new ListViewScrollListener());
		swipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position = position - swipeListView.getHeaderViewsCount();
				RollCall rollCall = rollCalls.get(position);
				if (rollCall != null) {
					Intent intent = new Intent(getActivity(), RollCallDetailActivity_.class);
					intent.putExtra(Define.TEAM_ID, teamId);
					intent.putExtra(Define.ROLLCALL_ID, rollCall.getRollCallID());
					startActivityForResult(intent, REQUEST_CODE_ROLLCALL_DETAIL);
				}
			}
		});
		// 下拉刷新配置
		MaterialHeader materialHeader = new MaterialHeader(getActivity());
		materialHeader.setPadding(0, ViewUtils.dpToPx(15, getResources()), 0, ViewUtils.dpToPx(15, getResources()));

		refreshLayout.setHeaderView(materialHeader);
		refreshLayout.addPtrUIHandler(materialHeader);
		refreshLayout.setPtrHandler(new PtrHandler() {
			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
				return PtrDefaultHandler.checkContentCanBePulledDown(frame, swipeListView, header);
			}

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				onRefresh();
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
							loadRollCallList();
						} else {
							handler.sendEmptyMessageAtTime(NORE_MORE_DATA, 100);
						}
					}
				}
				break;
			}
		}
	}

	private boolean isNeedToShowTips = true; // 是否需要提示无更多数据
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
				if (isNeedToShowTips) {
					isNeedToShowTips = false;
					Toast.makeText(getActivity(), noreMoreDataTips, Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	List<RollCall> rollCalls = new ArrayList<RollCall>();
	private boolean isLoadingMore; // 是否正在加载更多

	/**
	 * 获取点名名单列表
	 */
	private void loadRollCallList() {
		if (currentPageNum > 1) {
			isLoadingMore = true;
		}

		UserManager.getInstance().loadRollCallList(currentPageNum, teamId, new RollCallListResultCallback() {

			@Override
			public void result(String errMsg, List<RollCall> rollCalls, int pageCount) {
				loadRollCallListComplete(errMsg, rollCalls, pageCount);
			}
		});
	}

	private int currentPageNum = 1; // 当前页码
	private int pageCount = 0; // 总共的页数

	/**
	 * 获取点名名单列表请求完成
	 * 
	 * @param errMsg
	 * @param rollCalls
	 * @param pageCount
	 */
	@UiThread
	void loadRollCallListComplete(String errMsg, List<RollCall> rollCalls, int pageCount) {
		swipeListView.resetMenu();
		refreshLayout.refreshComplete();
		if (currentPageNum > 1) {
			isLoadingMore = false;
		}

		if (errMsg == null && rollCalls != null) {
			this.pageCount = pageCount;
			if (currentPageNum == 1) {
				if (rollCalls.isEmpty()) {
					noDataLayout.setVisibility(View.VISIBLE);
				} else {
					this.rollCalls.clear();
					this.rollCalls = rollCalls;
				}
			} else {
				this.rollCalls.addAll(rollCalls);
			}

			adapter.notifyDataSetChanged();
		} else {
			Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	String dialogTitle = "点名名单删除"; // 对话框标题
	String dialogMessage = "确定删除该点名名单吗？"; // 对话框内容
	Dialog dialog; // 删除确认对话框

	@Override
	public void onSideSlipOptionSelected(int menuType, final int position) {
		if (menuType == 3) {
			dialog = new Dialog(getActivity(), dialogTitle, dialogMessage);
			dialog.setCancelable(false);
			dialog.setOnAcceptButtonClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					deleteRollCall(position);
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
	}

	/**
	 * 删除点名名单
	 * 
	 * @param position
	 */
	private void deleteRollCall(final int position) {
		RollCall rollCall = rollCalls.get(position);
		UserManager.getInstance().deleteRollCall(rollCall.getRollCallID(), new StringResultCallback() {

			@Override
			public void result(String errMsg) {
				if (errMsg == null) {
					rollCalls.remove(position);
					handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
				} else {
					Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 下拉刷新
	 */
	private void onRefresh() {
		isNeedToShowTips = true;
		currentPageNum = 1;
		loadRollCallList();
	}

	class RollCallAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return rollCalls.size();
		}

		@Override
		public Object getItem(int position) {
			return rollCalls.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_rollcall, null);
				holder.nameCountTextView = (TextView) convertView.findViewById(R.id.tv_call_name_count);
				holder.dateTextView = (TextView) convertView.findViewById(R.id.tv_call_name_date);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			RollCall rollCall = rollCalls.get(position);
			// 点名时间
			long rollCallTimeStamp = rollCall.getRollCallTime();
			String rollCallTime = DateUtil.timeDistanceString(rollCallTimeStamp, Define.FORMAT_YMDHM);
			holder.dateTextView.setText(rollCallTime);
			// 点名人数比例
			int havenNum = rollCall.getHeavenNum();
			int totalNum = rollCall.getTotalNum();
			String countText = havenNum + "/" + totalNum;
			holder.nameCountTextView.setText(countText);

			return convertView;
		}

	}

	static class ViewHolder {
		TextView nameCountTextView;
		TextView dateTextView;
	}

	final int REQUEST_CODE_ROLLCALL_DETAIL = 1; // 点名详情

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_ROLLCALL_DETAIL: // 点名详情
			if (resultCode == Activity.RESULT_OK) {
				handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
