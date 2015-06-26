package com.kooniao.travel.model;

import java.io.Serializable;
import java.util.List;

public class DayList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6014408451400074429L;
	private String dayTips; // 备注
	private long dayDate; // 时间
	private String dayTitle; // 标题
	private List<NodeList> nodeList;

	public class NodeList implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8492819339417652166L;
		private String nodeName; // 节点名
		private String nodeType; // 节点类型
		private int nodeId; // 节点id
		private String nodeTime; // 节点时间
		private int nodeTimeStatus;//节点时间为空判断
		private String nodeRemark; // 节点备注
		private float nodeLat; // 纬度
		private float nodeLon; // 纬度

		public String getNodeName() {
			return nodeName;
		}

		public void setNodeName(String nodeName) {
			this.nodeName = nodeName;
		}

		public String getNodeType() {
			return nodeType;
		}

		public void setNodeType(String nodeType) {
			this.nodeType = nodeType;
		}

		public int getNodeId() {
			return nodeId;
		}

		public void setNodeId(int nodeId) {
			this.nodeId = nodeId;
		}

		public String getNodeTime() {
			return nodeTime;
		}

		public void setNodeTime(String nodeTime) {
			this.nodeTime = nodeTime;
		}

		public String getNodeRemark() {
			return nodeRemark;
		}

		public void setNodeRemark(String nodeRemark) {
			this.nodeRemark = nodeRemark;
		}

		public float getNodeLat() {
			return nodeLat;
		}

		public void setNodeLat(float nodeLat) {
			this.nodeLat = nodeLat;
		}

		public float getNodeLon() {
			return nodeLon;
		}

		public void setNodeLon(float nodeLon) {
			this.nodeLon = nodeLon;
		}

		@Override
		public String toString() {
			return "nodeList [nodeName=" + nodeName + ", nodeType=" + nodeType + ", nodeId=" + nodeId + ", nodeTime=" + nodeTime + ", nodeRemark=" + nodeRemark + ", nodeLat=" + nodeLat + ", nodeLon=" + nodeLon + "]";
		}

		public int getNodeTimeStatus() {
			return nodeTimeStatus;
		}

		public void setNodeTimeStatus(int nodeTimeStatus) {
			this.nodeTimeStatus = nodeTimeStatus;
		}

	}

	public String getDayTips() {
		return dayTips;
	}

	public void setDayTips(String dayTips) {
		this.dayTips = dayTips;
	}

	public long getDayDate() {
		return dayDate;
	}

	public void setDayDate(long dayDate) {
		this.dayDate = dayDate;
	}

	public String getDayTitle() {
		return dayTitle;
	}

	public void setDayTitle(String dayTitle) {
		this.dayTitle = dayTitle;
	}

	public List<NodeList> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<NodeList> nodeList) {
		this.nodeList = nodeList;
	}

	@Override
	public String toString() {
		return "dayList [dayTips=" + dayTips + ", dayDate=" + dayDate + ", dayTitle=" + dayTitle + ", nodeList=" + nodeList + "]";
	}

}
