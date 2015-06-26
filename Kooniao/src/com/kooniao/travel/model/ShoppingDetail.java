package com.kooniao.travel.model;

/**
 * 购物详情
 * 
 * @author ke.wei.quan
 * 
 */
public class ShoppingDetail extends AroundDetail {
	private float price; // 人均消费
	private String tags;// 标签

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "ShoppingDetail [price=" + price + ", tags=" + tags + "]";
	}

}
