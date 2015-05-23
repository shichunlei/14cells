package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

/**
 * 照片实体类
 * 
 * @author 师春雷
 */
public class Picture implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6127863505165339455L;

	/** 照片所属活动名称 */
	public String event_name;
	/** 照片上传者信息 */
	public String publisher_info;
	/** 照片评论 */
	public String comments;
	public String likes;
	/** 照片信息 */
	public String photo_info;
	public String islike;
	/** 照片所属社团名称 */
	public String club_name;

	public String getEvent_name() {
		return event_name;
	}

	public void setEvent_name(String event_name) {
		this.event_name = event_name;
	}

	public String getPublisher_info() {
		return publisher_info;
	}

	public void setPublisher_info(String publisher_info) {
		this.publisher_info = publisher_info;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getLikes() {
		return likes;
	}

	public void setLikes(String likes) {
		this.likes = likes;
	}

	public String getPhoto_info() {
		return photo_info;
	}

	public void setPhoto_info(String photo_info) {
		this.photo_info = photo_info;
	}

	public String getIslike() {
		return islike;
	}

	public void setIslike(String islike) {
		this.islike = islike;
	}

	public String getClub_name() {
		return club_name;
	}

	public void setClub_name(String club_name) {
		this.club_name = club_name;
	}

	@Override
	public String toString() {
		return "Picture [event_name=" + event_name + ", publisher_info="
				+ publisher_info + ", comments=" + comments + ", likes="
				+ likes + ", photo_info=" + photo_info + ", islike=" + islike
				+ ", club_name=" + club_name + "]";
	}

}
