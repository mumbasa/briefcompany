package com.akoo.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class UserRowMapping implements RowMapper<User>{

	@Override
	public User mapRow(ResultSet row, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		User user = new User();
		user.setId(row.getInt("id"));
		user.setRole(row.getString("role"));
		user.setUsername(row.getString("username"));
		user.setEmail(row.getString("email"));
		user.setPassword(row.getString("password"));
		user.setStatus(row.getInt("status")==0?"Suspended": "Active");
		return user;
	}

}
