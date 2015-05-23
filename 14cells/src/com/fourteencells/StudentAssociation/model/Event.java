package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

/**
 * 活动实体类
 * 
 * @author shichunlei
 */
public class Event implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 活动ID */
	private String id;
	/** 活动所在社团ID */
	private String club_id;
	/** 创建活动者ID */
	private String publisher_id;
	/** 活动名称 */
	private String name;
	/** 结束时间 */
	private String start_time;
	/** 开始时间 */
	private String end_time;
	/** 活动地址 */
	private String address;
	/** 活动简介 */
	private String description;
	/** 限制最大参加人数 */
	private int max_number;
	/** 实际参加人数 */
	private int count;
	/** 活动图标 */
	private Cover cover;
	/** 活动成员数 */
	private String status;
	/** 活动类型（公开活动OR私密活动） */
	private String open_to;
	/** 创建时间 */
	private String created_at;
	/** 更新时间 */
	private String updated_at;
	/** 是否有人数限制 */
	private boolean limit;
	/** 是否收费 */
	private boolean charge;
	/** 收费金额 */
	private int fee;
	/** 发起者 */
	private String publisher_name;
	/** 活动所在社团名称 */
	private String club_name;
	/** 签到状态 */
	private String role;

	private String application_id;

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

	public String getPublisher_id() {
		return publisher_id;
	}

	public void setPublisher_id(String publisher_id) {
		this.publisher_id = publisher_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getMax_number() {
		return max_number;
	}

	public void setMax_number(int max_number) {
		this.max_number = max_number;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Cover getCover() {
		return cover;
	}

	public void setCover(Cover cover) {
		this.cover = cover;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOpen_to() {
		return open_to;
	}

	public void setOpen_to(String open_to) {
		this.open_to = open_to;
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

	public boolean isLimit() {
		return limit;
	}

	public void setLimit(boolean limit) {
		this.limit = limit;
	}

	public boolean isCharge() {
		return charge;
	}

	public void setCharge(boolean charge) {
		this.charge = charge;
	}

	public int getFee() {
		return fee;
	}

	public void setFee(int fee) {
		this.fee = fee;
	}

	public String getPublisher_name() {
		return publisher_name;
	}

	public void setPublisher_name(String publisher_name) {
		this.publisher_name = publisher_name;
	}

	public String getClub_name() {
		return club_name;
	}

	public void setClub_name(String club_name) {
		this.club_name = club_name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getApplication_id() {
		return application_id;
	}

	public void setApplication_id(String application_id) {
		this.application_id = application_id;
	}

	@Override
	public String toString() {
		return "Event [id=" + id + ", club_id=" + club_id + ", publisher_id="
				+ publisher_id + ", name=" + name + ", start_time="
				+ start_time + ", end_time=" + end_time + ", address="
				+ address + ", description=" + description + ", max_number="
				+ max_number + ", count=" + count + ", cover=" + cover
				+ ", status=" + status + ", open_to=" + open_to
				+ ", created_at=" + created_at + ", updated_at=" + updated_at
				+ ", limit=" + limit + ", charge=" + charge + ", fee=" + fee
				+ ", publisher_name=" + publisher_name + ", club_name="
				+ club_name + ", role=" + role + ", application_id="
				+ application_id + "]";
	}
}
