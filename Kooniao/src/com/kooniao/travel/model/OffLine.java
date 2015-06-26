package com.kooniao.travel.model;

import com.kooniao.travel.manager.DownloadManager.DownloadManagerCallback;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.PrimaryKey.AssignType;

/**
 * 离线model
 * 
 * @author ke.wei.quan
 * 
 */
@Table(value = "offLine")
public class OffLine {
	@PrimaryKey(value = AssignType.BY_MYSELF)
	@Column(value = "_id")
	private int travelId;
	private String travelName; // 行程名称
	private String startPlace; // 出发地
	private float price; // 参考价格
	private String coverImg; // 封面图
	private long mTime; // 离线时间

	private int downloadStatus = -1; // 下载状态(-1：下载失败，未下载、0：下载中、1：下载成功、2：需要更新、3：请稍后)
	private String progress; // 下载百分比
	private String downloadPath; // 下载路径
	private String fileName; // 文件名
	private long downloadedTimeStamp = System.currentTimeMillis(); // 下载时间
	@Ignore
	private DownloadManagerCallback callback;

	public int getTravelId() {
		return travelId;
	}

	public void setTravelId(int travelId) {
		this.travelId = travelId;
	}

	public String getTravelName() {
		return travelName;
	}

	public void setTravelName(String travelName) {
		this.travelName = travelName;
	}

	public String getStartPlace() {
		return startPlace;
	}

	public void setStartPlace(String startPlace) {
		this.startPlace = startPlace;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getCoverImg() {
		return coverImg;
	}

	public void setCoverImg(String coverImg) {
		this.coverImg = coverImg;
	}

	public long getmTime() {
		return mTime;
	}

	public void setmTime(long mTime) {
		this.mTime = mTime;
	}

	public int getDownloadStatus() {
		return downloadStatus;
	}

	public void setDownloadStatus(int downloadStatus) {
		this.downloadStatus = downloadStatus;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getDownloadedTimeStamp() {
		return downloadedTimeStamp;
	}

	public void setDownloadedTimeStamp(long downloadedTimeStamp) {
		this.downloadedTimeStamp = downloadedTimeStamp;
	}

	public DownloadManagerCallback getCallback() {
		return callback;
	}

	public void setCallback(DownloadManagerCallback callback) {
		this.callback = callback;
	}

	@Override
	public String toString() {
		return "OffLine [travelId=" + travelId + ", travelName=" + travelName + ", startPlace=" + startPlace + ", price=" + price + ", coverImg=" + coverImg + ", mTime=" + mTime + ", downloadStatus=" + downloadStatus + ", progress=" + progress + ", downloadPath=" + downloadPath + ", fileName=" + fileName + ", downloadedTimeStamp=" + downloadedTimeStamp + ", callback=" + callback + "]";
	}

}
