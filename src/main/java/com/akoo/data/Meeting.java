package com.akoo.data;

import java.util.List;

public class Meeting {
	private String agenda;
	private String date;
	private String starttime;
	private String endtime;
	private String venue;
	private long id;
	private List<Staff> attendants;
	public String getAgenda() {
		return agenda;
	}
	public void setAgenda(String agenda) {
		this.agenda = agenda;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public List<Staff> getAttendants() {
		return attendants;
	}
	public void setAttendants(List<Staff> attendants) {
		this.attendants = attendants;
	}
	public String getVenue() {
		return venue;
	}
	public void setVenue(String venue) {
		this.venue = venue;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

}
