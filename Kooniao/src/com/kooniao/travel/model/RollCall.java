package com.kooniao.travel.model;

/**
 * 点名列表
 * 
 * @author ke.wei.quan
 * 
 */
public class RollCall {
	private long rollCallTime;
	private int totalNum;
	private int heavenNum;
	private int rollCallID;

	public long getRollCallTime() {
		return rollCallTime;
	}

	public void setRollCallTime(long rollCallTime) {
		this.rollCallTime = rollCallTime;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public int getHeavenNum() {
		return heavenNum;
	}

	public void setHeavenNum(int heavenNum) {
		this.heavenNum = heavenNum;
	}

	public int getRollCallID() {
		return rollCallID;
	}

	public void setRollCallID(int rollCallID) {
		this.rollCallID = rollCallID;
	}

	@Override
	public String toString() {
		return "RollCall [rollCallTime=" + rollCallTime + ", totalNum=" + totalNum + ", heavenNum=" + heavenNum + ", rollCallID=" + rollCallID + "]";
	}

}
