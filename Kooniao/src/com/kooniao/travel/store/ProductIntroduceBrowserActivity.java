package com.kooniao.travel.store;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import com.kooniao.travel.R;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 产品介绍浏览
 * 
 * @author zheng.zong.di
 * 
 */
@EActivity(R.layout.activity_webview)
public class ProductIntroduceBrowserActivity extends BaseActivity {

	@ViewById(R.id.pb_webview)
	ProgressBar progressBar; // 进度条
	@ViewById(R.id.webview)
	WebView webView;
	@ViewById(R.id.tv_webview_title)
	TextView titlebarTextView; // 标题
	@ViewById(R.id.iv_webview_refresh)
	ImageView refreshImageView; // 刷新按钮

	/**
	 * 初始化
	 */
	@AfterViews
	void initialize() {
		initData();
		initView();
	}

	private String title = "产品介绍"; // 标题 
	private String introduce; // 产品介绍

	/**
	 * 初始化数据
	 */
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			if (intent.hasExtra(Define.DATA)) {
				introduce = intent.getStringExtra(Define.DATA);
			}
		}
	}

	@StringRes(R.string.loading)
	String webViewTitle; // title初始值
	KooniaoProgressDialog progressDialog;

	/**
	 * 初始化界面
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		refreshImageView.setVisibility(View.GONE);
		// 设置标题
		titlebarTextView.setText(title);
		if (progressDialog == null) {
			progressDialog = new KooniaoProgressDialog(ProductIntroduceBrowserActivity.this);
		}
		/**
		 * webview设置
		 */
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setDisplayZoomControls(false);
		webSettings.setDatabaseEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setAllowFileAccess(true);
		webSettings.setAppCacheEnabled(true);

		if (introduce != null) {
			webView.setWebViewClient(new WebViewClient());
			webView.setWebChromeClient(new WebChromeClient());

			if (introduce != null) {
				webView.loadDataWithBaseURL(null, introduce, "text/html", "UTF-8", null);
				}
		} else {
			refreshImageView.setVisibility(View.INVISIBLE); 
			if (!progressDialog.isShowing()) {
				progressDialog.show();
			}
		}
	}
	

	class WebChromeClient extends android.webkit.WebChromeClient {

		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				progressBar.setVisibility(View.GONE);
			} else {
				progressBar.setVisibility(View.VISIBLE);
				progressBar.setProgress(newProgress);
			}
			super.onProgressChanged(view, newProgress);
		}

	}

	class WebViewClient extends android.webkit.WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView webView, final String url) {
			webView.loadUrl(url);
			return true;
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			progressBar.setVisibility(View.GONE);
		};
	}

	/**
	 * 后退按钮点击
	 * 
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}

	/**
	 * 刷新按钮点击
	 */
	@Click(R.id.iv_webview_refresh)
	void onRefreshClick() {
		webView.reload();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && event.getAction() == KeyEvent.ACTION_DOWN) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
