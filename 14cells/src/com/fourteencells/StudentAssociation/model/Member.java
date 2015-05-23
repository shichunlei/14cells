package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

/**
 * 成员实体类
 * 
 * @author 师春雷
 * 
 */
public class Member implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7174706111259618474L;

	/** 用户个人信息 */
	private User user_info;
	/** 社团成员信息 */
	private ClubMember clubmember_info;
	/** 活动成员信息 */
	private EventMember eventmember_info;

	public User getUser_info() {
		return user_info;
	}

	public void setUser_info(User user_info) {
		this.user_info = user_info;
	}

	public ClubMember getClubmember_info() {
		return clubmember_info;
	}

	public void setClubmember_info(ClubMember clubmember_info) {
		this.clubmember_info = clubmember_info;
	}

	public EventMember getEventmember_info() {
		return eventmember_info;
	}

	public void setEventmember_info(EventMember eventmember_info) {
		this.eventmember_info = eventmember_info;
	}

	@Override
	public String toString() {
		return "Member [user_info=" + user_info + ", clubmember_info="
				+ clubmember_info + ", eventmember_info=" + eventmember_info
				+ "]";
	}

}
