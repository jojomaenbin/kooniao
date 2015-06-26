package com.kooniao.travel.model;

/**
 * 导游行程model
 * 
 * @author ke.wei.quan
 * 
 */
public class GuideTravel {
	private int planId; // 行程id
	private String planTitle; // 行程标题
	private float planRank; // 行程评分
	private String planImage; // 行程封面
	private float planPrice; // 行程价格

	public int getPlanId() {
		return planId;
	}

	public void setPlanId(int planId) {
		this.planId = planId;
	}

	public String getPlanTitle() {
		return planTitle;
	}

	public void setPlanTitle(String planTitle) {
		this.planTitle = planTitle;
	}

	public float getPlanRank() {
		return planRank;
	}

	public void setPlanRank(float planRank) {
		this.planRank = planRank;
	}

	public String getPlanImage() {
		return planImage;
	}

	public void setPlanImage(String planImage) {
		this.planImage = planImage;
	}

	public float getPlanPrice() {
		return planPrice;
	}

	public void setPlanPrice(float planPrice) {
		this.planPrice = planPrice;
	}

	@Override
	public String toString() {
		return "GuideTravel [planId=" + planId + ", planTitle=" + planTitle + ", planRank=" + planRank + ", planImage=" + planImage + ", planPrice=" + planPrice + "]";
	}

}
