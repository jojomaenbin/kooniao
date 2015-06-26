package com.kooniao.travel.model;

import java.io.Serializable;

/**
 * 类说明
 * @author ZZD
 * @date 2015年6月15日
 * @version 1.0
 *
 */
public class ProductCatalog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7771873236783260848L;
	private int id;
	private String title;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
