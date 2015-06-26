package com.kooniao.travel.model;

/**
 * 广告
 * 
 * @author ke.wei.quan
 * 
 */
public class Ad {
	private int id;// 图片
	/**
	 * 类型： 店铺：0 线路：4 组合：2 酒店：5 美食：8 娱乐：7
	 */
	private String img;// 图片
	private int type;// 类型
	private String url;// 链接
	private String title;// 标题

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "Ad [id=" + id + ", img=" + img + ", type=" + type + ", url=" + url + ", title=" + title + "]";
	}

}
