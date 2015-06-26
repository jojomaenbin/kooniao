package com.kooniao.travel.model;

import com.kooniao.travel.constant.Define;
import com.kooniao.travel.utils.AppSetting;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.PrimaryKey.AssignType;
import com.litesuits.orm.db.annotation.Table;

/**
 * 短信模板
 * 
 * @author ke.wei.quan
 * 
 */
@Table("messageTemplate")
public class MessageTemplate {

	@PrimaryKey(AssignType.AUTO_INCREMENT)
	@Column("_id")
	private int id;
	private String message;
	private int uid = AppSetting.getInstance().getIntPreferencesByKey(Define.UID); 

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	@Override
	public String toString() {
		return "MessageTemplate [id=" + id + ", message=" + message + ", uid=" + uid + "]";
	}

}
