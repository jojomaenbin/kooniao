package com.kooniao.travel.mine;

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
import com.kooniao.travel.model.MyOrder;
import com.kooniao.travel.utils.ColorUtil;
import com.kooniao.travel.utils.OrderUtil;
import com.kooniao.travel.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyOrderListAdapter extends BaseAdapter {
	private Context context;

	public MyOrderListAdapter(Context context) {
		this.context = context;
	}

	private List<MyOrder> myOrders = new ArrayList<MyOrder>();

	public void setOrderList(List<MyOrder> myOrders) {
		if (myOrders != null) {
			this.myOrders = myOrders;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return myOrders.size();
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
			viewHolder.orderCommenTextView = (TextView) convertView.findViewById(R.id.tv_order_comment);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final MyOrder myOrder = myOrders.get(position);
		// 订单号
		String orderNum = myOrder.getOrderNumber();
		viewHolder.orderNumTextView.setText(orderNum);
		// 是否显示编辑
		viewHolder.orderEditImageView.setVisibility(View.INVISIBLE);
		// 订单价格
		String orderTotalPrice = myOrder.getOrderTotal();
		orderTotalPrice = StringUtil.getStringFromR(R.string.rmb) + orderTotalPrice;
		viewHolder.priceTextView.setText(orderTotalPrice);
		// 订单来源
		String orderResource = myOrder.getShopName();
		viewHolder.resourceTextView.setText(orderResource);
		// 订单状态、编辑
		// 订单状态。0：未处理(红色)；1:已确认(绿色)；-1：已取消(灰色)；-2：已关闭(灰色)；2：已收款(绿色)；3：已退订(灰色)；4：已出团(绿色)；5：部分收款(灰色)
		int status = myOrder.getOrderStatus();
		if (status == -2) { // 已关闭则显示点评按钮
			viewHolder.orderCommenTextView.setVisibility(View.VISIBLE);
		} else {
			viewHolder.orderCommenTextView.setVisibility(View.INVISIBLE);
		}
		String orderStatus = OrderUtil.getStatusText(status); // 订单状态
		int orderStatusColorRes = OrderUtil.getStatusTextColor(status); // 订单文字颜色
		viewHolder.orderStatusTextView.setTextColor(context.getResources().getColor(orderStatusColorRes));
		viewHolder.orderStatusTextView.setText(orderStatus);
		// 加载封面
		String coverImgUrl = myOrder.getProductLogo();
		viewHolder.coverImageView.setBackgroundColor(ColorUtil.getRandomColorRes());
		ImageLoader.getInstance().displayImage(coverImgUrl, viewHolder.coverImageView); 
		// 产品名
		String orderName = myOrder.getProductName();
		viewHolder.orderNameTextView.setText(orderName);
		// 订单数量
		int orderCount = myOrder.getProductCount();
		viewHolder.orderCountTextView.setText("x" + String.valueOf(orderCount));
		// 订单日期
		String orderDate = myOrder.getOrderTime();
		viewHolder.orderDateTextView.setText(orderDate);
		// 点评点击
		if (listener != null) {
			viewHolder.orderCommenTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onOrderCommentClick(position);
				}
			});
			
			viewHolder.resourceTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					listener.onStoreClick(position); 
				}
			});

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onItemClick(position);
				}
			});
		}

		return convertView;
	}

	public interface OnOrderItemClickListener {
		void onOrderCommentClick(int position);
		
		void onStoreClick(int position);

		void onItemClick(int position);
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
		TextView orderCommenTextView; // 点评按钮
	}

}
