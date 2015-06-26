package com.kooniao.travel.store;

import java.util.ArrayList;
import java.util.List;
import com.kooniao.travel.R;
import com.kooniao.travel.model.StoreProduct;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * C店产品管理adapter适配器
 * @author ke.wei.quan
 *
 */
@SuppressLint("InflateParams")
public class AStoreProductManagerAdapter extends BaseAdapter {

	private Context context;

	public AStoreProductManagerAdapter(Context context) {
		this.context = context;
	}

	List<StoreProduct> storeProducts = new ArrayList<StoreProduct>();

	public void setProducts(List<StoreProduct> storeProducts) {
		if (storeProducts != null) {
			this.storeProducts = storeProducts;
			notifyDataSetChanged(); 
		}
	}

	@Override
	public int getCount() {
		return storeProducts.size();
	}

	@Override
	public Object getItem(int position) {
		return storeProducts.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_a_store_product_manage, null);
			viewHolder.coverImageView = (ImageView) convertView.findViewById(R.id.iv_product_cover_img);
			viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.tv_product_name);
			viewHolder.productInfoTextView = (TextView) convertView.findViewById(R.id.tv_product_info);
			viewHolder.priceRangeTextView = (TextView) convertView.findViewById(R.id.tv_product_price_range);
			viewHolder.shareTextView = (TextView) convertView.findViewById(R.id.tv_share);
			viewHolder.recommendTextView = (TextView) convertView.findViewById(R.id.tv_recommend);
			viewHolder.distritbuteTextView = (TextView) convertView.findViewById(R.id.tv_distribute);
			viewHolder.editTextView = (TextView) convertView.findViewById(R.id.tv_edit);
			viewHolder.productStatusTextView = (TextView) convertView.findViewById(R.id.tv_product_status);
			viewHolder.deleteTextView = (TextView) convertView.findViewById(R.id.tv_delete);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final StoreProduct storeProduct = storeProducts.get(position);
		// 产品名称
		viewHolder.nameTextView.setText(storeProduct.getProductName());
		// 产品信息(库存+套餐数)
		String productStock = storeProduct.getProductStock(); // 库存
		String productPriceAmount = storeProduct.getProductPriceAmount(); // 套餐
		String text = productStock + "  |  " + productPriceAmount;
 		viewHolder.productInfoTextView.setText(text);
 		// 产品价格范围
 		String productScope = storeProduct.getProductScope();
 		viewHolder.priceRangeTextView.setText(productScope); 
 		// 产品是否已推荐
 		final int productRecommend = storeProduct.getProductRecommend();
 		if (productRecommend == 0) { // 未推荐
 			viewHolder.recommendTextView.setText(R.string.recommend);
		} else {
			viewHolder.recommendTextView.setText(R.string.recommended);
		}
 		// 产品是上下架
 		final int productStatus = storeProduct.getProductStatus();
 		if (productStatus == 1) { // 产品目前是上架状态
 			viewHolder.productStatusTextView.setText(R.string.sloadout);
		} else if (productStatus == 3) { // 产品目前是下架状态
			viewHolder.productStatusTextView.setText(R.string.grounding);
		}
		
		if (listener != null) {
			// 加载图片
			ImageView coverImageView = viewHolder.coverImageView;
			String imgUrl = storeProduct.getImg();
			listener.onLoadCoverImgListener(imgUrl, coverImageView);
			
			// 条目点击
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onItemClickListener(position);
				}
			});

			// 分享
			viewHolder.shareTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onShareClickListener(position);
				}
			});

			// 推荐
			viewHolder.recommendTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (productRecommend == 0) { // 未推荐
						listener.onRecommendClickListener(position);
					} else { // 已推荐
						listener.onRecommendedClickListener(position);
					}
				}
			});
			
			// 分销
			viewHolder.distritbuteTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					listener.onDistributeListener(position);
				}
			});
			
			// 编辑
			viewHolder.editTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					listener.onEditListener(position);
				}
			});
			
			// 下架
			viewHolder.productStatusTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (productStatus == 1) { // 出售中，进行下架操作
						listener.onShelfListener(position); 
					} else if (productStatus == 3) { // 未出售状态，进行上架操作
						listener.onPutawayListener(position); 
					}
				}
			});

			// 删除
			viewHolder.deleteTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onDeleteListener(position);
				}
			});
		}

		return convertView;
	}

	private static class ViewHolder {
		ImageView coverImageView; // 封面
		TextView nameTextView; // 产品名称
		TextView productInfoTextView; // 产品信息(库存+套餐数)
		TextView priceRangeTextView; // 产品价格范围
		// 底部栏操作
		TextView shareTextView; // 分享
		TextView recommendTextView; // 推荐
		TextView distritbuteTextView; // 分销
		TextView editTextView; // 编辑
		TextView productStatusTextView; // 上下架
		TextView deleteTextView; // 删除
	}

	public interface ListItemRequestListener {
		void onLoadCoverImgListener(String imgUrl, ImageView coverImageView);

		void onShareClickListener(int position); // 分享
		
		void onRecommendClickListener(int position); // 推荐
		
		void onRecommendedClickListener(int position); // 已推荐

		void onDistributeListener(int position); // 分销

		void onEditListener(int position); // 编辑
		
		void onShelfListener(int position); // 下架
		
		void onPutawayListener(int position); // 上架

		void onDeleteListener(int position); // 删除

		void onItemClickListener(int position);
	}

	private ListItemRequestListener listener;

	public void setOnListItemRequestListener(ListItemRequestListener listener) {
		this.listener = listener;
	}

}
