package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

/**
 * 新增实体类
 * 
 * @author LiuZhenYu
 * 
 */

public class Photo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7501326841363577900L;

	private String url;

	@Override
	public String toString() {
		return "Photo [url=" + url + "]";
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
