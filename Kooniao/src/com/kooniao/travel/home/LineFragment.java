package com.kooniao.travel.home;

import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseFragment;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.LinearListLayout;
import com.kooniao.travel.model.DayList;
import com.kooniao.travel.model.ProductDetail;

/**
 * 线路fragment
 * 
 * @author ke.wei.quan
 * 
 */
@EFragment(R.layout.fragment_line)
public class LineFragment extends BaseFragment {

	@ViewById(R.id.view_daylist_start)
	View startDescriptionLayout;
	@ViewById(R.id.tv_travel_detail_start_place)
	TextView startDescriptionTextView; // 开始节点
	@ViewById(R.id.ll_product_line)
	LinearListLayout linearListLayout; // 产品线路
	@ViewById(R.id.view_daylist_end)
	View endDescriptionLayout;
	@ViewById(R.id.tv_travel_detail_end_place)
	TextView endDescriptionTextView; // 结束节点
	@ViewById(R.id.layout_no_data)
	LinearLayout noDataLayout; // 无数据布局
	@ViewById(R.id.no_data_margin_top)
	View noDataMarginTopView;

	private ProductDetail productDetail;

	@Override
	public void onAttach(Activity activity) {
		productDetail = (ProductDetail) getArguments().getSerializable(Define.DATA);
		super.onAttach(activity);
	}

	@AfterViews
	void init() {
		initView();
	}

	private LineAdapter adapter;

	/**
	 * 初始化界面
	 */
	private void initView() {
		List<DayList> dayLists = productDetail.getDayList();
		noDataMarginTopView.setVisibility(View.VISIBLE); 
		if (dayLists == null) {
			noDataLayout.setVisibility(View.VISIBLE);
		} else {
			if (dayLists.isEmpty()) {
				noDataLayout.setVisibility(View.VISIBLE);
			} else {
				// 开始节点
				String startDescription = productDetail.getStartDescription();
				startDescriptionTextView.setText(startDescription);
				startDescriptionLayout.setVisibility(View.VISIBLE);
				// 行程路线
				adapter = new LineAdapter(getActivity(), dayLists, false, true);
				linearListLayout.setExpandadleAdapter(adapter);
				// 结束节点
				String endDescription = productDetail.getEndDescription();
				endDescriptionTextView.setText(endDescription);
				endDescriptionLayout.setVisibility(View.VISIBLE);
			}
		}
	}
}
