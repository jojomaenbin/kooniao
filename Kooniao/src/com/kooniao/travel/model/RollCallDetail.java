package com.kooniao.travel.model;

import java.io.Serializable;
import java.util.List;

/**
 * 点名名单详情
 * 
 * @author ke.wei.quan
 * 
 */
public class RollCallDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4075856967750055279L;
	private int team_id; // 团单id
	private int rollCallID; // 点名名单id
	private long rollCallTime; // 点名时间
	private List<Tourist> touristList; // 点名人员列表

	public int getTeam_id() {
		return team_id;
	}

	public void setTeam_id(int team_id) {
		this.team_id = team_id;
	}

	public int getRollCallID() {
		return rollCallID;
	}

	public void setRollCallID(int rollCallID) {
		this.rollCallID = rollCallID;
	}

	public long getRollCallTime() {
		return rollCallTime;
	}

	public void setRollCallTime(long rollCallTime) {
		this.rollCallTime = rollCallTime;
	}

	public List<Tourist> getTouristList() {
		return touristList;
	}

	public void setTouristList(List<Tourist> touristList) {
		this.touristList = touristList;
	}

	public static final class Tourist implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2778229004926243338L;
		public String name; // 名字
		public int state; // 点名状态
		public String tel; // 名字

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getState() {
			return state;
		}

		public void setState(int state) {
			this.state = state;
		}

		public String getTel() {
			return tel;
		}

		public void setTel(String tel) {
			this.tel = tel;
		}

		@Override
		public String toString() {
			return "Tourist [name=" + name + ", state=" + state + ", tel=" + tel + "]";
		}

	}

	@Override
	public String toString() {
		return "RollCallDetail [team_id=" + team_id + ", rollCallID=" + rollCallID + ", rollCallTime=" + rollCallTime + ", touristList=" + touristList + "]";
	}

}
