package com.kooniao.travel.home;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseFragment;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.model.ProductDetail;
import com.kooniao.travel.utils.KooniaoTypeUtil;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

/**
 * 产品详情fragment
 * 
 * @author ke.wei.quan
 *
 */
@EFragment(R.layout.fragment_product_detail)
public class ProductDetailFragment extends BaseFragment {

	@ViewById(R.id.ll_address)
	View addressLayout; // 地址布局
	@ViewById(R.id.tv_address)
	TextView addressTextView; // 地址
	@ViewById(R.id.ll_time)
	View timeLayout; // 时间布局
	@ViewById(R.id.tv_time)
	TextView timeTextView; // 时间
	@ViewById(R.id.ll_type)
	View typeLayout; // 类型布局
	@ViewById(R.id.tv_type)
	TextView typeTextView; // 类型
	@ViewById(R.id.webview)
	WebView webView;
	@ViewById(R.id.layout_no_data)
	LinearLayout noDataLayout; // 无数据布局

	@AfterViews
	void init() {
		initView();
	}

	private ProductDetail productDetail;

	@Override
	public void onAttach(Activity activity) {
		productDetail = (ProductDetail) getArguments().getSerializable(Define.DATA);
		super.onAttach(activity);
	}

	/**
	 * 初始化界面
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		// 设置条目信息
		// 地址
		String address = productDetail.getAddress();
		if ("".equals(address.trim())) {
			addressLayout.setVisibility(View.GONE);
		} else {
			addressTextView.setText(address);
		}
		// 时间
		String time = productDetail.getOpenTime();
		if ("".equals(time.trim())) {
			timeLayout.setVisibility(View.GONE);
		} else {
			timeTextView.setText(time);
		}
		// 类型
		String type = productDetail.getProductType();// location_ticket_type
		String typeText = KooniaoTypeUtil.getStringByType(type);
		if ("".equals(typeText)) {
			typeLayout.setVisibility(View.GONE);
		} else {
			typeTextView.setText(typeText);
		}

		String introduction = productDetail.getIntroduction();
		if ("".equals(introduction.trim())) {
			noDataLayout.setVisibility(View.VISIBLE);
		} else {
			/**
			 * webview设置
			 */
			webView.getView().setFocusable(false);
			webView.getView().setFocusableInTouchMode(false);
			WebSettings webSettings = webView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webSettings.setSupportZoom(false);
			webSettings.setBuiltInZoomControls(true);
			webSettings.setDisplayZoomControls(false);
			webSettings.setDatabaseEnabled(true);
			webSettings.setDomStorageEnabled(true);
			webSettings.setAllowFileAccess(true);
			webSettings.setAppCacheEnabled(true);
			webSettings.setDefaultTextEncodingName("UTF-8");
			// 加载数据
			webView.loadDataWithBaseURL(null, productDetail.getIntroduction(), "text/html", "UTF-8", null);
		}
	}

}
