package com.kooniao.travel.store;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

import com.kooniao.travel.R;
import com.kooniao.travel.model.Product;
import com.kooniao.travel.utils.ColorUtil;
import com.kooniao.travel.utils.StringUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StoreProductListAdapter extends BaseAdapter implements StickyListHeadersAdapter {
	private Context context;

	public StoreProductListAdapter(Context context) {
		this.context = context;
	}

	List<Product> products = new ArrayList<Product>();

	public void setProducts(List<Product> products) {
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
		return products.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ItemViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ItemViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_product_home_list, null);
			viewHolder.coverImageView = (ImageView) convertView.findViewById(R.id.iv_product_cover_img);
			viewHolder.scoreTextView = (TextView) convertView.findViewById(R.id.tv_product_score);
			viewHolder.registrationNumberTextView = (TextView) convertView.findViewById(R.id.tv_product_registration_number);
			viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.tv_product_name);
			viewHolder.resourceTextView = (TextView) convertView.findViewById(R.id.tv_product_resource);
			viewHolder.priceTextView = (TextView) convertView.findViewById(R.id.tv_product_price);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ItemViewHolder) convertView.getTag();
		}

		final Product product = products.get(position);
		// 背景
		viewHolder.coverImageView.setBackgroundColor(ColorUtil.getRandomColorRes()); // 设置随机背景色
		// 产品评分
		float score = product.getRate();
		String scoreString = score + StringUtil.getStringFromR(R.string.unit_point);
		viewHolder.scoreTextView.setText(scoreString);
		// 产品报名人数
		int registrationNumber = product.getOrderCount();
		String registrationNumberString = registrationNumber + StringUtil.getStringFromR(R.string.unit_person);
		viewHolder.registrationNumberTextView.setText(registrationNumberString);
		// 产品名
		String productName = product.getTitle();
		viewHolder.nameTextView.setText(productName);
		// 产品来源
		String resource = product.getShopName();
		viewHolder.resourceTextView.setText(resource);
		// 产品价格
		String price = product.getPrice();
		viewHolder.priceTextView.setText(price);

		// item请求事件
		if (itemRequestListener != null) {
			itemBaseRequestListener = itemRequestListener;
		}
		
		if (itemBaseRequestListener != null) {
			viewHolder.coverImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					itemBaseRequestListener.onListItemClick(position);
				}
			});
			
			viewHolder.resourceTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					itemBaseRequestListener.onStoreClick(position);
				}
			});
			
			// 封面
			ImageView coverImageView = viewHolder.coverImageView;
			String coverImgUrl = product.getImg();
			itemBaseRequestListener.onLoadCoverImgListener(coverImgUrl, coverImageView); 
		}

		return convertView;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeadViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new HeadViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.store_product_stick_top, null);
			viewHolder.categoryAllTextView = (TextView) convertView.findViewById(R.id.tv_category_all);
			viewHolder.categoryLineTextView = (TextView) convertView.findViewById(R.id.tv_category_line);
			viewHolder.categoryHotelTextView = (TextView) convertView.findViewById(R.id.tv_category_hotel);
			viewHolder.categoryScenicTextView = (TextView) convertView.findViewById(R.id.tv_category_scenic);
			viewHolder.categoryShoppingTextView = (TextView) convertView.findViewById(R.id.tv_category_shopping);
			viewHolder.categoryAmusementTextView = (TextView) convertView.findViewById(R.id.tv_category_amusement);
			viewHolder.categoryFoodTextView = (TextView) convertView.findViewById(R.id.tv_category_food);
			viewHolder.categoryTrafficTextView = (TextView) convertView.findViewById(R.id.tv_category_traffic);
			viewHolder.categoryGroupProductTextView = (TextView) convertView.findViewById(R.id.tv_category_group_product);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (HeadViewHolder) convertView.getTag();
		}

		if (itemRequestListener != null) {
			viewHolder.categoryLineTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					itemRequestListener.onCategoryLineClick(v);
				}
			});

			viewHolder.categoryHotelTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					itemRequestListener.onCategoryHotelClick(v);
				}
			});

			viewHolder.categoryScenicTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					itemRequestListener.onCategoryScenicClick(v);
				}
			});

			viewHolder.categoryShoppingTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					itemRequestListener.onCategoryShoppingClick(v);
				}
			});

			viewHolder.categoryAmusementTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					itemRequestListener.onCategoryAmusementClick(v);
				}
			});

			viewHolder.categoryFoodTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					itemRequestListener.onCategoryFoodClick(v);
				}
			});

			viewHolder.categoryTrafficTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					itemRequestListener.onCategoryTrafficClick(v);
				}
			});

			viewHolder.categoryGroupProductTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					itemRequestListener.onCategoryGroupProductClick(v);
				}
			});
		}

		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		return 0;
	}

	static class ItemViewHolder {
		ImageView coverImageView; // 封面
		TextView scoreTextView; // 产品评分
		TextView registrationNumberTextView; // 产品报名人数
		TextView nameTextView; // 产品名
		TextView resourceTextView; // 产品来源
		TextView priceTextView; // 产品价格
	}

	static class HeadViewHolder {
		TextView categoryAllTextView; // 全部产品分类
		TextView categoryLineTextView; // 线路产品分类
		TextView categoryHotelTextView; // 酒店产品分类
		TextView categoryScenicTextView; // 景点门票产品分类
		TextView categoryShoppingTextView; // 购物优惠产品分类
		TextView categoryAmusementTextView; // 休闲娱乐产品分类
		TextView categoryFoodTextView; // 餐饮美食产品分类
		TextView categoryTrafficTextView; // 交通产品分类
		TextView categoryGroupProductTextView; // 组合产品产品分类
	}

	public interface ListItemBaseRequestListener {
		void onLoadCoverImgListener(String imgUrl, ImageView coverImageView);

		void onStoreClick(int position);

		void onListItemClick(int position);
	}

	public interface ItemRequestListener extends ListItemBaseRequestListener {

		void onCategoryLineClick(View v);

		void onCategoryHotelClick(View v);

		void onCategoryScenicClick(View v);

		void onCategoryShoppingClick(View v);

		void onCategoryAmusementClick(View v);

		void onCategoryFoodClick(View v);

		void onCategoryTrafficClick(View v);

		void onCategoryGroupProductClick(View v);
	}

	private ListItemBaseRequestListener itemBaseRequestListener;

	public void setOnListItemBaseRequestListener(ListItemBaseRequestListener itemBaseRequestListener) {
		this.itemBaseRequestListener = itemBaseRequestListener;
	}

	private ItemRequestListener itemRequestListener;

	public void setOnItemRequestListener(ItemRequestListener itemRequestListener) {
		this.itemRequestListener = itemRequestListener;
	}

}
