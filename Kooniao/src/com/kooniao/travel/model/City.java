package com.kooniao.travel.model;

import java.io.Serializable;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.PrimaryKey.AssignType;
import com.litesuits.orm.db.annotation.Table;

/**
 * 城市model
 * 
 * @author ke.wei.quan
 * 
 */
@Table(value = "city") 
public class City implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2493363737195251974L;
	@PrimaryKey(value = AssignType.BY_MYSELF)
	@Column(value = "_id") 
	private int id; // 城市id
	private String name; // 城市名
	private String sortLetters; // 显示数据拼音的首字母
	private boolean isHotCity; // 是否是热门的城市
	private boolean isLocateCity; // 是否是定位出来的城市
	private double lat; // 城市纬度
	private double lon; // 城市经度

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public boolean isHotCity() {
		return isHotCity;
	}

	public boolean isLocateCity() {
		return isLocateCity;
	}

	public void setHotCity(boolean isHotCity) {
		this.isHotCity = isHotCity;
	}

	public void setLocateCity(boolean isLocateCity) {
		this.isLocateCity = isLocateCity;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	@Override
	public String toString() {
		return "City [id=" + id + ", name=" + name + ", sortLetters=" + sortLetters + ", isHotCity=" + isHotCity + ", isLocateCity=" + isLocateCity + ", lat=" + lat + ", lon=" + lon + "]";
	}

}
