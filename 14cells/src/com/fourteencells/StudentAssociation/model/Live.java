package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

/** 直播实体类 */
public class Live implements Serializable {

	private static final long serialVersionUID = 8563855421739604072L;

	private String like_count;
	private String comment_count;
	private String id;
	private String description;
	private Photo photo;

	public String getLike_count() {
		return like_count;
	}

	public void setLike_count(String like_count) {
		this.like_count = like_count;
	}

	public String getComment_count() {
		return comment_count;
	}

	public void setComment_count(String comment_count) {
		this.comment_count = comment_count;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	@Override
	public String toString() {
		return "Invention [like_count=" + like_count + ", comment_count="
				+ comment_count + ", id=" + id + ", description=" + description
				+ ", photo=" + photo + "]";
	}
}
