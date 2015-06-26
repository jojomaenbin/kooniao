package com.kooniao.travel.model;

import java.io.Serializable;
import java.util.List;

/**
 * 类说明
 * @author zheng.zong.di
 * @date 2015年5月25日
 * @version 1.0
 *
 */
public class ProductCategory implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9116403979077119646L;
	public classList classList;
	public class_0 cl0;
	public class_1 cl1;
	public class_2 cl2;
	public long serviceTime;

	public class class_0 implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 8025808765657171062L;
		public String id; // 
		public String bname; // 
		public List<class_1> class_1; //
	}
	public class class_1 implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -4383892974937585204L;
		public String id; // 
		public String bname; // 
		public String pid; // 
		public List<class_2> class_2; // 
	}
	public class class_2 implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2690276426996124769L;
		public String id; // 
		public String bname; // 
		public String pid; // 
	}
	public class classList implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 8020906585031120480L;
		public List<class_0> class_0; // 
	}

}
