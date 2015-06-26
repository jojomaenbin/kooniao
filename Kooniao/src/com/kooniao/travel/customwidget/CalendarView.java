package com.kooniao.travel.customwidget;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.kooniao.travel.constant.Define;
import com.kooniao.travel.utils.DateUtil;

/**
 * 日历视图
 * 
 * @author ke.wei.quan
 * @date 2015年4月20日
 *
 */
public class CalendarView extends View {

	private Context context;

	/**
	 * 日历模式
	 * 
	 * @author ke.wei.quan
	 *
	 */
	enum CalendarMode {
		NORMAL, // 普通模式
		SINGLE_CHOICE, // 单选模式
		MULTIPLE_CHOICE, // 多选模式
		ADD_DAY_INFO; // 可输入天内容模式
	}

	public CalendarView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	private CalendarMode mCalendarMode;

	public CalendarView(Context context, CalendarMode calendarMode) {
		super(context);
		this.context = context;
		mCalendarMode = calendarMode;
		init();
	}

	public CalendarView(Context context, CalendarMode calendarMode, int currentYear, int currentMonth) {
		super(context);
		this.context = context;
		mCurrentYear = currentYear;
		mCurrentMonth = currentMonth;
		mCalendarMode = calendarMode;
		init();
	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	// 行cell的大小
	private int mRowSpace;
	// 列cell的大小
	private int mColSpace;
	// 一个cell的大小
	private int mCellSpace;
	// 圆的半径
	private int mCircleRadius;
	// 可以点击的date的字体颜色
	private int clickAbleTextColor = Color.parseColor("#16B8EB");
	// 圆圈颜色
	private int circleColor = Color.parseColor("#16B8EB");
	// 星期title背景
	private int weekTitleBg = Color.parseColor("#E1F8FF");
	// 当前月字体颜色
	private int currentMonthTextColor = Color.BLACK;
	// 其他月份字体颜色
	private int otherMonthTextColor = Color.parseColor("#7B7B7B");

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mRowSpace = getMeasuredWidth() / 7;
		mColSpace = getMeasuredHeight() / 7;
		mCellSpace = Math.min(mRowSpace, mColSpace);
		mCircleRadius = mCellSpace / 2 - 5;
		mTextPaint.setTextSize(mCellSpace / 3); // 字体大小
	}

	private String[] canlendarTitle = { "日", "一", "二", "三", "四", "五", "六" };

	private Paint mTextPaint; // 文字画笔
	private Paint mHollowCirclePaint; // 空心圆画笔
	private Paint mSolidCirclePaint; // 实心圆画笔

	private int mPreviousYear;// 上页数据是哪年
	private int mCurrentYear = -1;// 当前页数据是哪年
	private int mNextYear;// 下页数据是哪年
	private int mpreviousMonth;// 上页数据是哪月
	private int mCurrentMonth = -1;// 当前页数据是哪月
	private int mNextMonth;// 下页数据是哪
	private int mPreviousMonthDays;// 上页数据的月份总共有多少天
	private int mCurrentMonthDays;// 当前页数据的月份总共有多少天
	private int mFirstDayIndex;// 当前页数据的1号是周几

	private FontMetrics mFontMetrics;
	private int mFontHeight;

	private int touchSlop;

	/**
	 * 初始化
	 */
	private void init() {
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		/*
		 * 文字画笔
		 */
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(currentMonthTextColor);
		mTextPaint.setTextAlign(Align.CENTER);
		mFontMetrics = mTextPaint.getFontMetrics();
		mFontHeight = (int) (mFontMetrics.bottom - mFontMetrics.top);

		/*
		 * 空心圆画笔
		 */
		mHollowCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mHollowCirclePaint.setColor(circleColor);
		mHollowCirclePaint.setStyle(Style.STROKE);
		mHollowCirclePaint.setStrokeWidth(3);

		/*
		 * 实心圆画笔
		 */
		mSolidCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mSolidCirclePaint.setColor(circleColor);
		mSolidCirclePaint.setStyle(Style.FILL);

		// 当前页数据的年份
		if (mCurrentYear == -1) {
			mCurrentYear = DateUtil.getCurrentYear();
		}
		// 当前页数据的月份
		if (mCurrentMonth == -1) {
			mCurrentMonth = DateUtil.getCurrentMonth();
		}
		countOtherMonths();
		prepareMonthData();
		// 填充日期
		fillDate();
	}

	private int cellXDistance;
	private int cellYDistance;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		mTextPaint.setColor(Color.BLACK);
		cellYDistance = (int) (mColSpace + mFontMetrics.bottom - (mColSpace - mFontHeight) / 2);

		int color = mTextPaint.getColor();
		// 设置星期title背景
		mTextPaint.setColor(weekTitleBg);
		canvas.drawRect(0, 0, getMeasuredWidth(), mColSpace, mTextPaint);
		mTextPaint.setColor(color);

		// 画出星期title
		for (int i = 0; i < canlendarTitle.length; i++) {
			cellXDistance = mRowSpace / 2 + mRowSpace * i;
			canvas.drawText(canlendarTitle[i], cellXDistance, cellYDistance, mTextPaint);
		}

		// 画日期
		for (int i = 0; i < mCellArray.size(); i++) {
			Cell cell = mCellArray.get(i);
			cell.drawSelf(canvas);
		}
	}

	private boolean mDownTouch;

	private float mDownX;
	private float mDownY;

	private int lastMoveRow;
	private int lastMoveCol;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownTouch = true;
			mDownX = event.getX();
			mDownY = event.getY();

			CellBg cellBg;
			CellBg cellTempBg;
			if (mCalendarMode == CalendarMode.MULTIPLE_CHOICE || mCalendarMode == CalendarMode.ADD_DAY_INFO && mCellArray != null) {
				for (int i = 0; i < mCellArray.size(); i++) {
					Cell cell = mCellArray.get(i);
					cellBg = cell.getCellBg();
					cellTempBg = cell.getCellTempBg();
					if (cellBg != CellBg.SOLID_CIRCLE && cellTempBg != CellBg.SOLID_CIRCLE) {
						cell.setCellTempBg(cellBg);
					} else {
						cell.setCellTempBg(CellBg.SOLID_CIRCLE);
					}
				}
			}
			return true;

		case MotionEvent.ACTION_MOVE:
			if (mCalendarMode == CalendarMode.MULTIPLE_CHOICE) {
				// 起始点
				int firstRow = (int) (mDownY / mColSpace) - 1;
				int firstCol = (int) (mDownX / mRowSpace);

				// 结束点
				int lastRow = (int) (event.getY() / mColSpace) - 1;
				int lastCol = (int) (event.getX() / mRowSpace);

				if (firstRow >= 0 && lastRow >= 0 && lastRow < 6 && firstCol >= 0 && lastCol >= 0 && lastCol < 7) {
					if (((firstRow != lastRow) || (firstCol != lastCol)) && ((lastRow != lastMoveRow) || (lastCol != lastMoveCol))) {
						mesureSlideRange(firstRow, firstCol, lastRow, lastCol);
					}
				}

				lastMoveRow = lastRow;
				lastMoveCol = lastCol;
			}
			return true;

		case MotionEvent.ACTION_UP:
			if (mDownTouch) {
				mDownTouch = false;
				float disX = event.getX() - mDownX;
				float disY = event.getY() - mDownY;
				if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) { // 点击
					int row = (int) (mDownY / mColSpace) - 1;
					int col = (int) (mDownX / mRowSpace);

					// 过滤掉星期的title点击
					if (row >= 0 && col >= 0) {
						mesureClick(row, col);
					}
					performClick();
				}
				return true;
			}
		}

		return false;
	}

	private boolean canClickPreviousDate = true; // 能否点击之前的日期

	public void setCanClickPreviousDate(boolean canClickPreviousDate) {
		this.canClickPreviousDate = canClickPreviousDate;
	}

	private long mStartDateStamp; // 可点击的起始日期
	private long mEndDateStamp; // 可点击的结束日期

	/**
	 * 设置能点击的日期范围(yyyy-MM-dd转化为时间戳)
	 * 
	 * @param startDateStamp
	 * @param endDateStamp
	 */
	public void setCanClickAbleDateRange(long startDateStamp, long endDateStamp) {
		mStartDateStamp = startDateStamp;
		mEndDateStamp = endDateStamp;
	}

	/*
	 * 单选
	 */
	private int mLastClickRow = -1;
	private int mLastClickCol = -1;
	private Cell mLastClickCell;

	/**
	 * 点击
	 * 
	 * @param row
	 * @param col
	 */
	private void mesureClick(int row, int col) {
		int key = 0;
		if (mLastClickRow != -1 && mLastClickCol != -1) {
			key = mLastClickRow * 7 + mLastClickCol;
			mLastClickCell = mCellArray.get(key);
			if (mCalendarMode != CalendarMode.MULTIPLE_CHOICE) {
				if (mLastClickCell.getCellTempBg() != CellBg.SOLID_CIRCLE) {
					mLastClickCell.setCellBg(CellBg.NORMAL);
				} else {
					mLastClickCell.setCellBg(CellBg.SOLID_CIRCLE);
				}
			}
		}
		mLastClickRow = row;
		mLastClickCol = col;

		key = row * 7 + col;
		Cell cell = mCellArray.get(key);
		CustomDate customDate = cell.getCustomDate();

		int year = customDate.getYear();
		int month = customDate.getMonth();
		int day = customDate.getDay();

		if (mCalendarMode == CalendarMode.SINGLE_CHOICE && cell.getTextColor() == clickAbleTextColor) {
			changeClickDateStatus(mLastClickCell, cell, year, month, day);
		} else if (mCalendarMode != CalendarMode.SINGLE_CHOICE) {
			String clickDate = year + "-" + month + "-" + day;
			long clickDateTimeStamp = DateUtil.strToTimestamp(clickDate, Define.FORMAT_YMD);
			if (mStartDateStamp != 0 && mEndDateStamp != 0 && mStartDateStamp < mEndDateStamp) {
				if (clickDateTimeStamp >= mStartDateStamp && clickDateTimeStamp <= mEndDateStamp) {
					changeClickDateStatus(mLastClickCell, cell, year, month, day);
				}
			} else {
				if (canClickPreviousDate) {
					changeClickDateStatus(mLastClickCell, cell, year, month, day);
				} else {
					String currentDate = DateUtil.getCurrentDate();
					long currentDateTimeStamp = DateUtil.strToTimestamp(currentDate);
					if (clickDateTimeStamp >= currentDateTimeStamp) {
						changeClickDateStatus(mLastClickCell, cell, year, month, day);
					}
				}
			}
		}
	}

	/**
	 * 更改点击的日期状态
	 * 
	 * @param cell
	 * @param year
	 * @param month
	 * @param day
	 */
	private void changeClickDateStatus(Cell lastCell, Cell currentCell, int year, int month, int day) {
		if (lastCell != null) {
			lastCell.setSelected(false);
		}
		currentCell.setSelected(true);
		if (currentCell.getCellBg() == CellBg.HOLLOW_CIRCLE) {
			if (currentCell.getCellTempBg() != CellBg.SOLID_CIRCLE) {
				currentCell.setCellBg(CellBg.NORMAL);
			} else {
				currentCell.setCellBg(CellBg.SOLID_CIRCLE);
			}
		} else {
			currentCell.setCellBg(CellBg.HOLLOW_CIRCLE);
		}
		invalidate();

		if (mOnDateClickListener != null) {
			mOnDateClickListener.clickDate(lastCell, currentCell, year, month, day);
		}
	}

	/**
	 * 滑动
	 * 
	 * @param firstRow
	 * @param firstCol
	 * @param lastRow
	 * @param lastCol
	 */
	private void mesureSlideRange(int firstRow, int firstCol, int lastRow, int lastCol) {
		if (mOnDateSlideRangeListener != null) {
			// 开始日期
			int startDateIndex = firstRow * 7 + firstCol;
			Cell cell = mCellArray.get(startDateIndex);
			CustomDate customDate = cell.getCustomDate();

			int startYear = customDate.getYear();
			int startMonth = customDate.getMonth();
			int startDay = customDate.getDay();
			String startDate = startYear + "." + startMonth + "." + startDay;

			// 结束日期
			int endDateIndex = lastRow * 7 + lastCol;
			cell = mCellArray.get(endDateIndex);
			customDate = cell.getCustomDate();

			int endYear = customDate.getYear();
			int endMonth = customDate.getMonth();
			int endDay = customDate.getDay();
			String endDate = endYear + "." + endMonth + "." + endDay;

			if (startDateIndex > endDateIndex) {
				int tempIndex = startDateIndex;
				startDateIndex = endDateIndex;
				endDateIndex = tempIndex;
			}

			String currentDate = DateUtil.getCurrentDate();
			long currentDateTimeStamp = DateUtil.strToTimestamp(currentDate);
			for (int i = 0; i < mCellArray.size(); i++) {
				Cell localCell = mCellArray.get(i);
				String selectDate = localCell.getCustomDate().getYear() + "-" + localCell.getCustomDate().getMonth() + "-" + localCell.getCustomDate().getDay();
				long selectDateTimeStamp = DateUtil.strToTimestamp(selectDate);

				if (selectDateTimeStamp >= currentDateTimeStamp) {
					if (i >= startDateIndex && i <= endDateIndex) {
						localCell.setCellBg(CellBg.HOLLOW_CIRCLE);
					} else {
						if (localCell.getCellTempBg() == CellBg.NORMAL) {
							localCell.setCellBg(CellBg.NORMAL);
						}
					}
				}
			}

			invalidate();

			mOnDateSlideRangeListener.slideDateRange(startDate, endDate);
		}
	}

	private OnDateSlideRangeListener mOnDateSlideRangeListener;

	/**
	 * 设置滑动选择日期范围监听
	 * 
	 * @param onDateSlideRangeListener
	 */
	public void setOnDateSlideRangeListener(OnDateSlideRangeListener onDateSlideRangeListener) {
		mOnDateSlideRangeListener = onDateSlideRangeListener;
	}

	public interface OnDateSlideRangeListener {
		void slideDateRange(String startDate, String endDate);
	}

	public interface OnDateClickListener {
		void clickDate(Cell lastCell, Cell currentCell, int year, int month, int day);
	}

	private OnDateClickListener mOnDateClickListener;

	/**
	 * 设置点击日期监听
	 * 
	 * @param onDateClickListener
	 */
	public void setOnDateClickListener(OnDateClickListener onDateClickListener) {
		mOnDateClickListener = onDateClickListener;
	}

	public interface OnDateChangeListener {
		void dateChangeListener(int year, int month);
	}

	private OnDateChangeListener mOnDateChangeListener;

	public void setOnDateChangeListener(OnDateChangeListener onDateChangeListener) {
		mOnDateChangeListener = onDateChangeListener;
	}

	@Override
	public boolean performClick() {
		super.performClick();
		return true;
	}

	private SparseArray<Cell> mCellArray;

	/**
	 * 填充日期
	 */
	private void fillDate() {
		if (mCellArray == null) {
			mCellArray = new SparseArray<>();
		} else {
			mCellArray.clear();
		}
		int rowNum = 0;
		int colNum = 0;

		Cell cell = null;
		CustomDate customDate = null;
		// 上个月剩余的天数
		for (int i = 0; i < mFirstDayIndex; i++) {
			rowNum = i / 7;
			colNum = i % 7;
			customDate = new CustomDate(mPreviousYear, mpreviousMonth, mPreviousMonthDays - mFirstDayIndex + i + 1);
			cell = new Cell(rowNum, colNum, otherMonthTextColor, customDate, CellBg.NORMAL);
			mCellArray.append(i, cell);
		}

		int index = mCellArray.size();
		// 当前月份
		int currentTextPaintColor = currentMonthTextColor;
		if (mCalendarMode == CalendarMode.SINGLE_CHOICE) {
			currentTextPaintColor = otherMonthTextColor;
		}
		for (int i = 0; i < mCurrentMonthDays; i++) {
			rowNum = (mFirstDayIndex + i) / 7;
			colNum = (mFirstDayIndex + i) % 7;
			customDate = new CustomDate(mCurrentYear, mCurrentMonth, i + 1);
			cell = new Cell(rowNum, colNum, currentTextPaintColor, customDate, CellBg.NORMAL);
			mCellArray.append(index + i, cell);
		}

		index = mCellArray.size();
		Cell lastCell = mCellArray.get(mCellArray.size() - 1);
		// 下个月剩余的天数
		int nextMonthResidueDay = 6 * 7 - index;
		for (int i = 0; i < nextMonthResidueDay; i++) {
			rowNum = (lastCell.getColNum() + i + 1) / 7 + lastCell.getRowNum();
			colNum = (lastCell.getColNum() + i + 1) % 7;
			customDate = new CustomDate(mNextYear, mNextMonth, i + 1);
			cell = new Cell(rowNum, colNum, otherMonthTextColor, customDate, CellBg.NORMAL);
			mCellArray.append(index + i, cell);
		}
	}

	/**
	 * 上一个月
	 */
	public void previousMonth() {

		if (mCurrentMonth > 1) {
			// 当前月
			mCurrentMonth--;
		} else {
			// 当前月
			mCurrentMonth = 12;
			// 当前年
			mCurrentYear--;
		}

		countOtherMonths();

		prepareMonthData();
		fillDate();
		invalidate();

		if (mOnDateChangeListener != null) {
			mOnDateChangeListener.dateChangeListener(mCurrentYear, mCurrentMonth);
		}
	}

	/**
	 * 下一个月
	 */
	public void nextMonth() {
		// 当前页数据的年月
		if (mCurrentMonth < 12) {
			// 当前月
			mCurrentMonth++;
		} else {
			// 当前月
			mCurrentMonth = 1;
			// 当前年
			mCurrentYear++;
		}

		countOtherMonths();
		prepareMonthData();
		fillDate();
		invalidate();

		if (mOnDateChangeListener != null) {
			mOnDateChangeListener.dateChangeListener(mCurrentYear, mCurrentMonth);
		}
	}

	/**
	 * 计算其他月份
	 */
	private void countOtherMonths() {
		// 上一页数据的年月
		if (mCurrentMonth > 1) {
			mpreviousMonth = mCurrentMonth - 1;
			mPreviousYear = mCurrentYear;
		} else {
			mpreviousMonth = 12;
			mPreviousYear = mCurrentYear - 1;
		}

		// 下一页数据的年月
		if (mCurrentMonth < 12) {
			mNextMonth = mCurrentMonth + 1;
			mNextYear = mCurrentYear;
		} else {
			mNextMonth = 1;
			mNextYear = mCurrentYear + 1;
		}
	}

	/**
	 * 准备月份的数据
	 */
	private void prepareMonthData() {
		// 上个月份总共有多少天
		mPreviousMonthDays = DateUtil.getPreviousMonthDays(mCurrentYear, mCurrentMonth);
		// 当前月份总共有多少天
		mCurrentMonthDays = DateUtil.getMonthDays(mCurrentYear, mCurrentMonth);
		// 当前月的1号是周几
		String dateStr = mCurrentYear + "-" + mCurrentMonth + "-1";
		mFirstDayIndex = DateUtil.getWeekIndex(dateStr);
	}

	/**
	 * 获取当前页数据的年份
	 * 
	 * @return
	 */
	public int getCurrentYear() {
		return mCurrentYear;
	}

	/**
	 * 获取当前页数据的月份
	 * 
	 * @return
	 */
	public int getCurrentMonth() {
		return mCurrentMonth;
	}

	/**
	 * 设置可点击的日期
	 * 
	 * @param dateList
	 *            日
	 */
	public void setClickAbleDate(List<Integer> dayList) {
		int position = 0;
		for (int i = 0; i < dayList.size(); i++) {
			position = mFirstDayIndex + dayList.get(i) - 1;
			mCellArray.get(position).setTextColor(clickAbleTextColor);
		}

		invalidate();
	}

	/**
	 * 设置已经设置了内容的日期
	 * 
	 * @param addedContentDates
	 */
	public void setAddedContentDate(List<ContentDate> addedContentDates) {
		int position = 0;
		Cell cell = null;
		ContentDate addedContentDate;
		for (int i = 0; i < addedContentDates.size(); i++) {
			addedContentDate = addedContentDates.get(i);
			String date = addedContentDate.getDate();
			int month = Integer.parseInt(date.substring(5, 7));
			if (month == mCurrentMonth) {
				int day = Integer.parseInt(date.substring(date.lastIndexOf("-") + 1));
				position = mFirstDayIndex + day - 1;
				cell = mCellArray.get(position);
				cell.setCellBg(CellBg.SOLID_CIRCLE);
				cell.setAddContent(addedContentDate.getContent());
			}
		}

		invalidate();
	}

	enum CellBg {
		NORMAL, // 无背景
		SOLID_CIRCLE, // 实心圆
		HOLLOW_CIRCLE; // 空心圆
	}

	public class Cell {
		private int mRowNum;
		private int mColNum;
		private int mTextColor;
		private CustomDate mCustomDate;
		private CellBg mCellBg;
		private CellBg mCellTempBg;
		private Object mAddContent; // 添加的内容
		private boolean isSelected; // 当前选中

		public Cell(CellBg cellBg, Object addContent) {
			super();
			mCellTempBg = mCellBg = cellBg;
			mAddContent = addContent;
		}

		public Cell(int rowNum, int colNum, int textColor, CustomDate customDate, CellBg cellBg) {
			super();
			mRowNum = rowNum;
			mColNum = colNum;
			mTextColor = textColor;
			mCustomDate = customDate;
			mCellTempBg = mCellBg = cellBg;
		}

		public Cell(int rowNum, int colNum, int textColor, CustomDate customDate, CellBg cellBg, Object addContent) {
			super();
			mRowNum = rowNum;
			mColNum = colNum;
			mTextColor = textColor;
			mCustomDate = customDate;
			mCellTempBg = mCellBg = cellBg;
			mAddContent = addContent;
		}

		private String mDrawContent;

		public void drawSelf(Canvas canvas) {
			mDrawContent = String.valueOf(mCustomDate.getDay());
			cellXDistance = mRowSpace / 2 + mRowSpace * mColNum;
			cellYDistance = (int) (mColSpace - mFontMetrics.bottom - (mColSpace - mFontHeight) / 2) + mColSpace * (mRowNum + 1);

			// 日期颜色
			mTextPaint.setColor(mTextColor);

			// 画圆
			switch (mCellBg) {
			case HOLLOW_CIRCLE: // 空心圆
				mHollowCirclePaint.setColor(circleColor);
				canvas.drawCircle((2 * mColNum + 1) * (mRowSpace / 2), (2 * (mRowNum + 1) + 1) * (mColSpace / 2), mCircleRadius, mHollowCirclePaint);
				break;

			case SOLID_CIRCLE:// 实心圆
				mSolidCirclePaint.setColor(circleColor);
				canvas.drawCircle((2 * mColNum + 1) * (mRowSpace / 2), (2 * (mRowNum + 1) + 1) * (mColSpace / 2), mCircleRadius, mSolidCirclePaint);
				break;

			default:
				break;
			}

			if (mCalendarMode == CalendarMode.ADD_DAY_INFO && getAddContent() != null && !isSelected) {
				mSolidCirclePaint.setColor(circleColor);
				canvas.drawCircle((2 * mColNum + 1) * (mRowSpace / 2), (2 * (mRowNum + 1) + 1) * (mColSpace / 2), mCircleRadius, mSolidCirclePaint);
			}

			canvas.drawText(mDrawContent, cellXDistance, cellYDistance, mTextPaint);
		}

		public int getRowNum() {
			return mRowNum;
		}

		public void setRowNum(int mRowNum) {
			this.mRowNum = mRowNum;
		}

		public int getColNum() {
			return mColNum;
		}

		public void setColNum(int mColNum) {
			this.mColNum = mColNum;
		}

		public int getTextColor() {
			return mTextColor;
		}

		public void setTextColor(int textColor) {
			this.mTextColor = textColor;
		}

		public CustomDate getCustomDate() {
			return mCustomDate;
		}

		public void setCustomDate(CustomDate mCustomDate) {
			this.mCustomDate = mCustomDate;
		}

		public CellBg getCellBg() {
			return mCellBg;
		}

		public void setCellBg(CellBg cellBg) {
			this.mCellBg = cellBg;
		}

		public CellBg getCellTempBg() {
			return mCellTempBg;
		}

		public void setCellTempBg(CellBg cellTempBg) {
			this.mCellTempBg = cellTempBg;
		}

		public Object getAddContent() {
			return mAddContent;
		}

		public void setAddContent(Object mAddContent) {
			this.mAddContent = mAddContent;
		}

		public boolean isSelected() {
			return isSelected;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}

		@Override
		public String toString() {
			return "Cell [mRowNum=" + mRowNum + ", mColNum=" + mColNum + ", mTextColor=" + mTextColor + ", mCustomDate=" + mCustomDate + ", mCellBg=" + mCellBg + ", mCellTempBg=" + mCellTempBg + ", mAddContent=" + mAddContent + ", isSelected=" + isSelected + ", mDrawContent=" + mDrawContent + "]";
		}

	}

	public class CustomDate {
		private int year;
		private int month;
		private int day;

		public CustomDate(int year, int month, int day) {
			super();
			this.year = year;
			this.month = month;
			this.day = day;
		}

		public int getYear() {
			return year;
		}

		public void setYear(int year) {
			this.year = year;
		}

		public int getMonth() {
			return month;
		}

		public void setMonth(int month) {
			this.month = month;
		}

		public int getDay() {
			return day;
		}

		public void setDay(int day) {
			this.day = day;
		}

		@Override
		public String toString() {
			return "CustomDate [year=" + year + ", month=" + month + ", day=" + day + "]";
		}

	}

}
