package com.kooniao.travel.customwidget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.kooniao.travel.R;
import com.kooniao.travel.customwidget.CalendarView.OnDateChangeListener;
import com.kooniao.travel.customwidget.CalendarView.OnDateClickListener;
import com.kooniao.travel.customwidget.CalendarView.OnDateSlideRangeListener;
import com.kooniao.travel.utils.DateUtil;

/**
 * 日历控件
 * 
 * @author ke.wei.quan
 * @date 2015年4月24日
 *
 */
public class CalendarWidget extends ViewFlipper {

	public CalendarWidget(Context context) {
		this(context, null);
	}

	private int year = -1;
	private int month = -1;
	
	public CalendarWidget(Context context, int calendarMode, long timeStamp) {
		super(context);
		mContext = context;
		this.calendarMode = calendarMode;
		String dateString = DateUtil.timeDistanceString(timeStamp, "yyyy-MM-dd");  
		year = Integer.valueOf(dateString.substring(0, 4));
		String monthString = dateString.substring(5, 7);
		month = Integer.valueOf(monthString) >= 10 ? Integer.valueOf(monthString) : Integer.valueOf(dateString.substring(6, 7));
		init();
	}

	private Context mContext;
	// 日历模式
	private int calendarMode;

	public CalendarWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.Calendar, 0, 0);
		calendarMode = arr.getInt(R.styleable.Calendar_mode, 0);
		arr.recycle();
		init();
	}

	private CalendarView currentCalendarView;
	private CalendarView switchTempCalendarView;

	private Animation leftIn; // 动画-左进
	private Animation leftOut; // 动画-左出
	private Animation rightIn; // 动画-右进
	private Animation rightOut; // 动画-右出

	private void init() {
		// 初始化日历翻动动画
		leftIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
		leftOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
		rightIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left);
		rightOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);

		switch (calendarMode) {
		case 0: // 普通模式
			currentCalendarView = new CalendarView(mContext, CalendarView.CalendarMode.NORMAL, year, month);
			switchTempCalendarView = new CalendarView(mContext, CalendarView.CalendarMode.NORMAL, year, month);
			break;

		case 1: // 单选模式
			currentCalendarView = new CalendarView(mContext, CalendarView.CalendarMode.SINGLE_CHOICE, year, month);
			switchTempCalendarView = new CalendarView(mContext, CalendarView.CalendarMode.SINGLE_CHOICE, year, month);
			break;

		case 2:
			currentCalendarView = new CalendarView(mContext, CalendarView.CalendarMode.MULTIPLE_CHOICE, year, month);
			switchTempCalendarView = new CalendarView(mContext, CalendarView.CalendarMode.MULTIPLE_CHOICE, year, month);
			break;
			
		case 3:
			currentCalendarView = new CalendarView(mContext, CalendarView.CalendarMode.ADD_DAY_INFO, year, month);
			switchTempCalendarView = new CalendarView(mContext, CalendarView.CalendarMode.ADD_DAY_INFO, year, month);
			break;

		default:
			currentCalendarView = new CalendarView(mContext);
			switchTempCalendarView = new CalendarView(mContext);
			break;
		}

		addView(currentCalendarView);
		addView(switchTempCalendarView);
	}

	public void nextMonth() {
		// 设置动画
		setInAnimation(leftIn);
		setOutAnimation(leftOut);
		showNext();

		currentCalendarView.nextMonth();
		switchTempCalendarView.nextMonth();
		postDelayed(new Runnable() {

			@Override
			public void run() {
			}
		}, 600);
	}

	public void previousMonth() {
		// 设置动画
		setInAnimation(rightIn);
		setOutAnimation(rightOut);
		showPrevious();

		postDelayed(new Runnable() {

			@Override
			public void run() {
				currentCalendarView.previousMonth();
				switchTempCalendarView.previousMonth();
			}
		}, 600);
	}

	/**
	 * 设置可点击的日期
	 * 
	 * @param dateList
	 *            日
	 */
	public void setClickAbleDate(List<String> dayList) {
		List<Integer> dayIntegers = new ArrayList<>();
		for (int i = 0; i < dayList.size(); i++) {
			String date = dayList.get(i);
			int day = Integer.parseInt(date.substring(date.lastIndexOf("-") + 1));
			dayIntegers.add(day);
		}

		currentCalendarView.setClickAbleDate(dayIntegers);
		switchTempCalendarView.setClickAbleDate(dayIntegers);
	}
	
	/**
	 * 设置已经设置了内容的日期
	 * 
	 * @param contentDateList
	 */
	public void setAddedContentDate(List<ContentDate> contentDateList) {
		currentCalendarView.setAddedContentDate(contentDateList);
		switchTempCalendarView.setAddedContentDate(contentDateList);
	}

	/**
	 * 设置能点击的日期范围(yyyy-MM-dd转化为时间戳)
	 * @param startDateStamp
	 * @param endDateStamp
	 */
	public void setCanClickAbleDateRange(long startDateStamp, long endDateStamp) {
		currentCalendarView.setCanClickAbleDateRange(startDateStamp, endDateStamp);
		switchTempCalendarView.setCanClickAbleDateRange(startDateStamp, endDateStamp);
	}
	
	/**
	 * 监听日期变化
	 * 
	 * @param onDateChangeListener
	 */
	public void setOnDateChangeListener(OnDateChangeListener onDateChangeListener) {
		currentCalendarView.setOnDateChangeListener(onDateChangeListener);
		switchTempCalendarView.setOnDateChangeListener(onDateChangeListener);
	}

	/**
	 * 设置滑动选择日期范围监听
	 * 
	 * @param onDateSlideRangeListener
	 */
	public void setOnDateSlideRangeListener(OnDateSlideRangeListener onDateSlideRangeListener) {
		currentCalendarView.setOnDateSlideRangeListener(onDateSlideRangeListener);
		switchTempCalendarView.setOnDateSlideRangeListener(onDateSlideRangeListener);
	}

	/**
	 * 设置点击日期监听
	 * 
	 * @param onDateClickListener
	 */
	public void setOnDateClickListener(OnDateClickListener onDateClickListener) {
		currentCalendarView.setOnDateClickListener(onDateClickListener);
		switchTempCalendarView.setOnDateClickListener(onDateClickListener);
	}

	/**
	 * 是否可以点击之前的日期
	 * 
	 * @param canClickPreviousDate
	 */
	public void setCanClickPreviousDate(boolean canClickPreviousDate) {
		currentCalendarView.setCanClickPreviousDate(canClickPreviousDate);
		switchTempCalendarView.setCanClickPreviousDate(canClickPreviousDate);
	}

	/**
	 * 获取当前页数据的年份
	 * 
	 * @return
	 */
	public int getCurrentYear() {
		return currentCalendarView.getCurrentYear();
	}

	/**
	 * 获取当前页数据的月份
	 * 
	 * @return
	 */
	public int getCurrentMonth() {
		return currentCalendarView.getCurrentMonth();
	}
	
	public void notifyViewChange() {
		currentCalendarView.invalidate();
		switchTempCalendarView.invalidate();
	}

}
