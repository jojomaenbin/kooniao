package com.kooniao.travel.model;

import java.util.List;

/**
 * 当前行程
 * 
 * @author ke.wei.quan
 * 
 */
public class CurrentTravel {
	private int id;
	private float rank;
	private long time;
	private String title;
	private float price;
	private String image;
	private int permission;
	private List<DayList> dayList;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getRank() {
		return rank;
	}

	public void setRank(float rank) {
		this.rank = rank;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getPermission() {
		return permission;
	}

	public void setPermission(int permission) {
		this.permission = permission;
	}

	public List<DayList> getDayList() {
		return dayList;
	}

	public void setDayList(List<DayList> dayList) {
		this.dayList = dayList;
	}

	@Override
	public String toString() {
		return "CurrentTravel [id=" + id + ", rank=" + rank + ", time=" + time + ", title=" + title + ", price=" + price + ", image=" + image + ", permission=" + permission + ", dayList=" + dayList + "]";
	}

}
