package com.akoo.data;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("persons")
public class VisitorStatus implements Serializable{
private String message;
private String visitorName;
private @Id String staff;
private String time;
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
public String getVisitorName() {
	return visitorName;
}
public void setVisitorName(String visitorName) {
	this.visitorName = visitorName;
}
public String getStaff() {
	return staff;
}
public void setStaff(String staff) {
	this.staff = staff;
}
public String getTime() {
	return time;
}
public void setTime(String time) {
	this.time = time;
}



}
