package com.kooniao.travel.model;

import java.util.List;

/**
 * 客户详细
 * 
 * @author ke.wei.quan
 * 
 */
public class CustomerDetail {
	private CustomerInfo customerInfo;
	private List<Order> orderList;// 订单列表

	public class Order {
		private int id;// 订单id
		private int sn;// 订单号
		private int status;// 订单状态
		private String img;// 产品图片
		private String title;// 产品名称
		private String ctime;// 预定时间

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getSn() {
			return sn;
		}

		public void setSn(int sn) {
			this.sn = sn;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

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

		public String getCtime() {
			return ctime;
		}

		public void setCtime(String ctime) {
			this.ctime = ctime;
		}

		@Override
		public String toString() {
			return "Order [id=" + id + ", sn=" + sn + ", status=" + status + ", img=" + img + ", title=" + title + ", ctime=" + ctime + "]";
		}

	}

	public CustomerInfo getCustomerInfo() {
		return customerInfo;
	}

	public void setCustomerInfo(CustomerInfo customerInfo) {
		this.customerInfo = customerInfo;
	}

	public List<Order> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<Order> orderList) {
		this.orderList = orderList;
	}

	@Override
	public String toString() {
		return "CustomerDetail [customerInfo=" + customerInfo + ", orderList=" + orderList + "]";
	}

}
