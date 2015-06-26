package com.kooniao.travel.model;

/**
 * 消息
 * 
 * @author ke.wei.quan
 * 
 */
public class Message {
	private String content;
	private String type;
	private long ctime;
	private String title;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getCtime() {
		return ctime;
	}

	public void setCtime(long ctime) {
		this.ctime = ctime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "Message [content=" + content + ", type=" + type + ", ctime=" + ctime + ", title=" + title + "]";
	}

}
