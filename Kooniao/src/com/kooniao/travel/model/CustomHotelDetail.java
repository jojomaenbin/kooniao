package com.kooniao.travel.model;

import java.util.List;

/**
 * 自定义酒店
 * 
 * @author ke.wei.quan
 * 
 */
public class CustomHotelDetail extends Custom {

	/**
	 * 
	 */
	private static final long serialVersionUID = 985231813037742022L;
	private String ticket_price; // 酒店价格
	private float grade_id; // 酒店星级
	private List<String> special; // 特色服务
	private List<String> metting; // 会议设施
	private String room_type; // 房间类型

	public String getTicket_price() {
		return ticket_price;
	}

	public void setTicket_price(String ticket_price) {
		this.ticket_price = ticket_price;
	}

	public float getGrade_id() {
		return grade_id;
	}

	public void setGrade_id(float grade_id) {
		this.grade_id = grade_id;
	}

	public List<String> getSpecial() {
		return special;
	}

	public void setSpecial(List<String> special) {
		this.special = special;
	}

	public List<String> getMetting() {
		return metting;
	}

	public void setMetting(List<String> metting) {
		this.metting = metting;
	}

	public String getRoom_type() {
		return room_type;
	}

	public void setRoom_type(String room_type) {
		this.room_type = room_type;
	}

	@Override
	public String toString() {
		return "CustomHotelDetail [ticket_price=" + ticket_price + ", grade_id=" + grade_id + ", special=" + special + ", metting=" + metting + ", room_type=" + room_type + "]";
	}

}
