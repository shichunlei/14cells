package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

/**
 * 社团实体类
 * 
 * @author shichunlei
 * 
 */
public class Club implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String club_id;
	/** 社团名称 */
	private String name;
	/** 社团简介 */
	private String description;
	/** 社团团长的user_id */
	private String chairman;
	/** 是否经过认证 */
	private String verified;
	/** 社团类型 */
	private String type;
	/** 社团图片 */
	private String url;
	/** 社团标签 */
	private String club_tag;
	/** 团长 */
	private String club_manager;
	/** 社团规模（最大人数） */
	private int max_number;
	/** 下次活动时间 */
	private String latest;

	private int member_count;

	private int role;

	private Logo logo;
	private String created_at;
	private String updated_at;
	private String id;
	private String club_type;

	private String school;

	public String getClub_id() {
		return club_id;
	}

	public void setClub_id(String club_id) {
		this.club_id = club_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getChairman() {
		return chairman;
	}

	public void setChairman(String chairman) {
		this.chairman = chairman;
	}

	public String getVerified() {
		return verified;
	}

	public void setVerified(String verified) {
		this.verified = verified;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getClub_tag() {
		return club_tag;
	}

	public void setClub_tag(String club_tag) {
		this.club_tag = club_tag;
	}

	public String getClub_manager() {
		return club_manager;
	}

	public void setClub_manager(String club_manager) {
		this.club_manager = club_manager;
	}

	public int getMax_number() {
		return max_number;
	}

	public void setMax_number(int max_number) {
		this.max_number = max_number;
	}

	public String getLatest() {
		return latest;
	}

	public void setLatest(String latest) {
		this.latest = latest;
	}

	public int getMember_count() {
		return member_count;
	}

	public void setMember_count(int member_count) {
		this.member_count = member_count;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public Logo getLogo() {
		return logo;
	}

	public void setLogo(Logo logo) {
		this.logo = logo;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClub_type() {
		return club_type;
	}

	public void setClub_type(String club_type) {
		this.club_type = club_type;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	@Override
	public String toString() {
		return "Club [club_id=" + club_id + ", name=" + name + ", description="
				+ description + ", chairman=" + chairman + ", verified="
				+ verified + ", type=" + type + ", url=" + url + ", club_tag="
				+ club_tag + ", club_manager=" + club_manager + ", max_number="
				+ max_number + ", latest=" + latest + ", member_count="
				+ member_count + ", role=" + role + ", logo=" + logo
				+ ", created_at=" + created_at + ", updated_at=" + updated_at
				+ ", id=" + id + ", club_type=" + club_type + ", school="
				+ school + "]";
	}

}
