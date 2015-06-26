package com.kooniao.travel.model;

/**
 * 点评
 * 
 * @author ke.wei.quan
 * 
 */
public class Review {
	private int reviewID;
	private int reviewUID;
	private String reviewUName;
	private float reviewRank;
	private String reviewCont;
	private long reviewTime;
	private String reviewUserImg;

	public int getReviewID() {
		return reviewID;
	}

	public void setReviewID(int reviewID) {
		this.reviewID = reviewID;
	}

	public int getReviewUID() {
		return reviewUID;
	}

	public void setReviewUID(int reviewUID) {
		this.reviewUID = reviewUID;
	}

	public String getReviewUName() {
		return reviewUName;
	}

	public void setReviewUName(String reviewUName) {
		this.reviewUName = reviewUName;
	}

	public float getReviewRank() {
		return reviewRank;
	}

	public void setReviewRank(float reviewRank) {
		this.reviewRank = reviewRank;
	}

	public String getReviewCont() {
		return reviewCont;
	}

	public void setReviewCont(String reviewCont) {
		this.reviewCont = reviewCont;
	}

	public long getReviewTime() {
		return reviewTime;
	}

	public void setReviewTime(long reviewTime) {
		this.reviewTime = reviewTime;
	}

	public String getReviewUserImg() {
		return reviewUserImg;
	}

	public void setReviewUserImg(String reviewUserImg) {
		this.reviewUserImg = reviewUserImg;
	}

	@Override
	public String toString() {
		return "Review [reviewID=" + reviewID + ", reviewUID=" + reviewUID + ", reviewUName=" + reviewUName + ", reviewRank=" + reviewRank + ", reviewCont=" + reviewCont + ", reviewTime=" + reviewTime + ", reviewUserImg=" + reviewUserImg + "]";
	}

}
