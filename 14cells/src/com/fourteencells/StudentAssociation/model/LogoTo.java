package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

public class LogoTo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8433896115685873591L;

	private String url;

	public String getUrl() {
		return url + "!160";
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "LogoTo [url=" + url + "]";
	}

}
