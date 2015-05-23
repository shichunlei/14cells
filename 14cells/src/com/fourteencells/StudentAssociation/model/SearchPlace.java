package com.fourteencells.StudentAssociation.model;

import java.io.Serializable;

/**
 * 搜索热点实体类
 * 
 * @author 师春雷
 * 
 */
public class SearchPlace implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3678981699263901521L;

	private String name;
	private Location location;
	private String address;
	private String street_id;
	private String uid;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getStreet_id() {
		return street_id;
	}

	public void setStreet_id(String street_id) {
		this.street_id = street_id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Override
	public String toString() {
		return "SearchPlace [name=" + name + ", location=" + location
				+ ", address=" + address + ", street_id=" + street_id
				+ ", uid=" + uid + "]";
	}
}
