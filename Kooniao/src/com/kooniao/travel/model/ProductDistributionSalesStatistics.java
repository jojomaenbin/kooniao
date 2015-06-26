package com.kooniao.travel.model;

import java.util.List;

/**
 * 产品分销统计
 * 
 * @author ke.wei.quan
 * 
 */
public class ProductDistributionSalesStatistics {
	private List<Data> dataList;
	private int orderTotal;// 订单总计
	private float turnoverTotal;// 成交金额总计
	private boolean isStore = false; // 是否是店铺

	public class Data {
		private String logo;// 图片
		private String img;// 产品图片
		private String title;// 产品名称
		private String mobile;// 电话
		private int orderCount;// 订单数
		private float turnover;// 成交金额
		private int type; // 产品类型
		private String name; // 分销店铺名称
		private int id; // 店铺id

		public String getLogo() {
			return logo;
		}

		public void setLogo(String logo) {
			this.logo = logo;
		}

		public String getImg() {
			return img;
		}

		public void setImg(String img) {
			this.img = img;
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

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return "Data [logo=" + logo + ", title=" + title + ", mobile=" + mobile + ", orderCount=" + orderCount + ", turnover=" + turnover + ", type=" + type + ", name=" + name + ", id=" + id + "]";
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
		return "ProductDistributionSalesStatistics [dataList=" + dataList + ", orderTotal=" + orderTotal + ", turnoverTotal=" + turnoverTotal + ", isStore=" + isStore + "]";
	}

}
