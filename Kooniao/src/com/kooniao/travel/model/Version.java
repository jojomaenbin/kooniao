package com.kooniao.travel.model;

/**
 * 版本
 * 
 * @author ke.wei.quan
 * 
 */
public class Version {
	private String version; // 版本号
	private int updates; // 是否需要强制更新
	private String appUrl; // app下载地址

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getUpdates() {
		return updates;
	}

	public void setUpdates(int updates) {
		this.updates = updates;
	}

	public String getAppUrl() {
		return appUrl;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}

	@Override
	public String toString() {
		return "Version [version=" + version + ", updates=" + updates + ", appUrl=" + appUrl + "]";
	}

}
