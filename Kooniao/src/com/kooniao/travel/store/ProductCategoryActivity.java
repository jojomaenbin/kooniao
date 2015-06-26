package com.kooniao.travel.store;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.*;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.manager.ProductManager;
import com.kooniao.travel.manager.ProductManager.ProductCategoryResultCallback;
import com.kooniao.travel.model.ProductCategory;
import com.kooniao.travel.model.ProductCategory.class_0;

/**
 * 类说明
 *
 * @author zheng.zong.di
 * @version 1.0
 * @date 2015年5月22日
 */
@EActivity(R.layout.activity_product_category)
public class ProductCategoryActivity extends BaseActivity {

	@ViewById(R.id.iv_check_box)
	TextView checkTextView; // 请选择
	@ViewById(R.id.iv_no_data)
	TextView no_dataTextView; // 无内容
	@ViewById(R.id.tv_next_step)
	Button nextstep; // 下一步
	@ViewById(R.id.lv_third_layout)
	LinearLayout third_layout; // 第三分类
	@ViewById(R.id.iv_check_third_box)
	TextView tv_ThirdTextView; // 第三分类

	@AfterViews
	void init() {
		initData();
		initView();
		loadCategoryDetail();
	}

	public void initData() {
	}

	public void initView() {

	}

	private ProductCategory mClass_List;
	private class_0 lineclass0;
	private class_0 singleclass0;
	private class_0 groupclass0;

	/**
	 * 加载分类详情详情
	 */
	private void loadCategoryDetail() {
		showProgressDialog();
		ProductManager.getInstance().loadProductCategory(new ProductCategoryResultCallback() {

			@Override
			public void result(String errMsg, ProductCategory class_List) {
				if (errMsg == null && class_List != null) {
					dissmissProgressDialog();
					mClass_List = class_List;
					for (class_0 c0 : mClass_List.classList.class_0) {
						if (c0.bname.equals("线路产品")) {
							lineclass0 = c0;
						}
						if (c0.bname.equals("单一产品")) {
							singleclass0 = c0;
						}
						if (c0.bname.equals("组合产品")) {
							groupclass0 = c0;
							mClass_List.cl0 = groupclass0;
						}
					}
				} else {
					finish();
					Toast.makeText(ProductCategoryActivity.this, errMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 点击下一步
	 */
	@Click(R.id.tv_next_step)
	void onNextClick() {
		if (mClass_List != null) {
			Intent intent = new Intent(ProductCategoryActivity.this, ProductInfoActivity_.class);
			intent.putExtra(Define.CATEGORY, mClass_List);
			startActivityForResult(intent, REQUEST_CODE_PRODUCT_ADDSUCCESS);
		}
	}

	final int REQUEST_CODE_SECOND_CLASS = 11; // 第二类型
	final int REQUEST_CODE_THIRD_CLASS = 12; // 第三类型
	final int REQUEST_CODE_PRODUCT_ADDSUCCESS = 24;// 产品添加完成

	/**
	 * 点击二级分类
	 */
	@Click(R.id.iv_check_box)
	void onCheckClick() {
		if (mClass_List.cl0 != null) {
			Intent intent = new Intent(ProductCategoryActivity.this, CategoryActivity_.class);
			intent.putExtra(Define.CLASS1, mClass_List.cl0);
			intent.putExtra(Define.CLASS2, mClass_List.cl1);
			startActivityForResult(intent, REQUEST_CODE_SECOND_CLASS);
		}

	}

	/**
	 * 点击三级分类
	 */
	@Click(R.id.iv_check_third_box)
	void onThirdCheckClick() {
		if (mClass_List.cl1 != null) {
			Intent intent = new Intent(ProductCategoryActivity.this, CategoryActivity_.class);
			intent.putExtra(Define.CLASS2, mClass_List.cl1);
			intent.putExtra(Define.CLASS3, mClass_List.cl2);
			startActivityForResult(intent, REQUEST_CODE_THIRD_CLASS);
		}

	}

	/**
	 * 点击组合产品
	 */
	@Click(R.id.tv_group_product)
	void onCheckGroupClick() {
		no_dataTextView.setVisibility(View.VISIBLE);
		checkTextView.setVisibility(View.GONE);
		third_layout.setVisibility(View.GONE);
		nextstep.setClickable(true);
		mClass_List.cl0 = groupclass0;
		nextstep.setBackgroundResource(R.drawable.blue_retancle_button_selector);
	}

	/**
	 * 点击线性产品
	 */
	@Click(R.id.tv_line_product)
	void onCheckLineClick() {
		no_dataTextView.setVisibility(View.GONE);
		checkTextView.setVisibility(View.VISIBLE);
		checkTextView.setText(R.string.please_check);
		nextstep.setClickable(false);
		third_layout.setVisibility(View.GONE);
		nextstep.setBackgroundResource(R.color.vd0d0d0);
		mClass_List.cl0 = lineclass0;
	}

	/**
	 * 点击单一产品
	 */
	@Click(R.id.tv_single_product)
	void onCheckSingleClick() {
		no_dataTextView.setVisibility(View.GONE);
		checkTextView.setVisibility(View.VISIBLE);
		checkTextView.setText(R.string.please_check);
		third_layout.setVisibility(View.GONE);
		nextstep.setBackgroundResource(R.color.vd0d0d0);
		nextstep.setClickable(false);
		mClass_List.cl0 = singleclass0;
	}

	/**
	 * 找资源
	 */
	@Click(R.id.tv_find_resource)
	void onCheckFindClick() {
		if (mClass_List != null) {
			no_dataTextView.setVisibility(View.VISIBLE);
			checkTextView.setVisibility(View.GONE);
			nextstep.setClickable(false);
			third_layout.setVisibility(View.GONE);
			mClass_List.cl0 = null;
			nextstep.setBackgroundResource(R.color.vd0d0d0);
			Intent intent = new Intent(ProductCategoryActivity.this, ProductResourceLibActivity_.class);
			intent.putExtra(Define.DATA, mClass_List);
			startActivityForResult(intent, REQUEST_CODE_PRODUCT_ADDSUCCESS);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_SECOND_CLASS:
				mClass_List.cl1 = (ProductCategory.class_1) data.getSerializableExtra(Define.CLASS1);
				checkTextView.setText(mClass_List.cl1.bname);
				boolean changeable=data.getBooleanExtra(Define.CATEGORY_CHANGE, false);
				if (changeable) {
					tv_ThirdTextView.setText("请选择");
					nextstep.setBackgroundResource(R.color.vd0d0d0);
					nextstep.setClickable(false);
				}
				third_layout.setVisibility(View.VISIBLE);
				break;
			case REQUEST_CODE_THIRD_CLASS:
				mClass_List.cl2 = (ProductCategory.class_2) data.getSerializableExtra(Define.CLASS2);
				tv_ThirdTextView.setText(mClass_List.cl2.bname);
				nextstep.setBackgroundResource(R.drawable.blue_retancle_button_selector);
				nextstep.setClickable(true);
				break;
			case REQUEST_CODE_PRODUCT_ADDSUCCESS:
				setResult(RESULT_OK,data);
				finish();
				break;

			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}
}
