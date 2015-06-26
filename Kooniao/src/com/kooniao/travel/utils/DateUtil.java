package com.kooniao.travel.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.kooniao.travel.constant.Define;

@SuppressLint("SimpleDateFormat")
public class DateUtil {
	
	/**
	 * 获取当前日期
	 * @param format
	 * @return
	 */
	public static String getCurrentDate() {
		SimpleDateFormat sdf=new SimpleDateFormat(Define.FORMAT_YMD);   
		String date=sdf.format(new java.util.Date()); 
		return date;
	}
	
	public static String getCurrentDate(String format) {
		SimpleDateFormat sdf=new SimpleDateFormat(format);    
		String date=sdf.format(new java.util.Date()); 
		return date;
	}

	/**
	 * 时间戳转格式化日期
	 * 
	 * @param timestamp
	 *            单位毫秒
	 * @param format
	 *            日期格式
	 * @return
	 */
	public static String timestampToStr(long timestamp, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = new Date(timestamp * 1000);
		return sdf.format(date);
	}

	/**
	 * 格式化日期转时间戳
	 * 
	 * @param dateString
	 *            日期字符串
	 * @return
	 */
	public static long strToTimestamp(String date) {
		return strToTimestamp(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 格式化日期转时间戳
	 * 
	 * @param dateString
	 *            日期字符串
	 * @param format
	 *            日期格式
	 * @return
	 */
	public static long strToTimestamp(String date, String format) {
		long timestamp = 0;
		try {
			timestamp = new SimpleDateFormat(format).parse(date).getTime() / 1000;

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timestamp;
	}

	public static String timeDistanceString(long unixtime, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		String dateString = dateFormat.format(new Date(unixtime * 1000));
		return dateString;
	}

	/**
	 * 转化时间为xx之前
	 * @param unixtime
	 * @return
	 */
	public static String timeDistanceString(long unixtime) {
		final long MILLISEC = 1000;
		final long MILLISEC_MIN = 60 * MILLISEC;
		final long MILLISEC_HOUR = 60 * MILLISEC_MIN;
		final long MILLISEC_DAY = 24 * MILLISEC_HOUR;
		final long MILLISEC_MONTH = 30 * MILLISEC_DAY;
		final long MILLISEC_YEAR = 12 * MILLISEC_MONTH;
		long currentTime = System.currentTimeMillis();
		long timeDifference = Math.abs(currentTime - unixtime * 1000);

		/*
		 * 今年之前
		 */
		if (timeDifference / MILLISEC_YEAR > 1) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String dateString = dateFormat.format(new Date(unixtime * 1000));
			return dateString;
		}

		/*
		 * 今年
		 */
		if (timeDifference / MILLISEC_DAY > 1 && timeDifference / MILLISEC_YEAR <= 1) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
			String dateString = dateFormat.format(new Date(unixtime * 1000));
			return dateString;
		}

		/*
		 * 昨天
		 */
		if (timeDifference / MILLISEC_DAY == 1) {
			return "昨天";
		}

		/*
		 * 1天内
		 */
		if (timeDifference / MILLISEC_HOUR >= 1 && timeDifference / MILLISEC_DAY < 1) {
			return String.format(Locale.US, "%d小时前", timeDifference / MILLISEC_HOUR);
		}

		/*
		 * 1小时内
		 */
		if (timeDifference / MILLISEC_MIN >= 1 && timeDifference / MILLISEC_HOUR < 1) {
			return String.format(Locale.US, "%d分钟前", timeDifference / MILLISEC_MIN);
		}

		return "刚刚";

	}

	/**
	 * 把时间转换为秒
	 * 
	 * @param dateString
	 * @return
	 */
	public static String dateToSecond(String dateString) {
		String count = dateString.trim().substring(0, dateString.length() - 1);
		String unit = dateString.trim().substring(dateString.length() - 1, dateString.length());
		if (unit.equals("周")) {
			if (count.equals("一")) {
				return String.valueOf(7 * 24 * 60 * 60);
			}

			if (count.equals("二")) {
				return String.valueOf(2 * 7 * 24 * 60 * 60);
			}
		}

		if (unit.equals("日")) {
			return String.valueOf(Integer.parseInt(count) * 24 * 60 * 60);
		}

		return null;
	}

	public static String formatDay(int day) {
		if (day == 7) {
			return "一周";
		} else if (day == 14) {
			return "两周";
		} else {
			return day + "天";
		}
	}
	
	/**
	 * 获取某一年的某一个月有多少天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getMonthDays(int year, int month) {
		Calendar time = Calendar.getInstance();
		time.clear();
		time.set(Calendar.YEAR, year + 1900);
		time.set(Calendar.MONTH, month - 1);
		int days = time.getActualMaximum(Calendar.DAY_OF_MONTH);
		return days;
	}

	/**
	 * 获取当前月份一号是星期几
	 * 
	 * @return
	 */
	public static String getWeekOfCurrentMonth() {
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.CHINA);
		Calendar firstDate = Calendar.getInstance();
		firstDate.set(Calendar.DATE, 1);
		String week = sdf.format(firstDate.getTime());
		return week;
	}
	
	/**
	 * 获取某一天是星期几(0:星期日，1:星期一)
	 * @param dateStr
	 * @return
	 */
	public static int getWeekIndex(String dateStr) {
		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		Date date = null;
		try {
			date = dateSdf.parse(dateStr);
		} catch (ParseException e) {
			date = new Date();
		}
		int weekIndex = getWeekIndex(date); 
		return weekIndex;
	}
	
	/**
	 * 获取某一天是星期几(0:星期日，1:星期一)
	 * @param date
	 * @return
	 */
	public static int getWeekIndex(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int weekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		weekIndex = weekIndex <= 0 ? 0 : weekIndex;
		return weekIndex;
	}

	/**
	 * 获取上个月份有几天
	 * @param currentYear
	 * @param currentMonth
	 * @return
	 */
	public static int getPreviousMonthDays(int currentYear, int currentMonth) {
		int previousMonth = currentMonth;
		if (currentMonth != 1) {
			previousMonth = currentMonth - 1;
		} else if (currentMonth == 1) {
			previousMonth = 12;
			currentYear = currentYear - 1;
		}
		int monthdays = getMonthDays(currentYear, previousMonth);
		return monthdays;
	}

	/**
	 * 获取上个月份有几天
	 * 
	 * @return
	 */
	public static int getPreviousMonthDays() {
		int currentYear = getCurrentYear();
		int currentMonth = getCurrentMonth();
		int monthdays = getPreviousMonthDays(currentYear, currentMonth);
		return monthdays;
	}
	
	/**
	 * 获取当前月份有几天
	 * 
	 * @return
	 */
	public static int getCurrentMonthDays() {
		int currentYear = getCurrentYear();
		int currentMonth = getCurrentMonth();
		int monthdays = getMonthDays(currentYear, currentMonth);
		return monthdays;
	}

	/**
	 * 获取下个前月份有几天
	 * 
	 * @return
	 */
	public static int getNextMonthDays() {
		int currentMonth = getCurrentMonth();
		int nextMonth = currentMonth;
		int year = getCurrentYear();
		if (currentMonth != 12) {
			nextMonth = currentMonth + 1;
		} else if (currentMonth == 12) {
			nextMonth = 1;
			year = year + 1;
		}
		int monthdays = getMonthDays(year, nextMonth);
		return monthdays;
	}

	/**
	 * 获取当前月份
	 * 
	 * @return
	 */
	public static int getCurrentMonth() {
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH) + 1;
		return month;
	}

	/**
	 * 获取当前年份
	 * 
	 * @return
	 */
	public static int getCurrentYear() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		return year;
	}

//	/**
//	 * 获取当前日期("yyyy-MM-dd")
//	 * 
//	 * @return
//	 */
//	public static String getCurrentDate() {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
//		String date = sdf.format(new java.util.Date());
//		return date;
//	}
	
	/**
	 * 获取今天是几号
	 * @return
	 */
	public static int getCurrentMonthDay() {
		String currentDate = getCurrentDate();
		String day = currentDate.substring(8);
		if (day.startsWith("0")) {
			day = day.substring(1);
		}
		int currentMonthDay = Integer.parseInt(day);
		return currentMonthDay;
	}

//	/**
//	 * 获取当前日期
//	 * 
//	 * @param format
//	 * @return
//	 */
//	public static String getCurrentDate(String format) {
//		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
//		String date = sdf.format(new java.util.Date());
//		return date;
//	}
//
//	/**
//	 * 日期转时间戳("yyyy-MM-dd")
//	 * 
//	 * @param date
//	 * @return
//	 */
//	public static long strToTimestamp(String date) {
//		long timestamp = strToTimestamp(date, "yyyy-MM-dd");
//		return timestamp;
//	}
//
//	/**
//	 * 日期转时间戳
//	 * 
//	 * @param dateString
//	 * @param format
//	 * @return
//	 */
//	public static long strToTimestamp(String date, String format) {
//		long timestamp = 0;
//		try {
//			timestamp = new SimpleDateFormat(format, Locale.CHINA).parse(date).getTime() / 1000;
//
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		return timestamp;
//	}
}
