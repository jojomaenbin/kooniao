package com.kooniao.travel.model;

/**
 * 收藏
 * 
 * @author ke.wei.quan
 * 
 */
public class Collect {
	/**
	 * 线路
	 */
	private int id;
	private String name;
	private String image;
	private String module;
	/**
	 * 产品
	 */
	private int productId;
	private String productName;
	private String time;
	private int shopId;
	private String shopName;
	private String shopType;

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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getShopId() {
		return shopId;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getShopType() {
		return shopType;
	}

	public void setShopType(String shopType) {
		this.shopType = shopType;
	}

	@Override
	public String toString() {
		return "Collect [id=" + id + ", name=" + name + ", image=" + image + ", module=" + module + ", productId=" + productId + ", productName=" + productName + ", time=" + time + ", shopId=" + shopId + ", shopName=" + shopName + ", shopType=" + shopType + "]";
	}

}
