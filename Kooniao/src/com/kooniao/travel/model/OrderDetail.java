package com.kooniao.travel.model;

import java.io.Serializable;
import java.util.List;

/**
 * 产品详细
 * 
 * @author ke.wei.quan
 * 
 */
public class OrderDetail {

	private int orderStatus;// 订单状态
	private String aShopMobile;// A店电话
	private String productName;// 产品名
	private int aShopId;// A店id
	private String aShopName;// A店店名
	private int productCount;// 购买数量
	private float orderCount;// 订单总价
	private String orderSn;// 订单编号
	private String productSku;// 产品编号
	private int productId;// 产品id
	private String orderTime;// 购买时间
	private String startTime;// 出发时间
	private String tripsNumber;// 出游人数(成人xx，儿童xx)
	private String conractName;// 联系名称
	private String conractMobile;// 联系电话
	private String conractEmail;// 联系邮箱
	private List<ParticipantInfo> joinerList;// 出游人信息
	private int cShopId;// C店id
	private String cShopName;// C店名称
	private int productType; // 产品类型("类型： 全部：0 ，线路：4， 组合：2， 酒店：5， 美食：8， 娱乐：7")
	private String bookMethodes;// 预定方式
	private float depositNum;// 订单价格
	private String packageName;// 套餐名称
	private float salePrice;// 套餐单价
	private String productLogo;// 产品logo
	private String aShopLogo;// A店logo
	private int pageCount;// 总页数
	private String cShopLogo;// C店logo
	private int order_status;// "订单操作状态(0:默认 1:未付款2:订单关闭,再次购买 3:支付尾款 4:支付完成)"
	private List<PayMethod> pay_method;// 支付方式列表

	public class PayMethod implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 431914195788572613L;
		private int id;
		private String name;
		private String code;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

	}

	public int getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(int orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getaShopMobile() {
		return aShopMobile;
	}

	public void setaShopMobile(String aShopMobile) {
		this.aShopMobile = aShopMobile;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getaShopId() {
		return aShopId;
	}

	public void setaShopId(int aShopId) {
		this.aShopId = aShopId;
	}

	public String getaShopName() {
		return aShopName;
	}

	public void setaShopName(String aShopName) {
		this.aShopName = aShopName;
	}

	public int getProductCount() {
		return productCount;
	}

	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

	public float getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}

	public String getOrderSn() {
		return orderSn;
	}

	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
	}

	public String getProductSku() {
		return productSku;
	}

	public void setProductSku(String productSku) {
		this.productSku = productSku;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getTripsNumber() {
		return tripsNumber;
	}

	public void setTripsNumber(String tripsNumber) {
		this.tripsNumber = tripsNumber;
	}

	public String getConractName() {
		return conractName;
	}

	public void setConractName(String conractName) {
		this.conractName = conractName;
	}

	public String getConractMobile() {
		return conractMobile;
	}

	public void setConractMobile(String conractMobile) {
		this.conractMobile = conractMobile;
	}

	public String getConractEmail() {
		return conractEmail;
	}

	public void setConractEmail(String conractEmail) {
		this.conractEmail = conractEmail;
	}

	public List<ParticipantInfo> getJoinerList() {
		return joinerList;
	}

	public void setJoinerList(List<ParticipantInfo> joinerList) {
		this.joinerList = joinerList;
	}

	public int getcShopId() {
		return cShopId;
	}

	public void setcShopId(int cShopId) {
		this.cShopId = cShopId;
	}

	public String getcShopName() {
		return cShopName;
	}

	public void setcShopName(String cShopName) {
		this.cShopName = cShopName;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public String getBookMethodes() {
		return bookMethodes;
	}

	public void setBookMethodes(String bookMethodes) {
		this.bookMethodes = bookMethodes;
	}

	public float getDepositNum() {
		return depositNum;
	}

	public void setDepositNum(float depositNum) {
		this.depositNum = depositNum;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setOrderCount(float orderCount) {
		this.orderCount = orderCount;
	}

	public float getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(float salePrice) {
		this.salePrice = salePrice;
	}

	public String getProductLogo() {
		return productLogo;
	}

	public void setProductLogo(String productLogo) {
		this.productLogo = productLogo;
	}

	public String getaShopLogo() {
		return aShopLogo;
	}

	public void setaShopLogo(String aShopLogo) {
		this.aShopLogo = aShopLogo;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public String getcShopLogo() {
		return cShopLogo;
	}

	public void setcShopLogo(String cShopLogo) {
		this.cShopLogo = cShopLogo;
	}

	public int getOrder_status() {
		return order_status;
	}

	public void setOrder_status(int order_status) {
		this.order_status = order_status;
	}

	public List<PayMethod> getPay_method() {
		return pay_method;
	}

	public void setPay_method(List<PayMethod> pay_method) {
		this.pay_method = pay_method;
	}

	@Override
	public String toString() {
		return "OrderDetail [orderStatus=" + orderStatus + ", aShopMobile=" + aShopMobile + ", productName=" + productName + ", aShopId=" + aShopId + ", aShopName=" + aShopName + ", productCount=" + productCount + ", orderCount=" + orderCount + ", orderSn=" + orderSn + ", productSku=" + productSku + ", productId=" + productId + ", orderTime=" + orderTime + ", startTime=" + startTime + ", tripsNumber=" + tripsNumber + ", conractName=" + conractName + ", conractMobile=" + conractMobile + ", conractEmail=" + conractEmail + ", joinerList=" + joinerList + ", cShopId=" + cShopId + ", cShopName=" + cShopName + ", productType=" + productType + "]";
	}

}
