package com.akoo.api;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.akoo.data.Appointment;

public class AppointmentMapper implements RowMapper<Appointment> {


	@Override
	public Appointment mapRow(ResultSet row, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		Appointment app = new Appointment();
		app.setId(row.getLong("id"));
		app.setStatus(row.getString("status"));		
		app.setVisitId(row.getString("visit_id"));
		app.setAppointmentDate(row.getString("appointment_date"));
		app.setAppointmentTime(row.getString("appointment_time"));
		app.setTimeStamp(row.getLong("timestamp_date"));
		app.setPurpose(row.getString("reason"));
		app.setMessage(row.getString("response_message"));
		app.setStaffId(row.getString("staff_id"));
		app.setEmail(row.getString("email"));
		app.setBookingDate(row.getString("date"));
		app.setTitle(row.getString("title"));
		return app;
	}

}
