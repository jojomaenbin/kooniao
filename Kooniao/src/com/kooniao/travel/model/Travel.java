package com.kooniao.travel.model;

/**
 * 行程
 * 
 * @author ke.wei.quan
 * 
 */
public class Travel {

	private int likeCount; // 想去数目
	private String title;// 行程名称
	private String image;// 行程封面
	private int id;// 行程ID
	private String price;// 行程价格
	private int collect;// 是否收藏 1：收藏/ 0:未收藏
	private int cityId; // 所属城市id

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public int getCollect() {
		return collect;
	}

	public void setCollect(int collect) {
		this.collect = collect;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	@Override
	public String toString() {
		return "Travel [likeCount=" + likeCount + ", title=" + title + ", image=" + image + ", id=" + id + ", price=" + price + ", collect=" + collect + ", cityId=" + cityId + "]";
	}

}
