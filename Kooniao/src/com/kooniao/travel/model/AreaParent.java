package com.kooniao.travel.model;

import java.io.Serializable;
import java.util.List;

/**
 * 地区model
 * 
 * @author ke.wei.quan
 * @date 2015年5月21日
 *
 */
public class AreaParent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3650380411543105116L;
	private String area_id;
	private String area_name;
	private String short_spell_name;
	private String sort_letter;
	private String category;
	private boolean isSelected; // 是否已经选择
	private List<SubArea> cityList;

	public String getArea_id() {
		return area_id;
	}

	public void setArea_id(String area_id) {
		this.area_id = area_id;
	}

	public String getArea_name() {
		return area_name;
	}

	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}

	public String getShort_spell_name() {
		return short_spell_name;
	}

	public void setShort_spell_name(String short_spell_name) {
		this.short_spell_name = short_spell_name;
	}

	public String getSort_letter() {
		return sort_letter;
	}

	public void setSort_letter(String sort_letter) {
		this.sort_letter = sort_letter;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<SubArea> getCityList() {
		return cityList;
	}

	public void setCityList(List<SubArea> cityList) {
		this.cityList = cityList;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	@Override
	public String toString() {
		return "AreaParent [area_id=" + area_id + ", area_name=" + area_name + ", short_spell_name=" + short_spell_name + ", sort_letter=" + sort_letter + ", category=" + category + ", isSelected=" + isSelected + ", cityList=" + cityList + "]";
	}

}
