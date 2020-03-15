package com.akoo.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class EnquiryMapper implements RowMapper<Enquiry> {

	@Override
	public Enquiry mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Enquiry enquiry = new Enquiry();
		enquiry.setId(rs.getLong(1));
		enquiry.setVisitId(rs.getString(2));
		enquiry.setStatus(rs.getString(3));
		enquiry.setAnsweredDate(rs.getString(4));
		enquiry.setEnquiry(rs.getString(5));
		enquiry.setReply(rs.getString(6));
		enquiry.setEmail(rs.getString(7));
		enquiry.setEnquiryDate(rs.getString(8));
		enquiry.setDepartment(rs.getString(9));
		return enquiry;
	}

}
