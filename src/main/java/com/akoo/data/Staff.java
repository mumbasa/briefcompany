package com.akoo.data;

public class Staff {
private String id;
private String password;
private String dept;
private String firstname;
private String middleName;
private String lastName;
private String email;
private String position;
private String company;
private long rowId;
private String status;

public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
public String getDept() {
	return dept;
}
public void setDept(String dept) {
	this.dept = dept;
}
public String getFirstname() {
	return firstname;
}
public void setFirstname(String firstname) {
	this.firstname = firstname;
}
public String getMiddleName() {
	return middleName;
}
public void setMiddleName(String middleName) {
	this.middleName = middleName;
}
public String getLastName() {
	return lastName;
}
public void setLastName(String lastName) {
	this.lastName = lastName;
}
public String getPosition() {
	return position;
}
public void setPosition(String position) {
	this.position = position;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public long getRowId() {
	return rowId;
}
public void setRowId(long rowId) {
	this.rowId = rowId;
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public String getCompany() {
	return company;
}
public void setCompany(String company) {
	this.company = company;
}

public String getName() {
	return lastName+" "+firstname;
	
}



}
