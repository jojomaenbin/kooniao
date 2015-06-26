package com.kooniao.travel.model;

import java.util.List;

/**
 * 产品销售统计
 * 
 * @author ke.wei.quan
 * 
 */
public class ProductSalesStatistics {

	private List<Data> dataList;
	private String orderTotal;// 订单总计
	private String commissionTotal;// 佣金总计
	private boolean isStore = false; // 是否是店铺

	public class Data {
		private String logo;// 产品图标
		private String title;// 产品名
		private String name;// 店铺名
		private int type;// 产品类型
		private int orderCount;// 订单数
		private float brokerageCount;// 佣金
		private int id; // 店铺id

		public String getLogo() {
			return logo;
		}

		public void setLogo(String logo) {
			this.logo = logo;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getOrderCount() {
			return orderCount;
		}

		public void setOrderCount(int orderCount) {
			this.orderCount = orderCount;
		}

		public float getBrokerageCount() {
			return brokerageCount;
		}

		public void setBrokerageCount(float brokerageCount) {
			this.brokerageCount = brokerageCount;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return "Data [logo=" + logo + ", title=" + title + ", name=" + name + ", type=" + type + ", orderCount=" + orderCount + ", brokerageCount=" + brokerageCount + ", id=" + id + "]";
		}

	}

	public List<Data> getDataList() {
		return dataList;
	}

	public void setDataList(List<Data> dataList) {
		this.dataList = dataList;
	}

	public String getOrderTotal() {
		return orderTotal;
	}

	public void setOrderTotal(String orderTotal) {
		this.orderTotal = orderTotal;
	}

	public String getCommissionTotal() {
		return commissionTotal;
	}

	public void setCommissionTotal(String commissionTotal) {
		this.commissionTotal = commissionTotal;
	}

	public boolean isStore() {
		return isStore;
	}

	public void setStore(boolean isStore) {
		this.isStore = isStore;
	}

	@Override
	public String toString() {
		return "ProductSalesStatistics [dataList=" + dataList + ", orderTotal=" + orderTotal + ", commissionTotal=" + commissionTotal + ", isStore=" + isStore + "]";
	}

}
