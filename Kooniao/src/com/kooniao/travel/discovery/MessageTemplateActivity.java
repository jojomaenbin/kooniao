package com.kooniao.travel.discovery;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.customwidget.SwipeListView;
import com.kooniao.travel.customwidget.SwipeListView.SideSlipOptionCallback;
import com.kooniao.travel.manager.TravelManager;
import com.kooniao.travel.model.MessageTemplate;

/**
 * 短信模板
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_swipe_listview)
public class MessageTemplateActivity extends BaseActivity implements SideSlipOptionCallback {
	@ViewById(R.id.title)
	TextView titleTextView;
	@ViewById(R.id.listview)
	SwipeListView swipeListView;
	@ViewById(R.id.layout_no_data)
	View noDataLayout; // 无数据布局

	@AfterViews
	void init() {
		initData();
		initView();
	} 
	
	private List<MessageTemplate> messageTemplates = new ArrayList<MessageTemplate>();

	private void initData() {
		messageTemplates = TravelManager.getInstance().getMessageTemplates();
		messageTemplates = messageTemplates == null ? new ArrayList<MessageTemplate>() : messageTemplates;
	}

	MessageTemplateAdapter adapter;
	
	private void initView() {
		titleTextView.setText("短信模板"); 
		adapter = new MessageTemplateAdapter();
		swipeListView.setBackViewOffSet((int) (Define.DENSITY * 80));
		swipeListView.setSideSlipOptionCallback(this);
		swipeListView.setAdapter(adapter);
		// 设置滑动监听
		swipeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position = position - swipeListView.getHeaderViewsCount();
				MessageTemplate messageTemplate = messageTemplates.get(position);
				Intent intent = new Intent();
				intent.putExtra(Define.DATA, messageTemplate.getMessage());
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	Dialog dialog;
	
	@Override
	public void onSideSlipOptionSelected(int menuType, final int position) {
		if (menuType == 3) {
			dialog = new Dialog(MessageTemplateActivity.this, "删除短信模板", "确定删除改模板吗？");
			dialog.setOnAcceptButtonClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					messageTemplates.remove(position);
					adapter.notifyDataSetChanged();
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
	
	class MessageTemplateAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return messageTemplates.size();
		}

		@Override
		public Object getItem(int position) {
			return messageTemplates.get(position);
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
				convertView = LayoutInflater.from(MessageTemplateActivity.this).inflate(R.layout.item_message_template, null);
				holder.messageContent = (TextView) convertView.findViewById(R.id.tv_content);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			MessageTemplate messageTemplate = messageTemplates.get(position);
			holder.messageContent.setText(messageTemplate.getMessage()); 
			return convertView;
		}
	}
	
	static class ViewHolder {
		TextView messageContent; // 内容
	}
}
