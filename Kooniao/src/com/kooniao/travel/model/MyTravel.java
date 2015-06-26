package com.kooniao.travel.model;

/**
 * 我的行程
 * 
 * @author ke.wei.quan
 * 
 */
public class MyTravel {
	private long startTime; // 出发时间
	private float rank; // 评分
	private String startCity; // 出发城市
	private float price; // 行程价格
	private String name; // 行程名称
	private int current; // 是否当前行程(1:是)
	private String image; // 封面
	private String planState; // 行程状态
	private int permission; // 行程权限
	private int planId; // 行程id
	private int teamId; // 团单id
	private int offlineDownloadState; // 离线行程下载状态(-1:下载失败，0:没有下载,1:下载中,2:下载成功,3:请稍后)
	private boolean receiveTravelRemind=true;//是否闹钟提醒

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public float getRank() {
		return rank;
	}

	public void setRank(float rank) {
		this.rank = rank;
	}

	public String getStartCity() {
		return startCity;
	}

	public void setStartCity(String startCity) {
		this.startCity = startCity;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getPlanState() {
		return planState;
	}

	public void setPlanState(String planState) {
		this.planState = planState;
	}

	public int getPermission() {
		return permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	public int getPlanId() {
		return planId;
	}

	public void setPlanId(int planId) {
		this.planId = planId;
	}

	public int getTeamId() {
		return teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

	public int getOfflineDownloadState() {
		return offlineDownloadState;
	}

	public void setOfflineDownloadState(int offlineDownloadState) {
		this.offlineDownloadState = offlineDownloadState;
	}

	@Override
	public String toString() {
		return "MyTravel [startTime=" + startTime + ", rank=" + rank + ", startCity=" + startCity + ", price=" + price + ", name=" + name + ", current=" + current + ", image=" + image + ", planState=" + planState + ", permission=" + permission + ", planId=" + planId + ", teamId=" + teamId + ", offlineDownloadState=" + offlineDownloadState + "]";
	}

	public boolean isReceiveTravelRemind() {
		return receiveTravelRemind;
	}

	public void setReceiveTravelRemind(boolean receiveTravelRemind) {
		this.receiveTravelRemind = receiveTravelRemind;
	}

}
