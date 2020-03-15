package com.akoo.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class VisitSummaryMapper implements RowMapper<VisitorVisitSummary>{



	@Override
	public VisitorVisitSummary mapRow(ResultSet row, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		VisitorVisitSummary visit = new VisitorVisitSummary();
		visit.setVisitId(row.getLong(1));
		visit.setVisitorName(row.getString(2));
		visit.setTelephone(row.getString(3));
		visit.setCarNumber(row.getString(4));
		visit.setCompany(row.getString(5));
		visit.setDate(row.getString(6));
		visit.setTimeIn(row.getString(7));
		visit.setTimeOut(row.getString(8));
		visit.setStatus(row.getString(9));
		visit.setStaffName(row.getString(10));
		visit.setMission(row.getString(11));
		visit.setPicture(row.getString(12));
		visit.setStaffid(row.getString(13));
		visit.setTag(row.getString(14));
		return visit;
	}

}
