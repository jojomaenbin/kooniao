package com.kooniao.travel.home;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.CalendarView.Cell;
import com.kooniao.travel.customwidget.CalendarView.OnDateChangeListener;
import com.kooniao.travel.customwidget.CalendarView.OnDateClickListener;
import com.kooniao.travel.customwidget.CalendarWidget;
import com.kooniao.travel.customwidget.ChangeNumView;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.manager.ProductManager;
import com.kooniao.travel.manager.ProductManager.ProductpackageResultCallback;
import com.kooniao.travel.model.ProductDetail;
import com.kooniao.travel.model.ProductPackage;
import com.kooniao.travel.model.ProductPackage.Relation;
import com.kooniao.travel.utils.DateUtil;

/**
 * 选择套餐
 * 
 * @author ke.wei.quan
 * @date 2015年4月28日
 *
 */
@EActivity(R.layout.activity_product_select_package)
public class SelectProductPackageActivity extends BaseActivity {

	@ViewById(R.id.ll_calendar)
	LinearLayout calendarLayout;
	@ViewById(R.id.ll_pacakge_container)
	LinearLayout productPackageLayoutContainer;
	@ViewById(R.id.tv_date)
	TextView dateTextView;
	CalendarWidget calendarWidget;
	@ViewById(R.id.cn_product_package)
	ChangeNumView productPackageNumView;
	@ViewById(R.id.tv_stockpile)
	TextView stockpileTextView;
	@ViewById(R.id.bt_accept)
	Button acceptButton;

	@AfterViews
	void init() {
		initView();
		initData();
		initCalendarLayout();
		loadProductPackage();
	}

	/**
	 * 判断初始化是否需要显示日历
	 */
	private void initCalendarLayout() {
		if (productDetail != null) {
			boolean isShowCalendar = productDetail.isShowCalendar() == 0 ? false : true;
			if (isShowCalendar) {
				calendarLayout.setVisibility(View.VISIBLE);
			} else {
				calendarLayout.setVisibility(View.GONE);
			}
		}
	}

	private KooniaoProgressDialog progressDialog;

	/**
	 * 初始化view
	 */
	private void initView() {
		progressDialog = new KooniaoProgressDialog(SelectProductPackageActivity.this);
		progressDialog.show();
	}

	// 产品id
	private int productId;
	private int storeId; // 店铺id
	private int productType; // 产品类型
	private ProductDetail productDetail;
	private String storeType; // 店铺类型

	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			productId = intent.getIntExtra(Define.PID, 0);
			storeId = intent.getIntExtra(Define.SID, 0);
			productType = intent.getIntExtra(Define.PRODUCT_TYPE, 0);
			productDetail = (ProductDetail) intent.getSerializableExtra(Define.DATA);
			storeType = intent.getStringExtra(Define.STORE_TYPE);
		}
	}

	/**
	 * 获取产品套餐
	 */
	private void loadProductPackage() {
		ProductManager.getInstance().loadProductPackageList(productId, new ProductpackageResultCallback() {

			@Override
			public void result(String errMsg, long serviceTime, int isShowStock, int isMinBuy, int minBuy, int isBook, List<ProductPackage> productPackages) {
				loadProductPackageComplete(errMsg, serviceTime, isShowStock, isMinBuy, minBuy, isBook, productPackages);
			}
		});
	}

	private long mServiceTime; // 服务器时间
	private int mIsShowStock; // 是否显示总库存
	private int mIsBook; // 库存为0是否还可以预定
	private int mIsMinBuy; // 是否设置了最低起订量
	private int mMinBuy; // 最低起订量
	private List<ProductPackage> mProductPackages = new ArrayList<>();

	/**
	 * 请求产品套餐列表完成
	 * 
	 * @param errMsg
	 * @param isBook
	 * @param productPackages
	 */
	private void loadProductPackageComplete(String errMsg, long serviceTime, int isShowStock, int isMinBuy, int minBuy, int isBook, List<ProductPackage> productPackages) {
		progressDialog.dismiss();
		if (errMsg == null) {
			mServiceTime = serviceTime;
			mIsShowStock = isShowStock;
			mIsBook = isBook;
			mIsMinBuy = isMinBuy;
			mMinBuy = minBuy;
			mProductPackages = productPackages;
			setViewData();
		} else {
			Toast.makeText(KooniaoApplication.getInstance(), errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	// 滑动的日历的年月
	private String mDate;
	// 当前选择的日期年月日
	private String mCurrentClickDate;
	private TextView packageItemTextView;
	private String lastClickDateStockpile; // 上次点击的日期的库存text
	private int currentStock; // 当前库存
	private boolean isSelectedDate; // 是否选择了日期

	@SuppressLint("InflateParams")
	private void setViewData() {
		// 初始化日历
		calendarWidget = new CalendarWidget(this, 1, mServiceTime);
		LayoutParams calendarWidgetParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) (Define.DENSITY * 250));
		calendarWidget.setLayoutParams(calendarWidgetParams);
		calendarWidget.setBackgroundColor(getResources().getColor(R.color.white));
		calendarLayout.addView(calendarWidget);

		ProductPackage productPackage = mProductPackages.get(0);
		int stockDate = productPackage.getStockDate();
		if (stockDate == 0) { // 按总库存来显示，不显示日历
			currentStock = productPackage.getStock();
			if (currentStock > 0 && mIsShowStock == 1) {
				lastClickDateStockpile = "/" + getResources().getString(R.string.stockpile) + currentStock;
				stockpileTextView.setText(lastClickDateStockpile);
			} else {
				lastClickDateStockpile = "";
				stockpileTextView.setText(lastClickDateStockpile);
			}
		}

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
				// 填充点击数据
				initCalendarViewData(year, month);
				isSelectedDate = false;
				lastClickDateStockpile = null;
				lastClickDateStockpile = "";
				stockpileTextView.setText(lastClickDateStockpile);
			}
		});

		calendarWidget.setOnDateClickListener(new OnDateClickListener() {

			@Override
			public void clickDate(Cell lastCell, Cell currentCell, int year, int month, int day) {
				isSelectedDate = true;
				mCurrentClickDate = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
				List<Relation> relations = currentProductPackage.getRelationList();
				int index = -1;
				for (int i = 0; i < relations.size(); i++) {
					Relation relation = relations.get(i);
					if (relation.getStockDate().equals(mCurrentClickDate)) {
						index = i;
						break;
					}
				}

				if (index != -1) {
					// 设置库存
					currentStock = relations.get(index).getStock();
					if (currentStock > 0 && mIsShowStock == 1) {
						lastClickDateStockpile = "/" + getResources().getString(R.string.stockpile) + currentStock;
						stockpileTextView.setText(lastClickDateStockpile);
					} else {
						lastClickDateStockpile = null;
						lastClickDateStockpile = "";
						stockpileTextView.setText(lastClickDateStockpile);
					}
				}
			}

		});

		LayoutParams packageItemParams = new LayoutParams(LayoutParams.WRAP_CONTENT, (int) (40 * Define.DENSITY));
		packageItemParams.setMargins(0, 0, (int) (10 * Define.DENSITY), 0);
		ProductPackageItemClickListener packageClickListener = new ProductPackageItemClickListener();

		// 日期库存的判断结果
		boolean stockDateResult = false;
		for (int i = 0; i < mProductPackages.size(); i++) {
			productPackage = mProductPackages.get(i);
			stockDateResult = (productPackage.getRelationList() != null && productPackage.getRelationList().isEmpty())//
					|| (productPackage.getRelationList() == null);

			packageItemTextView = (TextView) LayoutInflater.from(KooniaoApplication.getInstance()).inflate(R.layout.item_package, null);
			packageItemTextView.setLayoutParams(packageItemParams);
			packageItemTextView.setText(productPackage.getTitle());
			// 库存为零可预订就啥都不用判断了，库存为零不可预订那就要判断每个套餐的库存量
			if (mIsBook == 0 && productPackage.getStock() <= 0 || (productPackage.getStockDate() == 1 && stockDateResult)) { // 设置了库存为零不可预订且库存小于等于零，或者日期库存
				packageItemTextView.setBackgroundResource(R.drawable.gray_retangle_cant_click_bg);
			} else {
				packageItemTextView.setTag(i);
				packageItemTextView.setOnClickListener(packageClickListener);
			}
			productPackageLayoutContainer.addView(packageItemTextView, i);
		}

		// 默认选中第一个
		TextView itemView = (TextView) productPackageLayoutContainer.getChildAt(0);
		Object tag = itemView.getTag();
		if (tag != null) {
			itemView.setBackgroundResource(R.drawable.blue_retangle_bg);
			itemView.setTextColor(getResources().getColor(R.color.v16b8eb));
			itemView.performClick();
		} else {
			calendarLayout.setVisibility(View.GONE);
		}

		// 起订量
		if (mIsMinBuy == 0) {// 没设置最低
			mMinBuy = 1;
		}
		productPackageNumView.setTextNum(String.valueOf(mMinBuy));
		productPackageNumView.setMinimumNum(mMinBuy);
	}

	// 上次点击的item的索引位置
	private int lastProductPackageItemClickIndex = -1;
	private List<String> dateStrList = new ArrayList<>();// 设置可点击的日期
	private ProductPackage currentProductPackage;

	class ProductPackageItemClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int index = (int) v.getTag();
			currentProductPackage = mProductPackages.get(index);
			// 还原上次点击的item
			if (lastProductPackageItemClickIndex != -1) {
				TextView lastClickItemView = (TextView) productPackageLayoutContainer.getChildAt(lastProductPackageItemClickIndex);
				lastClickItemView.setBackgroundResource(R.drawable.gray_retangle_bg);
				lastClickItemView.setTextColor(getResources().getColor(R.color.v020202));
			}

			// 设置当前点击的item
			TextView itemView = (TextView) v;
			itemView.setBackgroundResource(R.drawable.blue_retangle_bg);
			itemView.setTextColor(getResources().getColor(R.color.v16b8eb));

			lastProductPackageItemClickIndex = index;

			int stockDate = currentProductPackage.getStockDate();
			if (stockDate == 1) {
				// 当前年
				int currentYear = calendarWidget.getCurrentYear();
				// 当前月
				int currentMonth = calendarWidget.getCurrentMonth();
				initCalendarViewData(currentYear, currentMonth);
				if (lastClickDateStockpile != null && mIsShowStock == 1) {
					stockpileTextView.setText(lastClickDateStockpile);
				} else {
					lastClickDateStockpile = "";
					stockpileTextView.setText(lastClickDateStockpile);
				}
			} else if (stockDate == 0) { // 按总库存显示，不显示日历
				calendarLayout.setVisibility(View.GONE);
				// 设置库存
				currentStock = currentProductPackage.getStock();
				if (currentStock > 0 && mIsShowStock == 1) {
					String stockpile = "/" + getResources().getString(R.string.stockpile) + currentStock;
					stockpileTextView.setText(stockpile);
				} else {
					lastClickDateStockpile = "";
					stockpileTextView.setText(lastClickDateStockpile);
				}
			}

		}

	}

	/**
	 * 填充日历可点击的数据
	 * 
	 * @param currentYear
	 * @param currentMonth
	 */
	private void initCalendarViewData(int currentYear, int currentMonth) {
		calendarLayout.setVisibility(View.VISIBLE);
		// 设置日历标题
		String date = currentYear + getResources().getString(R.string.year) + currentMonth + getResources().getString(R.string.month);
		dateTextView.setText(date);

		String currentDate = currentYear + "-" + (currentMonth < 10 ? "0" + currentMonth : currentMonth);
		List<Relation> relationList = currentProductPackage.getRelationList();
		dateStrList.clear();
		for (int j = 0; j < relationList.size(); j++) {
			Relation relation = relationList.get(j);
			if (relation.getStockDate().startsWith(currentDate)) {
				dateStrList.add(relation.getStockDate());
			}
		}
		calendarWidget.setClickAbleDate(dateStrList);
	}

	/**
	 * 后退点击
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
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
		}
	}

	private Dialog dialog;

	/**
	 * 点击确定按钮
	 */
	@Click(R.id.bt_accept)
	void onAcceptButtonClick() {
		int stockDate = currentProductPackage.getStockDate();
		if (!isSelectedDate && stockDate != 0) {
			Toast.makeText(SelectProductPackageActivity.this, "请您选择产品选购日期，谢谢！", Toast.LENGTH_SHORT).show();
		} else {
			int productPackageNum = productPackageNumView.getTextNum();
			if (mIsBook == 1) { // 库存为0或不足还可以购买
				submitSelectedProductPackage(productPackageNum);
			} else if (mIsBook == 0) {// 库存为0或不足不可以购买
				int totalStockpile = currentProductPackage.getStock();
				if (productPackageNum > currentStock) {
					String tips = "";
					if (stockDate == 0) { // 总库存
						tips = "目前产品库存是" + totalStockpile + "个，您预定了" + productPackageNum + "个，由于库存不足，请选择其他套餐，如有需要请联系  " + productDetail.getMobile();
					} else if (stockDate == 1) { // 每日库存
						tips = "目前产品该日库存是" + currentStock + "个，您目前预定了" + productPackageNum + "个，由于库存不足，请选择其他日期或者其他套餐，如有需要请联系  " + productDetail.getMobile();
					}

					dialog = new Dialog(SelectProductPackageActivity.this, View.VISIBLE, View.GONE, "提示", tips);
					dialog.setCancelable(false);
					dialog.setOnAcceptButtonClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.show();
				} else {
					submitSelectedProductPackage(productPackageNum);
				}
			}
		}
	}

	/**
	 * 提交套餐选择
	 * 
	 * @param count
	 */
	private void submitSelectedProductPackage(int count) {
		Intent intent = null;
		int depositType = currentProductPackage.getDepositType();
		if (depositType == 0) { // 定金类型(0:关闭,不设置定金类型
								// 1:按金额启用 2:按百分比启用)
			intent = new Intent(SelectProductPackageActivity.this, ProductBookingActivity_.class);
		} else {
			intent = new Intent(SelectProductPackageActivity.this, DepositTypeActivity_.class);
		}
		intent.putExtra(Define.DATA, productDetail);
		long dateStamp = 0;
		if (mCurrentClickDate != null) {
			dateStamp = DateUtil.strToTimestamp(mCurrentClickDate, Define.FORMAT_YMD);
		}
		intent.putExtra(Define.DATE, dateStamp); // 选择的日期
		intent.putExtra(Define.ADULT_COUNT, count); // 选择的套餐数
		intent.putExtra(Define.PID, productId); // 产品id
		intent.putExtra(Define.SID, storeId); // 店铺id
		intent.putExtra(Define.PRODUCT_TYPE, productType); // 产品类型
		intent.putExtra(Define.PRICE_ID, currentProductPackage.getPriceId()); // 套餐id
		intent.putExtra(Define.STORE_TYPE, storeType); // 店铺类型
		float price = currentProductPackage.getPrice();
		intent.putExtra(Define.PACKAGE_PRICE, price); // 套餐价格
		intent.putExtra(Define.DEPOSIT, currentProductPackage.getDeposit()); // 定金金额
		intent.putExtra(Define.TITLE, currentProductPackage.getTitle()); // 套餐名称
		intent.putExtra(Define.DEPOSIT_TYPE, depositType); // 定金类型(0:关闭,不设置定金类型
															// 1:按金额启用 2:按百分比启用)
		intent.putExtra(Define.DEPOSIT_PERCENT, currentProductPackage.getDepositPercent()); // 定金百分比
		startActivityForResult(intent, REQUEST_CODE_SELECT_DEPOSIT_TYPE);
	}

	final int REQUEST_CODE_SELECT_DEPOSIT_TYPE = 11;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_SELECT_DEPOSIT_TYPE:
			if (resultCode == RESULT_OK) {
				finish();
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
