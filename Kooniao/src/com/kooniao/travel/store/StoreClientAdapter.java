package com.kooniao.travel.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.kooniao.travel.R;
import com.kooniao.travel.model.CustomerInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class StoreClientAdapter extends BaseAdapter implements SectionIndexer {

	private Context context;

	public StoreClientAdapter(Context context) {
		this.context = context;
	}

	private List<CustomerInfo> customerInfos = new ArrayList<CustomerInfo>();

	public void setCustomerInfos(List<CustomerInfo> customerInfos) {
		if (customerInfos != null) {
			this.customerInfos = customerInfos;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return customerInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return customerInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_store_client_manage, null);
			viewHolder.categoryTextView = (TextView) convertView.findViewById(R.id.tv_client_selected_category);
			viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.tv_client_name);
			viewHolder.phoneCallImageView = (ImageView) convertView.findViewById(R.id.iv_client_phone_call);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final CustomerInfo customerInfo = customerInfos.get(position);

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			viewHolder.categoryTextView.setVisibility(View.VISIBLE);
			String sortLetter = customerInfo.getSortLetters();
			viewHolder.categoryTextView.setText(sortLetter);
		} else {
			viewHolder.categoryTextView.setVisibility(View.GONE);
		}
		// 名称
		String name = customerInfo.getName();
		name = name.length() > 8 ? name.substring(0, 8) + "..." : name;
		viewHolder.nameTextView.setText(name);
		if (listener != null) {
			// 点击名称
			viewHolder.nameTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CustomerInfo customerInfo = customerInfos.get(position);
					listener.onItemClick(customerInfo);
				}
			});

			// 点击拨打电话
			viewHolder.phoneCallImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CustomerInfo customerInfo = customerInfos.get(position);
					String phoneNum = customerInfo.getMobile();
					listener.onPhoneCallClick(phoneNum);
				}
			});
		}

		return convertView;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = customerInfos.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase(Locale.CHINA).charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return customerInfos.get(position).getSortLetters().charAt(0);
	}

	static class ViewHolder {
		TextView categoryTextView; // 字母
		TextView nameTextView; // 名称
		ImageView phoneCallImageView; // 拨打电话
	}

	public interface ListItemRequestListener {
		void onPhoneCallClick(String phoneNum);

		void onItemClick(CustomerInfo customerInfo);
	}

	private ListItemRequestListener listener;

	public void setOnListItemRequestListener(ListItemRequestListener listener) {
		this.listener = listener;
	}

}
