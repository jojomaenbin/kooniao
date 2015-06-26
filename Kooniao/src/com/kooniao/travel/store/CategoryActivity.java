package com.kooniao.travel.store;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.model.ProductCategory.class_0;
import com.kooniao.travel.model.ProductCategory.class_1;
import com.kooniao.travel.model.ProductCategory.class_2;
import com.kooniao.travel.utils.ViewUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * 产品二级三级分类 Created by ZZD on 2015/5/24.
 */
@EActivity(R.layout.activity_category)
public class CategoryActivity extends BaseActivity {
	@ViewById(R.id.ll_participant_info)
	LinearLayout noDataLayout; // 总布局

	@AfterViews
	void init() {
		initData();
		initView();
	}

	private class_0 mclass_0;// 一级目录
	private class_1 mclass_1;// 二
	private class_2 mclass_2;// 三

	public void initData() {
		Intent intent = getIntent();
		if (intent.getSerializableExtra(Define.CLASS1) != null) {
			mclass_0 = (class_0) intent.getSerializableExtra(Define.CLASS1);
		}
		if (intent.getSerializableExtra(Define.CLASS2) != null) {
			mclass_1 = (class_1) intent.getSerializableExtra(Define.CLASS2);
		}
		if (intent.getSerializableExtra(Define.CLASS3) != null) {
			mclass_2 = (class_2) intent.getSerializableExtra(Define.CLASS3);
		}
	}

	@SuppressLint("NewApi")
	public void initView() {
		int padding = ViewUtils.dip2px(CategoryActivity.this, 10);
		if (mclass_0 != null) {
			for (final class_1 c1 : mclass_0.class_1) {
				TextView textView1 = new TextView(CategoryActivity.this);
				textView1.setBackgroundResource(R.color.white);
				textView1.setText(c1.bname);
				textView1.setTag(c1.id);
				textView1.setTextSize(16);
				textView1.setPadding(padding, padding, padding, padding);
				textView1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent data = new Intent(CategoryActivity.this, ProductCategoryActivity_.class);
						data.putExtra(Define.CLASS1, c1);
						if (mclass_1 != null) {
							if (!c1.id.equals(mclass_1.id + "")) {
								data.putExtra(Define.CATEGORY_CHANGE, true);
							}
						}
						setResult(RESULT_OK, data);
						finish();
					}
				});
				noDataLayout.addView(textView1);
				View line1 = new View(CategoryActivity.this);
				line1.setBackgroundResource(R.color.divider_line_bg);
				noDataLayout.addView(line1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
				if (mclass_1 != null) {
					if (c1.id.equals(mclass_1.id + "")) {
						textView1.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.catalog_select, 0);
					}
				}
			}
		} else if (mclass_1 != null) {
			for (final class_2 c2 : mclass_1.class_2) {
				TextView textView1 = new TextView(CategoryActivity.this);
				textView1.setBackgroundResource(R.color.white);
				textView1.setText(c2.bname);
				textView1.setTag(c2.id);
				textView1.setTextSize(16);
				textView1.setPadding(padding, padding, padding, padding);
				textView1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent data = new Intent(CategoryActivity.this, ProductCategoryActivity_.class);
						data.putExtra(Define.CLASS2, c2);
						setResult(RESULT_OK, data);
						finish();
					}
				});
				noDataLayout.addView(textView1);
				View line1 = new View(CategoryActivity.this);
				line1.setBackgroundResource(R.color.divider_line_bg);
				noDataLayout.addView(line1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
				if (mclass_2 != null) {
					if (c2.id.equals(mclass_2.id + "")) {
						textView1.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.catalog_select, 0);
					}
				}
			}
		}

	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}
}
