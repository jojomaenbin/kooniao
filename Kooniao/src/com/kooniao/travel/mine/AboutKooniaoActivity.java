package com.kooniao.travel.mine;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.kooniao.travel.R;
import com.kooniao.travel.WebBrowserActivity_;
import com.kooniao.travel.base.BaseActivity;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.customwidget.Dialog;
import com.kooniao.travel.customwidget.KooniaoProgressDialog;
import com.kooniao.travel.manager.AppManager;
import com.kooniao.travel.manager.AppManager.CheckVersionResultCallback;

/**
 * 关于酷鸟界面
 * 
 * @author ke.wei.quan
 * 
 */
@EActivity(R.layout.activity_about_kooniao)
public class AboutKooniaoActivity extends BaseActivity {

	@ViewById(R.id.tv_version)
	TextView versionTextView; // 版本号

	/**
	 * 初始化
	 */
	@AfterViews
	void init() {
		initData();
		initView();
	}

	String versionText; // 版本号

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 获取当前app版本号
		versionText = AppManager.getInstance().getCurrentAppVersion();
	}

	KooniaoProgressDialog progressDialog;
	
	/**
	 * 初始化界面
	 */
	private void initView() {
		// 设置版本号
		versionTextView.setText(versionText);
		if (progressDialog == null) {
			progressDialog = new KooniaoProgressDialog(AboutKooniaoActivity.this);
		}
	}

	/**
	 * 后退按钮点击
	 */
	@Click(R.id.iv_go_back)
	void onGoBackClick() {
		finish();
	}
	
	/**
	 * 版本更新条目点击
	 */
	@Click(R.id.lr_about_kooniao_version)
	void onVersionUpdateItemClick() {
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
		
		AppManager.getInstance().checkLastVersion(new CheckVersionResultCallback() {

			@Override
			public void result(String errMsg, boolean isNeedForceUpdate, String appDownloadUrl) {
				checkVersionFinish(errMsg, isNeedForceUpdate, appDownloadUrl);
			}

		});
	}

	@StringRes(R.string.version_update)
	String dialogTitle; // 对话框标题
	@StringRes(R.string.has_new_version)
	String dialogMessage; // 对话框内容
	Dialog dialog; // 对话框

	/**
	 * 检查版本完成
	 */
	@UiThread
	void checkVersionFinish(String errMsg, boolean isNeedForceUpdate, final String appDownloadUrl) {
		progressDialog.dismiss();
		
		if (errMsg == null) {
			if (appDownloadUrl != null) {
				dialog = new Dialog(AboutKooniaoActivity.this, dialogTitle, dialogMessage);
				dialog.setCancelable(false);
				if (isNeedForceUpdate) { // 强制更新
					dialog.setOnAcceptButtonClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							skipWebViewToUpdate(appDownloadUrl);
							finish();
						}
					});
					dialog.setOnCancelButtonClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							skipWebViewToUpdate(appDownloadUrl);
							finish();
						}
					});
				} else { // 普通更新
					dialog.setOnAcceptButtonClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							skipWebViewToUpdate(appDownloadUrl);
						}
					});
					dialog.setOnCancelButtonClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
				}
			}
			dialog.show();
		} else {
			Toast.makeText(getBaseContext(), errMsg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 跳转浏览器下载更新
	 */
	private void skipWebViewToUpdate(String url) {
		Uri webUri = Uri.parse(url);
		Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
		startActivity(webIntent);
	}

	/**
	 * 官网按钮点击
	 */
	@Click(R.id.tv_offical_websize)
	void onOfficalWebsizeClick() {
		Uri webUri = Uri.parse(Define.OFFICAL_WEBSIZE);
		Intent webSizeIntent = new Intent(Intent.ACTION_VIEW, webUri);
		startActivity(webSizeIntent);
	}

	/**
	 * 服务协议按钮点击
	 */
	@Click(R.id.tv_service_agreement)
	void onServiceAgreementClick() {
		Intent localWebIntent = new Intent(AboutKooniaoActivity.this, WebBrowserActivity_.class);
		localWebIntent.putExtra(Define.TITLE, "平台服务协议"); // 平台服务协议
		localWebIntent.putExtra(Define.TYPE, 3); // 平台服务协议
		startActivity(localWebIntent);
	}

}
