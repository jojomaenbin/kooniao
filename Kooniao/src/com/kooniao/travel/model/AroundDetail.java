package com.kooniao.travel.model;

import java.util.List;

/**
 * 附近详情基类
 * 
 * @author ke.wei.quan
 * 
 */
public abstract class AroundDetail {
	private String name;// 名称（如景点名称、住宿名称...）
	private float rank;// 评分
	private int likeCount;// 想去
	private String description;// 简介
	private String img;// 图片url（大图1张）
	private String labelList; // 标签
	private int imgCount;// 图片个数
	private float lon;// 经度
	private float lat;// 纬度
	private String contactways;// 联系方式
	private String addr;// 地址
	private int reviewCount;// 点评条数
	private int collect;// 是否收藏 (1:收藏，0：没有)
	private String shareUrl;// 分享链接
	private List<Review> reviewList;
	private List<Around> recommendList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getRank() {
		return rank;
	}

	public void setRank(float rank) {
		this.rank = rank;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getLabelList() {
		return labelList;
	}

	public void setLabelList(String labelList) {
		this.labelList = labelList;
	}

	public int getImgCount() {
		return imgCount;
	}

	public void setImgCount(int imgCount) {
		this.imgCount = imgCount;
	}

	public float getLon() {
		return lon;
	}

	public void setLon(float lon) {
		this.lon = lon;
	}

	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}

	public String getContactways() {
		return contactways;
	}

	public void setContactways(String contactways) {
		this.contactways = contactways;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public int getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
	}

	public int getCollect() {
		return collect;
	}

	public void setCollect(int collect) {
		this.collect = collect;
	}

	public String getShareUrl() {
		return shareUrl;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}

	public List<Review> getReviewList() {
		return reviewList;
	}

	public void setReviewList(List<Review> reviewList) {
		this.reviewList = reviewList;
	}

	public List<Around> getRecommendList() {
		return recommendList;
	}

	public void setRecommendList(List<Around> recommendList) {
		this.recommendList = recommendList;
	}

	@Override
	public String toString() {
		return "AroundDetail [name=" + name + ", rank=" + rank + ", likeCount=" + likeCount + ", description=" + description + ", img=" + img + ", labelList=" + labelList + ", imgCount=" + imgCount + ", lon=" + lon + ", lat=" + lat + ", contactways=" + contactways + ", addr=" + addr + ", reviewCount=" + reviewCount + ", collect=" + collect + ", shareUrl=" + shareUrl + ", reviewList=" + reviewList + ", recommendList=" + recommendList + "]";
	}

}
