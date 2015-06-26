package com.kooniao.travel.store;

import java.util.ArrayList;
import java.util.List;

import com.kooniao.travel.R;
import com.kooniao.travel.model.StoreProduct;
import com.kooniao.travel.utils.KooniaoTypeUtil;
import com.kooniao.travel.utils.StringUtil;

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
public class CStoreProductManagerAdapter extends BaseAdapter {

	private Context context;

	public CStoreProductManagerAdapter(Context context) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_c_store_product_manage, null);
			viewHolder.coverImageView = (ImageView) convertView.findViewById(R.id.iv_product_cover_img);
			viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.tv_product_name);
			viewHolder.typeTextView = (TextView) convertView.findViewById(R.id.tv_product_type);
			viewHolder.resourceTextView = (TextView) convertView.findViewById(R.id.tv_product_resource);
			viewHolder.priceTextView = (TextView) convertView.findViewById(R.id.tv_product_price);
			viewHolder.remarkTextView = (TextView) convertView.findViewById(R.id.tv_product_remark);
			viewHolder.deleteTextView = (TextView) convertView.findViewById(R.id.tv_product_operation_delete);
			viewHolder.stateTextView = (TextView) convertView.findViewById(R.id.tv_product_cover_mask);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final StoreProduct storeProduct = storeProducts.get(position);
		// 产品名称
		viewHolder.nameTextView.setText(storeProduct.getProductName());
		// 产品类型
		int type = storeProduct.getProductType();
		String productType = KooniaoTypeUtil.getStringByType(type);
		viewHolder.typeTextView.setText(productType);
		// 产品来源
		viewHolder.resourceTextView.setText(storeProduct.getShopName());
		// 产品价格
		String price = StringUtil.getStringFromR(R.string.rmb) + storeProduct.getProductPrice();
		viewHolder.priceTextView.setText(price);
		int affiliateStatus = storeProduct.getAffiliateStatus();
		if (affiliateStatus != 0) {
			// 备注
			String remark = StringUtil.getStringFromR(R.string.commission_desc) + storeProduct.getBrokerage() + StringUtil.getStringFromR(R.string.commission_desc_per);
			viewHolder.remarkTextView.setText(remark);
		}
		// 产品状态
		final int productStatus = storeProduct.getProductStatus();
		if (productStatus == 1 || productStatus == 4) {
			viewHolder.stateTextView.setVisibility(View.GONE);
		} else if(productStatus == 3) {
			viewHolder.stateTextView.setVisibility(View.VISIBLE);
		}
		
		if (listener != null) {
			// 条目点击
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onItemClickListener(position);
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
			String imgUrl = storeProduct.getImg();
			listener.onLoadCoverImgListener(imgUrl, coverImageView);

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
		TextView typeTextView; // 产品类型
		TextView resourceTextView; // 产品来源
		TextView priceTextView; // 产品价格
		TextView remarkTextView; // 备注
		TextView deleteTextView; // 产品操作（删除）
		TextView stateTextView; // 产品状态
	}

	public interface ListItemRequestListener {
		void onLoadCoverImgListener(String imgUrl, ImageView coverImageView);

		void onRemarkClickListener(int position); // 备注

		void onDeleteListener(int position); // 删除

		void onItemClickListener(int position);
	}

	private ListItemRequestListener listener;

	public void setOnListItemRequestListener(ListItemRequestListener listener) {
		this.listener = listener;
	}

}
