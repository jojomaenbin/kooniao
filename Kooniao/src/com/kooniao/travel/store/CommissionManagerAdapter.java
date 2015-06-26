package com.kooniao.travel.store;

import java.util.ArrayList;
import java.util.List;

import com.kooniao.travel.R;
import com.kooniao.travel.model.Commission;
import com.kooniao.travel.utils.StringUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommissionManagerAdapter extends BaseAdapter {

	private Context context;
	private String storeType; // 店铺类型

	public CommissionManagerAdapter(Context context, String storeType) {
		this.context = context;
		this.storeType = storeType;
	}

	List<Commission> commissions = new ArrayList<Commission>();

	public void setCommissions(List<Commission> commissions) {
		if (commissions != null) {
			this.commissions = commissions;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return commissions.size();
	}

	@Override
	public Object getItem(int position) {
		return commissions.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_commission_manage, null);
			viewHolder.itemView = convertView.findViewById(R.id.ll_item);
			viewHolder.logoImageView = (ImageView) convertView.findViewById(R.id.iv_store_logo);
			viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.tv_store_name);
			viewHolder.contactPhoneTextView = (TextView) convertView.findViewById(R.id.tv_store_contact_phone);
			viewHolder.payCommissionTextView = (TextView) convertView.findViewById(R.id.tv_pay_commission);
			viewHolder.orderCounTextView = (TextView) convertView.findViewById(R.id.count);
			viewHolder.orderTotalPriceTextView = (TextView) convertView.findViewById(R.id.money);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final Commission commission = commissions.get(position);
		// 店铺名称
		String name = commission.getShopName();
		viewHolder.nameTextView.setText(name);
		// 店铺联系电话
		String phoneNum = commission.getMobile();
		viewHolder.contactPhoneTextView.setText(phoneNum);
		// 订单数
		String orderCount = String.valueOf(commission.getOrderCount());
		viewHolder.orderCounTextView.setText(orderCount);
		// 佣金总计
		float brokerageCount = commission.getBrokerageCount();
		String totalCount = StringUtil.getStringFromR(R.string.rmb) + brokerageCount;
		viewHolder.orderTotalPriceTextView.setText(totalCount);
		float unDefrayMoney = commission.getUnDefrayMoney();
		// 是否显示支付佣金按钮,如果待支付佣金为0则隐藏支付佣金按钮
		if ("c".equals(storeType) || unDefrayMoney == 0) { // C店不显示
			viewHolder.payCommissionTextView.setVisibility(View.GONE);
		} else if (commission.getBrokerageCount() != 0 && commission.getBrokerageCount() != 0 && unDefrayMoney > 0) { // A店显示
			viewHolder.payCommissionTextView.setVisibility(View.VISIBLE);
		}

		if (listener != null) {
			// 店铺logo
			String logoUrl = commission.getShopLogo();
			ImageView logoImageView = viewHolder.logoImageView;
			listener.onLoadLogoListener(logoUrl, logoImageView);

			// 点击联系电话
			viewHolder.contactPhoneTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String phoneNum = commission.getMobile();
					listener.onContactPhoneClickListener(phoneNum);
				}
			});

			// 点击支付佣金
			viewHolder.payCommissionTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onPayCommissionClick(position);
				}
			});

			// 条目点击
			viewHolder.itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onItemClickListener(position);
				}
			});
		}

		return convertView;
	}

	static class ViewHolder {
		View itemView;
		ImageView logoImageView; // 店铺logo
		TextView nameTextView; // 店铺名称
		TextView contactPhoneTextView; // 店铺联系电话
		TextView payCommissionTextView; // 支付佣金按钮
		TextView orderCounTextView; // 订单数
		TextView orderTotalPriceTextView; // 合计佣金
	}

	public interface ItemRequestListener {
		void onLoadLogoListener(String logoUrl, ImageView logoImageView);

		void onContactPhoneClickListener(String phoneNum);

		void onItemClickListener(int position);

		void onPayCommissionClick(int position);
	}

	private ItemRequestListener listener;

	public void setOnItemRequestListener(ItemRequestListener listener) {
		this.listener = listener;
	}

}
