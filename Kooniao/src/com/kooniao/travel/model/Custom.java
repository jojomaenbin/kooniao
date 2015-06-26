package com.kooniao.travel.model;

import java.io.Serializable;
import java.util.List;

/**
 * 自定义节点
 * 
 * @author ke.wei.quan
 * 
 */
public class Custom implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9035512929826395151L;
	private String name; // 名字
	private String tel; // 联系电话
	private String address; // 地址
	private String logo; // 封面图片
	private String description; // 简介
	private List<String> all_attach_list; // 大图列表

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getAll_attach_list() {
		return all_attach_list;
	}

	public void setAll_attach_list(List<String> all_attach_list) {
		this.all_attach_list = all_attach_list;
	}

	@Override
	public String toString() {
		return "Custom [name=" + name + ", tel=" + tel + ", address=" + address + ", logo=" + logo + ", description=" + description + ", all_attach_list=" + all_attach_list + "]";
	}

}
