package com.akoo.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import com.akoo.data.ResultResponse;
import com.akoo.data.User;
import com.akoo.data.UserRowMapping;

@Repository
public class UserRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private BCryptPasswordEncoder encoder;

	public ResultResponse insertUser(User user) {
		String sql = "INSERT INTO users(username, password, role, email,status,date_added) VALUES(?,?,?,?,?,curdate())";
		int rows = jdbcTemplate.update(sql, user.getUsername(), encoder.encode(user.getPassword()), user.getRole(),
				user.getEmail(), user.getStatus());

		ResultResponse r = new ResultResponse();
		r.setResponse(rows + "");
		return r;
	}

	public ResultResponse deletetUser(int id) {
		String sql = "DELETE FROM users where id=?";
		int rows = 0;
		rows = jdbcTemplate.update(sql, id);
		ResultResponse r = new ResultResponse();
		r.setResponse(rows + "");
		return r;
	}

	public ResultResponse changePassword(int id, String password) {
		String sql = "UPDATE users set password=? where id=?";
		int rows = jdbcTemplate.update(sql, encoder.encode(password), id);

		ResultResponse r = new ResultResponse();
		r.setResponse(rows + "");
		return r;
	}

	public ResultResponse changePassword(String email, String password) {
		String sql = "UPDATE users set password=? where email=?";
		int rows = jdbcTemplate.update(sql, encoder.encode(password), email);

		ResultResponse r = new ResultResponse();
		r.setResponse(rows + "");
		return r;
	}

	public ResultResponse suspendUser(int id) {
		String sql = "UPDATE users set status=0 where id=?";
		int rows = jdbcTemplate.update(sql, id);

		ResultResponse r = new ResultResponse();
		r.setResponse(rows + "");
		return r;
	}

	public ResultResponse activate(int id) {
		String sql = "UPDATE users set status=1 where id=?";
		int rows = jdbcTemplate.update(sql, id);

		ResultResponse r = new ResultResponse();
		r.setResponse(rows + "");
		return r;
	}

	public List<User> getUsers() {
		String sql = "SELECT * FROM users";
		RowMapper<User> users = new UserRowMapping();
		return jdbcTemplate.query(sql, users);

	}

	public List<Integer> getReceptionistCount() {
		List<Integer> data = new ArrayList<Integer>();
		String sql = "select (SELECT count(*) FROM users where role='Receptionist'),(SELECT count(*) FROM staff),(SELECT count(*) FROM department)";
		SqlRowSet set = jdbcTemplate.queryForRowSet(sql);
		if (set.next()) {
			data.add(set.getInt(1));
			data.add(set.getInt(2));
			data.add(set.getInt(3));
		}
		return data;
	}

	public User getUser(String username, String password) {
		User user;
		String sql = "SELECT * FROM users where username=?";
		RowMapper<User> userMapper = new UserRowMapping();
		user = jdbcTemplate.queryForObject(sql, userMapper, username);

		if (encoder.matches(password, user.getPassword())) {
			return user;
		} else {
			return null;
		}
	}

	public User getUser(String username) {
		User user;
		String sql = "SELECT * FROM users where email=?";
		RowMapper<User> userMapper = new UserRowMapping();
		user = jdbcTemplate.queryForObject(sql, userMapper, username);
		return user;
	}
}
