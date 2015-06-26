package com.kooniao.travel.model;

import java.io.Serializable;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.PrimaryKey.AssignType;
import com.litesuits.orm.db.annotation.Table;

@Table(value = "alarm")
public class Alarm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8127827528576676217L;
	@PrimaryKey(AssignType.AUTO_INCREMENT)
	@Column(value = "_id")
	private int id;
	private int userid;
	private int travelId;
	private int nodeId; // 节点id
	private int alarmCode; // 闹钟code
	private long alarmTime;
	private String alarmTitle;
	private String alarmContent;
	private boolean isBeforeTravel = true;
	private boolean isNodeAlarm = false; // 是否是节点提醒
	private boolean isEnable = true; // 闹钟是否开启
	private String nodeAlarmType; // 节点提醒类型
	private String coverImgPath; // 封面图
//	private long alarmAddedTimeStamp = System.currentTimeMillis(); // 设置闹钟时间

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

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public int getAlarmCode() {
		return alarmCode;
	}

	public void setAlarmCode(int alarmCode) {
		this.alarmCode = alarmCode;
	}

	public long getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(long alarmTime) {
		this.alarmTime = alarmTime;
	}

	public String getAlarmTitle() {
		return alarmTitle;
	}

	public void setAlarmTitle(String alarmTitle) {
		this.alarmTitle = alarmTitle;
	}

	public String getAlarmContent() {
		return alarmContent;
	}

	public void setAlarmContent(String alarmContent) {
		this.alarmContent = alarmContent;
	}

	public boolean isBeforeTravel() {
		return isBeforeTravel;
	}

	public void setBeforeTravel(boolean isBeforeTravel) {
		this.isBeforeTravel = isBeforeTravel;
	}

	public boolean isNodeAlarm() {
		return isNodeAlarm;
	}

	public void setNodeAlarm(boolean isNodeAlarm) {
		this.isNodeAlarm = isNodeAlarm;
	}

	public boolean isEnable() {
		return isEnable;
	}

	public void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}

	public String getNodeAlarmType() {
		return nodeAlarmType;
	}

	public void setNodeAlarmType(String nodeAlarmType) {
		this.nodeAlarmType = nodeAlarmType;
	}

	public String getCoverImgPath() {
		return coverImgPath;
	}

	public void setCoverImgPath(String coverImgPath) {
		this.coverImgPath = coverImgPath;
	}

//	public long getAlarmAddedTimeStamp() {
//		return alarmAddedTimeStamp;
//	}
//
//	public void setAlarmAddedTimeStamp(long alarmAddedTimeStamp) {
//		this.alarmAddedTimeStamp = alarmAddedTimeStamp;
//	}

	@Override
	public String toString() {
		return "Alarm [id=" + id + ", travelId=" + travelId + ", nodeId=" + nodeId + ", alarmCode=" + alarmCode + ", alarmTime=" + alarmTime + ", alarmTitle=" + alarmTitle + ", alarmContent=" + alarmContent + ", isBeforeTravel=" + isBeforeTravel + ", isEnable=" + isEnable + ", nodeAlarmType=" + nodeAlarmType + ", coverImgPath=" + coverImgPath + ", alarmAddedTimeStamp=" + "]";
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

}
