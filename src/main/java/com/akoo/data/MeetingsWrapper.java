package com.akoo.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class MeetingsWrapper implements RowMapper<Meeting>{

	@Override
	public Meeting mapRow(ResultSet row, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		Meeting meeting = new Meeting();
		meeting.setId(row.getLong(1));
		meeting.setAgenda(row.getString(2));
		meeting.setDate(row.getString(3));
		meeting.setStarttime(row.getString(4));
		meeting.setEndtime(row.getString(5));
		meeting.setVenue(row.getString(6));
		return meeting;
	}

}
