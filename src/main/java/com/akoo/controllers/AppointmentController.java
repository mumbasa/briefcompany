package com.akoo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akoo.data.Appointment;
import com.akoo.data.ResultResponse;
import com.akoo.repository.AppointmentRepository;

@RestController
public class AppointmentController {

	@Autowired
	AppointmentRepository appointment;

	@RequestMapping(value ="/api/add/appointment",method = RequestMethod.POST)
	public ResultResponse insertAppontment(@RequestParam(value="id")String id,
			@RequestParam(value="reason")String reason,@RequestParam(value="staff")String staff,@RequestParam(value="email")String email){
		return appointment.bookAppointment(id, reason, email, staff);
	}
	
	@RequestMapping(value ="/api/new/appointment",method = RequestMethod.POST)
	public ResultResponse insertAppontmentReception(@RequestParam(value="contact")String contact,@RequestParam(value="name")String name,@RequestParam(value="company")String company,
			@RequestParam(value="reason")String reason,@RequestParam(value="staff")String staff,@RequestParam(value="email")String email,@RequestParam(value="title")String title){
		return appointment.bookAppointment(contact,name,company,reason, email, staff,title);
	}
	
	
	@RequestMapping(value ="/api/get/staff/{id}/appointment",method = RequestMethod.GET)
	public List<Appointment> getStaffAppontments(@PathVariable(value="id")String id){
		return appointment.getStaffAppointments(id);
	}
	
	@RequestMapping(value ="/api/get/staff/{id}/appointment/{date}",method = RequestMethod.GET)
	public List<Appointment> getStaffAppontmentsPending(@PathVariable(value="id")String id,@PathVariable(value="date")String date){
		return appointment.getStaffUpcomingAppointments(id,date);
	}
	
	@RequestMapping(value ="/api/get/staff/{id}/appointment/today",method = RequestMethod.GET)
	public List<Appointment> getStaffAppontmentsPending(@PathVariable(value="id")String id){
		return appointment.getStaffTodayAppointments(id);
	}
	
	@RequestMapping(value ="/api/get/staff/{id}/appointment/unanswered",method = RequestMethod.GET)
	public List<Appointment> getStaffAppontmentsUnswered(@PathVariable(value="id")String id){
		return appointment.getStaffUnAnsweredAppointments(id);
	}
	
	
	@RequestMapping(value ="/api/get/appointment/unanswered",method = RequestMethod.GET)
	public List<Appointment> getAllAppontmentsUnswered(){
		return appointment.getAllUnAnsweredAppointments();
	}
	
	
	@RequestMapping(value ="/api/staff/appointment/response",method = RequestMethod.POST)
	public ResultResponse respondAppointment(@RequestParam(value="answer")String answer,@RequestParam(value="appid")String appid,
			@RequestParam(value="appointmentDate")String date,
			@RequestParam(value="time")String time,
			@RequestParam(value="timeStamp")long timestamp){
		return appointment.replyAppointment(appid, answer, date, time, timestamp);
	}
	
	
	@RequestMapping(value ="/api/staff/appointment/{appid}/cancel",method = RequestMethod.POST)
	public ResultResponse cancelAppointment(@PathVariable(value="appid")String appid){
		return appointment.cancelAppointment(appid);
	}
	
	@RequestMapping(value ="/api/get/appointments/upcoming/{date}",method = RequestMethod.GET)
	public List<Appointment>  getAppointments(@PathVariable("date")String date){
		return appointment.getAllUpcomingAppointments (date);
	}
}
