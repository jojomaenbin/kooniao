package com.kooniao.travel.model;

import java.io.Serializable;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.PrimaryKey.AssignType;

@Table(value = "userTravel") 
public class UserTravel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 606410419044253312L;
	@PrimaryKey(value = AssignType.BY_MYSELF)
	@Column(value = "_userid") 
	private int userid; // 用户id
	private int travelid;//用户当前行程id
	private long travelbengin;//用户当前行程开始时间
	private long travelend;//用户当前行程结束时间
	private boolean receiveTravelRemind=true;//是否闹钟提醒
	private boolean travelable=false;//是否有当前行程
	
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getTravelid() {
		return travelid;
	}
	public void setTravelid(int travelid) {
		this.travelid = travelid;
	}
	public long getTravelbengin() {
		return travelbengin;
	}
	public void setTravelbengin(long travelbengin) {
		this.travelbengin = travelbengin;
	}
	public long getTravelend() {
		return travelend;
	}
	public void setTravelend(long travelend) {
		this.travelend = travelend;
	}
	public boolean isReceiveTravelRemind() {
		return receiveTravelRemind;
	}
	public void setReceiveTravelRemind(boolean receiveTravelRemind) {
		this.receiveTravelRemind = receiveTravelRemind;
	}
	public boolean isTravelable() {
		return travelable;
	}
	public void setTravelable(boolean travelable) {
		this.travelable = travelable;
	}
}
