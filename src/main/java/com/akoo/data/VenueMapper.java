package com.akoo.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class VenueMapper implements RowMapper<Venue>{

	@Override
	public Venue mapRow(ResultSet arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		Venue dept = new Venue();
		dept.setId(arg0.getInt(1));
		dept.setVenue(arg0.getString(2));
		return dept;
	}

}
