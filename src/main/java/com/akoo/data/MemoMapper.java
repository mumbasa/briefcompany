package com.akoo.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class MemoMapper implements RowMapper<Memo>{

	@Override
	public Memo mapRow(ResultSet arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		Memo memo = new Memo();
		memo.setTitle(arg0.getString(2));
		memo.setDeadline(arg0.getString(3));
		memo.setContent(arg0.getString(4));
		memo.setDateAdded(arg0.getString(5));
		memo.setStaff(arg0.getString(6));
		memo.setId(arg0.getLong(1));
		return memo;
	}
	

}
