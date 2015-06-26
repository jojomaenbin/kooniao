package com.kooniao.travel.store;

import java.util.List;

import com.kooniao.travel.R;
import com.kooniao.travel.model.ProductDistributionSalesStatistics;
import com.kooniao.travel.model.ProductSalesStatistics;
import com.kooniao.travel.model.SelfLockingSalesStatistics;
import com.kooniao.travel.model.StoreDistributionSalesStatistics;
import com.kooniao.travel.model.StoreSalesStatistics;
import com.kooniao.travel.model.StoreTotalSalesStatistics;
import com.kooniao.travel.utils.ColorUtil;
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

public class SaleStatisticsAdapter extends BaseAdapter {

	public static enum StatisticsType {
		STATISTICS_STORESALESTATISTICS(0), // 店铺销售统计
		STATISTICS_PRODUCTSALESTATISTICS(1), // 产品销售统计
		STATISTICS_SELFSALESTATISTICS(2), // 自销统计
		STATISTICS_STORETOTALSALESTATISTICS(3), // 店铺总销
		STATISTICS_STOREDISTRIBUTIONSALESTATISTICS(4), // 店铺分销统计
		STATISTICS_PRODUCTDISTRIBUTIONSALESTATISTICSTYPE(5); // 产品分销统计

		public int type;

		StatisticsType(int type) {
			this.type = type;
		}
	}

	private Context context;

	public SaleStatisticsAdapter(Context context) {
		this.context = context;
	}

	// 统计类型（产品、店铺）
	private StatisticsType statisticsType = StatisticsType.STATISTICS_STORESALESTATISTICS;

	public void setStatisticsType(StatisticsType statisticsType) {
		this.statisticsType = statisticsType;
	}

	public int getStatisticsType() {
		return statisticsType.type;
	}

	/**
	 * C店
	 */
	// 店铺销售统计
	StoreSalesStatistics storeSalesStatistics = new StoreSalesStatistics();
	// 产品销售统计
	ProductSalesStatistics productSalesStatistics = new ProductSalesStatistics();

	/**
	 * A店
	 */
	// 自销统计
	SelfLockingSalesStatistics selfLockingSalesStatistics = new SelfLockingSalesStatistics();
	// 店铺总销
	StoreTotalSalesStatistics storeTotalSalesStatistics = new StoreTotalSalesStatistics();
	// 店铺分销统计
	StoreDistributionSalesStatistics storeDistributionSalesStatistics = new StoreDistributionSalesStatistics();
	// 产品分销统计
	ProductDistributionSalesStatistics productDistributionSalesStatistics = new ProductDistributionSalesStatistics();

	/**
	 * 店铺销售统计
	 * 
	 * @param storeSalesStatistics
	 */
	public void setStoreSalesStatistics(StoreSalesStatistics storeSalesStatistics) {
		if (storeSalesStatistics != null) {
			this.storeSalesStatistics = storeSalesStatistics;
			notifyDataSetChanged();
		}
	}

	/**
	 * 产品销售统计
	 * 
	 * @param productSalesStatistics
	 */
	public void setProductSalesStatistics(ProductSalesStatistics productSalesStatistics) {
		if (productSalesStatistics != null) {
			this.productSalesStatistics = productSalesStatistics;
			notifyDataSetChanged();
		}
	}

	/**
	 * 自销统计
	 * 
	 * @param selfLockingSalesStatistics
	 */
	public void setSelfLockingSalesStatistics(SelfLockingSalesStatistics selfLockingSalesStatistics) {
		if (selfLockingSalesStatistics != null) {
			this.selfLockingSalesStatistics = selfLockingSalesStatistics;
			notifyDataSetChanged();
		}
	}

	/**
	 * 店铺总销
	 * 
	 * @param storeTotalSalesStatistics
	 */
	public void setStoreTotalSalesStatistics(StoreTotalSalesStatistics storeTotalSalesStatistics) {
		if (storeTotalSalesStatistics != null) {
			this.storeTotalSalesStatistics = storeTotalSalesStatistics;
			notifyDataSetChanged();
		}
	}

	/**
	 * 店铺分销统计
	 * 
	 * @param storeDistributionSalesStatistics
	 */
	public void setStoreDistributionSalesStatistics(StoreDistributionSalesStatistics storeDistributionSalesStatistics) {
		if (storeDistributionSalesStatistics != null) {
			this.storeDistributionSalesStatistics = storeDistributionSalesStatistics;
			notifyDataSetChanged();
		}
	}

	/**
	 * 产品分销统计
	 * 
	 * @param productDistributionSalesStatistics
	 */
	public void setProductDistributionSalesStatistics(ProductDistributionSalesStatistics productDistributionSalesStatistics) {
		if (productDistributionSalesStatistics != null) {
			this.productDistributionSalesStatistics = productDistributionSalesStatistics;
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		if (getStatisticsType() == StatisticsType.STATISTICS_STORESALESTATISTICS.type) { // 店铺销售统计
			List<com.kooniao.travel.model.StoreSalesStatistics.Data> datas = storeSalesStatistics.getDataList();
			return datas == null ? 0 : datas.size();
		} else if (getStatisticsType() == StatisticsType.STATISTICS_PRODUCTSALESTATISTICS.type) { // 产品销售统计
			List<com.kooniao.travel.model.ProductSalesStatistics.Data> datas = productSalesStatistics.getDataList();
			return datas == null ? 0 : datas.size();
		} else if (getStatisticsType() == StatisticsType.STATISTICS_SELFSALESTATISTICS.type) { // 自销统计
			List<com.kooniao.travel.model.SelfLockingSalesStatistics.Data> datas = selfLockingSalesStatistics.getDataList();
			return datas == null ? 0 : datas.size();
		} else if (getStatisticsType() == StatisticsType.STATISTICS_STORETOTALSALESTATISTICS.type) { // 店铺总销
			List<com.kooniao.travel.model.StoreTotalSalesStatistics.Data> datas = storeTotalSalesStatistics.getDataList();
			return datas == null ? 0 : datas.size();
		} else if (getStatisticsType() == StatisticsType.STATISTICS_STOREDISTRIBUTIONSALESTATISTICS.type) { // 店铺分销统计
			List<com.kooniao.travel.model.StoreDistributionSalesStatistics.Data> datas = storeDistributionSalesStatistics.getDataList();
			return datas == null ? 0 : datas.size();
		} else if (getStatisticsType() == StatisticsType.STATISTICS_PRODUCTDISTRIBUTIONSALESTATISTICSTYPE.type) { // 产品分销统计
			List<com.kooniao.travel.model.ProductDistributionSalesStatistics.Data> datas = productDistributionSalesStatistics.getDataList();
			return datas == null ? 0 : datas.size();
		}
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		if (getStatisticsType() == StatisticsType.STATISTICS_STORESALESTATISTICS.type) { // 店铺销售统计
			return storeSalesStatistics.isStore() ? 1 : 0;
		} else if (getStatisticsType() == StatisticsType.STATISTICS_PRODUCTSALESTATISTICS.type) { // 产品销售统计
			return productSalesStatistics.isStore() ? 1 : 0;
		} else if (getStatisticsType() == StatisticsType.STATISTICS_SELFSALESTATISTICS.type) { // 自销统计
			return selfLockingSalesStatistics.isStore() ? 1 : 0;
		} else if (getStatisticsType() == StatisticsType.STATISTICS_STORETOTALSALESTATISTICS.type) { // 店铺总销
			return storeTotalSalesStatistics.isStore() ? 1 : 0;
		} else if (getStatisticsType() == StatisticsType.STATISTICS_STOREDISTRIBUTIONSALESTATISTICS.type) { // 店铺分销统计
			return storeDistributionSalesStatistics.isStore() ? 1 : 0;
		} else if (getStatisticsType() == StatisticsType.STATISTICS_PRODUCTDISTRIBUTIONSALESTATISTICSTYPE.type) { // 产品分销统计
			return productDistributionSalesStatistics.isStore() ? 1 : 0;
		}
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public Object getItem(int position) {
		if (getStatisticsType() == StatisticsType.STATISTICS_STORESALESTATISTICS.type) { // 店铺销售统计
			List<com.kooniao.travel.model.StoreSalesStatistics.Data> datas = storeSalesStatistics.getDataList();
			return datas == null ? 0 : datas.get(position);
		} else if (getStatisticsType() == StatisticsType.STATISTICS_PRODUCTSALESTATISTICS.type) { // 产品销售统计
			List<com.kooniao.travel.model.ProductSalesStatistics.Data> datas = productSalesStatistics.getDataList();
			return datas == null ? 0 : datas.get(position);
		} else if (getStatisticsType() == StatisticsType.STATISTICS_SELFSALESTATISTICS.type) { // 自销统计
			List<com.kooniao.travel.model.SelfLockingSalesStatistics.Data> datas = selfLockingSalesStatistics.getDataList();
			return datas == null ? 0 : datas.get(position);
		} else if (getStatisticsType() == StatisticsType.STATISTICS_STORETOTALSALESTATISTICS.type) { // 店铺总销
			List<com.kooniao.travel.model.StoreTotalSalesStatistics.Data> datas = storeTotalSalesStatistics.getDataList();
			return datas == null ? 0 : datas.get(position);
		} else if (getStatisticsType() == StatisticsType.STATISTICS_STOREDISTRIBUTIONSALESTATISTICS.type) { // 店铺分销统计
			List<com.kooniao.travel.model.StoreDistributionSalesStatistics.Data> datas = storeDistributionSalesStatistics.getDataList();
			return datas == null ? 0 : datas.get(position);
		} else if (getStatisticsType() == StatisticsType.STATISTICS_PRODUCTDISTRIBUTIONSALESTATISTICSTYPE.type) { // 产品分销统计
			List<com.kooniao.travel.model.ProductDistributionSalesStatistics.Data> datas = productDistributionSalesStatistics.getDataList();
			return datas == null ? 0 : datas.get(position);
		}
		return 0;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		switch (getItemViewType(position)) {
		case 0: // 统计产品
			// 产品销售统计、自销统计、店铺总销、产品分销统计
			ProductItemViewHolder productItemViewHolder = null;
			if (convertView == null) {
				productItemViewHolder = new ProductItemViewHolder();
				convertView = layoutInflater.inflate(R.layout.item_statistics_product, null);
				productItemViewHolder.coverImageView = (ImageView) convertView.findViewById(R.id.iv_product_cover_img);
				productItemViewHolder.productNameTextView = (TextView) convertView.findViewById(R.id.tv_product_name);
				productItemViewHolder.productTypeTextView = (TextView) convertView.findViewById(R.id.tv_product_type);
				productItemViewHolder.storeNameTextView = (TextView) convertView.findViewById(R.id.tv_product_resource);
				productItemViewHolder.orderCountTitleTextView = (TextView) convertView.findViewById(R.id.count_title);
				productItemViewHolder.orderCountTextView = (TextView) convertView.findViewById(R.id.count);
				productItemViewHolder.commissionCountTitleTextView = (TextView) convertView.findViewById(R.id.money_title);
				productItemViewHolder.commissionCountTextView = (TextView) convertView.findViewById(R.id.money);
				convertView.setTag(productItemViewHolder);
			} else {
				productItemViewHolder = (ProductItemViewHolder) convertView.getTag();
			}

			// 产品封面
			String coverImgUrl = "";
			// 产品名称
			String productName = "";
			// 产品类型
			String productType = "";
			// 店铺名称
			String storeName = "";
			// 实际成单量
			String actualOrderamount = "";
			// 实际成交金额
			String actualTurnover = "";

			if (getStatisticsType() == StatisticsType.STATISTICS_PRODUCTSALESTATISTICS.type) { // 产品销售统计
				// 订单数
				productItemViewHolder.orderCountTitleTextView.setText(R.string.order_count);
				// 合计佣金
				productItemViewHolder.commissionCountTitleTextView.setVisibility(View.VISIBLE);
				productItemViewHolder.commissionCountTextView.setVisibility(View.VISIBLE);
				productItemViewHolder.commissionCountTitleTextView.setText(R.string.commission_total);

				List<com.kooniao.travel.model.ProductSalesStatistics.Data> datas = productSalesStatistics.getDataList();
				com.kooniao.travel.model.ProductSalesStatistics.Data data = datas.get(position);
				// 产品封面
				coverImgUrl = data.getLogo();
				// 产品名称
				productName = data.getTitle();
				// 产品类型
				int type = data.getType();
				productType = KooniaoTypeUtil.getStringByType(type);
				// 店铺名称
				storeName = data.getName();
				// 实际成单量
				actualOrderamount = String.valueOf(data.getOrderCount());
				// 实际成交金额
				actualTurnover = String.valueOf(data.getBrokerageCount());

			} else if (getStatisticsType() == StatisticsType.STATISTICS_SELFSALESTATISTICS.type) { // 自销统计
				// 实际成单量
				productItemViewHolder.orderCountTitleTextView.setText(R.string.actual_order_count);
				// 合计佣金
				productItemViewHolder.commissionCountTitleTextView.setVisibility(View.INVISIBLE);
				productItemViewHolder.commissionCountTextView.setVisibility(View.INVISIBLE);

				List<com.kooniao.travel.model.SelfLockingSalesStatistics.Data> datas = selfLockingSalesStatistics.getDataList();
				com.kooniao.travel.model.SelfLockingSalesStatistics.Data data = datas.get(position);
				// 产品封面
				coverImgUrl = data.getImg();
				// 产品名称
				productName = data.getTitle();
				// 产品类型
				int type = data.getType();
				productType = KooniaoTypeUtil.getStringByType(type);
				// 店铺名称
				storeName = "";
				// 实际成单量
				actualOrderamount = String.valueOf(data.getOrderCount());
				// 实际成交金额
				actualTurnover = "";

			} else if (getStatisticsType() == StatisticsType.STATISTICS_STORETOTALSALESTATISTICS.type) { // 店铺总销
				// 实际成单量
				productItemViewHolder.orderCountTitleTextView.setText(R.string.actual_order_count);
				// 实际成交金额
				productItemViewHolder.commissionCountTitleTextView.setVisibility(View.VISIBLE);
				productItemViewHolder.commissionCountTextView.setVisibility(View.VISIBLE);
				productItemViewHolder.commissionCountTitleTextView.setText(R.string.actual_turnover);

				List<com.kooniao.travel.model.StoreTotalSalesStatistics.Data> datas = storeTotalSalesStatistics.getDataList();
				com.kooniao.travel.model.StoreTotalSalesStatistics.Data data = datas.get(position);
				// 产品封面
				coverImgUrl = data.getImg();
				// 产品名称
				productName = data.getTitle();
				// 产品类型
				int type = data.getType();
				productType = KooniaoTypeUtil.getStringByType(type);
				// 店铺名称
				storeName = data.getName();
				// 实际成单量
				actualOrderamount = String.valueOf(data.getOrderCount());
				// 实际成交金额
				actualTurnover = String.valueOf(data.getTurnover()); 

			} else if (getStatisticsType() == StatisticsType.STATISTICS_PRODUCTDISTRIBUTIONSALESTATISTICSTYPE.type) { // 产品分销统计
				// 实际成单量
				productItemViewHolder.orderCountTitleTextView.setText(R.string.actual_order_count);
				// 实际成交金额
				productItemViewHolder.commissionCountTitleTextView.setVisibility(View.INVISIBLE);
				productItemViewHolder.commissionCountTextView.setVisibility(View.INVISIBLE);

				List<com.kooniao.travel.model.ProductDistributionSalesStatistics.Data> datas = productDistributionSalesStatistics.getDataList();
				com.kooniao.travel.model.ProductDistributionSalesStatistics.Data data = datas.get(position);
				// 产品封面
				coverImgUrl = data.getImg();
				// 产品名称
				productName = data.getTitle();
				// 产品类型
				int type = data.getType();
				productType = KooniaoTypeUtil.getStringByType(type);
				// 店铺名称
				storeName = data.getName();
				// 实际成单量
				actualOrderamount = String.valueOf(data.getOrderCount());
				// 实际成交金额
				actualTurnover = String.valueOf(data.getTurnover());
			}

			// 设置数据
			ImageView coverImageView = productItemViewHolder.coverImageView;
			coverImageView.setBackgroundColor(ColorUtil.getRandomColorRes());
			if (itemRequestListener != null) {
				// 请求加载图片
				itemRequestListener.onLoadImgListener(coverImgUrl, coverImageView, false);
			}
			// 产品名称
			productItemViewHolder.productNameTextView.setText(productName);
			// 产品类型
			productItemViewHolder.productTypeTextView.setText(productType);
			// 店铺名称
			productItemViewHolder.storeNameTextView.setText(storeName);
			// 订单数
			productItemViewHolder.orderCountTextView.setText(actualOrderamount);
			// 佣金数
			if (!"".equals(actualTurnover)) {
				actualTurnover = StringUtil.getStringFromR(R.string.rmb) + actualTurnover;
			}
			productItemViewHolder.commissionCountTextView.setText(actualTurnover);

			break;

		case 1: // 统计店铺
			// 店铺销售统计、店铺分销统计
			StoreItemViewHolder storeItemViewHolder = null;
			if (convertView == null) {
				storeItemViewHolder = new StoreItemViewHolder();
				convertView = layoutInflater.inflate(R.layout.item_statistics_store, null);
				storeItemViewHolder.logoImageView = (ImageView) convertView.findViewById(R.id.iv_store_logo);
				storeItemViewHolder.storeNameTextView = (TextView) convertView.findViewById(R.id.tv_store_name);
				storeItemViewHolder.storeContactPhoneTextView = (TextView) convertView.findViewById(R.id.tv_store_contact_phone);
				storeItemViewHolder.orderCountTitleTextView = (TextView) convertView.findViewById(R.id.count_title);
				storeItemViewHolder.orderCountTextView = (TextView) convertView.findViewById(R.id.count);
				storeItemViewHolder.commissionCountTitleTextView = (TextView) convertView.findViewById(R.id.money_title);
				storeItemViewHolder.commissionCountTextView = (TextView) convertView.findViewById(R.id.money);
				convertView.setTag(storeItemViewHolder);
			} else {
				storeItemViewHolder = (StoreItemViewHolder) convertView.getTag();
			}

			// 店铺logo
			String logoUrl = "";
			// 店铺名称
			storeName = "";
			// 店铺联系方式
			String storeContactPhone = "";
			// 订单数、实际成单量
			String orderAmount = "";
			// 合计佣金、实际成交金额
			actualTurnover = "";

			if (getStatisticsType() == StatisticsType.STATISTICS_STORESALESTATISTICS.type) { // 店铺销售统计
				// 订单数
				storeItemViewHolder.orderCountTitleTextView.setText(R.string.order_count);
				// 合计佣金
				storeItemViewHolder.commissionCountTitleTextView.setText(R.string.commission_total);

				List<com.kooniao.travel.model.StoreSalesStatistics.Data> datas = storeSalesStatistics.getDataList();
				com.kooniao.travel.model.StoreSalesStatistics.Data data = datas.get(position);
				// 店铺logo
				logoUrl = data.getLogo();
				// 店铺名称
				storeName = data.getName();
				// 店铺联系方式
				storeContactPhone = data.getMobile();
				// 订单数
				orderAmount = String.valueOf(data.getOrderCount());
				// 合计佣金
				actualTurnover = String.valueOf(data.getBrokerageCount());
			} else if (getStatisticsType() == StatisticsType.STATISTICS_STOREDISTRIBUTIONSALESTATISTICS.type) { // 店铺分销
				// 实际成单量
				storeItemViewHolder.orderCountTitleTextView.setText(R.string.actual_order_count);
				// 实际成交金额
				storeItemViewHolder.commissionCountTitleTextView.setText(R.string.actual_turnover);

				List<com.kooniao.travel.model.StoreDistributionSalesStatistics.Data> datas = storeDistributionSalesStatistics.getDataList();
				com.kooniao.travel.model.StoreDistributionSalesStatistics.Data data = datas.get(position);
				// 店铺logo
				logoUrl = data.getLogo();
				// 店铺名称
				storeName = data.getName();
				// 店铺联系方式
				storeContactPhone = data.getMobile();
				// 订单数
				orderAmount = String.valueOf(data.getOrderCount());
				// 合计佣金
				actualTurnover = String.valueOf(data.getTurnover());
			}

			// 设置数据
			ImageView logoImageView = storeItemViewHolder.logoImageView;
			if (itemRequestListener != null) {
				// 请求加载图片
				itemRequestListener.onLoadImgListener(logoUrl, logoImageView, true);
				// 点击电话
				storeItemViewHolder.storeContactPhoneTextView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String phoneNum = "";
						if (getStatisticsType() == StatisticsType.STATISTICS_STORESALESTATISTICS.type) { // 店铺销售统计
							List<com.kooniao.travel.model.StoreSalesStatistics.Data> datas = storeSalesStatistics.getDataList();
							com.kooniao.travel.model.StoreSalesStatistics.Data data = datas.get(position);
							// 店铺联系方式
							phoneNum = data.getMobile();
						} else if (getStatisticsType() == StatisticsType.STATISTICS_STOREDISTRIBUTIONSALESTATISTICS.type) { // 店铺分销
							List<com.kooniao.travel.model.StoreDistributionSalesStatistics.Data> datas = storeDistributionSalesStatistics.getDataList();
							com.kooniao.travel.model.StoreDistributionSalesStatistics.Data data = datas.get(position);
							// 店铺联系方式
							phoneNum = data.getMobile();
						}
						itemRequestListener.onPhoneClick(phoneNum);
					}
				});
			}
			// 店铺名称
			storeItemViewHolder.storeNameTextView.setText(storeName);
			// 店铺联系电话
			storeItemViewHolder.storeContactPhoneTextView.setText(storeContactPhone);
			storeItemViewHolder.orderCountTextView.setText(orderAmount);
			if (!"".equals(actualTurnover)) {
				actualTurnover = StringUtil.getStringFromR(R.string.rmb) + actualTurnover;
			}
			storeItemViewHolder.commissionCountTextView.setText(actualTurnover);

			break;

		default:
			break;
		}
		
		return convertView;
	}

	/**
	 * 店铺类型item
	 * 
	 * @author ke.wei.quan
	 * 
	 */
	private static class StoreItemViewHolder {
		ImageView logoImageView; // 店铺logo
		TextView storeNameTextView; // 店铺名称
		TextView storeContactPhoneTextView; // 店铺联系电话
		TextView orderCountTitleTextView; // 订单数标题
		TextView orderCountTextView; // 订单数
		TextView commissionCountTitleTextView; // 佣金总计标题
		TextView commissionCountTextView; // 佣金数
	}

	/**
	 * 产品类型item
	 * 
	 * @author ke.wei.quan
	 * 
	 */
	private static class ProductItemViewHolder {
		ImageView coverImageView; // 产品封面
		TextView productNameTextView; // 产品名称
		TextView productTypeTextView; // 产品类型
		TextView storeNameTextView; // 店铺名称
		TextView orderCountTitleTextView; // 订单数标题
		TextView orderCountTextView; // 订单数
		TextView commissionCountTitleTextView; // 佣金总计标题
		TextView commissionCountTextView; // 佣金数
	}

	public interface ListItemRequestListener {
		void onLoadImgListener(String imgUrl, ImageView coverImageView, boolean isAvatar);

		void onStoreClick(int storeId);

		void onPhoneClick(String phoneNum);
	}

	private ListItemRequestListener itemRequestListener;

	public void setOnListItemBaseRequestListener(ListItemRequestListener itemRequestListener) {
		this.itemRequestListener = itemRequestListener;
	}

}
