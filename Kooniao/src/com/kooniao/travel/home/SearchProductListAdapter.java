package com.kooniao.travel.home;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.model.Product;
import com.kooniao.travel.utils.ColorUtil;

public class SearchProductListAdapter extends BaseAdapter {
	private Context context;

	public SearchProductListAdapter(Context context) {
		this.context = context;
	}

	private List<Product> products = new ArrayList<Product>();

	public void setProductList(List<Product> products) {
		if (products != null) {
			this.products = products;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return products.size();
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_product_search_list, null);
			viewHolder.itemView = convertView.findViewById(R.id.lv_item);
			viewHolder.coverImageView = (ImageView) convertView.findViewById(R.id.iv_product_cover_img);
			viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.tv_product_name);
			viewHolder.scoreRatingBar = (RatingBar) convertView.findViewById(R.id.rb_product);
			viewHolder.resourceTextView = (TextView) convertView.findViewById(R.id.tv_product_resource);
			viewHolder.priceTextView = (TextView) convertView.findViewById(R.id.tv_product_price);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final Product product = products.get(position);
		// 背景
		viewHolder.coverImageView.setBackgroundColor(ColorUtil.getRandomColorRes()); // 设置随机背景色
		// 产品评分
		float score = product.getRate();
		viewHolder.scoreRatingBar.setRating(score);
		// 产品名
		String productName = product.getTitle();
		viewHolder.nameTextView.setText(productName);
		// 产品来源
		String resource = product.getShopName();
		viewHolder.resourceTextView.setText(resource);
		// 产品价格
		String price = product.getPrice();
		viewHolder.priceTextView.setText(price);
		if (listener != null) {
			// 条目点击
			viewHolder.itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onItemClick(position);
				}
			});
			// 来源店铺点击
			viewHolder.resourceTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onStoreClick(position);
				}
			});

			// 封面
			ImageView coverImageView = viewHolder.coverImageView;
			String coverImgUrl = product.getImg();
			listener.onLoadCoverImgListener(coverImgUrl, coverImageView);
		}

		return convertView;
	}

	static class ViewHolder {
		View itemView;
		ImageView coverImageView; // 封面
		TextView nameTextView; // 产品名
		RatingBar scoreRatingBar; // 产品评分
		TextView resourceTextView; // 产品来源
		TextView priceTextView; // 产品价格
	}

	public interface ListItemRequestListener {
		void onLoadCoverImgListener(String imgUrl, ImageView coverImageView);

		void onStoreClick(int position);

		void onItemClick(int position);
	}

	private ListItemRequestListener listener;

	public void setOnListItemRequestListener(ListItemRequestListener listener) {
		this.listener = listener;
	}

}
