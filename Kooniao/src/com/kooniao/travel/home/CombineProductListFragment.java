package com.kooniao.travel.home;

import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseFragment;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.LinearListLayout;
import com.kooniao.travel.model.Product;
import com.kooniao.travel.model.ProductDetail;
import com.kooniao.travel.store.StoreActivity_;
import com.kooniao.travel.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 组合产品列表fragment
 * 
 * @author ke.wei.quan
 * 
 */
@EFragment(R.layout.fragment_combine_product_list)
public class CombineProductListFragment extends BaseFragment {

	@ViewById(R.id.ll_product_combine)
	LinearListLayout linearListLayout; // 组合产品列表布局
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
		iniData();
		initView();
	}

	private List<Product> combineProductList;

	/**
	 * 初始化界面数据
	 */
	private void iniData() {
		if (productDetail != null) {
			combineProductList = productDetail.getPartList();
		}
	}

	private SearchProductListAdapter adapter;

	/**
	 * 初始化界面
	 */
	private void initView() {
		noDataMarginTopView.setVisibility(View.VISIBLE); 
		if (combineProductList.isEmpty()) {
			noDataLayout.setVisibility(View.VISIBLE);
		} else {
			adapter = new SearchProductListAdapter(getActivity());
			adapter.setProductList(combineProductList);
			adapter.setOnListItemRequestListener(listItemRequestListener);
			linearListLayout.setBaseAdapter(adapter);
		}
	}

	SearchProductListAdapter.ListItemRequestListener listItemRequestListener = new SearchProductListAdapter.ListItemRequestListener() {

		@Override
		public void onStoreClick(int position) {
			Intent intent = new Intent(getActivity(), StoreActivity_.class);
			int sid = productDetail.getShopId();
			intent.putExtra(Define.SID, sid);
		}

		@Override
		public void onLoadCoverImgListener(String imgUrl, ImageView coverImageView) {
			ImageLoaderUtil.loadListCoverImg(ImageLoader.getInstance(), imgUrl, coverImageView);
		}

		@Override
		public void onItemClick(int position) {
			if (combineProductList != null) {
				Product product = combineProductList.get(position);
				int productType = product.getType();
				Intent productDetailIntent = null;
				if (productType == 2) {
					// 组合产品详情
					productDetailIntent = new Intent(getActivity(), CombineProductDetailActivity_.class);
				} else {
					if (productType == 4) {
						// 线路产品详情
						productDetailIntent = new Intent(getActivity(), LineProductDetailActivity_.class);
					} else {
						// 非线路产品详情
						productDetailIntent = new Intent(getActivity(), NonLineProductDetailActivity_.class);
					}
				}

				if (productDetailIntent != null) {
					productDetailIntent.putExtra(Define.PID, product.getProductId());
					productDetailIntent.putExtra(Define.TYPE, productType);
					startActivity(productDetailIntent);
				}
			}
		}
	};
}
