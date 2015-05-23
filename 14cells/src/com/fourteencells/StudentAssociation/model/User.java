package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8972964430254917780L;

	/** 用户头像 */
	private Headpic headpic;
	/** 用户Token */
	private String authentication_token;
	/** 登录ID */
	private String id;
	/** 邮箱 */
	private String email;
	/** 创建时间 */
	private String created_at;
	/** 更新时间 */
	private String updated_at;
	/** 姓名 */
	private String name;
	/** 学校 */
	private String school;
	/** 学院 */
	private String college;
	/** 学号 */
	private String school_no;
	/** 入学年份 */
	private String enrollment;
	/** 性别 */
	private String gender;

	private String present;

	public Headpic getHeadpic() {
		return headpic;
	}

	public void setHeadpic(Headpic headpic) {
		this.headpic = headpic;
	}

	public String getAuthentication_token() {
		return authentication_token;
	}

	public void setAuthentication_token(String authentication_token) {
		this.authentication_token = authentication_token;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getCollege() {
		return college;
	}

	public void setCollege(String college) {
		this.college = college;
	}

	public String getSchool_no() {
		return school_no;
	}

	public void setSchool_no(String school_no) {
		this.school_no = school_no;
	}

	public String getEnrollment() {
		return enrollment;
	}

	public void setEnrollment(String enrollment) {
		this.enrollment = enrollment;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPresent() {
		return present;
	}

	public void setPresent(String present) {
		this.present = present;
	}

	@Override
	public String toString() {
		return "User [headpic=" + headpic + ", authentication_token="
				+ authentication_token + ", id=" + id + ", email=" + email
				+ ", created_at=" + created_at + ", updated_at=" + updated_at
				+ ", name=" + name + ", school=" + school + ", college="
				+ college + ", school_no=" + school_no + ", enrollment="
				+ enrollment + ", gender=" + gender + ", present=" + present
				+ "]";
	}

}
