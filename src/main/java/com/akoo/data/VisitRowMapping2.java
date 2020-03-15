package com.akoo.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import com.akoo.repository.VisitorRepository;

public class VisitRowMapping2 implements RowMapper<Visit>{

	@Autowired
	VisitorRepository v;
	@Override
	public Visit mapRow(ResultSet row, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		Visit visitor = new Visit();
		visitor.setId(row.getLong("visit_id"));
		visitor.setCarNumber(row.getString("car_number"));
		visitor.setTimeIn( row.getString("time_in"));
		visitor.setTimeOut(row.getString("time_out"));
		visitor.setDate(row.getString("date"));
		visitor.setVisitorId(row.getLong("visitor_id"));
		visitor.setStaffId(row.getString("staff_id"));
		return visitor;
	}

}
