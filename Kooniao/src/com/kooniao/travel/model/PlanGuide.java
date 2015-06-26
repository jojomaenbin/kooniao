package com.kooniao.travel.model;

/**
 * 行程带队
 * 
 * @author ke.wei.quan
 * 
 */
public class PlanGuide {
	private String name; // 姓名
	private String tel; // 电话号码
	private String carLicense;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getCarLicense() {
		return carLicense;
	}

	public void setCarLicense(String carLicense) {
		this.carLicense = carLicense;
	}

	@Override
	public String toString() {
		return "PlanGuide [name=" + name + ", tel=" + tel + ", carLicense=" + carLicense + "]";
	}

}
