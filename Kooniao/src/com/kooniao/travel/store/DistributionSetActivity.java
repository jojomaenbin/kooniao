package com.kooniao.travel.store;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.StoreManager;
import com.kooniao.travel.manager.StoreManager.DistributionTemplateListResultCallback;
import com.kooniao.travel.manager.StoreManager.StringResultCallback;
import com.kooniao.travel.model.DistributionTemplate;

/**
 * 分销设置
 * 
 * @author ke.wei.quan
 * @date 2015年5月27日
 * @version 1.0
 *
 */
@SuppressLint("InflateParams")
@EActivity(R.layout.activity_distribution_set)
public class DistributionSetActivity extends BaseActivity {
	@ViewById(R.id.ll_setting_way_manual)
	View manualSettingView; // 手动设置
	@ViewById(R.id.tv_setting_way_manual)
	TextView manualSettingTextView; // 手动设置
	@ViewById(R.id.ll_template_setting)
	View templateSettingView; // 模板设置
	@ViewById(R.id.tv_template_setting)
	TextView templateSettingTextView; // 模板设置
	@ViewById(R.id.rl_params_setting)
	View paramsSettingLayout; // 参数设置布局
	@ViewById(R.id.ll_according_money)
	View accordingMoneyView; // 按金额
	@ViewById(R.id.tv_according_money)
	TextView accordingMoneyTextView; // 按金额
	@ViewById(R.id.ll_according_percent)
	View accordingPercentView; // 按百分比
	@ViewById(R.id.tv_according_percent)
	TextView accordingPercentTextView; // 按百分比
	@ViewById(R.id.et_commission_sum)
	EditText commissionSumEditText; // 佣金金额
	@ViewById(R.id.tv_template_empty_tips)
	TextView templateEmptyTipsTextView; // 没有模板的提示
	@ViewById(R.id.ll_commission_template)
	View commissionLayout; // 模板列表布局
	@ViewById(R.id.lv_commission_template)
	ListView templateListview; // 模板列表
	@ViewById(R.id.tv_finish)
	TextView finishTextView; // 完成按钮

	@AfterViews
	void init() {
		initData();
		initView();
	}

	/**
	 * 设置方式
	 * 
	 * @author ke.wei.quan
	 *
	 */
	enum SettingWay {
		MANUAL("manually"), // 手动设置
		TEMPLATE("template"); // 模板设置

		public String type;

		SettingWay(String type) {
			this.type = type;
		}
	}
	
	/**
	 * 模式
	 * @author ke.wei.quan
	 *
	 */
	enum Mode {
		NORMAL("normal"), // 普通
		EDIT("edit"); // 编辑
		
		public String mode;

		Mode(String mode) {
			this.mode = mode;
		}
	}

	/**
	 * 参数设置
	 * 
	 * @author ke.wei.quan
	 *
	 */
	enum ParamsSetting {
		SUM("sum"), // 按金额
		PERCENT("percent"); // 按比例
		public String type;

		ParamsSetting(String type) {
			this.type = type;
		}
	}

	String mode; // 模式
	int productId; // 产品id
	String settingWay; // 设置方式
	String paramsSetting; // 参数设置
	String paramsSettingSum; // 参数设置的金额
	int templateId; // 模板id

	TemplateAdapter adapter;

	/**
	 * 初始化数据
	 */
	private void initData() {
		mode = Mode.NORMAL.mode;
		
		Intent intent = getIntent();
		if (intent != null) {
			productId = intent.getIntExtra(Define.PID, -1);
			settingWay = intent.getStringExtra(Define.SETTING_WAY);
			paramsSetting = intent.getStringExtra(Define.PARAMS_SETTING);
			paramsSettingSum = intent.getStringExtra(Define.PARAMS_SETTING_SUM);
			templateId = intent.getIntExtra(Define.TEMPLATE_ID, 0);
			
			if (intent.hasExtra(Define.MODE)) {
				mode = intent.getStringExtra(Define.MODE);
			}
		}

		adapter = new TemplateAdapter();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		// 没有模板的布局字体颜色更改
		SpannableString spannableString = new SpannableString(templateEmptyTipsTextView.getText());
		ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.v16b8eb));
		spannableString.setSpan(foregroundColorSpan, 10, 14, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		templateEmptyTipsTextView.setText(spannableString);

		// 初始化设置方式和参数设置
		if (SettingWay.MANUAL.type.equals(settingWay)) { // 手动设置
			manualSettingView.performClick();
			if (ParamsSetting.SUM.type.equals(paramsSetting)) {
				accordingMoneyView.performClick();
			} else {
				accordingPercentView.performClick();
			}
		} else if (SettingWay.TEMPLATE.type.equals(settingWay)) { // 模板设置
			templateSettingView.performClick();
		}

		templateListview.setAdapter(adapter);
		templateListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				isInit = false;
				DistributionTemplate template = distributionTemplateList.get(position);
				template.setSelected(true);
				selectTemplateId = template.getId();
				if (lastSelectedItemIndex != position) {
					if (lastSelectedItemIndex != -1) {
						DistributionTemplate lastSelectedTemplate = distributionTemplateList.get(lastSelectedItemIndex);
						lastSelectedTemplate.setSelected(false);
					}
					lastSelectedItemIndex = position;
					isFinishClickAble = true;
					finishTextView.setBackgroundResource(R.drawable.blue_round_button_selector);
				}
				adapter.notifyDataSetChanged();
			}
		});
		
		// 编辑模式
		if (mode.equals(Mode.EDIT.mode)) {
			finishTextView.setVisibility(View.INVISIBLE); 
		}
	}

	boolean isFinishClickAble; // 完成按钮是否可以点击

	/**
	 * 佣金金额变化
	 */
	@AfterTextChange(R.id.et_commission_sum)
	void onCommissionSumTextChange() {
		if (manualSettingView.isSelected()) {
			if (!commissionSumEditText.getText().toString().equals("")) {
				isFinishClickAble = true;
				finishTextView.setBackgroundResource(R.drawable.blue_round_button_selector);
			} else {
				isFinishClickAble = false;
				finishTextView.setBackgroundResource(R.drawable.gray_retangle_full_gray_bg);
			}
		}
	}

	/**
	 * 点击返回
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		if (mode.equals(Mode.EDIT.mode)) {
			updateAndGoBack(); 
		} else {
			finish();
		}
	}

	/**
	 * 点击添加模板
	 */
	@Click(R.id.tv_add_template)
	void onAddTemplateClick() {
		addTemplate();
	}

	/**
	 * 点击没有模板提示文字的添加模板按钮
	 */
	@Click(R.id.tv_template_empty_tips)
	void onTipsAddTemplateClick() {
		addTemplate();
	}

	/**
	 * 添加模板
	 */
	private void addTemplate() {
		Intent intent = new Intent(DistributionSetActivity.this, AddDistributionActivity_.class);
		startActivityForResult(intent, REQUEST_CODE_ADD_TEMPLATE);
	}

	final int REQUEST_CODE_ADD_TEMPLATE = 1; // 添加模板

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_ADD_TEMPLATE: // 添加模板
			if (resultCode == RESULT_OK && data != null) {
				if (templateSettingView.isSelected()) {
					loadDistributionTemplateList();
				}
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 点击手动设置
	 */
	@Click(R.id.ll_setting_way_manual)
	void onManualSettingClick() {
		manualSettingTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_blue, 0, 0, 0);
		manualSettingView.setSelected(true);
		manualSettingTextView.setTextColor(getResources().getColor(R.color.v16b8eb));

		templateSettingTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		templateSettingView.setSelected(false);
		templateSettingTextView.setTextColor(getResources().getColor(R.color.vd0d0d0));

		// 默认选中按金额
		if (!accordingPercentView.isSelected()) {
			accordingMoneyView.performClick();
		}

		if (!commissionSumEditText.getText().toString().equals("")) {
			isFinishClickAble = true;
			finishTextView.setBackgroundResource(R.drawable.blue_round_button_selector);
		} else {
			isFinishClickAble = false;
			finishTextView.setBackgroundResource(R.drawable.gray_retangle_full_gray_bg);
		}

		paramsSettingLayout.setVisibility(View.VISIBLE);
		templateEmptyTipsTextView.setVisibility(View.GONE);
		commissionLayout.setVisibility(View.GONE);
	}

	/**
	 * 点击按金额
	 */
	@Click(R.id.ll_according_money)
	void onAccordingMoneyClick() {
		accordingMoneyTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_blue, 0, 0, 0);
		accordingMoneyView.setSelected(true);
		accordingMoneyTextView.setTextColor(getResources().getColor(R.color.v16b8eb));

		accordingPercentTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		accordingPercentView.setSelected(false);
		accordingPercentTextView.setTextColor(getResources().getColor(R.color.vd0d0d0));

		commissionSumEditText.setHint("请输入该产品佣金金额");
		if (paramsSettingSum != null) {
			if (ParamsSetting.SUM.type.equals(paramsSetting)) {
				commissionSumEditText.setText(paramsSettingSum);
				commissionSumEditText.setSelection(paramsSettingSum.length());
			} else {
				commissionSumEditText.setText("");
			}
		}
	}

	/**
	 * 点击按比例
	 */
	@Click(R.id.ll_according_percent)
	void onAccordingPercentClick() {
		accordingMoneyTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		accordingMoneyView.setSelected(false);
		accordingMoneyTextView.setTextColor(getResources().getColor(R.color.vd0d0d0));

		accordingPercentTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_blue, 0, 0, 0);
		accordingPercentView.setSelected(true);
		accordingPercentTextView.setTextColor(getResources().getColor(R.color.v16b8eb));

		commissionSumEditText.setHint("请输入该产品佣金所占比例");
		if (paramsSettingSum != null) {
			if (ParamsSetting.PERCENT.type.equals(paramsSetting)) {
				commissionSumEditText.setText(paramsSettingSum);
				commissionSumEditText.setSelection(paramsSettingSum.length());
			} else {
				commissionSumEditText.setText("");
			}
		}
	}

	boolean isNeedToLoadDistributionTemplateList = true; // 是否需要请求模板设置列表

	/**
	 * 点击模板设置
	 */
	@Click(R.id.ll_template_setting)
	void onTemplateSettingClick() {
		manualSettingTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		manualSettingView.setSelected(false);
		manualSettingTextView.setTextColor(getResources().getColor(R.color.vd0d0d0));

		templateSettingTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick_blue, 0, 0, 0);
		templateSettingView.setSelected(true);
		templateSettingTextView.setTextColor(getResources().getColor(R.color.v16b8eb));

		paramsSettingLayout.setVisibility(View.GONE);
		if (!distributionTemplateList.isEmpty()) {
			templateEmptyTipsTextView.setVisibility(View.GONE);
			commissionLayout.setVisibility(View.VISIBLE);
		} else {
			templateEmptyTipsTextView.setVisibility(View.VISIBLE);
		}

		if (isNeedToLoadDistributionTemplateList) {
			loadDistributionTemplateList();
			isNeedToLoadDistributionTemplateList = false;
		}
		isFinishClickAble = true;
		finishTextView.setBackgroundResource(R.drawable.blue_round_button_selector);
	}

	List<DistributionTemplate> distributionTemplateList = new ArrayList<>();

	/**
	 * 获取分销模板列表
	 */
	void loadDistributionTemplateList() {
		showProgressDialog();
		StoreManager.getInstance().loadDistributionTemplateList(new DistributionTemplateListResultCallback() {

			@Override
			public void result(String errMsg, List<DistributionTemplate> distributionTemplates) {
				dissmissProgressDialog();
				if (errMsg == null) {
					distributionTemplateList = distributionTemplates;
					if (!distributionTemplateList.isEmpty()) {
						if (templateId<=0) {
							for (int i = 0; i < distributionTemplateList.size(); i++) {
								if (distributionTemplateList.get(i).getDefaultTemplate()==1) {
									templateId=distributionTemplateList.get(i).getId();
								}
							}
							if (templateId<=0)
							templateId=distributionTemplateList.get(0).getId();
						}
						templateEmptyTipsTextView.setVisibility(View.GONE);
						commissionLayout.setVisibility(View.VISIBLE);
					} else {
						templateEmptyTipsTextView.setVisibility(View.VISIBLE);
					}
					adapter.notifyDataSetChanged();
				} else {
					showToast(errMsg);
				}
			}
		});
	}

	int isPercentage = 0; // 是否是百分比
	String paramsSettingSumTemp = ""; // 金额
	int selectTemplateId; // 模板设置选择的佣金模板id

	/**
	 * 点击完成
	 */
	@Click(R.id.tv_finish)
	void onFinishClick() {
		if (isFinishClickAble) {
			updateAndGoBack();
		}
	}

	/**
	 * 完成并返回
	 */
	private void updateAndGoBack() {
		final Intent intent = new Intent();
		// 设置方式
		String settingWay = manualSettingView.isSelected() ? SettingWay.MANUAL.type : SettingWay.TEMPLATE.type;
		intent.putExtra(Define.SETTING_WAY, settingWay);
		// 参数设置(如果选择的是手动设置)
		if (manualSettingView.isSelected()) {
			paramsSetting = accordingMoneyView.isSelected() ? ParamsSetting.SUM.type : ParamsSetting.PERCENT.type;
			intent.putExtra(Define.PARAMS_SETTING, paramsSetting);
			paramsSettingSum = commissionSumEditText.getText().toString();
			intent.putExtra(Define.PARAMS_SETTING_SUM, paramsSettingSum);
		} else {
			intent.putExtra(Define.TEMPLATE_ID, selectTemplateId);
		}

		if (productId != -1) {
			if (mode.equals(Mode.NORMAL.mode)) {
				showProgressDialog();
			}

			if (manualSettingView.isSelected()) { // 手动设置
				isPercentage = ParamsSetting.PERCENT.type.equals(paramsSetting) ? 1 : 0;
				paramsSettingSumTemp = commissionSumEditText.getText().toString();

				if (paramsSettingSumTemp.equals("")) {
					dissmissProgressDialog();
					if (accordingMoneyView.isSelected()) { // 按金额
						showToast(commissionSumEditText.getHint());
					} else if (accordingPercentView.isSelected()) { // 按比例
						showToast(commissionSumEditText.getHint());
					}
					return;
				} else {
					if (mode.equals(Mode.EDIT.mode)) { 
						dissmissProgressDialog();
						setResult(RESULT_OK, intent);
						finish();
					} else {
						submitDistributionSetting(intent, settingWay, templateId);
					}
				}
			} else {
				if (lastSelectedItemIndex != -1) {
					DistributionTemplate lastSelectedTemplate = distributionTemplateList.get(lastSelectedItemIndex);
					paramsSettingSumTemp = lastSelectedTemplate.getValue() + "";
					isPercentage = lastSelectedTemplate.getType();
					int templateIdTemp = lastSelectedTemplate.getId();
					if (templateIdTemp == templateId) {
						dissmissProgressDialog();
						intent.putExtra(Define.TEMPLATE_ID, templateId);
						setResult(RESULT_OK, intent);
						finish();
					} else {
						submitDistributionSetting(intent, settingWay, templateIdTemp);
					}
				} else {
					finish();
				}
			}

		} else {
			setResult(RESULT_OK, intent);
			finish();
		}
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * 提交分销设置
	 * 
	 * @param intent
	 * @param settingWay
	 */
	private void submitDistributionSetting(final Intent intent, String settingWay, final int templateId) {
		StoreManager.getInstance().updateDistributionSetting(productId, settingWay, isPercentage, paramsSettingSumTemp, templateId, new StringResultCallback() {

			@Override
			public void result(String errMsg) {
				dissmissProgressDialog();
				if (errMsg == null) {
					intent.putExtra(Define.TEMPLATE_ID, templateId);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					showToast(errMsg);
				}
			}
		});
	}

	int lastSelectedItemIndex = -1; // 上次选择的item的位置索引
	boolean isInit = true; // 是否是初始化

	class TemplateAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return distributionTemplateList.size();
		}

		@Override
		public Object getItem(int position) {
			return distributionTemplateList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(DistributionSetActivity.this).inflate(R.layout.item_distribution_template, null);
				viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.tv_template_title);
				viewHolder.moneyTextView = (TextView) convertView.findViewById(R.id.tv_money);
				viewHolder.selectedImageView = (ImageView) convertView.findViewById(R.id.iv_select);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			DistributionTemplate template = distributionTemplateList.get(position);
			String title = template.getTitle();
			int type = template.getType();// 佣金类型 1:按百分比; 2:直接给出佣金
			float value = template.getValue();// 百分比/金额
			DecimalFormat df = new DecimalFormat("0.00");
			String money = df.format(value);
			if (type == 1) { // 按比例
				money = money + "%";
				title = title + "(" + getResources().getString(R.string.according_percent) + ")";
			} else { // 按金额
				money = "¥" + money;
				title = title + "(" + getResources().getString(R.string.according_money) + ")";
			}

			viewHolder.titleTextView.setText(title);
			viewHolder.moneyTextView.setText(money);
			if (isInit) {
				if (templateId != 0) {
					if (templateId == template.getId()) {
						isInit = false;
						selectTemplateId = templateId;
						lastSelectedItemIndex = position;
						template.setSelected(true);
						isFinishClickAble = true;
						finishTextView.setBackgroundResource(R.drawable.blue_round_button_selector);
					} else {
						template.setSelected(false);
					}
				} else {
					int defaultTemplate = template.getDefaultTemplate();// 默认模板
					if (defaultTemplate == 1) {
						selectTemplateId = template.getId();
						lastSelectedItemIndex = position;
						template.setSelected(true);
						isFinishClickAble = true;
						finishTextView.setBackgroundResource(R.drawable.blue_round_button_selector);
					} else {
						template.setSelected(false);
					}
				}
			}

			if (template.isSelected()) {
				viewHolder.selectedImageView.setVisibility(View.VISIBLE);
			} else {
				viewHolder.selectedImageView.setVisibility(View.GONE);
			}

			return convertView;
		}

	}

	static class ViewHolder {
		TextView titleTextView; // 标题
		TextView moneyTextView; // 金额
		ImageView selectedImageView; // 是否已选择的勾
	}

}
