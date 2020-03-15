package com.akoo.data;

public class Booking {
	private long id;
	private String venue;
	private String date;
	private String start;
	private String stop;
	private long startLong;
	private long stopLong;
	private String staff;
	private long datAdded;
	private String agenda;
	private String status;
	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getStop() {
		return stop;
	}

	public void setStop(String stop) {
		this.stop = stop;
	}

	public long getStartLong() {
		return startLong;
	}

	public void setStartLong(long startLong) {
		this.startLong = startLong;
	}

	public long getStopLong() {
		return stopLong;
	}

	public void setStopLong(long stopLong) {
		this.stopLong = stopLong;
	}

	public String getStaff() {
		return staff;
	}

	public void setStaff(String staff) {
		this.staff = staff;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDatAdded() {
		return datAdded;
	}

	public void setDatAdded(long datAdded) {
		this.datAdded = datAdded;
	}

	public String getAgenda() {
		return agenda;
	}

	public void setAgenda(String agenda) {
		this.agenda = agenda;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
