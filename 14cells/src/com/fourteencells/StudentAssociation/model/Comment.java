package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

/**
 * 图片评论实体类
 * 
 * @author 师春雷
 * 
 */
public class Comment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1352023975295461564L;

	private String id;
	private String parent_name;
	private String body;

	private User user;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParent_name() {
		return parent_name;
	}

	public void setParent_name(String parent_name) {
		this.parent_name = parent_name;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", parent_name=" + parent_name + ", body="
				+ body + ", user=" + user + "]";
	}
}
