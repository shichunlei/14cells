package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

/**
 * 返回结果实体类
 * 
 * @author Administrator
 * 
 */
public class Result implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5503267134486542864L;

	/** 返回码 */
	private int resultcode;
	/** 用户信息 */
	private User user;
	/** 错误提示 */
	private String error;

	public int getResultcode() {
		return resultcode;
	}

	public void setResultcode(int resultcode) {
		this.resultcode = resultcode;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "Result [resultcode=" + resultcode + ", user=" + user
				+ ", error=" + error + "]";
	}

}
