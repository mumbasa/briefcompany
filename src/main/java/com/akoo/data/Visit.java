package com.akoo.data;

import java.util.List;

public class Visit {
	private long id;
	private String timeIn;
	private String timeOut;
	private String date;
	private String mission;
	private String staffId;
	private String email;
	private String carNumber;
	private List<String> company; 
	private long visitorId;
	private String picture;
	public String getTimeIn() {
		return timeIn;
	}
	public void setTimeIn(String timeIn) {
		this.timeIn = timeIn;
	}
	public String getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(String timeOut) {
		this.timeOut = timeOut;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getMission() {
		return mission;
	}
	public void setMission(String mission) {
		this.mission = mission;
	}
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCarNumber() {
		return carNumber;
	}
	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}
	public List<String> getCompany() {
		return company;
	}
	public void setCompany(List<String> company) {
		this.company = company;
	}
	public long getVisitorId() {
		return visitorId;
	}
	public void setVisitorId(long visitorId) {
		this.visitorId = visitorId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	
}
