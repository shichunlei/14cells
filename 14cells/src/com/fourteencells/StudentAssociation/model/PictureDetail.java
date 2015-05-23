package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

/**
 * 图片（照片）详情实体类
 * 
 * @author 师春雷
 * 
 */
public class PictureDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1792509912869755866L;

	/** 照片ID */
	private String id;
	/** 照片所属活动ID */
	private String event_id;
	/** 照片描述 */
	private String description;
	/** 照片 */
	private Photo photo;
	/** 创建照片用户ID */
	private String user_id;
	/** 创建时间 */
	private String created_at;
	/** 更新时间 */
	private String updated_at;
	/** 照片创建地点 */
	private String position;
	/** 照片赞数 */
	private int like_count;
	/** 照片评论数 */
	private int comment_count;

	private int longitude;
	private int latitude;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEvent_id() {
		return event_id;
	}

	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
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

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public int getLike_count() {
		return like_count;
	}

	public void setLike_count(int like_count) {
		this.like_count = like_count;
	}

	public int getComment_count() {
		return comment_count;
	}

	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	@Override
	public String toString() {
		return "PictureDetail [id=" + id + ", event_id=" + event_id
				+ ", description=" + description + ", photo=" + photo
				+ ", user_id=" + user_id + ", created_at=" + created_at
				+ ", updated_at=" + updated_at + ", position=" + position
				+ ", like_count=" + like_count + ", comment_count="
				+ comment_count + ", longitude=" + longitude + ", latitude="
				+ latitude + "]";
	}

}
