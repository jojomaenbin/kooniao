package com.kooniao.travel.model;

import java.util.List;

/**
 * 店铺分销统计
 * 
 * @author ke.wei.quan
 * 
 */
public class StoreDistributionSalesStatistics {

	private List<Data> dataList;
	private int orderTotal;// 订单总计
	private float turnoverTotal;// 成交金额总计
	private boolean isStore = true; // 是否是店铺

	public class Data {
		private String logo;// 图片
		private String name;// 标题
		private String mobile;// 电话
		private int orderCount;// 订单数
		private float turnover;// 成交金额

		public String getLogo() {
			return logo;
		}

		public void setLogo(String logo) {
			this.logo = logo;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public int getOrderCount() {
			return orderCount;
		}

		public void setOrderCount(int orderCount) {
			this.orderCount = orderCount;
		}

		public float getTurnover() {
			return turnover;
		}

		public void setTurnover(float turnover) {
			this.turnover = turnover;
		}

		@Override
		public String toString() {
			return "Data [logo=" + logo + ", name=" + name + ", mobile=" + mobile + ", orderCount=" + orderCount + ", turnover=" + turnover + "]";
		}

	}

	public List<Data> getDataList() {
		return dataList;
	}

	public void setDataList(List<Data> dataList) {
		this.dataList = dataList;
	}

	public int getOrderTotal() {
		return orderTotal;
	}

	public void setOrderTotal(int orderTotal) {
		this.orderTotal = orderTotal;
	}

	public float getTurnoverTotal() {
		return turnoverTotal;
	}

	public void setTurnoverTotal(float turnoverTotal) {
		this.turnoverTotal = turnoverTotal;
	}

	public boolean isStore() {
		return isStore;
	}

	public void setStore(boolean isStore) {
		this.isStore = isStore;
	}

	@Override
	public String toString() {
		return "StoreDistributionSalesStatistics [dataList=" + dataList + ", orderTotal=" + orderTotal + ", turnoverTotal=" + turnoverTotal + ", isStore=" + isStore + "]";
	}

}
