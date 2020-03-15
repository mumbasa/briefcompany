package com.akoo.data;

public class Enquiry {
	private long id;
	private Visitor visitor;
	private String email;
	private String status;
	private String enquiry;
	private String  answeredDate;
	private String enquiryDate;
	private String reply;
	private String staffId;
	private String department;
	private String deptId;
	private String visitId;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Visitor getVisitor() {
		return visitor;
	}
	public void setVisitor(Visitor visitor) {
		this.visitor = visitor;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getEnquiry() {
		return enquiry;
	}
	public void setEnquiry(String enquiry) {
		this.enquiry = enquiry;
	}
	public String getAnsweredDate() {
		return answeredDate;
	}
	public void setAnsweredDate(String answeredDate) {
		this.answeredDate = answeredDate;
	}
	public String getEnquiryDate() {
		return enquiryDate;
	}
	public void setEnquiryDate(String enquiryDate) {
		this.enquiryDate = enquiryDate;
	}
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}
	
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	public String getVisitId() {
		return visitId;
	}
	public void setVisitId(String visitId) {
		this.visitId = visitId;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

}
