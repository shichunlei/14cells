package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

/**
 * 
 * 消息实体类
 * 
 * @author LiuZhenYu
 * 
 */
public class Messages implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2216472772642165388L;

	private String id;
	/** 需要查看该消息的用户ID */
	private String user_id;
	/** 内容 */
	private String content;
	/**  */
	private String created_at;
	/**  */
	private String updated_at;
	/** 1为通知，2为回应 */
	private String father_type;
	/***
	 * 回应：1为回复照片，2为回复评论
	 * 
	 * 通知：1为入社申请，2申请结果通知，3新社员广播，4活动通知
	 * 
	 */
	private String sub_type;
	/** 社团ID */
	private String club_id;
	/** 照片ID */
	private String photo_id;
	/** 活动ID */
	private String event_id;
	/** 评论ID */
	private String comment_id;
	/** 该消息是否已推送给用户 */
	private String push;
	/** 社团名称 */
	private String club_name;
	/**  */
	private String target;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getFather_type() {
		return father_type;
	}

	public void setFather_type(String father_type) {
		this.father_type = father_type;
	}

	public String getSub_type() {
		return sub_type;
	}

	public void setSub_type(String sub_type) {
		this.sub_type = sub_type;
	}

	public String getClub_id() {
		return club_id;
	}

	public void setClub_id(String club_id) {
		this.club_id = club_id;
	}

	public String getPhoto_id() {
		return photo_id;
	}

	public void setPhoto_id(String photo_id) {
		this.photo_id = photo_id;
	}

	public String getEvent_id() {
		return event_id;
	}

	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}

	public String getComment_id() {
		return comment_id;
	}

	public void setComment_id(String comment_id) {
		this.comment_id = comment_id;
	}

	public String getPush() {
		return push;
	}

	public void setPush(String push) {
		this.push = push;
	}

	public String getClub_name() {
		return club_name;
	}

	public void setClub_name(String club_name) {
		this.club_name = club_name;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	@Override
	public String toString() {
		return "Messages [id=" + id + ", user_id=" + user_id + ", content="
				+ content + ", created_at=" + created_at + ", updated_at="
				+ updated_at + ", father_type=" + father_type + ", sub_type="
				+ sub_type + ", club_id=" + club_id + ", photo_id=" + photo_id
				+ ", event_id=" + event_id + ", comment_id=" + comment_id
				+ ", push=" + push + ", club_name=" + club_name + ", target="
				+ target + "]";
	}

}
