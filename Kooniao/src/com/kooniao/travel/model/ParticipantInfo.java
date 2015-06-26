package com.kooniao.travel.model;

import java.io.Serializable;

/**
 * 参与人信息
 * 
 * @author ke.wei.quan
 * 
 */
public class ParticipantInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5149786579722718474L;
	private String name; // 客户姓名
	private String mobile; // 客户手机
	private String idCardType; // 证件类型
	private String idCard; // 证件号码

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getIdCardType() {
		return idCardType;
	}

	public void setIdCardType(String idCardType) {
		this.idCardType = idCardType;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	@Override
	public String toString() {
		return name + mobile + idCardType + idCard;
	}

}
