package com.fourteencells.StudentAssociation.model;

public class Thumb {

	private String url;

	public String getUrl() {
		return url + "!160";
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Thumb [url=" + url + "]";
	}

}
