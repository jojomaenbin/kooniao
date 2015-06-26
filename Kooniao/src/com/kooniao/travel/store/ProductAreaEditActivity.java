package com.kooniao.travel.store;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.citylist.AreaListActivity_;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.model.Area;

/**
 * 地址编辑
 * 
 * @author zheng.zong.di
 * @date 2015年6月10日
 * @version 1.0
 *
 */
@SuppressLint("InflateParams")
@EActivity(R.layout.activity_product_area_edit)
public class ProductAreaEditActivity extends BaseActivity {
	@ViewById(R.id.address_layout)
	LinearLayout address_layout;

	private List<Area> areaList;

	@SuppressWarnings("unchecked")
	@AfterViews
	public void init() {
		areaList = (List<Area>) getIntent().getSerializableExtra(Define.DATA);
		for (Area area : areaList) {
			final LinearLayout newaddress = (LinearLayout) getLayoutInflater().inflate(R.layout.item_address_layout, null);
			newaddress.setTag(R.id.lv_city, area.city_id.id);
			newaddress.setTag(R.id.lv_country, area.country_id.id);
			newaddress.findViewById(R.id.delete_address).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((LinearLayout) newaddress.getParent()).getChildCount() > 1) {
						((LinearLayout) newaddress.getParent()).removeView(newaddress);
					}
				}
			});
			if (area.area_name != null) {
				((TextView) newaddress.findViewById(R.id.new_address)).setText(area.area_name);
			} else {
				((TextView) newaddress.findViewById(R.id.new_address)).setText(area.country_id.bname + " " + area.city_id.bname);
				;

			}
			((TextView) newaddress.findViewById(R.id.new_address)).setTextColor(Color.parseColor("#323232"));
			address_layout.addView(newaddress);
			newaddress.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					clickAddText = (TextView) newaddress.findViewById(R.id.new_address);
					Intent intent = new Intent(ProductAreaEditActivity.this, AreaListActivity_.class);
					startActivityForResult(intent, REQUEST_CODE_SELECT_CITY);
				}
			});

		}
	}

	private TextView clickAddText;
	final int REQUEST_CODE_SELECT_CITY = 18;// 地址修改

	/**
	 * 添加产品地址点击
	 */
	@Click(R.id.ll_addaddress)
	void onAddAddressClick() {
		boolean add = true;
		for (int i = 0; i < address_layout.getChildCount(); i++) {
			if (address_layout.getChildAt(i).getTag(R.id.lv_city) == null) {
				add = false;
			}
		}
		if (add) {

			final LinearLayout newaddress = (LinearLayout) getLayoutInflater().inflate(R.layout.item_address_layout, null);
			newaddress.findViewById(R.id.delete_address).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((LinearLayout) newaddress.getParent()).getChildCount() > 1) {
						((LinearLayout) newaddress.getParent()).removeView(newaddress);
					}
				}
			});
			address_layout.addView(newaddress);
			newaddress.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					clickAddText = (TextView) newaddress.findViewById(R.id.new_address);
					Intent intent = new Intent(ProductAreaEditActivity.this, AreaListActivity_.class);
					startActivityForResult(intent, REQUEST_CODE_SELECT_CITY);
				}
			});
		} else {
			showToast("请选择一个目的地");
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SELECT_CITY) {
			String cityName = data.getStringExtra(Define.SELECTED_AREA_STRING);
			((LinearLayout) ((LinearLayout) clickAddText.getParent()).getParent()).setTag(R.id.lv_city, data.getIntExtra(Define.SELECTED_SUB_AREA_ID, 0));
			((LinearLayout) ((LinearLayout) clickAddText.getParent()).getParent()).setTag(R.id.lv_country, data.getIntExtra(Define.SELECTED_AREA_ID, 0));
			clickAddText.setText(cityName);
			Area areasuba = new Area();
			areasuba.city_id.id =data.getIntExtra(Define.SELECTED_SUB_AREA_ID, 0);
			areasuba.country_id.id =  data.getIntExtra(Define.SELECTED_AREA_ID, 0);
			areasuba.area_name = cityName;
			areaList.add(areasuba);
			clickAddText.setTextColor(getResources().getColor(R.color.v323232));
		}
	}

	/**
	 * 后退按钮
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		List<Area> areaListsumb = new ArrayList<>();
		for (int i = 0; i < address_layout.getChildCount(); i++) {
			if (address_layout.getChildAt(i).getTag(R.id.lv_city) != null) {
				int cityid = (int) address_layout.getChildAt(i).getTag(R.id.lv_city);
				int countryid = (int) address_layout.getChildAt(i).getTag(R.id.lv_country);
				for (Area addArea:areaList) {
					if (addArea.city_id.id==cityid&&countryid==addArea.country_id.id) {
						areaListsumb.add(addArea);
						break;
					}
				}
			}
		}
		Intent intent = new Intent(ProductAreaEditActivity.this, ProductEditActivity_.class);
		intent.putExtra(Define.DATA, (Serializable) areaListsumb);
		setResult(RESULT_OK, intent);
		;
		finish();
	}
}
