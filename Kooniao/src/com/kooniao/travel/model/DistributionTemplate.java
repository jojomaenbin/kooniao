package com.kooniao.travel.model;

import java.io.Serializable;

/**
 * 分销模板
 * 
 * @author ke.wei.quan
 * @date 2015年5月27日
 * @version 1.0
 *
 */
public class DistributionTemplate implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 504989334886810720L;
	private int id; // 模板id
	private String title;// 模板标题
	private int type;// 佣金类型 1:按百分比;2:直接给出佣金
	private float value;// 百分比/金额
	private int defaultTemplate; // 默认模板 (0:不是、1：是)
	private boolean isSelected; // 是否已经选择

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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public int getDefaultTemplate() {
		return defaultTemplate;
	}

	public void setDefaultTemplate(int defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
