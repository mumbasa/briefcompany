package com.akoo.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class StaffBookingMapper implements RowMapper<StaffBooking>{

	@Override
	public StaffBooking mapRow(ResultSet arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		StaffBooking m = new StaffBooking();
		m.setStaffid(arg0.getString(1));
		m.setStaffName(arg0.getString(2));
		m.setBookingId(arg0.getInt(3));
		m.setStatus(arg0.getString(4));
		return m;
	}

}
