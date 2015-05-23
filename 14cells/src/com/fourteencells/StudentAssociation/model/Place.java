package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

public class Place implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1738232163670574164L;

	private String name;
	private String uid;
	private String addr;
	private String cp;
	private String distance;
	private String tel;
	private String poiType;
	private Point point;
	private String zip;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getCp() {
		return cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getPoiType() {
		return poiType;
	}

	public void setPoiType(String poiType) {
		this.poiType = poiType;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	@Override
	public String toString() {
		return "Place [name=" + name + ", uid=" + uid + ", addr=" + addr
				+ ", cp=" + cp + ", distance=" + distance + ", tel=" + tel
				+ ", poiType=" + poiType + ", point=" + point + ", zip=" + zip
				+ "]";
	}
}
