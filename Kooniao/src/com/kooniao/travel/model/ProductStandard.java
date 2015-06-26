package com.kooniao.travel.model;

import java.io.Serializable;
import java.util.List;

import com.kooniao.travel.customwidget.ContentDate;

/**
 * 产品规格
 * 
 * @author ke.wei.quan
 * @date 2015年5月26日
 * @lastModifyDate
 * @version 1.0
 *
 */
public class ProductStandard implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4241439108081409676L;
	private int priceId; // 套餐id
	private String type; // 商品规格或套餐名
	private String sale_price_2_c; // 产品现价
	private String unit; // 产品现价单位
	private String is_set_stock_date; // 库存方式,是否设置库存日期数量(0:按总库存 1:设置指定日期库存)
	private List<ContentDate> contentDates; // 每日库存
	private String stock; // 库存总量
	private String deposit_type; // 定金方式,0:关闭,不设置定金类型 1:按金额启用 2:按百分比启用
	private String deposit; // 定金金额
	private String deposit_percent; // 定金百分比
	private int status; // 是否隐藏
	private String comment; // 备注

	public int getPriceId() {
		return priceId;
	}

	public void setPriceId(int priceId) {
		this.priceId = priceId;
	}

	public String getTitle() {
		return type;
	}

	public void setTitle(String type) {
		this.type = type;
	}

	public String getPrice() {
		return sale_price_2_c;
	}

	public void setPrice(String price) {
		this.sale_price_2_c = price;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getInventoryWay() {
		return is_set_stock_date;
	}

	public void setInventoryWay(String inventoryWay) {
		this.is_set_stock_date = inventoryWay;
	}

	public List<ContentDate> getContentDates() {
		return contentDates;
	}

	public void setContentDates(List<ContentDate> contentDates) {
		this.contentDates = contentDates;
	}

	public String getInventoryTotalCount() {
		return stock;
	}

	public void setInventoryTotalCount(String inventoryTotalCount) {
		this.stock = inventoryTotalCount;
	}

	public String getDepositWay() {
		return deposit_type;
	}

	public void setDepositWay(String depositWay) {
		this.deposit_type = depositWay;
	}

	public String getDepositMoney() {
		return deposit;
	}

	public void setDepositMoney(String depositMoney) {
		this.deposit = depositMoney;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDepositPercent() {
		return deposit_percent;
	}

	public void setDepositPercent(String deposit_percent) {
		this.deposit_percent = deposit_percent;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "ProductStandard [priceId=" + priceId + ", type=" + type + ", sale_price_2_c=" + sale_price_2_c + ", unit=" + unit + ", is_set_stock_date=" + is_set_stock_date + ", contentDates=" + contentDates + ", stock=" + stock + ", deposit_type=" + deposit_type + ", deposit=" + deposit + ", deposit_percent=" + deposit_percent + ", status=" + status + ", comment=" + comment + "]";
	}

}
