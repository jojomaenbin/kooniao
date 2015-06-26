package com.kooniao.travel.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.CalendarView.Cell;
import com.kooniao.travel.customwidget.CalendarWidget;
import com.kooniao.travel.customwidget.ContentDate;
import com.kooniao.travel.customwidget.CalendarView.OnDateChangeListener;
import com.kooniao.travel.customwidget.CalendarView.OnDateClickListener;
import com.kooniao.travel.utils.DateUtil;

/**
 * 每日库存量
 * 
 * @author ke.wei.quan
 * @date 2015年5月28日
 * @version 1.0
 *
 */
@EActivity(R.layout.activity_everyday_inventory_acount)
public class EverydayInventoryAccountActivity extends BaseActivity {
	@ViewById(R.id.ll_calendar)
	LinearLayout calendarLayout;
	@ViewById(R.id.tv_date)
	TextView dateTextView;
	@ViewById(R.id.tv_selected_date)
	TextView selectedDateTextView;
	@ViewById(R.id.et_inventory_acount)
	EditText inventoryAcountEditText;

	@AfterViews
	void init() {
		initData();
		initView();
	}

	private Handler mHandler = new Handler();
	private long serviceTimeStame; // 服务器时间戳
	private List<ContentDate> contentDates = new ArrayList<>(); // 已添加内容的天日期列表

	/**
	 * 初始化界面数据
	 */
	@SuppressWarnings("unchecked")
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			serviceTimeStame = intent.getLongExtra(Define.TIME_STAMP, 0);
			if (intent.hasExtra(Define.DATA)) {
				contentDates = (List<ContentDate>) intent.getSerializableExtra(Define.DATA);
				contentDates = (List<ContentDate>) (contentDates == null ? new ArrayList<>() : contentDates);
			}
		}
	}

	private CalendarWidget calendarWidget;
	private String mDate;
	private Cell currentSelectCell; // 当前选中的cell
	private ContentDate contentDate;

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 初始化日历
		calendarWidget = new CalendarWidget(this, 3, serviceTimeStame);
		LayoutParams calendarWidgetParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) (Define.DENSITY * 250));
		calendarWidget.setLayoutParams(calendarWidgetParams);
		calendarWidget.setBackgroundColor(getResources().getColor(R.color.white));
		calendarWidget.setCanClickPreviousDate(false); // 不允许点击之前的日期
		calendarLayout.addView(calendarWidget);

		// 设置日历标题
		int currentYear = calendarWidget.getCurrentYear();
		int currentMonth = calendarWidget.getCurrentMonth();
		mDate = currentYear + getResources().getString(R.string.year) + currentMonth + getResources().getString(R.string.month);
		dateTextView.setText(mDate);
		// 设置日历变化监听
		calendarWidget.setOnDateChangeListener(new OnDateChangeListener() {

			@Override
			public void dateChangeListener(int year, int month) {
				mDate = year + getResources().getString(R.string.year) + month + getResources().getString(R.string.month);
				dateTextView.setText(mDate);
			}
		});

		// 设置日期点击事件
		calendarWidget.setOnDateClickListener(new OnDateClickListener() {

			@Override
			public void clickDate(Cell lastCell, Cell currentCell, int year, int month, int day) {
				currentSelectCell = currentCell;
				selectedDateTextView.setText(year + "-" + month + "-" + day);
				if (lastCell != null) {
					String inputString = inventoryAcountEditText.getText().toString();
					if (!inputString.equals("")) {
						lastCell.setAddContent(inputString);
						inventoryAcountEditText.setText("");

						int lastCellYear = lastCell.getCustomDate().getYear();
						int lastCellMonth = lastCell.getCustomDate().getMonth();
						String lastCellMonthString = lastCellMonth > 9 ? lastCellMonth + "" : "0" + lastCellMonth;
						int lastCellDay = lastCell.getCustomDate().getDay();
						String date = lastCellYear + "-" + lastCellMonthString + "-" + lastCellDay;
						int index = getIndex(date);
						if (index != -1) {
							contentDate = contentDates.get(index);
						} else {
							contentDate = new ContentDate();
						}
						contentDate.setContent(inputString);
						contentDate.setDate(date);
						if (index == -1) {
							contentDates.add(contentDate);
						}

						calendarWidget.notifyViewChange();
					}
				}

				String addContent = String.valueOf(currentCell.getAddContent());
				if (addContent.equals("null")) {
					inventoryAcountEditText.setText("");
				} else {
					inventoryAcountEditText.setText(addContent);
					inventoryAcountEditText.setSelection(addContent.length());
				}
			}

		});

		// 设置日历可点击的日期
		String startDateString = DateUtil.timestampToStr(serviceTimeStame, Define.FORMAT_YMD);
		long startDateStamp = DateUtil.strToTimestamp(startDateString, Define.FORMAT_YMD);
		long endDateStamp = startDateStamp + 60 * 24 * 60 * 60;
		calendarWidget.setCanClickAbleDateRange(startDateStamp, endDateStamp);

		// 初始化已添加的日期内容
		calendarWidget.setAddedContentDate(contentDates);
	}

	private int getIndex(String date) {
		int index = -1;
		String addedDate;
		for (int i = 0; i < contentDates.size(); i++) {
			addedDate = contentDates.get(i).getDate();
			if (date.equals(addedDate)) {
				index = i;
				break;
			}
		}

		return index;
	}

	private int mCurrentMonthIndex = 1;

	/**
	 * 点击上一个月
	 */
	@Click(R.id.iv_previous_month)
	void onPreviousMonthClick() {
		if (mCurrentMonthIndex > 1) {
			mCurrentMonthIndex--;
			calendarWidget.previousMonth();
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// 初始化已添加的日期内容
					calendarWidget.setAddedContentDate(contentDates);
				}
			}, 700);
		}
	}

	/**
	 * 点击下一个月
	 */
	@Click(R.id.iv_next_month)
	void onNextMonthClick() {
		if (mCurrentMonthIndex < 3) {
			mCurrentMonthIndex++;
			calendarWidget.nextMonth();
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// 初始化已添加的日期内容
					calendarWidget.setAddedContentDate(contentDates);
				}
			}, 700);
		}
	}

	/**
	 * 点击关闭按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 点击完成
	 */
	@Click(R.id.iv_finish)
	void onFinishClick() {
		String inputString = inventoryAcountEditText.getText().toString();
		if (currentSelectCell != null) {
			int lastCellYear = currentSelectCell.getCustomDate().getYear();
			int lastCellMonth = currentSelectCell.getCustomDate().getMonth();
			String lastCellMonthString = lastCellMonth > 9 ? lastCellMonth + "" : "0" + lastCellMonth;
			int lastCellDay = currentSelectCell.getCustomDate().getDay();
			String currentSelectDate = lastCellYear + "-" + lastCellMonthString + "-" + lastCellDay;

			int index = getIndex(currentSelectDate);
			if (index != -1) {
				contentDates.get(index).setContent(inputString);
			} else if (!TextUtils.isEmpty(inputString)) {
				ContentDate currentContentDate = new ContentDate();
				currentContentDate.setContent(inputString);
				currentContentDate.setDate(currentSelectDate);
				contentDates.add(currentContentDate);
			}

			Intent intent = new Intent();
			Bundle extras = new Bundle();
			extras.putSerializable(Define.DATA, (Serializable) contentDates);
			intent.putExtras(extras);
			setResult(RESULT_OK, intent);
		}
		finish();
	}

	@Override
	protected void onDestroy() {
		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
		}
		super.onDestroy();
	}
}
