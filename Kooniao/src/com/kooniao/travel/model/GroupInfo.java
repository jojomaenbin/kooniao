package com.kooniao.travel.model;

import java.util.List;

/**
 * 团信息
 * 
 * @author ke.wei.quan
 * 
 */
public class GroupInfo {
	private String teamOrganization; // 出行单位
	private String stime; // 出行时间
	private List<PlanGuide> guide; // 全陪导游
	private List<PlanGuide> localGuide; // 地陪导游
	private List<PlanGuide> driver; // 随行司机
	private String carLicense; // 车牌（加上名称、电话）
	private int adult; // 成人人数
	private int oldMan; // 老人人数
	private int children; // 儿童人数
	private int handicapped; // 残疾人人数
	private int soldier; // 军人人数
	private int teamCount; // 出行人数

	public String getTeamOrganization() {
		return teamOrganization;
	}

	public void setTeamOrganization(String teamOrganization) {
		this.teamOrganization = teamOrganization;
	}

	public String getStime() {
		return stime;
	}

	public void setStime(String stime) {
		this.stime = stime;
	}

	public List<PlanGuide> getGuide() {
		return guide;
	}

	public void setGuide(List<PlanGuide> guide) {
		this.guide = guide;
	}

	public List<PlanGuide> getLocalGuide() {
		return localGuide;
	}

	public void setLocalGuide(List<PlanGuide> localGuide) {
		this.localGuide = localGuide;
	}

	public List<PlanGuide> getDriver() {
		return driver;
	}

	public void setDriver(List<PlanGuide> driver) {
		this.driver = driver;
	}

	public String getCarLicense() {
		return carLicense;
	}

	public void setCarLicense(String carLicense) {
		this.carLicense = carLicense;
	}

	public int getAdult() {
		return adult;
	}

	public void setAdult(int adult) {
		this.adult = adult;
	}

	public int getOldMan() {
		return oldMan;
	}

	public void setOldMan(int oldMan) {
		this.oldMan = oldMan;
	}

	public int getChildren() {
		return children;
	}

	public void setChildren(int children) {
		this.children = children;
	}

	public int getHandicapped() {
		return handicapped;
	}

	public void setHandicapped(int handicapped) {
		this.handicapped = handicapped;
	}

	public int getSoldier() {
		return soldier;
	}

	public void setSoldier(int soldier) {
		this.soldier = soldier;
	}

	public int getTeamCount() {
		return teamCount;
	}

	public void setTeamCount(int teamCount) {
		this.teamCount = teamCount;
	}

	@Override
	public String toString() {
		return "GroupInfo [teamOrganization=" + teamOrganization + ", stime=" + stime + ", guide=" + guide + ", localGuide=" + localGuide + ", driver=" + driver + ", carLicense=" + carLicense + ", adult=" + adult + ", oldMan=" + oldMan + ", children=" + children + ", handicapped=" + handicapped + ", soldier=" + soldier + ", teamCount=" + teamCount + "]";
	}

}
