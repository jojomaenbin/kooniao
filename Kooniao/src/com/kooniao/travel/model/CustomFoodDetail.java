package com.kooniao.travel.model;

/**
 * 自定义美食
 * 
 * @author ke.wei.quan
 * 
 */
public class CustomFoodDetail extends Custom {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1417756913645741576L;
	private float ave_cost; // 人均消费
	private String open_time; // 营业时间
	private String sm_type; // 美食分类标签
	private String overview; // 推荐理由
	private String traffic_info; // 交通信息

	public float getAve_cost() {
		return ave_cost;
	}

	public void setAve_cost(float ave_cost) {
		this.ave_cost = ave_cost;
	}

	public String getOpen_time() {
		return open_time;
	}

	public void setOpen_time(String open_time) {
		this.open_time = open_time;
	}

	public String getSm_type() {
		return sm_type;
	}

	public void setSm_type(String sm_type) {
		this.sm_type = sm_type;
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
		return "CustomFoodDetail [ave_cost=" + ave_cost + ", open_time=" + open_time + ", sm_type=" + sm_type + ", overview=" + overview + ", traffic_info=" + traffic_info + "]";
	}

}
