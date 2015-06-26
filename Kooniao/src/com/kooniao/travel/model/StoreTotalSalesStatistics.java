package com.kooniao.travel.model;

import java.util.List;

/**
 * 店铺总销统计
 * 
 * @author ke.wei.quan
 * 
 */
public class StoreTotalSalesStatistics {
	private List<Data> dataList;
	private int orderTotal;// 订单总计
	private float turnoverTotal;// 成交金额总计
	private boolean isStore = false; // 是否是店铺

	public class Data {
		private String img;// 图片
		private String title;// 标题
		private int type;// 产品类型
		private int orderCount;// 订单数
		private String name; // 店铺名称
		private float turnover; // 实际成交金额
		private int id; // 店铺id

		public String getImg() {
			return img;
		}

		public void setImg(String img) {
			this.img = img;
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

		public int getOrderCount() {
			return orderCount;
		}

		public void setOrderCount(int orderCount) {
			this.orderCount = orderCount;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public float getTurnover() {
			return turnover;
		}

		public void setTurnover(float turnover) {
			this.turnover = turnover;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		@Override
		public String toString() {
			return "Data [img=" + img + ", title=" + title + ", type=" + type + ", orderCount=" + orderCount + ", name=" + name + ", turnover=" + turnover + ", id=" + id + "]";
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
		return "StoreTotalSalesStatistics [dataList=" + dataList + ", orderTotal=" + orderTotal + ", turnoverTotal=" + turnoverTotal + ", isStore=" + isStore + "]";
	}

}
