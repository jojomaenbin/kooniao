package com.kooniao.travel.store;

import java.util.ArrayList;
import java.util.List;

import com.kooniao.travel.R;
import com.kooniao.travel.model.Around;
import com.kooniao.travel.utils.ColorUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * 引用列表数据adapter
 * @author ke.wei.quan
 *
 */
@SuppressLint("InflateParams")
public class ReferenceListAdapter extends BaseAdapter {
	
	private Context context;
	
	public ReferenceListAdapter(Context context) {
		this.context = context;
	}
	
	private List<Around> references = new ArrayList<Around>();
	
	public void setReferences(List<Around> arounds) {
		if (arounds != null) {
			this.references = arounds;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return references.size();
	}

	@Override
	public Object getItem(int position) {
		return references.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_reference, null); 
			viewHolder.coverImageView = (ImageView) convertView.findViewById(R.id.iv_cover);
			viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.tv_reference_title);
			viewHolder.typeTextView = (TextView) convertView.findViewById(R.id.tv_reference_type);
			viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.rb_reference);
			convertView.setTag(viewHolder); 
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final Around around = references.get(position);
		// 名称
		String title = around.getName();
		viewHolder.titleTextView.setText(title); 
		// 评分
		float rank = around.getRank();
		viewHolder.ratingBar.setRating(rank);
		// 类型
		String type = around.getType();
		viewHolder.typeTextView.setText(type);
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
		TextView typeTextView; // 类型
		RatingBar ratingBar; // 评分
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
