package com.kooniao.travel.around;

import java.util.ArrayList;
import java.util.List;

import com.kooniao.travel.R;
import com.kooniao.travel.model.Around;
import com.kooniao.travel.utils.ColorUtil;
import com.kooniao.travel.utils.StringUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class AroundListAdapter extends BaseAdapter {
	
	private Context context;
	
	public AroundListAdapter(Context context) {
		this.context = context;
	}
	
	private List<Around> arounds = new ArrayList<Around>();
	
	public void setArounds(List<Around> arounds) {
		if (arounds != null) {
			this.arounds = arounds;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return arounds.size();
	}

	@Override
	public Object getItem(int position) {
		return arounds.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_around, null); 
			viewHolder.coverImageView = (ImageView) convertView.findViewById(R.id.iv_cover);
			viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.tv_around_title);
			viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.rb_around);
			viewHolder.priceTextView = (TextView) convertView.findViewById(R.id.tv_around_price);
			viewHolder.distanceTextView = (TextView) convertView.findViewById(R.id.tv_around_distance);
			convertView.setTag(viewHolder); 
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final Around around = arounds.get(position);
		// 名称
		String title = around.getName();
		viewHolder.titleTextView.setText(title); 
		// 评分
		float rank = around.getRank();
		viewHolder.ratingBar.setRating(rank);
		// 价格
		float price = around.getPrice();
		String priceString = StringUtil.getStringFromR(R.string.rmb) + price;
		viewHolder.priceTextView.setText(priceString);
		// 距离
		float distance = around.getDistance();
		String distanceString = distance + "km";
		viewHolder.distanceTextView.setText(distanceString);
		if (listener != null) {
			// 加载图片
			String imgUrl = around.getImg();
			ImageView coverImageView = viewHolder.coverImageView;
			coverImageView.setBackgroundColor(ColorUtil.getRandomColorRes()); 
			listener.onLoadCoverImgListener(imgUrl, coverImageView);
			
			// 条目点击
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					listener.onItemClickListener(position);  
				}
			});
		}
		return convertView;
	}
	
	static class ViewHolder {
		ImageView coverImageView; // 封面
		TextView titleTextView; // 标题
		RatingBar ratingBar; // 评分
		TextView priceTextView; // 价格
		TextView distanceTextView; // 距离
	}
	
	public interface ItemRequestListener {
		void onLoadCoverImgListener(String imgUrl, ImageView coverImageView);
		
		void onItemClickListener(int position);
	}
	
	private ItemRequestListener listener;
	
	public void setOnItemRequestListener(ItemRequestListener listener) {
		this.listener = listener;
	}

}
