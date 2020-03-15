package com.akoo.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.akoo.api.AppointmentMapper;
import com.akoo.data.Appointment;
import com.akoo.data.Enquiry;
import com.akoo.data.EnquiryMapper;
import com.akoo.data.ResultResponse;

@Repository
public class EnquiryRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	StaffRepository staffRepository;
	
	@Autowired
	VisitorRepository visitor;
	

	public  ResultResponse addEnquiry(String visitId, String purpose, String email, String dept) {
		String sql = "INSERT INTO enquiries(visit_id,enquiry,email,department,date) VALUES(?,?,?,?,now())";
		ResultResponse response = new ResultResponse();
		response.setResponse(jdbcTemplate.update(sql, visitId, purpose, email, dept) + "");
		return response;

	}
	
	public  List<Enquiry> getEnqurries(String date) {
		String sql = "SELECT * from enquiries where date>=?";
		List<Enquiry> enquiries=jdbcTemplate.query(sql,new EnquiryMapper(), date);
		return enquiries;
	}
	
	public  List<Enquiry> getEnqurries() {
		String sql = "SELECT * from enquiries";
		return jdbcTemplate.query(sql, new EnquiryMapper());
	}	
	
	public  List<Enquiry> getUanswered() {
		String sql = "SELECT * from enquiries where status is null";
		List<Enquiry> enquiries=jdbcTemplate.query(sql,new EnquiryMapper());
		return enquiries;
	}
	
	public  List<Enquiry> getUannsweredByDept(String dept) {
		String sql = "SELECT * from enquiries where status is null and department=?";
		List<Enquiry> enquiries=jdbcTemplate.query(sql,new EnquiryMapper(), dept);
		return enquiries;
	}
	
	public  List<Enquiry> getEnquriesByDept(String dept) {
		String sql = "SELECT * from enquiries where  department=?";
		List<Enquiry> enquiries=jdbcTemplate.query(sql,new EnquiryMapper(), dept);
		return enquiries;
	}
	
	public ResultResponse answerEnquiry(String reply,String staff){
		String sql= "UPDATE enquiries set response_message=?,status='answered',staff_id=?,date=now()";
		ResultResponse res = new ResultResponse();
		res.setResponse(jdbcTemplate.update(sql,reply,staff)+"");
		//Emailer.sendEmail("reply",staff, "Briefta");

		return res;
		
	}
	
	
	
	
	
	
	
	
	
	

	public List<Appointment> getAllUpcomingAppointments(String date) {
		String sql = "SELECT * FROM appointments where appointment_date>=? and status='accepted'";
		List<Appointment> g= jdbcTemplate.query(sql, new AppointmentMapper(),date);
		for(Appointment a:g){
			a.setStaff(staffRepository.getStaff(a.getStaffId()));
			a.setVisitor(visitor.getVisitorbyVisit(a.getVisitId(),false));
		}
		return g;
	}

	public List<Appointment> getStaffUpcomingAppointments(String staff,String date) {
		String sql = "SELECT * FROM appointments where staff_id=? and appointment_date>=? and status='accepted'";
		List<Appointment> g=  jdbcTemplate.query(sql, new AppointmentMapper(), staff,date);
		//looping to set visitor and staff details
		for(Appointment a:g){
			a.setStaff(staffRepository.getStaff(staff));
			a.setVisitor(visitor.getVisitorbyVisit(a.getVisitId(),false));
		}
		return g;
	}
	
	public List<Appointment> getStaffAppointments(String staff) {
		String sql = "SELECT * FROM appointments where staff_id=?";
		List<Appointment> g= jdbcTemplate.query(sql, new AppointmentMapper(), staff);
		for(Appointment a:g){
			a.setStaff(staffRepository.getStaff(staff));
			a.setVisitor(visitor.getVisitorbyVisit(a.getVisitId(),false));
		}
		return g;

	}

	public List<Appointment> getStaffUnAnsweredAppointments(String staff) {
		String sql = "SELECT * FROM appointments where staff_id=? and status is null";
		List<Appointment> g=  jdbcTemplate.query(sql, new AppointmentMapper(), staff);
		for(Appointment a:g){
			a.setStaff(staffRepository.getStaff(staff));
			a.setVisitor(visitor.getVisitorbyVisit(a.getVisitId(),false));
		}
		return g;
	}
	
	public List<Appointment> getAllUnAnsweredAppointments() {
		String sql = "SELECT * FROM appointments where status is null";
		List<Appointment> g= jdbcTemplate.query(sql, new AppointmentMapper());
		for(Appointment a:g){
			a.setStaff(staffRepository.getStaff(a.getStaffId()));
			a.setVisitor(visitor.getVisitorbyVisit(a.getVisitId(),false));
		}
		return g;
	}
}
