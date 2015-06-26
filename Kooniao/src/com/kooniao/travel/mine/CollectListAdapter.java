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
import com.kooniao.travel.model.Collect;
import com.kooniao.travel.utils.ColorUtil;

public class CollectListAdapter extends BaseAdapter {
	private Context context;

	public CollectListAdapter(Context context) {
		this.context = context;
	}

	private List<Collect> collects = new ArrayList<Collect>();

	public void setCollectList(List<Collect> collects) {
		if (collects != null) {
			this.collects = collects;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return collects.size();
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_collect, null);
			viewHolder.frontView = convertView.findViewById(R.id.front);
			viewHolder.coverImageView = (ImageView) convertView.findViewById(R.id.iv_collect_cover);
			viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.tv_collect_name);
			viewHolder.typeTextView = (TextView) convertView.findViewById(R.id.tv_collect_type);
			viewHolder.resourceLayout = (LinearLayout) convertView.findViewById(R.id.ll_collect_resource);
			viewHolder.resourceTextView = (TextView) convertView.findViewById(R.id.tv_product_resource);
			viewHolder.timeTextView = (TextView) convertView.findViewById(R.id.tv_collect_time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final Collect collect = collects.get(position);
		// 背景
		viewHolder.coverImageView.setBackgroundColor(ColorUtil.getRandomColorRes()); // 设置随机背景色
		// 收藏类型
		int productId = collect.getProductId();
		String name = "";
		if (productId == 0) {
			// 收藏名称
			name = collect.getName();
			viewHolder.nameTextView.setText(name);
			viewHolder.typeTextView.setText(R.string.discovery); // 发现
			viewHolder.resourceLayout.setVisibility(View.GONE);
		} else {
			// 收藏名称
			name = collect.getProductName();
			viewHolder.typeTextView.setText(R.string.product); // 产品
			viewHolder.resourceLayout.setVisibility(View.VISIBLE);
			String storeName = collect.getShopName(); // 来源店铺
			viewHolder.resourceTextView.setText(storeName); 
		}
		// 收藏名称
		viewHolder.nameTextView.setText(name);
		// 收藏时间
		String time = collect.getTime();
		viewHolder.timeTextView.setText(time); 
		if (listener != null) {
			// 店铺点击
			viewHolder.resourceTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					listener.onStoreClick(position); 
				}
			});
			// 加载封面
			ImageView coverImageView = viewHolder.coverImageView;
			String imgUrl = collect.getImage();
			listener.onLoadCoverImgListener(imgUrl, coverImageView);
		}
		
		return convertView;
	}

	static class ViewHolder {
		View frontView;
		ImageView coverImageView; // 封面
		TextView nameTextView; // 收藏名称
		TextView typeTextView; // 收藏类型
		LinearLayout resourceLayout; // 来源布局
		TextView resourceTextView; // 收藏来源
		TextView timeTextView; // 收藏时间
	}
	
	public interface ListItemRequestListener {
		void onLoadCoverImgListener(String imgUrl, ImageView coverImageView);

		void onStoreClick(int position);
	}
	
	private ListItemRequestListener listener;
	
	public void setOnListItemRequestListener(ListItemRequestListener listener) {
		this.listener = listener;
	}

}
