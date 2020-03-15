package com.akoo.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class DepartmentRowMapper implements RowMapper<Department>{

	@Override
	public Department mapRow(ResultSet arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		Department dept = new Department();
		dept.setId(arg0.getLong(1));
		dept.setDepartment(arg0.getString(2));
		return dept;
	}

}
