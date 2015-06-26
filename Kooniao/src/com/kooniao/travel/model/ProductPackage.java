package com.kooniao.travel.model;

import java.util.List;

/**
 * 产品套餐model
 * 
 * @author ke.wei.quan
 * @date 2015年4月28日
 *
 */
public class ProductPackage {
	private String unit; // 单位
	private String title; // 套餐名
	private int depositType; // 定金类型(0:关闭,不设置定金类型 1:按金额启用 2:按百分比启用)
	private int stock; // 套餐总库存
	private float price; // 套餐价格
	private String deposit; // 定金金额
	private String comment; // 备注
	private int priceId; // 套餐id
	private int depositPercent; // 定金百分比
	private int stockDate; // 是否设置库存日期数量(0:按总库存 1:设置指定日期库存)
	private List<Relation> relationList; // 天库存

	public class Relation {
		private String stockDate; // 天日期
		private int stock; // 天库存

		public String getStockDate() {
			return stockDate;
		}

		public void setStockDate(String stockDate) {
			this.stockDate = stockDate;
		}

		public int getStock() {
			return stock;
		}

		public void setStock(int stock) {
			this.stock = stock;
		}

		@Override
		public String toString() {
			return "Relation [stockDate=" + stockDate + ", stock=" + stock + "]";
		}

	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getDepositType() {
		return depositType;
	}

	public void setDepositType(int depositType) {
		this.depositType = depositType;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getDeposit() {
		return deposit;
	}

	public void setDeposit(String deposit) {
		this.deposit = deposit;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getPriceId() {
		return priceId;
	}

	public void setPriceId(int priceId) {
		this.priceId = priceId;
	}

	public int getDepositPercent() {
		return depositPercent;
	}

	public void setDepositPercent(int depositPercent) {
		this.depositPercent = depositPercent;
	}

	public int getStockDate() {
		return stockDate;
	}

	public void setStockDate(int stockDate) {
		this.stockDate = stockDate;
	}

	public List<Relation> getRelationList() {
		return relationList;
	}

	public void setRelationList(List<Relation> relationList) {
		this.relationList = relationList;
	}

	@Override
	public String toString() {
		return "ProductPackage [unit=" + unit + ", title=" + title + ", depositType=" + depositType + ", stock=" + stock + ", price=" + price + ", deposit=" + deposit + ", comment=" + comment + ", priceId=" + priceId + ", depositPercent=" + depositPercent + ", stockDate=" + stockDate + ", relationList=" + relationList + "]";
	}

}
