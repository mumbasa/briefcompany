package com.akoo.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class BookingMapper implements RowMapper<Booking> {

	@Override
	public Booking mapRow(ResultSet row, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		Booking booking = new Booking();
		booking.setId(row.getLong(1));
		booking.setVenue(row.getString(2));
		booking.setStaff(row.getString(3));
		booking.setStart(row.getString(4));
		booking.setStop(row.getString(5));
		booking.setDate(row.getString(6));
		booking.setStopLong(row.getLong(7));
		booking.setStartLong(row.getLong(8));
		booking.setAgenda(row.getString(9));
		booking.setDatAdded(row.getLong(10));
		booking.setStatus(row.getString(11));
		return booking;
	}

}
