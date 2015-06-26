package com.kooniao.travel.model;

/**
 * 佣金
 * 
 * @author ke.wei.quan
 * 
 */
public class CommissionManagement {
	private int shopId;// 店铺id
	private String shopLogo;// 店铺logo
	private String shopName;// 店铺名称
	private String mobile;// 店铺电话
	private int orderCount;// 订单数
	private String brokerageCount;// 合计佣金

	public int getShopId() {
		return shopId;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
	}

	public String getShopLogo() {
		return shopLogo;
	}

	public void setShopLogo(String shopLogo) {
		this.shopLogo = shopLogo;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
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

	public String getBrokerageCount() {
		return brokerageCount;
	}

	public void setBrokerageCount(String brokerageCount) {
		this.brokerageCount = brokerageCount;
	}

	@Override
	public String toString() {
		return "CommissionManagement [shopId=" + shopId + ", shopLogo=" + shopLogo + ", shopName=" + shopName + ", mobile=" + mobile + ", orderCount=" + orderCount + ", brokerageCount=" + brokerageCount + "]";
	}

}
