package com.kooniao.travel.model;

/**
 * 我的订单
 * 
 * @author ke.wei.quan
 * 
 */
public class MyOrder {
	private int orderId; // 订单id
	private String orderNumber; // 订单号
	private String orderTime; // 订单时间
	private int orderStatus; // 订单状态。0：未处理；1:已确认；-1：取消；-2：关闭；2：已收款；3：已退订；4：已出团；5：部分收款
	private String orderTotal; // 订单总价
	private int productType; // 产品类型
	private String productName; // 产品名称
	private String shopName; // 店铺名称
	private int shopId; // 店铺id
	private String shopType; // 店铺类型(a/c)
	private String productLogo; // 产品图片
	private int productCount; // 产品数量

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public int getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(int orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOrderTotal() {
		return orderTotal;
	}

	public void setOrderTotal(String orderTotal) {
		this.orderTotal = orderTotal;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public int getShopId() {
		return shopId;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
	}

	public String getShopType() {
		return shopType;
	}

	public void setShopType(String shopType) {
		this.shopType = shopType;
	}

	public String getProductLogo() {
		return productLogo;
	}

	public void setProductLogo(String productLogo) {
		this.productLogo = productLogo;
	}

	public int getProductCount() {
		return productCount;
	}

	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

	@Override
	public String toString() {
		return "MyOrder [orderId=" + orderId + ", orderNumber=" + orderNumber + ", orderTime=" + orderTime + ", orderStatus=" + orderStatus + ", orderTotal=" + orderTotal + ", productType=" + productType + ", productName=" + productName + ", shopName=" + shopName + ", shopId=" + shopId + ", shopType=" + shopType + ", productLogo=" + productLogo + ", productCount=" + productCount + "]";
	}

}
