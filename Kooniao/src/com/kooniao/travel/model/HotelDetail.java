package com.kooniao.travel.model;

/**
 * 酒店详情
 * @author ke.wei.quan
 *
 */
public class HotelDetail extends AroundDetail {

	private int stars;// 酒店星级
	private float price;// 价格
	private String service;// 酒店服务
	private String roomFacilities;// 房间设施
	private String meetingFacilities;// 会议设施
	private String payment;// 信用卡支付

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getRoomFacilities() {
		return roomFacilities;
	}

	public void setRoomFacilities(String roomFacilities) {
		this.roomFacilities = roomFacilities;
	}

	public String getMeetingFacilities() {
		return meetingFacilities;
	}

	public void setMeetingFacilities(String meetingFacilities) {
		this.meetingFacilities = meetingFacilities;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	@Override
	public String toString() {
		return "HotelDetail [stars=" + stars + ", price=" + price + ", service=" + service + ", roomFacilities=" + roomFacilities + ", meetingFacilities=" + meetingFacilities + ", payment=" + payment + "]";
	}

}
