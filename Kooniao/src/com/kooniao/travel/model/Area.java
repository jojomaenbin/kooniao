package com.kooniao.travel.model;

import java.io.Serializable;

/**
 * 类说明
 * @author zheng.zong.di
 * @date 2015年6月11日
 * @version 1.0
 *
 */
public class Area implements Serializable {
	public class City implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 7901898254463638760L;
		public int id;
		public String bname;
	}

	private static final long serialVersionUID = -8313807132512869783L;
	public City city_id=new City();
	public City province_id;
	public City country_id=new City();
	public String area_name;
	
}
