package com.kooniao.travel.model;

/**
 * 娱乐详情
 * 
 * @author ke.wei.quan
 * 
 */
public class AmusementDetail extends ShoppingDetail {
	private float shop_rate; // 综合评分
	private float environment_rate;// 环境评分
	private float service_rate;// 服务评分

	public float getShop_rate() {
		return shop_rate;
	}

	public void setShop_rate(float shop_rate) {
		this.shop_rate = shop_rate;
	}

	public float getEnvironment_rate() {
		return environment_rate;
	}

	public void setEnvironment_rate(float environment_rate) {
		this.environment_rate = environment_rate;
	}

	public float getService_rate() {
		return service_rate;
	}

	public void setService_rate(float service_rate) {
		this.service_rate = service_rate;
	}

	@Override
	public String toString() {
		return "AmusementDetail [shop_rate=" + shop_rate + ", environment_rate=" + environment_rate + ", service_rate=" + service_rate + "]";
	}

}
