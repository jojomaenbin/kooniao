package com.kooniao.travel.model;

import java.io.Serializable;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.PrimaryKey.AssignType;

/**
 * 产品
 * 
 * @author ke.wei.quan
 * 
 */
@Table(value = "product")
public class Product implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7230933574163998162L;
	@PrimaryKey(value = AssignType.BY_MYSELF)
	@Column(value = "_id")
	private int productId;// 产品id
	private int shopId;// 店铺id
	private String img; // 封面
	private String title;// 标题
	private String shopName;// 店铺名
	private String price;// 价格
	private float rate;// 评分
	private int type;// 产品类型
	private int orderCount;// 购买人数
	private int planType;// 线路类型（10：参团、11：自由行）
	private int brokerageType; // 返佣类型(0：不按百分比，1：按百分比)
	private String brokerage;// 返佣
	private int percentage; // 返佣百分比
	private String affiliate_comment; // 返佣备注

	private boolean isAdded; // 是否已经添加到列表

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getShopId() {
		return shopId;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
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

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
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

	public int getPlanType() {
		return planType;
	}

	public void setPlanType(int planType) {
		this.planType = planType;
	}

	public int getBrokerageType() {
		return brokerageType;
	}

	public void setBrokerageType(int brokerageType) {
		this.brokerageType = brokerageType;
	}

	public String getBrokerage() {
		return brokerage;
	}

	public void setBrokerage(String brokerage) {
		this.brokerage = brokerage;
	}

	public String getAffiliate_comment() {
		return affiliate_comment;
	}

	public void setAffiliate_comment(String affiliate_comment) {
		this.affiliate_comment = affiliate_comment;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public boolean isAdded() {
		return isAdded;
	}

	public void setAdded(boolean isAdded) {
		this.isAdded = isAdded;
	}

	@Override
	public String toString() {
		return "Product [productId=" + productId + ", shopId=" + shopId + ", img=" + img + ", title=" + title + ", shopName=" + shopName + ", price=" + price + ", rate=" + rate + ", type=" + type + ", orderCount=" + orderCount + ", planType=" + planType + ", brokerageType=" + brokerageType + ", brokerage=" + brokerage + ", percentage=" + percentage + ", affiliate_comment=" + affiliate_comment + ", isAdded=" + isAdded + "]";
	}

}
