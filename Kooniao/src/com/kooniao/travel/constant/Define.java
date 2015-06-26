package com.kooniao.travel.constant;

import com.kooniao.travel.KooniaoApplication;

public class Define {

	/**
	 * baseURL
	 */
	public static final String OFFICAL_WEBSIZE = "http://www.kooniao.com"; // 官网
	public static final String BASE_URL = "http://apit.kooniao.com/index.php?app=api&"; // 内网
//	public static final String BASE_URL = "http://www.kooniao.com/index.php?app=api&"; // 外网
	
	// C店分享链接
	public static final String C_STORE_SHARE_BASE_URL = "http://m.kooniao.com/index.php?app=webapp&mod=ComShop&act=otherComShop&shop_id="; 

	/**
	 * 是否捕获全局异常
	 */
	public static final boolean CRASHHANDLER_OPEN = false;

	public static final String DB_NAME = "kooniao.db";
	/**
	 * 请求超时时间
	 */
	public static final int REQUEST_TIMEOUT = 60 * 1000;
	/**
	 * 自动轮播时间
	 */
	public static final int AUTO_SCROLL_INTERVAL_TIME = 3 * 1000;

	/**
	 * 屏幕相关
	 */
	public static final float DENSITY = KooniaoApplication.getInstance().getResources().getDisplayMetrics().density;
	public static final float SCALESITY = KooniaoApplication.getInstance().getResources().getDisplayMetrics().scaledDensity;
	public static final int widthPx = KooniaoApplication.getInstance().getResources().getDisplayMetrics().widthPixels;
	public static final int heightPx = KooniaoApplication.getInstance().getResources().getDisplayMetrics().heightPixels;

	/**
	 * 日志打印tag
	 */
	public static final String LOG_TAG = "KooniaoApp";
	
	public static final int SMS_ERR_CODE_REGISTERED = 10035; // 该手机号已经注册
	public static final int SMS_ERR_CODE_OVER_SEND_COUNT = 10033; // 该手机号已经超过当天发送最大短信数

	/**
	 * 一些字符串常量
	 */
	public static final String TRAVEL = "plan"; // 行程
	public static final String LOCATION = "location"; // 景点
	public static final String HOTEL = "hotel"; // 酒店
	public static final String FOOD = "lifestyle_food"; // 美食
	public static final String AMUSEMENT = "lifestyle_funny"; // 娱乐
	public static final String SHOPPING = "lifestyle_shopping"; // 购物
	public static final String ENTERTAINM = "lifestyle_entertainm"; // 娱乐
	public static final String LIFESTYLE = "lifestyle"; // 美食、娱乐、购物
	
	public static final String LOCATION_TICKET_TYPE = "location_ticket_type"; // 景点门票
	public static final String PRODUCT_HOTEL = "product_hotel"; // 酒店
	public static final String PRODUCT_LIFESTYLE_ENTERTAINM = "product_lifestyle_entertainm"; // 休闲娱乐
	public static final String PRODUCT_LIFESTYLE_SHOPPING = "product_lifestyle_shopping"; // 购物优惠
	public static final String PRODUCT_LIFESTYLE_FOOD = "product_lifestyle_food"; // 餐饮美食
	public static final String PRODUCT_LOCATION_TICKET_TYPE = "product_location_ticket_type"; // 景点门票
	public static final String PRODUCT_PLAN = "product_plan"; // 线路
	public static final String PRODUCT_COMBINE = "product_combine"; // 组合产品
	//////////////////////////////////////////////////////////////////////////////////
	public static final String CUSTOM = "custom"; // 自定义节点
	public static final String CUSTOM_EVENT = "custom_event"; // 自定义事件
	public static final String CUSTOM_TRAFFIC = "custom_traffic"; // 自定义交通
	public static final String CUSTOM_LOCATION = "custom_location"; // 自定义景点
	public static final String CUSTOM_HOTEL = "custom_hotel"; // 自定义酒店
	public static final String CUSTOM_FOOD = "custom_food"; // 自定义美食
	public static final String CUSTOM_ENTERTAINMENT = "custom_entertainment"; // 自定义娱乐
	public static final String CUSTOM_SHOPPING = "custom_shopping"; // 自定义购物

	public static final String ApiKey = "ApiKey";
	public static final String ApiKeySecret = "ApiKeySecret";
	public static final String CURRENT_TRAVEL_LAST_DATE = "current_travel_last_date";
	public static final String CID_HOME_PAGE = "cid_home_page";
	public static final String CID_DISCOVERY = "cid_discovery";
	public static final String CITY_NAME_AROUND = "city_name_around";
	public static final String CITY_NAME_AROUND_LON = "city_name_around_lon";
	public static final String CITY_NAME_AROUND_LAT = "city_name_around_lat";
	public static final String UID = "uid";
	public static final String CURRENT_USER_NAME = "current_user_name";
	public static final String TYPE = "type";
	public static final String LUNCH_TYPE = "lunch_type";
	public static final String TAB_POSITION = "tab_position";
	public static final String PTYPE = "ptype";
	public static final String MTYPE = "mtype";
	public static final String EMAIL_ADDRESS = "email_address";
	public static final String PHONE = "phone";
	public static final String RESET_PASSWD = "reset_passwd";
	public static final String BOUND = "bound";
	public static final String PRODUCT_COUNT = "product_count";
	public static final String PRODUCT_TYPE = "product_type";
	public static final String PRODUCT_PRICE = "product_price";
	public static final String PRODUCT_UNIT = "product_unit";
	public static final String STORE_TYPE = "store_type";
	public static final String SID = "sid";
	public static final String SHOP_NAME = "shop_name";
	public static final String CONTACT_PHONE = "contact_phone";
	public static final String TITLE = "title";
	public static final String URL = "url";
	public static final String DATA = "data";
	public static final String PAYMENT_LIST = "payment_list";
	public static final String LOGIN_CHANGE = "loginChange";
	public static final String CATEGORY_CHANGE = "categoryChange";
	public static final String CURRENT_TRAVEL_CHANGE = "currentTravelChange";
	public static final String OFFLINE_COUNT = "offline_count";
	public static final String TRAVEL_COUNT = "travel_count";
	public static final String ORDER_STATUS = "order_status";
	public static final String DAY_LIST = "day_list";
	public static final String AROUND_LIST = "around_list";
	public static final String IS_MY_STORE = "is_my_store";
	public static final String FROM = "from";
	public static final String ID = "id";
	public static final String PID = "pid";
	public static final String TEAM_ID = "teamId";
	public static final String ROLLCALL_ID = "rollCallId";
	public static final String CLIENT = "client";
	public static final String DATE = "date";
	public static final String ORDER_CODE = "order_code";
	public static final String ORDER_ID = "order_id";
	public static final String INVOICE_ID = "invoice_id";
	public static final String CHILD_COUNT = "child_count";
	public static final String ADULT_COUNT = "adult_count";
	public static final String CONTACTS_NAME = "contacts_name";
	public static final String CONTACTS_MOBILE = "contacts_mobile";
	public static final String CONTACTS_EMAIL = "contacts_email";
	public static final String PACKAGE_PRICE = "total_money";
	public static final String UNDEFRAYMONEY = "unDefrayMoney";
	public static final String IS_OFFLINE = "is_offline";
	public static final String IMG_LIST = "img_list";
	public static final String MODULE = "module";
	public static final String USER_NAME = "user_name";
	public static final String SEX = "sex";
	public static final String LAT = "lat";
	public static final String LON = "lon";
	public static final String ADDRESS = "address";
	public static final String WAY_RESERVE = "way_reserve";
	public static final String SUB_AREA = "sub_area";
	public static final String CLASS1 = "class1";
	public static final String CLASS2 = "class2";
	public static final String CLASS3 = "class3";
	public static final String CATEGORY = "category";
	public static final String TAB_SORT = "tab_sort";
	public static final String PRODUCT_INFO = "product_info";
	public static final String PRODUCT_NAME = "product_name";
	public static final String PRODUCT_SKU = "product_sku";
	public static final String PRODUCT_LOGO = "product_logo";
	
	public static final String PRICE_ID = "priceId";
	public static final String DEPOSIT = "deposit";
	public static final String IS_PAYDEPOSIT = "isPayDeposit";
	public static final String DEPOSIT_TYPE = "depositType";
	public static final String DEPOSIT_PERCENT = "depositPercent";
	
	public static final String SELECTED_AREA_STRING = "selectedAreaString";
	public static final String SELECTED_AREA_ID = "selected_area_id";
	public static final String SELECTED_SUB_AREA_ID = "selected_sub_area_id";
	public static final String SELECTED_SUB_AREA = "selected_sub_area";
	
	public static final String CID = "cid";
	public static final String REFERENCE_TYPE = "reference_type";
	public static final String LINE_TYPE = "line_type";
	public static final String PARTICIPANTINFO = "participantinfo";
	
	public static final String SETTING_WAY = "settingWay";
	public static final String PARAMS_SETTING = "paramsSetting";
	public static final String PARAMS_SETTING_SUM = "paramsSettingSum";
	public static final String TEMPLATE_ID = "templateId";
	public static final String TIME_STAMP = "time_stamp";
	public static final String CRATE_STAMP = "crate_stamp";
	public static final String MODE = "mode";
	
	// 底部栏五大按钮
	public static final String HOME_PAGE = "home_page";
	public static final String STORE = "store";
	public static final String STORE_LOGO = "store_logo";
	public static final String DISCOVERY = "discovery";
	public static final String AROUND = "around";
	public static final String MINE = "mine";
	// 时间格式化
	public static final String FORMAT_YMD = "yyyy-MM-dd"; // 年月日格式化时间
	public static final String FORMAT_YMDHM = "yyyy-MM-dd HH:mm"; // 年月日时分格式化时间

	// 是否是第一次使用APP
	public static final String IS_NOT_FIRST_START_APP = "is_not_first_start_app";
	// 是否不是第一次进入我的行程列表
	public static final String IS_NOT_FIRST_TIME_IN_MY_TRAVEL = "is_not_first_time_in_my_travel";
	// 是否不是第一次进入点名界面
	public static final String IS_NOT_FIRST_TIME_IN_ROLL_CALL = "is_not_first_time_in_roll_call";
	// 是否是第一次开店
	public static final String IS_FIRST_TIME_OPEN_STORE = "is_not_first_time_open_store";
	// 是否是第一次获取短信模板
	public static final String IS_NOT_FIRST_TIME_GET_MESSAGE_TEMPLATE = "is_first_time_get_message_template";
	public static final String IS_FIRST_TIME_SAVE_SUB_AREA = "is_first_time_save_sub_area";
	
	public static final String PIC_NORMAL = "user_logo.jpeg";
	public static final String PIC_CLIP = "user_logo_round.jpeg";
	// 最后一次奔溃的日志文件名
	public static final String LAST_LOG_NAME = "last_log_name";
	
}
