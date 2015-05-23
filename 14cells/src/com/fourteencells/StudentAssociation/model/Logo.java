package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

public class Logo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8433896115685873591L;

	private LogoTo logo;

	private String url;

	public LogoTo getLogo() {
		return logo;
	}

	public void setLogo(LogoTo logo) {
		this.logo = logo;
	}

	public String getUrl() {
		return url + "!160";
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Logo [logo=" + logo + ", url=" + url + "]";
	}

}
