package com.kooniao.travel.store;

import java.util.ArrayList;
import java.util.List;

import com.kooniao.travel.R;
import com.kooniao.travel.model.CommissionDetail.CommissionRecord;
import com.kooniao.travel.utils.StringUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommissionDetailAdapter extends BaseAdapter {
	
	private Context context;
	
	public CommissionDetailAdapter(Context context) {
		this.context = context;
	}
	
	List<CommissionRecord> commissionRecords = new ArrayList<CommissionRecord>(); 
	
	public void setCommissionRecords(List<CommissionRecord> commissionRecords) {
		if (commissionRecords != null) {
			this.commissionRecords = commissionRecords;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return commissionRecords.size();
	}

	@Override
	public Object getItem(int position) {
		return commissionRecords.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_commission_detail, null);
			viewHolder.dateTextView = (TextView) convertView.findViewById(R.id.tv_pay_commission_date);
			viewHolder.paymentWayTextView = (TextView) convertView.findViewById(R.id.tv_pay_commission_way);
			viewHolder.amountTextView = (TextView) convertView.findViewById(R.id.tv_pay_commission_amount);
			viewHolder.remarkTextView = (TextView) convertView.findViewById(R.id.tv_remark);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag(); 
		}
		
		CommissionRecord commissionRecord = commissionRecords.get(position);
		// 支付日期
		String date = commissionRecord.getTime();
		viewHolder.dateTextView.setText(date);
		// 支付方式
		int paymentMode = commissionRecord.getMode();
		// 支付方式 1线下支付，2系统入账
		String paymentWay = paymentMode == 1 ? StringUtil.getStringFromR(R.string.pay_commission_way_offline) : StringUtil.getStringFromR(R.string.pay_commission_way_system);
		viewHolder.paymentWayTextView.setText(paymentWay);
		// 支付金额
		String amount = StringUtil.getStringFromR(R.string.rmb) + commissionRecord.getMoney();
		viewHolder.amountTextView.setText(amount);
		// 支付备注
		String remark = commissionRecord.getRemark();
		viewHolder.remarkTextView.setText(remark); 
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView dateTextView; // 支付日期
		TextView paymentWayTextView; // 支付方式
		TextView amountTextView; // 支付金额
		TextView remarkTextView; // 支付备注
	}

}
