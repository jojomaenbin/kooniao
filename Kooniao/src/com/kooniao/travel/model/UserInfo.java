package com.kooniao.travel.model;

import java.io.Serializable;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.PrimaryKey.AssignType;

/**
 * 用户资料
 * 
 * @author ke.wei.quan
 * 
 */
@Table(value = "userInfo") 
public class UserInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1300255463805522645L;
	@PrimaryKey(value = AssignType.BY_MYSELF)
	@Column(value = "_uid") 
	private int uid;// 用户ID
	private String ApiKey;
	private String ApiKeySecret;
	private String uname;// 站内昵称
	private long ctime;// 资料的更新时间
	private int sex;// 性别（0.女，1.男）
	private String mobile;// 手机号码
	private String email;// 邮箱
	private String city;// 带团区域
	private int planNum;// 行程数目
	private int favoriteNum;// 收藏数目
	private int newNotifyNum;// 新通知数目
	private String face;// 头像url
	private int userType;// 用户角色(0,游客，1，其他角色)
	private String userProfession;// 角色名称
	private int shopA;// 销售店id
	private int shopC;// 分销店id

	public String getApiKey() {
		return ApiKey;
	}

	public void setApiKey(String apiKey) {
		ApiKey = apiKey;
	}

	public String getApiKeySecret() {
		return ApiKeySecret;
	}

	public void setApiKeySecret(String apiKeySecret) {
		ApiKeySecret = apiKeySecret;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public long getCtime() {
		return ctime;
	}

	public void setCtime(long ctime) {
		this.ctime = ctime;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getPlanNum() {
		return planNum;
	}

	public void setPlanNum(int planNum) {
		this.planNum = planNum;
	}

	public int getFavoriteNum() {
		return favoriteNum;
	}

	public void setFavoriteNum(int favoriteNum) {
		this.favoriteNum = favoriteNum;
	}

	public int getNewNotifyNum() {
		return newNotifyNum;
	}

	public void setNewNotifyNum(int newNotifyNum) {
		this.newNotifyNum = newNotifyNum;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public String getUserProfession() {
		return userProfession;
	}

	public void setUserProfession(String userProfession) {
		this.userProfession = userProfession;
	}

	public int getShopA() {
		return shopA;
	}

	public void setShopA(int shopA) {
		this.shopA = shopA;
	}

	public int getShopC() {
		return shopC;
	}

	public void setShopC(int shopC) {
		this.shopC = shopC;
	}

	@Override
	public String toString() {
		return "UserInfo [uid=" + uid + ", ApiKey=" + ApiKey + ", ApiKeySecret=" + ApiKeySecret + ", uname=" + uname + ", ctime=" + ctime + ", sex=" + sex + ", mobile=" + mobile + ", email=" + email + ", city=" + city + ", planNum=" + planNum + ", favoriteNum=" + favoriteNum + ", newNotifyNum=" + newNotifyNum + ", face=" + face + ", userType=" + userType + ", userProfession=" + userProfession + ", shopA=" + shopA + ", shopC=" + shopC + "]";
	}

}
