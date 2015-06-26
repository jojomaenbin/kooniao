package com.kooniao.travel.model;

import java.util.List;

/**
 * 佣金详细
 * 
 * @author ke.wei.quan
 * 
 */
public class CommissionDetail {

	private CommissionInfo commissionInfo; // 佣金简单信息
	private ShopInfo shopInfo; // 店铺信息
	private List<CommissionRecord> commissionList;// 佣金记录

	public class ShopInfo {
		private String logo;
		private String mobile;
		private String name;

		public String getLogo() {
			return logo;
		}

		public void setLogo(String logo) {
			this.logo = logo;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "ShopInfo [logo=" + logo + ", mobile=" + mobile + ", name=" + name + "]";
		}

	}

	public class CommissionInfo {
		private int orderCount;// 订单数
		private float brokerageCount;// 合计佣金
		private float paied;// 已结算佣金
		private float unpaied;// 未结算佣金

		public int getOrderCount() {
			return orderCount;
		}

		public void setOrderCount(int orderCount) {
			this.orderCount = orderCount;
		}

		public float getBrokerageCount() {
			return brokerageCount;
		}

		public void setBrokerageCount(int brokerageCount) {
			this.brokerageCount = brokerageCount;
		}

		public float getPaied() {
			return paied;
		}

		public void setPaied(float paied) {
			this.paied = paied;
		}

		public float getUnpaied() {
			return unpaied;
		}

		public void setUnpaied(float unpaied) {
			this.unpaied = unpaied;
		}
	}

	public class CommissionRecord {
		private String time;// 支付日期
		private int mode;// 支付方式 1线下支付，2系统入账
		private float money;// 支付金额
		private String remark;// 支付备注

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public int getMode() {
			return mode;
		}

		public void setMode(int mode) {
			this.mode = mode;
		}

		public float getMoney() {
			return money;
		}

		public void setMoney(float money) {
			this.money = money;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		@Override
		public String toString() {
			return "CommissionRecord [time=" + time + ", mode=" + mode + ", money=" + money + ", remark=" + remark + "]";
		}

	}

	public CommissionInfo getCommissionInfo() {
		return commissionInfo;
	}

	public void setCommissionInfo(CommissionInfo commissionInfo) {
		this.commissionInfo = commissionInfo;
	}

	public ShopInfo getShopInfo() {
		return shopInfo;
	}

	public void setShopInfo(ShopInfo shopInfo) {
		this.shopInfo = shopInfo;
	}

	public List<CommissionRecord> getCommissionList() {
		return commissionList;
	}

	public void setCommissionList(List<CommissionRecord> commissionList) {
		this.commissionList = commissionList;
	}

	@Override
	public String toString() {
		return "CommissionDetail [commissionInfo=" + commissionInfo + ", shopInfo=" + shopInfo + ", commissionList=" + commissionList + "]";
	}

}
