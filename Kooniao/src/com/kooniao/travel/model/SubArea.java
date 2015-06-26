package com.kooniao.travel.model;

import java.io.Serializable;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.PrimaryKey.AssignType;

/**
 * 子地区
 * 
 * @author WEIGUANG
 * @date 2015年5月21日
 * @lastModifyDate
 * @version 1.0
 *
 */
@Table(value = "subArea")
public class SubArea implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6638973183912950599L;
	private String parent_id;
	private String parent_name;
	@PrimaryKey(value = AssignType.BY_MYSELF)
	@Column(value = "_id")
	private String area_id;
	private String area_name;
	private String short_spell_name;
	private String sort_letter;
	private String category;

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public String getParent_name() {
		return parent_name;
	}

	public void setParent_name(String parent_name) {
		this.parent_name = parent_name;
	}

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

	@Override
	public String toString() {
		return "SubArea [parent_id=" + parent_id + ", parent_name=" + parent_name + ", area_id=" + area_id + ", area_name=" + area_name + ", short_spell_name=" + short_spell_name + ", sort_letter=" + sort_letter + ", category=" + category + "]";
	}

}
