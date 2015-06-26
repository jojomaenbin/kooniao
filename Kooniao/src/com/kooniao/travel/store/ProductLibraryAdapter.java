package com.kooniao.travel.store;

import java.util.ArrayList;
import java.util.List;

import com.kooniao.travel.R;
import com.kooniao.travel.model.Product;
import com.kooniao.travel.utils.KooniaoTypeUtil;
import com.kooniao.travel.utils.StringUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductLibraryAdapter extends BaseAdapter {
	private Context context;

	public ProductLibraryAdapter(Context context) {
		this.context = context;
	}

	List<Product> products = new ArrayList<Product>();

	public void setProducts(List<Product> products) {
		if (products != null) {
			for (Product product : products) {
				for (Product addedProduct : getAddedProducts()) {
					if (product.toString().equals(addedProduct.toString())) {
						product.setAdded(true); 
					}
				}
			}
			
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

	private List<Product> addedProducts = new ArrayList<Product>();

	/**
	 * 已添加产品
	 * 
	 * @param addedProducts
	 */
	public void setAddedProducts(List<Product> addedProducts) {
		if (addedProducts != null) {
			this.addedProducts = addedProducts;
		}
	}

	public List<Product> getAddedProducts() {
		return addedProducts;
	}
	
	public void clearAddedProducts() {
		addedProducts.clear(); 
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_c_store_product_manage, null);
			viewHolder.coverImageView = (ImageView) convertView.findViewById(R.id.iv_product_cover_img);
			viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.tv_product_name);
			viewHolder.typeTextView = (TextView) convertView.findViewById(R.id.tv_product_type);
			viewHolder.resourceTextView = (TextView) convertView.findViewById(R.id.tv_product_resource);
			viewHolder.priceTextView = (TextView) convertView.findViewById(R.id.tv_product_price);
			viewHolder.remarkTextView = (TextView) convertView.findViewById(R.id.tv_product_remark);
			viewHolder.operationTextView = (TextView) convertView.findViewById(R.id.tv_product_operation_delete);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final Product product = products.get(position);
		// 产品名称
		viewHolder.nameTextView.setText(product.getTitle());
		// 产品类型
		int type = product.getType();
		String productType = KooniaoTypeUtil.getStringByType(type);
		viewHolder.typeTextView.setText(productType);
		// 产品来源
		viewHolder.resourceTextView.setText(product.getShopName());
		// 产品价格
		String price = StringUtil.getStringFromR(R.string.rmb) + product.getPrice();
		viewHolder.priceTextView.setText(price);
		// 备注
		String remark = StringUtil.getStringFromR(R.string.commission_desc) + product.getBrokerage() + StringUtil.getStringFromR(R.string.commission_desc_per);
		viewHolder.remarkTextView.setText(remark);
		// 显示添加或取消文字
		if (product.isAdded()) {
			viewHolder.operationTextView.setText(R.string.cancel);
			viewHolder.operationTextView.setTextColor(context.getResources().getColor(R.color.vdd3a2c));
			viewHolder.operationTextView.setBackgroundResource(R.drawable.red_retangle_bg); 
		} else {
			viewHolder.operationTextView.setText(R.string.select);
			viewHolder.operationTextView.setTextColor(context.getResources().getColor(R.color.v7ac142));
			viewHolder.operationTextView.setBackgroundResource(R.drawable.green_retangle_bg); 
		}

		if (listener != null) {
			// 条目点击
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onItemClickListener(position);
				}
			});
			
			// 点击店铺
			viewHolder.resourceTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					listener.onStoreClickListener(position); 
				}
			});
			
			viewHolder.remarkTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					listener.onRemarkClickListener(position); 
				}
			});

			// 加载图片
			ImageView coverImageView = viewHolder.coverImageView;
			String imgUrl = product.getImg();
			listener.onLoadCoverImgListener(imgUrl, coverImageView);

			// 添加/取消
			viewHolder.operationTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (addedProducts.contains(product)) {
						product.setAdded(false); 
						addedProducts.remove(product);
					} else {
						product.setAdded(true); 
						addedProducts.add(product);
					}
					notifyDataSetChanged(); 
					listener.onAddListener(addedProducts);
				}
			});
		}

		return convertView;
	}

	private static class ViewHolder {
		ImageView coverImageView; // 封面
		TextView nameTextView; // 产品名称
		TextView typeTextView; // 产品类型
		TextView resourceTextView; // 产品来源
		TextView priceTextView; // 产品价格
		TextView remarkTextView; // 备注
		TextView operationTextView; // 产品操作（添加/删除）
	}

	public interface ListItemRequestListener {
		void onLoadCoverImgListener(String imgUrl, ImageView coverImageView);

		void onAddListener(List<Product> addedProducts); // 添加
		
		void onRemarkClickListener(int position);

		void onItemClickListener(int position);
		
		void onStoreClickListener(int position);
	}

	private ListItemRequestListener listener;

	public void setOnListItemRequestListener(ListItemRequestListener listener) {
		this.listener = listener;
	}

}
