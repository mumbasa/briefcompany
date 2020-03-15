package com.akoo.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class SummaryMapping implements RowMapper<Summary> {

	@Override
	public Summary mapRow(ResultSet arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		Summary summary = new Summary();
		summary.setTotal(arg0.getInt(1));
		summary.setReceived(arg0.getInt(2));
		summary.setNotSeen(arg0.getInt(3));
		return summary;
	}

}
