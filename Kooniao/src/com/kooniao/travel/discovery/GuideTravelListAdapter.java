package com.kooniao.travel.discovery;

import java.util.ArrayList;
import java.util.List;

import com.kooniao.travel.R;
import com.kooniao.travel.model.GuideTravel;
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

public class GuideTravelListAdapter extends BaseAdapter {
	
	private Context context;
	
	public GuideTravelListAdapter(Context context) {
		this.context = context;
	}
	
	private List<GuideTravel> travels = new ArrayList<GuideTravel>();
	
	public void setTravels(List<GuideTravel> travels) {
		if (travels != null) {
			this.travels = travels;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return travels.size();
	}

	@Override
	public Object getItem(int position) {
		return travels.get(position);
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
			convertView.setTag(viewHolder); 
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final GuideTravel travel = travels.get(position);
		// 名称
		String title = travel.getPlanTitle();
		viewHolder.titleTextView.setText(title); 
		// 评分
		float rank = travel.getPlanRank();
		viewHolder.ratingBar.setRating(rank);
		// 价格
		float price = travel.getPlanPrice();
		String priceString = StringUtil.getStringFromR(R.string.rmb) + price;
		viewHolder.priceTextView.setText(priceString);
		if (listener != null) {
			// 加载图片
			String imgUrl = travel.getPlanImage();
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
