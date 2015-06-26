package com.kooniao.travel.model;

/**
 * 评论
 * 
 * @author ke.wei.quan
 * 
 */
public class Comment {

	private String commentUname; // 评论用户名
	private int commentUid; // 评论用户id
	private String commentUserFace; // 评论人头像
	private float commentRank; // 评论评分
	private String commentContent; // 评论内容
	private long commentTime; // 评论时间
	private int commentId; // 评论id
	private long serviceTime; // 服务器时间

	public String getCommentUname() {
		return commentUname;
	}

	public void setCommentUname(String commentUname) {
		this.commentUname = commentUname;
	}

	public int getCommentUid() {
		return commentUid;
	}

	public void setCommentUid(int commentUid) {
		this.commentUid = commentUid;
	}

	public String getCommentUserFace() {
		return commentUserFace;
	}

	public void setCommentUserFace(String commentUserFace) {
		this.commentUserFace = commentUserFace;
	}

	public float getCommentRank() {
		return commentRank;
	}

	public void setCommentRank(float commentRank) {
		this.commentRank = commentRank;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	public long getCommentTime() {
		return commentTime;
	}

	public void setCommentTime(long commentTime) {
		this.commentTime = commentTime;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public long getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(long serviceTime) {
		this.serviceTime = serviceTime;
	}

}
