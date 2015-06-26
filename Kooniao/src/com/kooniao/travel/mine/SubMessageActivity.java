package com.kooniao.travel.mine;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import java.util.ArrayList;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.discovery.TravelDetailActivity_;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.manager.UserManager.StringResultCallback;
import com.kooniao.travel.manager.UserManager.SubMessageResultCallback;
import com.kooniao.travel.model.SubMessage;
import com.kooniao.travel.store.OrderDetailActivity.From;
import com.kooniao.travel.store.CommissionDetailActivity_;
import com.kooniao.travel.store.OrderDetailActivity_;
import com.kooniao.travel.utils.DateUtil;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.kooniao.travel.utils.ViewUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * 消息二级目录
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.data_listview)
public class SubMessageActivity extends BaseActivity {
	@ViewById(R.id.title)
	TextView titleBarTextView; // title文字
	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout; // 下拉刷新
	@ViewById(R.id.listview)
	ListView listView; // 数据列表
	@ViewById(R.id.layout_no_data)
	View noDataLayout; // 无数据布局
	@ViewById(R.id.iv_no_data)
	ImageView noDataImageView;

	/**
	 * 初始化
	 */
	@AfterViews
	void init() {
		initData();
		initView();
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
	}

	private boolean isNeedShowNoMoreTips = true; // 是否需要提示没有更多数据提示

	@StringRes(R.string.no_more_data)
	String noMoreDataTips; // 没有更多数据的提示语

	final int NORE_MORE_DATA = 0; // 没有更多数据了
	final int REFRESH_DATA = 1; // 刷新数据

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH_DATA: // 刷新数据
				refreshLayout.autoRefresh(true);
				break;

			case NORE_MORE_DATA: // 没有更多数据了
				if (isNeedShowNoMoreTips) {
					isNeedShowNoMoreTips = false;
					Toast.makeText(SubMessageActivity.this, noMoreDataTips, Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private String ptype; // 一级类型
	private String mtype; // 二级类型
	private String storeType = "a"; // 店铺类型
	private int orderMessageFrom = From.FROM_ORDER_MANAGE.from; // 订单消息来自哪(我的、A店、C店)
	private MessageAdapter adapter;
	private ImageLoader imageLoader;

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			ptype = intent.getStringExtra(Define.PTYPE);
			mtype = intent.getStringExtra(Define.MTYPE);
			if (intent.hasExtra(Define.STORE_TYPE)) {
				storeType = intent.getStringExtra(Define.STORE_TYPE);
			}
			if (intent.hasExtra(Define.FROM)) {
				orderMessageFrom = intent.getIntExtra(Define.FROM, From.FROM_ORDER_MANAGE.from);
			}
		}

		adapter = new MessageAdapter();
		listView.setAdapter(adapter);
		// 初始化图片加载器
		imageLoader = ImageLoader.getInstance();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position = position - listView.getHeaderViewsCount();
				SubMessage subMessage = submessageList.get(position);
				String messageType = subMessage.getType();
				if ("personal".equals(ptype)) {
					// 个人信息
					if ("weibo_plan".equals(messageType) || "plan_team_notify".equals(messageType)) {
						// 游客报名通知、出团通知(跳转行程详情)
						int plandId = subMessage.getParams().getPlanId();
						Intent intent = new Intent(SubMessageActivity.this, TravelDetailActivity_.class);
						intent.putExtra(Define.PID, plandId);
						startActivity(intent);
					} else if ("order".equals(mtype)) {
						// 订单消息(跳转订单详情)
						int orderId = subMessage.getParams().getOrderId();
						startOrderDetailActivity(orderId);
					}
				} else {
					if ("order".equals(mtype)) {
						// 订单消息(跳转订单详情)
						int orderId = subMessage.getParams().getOrderId();
						startOrderDetailActivity(orderId);
					} else if ("commission".equals(mtype)) {
						Intent intent = new Intent(SubMessageActivity.this, CommissionDetailActivity_.class);
						intent.putExtra(Define.STORE_TYPE, storeType);
						int otherShopId = subMessage.getParams().getOthersShopId();
						intent.putExtra(Define.SID, otherShopId);
						startActivity(intent); 
					}
				}
			}

			/**
			 * 跳转订单详情
			 * 
			 * @param orderId
			 */
			private void startOrderDetailActivity(int orderId) {
				if (orderId != 0) {
					Intent intent = new Intent(SubMessageActivity.this, OrderDetailActivity_.class);
					intent.putExtra(Define.ORDER_ID, orderId);
					intent.putExtra(Define.FROM, orderMessageFrom);
					intent.putExtra(Define.STORE_TYPE, storeType);
					startActivity(intent);
				}
			}
		});
		listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, true, true, new ListViewScrollListener()));
	}

	private boolean isLoadingMore; // 是否正在加载更多

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
						if (isHasMore) {
							currentPageNum++;
							isLoadingMore = true;
							loadMessageList();
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
		// 设置标题
		if ("notify".equals(mtype)) {
			// 提醒消息
			titleBarTextView.setText("提醒消息");
		} else if ("order".equals(mtype)) {
			// 订单消息
			titleBarTextView.setText("订单消息");
		} else if ("product".equals(mtype)) {
			// 产品消息
			titleBarTextView.setText("产品消息");
		} else if ("commission".equals(mtype)) {
			// 佣金消息
			titleBarTextView.setText("佣金消息");
		}
		// 下拉刷新配置
		MaterialHeader materialHeader = new MaterialHeader(SubMessageActivity.this);
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

		// 无数据图
		noDataImageView.setImageResource(R.drawable.no_message);
	}

	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	private void onRefresh() {
		currentPageNum = 1;
		isHasMore = true;
		isNeedShowNoMoreTips = true;
		loadMessageList();
	}

	private boolean isHasMore = true; // 是否还有更多
	private int currentPageNum = 1; // 当前页码
	private List<SubMessage> submessageList = new ArrayList<SubMessage>();

	private void loadMessageList() {
		UserManager.getInstance().loadSubMessageList(ptype, mtype, currentPageNum, new SubMessageResultCallback() {

			@Override
			public void result(String errMsg, List<SubMessage> subMessages) {
				isLoadingMore = false;
				refreshLayout.refreshComplete();
				if (errMsg == null && subMessages != null) {
					if (currentPageNum == 1) {
						if (subMessages.isEmpty()) {
							noDataLayout.setVisibility(View.VISIBLE);
						} else {
							noDataLayout.setVisibility(View.GONE);
							submessageList.clear();
							submessageList = subMessages;
						}
					} else {
						if (subMessages.isEmpty()) {
							isHasMore = false;
						} else {
							isHasMore = true;
							submessageList.addAll(subMessages);
						}
					}
					adapter.notifyDataSetChanged();
				} else {
					Toast.makeText(SubMessageActivity.this, errMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	class MessageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return submessageList.size();
		}

		@Override
		public Object getItem(int position) {
			return submessageList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(SubMessageActivity.this).inflate(R.layout.item_message, null);
				holder.iconImageView = (ImageView) convertView.findViewById(R.id.iv_message_icon);
				holder.typeTextView = (TextView) convertView.findViewById(R.id.tv_message_type);
				holder.timeTextView = (TextView) convertView.findViewById(R.id.tv_message_time);
				holder.contentTextView = (TextView) convertView.findViewById(R.id.tv_message_content);
				holder.operationLayout = convertView.findViewById(R.id.ll_message_operate);
				holder.receiveTextView = (TextView) convertView.findViewById(R.id.tv_infomation_receive);
				holder.refuseTextView = (TextView) convertView.findViewById(R.id.tv_infomation_refuse);
				holder.operationStatusLayout = convertView.findViewById(R.id.ll_message_operate_status);
				holder.operateStatusTextView = (TextView) convertView.findViewById(R.id.tv_infomation_operate_status);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			convertView.setPressed(false);
			final SubMessage subMessage = submessageList.get(position);
			// 设置icon
			if ("notify".equals(mtype)) { // 提醒消息
				holder.iconImageView.setVisibility(View.GONE);
				// 标题
				String title = subMessage.getTitle();
				title = "".equals(title) ? titleBarTextView.getText().toString() : title;
				holder.typeTextView.setText(title);
			} else { // 订单消息
				holder.iconImageView.setVisibility(View.VISIBLE);
				ImageLoaderUtil.loadAvatar(imageLoader, subMessage.getLogo(), holder.iconImageView);
				// 标题
				String name = subMessage.getName();
				name = "".equals(name) ? titleBarTextView.getText().toString() : name;
				holder.typeTextView.setText(name);
			}
			// 名字
			long timeStamp = subMessage.getCtime();
			if (timeStamp == 0) {
				holder.timeTextView.setVisibility(View.INVISIBLE);
			} else {
				String time = DateUtil.timeDistanceString(timeStamp);
				holder.timeTextView.setText(time);
			}
			// 消息内容
			holder.contentTextView.setText(subMessage.getContent());
			// 个人消息的导游指派
			if ("personal".equals(ptype) && "plan_trip_create".equals(subMessage.getType())) {
				int status = subMessage.getParams().getOperateStatus();
				if (status == 2 || status == 3) {
					String operateStatus = status == 2 ? "已接受" : "已拒绝";
					holder.operateStatusTextView.setText(operateStatus);
					holder.operationStatusLayout.setVisibility(View.VISIBLE);
					holder.operationLayout.setVisibility(View.GONE);
				} else {
					// 接收指派导游
					holder.receiveTextView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							int notifyId = subMessage.getId();
							int teamId = subMessage.getParams().getTeamId();
							acceptGuideAppoint(position, notifyId, 1, teamId);
						}
					});
					// 拒绝
					holder.refuseTextView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							int notifyId = subMessage.getId();
							int teamId = subMessage.getParams().getTeamId();
							acceptGuideAppoint(position, notifyId, 0, teamId);
						}
					});
					holder.operationStatusLayout.setVisibility(View.GONE);
					holder.operationLayout.setVisibility(View.VISIBLE);
				}
			} else {
				holder.operationLayout.setVisibility(View.GONE);
			}
			return convertView;
		}
	}

	KooniaoProgressDialog progressDialog;

	/**
	 * 接收或拒绝指派导游
	 * 
	 * @param notifyId
	 * @param isAccept
	 * @param teamId
	 */
	private void acceptGuideAppoint(final int position, int notifyId, final int isAccept, int teamId) {
		if (progressDialog == null) {
			progressDialog = new KooniaoProgressDialog(SubMessageActivity.this);
		}
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
		UserManager.getInstance().acceptGuideAppoint(notifyId, isAccept, teamId, new StringResultCallback() {

			@Override
			public void result(String errMsg) {
				progressDialog.dismiss();
				if (errMsg == null) {
					if (isAccept == 1) {
						submessageList.get(position).getParams().setOperateStatus(2);
					} else {
						submessageList.get(position).getParams().setOperateStatus(3);
					}
					adapter.notifyDataSetChanged();
				} else {
					Toast.makeText(SubMessageActivity.this, errMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	static class ViewHolder {
		ImageView iconImageView;
		TextView typeTextView;
		TextView timeTextView;
		TextView contentTextView;
		View operationLayout; // 操作消息布局
		TextView receiveTextView; // 接收
		TextView refuseTextView; // 拒绝
		View operationStatusLayout; // 操作消息状态布局
		TextView operateStatusTextView; // 操作状态
	}
}
