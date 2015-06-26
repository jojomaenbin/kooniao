package com.kooniao.travel.citylist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.SideBar;
import com.kooniao.travel.customwidget.SideBar.OnTouchingLetterChangedListener;
import com.kooniao.travel.model.SubArea;
import com.kooniao.travel.utils.AreaPinyinComparator;

/**
 * 选择地区的子分类
 * 
 * @author ke.wei.quan
 * @date 2015年5月21日
 *
 */
@SuppressLint("InflateParams")
@EActivity(R.layout.activity_area_list)
public class SubAreaListActivity extends BaseActivity {
	@ViewById(R.id.lv_area)
	ListView listView;
	@ViewById(R.id.tv_area_selected_tips)
	TextView tipsTextView; // 选择城市提示
	@ViewById(R.id.sb_right)
	SideBar sideBar; // 侧边栏

	@AfterViews
	void init() {
		initData();
		initView();
		initCityList();
	}

	private SortAdapter adapter;

	/**
	 * 初始化数据
	 */
	@SuppressWarnings("unchecked")
	private void initData() {
		showProgressDialog();
		Intent intent = getIntent();
		if (intent != null) {
			areaDatas = (List<SubArea>) intent.getSerializableExtra(Define.DATA);
		}
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		sideBar.setTextView(tipsTextView);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					listView.setSelection(position);
				}

			}
		});
		adapter = new SortAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				SubArea area = ((SubArea) adapter.getItem(position));
				// 返回数据
				Intent data = new Intent();
				Bundle extras = new Bundle();
				extras.putSerializable(Define.SELECTED_SUB_AREA, area);
				data.putExtras(extras);
				setResult(Activity.RESULT_OK, data);
				finish();
			}
		});
	}

	private List<SubArea> areaDatas = new ArrayList<SubArea>(); // 地区列表数据

	/**
	 * 初始化城市列表
	 */
	private void initCityList() {
		setLetter(areaDatas);
		// 根据a-z进行排序源数据
		Collections.sort(areaDatas, new AreaPinyinComparator());
		adapter.notifyDataSetChanged();
		dissmissProgressDialog();
	}

	/**
	 * 设置侧边栏的分割字母
	 * 
	 * @param sortModelList
	 */
	private void setLetter(List<SubArea> sortModelList) {
		for (int i = 0; i < sortModelList.size(); i++) {
			SubArea sortModel = sortModelList.get(i);
			// 汉字转换成英文字母
			String pinyin = sortModel.getShort_spell_name();
			String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINA);
			sortModel.setSort_letter(sortString.toUpperCase(Locale.CHINA));
		}
	}

	/**
	 * 后退按钮的点击
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	class SortAdapter extends BaseAdapter implements SectionIndexer {

		public SortAdapter() {
		}

		public int getCount() {
			return areaDatas == null ? 0 : areaDatas.size();
		}

		public Object getItem(int position) {
			return areaDatas == null ? null : areaDatas.get(position);
		}

		public long getItemId(int position) {
			return areaDatas == null ? 0 : position;
		}

		public View getView(final int position, View view, ViewGroup arg2) {
			ViewHolder viewHolder = null;
			final SubArea area = areaDatas.get(position);
			if (view == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(SubAreaListActivity.this).inflate(R.layout.item_sub_area, null);
				viewHolder.titleTextView = (TextView) view.findViewById(R.id.tv_area_title);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			String title = area.getArea_name();
			viewHolder.titleTextView.setText(title);

			return view;
		}

		/**
		 * 根据ListView的当前位置获取分类的首字母的Char ascii值
		 */
		@Override
		public int getSectionForPosition(int position) {
			return areaDatas == null ? 0 : areaDatas.get(position).getSort_letter().charAt(0);
		}

		/**
		 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
		 */
		@Override
		public int getPositionForSection(int section) {
			for (int i = 0; i < getCount(); i++) {
				String sortStr = areaDatas.get(i).getSort_letter();
				char firstChar = sortStr.toUpperCase(Locale.CHINA).charAt(0);
				if (firstChar == section) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public Object[] getSections() {
			return null;
		}
	}

	final static class ViewHolder {
		TextView titleTextView;
	}

}
