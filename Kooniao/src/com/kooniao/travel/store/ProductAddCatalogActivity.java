package com.kooniao.travel.store;

import android.content.Intent;
import android.widget.CompoundButton;
import android.widget.EditText;
import com.kooniao.travel.R;
import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.view.materialdesign.SwitchButton;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * 添加目录页 
 * Created by ZZD on 2015/5/28.
 */
@EActivity(R.layout.activity_product_addcatalog)
public class ProductAddCatalogActivity extends BaseActivity {

	@ViewById(R.id.et_catalog_name)
	EditText catalognameEidt; //目录名称
	@ViewById(R.id.catalog_tag)
	EditText catalogtagEdit; //目录注释
	@ViewById(R.id.catalog_showable)
	SwitchButton showableSwitch; //是否课件

	private int isShow = 1;
	private int maxsort;

	@AfterViews
	public void init() {
		maxsort = getIntent().getIntExtra(Define.TAB_SORT, 0);
		showableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					isShow = 1;
				else
					isShow = 0;
			}
		});

	}

	/**
	 * 添加标签
	 */
	@Click(R.id.tjbq)
	void onTjbqClick() {
		String tab = catalogtagEdit.getText().toString();
		String check = tab.replace(" ", "");
		if (tab == null || check.equals(""))
			tab = null;
		final String titlename = catalognameEidt.getText().toString();
		check = titlename.replace(" ", "");
		if (titlename == null || check.equals(""))
			return;
		showProgressDialog();
		ApiCaller.getInstance().addProductCatalog(titlename, isShow, tab, maxsort, new ApiCaller.APIAddProductCatalogResultCallback() {
			@Override
			public void result(String errMsg, int flag, int cid) {
				dissmissProgressDialog();
				if (errMsg == null) {
					if (flag == 0)
						showToast("添加产品失败");
					else {
						Intent intent = new Intent();
						intent.putExtra(Define.CID, cid);
						intent.putExtra(Define.TITLE, titlename);
						setResult(RESULT_OK, intent);
						finish();
					}
				} else {
					showToast(errMsg);
				}
			}
		});

	}

	/**
	 * 取消按钮
	 */
	@Click(R.id.qxtj)
	void onQxtjClick() {
		finish();
	}
}
