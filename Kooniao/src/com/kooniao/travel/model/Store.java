package com.kooniao.travel.model;

import java.io.Serializable;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.PrimaryKey.AssignType;
import com.litesuits.orm.db.annotation.Table;

/**
 * 店铺
 * 
 * @author ke.wei.quan
 * 
 */
@Table(value = "store")
public class Store implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9025842965268715472L;
	@PrimaryKey(value = AssignType.BY_MYSELF)
	@Column(value = "_id")
	private int id; // 用户id
	private String bgImg; // 背景图
	private String logo; // 图标
	private String mobile; // 电话
	private String shopName; // 店铺名
	private String shareUrl; // 店铺分享链接
	private int status; // 店铺状态
	private String stopPrompt; // 店铺暂停提示语
	private String userName; // 店铺用户名

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBgImg() {
		return bgImg;
	}

	public void setBgImg(String bgImg) {
		this.bgImg = bgImg;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getShareUrl() {
		return shareUrl;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStopPrompt() {
		return stopPrompt;
	}

	public void setStopPrompt(String stopPrompt) {
		this.stopPrompt = stopPrompt;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "Store [id=" + id + ", bgImg=" + bgImg + ", logo=" + logo + ", mobile=" + mobile + ", shopName=" + shopName + ", shareUrl=" + shareUrl + ", status=" + status + ", stopPrompt=" + stopPrompt + ", userName=" + userName + "]";
	}

}
