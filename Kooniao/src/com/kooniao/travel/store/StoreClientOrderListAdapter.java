package com.kooniao.travel.store;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.model.CustomerDetail;
import com.kooniao.travel.model.CustomerDetail.Order;
import com.kooniao.travel.utils.AppSetting;
import com.kooniao.travel.utils.ColorUtil;
import com.kooniao.travel.utils.OrderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class StoreClientOrderListAdapter extends BaseAdapter {
	private Context context;

	public StoreClientOrderListAdapter(Context context) {
		this.context = context;
	}

	private List<Order> orders = new ArrayList<CustomerDetail.Order>();

	public void setOrderList(List<Order> orders) {
		if (orders != null) {
			this.orders = orders;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return orders.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_order, null);
			viewHolder.orderBuyDateTip = (TextView) convertView.findViewById(R.id.tv_order_buy_date_tip);
			viewHolder.orderNumTextView = (TextView) convertView.findViewById(R.id.tv_order_number);
			viewHolder.orderStatusLayout = (LinearLayout) convertView.findViewById(R.id.ll_order_status_edit);
			viewHolder.orderEditImageView = (ImageView) convertView.findViewById(R.id.iv_order_edit);
			viewHolder.orderStatusTextView = (TextView) convertView.findViewById(R.id.tv_order_status);
			viewHolder.coverImageView = (ImageView) convertView.findViewById(R.id.iv_order_cover_img);
			viewHolder.orderNameTextView = (TextView) convertView.findViewById(R.id.tv_order_name);
			viewHolder.orderCountTextView = (TextView) convertView.findViewById(R.id.tv_order_count);
			viewHolder.resourceTextView = (TextView) convertView.findViewById(R.id.tv_order_resource);
			viewHolder.priceTextView = (TextView) convertView.findViewById(R.id.tv_order_price);
			viewHolder.orderDateTextView = (TextView) convertView.findViewById(R.id.tv_order_buy_date);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.orderBuyDateTip.setText(R.string.booking_date);
		final Order order = orders.get(position);
		// 订单号
		int orderNum = order.getSn();
		viewHolder.orderNumTextView.setText(String.valueOf(orderNum));
		viewHolder.orderEditImageView.setVisibility(View.INVISIBLE);
		// 订单来源
		String orderResource = AppSetting.getInstance().getStringPreferencesByKey(Define.SHOP_NAME);
		viewHolder.resourceTextView.setText(orderResource);
		// 订单状态、编辑
		// 订单状态。0：未处理(红色)；1:已确认(绿色)；-1：已取消(灰色)；-2：已关闭(灰色)；2：已收款(绿色)；3：已退订(灰色)；4：已出团(绿色)；5：部分收款(灰色)
		int status = order.getStatus();
		String orderStatus = OrderUtil.getStatusText(status); // 订单状态
		int orderStatusColorRes = OrderUtil.getStatusTextColor(status); // 订单文字颜色
		viewHolder.orderStatusTextView.setTextColor(context.getResources().getColor(orderStatusColorRes));  
		viewHolder.orderStatusTextView.setText(orderStatus);
		// 加载封面
		String coverImgUrl = order.getImg();
		viewHolder.coverImageView.setBackgroundColor(ColorUtil.getRandomColorRes()); 
		ImageLoader.getInstance().displayImage(coverImgUrl, viewHolder.coverImageView); 
		// 产品名
		String orderName = order.getTitle();
		viewHolder.orderNameTextView.setText(orderName);
		// 订单数量
		viewHolder.orderCountTextView.setVisibility(View.INVISIBLE);
		// 订单日期
		String orderDate = order.getCtime();
		viewHolder.orderDateTextView.setText(orderDate);
		// item事件处理
		if (listener != null) {
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					listener.onItemClick(order); 
				}
			});
			
			// 请求加载封面
			String imgUrl = order.getImg();
			ImageView coverImageView = viewHolder.coverImageView;
			listener.onLoadCoverImgListener(imgUrl, coverImageView); 
		}

		return convertView;
	}
	
	public interface OnOrderItemClickListener {
		void onItemClick(Order order);
		
		void onLoadCoverImgListener(String imgUrl, ImageView coverImageView);
	}
	
	private OnOrderItemClickListener listener;
	
	public void setOnOrderItemClickListener(OnOrderItemClickListener listener) {
		this.listener = listener;
	}

	static class ViewHolder {
		TextView orderNumTextView; // 订单号
		LinearLayout orderStatusLayout; // 订单编辑布局
		ImageView orderEditImageView; // 订单编辑图标
		TextView orderStatusTextView; // 订单状态
		ImageView coverImageView; // 封面
		TextView orderNameTextView; // 订单名
		TextView orderCountTextView; // 订单数量
		TextView resourceTextView; // 订单来源
		TextView priceTextView; // 订单价格
		TextView orderDateTextView; // 订单日期
		TextView orderBuyDateTip; // 购买时间
	}

}
