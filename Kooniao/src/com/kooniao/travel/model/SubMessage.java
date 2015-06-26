package com.kooniao.travel.model;

/**
 * 二级消息
 * 
 * @author ke.wei.quan
 * 
 */
public class SubMessage {
	private int id;
	private String type;
	private String title; // (提醒消息)
	private String logo;
	private String name; // (订单、产品、佣金消息)
	private String content;
	private int is_read;
	private long ctime;
	private Params params; // 跳转参数

	public static final class Params {
		public int planId; // 行程id
		public int teamId; // 团单id
		public int orderId; // 订单id
		public int operateStatus; // 导游指派接收结果(2:接收，3:拒绝)
		public int othersShopId; // 佣金详细，查看店铺的店铺id

		public int getPlanId() {
			return planId;
		}

		public void setPlanId(int planId) {
			this.planId = planId;
		}

		public int getTeamId() {
			return teamId;
		}

		public void setTeamId(int teamId) {
			this.teamId = teamId;
		}

		public int getOrderId() {
			return orderId;
		}

		public void setOrderId(int orderId) {
			this.orderId = orderId;
		}

		public int getOperateStatus() {
			return operateStatus;
		}

		public void setOperateStatus(int operateStatus) {
			this.operateStatus = operateStatus;
		}

		public int getOthersShopId() {
			return othersShopId;
		}

		public void setOthersShopId(int othersShopId) {
			this.othersShopId = othersShopId;
		}

		@Override
		public String toString() {
			return "Params [planId=" + planId + ", teamId=" + teamId + ", orderId=" + orderId + ", operateStatus=" + operateStatus + ", othersShopId=" + othersShopId + "]";
		}

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getIs_read() {
		return is_read;
	}

	public void setIs_read(int is_read) {
		this.is_read = is_read;
	}

	public long getCtime() {
		return ctime;
	}

	public void setCtime(long ctime) {
		this.ctime = ctime;
	}

	public Params getParams() {
		return params;
	}

	public void setParams(Params params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "SubMessage [id=" + id + ", type=" + type + ", title=" + title + ", logo=" + logo + ", name=" + name + ", content=" + content + ", is_read=" + is_read + ", ctime=" + ctime + ", params=" + params + "]";
	}

}
