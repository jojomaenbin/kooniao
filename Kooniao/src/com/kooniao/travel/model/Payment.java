package com.kooniao.travel.model;

import java.io.Serializable;

/**
 * 支付方式
 * 
 * @author ke.wei.quan
 * @date 2015年6月16日
 * @version 1.0
 *
 */
public class Payment implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1855846097339490943L;
	private int id; // 支付方式id(pay_method数组中的每个对象的参数)
	private String code; // 支付方式code(pay_method数组中的每个对象的参数)
	private String name; // 支付方式的显示名称(pay_method数组中的每个对象的参数)

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
