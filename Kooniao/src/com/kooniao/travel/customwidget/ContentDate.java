package com.kooniao.travel.customwidget;

import java.io.Serializable;

/**
 * 日期内容
 * 
 * @author ke.wei.quan
 * @date 2015年5月28日
 * @lastModifyDate
 * @version 1.0
 *
 */
public class ContentDate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -406639129365755233L;
	private String stock_date;
	private Object stock;

	public String getDate() {
		return stock_date;
	}

	public void setDate(String date) {
		this.stock_date = date;
	}

	public Object getContent() {
		return stock;
	}

	public void setContent(Object content) {
		this.stock = content;
	}
}
