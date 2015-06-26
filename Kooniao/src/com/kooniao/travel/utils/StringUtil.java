package com.kooniao.travel.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kooniao.travel.KooniaoApplication;

public class StringUtil {
	
	/**
	 * 工具id获取系统String资源
	 * @param resId
	 * @return
	 */
	public static String getStringFromR(int resId) {
		String resString = KooniaoApplication.getInstance().getResources().getString(resId);
		return resString;
	}

	/**
	 * 判断email格式是否正确
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (email == null) {
			return false;
		}
		if (isEmpty(email)) {
			return false;
		}
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}
	
	/**
	 * 判断是否是纯数字
	 * @param number
	 * @return
	 */
	public static boolean isNumber(String number) {
		if (number == null) {
			return false;
		} else if (isEmpty(number)) {
			return false;
		}
		
		String str = "^[0-9]*$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(number);
		return m.matches();
	}

	/**
	 * 是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (null == str)
			return true;
		if (str.length() == 0)
			return true;
		if (str.trim().length() == 0)
			return true;
		return false;
	}

	/**
	 * html字符转义
	 * 
	 * @param str
	 *            需要转义的字符串
	 * @return
	 */
	public static String htmlEscapeCharsToString(String source) {
		return StringUtil.isEmpty(source) ? source : source.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&").replaceAll("&quot;", "\"");
	}
	
	/**
	 * 半角转为全角
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {  
		   char[] c = input.toCharArray();  
		   for (int i = 0; i< c.length; i++) {  
		       if (c[i] == 12288) {  
		         c[i] = (char) 32;  
		         continue;  
		       }if (c[i]> 65280&& c[i]< 65375)  
		          c[i] = (char) (c[i] - 65248);  
		       }  
		   return new String(c);  
		}  
}
