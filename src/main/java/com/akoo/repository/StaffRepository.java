package com.akoo.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.akoo.data.Department;
import com.akoo.data.DepartmentRowMapper;
import com.akoo.data.Memo;
import com.akoo.data.MemoMapper;
import com.akoo.data.ObjectValue;
import com.akoo.data.ResultResponse;
import com.akoo.data.Staff;
import com.akoo.data.StaffRowMapping;
import com.akoo.data.Summary;
import com.akoo.data.SummaryMapping;

@Repository
public class StaffRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	MessageRepository messageRepository;

	@Autowired
	VisitorRepository visitorRepository;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Value("${file.upload-dir}")
	String uploadFolder;

	@Value("${company.name}")
	String companyName;

	public ResultResponse saveStaff(Staff staff) {
		ResultResponse response = new ResultResponse();
		String sql = "INSERT INTO staff(id,firstname,lastname,dept,position) VALUES(?,?,?,?,?)";
		int rows = jdbcTemplate.update(sql, staff.getId(), staff.getFirstname(), staff.getLastName(), staff.getDept(),
				staff.getPosition());
		response.setResponse(rows + "");
		return response;

	}

	public int saveStaff(String email, String contact, String position, int department, String firstName,
			String lastName) {
			// TODO: handle exception
			String generatedString = RandomStringUtils.random(6, false, true);
			String sql = "INSERT INTO staff(id,firstname,lastname,dept,position,email,password) VALUES(?,?,?,?,?,?,?)";

			int rows = jdbcTemplate.update(sql, contact, firstName, lastName, department, position, email,
					passwordEncoder.encode(generatedString));
			messageRepository.sendSms(
					generatedString
							+ " is your Briefta VMS App login & verification PIN. Please keep it safe.\nThank you",
					contact);
			return rows;
	

	}

	public void saveStaff(List<Staff> staff) {

		String sql = "INSERT INTO staff(id,firstname,lastname,dept,position,email,password) VALUES(?,?,?,?,?,?,?)";
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				// TODO Auto-generated method stub
				String generatedString = RandomStringUtils.random(6, false, true);
				Staff individual = staff.get(i);
				ps.setString(1, individual.getId());
				ps.setString(2, individual.getFirstname());
				ps.setString(3, individual.getLastName());
				ps.setString(4, individual.getDept());
				ps.setString(5, individual.getPosition());
				ps.setString(6, individual.getEmail());
				ps.setString(7, passwordEncoder.encode(generatedString));

				messageRepository.sendSms(generatedString
						+ " is your Briefta VMS App login and verification PIN. Please keep it safe.\nThank you by Briefta",
						individual.getId());

			}

			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return staff.size();
			}
		});

	}

	public ResultResponse saveDept(String dept) {
		ResultResponse response = new ResultResponse();
		String sql = "INSERT INTO department(department) VALUES(?)";
		int rows = jdbcTemplate.update(sql, dept);
		response.setResponse(rows + "");
		return response;

	}

	public int deleteStaff(String id) {
		String sql = "DELETE FROM staff where sid=?";
		return jdbcTemplate.update(sql, id);

	}

	public int suspendStaff(String id) {
		String sql = "UPDATE staff set status='suspended' where sid=?";
		return jdbcTemplate.update(sql, id);
	}

	public int activateStaff(String id) {
		String sql = "UPDATE staff set status='active' where sid=?";
		return jdbcTemplate.update(sql, id);
	}

	public int deleteDept(int id) {
		String sql = "DELETE FROM department where dept_id=?";
		return jdbcTemplate.update(sql, id);

	}

	public int changeStaffPassword(String id, String password) {
		String sql = "DELETE FROM staff where id=?";
		return jdbcTemplate.update(sql, id);

	}

	public ResultResponse changeInfo(String id, String lname, String fname, String dept) {
		String sql = "UPDATE staff set dept=?,lastname=?,firstname=? where id=?";
		ResultResponse resp = new ResultResponse();
		resp.setResponse("" + jdbcTemplate.update(sql, dept, lname, fname, id));
		return resp;
	}

	public Staff getStaff(String id) {
		String sql = "SELECT * FROM staff as s join department as d on s.dept=d.dept_id where id=?";
		Staff staff = null;
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql,id);
		if(row.next()) {
			staff = new Staff();
			staff.setDept(row.getString(12));
			staff.setFirstname(row.getString("firstname"));
			staff.setPosition(row.getString("position"));
			staff.setLastName(row.getString("lastname"));
			staff.setId(row.getString("id"));
			staff.setEmail(row.getString("email"));
			staff.setPassword(row.getString("password"));
			staff.setRowId(row.getLong(8));
			staff.setStatus(row.getString("status"));
		}
		return staff;

	}

	public Staff getStaffAuth(String id, String password) {
		Staff staff = null;
		String sql = "SELECT * FROM staff as s join department as d on s.dept=d.dept_id where id=?";
		staff = jdbcTemplate.queryForObject(sql, new StaffRowMapping(), id);

		if (passwordEncoder.matches(password, staff.getPassword())) {
			if (staff.getStatus() == null) {
				activateStaff(String.valueOf(staff.getRowId()));
				messageRepository.sentAlert("reception",
						staff.getLastName() + " " + staff.getFirstname() + " has activated Account");
			}
			return staff;

		} else {
			return null;
		}

	}

	public List<Staff> getAllStaff() {
		String sql = "SELECT * FROM staff as s join department as d on s.dept=d.dept_id";
		RowMapper<Staff> staff = new StaffRowMapping();
		return jdbcTemplate.query(sql, staff);

	}

	public List<Department> getAllDepartment() {
		String sql = "SELECT * FROM department";
		RowMapper<Department> depts = new DepartmentRowMapper();
		return jdbcTemplate.query(sql, depts);

	}

	public List<ObjectValue> staffVistorsCount() {
		List<ObjectValue> data = new ArrayList<ObjectValue>();
		String sql = "SELECT s.id ,count(*) FROM visitor_visits as vv join staff as s on vv.staff_id=s.id join department as d on d.dept_id=s.dept group by s.id;";
		SqlRowSet set = jdbcTemplate.queryForRowSet(sql);
		while (set.next()) {
			ObjectValue ov = new ObjectValue();
			ov.setObject(getStaff(set.getString(1)));
			ov.setValue(set.getLong(2));
			data.add(ov);
		}
		return data;
	}

	public int renameDepartment(String name, long id) {
		String sql = "UPDATE department set department=? where id=?";
		return jdbcTemplate.update(sql, name, id);

	}

	public Summary getStaffVisitorsTodaySummary(String staffId) {
		String sql = "SELECT count(*),(SELECT count(*) from visitor_visits as k where k.staff_id=? and  date=curdate() and status is  null) , (SELECT count(*) from visitor_visits as k where k.staff_id=? and  date=curdate() and status is not null)  from visitor_visits as vv where date=curdate() and vv.staff_id=?;";
		return jdbcTemplate.queryForObject(sql, new SummaryMapping(), staffId, staffId, staffId);

	}

	public Summary getStaffVisitorsTodaySummary2(String staffId, String date) {
		String sql = "SELECT (SELECT count(*) FROM visitor_visits as c where status is  null and date=? and c.staff_id=?),"
				+ "(SELECT count(*) FROM appointments  as a where appointment_date>=? and a.staff_id=?), "
				+ " count(*) FROM staff_bookings  as b join venue_booking as v on b.booking_id=v.id where b.staff_id=? and v.date>=? ;";
		return jdbcTemplate.queryForObject(sql, new SummaryMapping(), date, staffId, date, staffId, staffId, date);

	}

	public List<Staff> getAStaffByDepartment(String id) {
		String sql = "SELECT * FROM staff as s join department as d on s.dept=d.dept_id where dept_id=?";
		RowMapper<Staff> staff = new StaffRowMapping();
		return jdbcTemplate.query(sql, staff, id);

	}

	public ResultResponse addMemo(Memo memo) {
		String sql = "INSERT INTO memo(title,content,deadline,date,staff) VALUES(?,?,?,curdate(),?)";
		int row = jdbcTemplate.update(sql, memo.getTitle(), memo.getContent(), memo.getDeadline(), memo.getStaff());
		ResultResponse res = new ResultResponse();
		res.setResponse(row + "");
		return res;

	}

	public List<Memo> getMemos() {
		String sql = "SELECT * FROM memo";
		RowMapper<Memo> staff = new MemoMapper();
		return jdbcTemplate.query(sql, staff);

	}

	public List<Memo> getUpcomingMemos() {
		String sql = "SELECT * FROM memo where month(deadline)>=month(deadline()) and day(deadline)>=day(curdate()) and year(deadline)>=year(curdate())";
		RowMapper<Memo> staff = new MemoMapper();
		return jdbcTemplate.query(sql, staff);

	}

	public List<Staff> extractStaff(MultipartFile file) {

		List<Staff> staff = new ArrayList<Staff>();
		Stream<String> lines;
		try {
			file.transferTo(new File(uploadFolder + companyName + "/" + file.getOriginalFilename()));

			lines = Files.lines(Paths.get(uploadFolder + companyName + "/" + file.getOriginalFilename()));
			List<String> content = lines.filter(e -> !e.contains("dep")).collect(Collectors.toList());
			content.forEach(e -> {
				System.err.println(e);
				String a[] = e.split(",");
				Staff staffer = new Staff();
				staffer.setLastName(a[0]);
				staffer.setFirstname(a[1]);
				staffer.setId(a[2]);
				staffer.setEmail(a[3]);
				staffer.setDept(a[4]);
				staffer.setPosition(a[5]);
				staff.add(staffer);
			});
			saveStaff(staff);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return staff;

	}

}
