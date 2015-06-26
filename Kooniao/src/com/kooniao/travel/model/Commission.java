package com.kooniao.travel.model;

/**
 * 佣金
 * 
 * @author ke.wei.quan
 * 
 */
public class Commission {

	private int shopId;// 店铺id
	private String shopLogo;// 店铺logo
	private String shopName;// 店铺名称
	private String mobile;// 店铺电话
	private int orderCount;// 订单数
	private float brokerageCount;// 合计佣金
	private float unDefrayMoney;// 待付佣金

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

	public float getBrokerageCount() {
		return brokerageCount;
	}

	public void setBrokerageCount(float brokerageCount) {
		this.brokerageCount = brokerageCount;
	}

	public float getUnDefrayMoney() {
		return unDefrayMoney;
	}

	public void setUnDefrayMoney(float unDefrayMoney) {
		this.unDefrayMoney = unDefrayMoney;
	}

	@Override
	public String toString() {
		return "Commission [shopId=" + shopId + ", shopLogo=" + shopLogo + ", shopName=" + shopName + ", mobile=" + mobile + ", orderCount=" + orderCount + ", brokerageCount=" + brokerageCount + ", unDefrayMoney=" + unDefrayMoney + "]";
	}

}
