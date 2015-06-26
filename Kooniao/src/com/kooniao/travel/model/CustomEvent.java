package com.kooniao.travel.model;

/**
 * 自定义事件
 * 
 * @author ke.wei.quan
 * 
 */
public class CustomEvent extends Custom {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8404774151925080179L;
	private String ave_cost; // 消费价格
	private String playtime; // 建议时长

	public String getAve_cost() {
		return ave_cost;
	}

	public void setAve_cost(String ave_cost) {
		this.ave_cost = ave_cost;
	}

	public String getPlaytime() {
		return playtime;
	}

	public void setPlaytime(String playtime) {
		this.playtime = playtime;
	}

	@Override
	public String toString() {
		return "CustomEvent [ave_cost=" + ave_cost + ", playtime=" + playtime + "]";
	}

}
