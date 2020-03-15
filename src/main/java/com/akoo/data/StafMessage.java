package com.akoo.data;

public class StafMessage {
private long id;
private String message;
private String sender;
private String dateTime;
private long timeStamp;
private String title;
public long getId() {
	return id;
}
public void setId(long id) {
	this.id = id;
}
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
public String getSender() {
	return sender;
}
public void setSender(String sender) {
	this.sender = sender;
}
public long getTimeStamp() {
	return timeStamp;
}
public void setTimeStamp(long timeStamp) {
	this.timeStamp = timeStamp;
}
public String getDateTime() {
	return dateTime;
}
public void setDateTime(String dateTime) {
	this.dateTime = dateTime;
}
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}

}
