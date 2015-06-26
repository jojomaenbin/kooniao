package com.kooniao.travel.model;

/**
 * 离线更新消息
 * 
 * @author ke.wei.quan
 * 
 */
public class OfflineUpdateInfo {
	private int planId; // 离线行程id
	private long mtime; // 更新时间

	public int getPlanId() {
		return planId;
	}

	public void setPlanId(int planId) {
		this.planId = planId;
	}

	public long getMtime() {
		return mtime;
	}

	public void setMtime(long mtime) {
		this.mtime = mtime;
	}

	@Override
	public String toString() {
		return "OfflineUpdateInfo [planId=" + planId + ", mtime=" + mtime + "]";
	}

}
