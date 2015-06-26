package com.kooniao.travel.model;

/**
 * 美食详情
 * 
 * @author ke.wei.quan
 * 
 */
public class FoodDetail extends AroundDetail {
	private float price;// 人均消费
	private String recommend;// 推荐菜

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getRecommend() {
		return recommend;
	}

	public void setRecommend(String recommend) {
		this.recommend = recommend;
	}

	@Override
	public String toString() {
		return "FoodDetail [price=" + price + ", recommend=" + recommend + "]";
	}

}
