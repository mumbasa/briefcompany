package com.akoo.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class LogoutMapper implements RowMapper<LogoutVisitor>{

	@Override
	public LogoutVisitor mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		LogoutVisitor visit = new LogoutVisitor();
		visit.setVisitorName(rs.getString(1));
		visit.setTelephone(rs.getString(2));
		visit.setCompany(rs.getString(3));
		visit.setTimeIn(rs.getString(4));
		visit.setVisitId(rs.getLong(5));
		
		return visit;
	}

}
