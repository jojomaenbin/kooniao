package com.kooniao.travel.model;

import java.util.List;

/**
 * 店铺销售统计
 * 
 * @author ke.wei.quan
 * 
 */
public class StoreSalesStatistics {

	private List<Data> dataList;
	private String orderTotal;// 订单总计
	private String commissionTotal;// 成交金额总计
	private boolean isStore = true; // 是否是店铺

	public class Data {
		private String logo;// 图片
		private String name;// 标题
		private String mobile;// 产品类型
		private float brokerageCount;// 合计佣金
		private int orderCount;// 订单数
		private int id; // 店铺id

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

		public float getBrokerageCount() {
			return brokerageCount;
		}

		public void setBrokerageCount(float brokerageCount) {
			this.brokerageCount = brokerageCount;
		}

		public int getOrderCount() {
			return orderCount;
		}

		public void setOrderCount(int orderCount) {
			this.orderCount = orderCount;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return "Data [logo=" + logo + ", name=" + name + ", mobile=" + mobile + ", brokerageCount=" + brokerageCount + ", orderCount=" + orderCount + ", id=" + id + "]";
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
		return "SelfLockingSalesStatistics [dataList=" + dataList + ", orderTotal=" + orderTotal + ", turnoverTotal=" + commissionTotal + ", isStore=" + isStore + "]";
	}

}
