package com.kooniao.travel.model;

/**
 * 订单
 * 
 * @author ke.wei.quan
 * 
 */
public class Order {
	private int orderId; // 订单id
	private String orderNumber; // 订单号
	private String orderTime; // 订单时间
	private int orderStatus; // 订单状态。0：未处理；1:已确认；-1：取消；-2：关闭；2：已收款；3：已退订；4：已出团；5：部分收款
	private String orderTotal; // 订单总价
	private String orderShop; // 店铺名称
	private int orderType; // 订单类型(0:自销， 1:分销)
	private String productName; // 产品名称
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

	public String getOrderShop() {
		return orderShop;
	}

	public void setOrderShop(String orderShop) {
		this.orderShop = orderShop;
	}

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductLogo() {
		return productLogo;
	}

	public void setProductLogo(String producLogo) {
		this.productLogo = producLogo;
	}

	public int getProductCount() {
		return productCount;
	}

	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

	@Override
	public String toString() {
		return "Order [orderId=" + orderId + ", orderNumber=" + orderNumber + ", orderTime=" + orderTime + ", orderStatus=" + orderStatus + ", orderTotal=" + orderTotal + ", orderShop=" + orderShop
				+ ", orderType=" + orderType + ", productName=" + productName + ", producLogo=" + productLogo + ", productCount=" + productCount + "]";
	}

}
