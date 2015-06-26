package com.kooniao.travel.model;

/**
 * 导游
 * 
 * @author ke.wei.quan
 * 
 */
public class Guide {
	private String image;
	private int sex;
	private String name;
	private String area;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	@Override
	public String toString() {
		return "Guide [image=" + image + ", sex=" + sex + ", name=" + name + ", area=" + area + "]";
	}

}
