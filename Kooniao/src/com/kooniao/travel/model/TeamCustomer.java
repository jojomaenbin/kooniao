package com.kooniao.travel.model;

import java.io.Serializable;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.PrimaryKey.AssignType;
import com.litesuits.orm.db.annotation.Table;

/**
 * 参团用户
 * 
 * @author ke.wei.quan
 * 
 */
@Table("teamCustomer")
public class TeamCustomer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1958376247347820733L;
	@PrimaryKey(AssignType.BY_MYSELF)
	@Column("_id")
	private int id;
	private int travelId;
	private String name; // 姓名
	private String tel; // 联系电话
	private String certificate; // 证件号
	private String certificateType; // 证件类型
	private String gender; // 性别
	private String sortLetters; // 显示数据拼音的首字母
	private boolean isSelected; // 是否选择了
	private boolean canSelect; // 是否能选择

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTravelId() {
		return travelId;
	}

	public void setTravelId(int travelId) {
		this.travelId = travelId;
	}

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

	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public String getCertificateType() {
		return certificateType;
	}

	public void setCertificateType(String certificateType) {
		this.certificateType = certificateType;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isCanSelect() {
		return canSelect;
	}

	public void setCanSelect(boolean canSelect) {
		this.canSelect = canSelect;
	}

}
