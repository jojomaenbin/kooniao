package com.kooniao.travel.store;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.content.Intent;
import android.widget.EditText;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;

/**
 * 类说明
 * @author ZZD
 * @date 2015年6月8日
 * @version 1.0
 *
 */
@EActivity(R.layout.activity_product_introduce)
public class ProductIntroduceActivity extends BaseActivity {
	@ViewById(R.id.ed_product_introduce)
	EditText introduceEdit; // 产品介绍

	private String mProductIntroduceString;
	
	@AfterViews
	void init() {
		initData();
	}
	public void initData() {
		mProductIntroduceString=getIntent().getStringExtra(Define.DATA);
		introduceEdit.setText(mProductIntroduceString);
	}
	
	
	/**
	 * 产品介绍 确认
	 */
	@Click(R.id.tjml)
	void onProductTypeClick()

	{
		mProductIntroduceString=introduceEdit.getText().toString();
		Intent intent = new Intent(ProductIntroduceActivity.this, ProductInfoActivity_.class);
		intent.putExtra(Define.DATA,mProductIntroduceString);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

}
