package com.kooniao.travel.model;

/**
 * 店铺消息
 * 
 * @author ke.wei.quan
 * 
 */
public class StoreMessage {
	private int notifyId;// 消息ID
	private String message;// 消息内容
	private int fromUid;// 发送人ID
	private String type;// 消息类型
	private String ctime;// 详细创建时间
	private int operateStatus;// 操作状态
	private int isRead;// 是否已读 0:未读，1：已读
	private long serverTime;// 服务器时间

	public int getNotifyId() {
		return notifyId;
	}

	public void setNotifyId(int notifyId) {
		this.notifyId = notifyId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getFromUid() {
		return fromUid;
	}

	public void setFromUid(int fromUid) {
		this.fromUid = fromUid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public int getOperateStatus() {
		return operateStatus;
	}

	public void setOperateStatus(int operateStatus) {
		this.operateStatus = operateStatus;
	}

	public int getIsRead() {
		return isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}

	public long getServerTime() {
		return serverTime;
	}

	public void setServerTime(long serverTime) {
		this.serverTime = serverTime;
	}

	@Override
	public String toString() {
		return "StoreMessage [notifyId=" + notifyId + ", message=" + message + ", fromUid=" + fromUid + ", type=" + type + ", ctime=" + ctime + ", operateStatus=" + operateStatus + ", isRead=" + isRead + ", serverTime=" + serverTime + "]";
	}

}
