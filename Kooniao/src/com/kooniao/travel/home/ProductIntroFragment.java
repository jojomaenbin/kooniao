package com.kooniao.travel.home;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseFragment;
import com.kooniao.travel.constant.Define;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * 产品详细介绍内容页
 * 
 * @author ke.wei.quan
 * 
 */
@EFragment(R.layout.fragment_booking_infomation)
public class ProductIntroFragment extends BaseFragment {

	@ViewById(R.id.webview)
	WebView webView; // 加载数据webview
	@ViewById(R.id.layout_no_data)
	LinearLayout noDataLayout; // 无数据布局

	private String data; // 需要加载的数据源

	@Override
	public void onAttach(Activity activity) {
		data = getArguments().getString(Define.DATA, "");
		super.onAttach(activity);
	}

	/**
	 * 初始化界面
	 */
	@SuppressLint("SetJavaScriptEnabled")
	@AfterViews
	void initView() {
		if (data.equals("")) {
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
			webView.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					webView.setVisibility(View.VISIBLE);
				}

				@Override
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					super.onReceivedError(view, errorCode, description, failingUrl);
					webView.setVisibility(View.VISIBLE);
				}
			});

			// 加载数据
			webView.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);
		}
	}

}
