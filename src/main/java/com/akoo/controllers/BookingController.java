package com.akoo.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akoo.data.Booking;
import com.akoo.data.Meeting;
import com.akoo.data.ResultResponse;
import com.akoo.data.StaffBooking;
import com.akoo.data.Venue;
import com.akoo.repository.BookingRespository;

@RestController
public class BookingController {
	@Autowired
	BookingRespository bookingRespository;

	@RequestMapping(value = "/api/get/venues")
	public ResponseEntity<List<Venue>> getVenues() {
		return new ResponseEntity<>(bookingRespository.getVenues(), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/get/staff/{id}/bookings/from/{date}")
	public ResponseEntity<List<Booking>> getBooking(@PathVariable(name = "id") String id,
			@PathVariable(name = "date") String date) {
		return new ResponseEntity<>(bookingRespository.getBookingMade(date, id), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/meeting/add/staff", method = RequestMethod.POST)
	public ResponseEntity<ResultResponse> addAttendants(@RequestParam("members") List<String> members,
			@RequestParam("bookid") String bookingId) {
		String[] trimmed = members.toString().replace("[", "").replace("]", "").split(",");
		ArrayList<String> list = new ArrayList<>();
		for (String a : trimmed) {
			list.add(a.trim());
		}

		return new ResponseEntity<>(bookingRespository.assignStaff(list, bookingId), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/get/staff/{id}/meetings/{date}")
	public ResponseEntity<List<Booking>> getMeetings(@PathVariable(name = "id") String id,
			@PathVariable(name = "date") String date) {
		return new ResponseEntity<>(bookingRespository.getUpcomingStaffMeetings(id, date), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/set/staff/{id}/meeting/{mid}/{status}")
	public ResponseEntity<ResultResponse> respondToMeeting(@PathVariable(name = "id") String id,
			@PathVariable(name = "status") String conf, @PathVariable(name = "mid") String mid) {
		return new ResponseEntity<>(bookingRespository.confirmMeetingAttendance(id, conf, mid), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/add/booking/", method = RequestMethod.POST)
	public ResponseEntity<ResultResponse> saveBooking(@RequestParam(name = "staff") String staff,
			@RequestParam(name = "venue") String venue, @RequestParam(name = "date") String date,
			@RequestParam(name = "from") String from, @RequestParam(name = "to") String to,
			@RequestParam(name = "agenda") String agenda, @RequestParam(name = "tostamp") long toTimeStamp,
			@RequestParam(name = "fromstamp") long fromStamp, @RequestParam(name = "dateAdded") long dateAdded) {
		Booking booking = new Booking();
		booking.setStaff(staff);
		booking.setStart(from);
		booking.setStop(to);
		booking.setDate(date);
		booking.setAgenda(agenda);
		booking.setStartLong(fromStamp);
		booking.setStopLong(toTimeStamp);
		booking.setVenue(venue);
		booking.setDatAdded(dateAdded);
		return new ResponseEntity<>(bookingRespository.addbooking(booking), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/get/meeting/{bookingid}/staff/")
	public ResponseEntity<List<StaffBooking>> getMeetingStaff(@PathVariable(name = "bookingid") String mid) {
		return new ResponseEntity<>(bookingRespository.getUpcomingMeetingStaff(mid), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/cancel/meeting/{bookingid}/")
	public ResponseEntity<ResultResponse> cancelMeeting(@PathVariable(name = "bookingid") String mid) {
		return new ResponseEntity<>(bookingRespository.deleteBooking(mid), HttpStatus.OK);
	}

	@RequestMapping(value="/api/get/meeting/{date}/{time}/{status}")
	public ResponseEntity<Meeting> getMeetingInfo(@PathVariable(name="time") String time,@PathVariable(name="date") String date,@PathVariable(name="status") String status){
		return new ResponseEntity<>(bookingRespository.getMeetingTime(date, time, status),HttpStatus.OK);
	}

	@RequestMapping(value="/api/get/meeting/{bookingid}/")
	public ResponseEntity<Meeting> getDateMeetingInfo(@PathVariable(name="bookingid") String id){
		return new ResponseEntity<>(bookingRespository.getMeetingInformation(id),HttpStatus.OK);
	}

	
	@RequestMapping(value="/api/get/meeting/on/{date}")
	public ResponseEntity<List<Meeting>> getDateMeetingInfoDate(@PathVariable(name="date") String date){
		return new ResponseEntity<>(bookingRespository.getMeetingInformationByDate(date),HttpStatus.OK);
	}
	

	@RequestMapping(value="/api/get/meeting/session/{date}/{time}")
	public ResponseEntity<Meeting> getMeetingTimeInSeesion(@PathVariable(name="date") String date,@PathVariable(name="time") String time){
		return new ResponseEntity<>(bookingRespository.getMeetingTimeInSeesion(date,time),HttpStatus.OK);
	}
	
	@RequestMapping(value="/api/get/meetings/dates/{year}")
	public ResponseEntity<List<String>> getDateMeetingoDates(@PathVariable(name="year") String year){
		return new ResponseEntity<>(bookingRespository.getMeetingDates(year),HttpStatus.OK);
	}
}
