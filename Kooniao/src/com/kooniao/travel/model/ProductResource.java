package com.kooniao.travel.model;

import java.io.Serializable;

/**
 * 产品资源
 * 
 * @author ke.wei.quan
 * @date 2015年5月22日
 *
 */
public class ProductResource implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1496253518993789010L;
	private int pid; // 产品id
	private String title; // 产品名
	private String productPrice;// 产品价格
	private int productType;// 产品类型
	private String logo;// 产品图片
	private String name;// 店铺名称
	private boolean isAdded; // 是否已经添加

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}

	public int getProductType() {
		return productType;
	}

	public void setProductType(int productType) {
		this.productType = productType;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAdded() {
		return isAdded;
	}

	public void setAdded(boolean isAdded) {
		this.isAdded = isAdded;
	}

	@Override
	public String toString() {
		return "ProductResource [pid=" + pid + ", title=" + title + ", productPrice=" + productPrice + ", productType=" + productType + ", logo=" + logo + ", name=" + name + ", isAdded=" + isAdded + "]";
	}

}
