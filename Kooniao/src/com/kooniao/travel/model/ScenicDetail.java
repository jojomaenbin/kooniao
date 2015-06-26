package com.kooniao.travel.model;

/**
 * 景点详情
 * 
 * @author ke.wei.quan
 * 
 */
public class ScenicDetail extends AroundDetail {
	private String suggested; // 建议时长
	private String bestTime;// 建议游玩时间
	private String openTime;// 开放时间
	private String ticket;// 门票（没有的话不显示，有的话显示，留出接口）

	public String getSuggested() {
		return suggested;
	}

	public void setSuggested(String suggested) {
		this.suggested = suggested;
	}

	public String getBestTime() {
		return bestTime;
	}

	public void setBestTime(String bestTime) {
		this.bestTime = bestTime;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	@Override
	public String toString() {
		return "ScenicDetail [suggested=" + suggested + ", bestTime=" + bestTime + ", openTime=" + openTime + ", ticket=" + ticket + "]";
	}

}
