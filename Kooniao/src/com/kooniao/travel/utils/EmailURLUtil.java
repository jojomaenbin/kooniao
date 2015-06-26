package com.kooniao.travel.utils;

/**
 * URL工具类，识别url后缀，跳转对应的邮箱地址
 * @author ke.wei.quan
 *
 */
public class EmailURLUtil {
	
	static final String _163_VIP_EMAIL_ADDRESS = "@vip.163.com";
	static final String _163_EMAIL_ADDRESS = "@163.com";
	static final String _126_EMAIL_ADDRESS = "@126.com";
	static final String _QQ_EMAIL_ADDRESS = "@qq.com";
	static final String _GMAIL_EMAIL_ADDRESS = "@gmail.com";
	static final String _YAHOO_EMAIL_ADDRESS = "@yahoo.com";
	static final String _SINA_EMAIL_ADDRESS = "@sina.com.cn";
	static final String _SOHU_EMAIL_ADDRESS = "@sohu.com";
	static final String _OUT_LOOKEMAIL_ADDRESS = "@outlook.com";
	static final String _TOM_EMAIL_ADDRESS = "@tom.com";
	static final String _21_CN_EMAIL_ADDRESS = "@21cn.com";
	static final String _ALIYUN_EMAIL_ADDRESS = "@aliyun.com";
	static final String _ICLOUD_EMAIL_ADDRESS = "@icloud.com";
	static final String _SOGOU_EMAIL_ADDRESS = "@sogou.com";
	static final String _189_EMAIL_ADDRESS = "@189.com";
	static final String _WO_EMAIL_ADDRESS = "@wo.cn";
	
	/**
	 * 返回真正的邮箱请求地址
	 * @param emailAddress
	 * @return
	 */
	public static String getRealURL(String emailAddress) {
		if (emailAddress.isEmpty()) {
			return "http://www.kooniao.com/";
		}
		
		if (emailAddress.contains(_163_VIP_EMAIL_ADDRESS)) {
			return "http://vip.163.com/";
		}
		
		if (emailAddress.contains(_163_EMAIL_ADDRESS)) {
			return "http://email.163.com";
		}
		
		if (emailAddress.contains(_126_EMAIL_ADDRESS)) {
			return "http://mail.126.com";
		}
		
		if (emailAddress.contains(_QQ_EMAIL_ADDRESS)) {
			return "http://mail.qq.com";
		}
		
		if (emailAddress.contains(_GMAIL_EMAIL_ADDRESS)) {
			return "http://gmail.google.com";
		}
		
		if (emailAddress.contains(_YAHOO_EMAIL_ADDRESS)) {
			return "http://mail.yahoo.com";
		}
		
		if (emailAddress.contains(_SINA_EMAIL_ADDRESS)) {
			return "http://mail.sina.com.cn";
		}
		
		if (emailAddress.contains(_SOHU_EMAIL_ADDRESS)) {
			return "http://mail.sohu.com";
		}
		
		if (emailAddress.contains(_OUT_LOOKEMAIL_ADDRESS)) {
			return "hppt://www.outlook.com";
		}
		
		if (emailAddress.contains(_TOM_EMAIL_ADDRESS)) {
			return "http://mail.tom.com";
		}
		
		if (emailAddress.contains(_21_CN_EMAIL_ADDRESS)) {
			return "http://mail.21cn.com";
		}
		
		if (emailAddress.contains(_ALIYUN_EMAIL_ADDRESS)) {
			return "http://mail.aliyun.com";
		}
		
		if (emailAddress.contains(_ICLOUD_EMAIL_ADDRESS)) {
			return "http://www.icloud.com";
		}
		
		if (emailAddress.contains(_SOGOU_EMAIL_ADDRESS)) {
			return "http://mail.sogou.com";
		}
		
		if (emailAddress.contains(_189_EMAIL_ADDRESS)) {
			return "http://mail.189.cn";
		}
		
		if (emailAddress.contains(_WO_EMAIL_ADDRESS)) {
			return "http://mail.wo.cn";
		}
		
		else {
			return "http://www.kooniao.com/";
		}
		
	}
	
}
