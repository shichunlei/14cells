package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

public class ClubMember implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7864514941504738737L;

	private String id;
	private String club_id;
	private String user_id;
	private String confirmed;

	private String role;
	private String created_at;
	private String updated_at;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClub_id() {
		return club_id;
	}

	public void setClub_id(String club_id) {
		this.club_id = club_id;
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
		return "ClubMember [id=" + id + ", club_id=" + club_id + ", user_id="
				+ user_id + ", confirmed=" + confirmed + ", role=" + role
				+ ", created_at=" + created_at + ", updated_at=" + updated_at
				+ "]";
	}
}
