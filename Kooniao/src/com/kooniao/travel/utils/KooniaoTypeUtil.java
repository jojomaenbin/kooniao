package com.kooniao.travel.utils;

import com.kooniao.travel.R;
import com.kooniao.travel.constant.Define;

/**
 * 类型转换
 * @author ke.wei.quan
 *
 */
public class KooniaoTypeUtil {

	/**
	 * 根据module转换为type
	 * @param module
	 * @return
	 */
	public static int getTypeByModule(String module) {
		if (module == null) {
			return -1;
		}
		
		/**
		 * 产品
		 */
		if (module.equals(Define.PRODUCT_PLAN)) {
			return 4; // 线路产品
		} else if (module.equals(Define.PRODUCT_LOCATION_TICKET_TYPE)) {
			return 6; // 景点门票
		} else if (module.equals(Define.PRODUCT_LIFESTYLE_SHOPPING)) {
			return 9; // 购物优惠
		} else if (module.equals(Define.PRODUCT_LIFESTYLE_ENTERTAINM)) {
			return 7; // 休闲娱乐
		} else if (module.equals(Define.PRODUCT_HOTEL)) {
			return 5; // 酒店
		} else if (module.equals(Define.PRODUCT_LIFESTYLE_FOOD)) {
			return 8; // 餐饮美食
		} else if (module.equals(Define.PRODUCT_COMBINE)) {
			return 2; // 组合产品
		}
		
		return -1;
	}
	
	/**
	 * 根据String转换为type
	 * @param module
	 * @return
	 */
	public static int getTypeByString(String module) {
		if (module == null) {
			return -1;
		}
		
		/**
		 * 产品
		 */
		if (module.equals(StringUtil.getStringFromR(R.string.line))) {
			return 4; // 线路产品
		} else if (module.equals(StringUtil.getStringFromR(R.string.category_scenic))) {
			return 6; // 景点门票
		} else if (module.equals(StringUtil.getStringFromR(R.string.category_shopping))) {
			return 9; // 购物优惠
		} else if (module.equals(StringUtil.getStringFromR(R.string.category_amusement))) {
			return 7; // 休闲娱乐
		} else if (module.equals(StringUtil.getStringFromR(R.string.hotel))) {
			return 5; // 酒店
		} else if (module.equals(StringUtil.getStringFromR(R.string.category_food))) {
			return 8; // 餐饮美食
		} else if (module.equals(StringUtil.getStringFromR(R.string.category_group_product))) {
			return 2; // 组合产品
		}
		
		return 0;
	}
	
	/**
	 * 根据String转换为type
	 * @param module
	 * @return
	 */
	public static int getProductStatusByString(String module) {
		if (module == null) {
			return -1;
		}
		
		/**
		 * 全部分类
		 */
		if (module.equals(StringUtil.getStringFromR(R.string.status_all))) {
			return 0; // 全部
		} else if (module.equals(StringUtil.getStringFromR(R.string.status_in_the_sale))) {
			return 1; // 出售中
		} else if (module.equals(StringUtil.getStringFromR(R.string.status_have_the_shelf))) {
			return 3; // 已下架
		}
		
		return 0;
	}
	
	/**
	 * 根据module获取type
	 * @param type
	 * @return
	 */
	public static String getModuleByType(int type) {
		/**
		 * 产品
		 */
		if (type == 4) {
			return Define.PRODUCT_PLAN; // 线路产品
		} else if (type == 6) {
			return Define.PRODUCT_LOCATION_TICKET_TYPE; // 景点门票
		} else if (type == 9) {
			return Define.PRODUCT_LIFESTYLE_SHOPPING; // 购物优惠
		} else if (type == 7) {
			return Define.PRODUCT_LIFESTYLE_ENTERTAINM; // 休闲娱乐
		} else if (type == 5) {
			return Define.PRODUCT_HOTEL; // 酒店
		} else if (type == 8) {
			return Define.PRODUCT_LIFESTYLE_FOOD; // 餐饮美食
		} else if (type == 2) {
			return Define.PRODUCT_COMBINE; // 组合产品
		}
		
		return null;
	}
	
	/**
	 * 根据type获取String
	 * @param type
	 * @return
	 */
	public static String getStringByType(int type) {
		/**
		 * 产品
		 */
		if (type == 4) { 
			return StringUtil.getStringFromR(R.string.line); // 线路产品
		} else if (type == 6) {
			return StringUtil.getStringFromR(R.string.location_ticket_type); // 景点门票
		} else if (type == 9) {
			return StringUtil.getStringFromR(R.string.category_shopping); // 购物优惠
		} else if (type == 7) {
			return StringUtil.getStringFromR(R.string.category_amusement); // 休闲娱乐
		} else if (type == 5) {
			return StringUtil.getStringFromR(R.string.hotel); // 酒店
		} else if (type == 8) {
			return StringUtil.getStringFromR(R.string.category_food); // 餐饮美食
		} else if (type == 2) {
			return StringUtil.getStringFromR(R.string.category_group_product); // 组合产品
		}
		
		return null;
	}
	
	/**
	 * 根据type获取String
	 * @param type
	 * @return
	 */
	public static String getAroundDetailTitleByType(String type) {
		/**
		 * 附近资讯
		 */
		if (Define.LOCATION.equals(type) || Define.CUSTOM_LOCATION.equals(type)) { 
			return StringUtil.getStringFromR(R.string.scenic_detail); // 景点详情
		} else if (Define.HOTEL.equals(type) || Define.CUSTOM_HOTEL.equals(type)) {
			return StringUtil.getStringFromR(R.string.hotel_detail); // 酒店详情
		} else if (Define.FOOD.equals(type) || Define.CUSTOM_FOOD.equals(type)) {
			return StringUtil.getStringFromR(R.string.food_detail); // 美食详情
		} else if (Define.SHOPPING.equals(type) || Define.CUSTOM_SHOPPING.equals(type)) {
			return StringUtil.getStringFromR(R.string.shopping_detail); // 购物详情
		} else if (Define.AMUSEMENT.equals(type) || Define.CUSTOM_ENTERTAINMENT.equals(type)) {
			return StringUtil.getStringFromR(R.string.amusement_detail); // 娱乐详情
		} else if(Define.CUSTOM_EVENT.equals(type)) {
			return StringUtil.getStringFromR(R.string.event_detail); // 自定义事件详情
		} else {
			return StringUtil.getStringFromR(R.string.traffic_detail); // 自定义交通详情
		}
	}
	
	public static String getStringByType(String type) {
		int typeResId = 0;
		if (Define.TRAVEL.equals(type)) {
			typeResId = R.string.travel; // 行程
		} else if (Define.LOCATION.equals(type)) {
			typeResId = R.string.scenic; // 景点
		} else if (Define.HOTEL.equals(type)) {
			typeResId = R.string.hotel; // 酒店 
		} else if (Define.FOOD.equals(type)) {
			typeResId = R.string.food; // 美食
		} else if (Define.AMUSEMENT.equals(type)) {
			typeResId = R.string.amusement; // 娱乐
		} else if (Define.SHOPPING.equals(type)) {
			typeResId = R.string.shopping; // 购物
		} else if (Define.LIFESTYLE.equals(type)) { 
			typeResId = R.string.lifestyle;
		} else if (Define.PRODUCT_LIFESTYLE_ENTERTAINM.equals(type) || Define.ENTERTAINM.equals(type)) { 
			typeResId = R.string.category_amusement; // 休闲娱乐
		} else if (Define.PRODUCT_LIFESTYLE_SHOPPING.equals(type)) {
			typeResId = R.string.category_shopping; // 购物优惠
		} else if (Define.PRODUCT_LOCATION_TICKET_TYPE.equals(type)) {
			typeResId = R.string.category_scenic; // 景点门票
		} else if (Define.PRODUCT_PLAN.equals(type) || Define.TRAVEL.equals(type)) {
			typeResId = R.string.line; // 线路
		} else if (Define.LOCATION_TICKET_TYPE.equals(type)) {
			typeResId = R.string.location_ticket_type; // 景点门票
		}
		
		String txt = "";
		if (typeResId != 0) {
			txt = StringUtil.getStringFromR(typeResId);
		}
		return txt;
	}
	
	/**
	 * 根据type获取String
	 * @param type
	 * @return
	 */
	public static String getAroundTitleByType(String type) {
		/**
		 * 附近资讯
		 */
		if (Define.LOCATION.equals(type) || Define.CUSTOM_LOCATION.equals(type)) {  
			return StringUtil.getStringFromR(R.string.in_the_vicinity_of_scenic); // 附近的景点
		} else if (Define.HOTEL.equals(type) || Define.CUSTOM_HOTEL.equals(type)) {
			return StringUtil.getStringFromR(R.string.in_the_vicinity_of_hotel); // 附近的酒店
		} else if (Define.FOOD.equals(type) || Define.CUSTOM_FOOD.equals(type)) {
			return StringUtil.getStringFromR(R.string.in_the_vicinity_of_food); // 附近的美食
		} else if (Define.SHOPPING.equals(type) || Define.CUSTOM_SHOPPING.equals(type)) {
			return StringUtil.getStringFromR(R.string.in_the_vicinity_of_shopping); // 附近的购物
		} else if (Define.AMUSEMENT.equals(type) || Define.CUSTOM_ENTERTAINMENT.equals(type)) {
			return StringUtil.getStringFromR(R.string.in_the_vicinity_of_amusement); // 附近的娱乐
		} 
		return null;
	}
	
}
