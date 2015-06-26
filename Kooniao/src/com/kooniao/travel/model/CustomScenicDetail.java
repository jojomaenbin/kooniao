package com.kooniao.travel.model;

/**
 * 自定义景点详情
 * 
 * @author ke.wei.quan
 * 
 */
public class CustomScenicDetail extends Custom {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8721559212346148489L;
	private String ticket_price; // 景点门票
	private String tc_ticket; // 景点门票说明
	private String open_time; // 开放时间
	private String playtime; // 建议时长
	private String location_type; // 景点类型
	private String best_time; // 最佳游玩时间

	public String getTicket_price() {
		return ticket_price;
	}

	public void setTicket_price(String ticket_price) {
		this.ticket_price = ticket_price;
	}

	public String getTc_ticket() {
		return tc_ticket;
	}

	public void setTc_ticket(String tc_ticket) {
		this.tc_ticket = tc_ticket;
	}

	public String getOpen_time() {
		return open_time;
	}

	public void setOpen_time(String open_time) {
		this.open_time = open_time;
	}

	public String getPlaytime() {
		return playtime;
	}

	public void setPlaytime(String playtime) {
		this.playtime = playtime;
	}

	public String getLocation_type() {
		return location_type;
	}

	public void setLocation_type(String location_type) {
		this.location_type = location_type;
	}

	public String getBest_time() {
		return best_time;
	}

	public void setBest_time(String best_time) {
		this.best_time = best_time;
	}

	@Override
	public String toString() {
		return "CustomScenicDetail [ticket_price=" + ticket_price + ", tc_ticket=" + tc_ticket + ", open_time=" + open_time + ", playtime=" + playtime + ", location_type=" + location_type + ", best_time=" + best_time + "]";
	}

}
