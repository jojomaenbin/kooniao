package com.kooniao.travel.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.api.ApiCaller.APISubmitProductInfoCallback;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.ContentDate;
import com.kooniao.travel.model.ProductInfo;
import com.kooniao.travel.model.ProductStandard;
import com.kooniao.travel.store.DistributionSetActivity.ParamsSetting;
import com.kooniao.travel.store.DistributionSetActivity.SettingWay;
import com.kooniao.travel.utils.JsonTools;
import com.kooniao.travel.view.materialdesign.SwitchButton;

/**
 * 库存定价
 * 
 * @author ke.wei.quan
 * @date 2015年5月26日
 * @version 1.0
 *
 */
@SuppressLint("InflateParams")
@EActivity(R.layout.activity_inventory_pricing)
public class InventoryPricingActivity extends BaseActivity {
	@ViewById(R.id.tv_set_distribution)
	TextView distributionSetTextView; // 设置分销按钮
	@ViewById(R.id.et_market_price)
	EditText marketPriceEditText; // 市场价
	@ViewById(R.id.et_market_unit)
	EditText marketPriceUnitEditText; // 市场价单位
	@ViewById(R.id.sb_min_order)
	SwitchButton minOrderSwitchButton; // 最小起订量
	@ViewById(R.id.et_min_order)
	EditText minOrderEditText; // 最小起订量输入框
	@ViewById(R.id.sb_bookable)
	SwitchButton bookableSwitchButton; // 库存为0是否可预订
	@ViewById(R.id.sb_show_inventory)
	SwitchButton showInventorySwitchButton; // 是否显示库存
	@ViewById(R.id.ll_direct_return_inventory)
	View directReturnInventoryView; // 直接回退至库存
	@ViewById(R.id.tv_direct_return_inventory)
	TextView directReturnInventoryTextView; // 直接回退至库存
	@ViewById(R.id.ll_manual_return_inventory)
	View manualReturnInventoryView; // 手动回退至库存
	@ViewById(R.id.tv_manual_return_inventory)
	TextView manualReturnInventoryTextView; // 手动回退至库存
	@ViewById(R.id.ll_product_standard_container)
	ViewGroup productStandardLayoutContainer; // 产品规格布局填充

	@ViewById(R.id.iv_product_standard_remove)
	ImageView standardLayoutRemoveImageView; // 商品规格布局移除按钮
	@ViewById(R.id.et_product_standard)
	EditText productStandardNameEditText; // 商品规格名称
	@ViewById(R.id.et_current_price)
	EditText currentPriceEditText; // 产品现价
	@ViewById(R.id.et_current_price_unit)
	EditText currentPriceUnitEditText; // 产品现价单位
	@ViewById(R.id.ll_total_inventory)
	View totalInventoryView; // 库存总量
	@ViewById(R.id.tv_total_inventory)
	TextView totalInventoryTextView; // 库存总量
	@ViewById(R.id.ll_everyday_inventory)
	View everydayInventoryView; // 每日库存
	@ViewById(R.id.tv_everyday_inventory)
	TextView everydayInventoryTextView; // 每日库存
	@ViewById(R.id.et_total_inventory)
	EditText totalInventoryEditText; // 库存总量输入框
	@ViewById(R.id.sb_way_deposit)
	SwitchButton depositWaySwitchButton; // 定金方式
	@ViewById(R.id.ll_according_way)
	View accordingWayLayout; // 定金方式布局
	@ViewById(R.id.ll_according_money)
	View accordingMoneyView; // 按金额
	@ViewById(R.id.tv_according_money)
	TextView accordingMoneyTextView; // 按金额
	@ViewById(R.id.ll_according_percent)
	View accordingPercentView; // 按比例
	@ViewById(R.id.tv_according_percent)
	TextView accordingPercentTextView; // 按比例
	@ViewById(R.id.et_according_sum)
	EditText accordingSumEditText; // 填写的金额
	@ViewById(R.id.sb_is_visiable)
	SwitchButton visibilitySwitchButton; // 是否隐藏按钮
	@ViewById(R.id.tv_finish)
	TextView finishTextView; // 完成按钮

	/**
	 * 模式
	 * 
	 * @author ke.wei.quan
	 *
	 */
	enum Mode {
		CREATE_PRODUCT("createProduct"), // 创建产品
		EDIT_PRODUCT("editProduct"); // 编辑产品

		public String mode;

		Mode(String mode) {
			this.mode = mode;
		}
	}

	@AfterViews
	void init() {
		initData();
		initView();
	}

	String mode; // 模式
	ProductInfo productInfo; // 产品编辑详细信息
	HashMap<String, String> paramsMaps; // 上个页面传过来的产品信息
	long serviceTimeStamp; // 服务器时间戳
	String marketPrice;
	String unit;

	/**
	 * 初始化数据
	 */
	@SuppressWarnings("unchecked")
	private void initData() {
		mode = Mode.CREATE_PRODUCT.mode;

		Intent intent = getIntent();
		if (intent != null) {
			paramsMaps = (HashMap<String, String>) intent.getSerializableExtra(Define.DATA);
			serviceTimeStamp = intent.getLongExtra(Define.TIME_STAMP, 0);
			marketPrice = intent.getStringExtra(Define.PRODUCT_PRICE);
			unit = intent.getStringExtra(Define.PRODUCT_UNIT);
			productInfo = (ProductInfo) intent.getSerializableExtra(Define.PRODUCT_INFO);
			if (intent.hasExtra(Define.MODE)) {
				mode = intent.getStringExtra(Define.MODE);
			}
		}
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		totalInventoryEditText.setVisibility(View.GONE);
		if (mode.equals(Mode.CREATE_PRODUCT.mode)) { // 创建产品
			// 默认勾选手动回退至库存
			manualReturnInventoryView.performClick();
			// 默认勾选库存总量
			totalInventoryView.performClick();
			// 默认勾选按金额
			accordingMoneyView.performClick();
		} else { // 编辑产品
			distributionSetTextView.setVisibility(View.INVISIBLE);
		}
		
		if (productInfo != null) {
			setViewInfo();
		}

		/*
		 * 点击最小起订量选择开关
		 */
		minOrderSwitchButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					minOrderEditText.setVisibility(View.VISIBLE);
				} else {
					minOrderEditText.setVisibility(View.GONE);
				}

				judgeFinishSnapClickStatus();
			}
		});

		/*
		 * 点击定金方式选择开关
		 */
		depositWaySwitchButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					accordingWayLayout.setVisibility(View.VISIBLE);
				} else {
					accordingWayLayout.setVisibility(View.GONE);
				}

				judgeFinishSnapClickStatus();
			}
		});
	}

	/**
	 * 初始化界面信息
	 */
	private void setViewInfo() {
		// 市场价
		String marketPriceText = productInfo.getMarketPrice();
		if (marketPriceText != null) {
			if (!marketPriceText.equals("0.00") && !marketPriceText.equals("0.0") && !marketPriceText.equals("0")) {
				marketPriceEditText.setText(marketPriceText);
			}
		}
		// 市场价单位
		String marketPriceUnit = productInfo.getUnit();
		if (marketPriceUnit != null) {
			marketPriceUnitEditText.setText(marketPriceUnit);
		}
		// 最小起订量
		int minBy = productInfo.getMinBuy();
		if (minBy > 0) {
			minOrderSwitchButton.performClick();
			minOrderEditText.setText(minBy + "");
		}
		// 库存为0时候可预订
		int isBook = productInfo.getIsBook();
		if (isBook == 1) {
			bookableSwitchButton.performClick();
		}
		// 是否显示库存
		int isShowStock = productInfo.getIsShowStock();
		if (isShowStock == 1) {
			showInventorySwitchButton.performClick();
		}
		// 退货处理
		int returnStatus = productInfo.getReturnStatus();
		if (returnStatus == 0) { // 手动回退
			manualReturnInventoryView.performClick();
		} else {
			directReturnInventoryView.performClick();
		}

		// 商品规格（套餐列表）
		List<ProductStandard> standardList = productInfo.getPackageList();
		if (standardList != null && !standardList.isEmpty()) {
			ProductStandard defaultStandard = standardList.get(0);
			// 商品规格名
			String productStandardName = defaultStandard.getTitle();
			productStandardNameEditText.setText(productStandardName);
			// 产品现价
			String price = defaultStandard.getPrice();
			if (!price.equals("0.00") && !price.equals("0.0") && !price.equals("0")) {
				currentPriceEditText.setText(price);
			}
			// 产品现价单位
			String unit = defaultStandard.getUnit();
			currentPriceUnitEditText.setText(unit);
			// 库存方式
			int inventoryWay = Integer.valueOf(defaultStandard.getInventoryWay());
			if (inventoryWay == 0) {
				totalInventoryView.performClick();
				// 库存总量
				String stock = defaultStandard.getInventoryTotalCount();
				totalInventoryEditText.setText(stock);
			} else {
				totalInventoryTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				totalInventoryView.setSelected(false);
				totalInventoryTextView.setTextColor(getResources().getColor(R.color.vd0d0d0));

				everydayInventoryTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_blue, 0, 0, 0);
				everydayInventoryView.setSelected(true);
				everydayInventoryTextView.setTextColor(getResources().getColor(R.color.v16b8eb));

				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						totalInventoryEditText.setVisibility(View.GONE);
					}
				}, 200);
			}
			// 定金方式
			int depositWay = Integer.valueOf(defaultStandard.getDepositWay());
			String depositSum = ""; // 输入框
			if (depositWay != 0) {
				depositWaySwitchButton.performClick();
				if (depositWay == 1) { // 按金额
					accordingMoneyView.performClick();
					depositSum = defaultStandard.getDepositMoney();
				} else if (depositWay == 2) { // 按比例
					accordingPercentView.performClick();
					depositSum = defaultStandard.getDepositPercent();
				}
				accordingSumEditText.setText(depositSum);
			}
			// 是否隐藏
			int status = defaultStandard.getStatus();
			if (status == 0) {
				visibilitySwitchButton.performClick();
			}

			for (int i = 1; i < standardList.size(); i++) {
				ProductStandard productStandard = standardList.get(i);
				addProductStandardItemLayout(productStandard);
			}

			judgeFinishSnapClickStatus();
		}
	}

	/**
	 * 商品规格名称输入变化
	 */
	@AfterTextChange(R.id.et_product_standard)
	void onProductStandardNameTextChange() {
		judgeFinishSnapClickStatus();
	}

	/**
	 * 产品现价输入变化
	 */
	@AfterTextChange(R.id.et_current_price)
	void onCurrentPriceTextChange() {
		judgeFinishSnapClickStatus();
	}

	/**
	 * 产品现价单位输入变化
	 */
	@AfterTextChange(R.id.et_current_price_unit)
	void onCurrentPriceUnitTextChange() {
		judgeFinishSnapClickStatus();
	}

	/**
	 * 产品库存总量输入变化
	 */
	@AfterTextChange(R.id.et_total_inventory)
	void onTotalInventoryTextChange() {
		judgeFinishSnapClickStatus();
	}

	/**
	 * 定金方式输入变化
	 */
	@AfterTextChange(R.id.et_according_sum)
	void onAccordingSumTextChange(Editable editable, TextView textView) {
		judgeFinishSnapClickStatus();
		String currentPriceString = currentPriceEditText.getText().toString();
		boolean isPercent = !accordingMoneyView.isSelected();
		judgeInputDepositSum(accordingSumEditText, currentPriceString, isPercent, editable);
	}

	String inputSumText; // 输入的金额

	/**
	 * 判断输入的金额
	 * 
	 * @param editable
	 */
	private void judgeInputDepositSum(EditText editText, String currentPriceString, boolean isPercent, Editable editable) {
		inputSumText = editText.getText().toString();
		if (StringUtils.isEmpty(currentPriceString)) {
			int end = editText.getSelectionEnd();
			if (inputSumText.length() > 0) {
				showToast("请输入产品现价");
				editable.delete(0, end);
				editText.setText(editable);
			}
		} else {
			if (!isPercent) { // 选择的是按金额
				float currentPrice = Float.valueOf(currentPriceString);
				if (!StringUtils.isEmpty(editText.getText().toString())) {
					float depositSum = Float.valueOf(editText.getText().toString());
					if (depositSum > currentPrice) {
						showToast("金额不能大于产品现价");
						int end = editText.getSelectionEnd();
						editText.setText(editText.getText().subSequence(0, end - 1));
						editText.setSelection(editText.getText().length());
					}
				}
			} else { // 按比例
				if (!StringUtils.isEmpty(editText.getText().toString())) {
					int percent = Integer.valueOf(editText.getText().toString());
					if (percent > 100) {
						showToast("比例不能大于100");
						int end = editText.getSelectionEnd();
						editText.setText(editText.getText().subSequence(0, end - 1));
						editText.setSelection(editText.getText().length());
					}
				}
			}
		}
	}

	/**
	 * 最小起订量输入变化
	 */
	@AfterTextChange(R.id.et_min_order)
	void onMinOrderTextChange() {
		judgeFinishSnapClickStatus();
	}

	/**
	 * 直接回退至库存点击
	 */
	@Click(R.id.ll_direct_return_inventory)
	void onDirectReturnToInventoryClick() {
		directReturnInventoryTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_blue, 0, 0, 0);
		directReturnInventoryView.setSelected(true);
		directReturnInventoryTextView.setTextColor(getResources().getColor(R.color.v16b8eb));

		manualReturnInventoryTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		manualReturnInventoryView.setSelected(false);
		manualReturnInventoryTextView.setTextColor(getResources().getColor(R.color.vd0d0d0));
	}

	/**
	 * 手动回退至库存点击
	 */
	@Click(R.id.ll_manual_return_inventory)
	void onManualReturnToInventoryClick() {
		directReturnInventoryTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		directReturnInventoryView.setSelected(false);
		directReturnInventoryTextView.setTextColor(getResources().getColor(R.color.vd0d0d0));

		manualReturnInventoryTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_blue, 0, 0, 0);
		manualReturnInventoryView.setSelected(true);
		manualReturnInventoryTextView.setTextColor(getResources().getColor(R.color.v16b8eb));
	}

	/**
	 * 点击库存总量
	 */
	@Click(R.id.ll_total_inventory)
	void onTotalInventoryClick() {
		totalInventoryTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_blue, 0, 0, 0);
		totalInventoryView.setSelected(true);
		totalInventoryTextView.setTextColor(getResources().getColor(R.color.v16b8eb));

		everydayInventoryTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		everydayInventoryView.setSelected(false);
		everydayInventoryTextView.setTextColor(getResources().getColor(R.color.vd0d0d0));

		totalInventoryEditText.setVisibility(View.VISIBLE);
		int inventory = 0;
		for (ContentDate contentDate : contentDates) {
			inventory += Integer.parseInt(contentDate.getContent().toString());
		}
		if (inventory != 0) {
			totalInventoryEditText.setText(inventory + "");
		}
	}

	boolean isNewAddProductStandard = false; // 是否是新添加的产品规格

	/**
	 * 点击每日库存
	 */
	@Click(R.id.ll_everyday_inventory)
	void onEverydayInventoryClick() {
		totalInventoryTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		totalInventoryView.setSelected(false);
		totalInventoryTextView.setTextColor(getResources().getColor(R.color.vd0d0d0));

		everydayInventoryTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_blue, 0, 0, 0);
		everydayInventoryView.setSelected(true);
		everydayInventoryTextView.setTextColor(getResources().getColor(R.color.v16b8eb));

		totalInventoryEditText.setVisibility(View.GONE);

		isNewAddProductStandard = false;
		Intent intent = new Intent(InventoryPricingActivity.this, EverydayInventoryAccountActivity_.class);
		Bundle extras = new Bundle();
		extras.putSerializable(Define.DATA, (Serializable) contentDates);
		extras.putLong(Define.TIME_STAMP, serviceTimeStamp);
		intent.putExtras(extras);
		startActivityForResult(intent, REQUEST_CODE_EVERY_DAY_INVENTORY);
	}

	List<ContentDate> contentDates = new ArrayList<>(); // 默认的商品规格的每日库存
	Map<Integer, List<ContentDate>> newAddContentDates = new HashMap<>();

	String settingWay;// 设置方式，（手动、模板）
	String paramsSetting; // 手动设置的何种方式（按金额、按比例）
	String paramsSettingSum; // 手动设置方式的输入的金额
	int selectTemplateId;// 模板id

	final int REQUEST_CODE_EVERY_DAY_INVENTORY = 1; // 每日库存
	final int REQUEST_CODE_DISTRIBUTION_SET = 2; // 设置分销

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_EVERY_DAY_INVENTORY:// 每日库存
			if (resultCode == RESULT_OK && data != null) {
				if (!isNewAddProductStandard) {
					contentDates = (List<ContentDate>) data.getSerializableExtra(Define.DATA);
				} else {
					List<ContentDate> contentDates = (List<ContentDate>) data.getSerializableExtra(Define.DATA);
					newAddContentDates.put(newAddStandardClickIndex, contentDates);
				}
			}
			break;

		case REQUEST_CODE_DISTRIBUTION_SET: // 设置分销
			if (resultCode == RESULT_OK && data != null) {
				settingWay = data.getStringExtra(Define.SETTING_WAY);
				paramsSetting = data.getStringExtra(Define.PARAMS_SETTING);
				paramsSettingSum = data.getStringExtra(Define.PARAMS_SETTING_SUM);
				selectTemplateId = data.getIntExtra(Define.TEMPLATE_ID, 0);
				if (productInfo != null) {
					// 分销信息
					setDistributionInfo(); 
				}
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 点击按金额
	 */
	@Click(R.id.ll_according_money)
	void onAccordingMoneyClick() {
		accordingSumEditText.setHint("请输入金额");
		accordingMoneyTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_blue, 0, 0, 0);
		accordingMoneyView.setSelected(true);
		accordingMoneyTextView.setTextColor(getResources().getColor(R.color.v16b8eb));

		accordingPercentTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		accordingPercentView.setSelected(false);
		accordingPercentTextView.setTextColor(getResources().getColor(R.color.vd0d0d0));

		accordingSumEditText.setText("");
	}

	/**
	 * 点击按比例
	 */
	@Click(R.id.ll_according_percent)
	void onAccordingPercentClick() {
		accordingSumEditText.setHint("请输入比例");
		accordingMoneyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		accordingMoneyView.setSelected(false);
		accordingMoneyTextView.setTextColor(getResources().getColor(R.color.vd0d0d0));

		accordingPercentTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_blue, 0, 0, 0);
		accordingPercentView.setSelected(true);
		accordingPercentTextView.setTextColor(getResources().getColor(R.color.v16b8eb));

		accordingSumEditText.setText("");
	}

	/**
	 * 点击添加产品规格
	 */
	@Click(R.id.ll_add_product_standard)
	void onAddProductStandardClick() {
		addProductStandardItemLayout(null);
	}

	int newAddStandardClickIndex;

	// 商品规格名称
	String standardTitle = "";
	String newItemStandardTitle = "";
	// 产品现价
	String currentPrice = "";
	String newItemCurrentPrice = "";
	// 产品现价单位
	String currentPriceUnit = "";
	String newItemCurrentPriceUnit = "";
	// 产品库存总量
	String totalInventory = "";
	String newItemTotalInventory = "";
	// 定金方式金额输入框
	String accordingSum = "";
	String newItemAccordingSum = "";

	/**
	 * 添加一条产品规格布局
	 */
	@SuppressLint("CutPasteId")
	private void addProductStandardItemLayout(ProductStandard productStandard) {
		if (productStandard == null) {
			// 商品规格名称
			standardTitle = productStandardNameEditText.getText().toString();
			// 产品现价
			currentPrice = currentPriceEditText.getText().toString();
			// 产品现价单位
			currentPriceUnit = currentPriceUnitEditText.getText().toString();
			// 产品库存总量
			totalInventory = totalInventoryEditText.getText().toString();
			// 定金方式金额输入框
			accordingSum = accordingSumEditText.getText().toString();
			boolean defaultStandardItemCheckResult = true;
			if (standardTitle.equals("")) {
				defaultStandardItemCheckResult = false;
			}
			if (!currentPriceUnit.equals("") && currentPrice.equals("")) {
				defaultStandardItemCheckResult = false;
			}
			if (totalInventoryView.isSelected() && totalInventory.equals("")) {
				defaultStandardItemCheckResult = false;
			}
			if (depositWaySwitchButton.isChecked() && accordingSum.equals("")) {
				defaultStandardItemCheckResult = false;
			}

			boolean newItemStandardItemCheckResult = true;
			int childCount = productStandardLayoutContainer.getChildCount();
			for (int i = 0; i < childCount; i++) {
				View itemView = productStandardLayoutContainer.getChildAt(i);
				initNewItemView(itemView, i);
				// 商品规格名称
				newItemStandardTitle = newItemProductStandardNameEditText.getText().toString();
				// 产品现价
				newItemCurrentPrice = newItemCurrentPriceEditText.getText().toString();
				// 产品现价单位
				newItemCurrentPriceUnit = newItemCurrentPriceUnitEditText.getText().toString();
				// 产品库存总量
				newItemTotalInventory = newItemTotalInventoryEditText.getText().toString();
				// 定金方式金额输入框
				newItemAccordingSum = newItemAccordingSumEditText.getText().toString();
				if (newItemStandardTitle.equals("")) {
					newItemStandardItemCheckResult = false;
				}
				if (!newItemCurrentPriceUnit.equals("") && newItemCurrentPrice.equals("")) {
					newItemStandardItemCheckResult = false;
				}
				if (newItemTotalInventoryView.isSelected() && newItemTotalInventory.equals("")) {
					newItemStandardItemCheckResult = false;
				}
				if (newItemDepositWaySwitchButton.isChecked() && newItemAccordingSum.equals("")) {
					newItemStandardItemCheckResult = false;
				}
			}

			boolean standardItemCheckResult = defaultStandardItemCheckResult && newItemStandardItemCheckResult;
			if (standardItemCheckResult) {
				addStandardLayout();
			} else {
				showToast("请完成以上的产品规格信息内容！");
			}
		} else {// 填充规格信息
			addStandardLayout();
			// 商品规格名
			String productStandardName = productStandard.getTitle();
			newItemProductStandardNameEditText.setText(productStandardName);
			// 产品现价
			String price = productStandard.getPrice();
			if (!price.equals("0.00") && !price.equals("0.0") && !price.equals("0")) {
				newItemCurrentPriceEditText.setText(price);
			}
			// 产品现价单位
			String unit = productStandard.getUnit();
			newItemCurrentPriceUnitEditText.setText(unit);
			// 库存方式
			int inventoryWay = Integer.valueOf(productStandard.getInventoryWay());
			if (inventoryWay == 0) {
				newItemTotalInventoryView.performClick();
				// 库存总量
				String stock = productStandard.getInventoryTotalCount();
				newItemTotalInventoryEditText.setText(stock);
			} else {
				newItemTotalInventoryTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				newItemTotalInventoryView.setSelected(false);
				newItemTotalInventoryTextView.setTextColor(getResources().getColor(R.color.vd0d0d0));

				newItemEverydayInventoryTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_blue, 0, 0, 0);
				newItemEverydayInventoryView.setSelected(true);
				newItemEverydayInventoryTextView.setTextColor(getResources().getColor(R.color.v16b8eb));

				newItemTotalInventoryEditText.setVisibility(View.GONE);
			}
			// 定金方式
			int depositWay = Integer.valueOf(productStandard.getDepositWay());
			String depositSum = ""; // 输入框
			if (depositWay != 0) {
				if (depositWay == 1) { // 按金额
					newItemAccordingMoneyView.performClick();
					depositSum = productStandard.getDepositMoney();
				} else if (depositWay == 2) { // 按比例
					newItemAccordingPercentView.performClick();
					depositSum = productStandard.getDepositPercent();
				}
				newItemAccordingSumEditText.setText(depositSum);
			}
			// 是否隐藏
			int status = productStandard.getStatus();
			if (status == 0) {
				newItemVisibilitySwitchButton.setChecked(true);
			}

		}
	}

	/**
	 * 添加新的商品规格
	 */
	private void addStandardLayout() {
		View productStandardItemView = LayoutInflater.from(InventoryPricingActivity.this).inflate(R.layout.item_product_standard, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(0, (int) (15 * Define.DENSITY), 0, 0);
		productStandardItemView.setLayoutParams(params);
		productStandardLayoutContainer.addView(productStandardItemView);
		judgeFinishSnapClickStatus();
		int childCount = productStandardLayoutContainer.getChildCount();
		initNewItemView(productStandardItemView, childCount);
	}

	/**
	 * 新添加的商品规格item
	 */
	ImageView removeImageView; // 删除按钮
	EditText newItemProductStandardNameEditText; // 商品规格名称
	EditText newItemCurrentPriceEditText; // 产品现价
	EditText newItemCurrentPriceUnitEditText; // 产品现价单位
	View newItemTotalInventoryView; // 库存总量
	TextView newItemTotalInventoryTextView; // 库存总量
	View newItemEverydayInventoryView; // 每日库存
	TextView newItemEverydayInventoryTextView; // 每日库存
	EditText newItemTotalInventoryEditText; // 库存总量输入框
	SwitchButton newItemDepositWaySwitchButton; // 定金方式
	View newItemAccordingWayLayout; // 定金方式布局
	View newItemAccordingMoneyView; // 按金额
	TextView newItemAccordingMoneyTextView; // 按金额
	View newItemAccordingPercentView; // 按比例
	TextView newItemAccordingPercentTextView; // 按比例
	EditText newItemAccordingSumEditText; // 填写的金额
	SwitchButton newItemVisibilitySwitchButton; // 是否隐藏按钮

	/**
	 * 初始化新添加的商品规格item
	 * 
	 * @param itemView
	 */
	private void initNewItemView(View itemView, int index) {
		// 删除按钮
		removeImageView = (ImageView) itemView.findViewById(R.id.iv_product_standard_remove);
		removeImageView.setTag(itemView);
		removeImageView.setVisibility(View.VISIBLE);
		removeImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View addedView = (View) v.getTag();
				productStandardLayoutContainer.removeView(addedView);
				judgeFinishSnapClickStatus();
			}
		});
		// 名称
		newItemProductStandardNameEditText = (EditText) itemView.findViewById(R.id.et_product_standard);
		newItemProductStandardNameEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				judgeFinishSnapClickStatus();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		// 现价
		newItemCurrentPriceEditText = (EditText) itemView.findViewById(R.id.et_current_price);
		newItemCurrentPriceEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				judgeFinishSnapClickStatus();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		// 现价单位
		newItemCurrentPriceUnitEditText = (EditText) itemView.findViewById(R.id.et_current_price_unit);
		newItemCurrentPriceUnitEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				judgeFinishSnapClickStatus();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		// 库存总量布局
		newItemTotalInventoryView = itemView.findViewById(R.id.ll_total_inventory);
		// 库存总量
		newItemTotalInventoryTextView = (TextView) itemView.findViewById(R.id.tv_total_inventory);
		// 每日库存
		newItemEverydayInventoryTextView = (TextView) itemView.findViewById(R.id.tv_everyday_inventory);
		// 库存总量输入框
		newItemTotalInventoryEditText = (EditText) itemView.findViewById(R.id.et_total_inventory);
		// 每日库存布局
		newItemEverydayInventoryView = itemView.findViewById(R.id.ll_everyday_inventory);
		// 库存总量布局
		newItemTotalInventoryView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				newItemTotalInventoryEditText.setVisibility(View.VISIBLE);
				newItemTotalInventoryTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_blue, 0, 0, 0);
				newItemTotalInventoryView.setSelected(true);
				newItemTotalInventoryTextView.setTextColor(getResources().getColor(R.color.v16b8eb));

				newItemEverydayInventoryTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				newItemEverydayInventoryView.setSelected(false);
				newItemEverydayInventoryTextView.setTextColor(getResources().getColor(R.color.vd0d0d0));

				totalInventoryEditText.setVisibility(View.VISIBLE);
				int inventory = 0;
				List<ContentDate> dates = newAddContentDates.get(newAddStandardClickIndex);
				if (dates != null) {
					for (ContentDate contentDate : dates) {
						inventory += ((int) contentDate.getContent());
					}
				}
				if (inventory != 0) {
					newItemTotalInventoryEditText.setText(inventory + "");
				}
			}
		});
		// 默认选中库存总量
		newItemTotalInventoryView.performClick();
		// 每日库存布局
		newItemEverydayInventoryView.setTag(index);
		newItemEverydayInventoryView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				newItemTotalInventoryTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				newItemTotalInventoryView.setSelected(false);
				newItemTotalInventoryTextView.setTextColor(getResources().getColor(R.color.vd0d0d0));

				newItemEverydayInventoryTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_blue, 0, 0, 0);
				newItemEverydayInventoryView.setSelected(true);
				newItemEverydayInventoryTextView.setTextColor(getResources().getColor(R.color.v16b8eb));

				newItemTotalInventoryEditText.setVisibility(View.GONE);

				newAddStandardClickIndex = (int) newItemEverydayInventoryView.getTag();
				isNewAddProductStandard = true;
				Intent intent = new Intent(InventoryPricingActivity.this, EverydayInventoryAccountActivity_.class);
				Bundle extras = new Bundle();
				List<ContentDate> dates = newAddContentDates.get(newAddStandardClickIndex);
				extras.putSerializable(Define.DATA, (Serializable) dates);
				extras.putLong(Define.TIME_STAMP, serviceTimeStamp);
				intent.putExtras(extras);
				startActivityForResult(intent, REQUEST_CODE_EVERY_DAY_INVENTORY);
			}
		});
		// 库存总量输入框
		newItemTotalInventoryEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				judgeFinishSnapClickStatus();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		// 定金方式开关
		newItemDepositWaySwitchButton = (SwitchButton) itemView.findViewById(R.id.sb_way_deposit);
		newItemDepositWaySwitchButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					newItemAccordingWayLayout.setVisibility(View.VISIBLE);
				} else {
					newItemAccordingWayLayout.setVisibility(View.GONE);
				}

				judgeFinishSnapClickStatus();

				// 默认选中按金额
				if (!newItemAccordingPercentView.isSelected()) {
					newItemAccordingMoneyView.performClick();
				}
			}
		});
		// 定金方式布局
		newItemAccordingWayLayout = itemView.findViewById(R.id.ll_according_way);
		// 按金额布局
		newItemAccordingMoneyView = itemView.findViewById(R.id.ll_according_money);
		newItemAccordingMoneyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				accordingSumEditText.setHint("请输入金额");
				newItemAccordingMoneyTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_blue, 0, 0, 0);
				newItemAccordingMoneyView.setSelected(true);
				newItemAccordingMoneyTextView.setTextColor(getResources().getColor(R.color.v16b8eb));

				newItemAccordingPercentTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				newItemAccordingPercentView.setSelected(false);
				newItemAccordingPercentTextView.setTextColor(getResources().getColor(R.color.vd0d0d0));

				newItemAccordingSumEditText.setText("");
			}
		});
		// 按金额
		newItemAccordingMoneyTextView = (TextView) itemView.findViewById(R.id.tv_according_money);
		// 按比例布局
		newItemAccordingPercentView = itemView.findViewById(R.id.ll_according_percent);
		newItemAccordingPercentView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				accordingSumEditText.setHint("请输入比例");
				newItemAccordingMoneyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				newItemAccordingMoneyView.setSelected(false);
				newItemAccordingMoneyTextView.setTextColor(getResources().getColor(R.color.vd0d0d0));

				newItemAccordingPercentTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_blue, 0, 0, 0);
				newItemAccordingPercentView.setSelected(true);
				newItemAccordingPercentTextView.setTextColor(getResources().getColor(R.color.v16b8eb));

				newItemAccordingSumEditText.setText("");
			}
		});
		// 按比例
		newItemAccordingPercentTextView = (TextView) itemView.findViewById(R.id.tv_according_percent);
		// 输入的金额
		newItemAccordingSumEditText = (EditText) itemView.findViewById(R.id.et_according_sum);
		// 是否隐藏规格开关
		newItemVisibilitySwitchButton = (SwitchButton) itemView.findViewById(R.id.sb_is_visiable);
	}

	/**
	 * 获取商品规格列表数据
	 * 
	 * @return
	 */
	private List<ProductStandard> getProductStandardList() {
		List<ProductStandard> productStandardList = new ArrayList<>();

		int childCount = productStandardLayoutContainer.getChildCount();
		/**
		 * 查找控件和设置监听
		 */
		for (int i = 0; i < childCount; i++) {
			ProductStandard productStandard = new ProductStandard();
			// 日历库存
			List<ContentDate> contentDates = newAddContentDates.get(i);
			productStandard.setContentDates(contentDates);
			View itemView = productStandardLayoutContainer.getChildAt(i);
			// 名称
			EditText standardNameEditText = (EditText) itemView.findViewById(R.id.et_product_standard);
			String standardName = standardNameEditText.getText().toString();
			productStandard.setTitle(standardName);
			// 现价
			EditText currentPriceEditText = (EditText) itemView.findViewById(R.id.et_current_price);
			String curentPrice = currentPriceEditText.getText().toString();
			productStandard.setPrice(curentPrice);
			// 现价单位
			EditText currentPriceUnitEditText = (EditText) itemView.findViewById(R.id.et_current_price_unit);
			String currentPriceUnit = currentPriceUnitEditText.getText().toString();
			productStandard.setUnit(currentPriceUnit);
			// 库存总量
			final View totalInventoryView = itemView.findViewById(R.id.ll_total_inventory);
			// 库存方式
			String inventoryWay = "";
			if (totalInventoryView.isSelected()) { // 库存总量
				inventoryWay = "0";
				EditText totalInventoryEditText = (EditText) itemView.findViewById(R.id.et_total_inventory);
				String totalInventory = totalInventoryEditText.getText().toString();
				productStandard.setInventoryTotalCount(totalInventory);
			} else { // 每日库存
				inventoryWay = "1";
				int inventory = 0;
				for (ContentDate contentDate : newAddContentDates.get(itemView.findViewById(R.id.ll_everyday_inventory).getTag())) {
					inventory += Integer.parseInt(contentDate.getContent().toString());
				}
				productStandard.setInventoryTotalCount(inventory + "");
			}
			productStandard.setInventoryWay(inventoryWay);

			// 库存总量输入框

			// 定金方式开关
			SwitchButton depositWaySwitchButton = (SwitchButton) itemView.findViewById(R.id.sb_way_deposit);
			final View depositWayLayout = itemView.findViewById(R.id.ll_according_way);
			// 按金额
			final View accordingMoneyView = depositWayLayout.findViewById(R.id.ll_according_money);
			final EditText accordingSumEditText = (EditText) depositWayLayout.findViewById(R.id.et_according_sum);
			String depositWay = "0"; // 定金方式
			String depositMoney = accordingSumEditText.getText().toString();
			if (depositWaySwitchButton.isChecked()) {
				if (accordingMoneyView.isSelected()) { // 按金额
					depositWay = "1";
					// 定金方式的输入金额
					productStandard.setDepositMoney(depositMoney);
				} else { // 按比例
					depositWay = "2";
					// 定金方式的输入比例
					productStandard.setDepositPercent(depositMoney);
				}
			}
			productStandard.setDepositWay(depositWay);
			// 是否隐藏
			SwitchButton visiableSwitchButton = (SwitchButton) itemView.findViewById(R.id.sb_is_visiable);
			boolean isVisiable = visiableSwitchButton.isChecked();
			int status = isVisiable ? 0 : 1;
			productStandard.setStatus(status);

			productStandardList.add(productStandard);
		}

		ProductStandard defaultProductStandard = new ProductStandard();
		// 商品规格
		String standardName = productStandardNameEditText.getText().toString();
		defaultProductStandard.setTitle(standardName);
		// 产品现价
		String curentPrice = currentPriceEditText.getText().toString();
		defaultProductStandard.setPrice(curentPrice);
		// 产品现价单位
		String currentPriceUnit = currentPriceUnitEditText.getText().toString();
		defaultProductStandard.setUnit(currentPriceUnit);
		// 库存方式
		String inventoryWay = "";
		if (totalInventoryView.isSelected()) { // 库存总量
			inventoryWay = "0";
			String totalInventory = totalInventoryEditText.getText().toString();
			defaultProductStandard.setInventoryTotalCount(totalInventory);
		} else { // 每日库存
			inventoryWay = "1";
			int inventory = 0;
			for (ContentDate contentDate : contentDates) {
				inventory += Integer.parseInt(contentDate.getContent().toString());
			}
			defaultProductStandard.setInventoryTotalCount(inventory + "");
		}
		defaultProductStandard.setInventoryWay(inventoryWay);
		// 库存总量输入框

		// 定金方式开关
		String depositWay = "0"; // 定金方式
		String depositMoney = accordingSumEditText.getText().toString();
		if (depositWaySwitchButton.isChecked()) {
			if (accordingMoneyView.isSelected()) { // 按金额
				depositWay = "1";
				// 定金方式的输入金额
				defaultProductStandard.setDepositMoney(depositMoney);
			} else { // 按比例
				depositWay = "2";
				// 定金方式的输入比例
				defaultProductStandard.setDepositPercent(depositMoney);
			}
		}
		defaultProductStandard.setDepositWay(depositWay);
		// 是否隐藏
		boolean isVisiable = visibilitySwitchButton.isChecked();
		int status = isVisiable ? 0 : 1;
		defaultProductStandard.setStatus(status);
		productStandardList.add(0, defaultProductStandard);

		return productStandardList;
	}

	/**
	 * 判断完成按钮的点击状态(是否可以点击)
	 */
	private void judgeFinishSnapClickStatus() {
		boolean isMinOrderOpen = minOrderSwitchButton.isChecked();// 如果打开了最小起订量
		// 最小起订量
		String minOrderString = minOrderEditText.getText().toString();

		/*
		 * 商品规格判断
		 */
		// 商品规格名称
		String standardName = productStandardNameEditText.getText().toString();
		// 产品现价
		String currentPriceString = currentPriceEditText.getText().toString();
		// 产品现价单位
		String currentPriceUnit = currentPriceUnitEditText.getText().toString();
		// 产品库存总量
		String productInventorySumString = "";
		if (totalInventoryView.isSelected()) {
			productInventorySumString = totalInventoryEditText.getText().toString();
		} else {
			productInventorySumString = totalInventoryEditText.toString();
		}
		// 定金方式
		boolean isDepositWayOpen = depositWaySwitchButton.isChecked();
		// 定金金额
		String depositMoney = accordingSumEditText.getText().toString();

		/*
		 * 新添加的产品规格
		 */
		boolean newAddedProductStandardResult = true;
		int childCount = productStandardLayoutContainer.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View itemView = productStandardLayoutContainer.getChildAt(i);
			// 名称
			EditText standardNameEditText = (EditText) itemView.findViewById(R.id.et_product_standard);
			// 现价
			EditText currentPriceEditText = (EditText) itemView.findViewById(R.id.et_current_price);
			// 现价单位
			EditText currentPriceUnitEditText = (EditText) itemView.findViewById(R.id.et_current_price_unit);
			// 库存总量
			View totalInventoryView = itemView.findViewById(R.id.ll_total_inventory);
			// 库存总量输入框
			EditText totalInventoryEditText = (EditText) itemView.findViewById(R.id.et_total_inventory);
			// 定金方式开关
			SwitchButton depositWaySwitchButton = (SwitchButton) itemView.findViewById(R.id.sb_way_deposit);
			// 输入的金额
			EditText accordingSumEditText = (EditText) itemView.findViewById(R.id.et_according_sum);

			// 产品规格名称
			String newAddedStandardName = standardNameEditText.getText().toString();
			// 现价
			String newAddedCurrentPrice = currentPriceEditText.getText().toString();
			// 现价价格单位
			String newAddedCurrentPriceUnit = currentPriceUnitEditText.getText().toString();
			// 产品库存总量
			String newAddedTotalInventory = totalInventoryEditText.getText().toString();
			// 定金开关是否开启
			boolean isNewAddedDepositWayOpen = depositWaySwitchButton.isChecked();
			// 定金方式金额
			String newAddedAccordingSum = accordingSumEditText.getText().toString();

			if (newAddedStandardName.equals("") || newAddedCurrentPrice.equals("") || //
					newAddedCurrentPriceUnit.equals("") || (totalInventoryView.isSelected() && newAddedTotalInventory.equals(""))) {
				newAddedProductStandardResult = false;
			}

			if (isNewAddedDepositWayOpen && newAddedAccordingSum.equals("")) {
				newAddedProductStandardResult = false;
			}
		}

		isFinishClickAble = true;

		if (isMinOrderOpen && minOrderString.equals("")) {
			isFinishClickAble = false;
		} else if (!isMinOrderOpen) {
			isFinishClickAble = isFinishClickAble && newAddedProductStandardResult;
		}

		if (standardName.equals("")) {
			isFinishClickAble = false;
		} else {
			isFinishClickAble = isFinishClickAble && newAddedProductStandardResult;
		}

		if (currentPriceString.equals("")) {
			isFinishClickAble = false;
		} else {
			isFinishClickAble = isFinishClickAble && newAddedProductStandardResult;
		}

		if (currentPriceUnit.equals("")) {
			isFinishClickAble = false;
		} else {
			isFinishClickAble = isFinishClickAble && newAddedProductStandardResult;
		}

		if (productInventorySumString.equals("")) {
			isFinishClickAble = false;
		} else {
			isFinishClickAble = isFinishClickAble && newAddedProductStandardResult;
		}

		if (isDepositWayOpen && depositMoney.equals("")) {
			isFinishClickAble = false;
		} else {
			isFinishClickAble = isFinishClickAble && newAddedProductStandardResult;
		}

		// 设置完成按钮状态
		if (isFinishClickAble) {
			finishTextView.setBackgroundResource(R.drawable.blue_round_button_selector);
		} else {
			finishTextView.setBackgroundResource(R.drawable.gray_retangle_full_gray_bg);
		}
	}

	/**
	 * 点击后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		// 商品规格列表
		List<ProductStandard> productStandards = getProductStandardList();
		// 市场价
		String marketPrice = marketPriceEditText.getText().toString();
		// 市场价单位
		String unit = marketPriceUnitEditText.getText().toString();
		// 最小起订量
		String minBuy = minOrderEditText.getText().toString();
		// 库存为0时是否可以预定
		int isBook = bookableSwitchButton.isChecked() ? 1 : 0;
		// 是否显示库存
		int isShowStock = showInventorySwitchButton.isChecked() ? 1 : 0;
		// 退货处理(直接回退至库存是1，手动为0)
		String returnStatus = directReturnInventoryView.isSelected() ? "1" : "2";
		// 库存总量
		int stocksum = 0;
		for (ProductStandard pStandard : productStandards) {
			String stock = pStandard.getInventoryTotalCount();
			if (TextUtils.isEmpty(stock)) {
				stock = "0";
			}
			stocksum += Integer.valueOf(stock);
		}
		setProductInfo(productStandards, marketPrice, unit, minBuy, isBook, isShowStock, returnStatus, stocksum);
		Intent intent = new Intent();
		intent.putExtra(Define.DATA, paramsMaps);
		intent.putExtra(Define.PRODUCT_INFO, productInfo);
		setResult(RESULT_OK, intent);
		finish();

	}

	/**
	 * 点击设置分销按钮
	 */
	@Click(R.id.tv_set_distribution)
	void onSetDistributionClick() {
		Intent intent = new Intent(InventoryPricingActivity.this, DistributionSetActivity_.class);
		if (productInfo != null) {
			// 1:手动、2：模板、0：没有设置
			int type = productInfo.getType();
			if (type == 1) {
				settingWay = SettingWay.MANUAL.type;
			} else if (type == 2) {
				settingWay = SettingWay.TEMPLATE.type;
			} 
			intent.putExtra(Define.SETTING_WAY, settingWay);
			// 按金额、按比例
			int isPercentage = productInfo.getIs_percentage();
			if (isPercentage == 1) {
				paramsSetting = ParamsSetting.PERCENT.type;
				int affiliateCommissionPercentage = productInfo.getAffiliate_commission_percentage();
				if (affiliateCommissionPercentage != 0) {
					paramsSettingSum = affiliateCommissionPercentage + "";
				}
				paramsSettingSum = productInfo.getAffiliate_commission_percentage() + "";
			} else {
				paramsSetting = ParamsSetting.SUM.type;
				int affiliateStatus = productInfo.getAffiliateStatus();
				if (affiliateStatus != 0) {
					paramsSettingSum = affiliateStatus + "";
				}
			}
			intent.putExtra(Define.PARAMS_SETTING, paramsSetting);
			intent.putExtra(Define.PARAMS_SETTING_SUM, paramsSettingSum);
			// 模板id
			selectTemplateId = productInfo.getCommission_theme();
			intent.putExtra(Define.TEMPLATE_ID, selectTemplateId);
		}
		startActivityForResult(intent, REQUEST_CODE_DISTRIBUTION_SET);
	}

	// 完成按钮是否可以点击
	boolean isFinishClickAble = false;

	/**
	 * 点击完成
	 */
	@Click(R.id.tv_finish)
	void onFinishClick() {
		if (isFinishClickAble) {
			// 新添加的产品规格列表
			List<ProductStandard> productStandards = getProductStandardList();
			boolean isHasNoneStandardVisiable = visibilitySwitchButton.isChecked(); // 是否没有一条规格是显示的
			boolean isVisiable;
			for (ProductStandard productStandard : productStandards) {
				isVisiable = productStandard.getStatus() == 0 ? true : false;
				isHasNoneStandardVisiable = isHasNoneStandardVisiable && isVisiable;
			}

			if (isHasNoneStandardVisiable) { // 如果全部是隐藏的
				showToast("在发布产品中请确保至少一个商品规格可见的！");
			} else {
				// 市场价
				String marketPrice = marketPriceEditText.getText().toString();
				// 市场价单位
				String unit = marketPriceUnitEditText.getText().toString();
				// 最小起订量
				String minBuy = minOrderEditText.getText().toString();
				// 库存为0时是否可以预定
				int isBook = bookableSwitchButton.isChecked() ? 1 : 0;
				// 是否显示库存
				int isShowStock = showInventorySwitchButton.isChecked() ? 1 : 0;
				// 退货处理(直接回退至库存是1，手动为0)
				String returnStatus = directReturnInventoryView.isSelected() ? "1" : "2";
				// 库存总量
				int stocksum = 0;
				for (ProductStandard pStandard : productStandards) {
					stocksum += Integer.valueOf(pStandard.getInventoryTotalCount());
				}
				paramsMaps.put("stock", stocksum + "");
				paramsMaps.put("marketPrice", marketPrice);
				paramsMaps.put("unit", unit);
				paramsMaps.put("minBuy", "0");
				if (minOrderSwitchButton.isChecked()) {
					paramsMaps.put("minBuy", minBuy);
				}
				paramsMaps.put("isBook", String.valueOf(isBook));
				paramsMaps.put("isShowStock", String.valueOf(isShowStock));
				paramsMaps.put("returnStatus", returnStatus);
				paramsMaps.put("affiliateStatus", settingWay != null && !settingWay.equals("") ? "1" : "0");
				paramsMaps.put("pricePackage", JsonTools.listToJson(productStandards));
				if (settingWay != null) {
					String typeString = settingWay.equals(SettingWay.MANUAL.type) ? "1" : "2";
					String paramsString = paramsSetting.equals(ParamsSetting.SUM.type) ? "0" : "1";
					String commission_string = "{  \"type\": " + typeString + ",    \"affiliate_commission\": " + paramsSettingSum + ",    \"is_percentage\":" + paramsString + ",     \"affiliate_commission_percentage\": " + paramsSettingSum + ",     \"commission_theme\":" + selectTemplateId + "  }";
					paramsMaps.put("commission", commission_string);
				}
				if (mode.equals(Mode.CREATE_PRODUCT.mode)) {
					showProgressDialog();
					ApiCaller.getInstance().submitProductInfo(paramsMaps, new APISubmitProductInfoCallback() {

						@Override
						public void result(String errMsg, int flag, int pid, int recommendCount) {
							dissmissProgressDialog();
							if (errMsg == null) {
								if (flag == 0)
									showToast("添加产品失败");
								else {
									showToast("添加成功");
									Intent intent = new Intent();
									intent.putExtra(Define.PID, pid);
									intent.putExtra(Define.DATA, recommendCount);
									setResult(RESULT_OK, intent);
									finish();
								}
							} else {
								Toast.makeText(InventoryPricingActivity.this, errMsg, Toast.LENGTH_SHORT).show();
							}
						}
					});
				} else {
					setProductInfo(productStandards, marketPrice, unit, minBuy, isBook, isShowStock, returnStatus, stocksum);
					Intent intent = new Intent(InventoryPricingActivity.this, ProductEditActivity_.class);
					intent.putExtra(Define.DATA, paramsMaps);
					intent.putExtra(Define.PRODUCT_INFO, productInfo);
					setResult(RESULT_OK, intent);
					finish();
				}

			}
		}
	}

	/**
	 * 设置产品信息
	 * 
	 * @param productStandards
	 * @param marketPrice
	 * @param unit
	 * @param minBuy
	 * @param isBook
	 * @param isShowStock
	 * @param returnStatus
	 * @param stocksum
	 */
	private void setProductInfo(List<ProductStandard> productStandards, String marketPrice, String unit, String minBuy, int isBook, int isShowStock, String returnStatus, int stocksum) {
		if (productInfo != null) {
			// 分销信息
			setDistributionInfo(); 
			// 市场价
			productInfo.setMarketPrice(marketPrice);
			// 市场价单位
			productInfo.setUnit(unit);
			// 最小起订量
			if (!TextUtils.isEmpty(minBuy))
				productInfo.setMinBuy(minOrderSwitchButton.isChecked() ? Integer.valueOf(minBuy) : 0);
			// 库存为0是否可预订
			productInfo.setIsBook(isBook);
			// 是否显示库存
			productInfo.setIsShowStock(isShowStock);
			// 退货处理
			productInfo.setReturnStatus(Integer.valueOf(returnStatus));
			// 商品规格列表
			productInfo.setPackageList(productStandards);
			productInfo.setStock(stocksum);
		}
	}

	/**
	 * 设置分销信息
	 */
	private void setDistributionInfo() {
		int type = 0; // 手动、模板
		if (SettingWay.MANUAL.type.equals(settingWay)) {
			type = 1;
			productInfo.setAffiliate_commission(1);
		} else if (SettingWay.TEMPLATE.type.equals(settingWay)) {
			type = 2;
			productInfo.setAffiliate_commission(1);
		} else {
			productInfo.setAffiliate_commission(0);
		}
		productInfo.setType(type);
		// 按金额、按比例
		int isPercentage = 0;
		int affiliateStatus = 0; // 分销金额
		int affiliateCommissionPercentage = 0; // 佣金百分比
		int sum = 0;
		if (!TextUtils.isEmpty(paramsSettingSum)) {
			sum = Integer.parseInt(paramsSettingSum);
		} 
		if (ParamsSetting.PERCENT.type.equals(paramsSetting)) { // 按比例
			isPercentage = 1;
			affiliateCommissionPercentage = sum;
		} else {
			affiliateStatus = sum;
		}
		productInfo.setIs_percentage(isPercentage); 
		productInfo.setAffiliateStatus(affiliateStatus);
		productInfo.setAffiliate_commission_percentage(affiliateCommissionPercentage); 
		productInfo.setCommission_theme(selectTemplateId);
	}
}
