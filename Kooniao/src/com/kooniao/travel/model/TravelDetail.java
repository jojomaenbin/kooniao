package com.kooniao.travel.model;

import java.io.Serializable;
import java.util.List;

/**
 * 行程详情
 * 
 * @author ke.wei.quan
 * 
 */
public class TravelDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3725557438589460315L;
	private int uid;// 行程创建人
	private String title;// 行程名称
	private String image;// 行程封面
	private String traffic_departure;// 行程出发地
	private float refer_price;// 行程价格
	private String description;// 行程描述
	private String detail_cost;// 行程的费用说明：两大项（包含的费用，不包含的费用）列表
	private String QRCode;// 行程二维码
	private String shareUrl;// 行程的分享URL
	private int collect;// 是否收藏 1、收藏，0、没有
	private int staff;// 是否团员 1、是, 0、不是
	private int dragoman;// 是否导游 1、是, 0、不是
	private int teamId;// 团id
	private int commentCount;// 评论数目
	private String startDescription; // 出发
	private String endDescription; // 结束

	private List<Comment> commentList; // 评论列表
	private List<DayList> dayList; // 天节点
	private GuideList guideList; // 导游信息

	public class GuideList implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8005931752200856356L;
		private String guideName;
		private String guideMobile;
		private String guideUserFace;
		private String guideCity;
		private int guideId;
		private int guideTravelCount;

		public String getGuideName() {
			return guideName;
		}

		public void setGuideName(String guideName) {
			this.guideName = guideName;
		}

		public String getGuideMobile() {
			return guideMobile;
		}

		public void setGuideMobile(String guideMobile) {
			this.guideMobile = guideMobile;
		}

		public String getGuideUserFace() {
			return guideUserFace;
		}

		public void setGuideUserFace(String guideUserFace) {
			this.guideUserFace = guideUserFace;
		}

		public String getGuideCity() {
			return guideCity;
		}

		public void setGuideCity(String guideCity) {
			this.guideCity = guideCity;
		}

		public int getGuideId() {
			return guideId;
		}

		public void setGuideId(int guideId) {
			this.guideId = guideId;
		}

		public int getGuideTravelCount() {
			return guideTravelCount;
		}

		public void setGuideTravelCount(int guideTravelCount) {
			this.guideTravelCount = guideTravelCount;
		}

		@Override
		public String toString() {
			return "GuideList [guideName=" + guideName + ", guideMobile=" + guideMobile + ", guideUserFace=" + guideUserFace + ", guideCity=" + guideCity + ", guideId=" + guideId + ", guideTravelCount=" + guideTravelCount + "]";
		}

	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
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

	public String getTraffic_departure() {
		return traffic_departure;
	}

	public void setTraffic_departure(String traffic_departure) {
		this.traffic_departure = traffic_departure;
	}

	public float getRefer_price() {
		return refer_price;
	}

	public void setRefer_price(float refer_price) {
		this.refer_price = refer_price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDetail_cost() {
		return detail_cost;
	}

	public void setDetail_cost(String detail_cost) {
		this.detail_cost = detail_cost;
	}

	public String getQRCode() {
		return QRCode;
	}

	public void setQRCode(String qRCode) {
		QRCode = qRCode;
	}

	public String getShareUrl() {
		return shareUrl;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}

	public int getCollect() {
		return collect;
	}

	public void setCollect(int collect) {
		this.collect = collect;
	}

	public int getStaff() {
		return staff;
	}

	public void setStaff(int staff) {
		this.staff = staff;
	}

	public int getDragoman() {
		return dragoman;
	}

	public void setDragoman(int dragoman) {
		this.dragoman = dragoman;
	}

	public int getTeamId() {
		return teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

	public int getCommentwCount() {
		return commentCount;
	}

	public void setCommentwCount(int commentwCount) {
		this.commentCount = commentwCount;
	}

	public String getStartDescription() {
		return startDescription;
	}

	public void setStartDescription(String startDescription) {
		this.startDescription = startDescription;
	}

	public String getEndDescription() {
		return endDescription;
	}

	public void setEndDescription(String endDescription) {
		this.endDescription = endDescription;
	}

	public List<Comment> getCommentList() {
		return commentList;
	}

	public void setCommentList(List<Comment> commentList) {
		this.commentList = commentList;
	}

	public List<DayList> getDayList() {
		return dayList;
	}

	public void setDayList(List<DayList> dayList) {
		this.dayList = dayList;
	}

	public GuideList getGuideList() {
		return guideList;
	}

	public void setGuideList(GuideList guideList) {
		this.guideList = guideList;
	}

	@Override
	public String toString() {
		return "TravelDetail [uid=" + uid + ", title=" + title + ", image=" + image + ", traffic_departure=" + traffic_departure + ", refer_price=" + refer_price + ", description=" + description + ", detail_cost=" + detail_cost + ", QRCode=" + QRCode + ", shareUrl=" + shareUrl + ", collect=" + collect + ", staff=" + staff + ", dragoman=" + dragoman + ", teamId=" + teamId + ", commentwCount=" + commentCount + ", startDescription=" + startDescription + ", endDescription=" + endDescription + ", commentList=" + commentList + ", dayList=" + dayList + ", guideList=" + guideList + "]";
	}

}
