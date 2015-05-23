package com.fourteencells.StudentAssociation.model;

public class Cover {

	private CoverTo cover;

	private Thumb thumb;

	private String url;

	public CoverTo getCover() {
		return cover;
	}

	public void setCover(CoverTo cover) {
		this.cover = cover;
	}

	public Thumb getThumb() {
		return thumb;
	}

	public void setThumb(Thumb thumb) {
		this.thumb = thumb;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Cover [cover=" + cover + ", thumb=" + thumb + ", url=" + url
				+ "]";
	}

}