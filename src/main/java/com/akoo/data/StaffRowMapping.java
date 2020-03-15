package com.akoo.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class StaffRowMapping implements RowMapper<Staff> {

	@Override
	public Staff mapRow(ResultSet row, int arg1) throws SQLException {
		// TODO Auto-generated method stub
	Staff staff = new Staff();
	staff.setDept(row.getString(12));
	staff.setFirstname(row.getString("firstname"));
	staff.setPosition(row.getString("position"));
	staff.setLastName(row.getString("lastname"));
	staff.setId(row.getString("id"));
	staff.setEmail(row.getString("email"));
	staff.setPassword(row.getString("password"));
	staff.setRowId(row.getLong(8));
	staff.setStatus(row.getString("status"));
	return staff;
	}

}
