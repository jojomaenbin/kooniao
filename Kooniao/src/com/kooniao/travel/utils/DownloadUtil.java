package com.kooniao.travel.utils;

import java.io.File;
import java.text.DecimalFormat;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

public class DownloadUtil {
	
	/**
	 * 下载文件
	 * @param savePath
	 * @param downloadPath
	 * @param callback
	 */
	public static void downloadFile(String savePath, String downloadPath, final DownloadCallback callback) {
		if (savePath == null) 
			return;
		File downloadFile = new File(savePath);
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.get(downloadPath, new FileAsyncHttpResponseHandler(downloadFile) {
			
			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				float progress = (float)bytesWritten / (float)totalSize;
				DecimalFormat decimalFormat = new DecimalFormat("##%");
				String progresString = decimalFormat.format(progress);
				callback.onLoading(progresString); 
				super.onProgress(bytesWritten, totalSize);
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, File arg2) {
				callback.onSuccess();
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, Throwable arg2, File arg3) {
				callback.onFailure();
			}
		});
	}
	
	public interface DownloadCallback {
		void onLoading(String progress);
		
		void onSuccess();
		
		void onFailure();
	}
	
}
