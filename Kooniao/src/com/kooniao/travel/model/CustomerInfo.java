package com.kooniao.travel.model;

import java.io.Serializable;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.PrimaryKey.AssignType;

/**
 * 客户信息
 * 
 * @author ke.wei.quan
 * 
 */
@Table(value = "customerInfo")
public class CustomerInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4350028142276199762L;
	@PrimaryKey(value = AssignType.BY_MYSELF)
	@Column(value = "_id")
	private int id;// id
	private String mobile;// 电话
	private String email;// 邮箱
	private String name;// 名称
	private String sortLetters; // 显示数据拼音的首字母

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	@Override
	public String toString() {
		return "CustomerInfo [id=" + id + ", mobile=" + mobile + ", email=" + email + ", name=" + name + ", sortLetters=" + sortLetters + "]";
	}

}
