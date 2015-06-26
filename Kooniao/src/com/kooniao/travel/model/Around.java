package com.kooniao.travel.model;

import java.io.Serializable;

/**
 * 附近
 * 
 * @author ke.wei.quan
 * 
 */
public class Around implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2063867143127238573L;
	/*
	 * "类型：景点 location 酒店 hotel 饮食 lifestyle_food 购物 lifestyle_shopping 娱乐
	 * lifestyle_funny"
	 */
	private String type;
	private int id; // ID：下述类型里的ID
	private int hot;// 热度
	private String name;// 名称（景点、住宿…）
	private float price;// 价格
	private double lon;// 经度（类型中的东东的ID）
	private double lat;// 纬度（类型中的东东的ID）
	private float rank;// 评价(就是右上角的那个总体评分）
	private String img;// 图片url
	private float distance;// 距离（当上传数据位经纬度时）

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHot() {
		return hot;
	}

	public void setHot(int hot) {
		this.hot = hot;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public float getRank() {
		return rank;
	}

	public void setRank(float rank) {
		this.rank = rank;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "Around [type=" + type + ", id=" + id + ", hot=" + hot + ", name=" + name + ", price=" + price + ", lon=" + lon + ", lat=" + lat + ", rank=" + rank + ", img=" + img + ", distance=" + distance + "]";
	}

}
