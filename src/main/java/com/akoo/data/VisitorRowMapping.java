package com.akoo.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;

import com.akoo.repository.VisitorRepository;

public class VisitorRowMapping implements RowMapper<Visitor>{

	@Autowired
	VisitorRepository visitor;
	@Override
	public Visitor mapRow(ResultSet row, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		Visitor visit = new Visitor();
		visit.setId(row.getLong(1));
		visit.setName(row.getString("name"));
		visit.setTelephone(row.getString("telephone"));
		visit.setCompany(row.getString("froms"));
		visit.setDate(row.getString("date"));
		
		return visit;
	}

}
