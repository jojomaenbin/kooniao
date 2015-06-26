package com.kooniao.travel.mine;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.UserManager;
import com.kooniao.travel.manager.UserManager.MessageResultCallback;
import com.kooniao.travel.store.OrderDetailActivity.From;
import com.kooniao.travel.utils.DateUtil;
import com.kooniao.travel.utils.ViewUtils;

/**
 * 消息界面
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.data_listview)
public class MessageActivity extends BaseActivity {
	@ViewById(R.id.title)
	TextView titleBarTextView; // title文字
	@ViewById(R.id.swipe_refresh_layout)
	PtrFrameLayout refreshLayout; // 下拉刷新
	@ViewById(R.id.listview)
	ListView listView; // 数据列表

	/**
	 * 初始化
	 */
	@AfterViews
	void init() {
		initData();
		initView();
		handler.sendEmptyMessageDelayed(REFRESH_DATA, 100);
	}

	final int REFRESH_DATA = 1; // 刷新数据

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REFRESH_DATA: // 刷新数据
				refreshLayout.autoRefresh(true);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private String type; // 类型
	private String storeType = "a"; // 类型
	private int orderMessageFrom = From.FROM_ORDER_MANAGE.from; // 订单消息来自哪(我的、A店、C店)
	private MessageAdapter adapter;

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			type = intent.getStringExtra(Define.TYPE);
			if (intent.hasExtra(Define.STORE_TYPE)) {
				storeType = intent.getStringExtra(Define.STORE_TYPE);
			}
			if (intent.hasExtra(Define.FROM)) {
				orderMessageFrom = intent.getIntExtra(Define.FROM, From.FROM_ORDER_MANAGE.from);
			}
		}

		adapter = new MessageAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position = position - listView.getHeaderViewsCount();
				com.kooniao.travel.model.Message message = messageList.get(position);
				// 二级分类
				String mtype = message.getType();
				Intent intent = new Intent(MessageActivity.this, SubMessageActivity_.class);
				intent.putExtra(Define.PTYPE, type);
				intent.putExtra(Define.MTYPE, mtype);
				intent.putExtra(Define.STORE_TYPE, storeType);
				intent.putExtra(Define.FROM, orderMessageFrom);
				startActivity(intent);
			}
		});
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 设置标题
		titleBarTextView.setText(R.string.message);
		// 下拉刷新配置
		MaterialHeader materialHeader = new MaterialHeader(MessageActivity.this);
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

	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	private void onRefresh() {
		loadMessageList();
	}

	private List<com.kooniao.travel.model.Message> messageList = new ArrayList<com.kooniao.travel.model.Message>();

	private void loadMessageList() {
		UserManager.getInstance().loadMessageList(type, new MessageResultCallback() {

			@Override
			public void result(String errMsg, List<com.kooniao.travel.model.Message> messages) {
				refreshLayout.refreshComplete();
				if (errMsg == null && messages != null) {
					messageList = messages;
					adapter.notifyDataSetChanged();
				} else {
					Toast.makeText(MessageActivity.this, errMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	class MessageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return messageList.size();
		}

		@Override
		public Object getItem(int position) {
			return messageList.get(position);
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
				convertView = LayoutInflater.from(MessageActivity.this).inflate(R.layout.item_message, null);
				holder.iconImageView = (ImageView) convertView.findViewById(R.id.iv_message_icon);
				holder.typeTextView = (TextView) convertView.findViewById(R.id.tv_message_type);
				holder.timeTextView = (TextView) convertView.findViewById(R.id.tv_message_time);
				holder.contentTextView = (TextView) convertView.findViewById(R.id.tv_message_content);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			com.kooniao.travel.model.Message message = messageList.get(position);
			// 设置icon
			if ("notify".equals(message.getType())) { // 提醒消息
				holder.iconImageView.setImageResource(R.drawable.remind_message_icon);
			} else if ("order".equals(message.getType())) { // 订单消息
				holder.iconImageView.setImageResource(R.drawable.order_message_icon);
			} else if ("product".equals(message.getType())) { // 产品消息
				holder.iconImageView.setImageResource(R.drawable.product_message_icon);
			} else if ("commission".equals(message.getType())) { // 佣金消息
				holder.iconImageView.setImageResource(R.drawable.commission_message_icon);
			}
			// 标题
			holder.typeTextView.setText(message.getTitle());
			// 名字
			long timeStamp = message.getCtime();
			if (timeStamp == 0) {
				holder.timeTextView.setVisibility(View.INVISIBLE);
			} else {
				String time = DateUtil.timeDistanceString(timeStamp);
				holder.timeTextView.setText(time);
			}
			// 消息内容
			holder.contentTextView.setText(message.getContent());

			return convertView;
		}
	}

	static class ViewHolder {
		ImageView iconImageView;
		TextView typeTextView;
		TextView timeTextView;
		TextView contentTextView;
	}

}
