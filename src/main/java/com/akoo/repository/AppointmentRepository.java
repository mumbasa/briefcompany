package com.akoo.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Repository;

import com.akoo.api.AppointmentMapper;
import com.akoo.api.Emailer;
import com.akoo.data.Appointment;

import com.akoo.data.ResultResponse;
import com.akoo.data.Visitor;

@Repository
public class AppointmentRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	StaffRepository staffRepository;

	@Autowired
	VisitorRepository visitorRepository;

	@Autowired
	MessageRepository messageRepository;

	@Autowired
	Emailer emailService;

	@Autowired
	JmsTemplate jmsTemplate;

	@Value("${company.name}")
	String companyName;

	@Value("${company.value}")
	String company;

	
	public ResultResponse bookAppointment(String visitId, String purpose, String email, String staff) {
		String sql = "INSERT INTO appointments(visit_id,reason,email,staff_id,date) VALUES(?,?,?,?,now())";
		ResultResponse response = new ResultResponse();
		response.setResponse(jdbcTemplate.update(sql, visitId, purpose, email, staff) + "");
		jmsTemplate.convertAndSend(staff, "You have an appointment");
		return response;

	}

	public ResultResponse bookAppointment(String telephone, String company, String name, String purpose, String email,
			String staff, String title) {
		Visitor visitor = null;
		try {
			visitor = visitorRepository.getVisitorByTelephoneNumber(telephone);
		} catch (EmptyResultDataAccessException e) {
			Visitor v = new Visitor();
			v.setName(name);
			v.setCompany(company);
			v.setTelephone(telephone);
			visitor = visitorRepository.addVisitor(v);
		}

		String sql = "INSERT INTO appointments(visit_id,reason,email,staff_id,title,date) VALUES(?,?,?,?,?,now())";
		ResultResponse response = new ResultResponse();
		response.setResponse(jdbcTemplate.update(sql, visitor.getId(), purpose, email, staff, title) + "");
		jmsTemplate.convertAndSend(staff, "You have an appointment");

		return response;

	}

	public ResultResponse replyAppointment(String id, String message, String appointmentDate, String appoointmentTime,
			long time) {
		String sql = "UPDATE appointments set status='accepted',appointment_date=?,appointment_time=?,response_message=?,timestamp_date=? where id=?";
		ResultResponse response = new ResultResponse();
		response.setResponse(jdbcTemplate.update(sql, appointmentDate, appoointmentTime, message, time, id) + "");
		Appointment appointment = getAppointmentById(id);
		emailService.prepareAndSend(appointment);
		String messageTemplate = "Your appointment with " + appointment.getStaff().getFirstname() + " "
				+ appointment.getStaff().getLastName() + " of " + company
				+ " has been accepted.Your appointment is on " + appointmentDate + " at "+appoointmentTime+"\n"+ message+".Thank you\n Message From Briefta";

		messageRepository.sendSms(messageTemplate, appointment.getVisitor().getTelephone());
		return response;

	}

	public ResultResponse cancelAppointment(String id) {
		String sql = "UPDATE appointments set status='rejected',response_message=? where id=?";
		Appointment appointment = getAppointmentById(id);
		String message = "Your appointment with " + appointment.getStaff().getFirstname() + " "
				+ appointment.getStaff().getLastName() + " of " + company + " has been declined";
		ResultResponse response = new ResultResponse();
		response.setResponse(jdbcTemplate.update(sql, message, id) + "");

		emailService.prepareAndSend(appointment);
		try {
			messageRepository.sendSms(message, appointment.getVisitor().getTelephone());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;

	}

	public List<Appointment> getAllUpcomingAppointments(String date) {
		String sql = "SELECT * FROM appointments where appointment_date>=? and status='accepted'";
		List<Appointment> g = new ArrayList<Appointment>();
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql,  date);
		while (row.next()) {
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
			app.setVisitor(visitorRepository.getVisitor(Long.parseLong(app.getVisitId())));
			app.setStaff(staffRepository.getStaff(app.getStaffId()));
			g.add(app);
		}
		return g;
	}

	public List<Appointment> getStaffUpcomingAppointments(String staff, String date) {
		String sql = "SELECT * FROM appointments where staff_id=? and appointment_date>=? and status='accepted'";
		List<Appointment> g = new ArrayList<Appointment>();
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, staff, date);
		while (row.next()) {
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
			app.setVisitor(visitorRepository.getVisitor(Long.parseLong(app.getVisitId())));
			app.setStaff(staffRepository.getStaff(app.getStaffId()));
			g.add(app);
		}
		return g;
	}

	public List<Appointment> getStaffTodayAppointments(String staff) {
		String sql = "SELECT * FROM appointments where staff_id=? and appointment_date=curdate() and status='accepted'";
		List<Appointment> g = new ArrayList<Appointment>();
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql,staff);
		while (row.next()) {
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
			app.setVisitor(visitorRepository.getVisitor(Long.parseLong(app.getVisitId())));
			app.setStaff(staffRepository.getStaff(app.getStaffId()));
			g.add(app);
		}
		return g;
	}

	public List<Appointment> getStaffAppointments(String staff) {
		String sql = "SELECT * FROM appointments where staff_id=?";
		List<Appointment> g = new ArrayList<Appointment>();
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql,staff);
		while (row.next()) {
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
			app.setVisitor(visitorRepository.getVisitor(Long.parseLong(app.getVisitId())));
			app.setStaff(staffRepository.getStaff(app.getStaffId()));
			g.add(app);
		}
		return g;

	}

	public List<Appointment> getStaffUnAnsweredAppointments(String staff) {
		String sql = "SELECT * FROM appointments where staff_id=? and status is null";
		List<Appointment> g = jdbcTemplate.query(sql, new AppointmentMapper(), staff);
		for (Appointment a : g) {
			a.setStaff(staffRepository.getStaff(staff));
			a.setVisitor(visitorRepository.getVisitor(Long.parseLong(a.getVisitId())));
		}
		return g;
	}

	public List<Appointment> getAllUnAnsweredAppointments() {
		String sql = "SELECT * FROM appointments where status is null";
		List<Appointment> g = new ArrayList<Appointment>(); 
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
				while (row.next()) {
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
					app.setVisitor(visitorRepository.getVisitor(Long.parseLong(app.getVisitId())));
					app.setStaff(staffRepository.getStaff(app.getStaffId()));
					g.add(app);
				}
						
		return g;
	}

	public List<Appointment> getAnsweredAppointments() {
		String sql = "SELECT * FROM appointments where status is not null and year(date)=year(curdate())";
		List<Appointment> g = new ArrayList<Appointment>(); 
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
				while (row.next()) {
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
					app.setVisitor(visitorRepository.getVisitor(Long.parseLong(app.getVisitId())));
					app.setStaff(staffRepository.getStaff(app.getStaffId()));
					g.add(app);
				}
						
		return g;
	}

	public Appointment getAppointmentById(String id) {
		String sql = "SELECT * FROM appointments where id=?";
		Appointment app = null;
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql,id);
				if (row.next()) {
					 app = new Appointment();
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
					app.setVisitor(visitorRepository.getVisitor(Long.parseLong(app.getVisitId())));
					app.setStaff(staffRepository.getStaff(app.getStaffId()));
				}
						
		
		return app;
	}
}
