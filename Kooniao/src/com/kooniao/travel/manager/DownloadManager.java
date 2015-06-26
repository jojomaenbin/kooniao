package com.kooniao.travel.manager;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.util.Log;

import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.model.OffLine;
import com.kooniao.travel.utils.FileUtil;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

/**
 * 下载管理
 * 
 * @author ke.wei.quan
 * 
 */
public class DownloadManager {

	DownloadManager() {
		if (isFirst) {
			isFirst = false;
			offLines = getAllLocalOffLines();
		}
	}

	private boolean isFirst = true;
	private List<OffLine> offLines = new ArrayList<OffLine>();

	public void backupOffline(OffLine offLine) {
		for (int i = 0; i < offLines.size(); i++) {
			if (offLines.get(i).getTravelId() == offLine.getTravelId()) {
				offLines.remove(i);
				break;
			}
		}
		offLines.add(offLine);
		saveOrUpdateOffline(offLine);
	}

	public List<OffLine> getAllOffLines() {
		return offLines;
	}

	/**
	 * 根据travelId获取离线model
	 * 
	 * @param travelId
	 * @return
	 */
	public OffLine getOffLineByTravelId(int travelId) {
		OffLine offLine = null;
		for (OffLine localOffLine : offLines) {
			int currentTravelId = localOffLine.getTravelId();
			if (travelId == currentTravelId) {
				offLine = localOffLine;
			}
		}
		return offLine;
	}

	/**
	 * 添加下载
	 * 
	 * @param offLine
	 * @param callback
	 */
	public void addNewDownload(final OffLine offLine, final DownloadCallback downloadCallback) {
		if (offLine == null) {
			throw new RuntimeException("model can not be null~~");
		}
		backupOffline(offLine);
		String savePath = KooniaoApplication.getInstance().getDownloadDir() + offLine.getFileName();
		File downloadFile = new File(savePath);
		DownloadManagerCallback downloadManagerCallback = new DownloadManagerCallback(downloadFile, offLine, downloadCallback);
		offLine.setCallback(downloadManagerCallback);
		downloadFile(offLine, downloadManagerCallback);
	}

	/**
	 * 重新下载
	 * 
	 * @param offLine
	 * @param downloadCallback
	 */
	public void resumeDownload(final OffLine offLine, final DownloadCallback downloadCallback) {
		if (offLine == null) {
			throw new RuntimeException("model can not be null~~");
		}
		String savePath = KooniaoApplication.getInstance().getDownloadDir() + offLine.getFileName();
		File downloadFile = new File(savePath);
		DownloadManagerCallback downloadManagerCallback = new DownloadManagerCallback(downloadFile, offLine, downloadCallback);
		offLine.setCallback(downloadManagerCallback);
		downloadFile(offLine, downloadManagerCallback);
	}

	/**
	 * 下载文件
	 * 
	 * @param downloadPath
	 * @param callback
	 */
	private void downloadFile(OffLine offLine, final DownloadManagerCallback downloadManagerCallback) {
		if (offLine.getDownloadPath() == null)
			return;
		if (!ProgressManager.mProgressMap.containsKey(offLine.getTravelId()+"")) {
			ProgressManager.putProgress(offLine.getTravelId()+"", "0%");
			AsyncHttpClient httpClient = new AsyncHttpClient();
			String downloadPath = offLine.getDownloadPath() + "?mTime=" + System.currentTimeMillis();
			httpClient.get(downloadPath, downloadManagerCallback);
		}
	}

	public class DownloadManagerCallback extends FileAsyncHttpResponseHandler {

		public DownloadManagerCallback(File file, OffLine offLine, DownloadCallback downloadCallback) {
			super(file);
			this.downloadCallback = downloadCallback;
			this.offLine = offLine;
		}

		private OffLine offLine;
		private DownloadCallback downloadCallback;

		public void setDownloadCallbackInfo(DownloadCallback downloadCallback, OffLine offLine) {
			this.downloadCallback = downloadCallback;
			this.offLine = offLine;
		}

		public DownloadCallback getDownloadCallback() {
			return downloadCallback;
		}

		@Override
		public void onProgress(int bytesWritten, int totalSize) {
			if (downloadCallback != null && offLine != null) {
				float progress = (float) bytesWritten / (float) totalSize;
				DecimalFormat decimalFormat = new DecimalFormat("##%");
				String progresString = decimalFormat.format(progress);
				offLine.setProgress(progresString);
				offLine.setDownloadStatus(0);
				downloadCallback.onLoading(progresString);
				ProgressManager.putProgress(offLine.getTravelId()+"", progresString);
			}
		}

		@Override
		public void onSuccess(int arg0, Header[] arg1, File arg2) {
			if (downloadCallback != null && offLine != null) {
				downloadCallback.onSuccess();
				offLine.setDownloadStatus(1);
				saveOrUpdateOffline(offLine);
				ProgressManager.endProgress(offLine);
				// 解压
				String savePath = KooniaoApplication.getInstance().getDownloadDir() + offLine.getFileName();
				File zipFile = new File(savePath);
				try {
					FileUtil.upZipFile(zipFile, KooniaoApplication.getInstance().getDownloadDir());
					FileUtil.delFile(savePath);
				} catch (Exception e) {
					Log.e(Define.LOG_TAG, e.toString());
				}
			}
		}

		@Override
		public void onFailure(int arg0, Header[] arg1, Throwable arg2, File arg3) {
			if (downloadCallback != null && offLine != null) {
				downloadCallback.onFailure();
				offLine.setDownloadStatus(-1);
				saveOrUpdateOffline(offLine);
				ProgressManager.endProgress(offLine);
			}
		}

	}

	public interface DownloadCallback {
		void onLoading(String progress);

		void onSuccess();

		void onFailure();
	}

	/**
	 * 保存或更新离线model
	 * 
	 * @param offLine
	 */
	public void saveOrUpdateOffline(OffLine offLine) {
		if (offLine == null) {
			throw new RuntimeException("model can not be null~~");
		}
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		OffLine localOffLine = db.queryById(offLine.getTravelId(), OffLine.class);
		if (localOffLine != null) {
			localOffLine = offLine;
			db.update(localOffLine);
		} else {
			db.save(offLine);
		}
	}

	/**
	 * 删除本地离线数据包
	 * 
	 * @param offLine
	 */
	public void deleteOffline(OffLine offLine) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		db.delete(offLine);
		String offlineFileName = offLine.getFileName();
		int end = offlineFileName.lastIndexOf(".zip");
		offlineFileName = offlineFileName.substring(0, end);
		String savePath = KooniaoApplication.getInstance().getDownloadDir() + offlineFileName;
		FileUtil.delFolder(savePath);
	}

	/**
	 * 获取数据库中所有的离线model
	 * 
	 * @return
	 */
	private List<OffLine> getAllLocalOffLines() {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		return db.queryAll(OffLine.class);
	}
}
