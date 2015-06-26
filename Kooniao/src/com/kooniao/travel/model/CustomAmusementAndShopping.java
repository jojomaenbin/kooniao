package com.kooniao.travel.model;

/**
 * 自定义娱乐、购物
 * 
 * @author ke.wei.quan
 * 
 */
public class CustomAmusementAndShopping extends Custom {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1422420054033153182L;
	private float ave_cost; // 人均消费
	private String overview; // 推荐理由
	private String traffic_info; // 交通信息

	public float getAve_cost() {
		return ave_cost;
	}

	public void setAve_cost(float ave_cost) {
		this.ave_cost = ave_cost;
	}

	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public String getTraffic_info() {
		return traffic_info;
	}

	public void setTraffic_info(String traffic_info) {
		this.traffic_info = traffic_info;
	}

	@Override
	public String toString() {
		return "CustomAmusementShopping [ave_cost=" + ave_cost + ", overview=" + overview + ", traffic_info=" + traffic_info + "]";
	}

}
