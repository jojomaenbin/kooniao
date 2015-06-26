package com.kooniao.travel.model;

/**
 * 用户可申请的角色
 * 
 * @author ke.wei.quan
 * 
 */
public class Role {
	private int id;
	private String title; // 角色名称
	private String desc; // 角色描述

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return "Role [id=" + id + ", title=" + title + ", desc=" + desc + "]";
	}

}
