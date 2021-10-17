package com.akoo.repository;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.akoo.data.KeyValues;
import com.akoo.data.LogoutMapper;
import com.akoo.data.LogoutVisitor;
import com.akoo.data.ResultResponse;
import com.akoo.data.Visit;
import com.akoo.data.VisitRowMapping2;
import com.akoo.data.VisitSummaryMapper;
import com.akoo.data.Visitor;
import com.akoo.data.VisitorRowMapping;
import com.akoo.data.VisitorVisitSummary;

@Transactional
@Repository
public class VisitorRepository {

	@Autowired
	public JdbcTemplate jdbcTemplate;
	@Autowired
	TagRepository tagRepository;

	static Calendar c = Calendar.getInstance();

	@Value("${file.upload-dir}")
	String UPLOAD_FOLDER;

	@Value("${spring.application.name}")
	String appName;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	@Value("${company.name}")
	String companyName;

	@Value("${app.host.ip}")
	String hostIp;

	@Autowired
	RedisTemplate<String, String> messager;

	@Autowired
	MessageRepository messageRepository;

	public Visitor addVisitor(final Visitor visitor) {
		String sql = "INSERT INTO visitors(name,telephone,froms,date) VALUES(?,?,?,now())";
		KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection arg0) throws SQLException {
				// TODO Auto-generated method stub
				PreparedStatement state = arg0.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				state.setString(1, visitor.getName());
				state.setString(2, visitor.getTelephone());
				state.setString(3, visitor.getCompany());

				return state;
			}
		}, holder);
		long key = holder.getKey().longValue();
		visitor.setId(key);
		return visitor;
	}

	public Visit addVisit(final Visit visitor) {
		String sql = "INSERT INTO visitor_visits (visitor_id, time_in, staff_id,  car_number,mission,picture, date) VALUES(?,?,?,?,?,?,curdate())";
		KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection arg0) throws SQLException {
				// TODO Auto-generated method stub
				PreparedStatement state = arg0.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				state.setLong(1, visitor.getVisitorId());
				state.setString(2, visitor.getTimeIn());
				state.setString(6, visitor.getPicture());
				state.setString(3, visitor.getStaffId());
				state.setString(4, visitor.getCarNumber());
				state.setString(5, visitor.getMission());
				return state;
			}
		}, holder);
		long key = holder.getKey().longValue();
		visitor.setId(key);
		return visitor;
	}

	public long addVisit(long visitId, String staffid, String car, String mission, String picture) {
		String sql = "INSERT INTO visitor_visits (visitor_id, time_in, staff_id,  car_number,mission,picture, date) VALUES(?,current_time(),?,?,?,?,curdate())";
		KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection arg0) throws SQLException {
				// TODO Auto-generated method stub
				PreparedStatement state = arg0.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				state.setLong(1, visitId);
				state.setString(2, staffid);
				state.setString(3, car);
				state.setString(4, mission);
				state.setString(5, picture);
				return state;
			}
		}, holder);
		long key = holder.getKey().longValue();

		return key;
	}

	public int addVisitors(final Visitor visitor) {
		String sql = "INSERT INTO visitors(name,telephone,froms,date) VALUES(?,?,?,?,?)";

		int rows = jdbcTemplate.update(sql, visitor.getName(), visitor.getTelephone(), visitor.getCompany(),
				visitor.getDate());
		return rows;
	}

	public ResultResponse addColleagues(List<String> data, String bookingId) {
		String sql = "INSERT INTO visitor_colleagues(visit_id,colleague_name) VALUES(?,?)";
		ResultResponse res = new ResultResponse();
		res.setResponse("OK");

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement arg0, int arg1) {
				// TODO Auto-generated method stub
				String b = data.get(arg1);
				try {
					arg0.setString(1, bookingId);
					arg0.setString(2, b);

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					res.setResponse("false");
				}
			}

			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub

				return data.size();
			}
		});
		return res;
	}

	public int addVisit(long visitor_id, long staff_id, String car) {
		String sql = "INSERT INTO visitor_visits(visitor_id, time_in, date, staff_id,car_number) VALUES(?,current_time(),curdate(),?,car_number)";

		int rows = jdbcTemplate.update(sql, visitor_id, staff_id, car);
		return rows;
	}

	public long addVisit(String company, String name, String tel, String car, String mission, String staffid,
			MultipartFile uploadfile) {
		Visitor visitor;
		String filename = System.currentTimeMillis() + ".jpg";
		try {
//			copyFileUsingStream(uploadfile, new File(filename));

			uploadfile.transferTo(new File(UPLOAD_FOLDER + appName + filename));

		} catch (IllegalStateException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			visitor = getVisitorByTelephoneNumber(tel);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Visitor v = new Visitor();
			v.setCompany(company);
			v.setName(name);
			v.setTelephone(tel);
			visitor = addVisitor(v);
			e.printStackTrace();
		}
		try {
			jmsTemplate.convertAndSend(staffid, name + " from " + company + " wants to see you");
			messageRepository.sendSms(name + " from " + company + " wants to see you. You can call your visitor on "
					+ visitor.getTelephone() + ". Thank you", staffid);
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("message not sent");
		}
		if (isVisitorStillIn(visitor.getId())) {
			return 0;
		} else {
			return addVisit(visitor.getId(), staffid, car, mission,
					"https://" + hostIp + ":8443/" + companyName + "/" + "downloadFile/" + filename);
		}
	}

	public long addVisitFromTab(String company, String name, String tel, String car, String mission, String staffid,
			String uploadfile) {
		Visitor visitor;
		String filename = System.currentTimeMillis() + ".jpg";

		decodeBase64String(uploadfile);

		try {
			visitor = getVisitorByTelephoneNumber(tel);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Visitor v = new Visitor();
			v.setCompany(company);
			v.setName(name);
			v.setTelephone(tel);
			visitor = addVisitor(v);
			e.printStackTrace();
		}
		try {
			jmsTemplate.convertAndSend(staffid, name + " from " + company + " wants to see you");

			messageRepository.sendSms(name + " from " + company + " wants to see you. You can call your visitor on "
					+ visitor.getTelephone() + ". Thank you", staffid);

		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("message not sent");
		}
		if (isVisitorStillIn(visitor.getId())) {
			return 0;
		} else {
			return addVisit(visitor.getId(), staffid, car, mission,
					"https://" + hostIp + ":8443/" + companyName + "/" + "downloadFile/" + filename);
		}
	}

	public List<String> getVisitorColleague(long id) {
		String sql = "SElECT colleague_name FROM visitor_colleagues where visit_id=?";
		return jdbcTemplate.queryForList(sql, String.class, id);

	}

	public Visitor getVisitorByTelephoneNumber(String id) {
		String sql = "SElECT * FROM visitors where telephone=? LIMIT 1";
		Visitor v = jdbcTemplate.queryForObject(sql, new VisitorRowMapping(), id);
		v.setVisits(getVisitorVisits(id));
		return v;
	}

	public Visitor getVisitor(long id) {
		String sql = "SElECT * FROM visitors where id=?";
		Visitor visit = null;
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, id);
		if (row.next()) {
			visit = new Visitor();
			visit.setId(row.getLong(1));
			visit.setName(row.getString("name"));
			visit.setTelephone(row.getString("telephone"));
			visit.setCompany(row.getString("froms"));
			visit.setDate(row.getString("date"));

		}
		return visit;
	}

	public List<Visitor> getVisitors() {
		String sql = "SElECT * FROM visitors";
		List<Visitor> v = new ArrayList<Visitor>();
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
		while (row.next()) {
			Visitor visit = new Visitor();
			visit.setId(row.getLong(1));
			visit.setName(row.getString("name"));
			visit.setTelephone(row.getString("telephone"));
			visit.setCompany(row.getString("froms"));
			visit.setDate(row.getString("date"));
			v.add(visit);
		}

		return v;
	}

	public boolean isVisitorStillIn(long id) {
		boolean result = false;
		String sql = "SELECT *  FROM  visitor_visits where visitor_id=? and date=curdate() and time_out is null;";
		SqlRowSet set = jdbcTemplate.queryForRowSet(sql, id);
		if (set.next()) {
			result = true;
		}
		return result;
	}

	public boolean isVisitorStillIn(String id) {
		boolean result = false;
		String sql = "SELECT *  FROM  visitor_visits where visitor_id=(SELECT id from visitors where telephone=?) and date=curdate() and time_out is null;";
		SqlRowSet set = jdbcTemplate.queryForRowSet(sql, id);
		if (set.next()) {
			result = true;
		}
		return result;
	}

	public int gettStaffVisitorWaiting(String id) {
		String sql = "SELECT count(*)  FROM  visitor_visits where staff_id=? and status is null and time_out is null and date=curdate()";
		return jdbcTemplate.queryForObject(sql, Integer.class, id);
	}

	public Visitor getVisitorById(String id) {
		String sql = "SElECT * FROM visitors where id=?";
		Visitor visit = null;
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, id);
		if (row.next()) {
			visit = new Visitor();
			visit.setId(row.getLong(1));
			visit.setName(row.getString("name"));
			visit.setTelephone(row.getString("telephone"));
			visit.setCompany(row.getString("froms"));
			visit.setDate(row.getString("date"));
		}

		return visit;
	}

	public LogoutVisitor getVisitData(String id) {
		String sql = "SELECT * from notlogout where telephone=? LIMIT 1";
		LogoutVisitor v = jdbcTemplate.queryForObject(sql, new LogoutMapper(), id);

		return v;
	}

	public List<LogoutVisitor> getVisitsStillintoday() {
		String sql = "SELECT * from notlogout";
		List<LogoutVisitor> v = jdbcTemplate.query(sql, new LogoutMapper());

		return v;
	}

	public Visitor getVisitorbyVisit(String id) {
		String sql = "SELECT * FROM visitors as v where v.id=(SELECT vv.visitor_id from visitor_visits as vv where vv.visitor_id=v.id and visit_id=?)";
		Visitor v = jdbcTemplate.queryForObject(sql, new VisitorRowMapping(), id);
		v.setVisits(getVisitorVisits(id));

		return v;
	}

	public Visitor getVisitorbyVisit(String id, boolean noVisits) {
		String sql = "SELECT * FROM visitors as v where v.id=(SELECT vv.visitor_id from visitor_visits as vv where vv.visit_id=? LIMIT 1)";
		Visitor v = jdbcTemplate.queryForObject(sql, new VisitorRowMapping(), id);

		return v;
	}

	public List<Visit> getVisitorVisits(String id) {
		String sql = "SELECT * FROM visitor_visits where visitor_id=?";
		RowMapper<Visit> vis = new VisitRowMapping2();
		List<Visit> visits = jdbcTemplate.query(sql, vis, id);
		for (Visit a : visits) {
			a.setCompany(getVisitorColleague(a.getId()));
		}
		return visits;
	}

	public List<VisitorVisitSummary> getVisitsByDate(String date) {
		String sql = "SELECT visit_id,name,telephone,car_number,froms,vv.date,time_in ,time_out,status,(SELECT concat(firstname, ' ',lastname) from staff as s where s.id=vv.staff_id),mission,picture,staff_id,tag   from visitor_visits as vv join visitors as v on v.id=vv.visitor_id where vv.date=? and vv.time_out is null order by vv.visit_id desc";
		RowMapper<VisitorVisitSummary> visits = new VisitSummaryMapper();
		return jdbcTemplate.query(sql, visits, date);

	}

	public List<VisitorVisitSummary> getVisitsByDate(String from, String to) {
		String sql = "SELECT visit_id,name,telephone,car_number,froms,vv.date,time_in ,time_out,status,(SELECT concat(firstname, ' ',lastname) from staff as s where s.id=vv.staff_id),mission,picture,staff_id,tag   from visitor_visits as vv join visitors as v on v.id=vv.visitor_id where vv.date>=? and vv.date<=? and vv.time_out is null order by vv.visit_id desc";
		RowMapper<VisitorVisitSummary> visits = new VisitSummaryMapper();
		return jdbcTemplate.query(sql, visits, from, to);

	}

	public List<VisitorVisitSummary> getVisitsToday() {
		String sql = "SELECT visit_id,name,telephone,car_number,froms,vv.date,time_in ,time_out,status,(SELECT concat(firstname, ' ',lastname) from staff as s where s.id=vv.staff_id),mission,picture,staff_id,tag   from visitor_visits as vv join visitors as v on v.id=vv.visitor_id where vv.date=curdate()  order by vv.visit_id desc";
		RowMapper<VisitorVisitSummary> visits = new VisitSummaryMapper();
		return jdbcTemplate.query(sql, visits);

	}

	public List<VisitorVisitSummary> getVisitsTodayNotSignedOut() {
		String sql = "SELECT visit_id,name,telephone,car_number,froms,vv.date,time_in ,time_out,status,(SELECT concat(firstname, ' ',lastname) from staff as s where s.id=vv.staff_id),mission,picture,staff_id,tag   from visitor_visits as vv join visitors as v on v.id=vv.visitor_id where vv.date=curdate() and vv.time_out is null order by vv.visit_id desc";
		RowMapper<VisitorVisitSummary> visits = new VisitSummaryMapper();
		return jdbcTemplate.query(sql, visits);

	}

	public List<VisitorVisitSummary> getVisitsAcceptButNoTags() {
		String sql = "SELECT visit_id,name,telephone,car_number,froms,vv.date,time_in ,time_out,status,(SELECT concat(firstname, ' ',lastname) from staff as s where s.id=vv.staff_id),mission,picture,staff_id,tag   from visitor_visits as vv join visitors as v on v.id=vv.visitor_id where vv.date=curdate()  and tag is null and status is not null and time_out is  null order by vv.visit_id desc";
		RowMapper<VisitorVisitSummary> visits = new VisitSummaryMapper();
		return jdbcTemplate.query(sql, visits);

	}

	public List<VisitorVisitSummary> getStaffVisitsToday(String id) {
		String sql = "SELECT visit_id,name,telephone,car_number,froms,vv.date,time_in ,time_out,status,(SELECT concat(firstname, ' ',lastname) from staff as s where s.id=vv.staff_id),mission,picture,staff_id,tag   from visitor_visits as vv join visitors as v on v.id=vv.visitor_id where vv.staff_id=? and vv.date=curdate() order by vv.visit_id desc";
		RowMapper<VisitorVisitSummary> visits = new VisitSummaryMapper();
		return jdbcTemplate.query(sql, visits, id);

	}

	public List<VisitorVisitSummary> getWaitingVisitsToday() {
		String sql = "SELECT visit_id,name,telephone,car_number,froms,vv.date,time_in ,time_out,status,(SELECT concat(firstname, ' ',lastname) from staff as s where s.id=vv.staff_id),mission,picture,staff_id,tag  from visitor_visits as vv join visitors as v on v.id=vv.visitor_id where status is null and time_out is  null and vv.date=curdate() order by vv.visit_id asc";
		RowMapper<VisitorVisitSummary> visits = new VisitSummaryMapper();
		return jdbcTemplate.query(sql, visits);

	}

	public List<VisitorVisitSummary> getStaffVisitors(long id) {
		String sql = "SELECT visit_id,name,telephone,car_number,froms,vv.date,time_in ,time_out,status,(SELECT concat(firstname, ' ',lastname) from staff as s where s.id=vv.staff_id),mission,picture,staff_id,tag   from visitor_visits as vv join visitors as v on v.id=vv.visitor_id where vv.staff_id=? and vv.date=curdate() order by vv.visit_id desc";
		RowMapper<VisitorVisitSummary> visits = new VisitSummaryMapper();
		return jdbcTemplate.query(sql, visits, id);

	}

	public List<VisitorVisitSummary> getStaffVisitorsByTelephone(String id) {
		String sql = "SELECT visit_id,name,telephone,car_number,froms,vv.date,time_in ,time_out,status,(SELECT concat(firstname, ' ',lastname) from staff as s where s.id=vv.staff_id),mission,picture,staff_id,tag   from visitor_visits as vv join visitors as v on v.id=vv.visitor_id where vv.staff_id=?  order by vv.visit_id desc";
		RowMapper<VisitorVisitSummary> visits = new VisitSummaryMapper();
		return jdbcTemplate.query(sql, visits, id);

	}
	
	public List<VisitorVisitSummary> getVisitorsVisitsByTelephone(String id) {
		String sql = "SELECT visit_id,name,telephone,car_number,froms,vv.date,time_in ,time_out,status,(SELECT concat(firstname, ' ',lastname) from staff as s where s.id=vv.staff_id),mission,picture,staff_id,tag   from visitor_visits as vv join visitors as v on v.id=vv.visitor_id where v.telephone=?  order by vv.visit_id desc";
		RowMapper<VisitorVisitSummary> visits = new VisitSummaryMapper();
		return jdbcTemplate.query(sql, visits, id);

	}

	public List<VisitorVisitSummary> getStaffVisitor(String id) {
		String sql = "SELECT visit_id,name,telephone,car_number,froms,vv.date,time_in ,time_out,status,(SELECT concat(firstname, ' ',lastname) from staff as s where s.id=vv.staff_id),mission,picture,staff_id,tag   from visitor_visits as vv join visitors as v on v.id=vv.visitor_id where vv.staff_id=? order by vv.visit_id desc";
		RowMapper<VisitorVisitSummary> visits = new VisitSummaryMapper();
		return jdbcTemplate.query(sql, visits, id);

	}

	public VisitorVisitSummary getSummByTelephone(String id) {
		String sql = "SELECT visit_id,name,telephone,car_number,froms,vv.date,time_in ,time_out,status,(SELECT concat(firstname, ' ',lastname) from staff as s where s.id=vv.staff_id),mission,picture,staff_id,tag   from visitor_visits as vv join visitors as v on v.id=vv.visitor_id where vv.visitor_id IN (SELECT id from visitors where telephone=?) and vv.date=curdate()  and time_out is null LIMIT 1";
		RowMapper<VisitorVisitSummary> visits = new VisitSummaryMapper();
		return jdbcTemplate.queryForObject(sql, visits, id);

	}

	public VisitorVisitSummary getSummById(String id) {
		String sql = "SELECT visit_id,name,telephone,car_number,froms,vv.date,time_in ,time_out,status,(SELECT concat(firstname, ' ',lastname) from staff as s where s.id=vv.staff_id),mission,picture,staff_id,tag   from visitor_visits as vv join visitors as v on v.id=vv.visitor_id where vv.visit_id=? ";
		RowMapper<VisitorVisitSummary> visits = new VisitSummaryMapper();
		return jdbcTemplate.queryForObject(sql, visits, id);

	}

	public List<Visitor> getStaffVisitors(String id) {
		String sql = "SELECT  id,  name,telephone,froms,date from visitors as v where v.id in (SELECT visitor_id from visitor_visits where staff_id=?)";
		RowMapper<Visitor> visits = new VisitorRowMapping();
		return jdbcTemplate.query(sql, visits, id);

	}

	public List<VisitorVisitSummary> getStaffVisitorByDay(String id, String date) {
		String sql = "SELECT visit_id,name,telephone,car_number,froms,vv.date,time_in ,time_out,status,(SELECT concat(firstname, ' ',lastname),mission from staff as s where s.id=vv.staff_id),mission,picture,staff_id,tag   from visitor_visits as vv join visitors as v on v.id=vv.visitor_id where vv.staff_id=? and vv.date=? and vv.time_out is null order by vv.visit_id desc";
		RowMapper<VisitorVisitSummary> visits = new VisitSummaryMapper();
		return jdbcTemplate.query(sql, visits, id, date);

	}

	public ResultResponse visitorSignOut(String id) {
		System.out.println(id + "------------");
		VisitorVisitSummary sum = getSummByTelephone(id);
		if (sum.getTag() != null) {
			tagRepository.returnTag(Integer.parseInt(sum.getTag()));
		}
		String sql = "UPDATE visitor_visits set time_out=current_time() where date=curdate() and visitor_id=(SELECT id from visitors where telephone=?);";
		ResultResponse response = new ResultResponse();
		response.setResponse("" + jdbcTemplate.update(sql, id));
		return response;
	}

	public ResultResponse visitorWebAppSignOut(String id) {
		System.out.println(id + "------------");
		VisitorVisitSummary sum = getSummById(id);
		if (sum.getTag() != null) {
			tagRepository.returnTag(Integer.parseInt(sum.getTag()));
		}
		String sql = "UPDATE visitor_visits set time_out=current_time() where date=curdate() and visit_id=?;";
		ResultResponse response = new ResultResponse();
		response.setResponse("" + jdbcTemplate.update(sql, id));
		return response;
	}

	public int visitorStatus(String id) {
		String sql = "UPDATE visitor_visits set status='Responded' where id=?";
		return jdbcTemplate.update(sql, id);
	}

	public List<KeyValues> visitsByDepartments() {
		List<KeyValues> vals = new ArrayList<KeyValues>();
		String sql = "SELECT d.department,count(*) FROM visitor_visits as vv join staff as s on vv.staff_id=s.id join department as d on d.dept_id=s.dept group by dept;";
		SqlRowSet set = jdbcTemplate.queryForRowSet(sql);
		while (set.next()) {
			KeyValues kv = new KeyValues();
			kv.setKey(set.getString(1));
			kv.setValue(set.getInt(2));
			vals.add(kv);
		}
		return vals;
	}

	public List<KeyValues> getSummaryData() {
		List<KeyValues> vals = new ArrayList<KeyValues>();
		String sql = "SELECT mission,count(*) FROM visitor_visits where mission is not null and date=curdate() group by mission ;";
		SqlRowSet set = jdbcTemplate.queryForRowSet(sql);
		while (set.next()) {
			KeyValues vs = new KeyValues();
			vs.setKey(set.getString(1));
			vs.setValue(set.getInt(2));
			vals.add(vs);
		}
		return vals;

	}

	public List<KeyValues> visitsByMission() {
		List<KeyValues> vals = new ArrayList<KeyValues>();
		String sql = "SELECT mission,count(*) FROM visitor_visits where mission is not null group by mission ;";
		SqlRowSet set = jdbcTemplate.queryForRowSet(sql);
		while (set.next()) {
			KeyValues vs = new KeyValues();
			vs.setKey(set.getString(1));
			vs.setValue(set.getInt(2));
			vals.add(vs);
		}
		return vals;

	}

	public List<KeyValues> monthlyVisits() {
		List<KeyValues> data = new ArrayList<KeyValues>();
		String sql = "SELECT month(date),count(*) FROM visitor_visits where year(date)=year(curdate()) group by month(date);";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
		while (row.next()) {
			KeyValues kv = new KeyValues();
			kv.setKey(row.getString(1));
			kv.setValue(row.getInt(2));
			data.add(kv);
		}
		return data;
	}

	public List<KeyValues> yearlyVisits() {
		List<KeyValues> data = new ArrayList<KeyValues>();
		String sql = "SELECT year(date),count(*) FROM visitor_visits group by year(date);";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
		while (row.next()) {
			KeyValues kv = new KeyValues();
			kv.setKey(row.getString(1));
			kv.setValue(row.getInt(2));
			data.add(kv);
		}
		return data;
	}

	public List<KeyValues> visionBymission() {
		List<KeyValues> data = new ArrayList<KeyValues>();
		String sql = "SELECT mission, count(*) FROM visitor_visits group by mission;";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
		while (row.next()) {
			KeyValues kv = new KeyValues();
			kv.setKey(row.getString(1));
			kv.setValue(row.getInt(2));
			data.add(kv);
		}
		return data;
	}

	public int[] visitorSummaryToday() {
		int[] data = new int[3];
		String sql = "SELECT count(*) ,(SELECT count(*) from visitor_visits where status ='Accepted' and time_out is null and date=curdate()),(SELECT count(*) FROM appointments where appointment_date=curdate() and status='accepted') FROM visitor_visits where date=curdate();";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
		if (row.next()) {
			data[0] = row.getInt(1);
			data[1] = row.getInt(2);
			data[2] = row.getInt(3);
		}
		return data;
	}

	public int[] visitorDataSummary() {
		int[] data = new int[10];
		String sql = "SELECT (SELECT count(distinct telephone) FROM visitors),(SELECT count(*) FROM appointments where status='accepted'),(SELECT count(*) FROM staff where status='active'),(SELECT count(*) FROM visitor_visits),(SELECT count(*) FROM visitor_visits where status='accepted'),(SELECT count(*) FROM staff),(SELECT count(*) FROM department),(SELECT count(*) FROM appointments),(SELECT count(*) FROM visitor_visits where year(date)=year(curdate())),(SELECT count(*) FROM staff);";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
		if (row.next()) {
			data[0] = row.getInt(1);
			data[1] = row.getInt(2);
			data[2] = row.getInt(3);
			data[3] = row.getInt(4);
			data[4] = row.getInt(5);
			data[5] = row.getInt(6);
			data[6] = row.getInt(7);
			data[7] = row.getInt(8);
			data[8] = row.getInt(9);
			data[9] = row.getInt(10);
		}
		return data;
	}

	public ResultResponse visitorAccept(String visitId) {
		VisitorVisitSummary visitData = getSummById(visitId);
		String messageToStore = visitData.getStaffName() + ";Accept Visit; Accept visit From "
				+ visitData.getVisitorName() + " from " + visitData.getCompany() + ";" + System.currentTimeMillis();
		jmsTemplate.convertAndSend(companyName + ":reception", messageToStore);
		messager.opsForList().leftPush(companyName + ":reception:messages", messageToStore);
		String sql = "UPDATE visitor_visits set status='Accepted' where visit_id=?";
		int row = jdbcTemplate.update(sql, visitId);
		ResultResponse r = new ResultResponse();
		r.setResponse("" + row);
		return r;

	}

	public ResultResponse visitorReject(String visitId) {
		VisitorVisitSummary visitData = getSummById(visitId);
		String messageToStore = visitData.getStaffName() + ";Decline Visit; Declines visit From "
				+ visitData.getVisitorName() + " from " + visitData.getCompany() + ";" + System.currentTimeMillis();
		jmsTemplate.convertAndSend(companyName + ":reception", messageToStore);
		messager.opsForList().leftPush(companyName + ":reception:messages", messageToStore);
		String sql = "UPDATE visitor_visits set status='Declined' where visit_id=?";
		int row = jdbcTemplate.update(sql, visitId);
		ResultResponse r = new ResultResponse();
		r.setResponse("" + row);
		return r;

	}

	public int giveTags(long visitId, String tag) {
		// visitorStatus(visitId);
		VisitorVisitSummary visitData = getSummById(String.valueOf(visitId));
		String sql = "UPDATE visitor_visits set tag=? where visit_id=?";

		int row = jdbcTemplate.update(sql, tag, visitId);
		jmsTemplate.convertAndSend(visitData.getStaffid(), "Your vistor ," + visitData.getVisitorName() + " from "
				+ visitData.getCompany() + " is on the way to you");
		tagRepository.giveTag(Integer.parseInt(tag));
		return row;

	}

	/*
	 * private static String encodeFileToBase64Binary(File file) { String
	 * encodedfile = null; try { FileInputStream fileInputStreamReader = new
	 * FileInputStream(file); byte[] bytes = new byte[(int) file.length()];
	 * fileInputStreamReader.read(bytes); fileInputStreamReader.close(); encodedfile
	 * = new String(Base64.encodeBase64(bytes), "UTF-8");
	 * 
	 * } catch (IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * return encodedfile; }
	 */
	public String decodeBase64String(String fileString) {
		String filename = System.currentTimeMillis() + ".jpg";
		byte dearr[] = Base64.getDecoder().decode(fileString.replaceAll(" ", "+"));

		try {
			FileUtils.writeByteArrayToFile(new File(UPLOAD_FOLDER + appName + filename), dearr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filename;

	}

}
