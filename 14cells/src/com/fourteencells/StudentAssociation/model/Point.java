package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

public class Point implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4614310207066754431L;

	public String x;
	public String y;

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}

}
