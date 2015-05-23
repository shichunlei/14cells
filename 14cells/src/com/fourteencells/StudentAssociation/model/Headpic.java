package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

public class Headpic implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7096640583033685610L;

	private String url;

	private Thumb thumb;

	public String getUrl() {
		return url + "!160";
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Thumb getThumb() {
		return thumb;
	}

	public void setThumb(Thumb thumb) {
		this.thumb = thumb;
	}

	@Override
	public String toString() {
		return "Headpic [url=" + url + ", thumb=" + thumb + "]";
	}

}
