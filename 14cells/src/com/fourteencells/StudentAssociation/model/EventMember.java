package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

public class EventMember implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8576803402130451724L;

	private String id;
	private String event_id;
	private String user_id;
	private String confirmed;
	private String present;
	private String role;
	private String created_at;
	private String updated_at;

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

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(String confirmed) {
		this.confirmed = confirmed;
	}

	public String getPresent() {
		return present;
	}

	public void setPresent(String present) {
		this.present = present;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
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

	@Override
	public String toString() {
		return "EventMember [id=" + id + ", event_id=" + event_id
				+ ", user_id=" + user_id + ", confirmed=" + confirmed
				+ ", present=" + present + ", role=" + role + ", created_at="
				+ created_at + ", updated_at=" + updated_at + "]";
	}

}
