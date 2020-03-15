package com.akoo.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.akoo.data.Booking;
import com.akoo.data.BookingMapper;
import com.akoo.data.Meeting;
import com.akoo.data.MeetingsWrapper;
import com.akoo.data.ResultResponse;
import com.akoo.data.Staff;
import com.akoo.data.StaffBooking;
import com.akoo.data.StaffBookingMapper;
import com.akoo.data.StaffRowMapping;
import com.akoo.data.Venue;
import com.akoo.data.VenueMapper;

@Repository
public class BookingRespository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public ResultResponse addbooking(Booking book) {
		String sql = "INSERT INTO `venue_booking` (`venue`, `staff_id`, `starttime`, `stoptime`, `date`, `tostamp`, `fromstamp`,date_added,agenda) VALUES (?,?,?,?,?,?,?,?,?)";
		int row = jdbcTemplate.update(sql, book.getVenue(), book.getStaff(), book.getStart(), book.getStop(),
				book.getDate(), book.getStopLong(), book.getStartLong(), book.getDatAdded(), book.getAgenda());
		ResultResponse res = new ResultResponse();
		res.setResponse(row + "");
		return res;
	}

	public ResultResponse updateBooking(Booking book) {
		String sql = "UPDATE venue_booking set `venue`=?, `staff`=?, `starttime`=?, `stoptime`=?, `date`=?, `tostamp`=?, `fromstamp`=? where id=?";
		int row = jdbcTemplate.update(sql, book.getVenue(), book.getStaff(), book.getStart(), book.getStop(),
				book.getDate(), book.getStopLong(), book.getStartLong());
		ResultResponse res = new ResultResponse();
		res.setResponse(row + "");
		return res;
	}

	public ResultResponse deleteBooking(String id) {
		String sql = "DELETE FROM venue_booking  where id=?";
		int row = jdbcTemplate.update(sql, id);
		deleteMeetingAttendants(id);
		ResultResponse res = new ResultResponse();
		res.setResponse(row + "");
		return res;
	}

	public ResultResponse deleteMeetingAttendants(String id) {
		String sql = "DELETE FROM staff_bookings  where booking_id=?";
		int row = jdbcTemplate.update(sql, id);
		ResultResponse res = new ResultResponse();
		res.setResponse(row + "");
		return res;
	}

	public ResultResponse confirmMeetingAttendance(String id, String response, String bookId) {
		String sql = "UPDATE staff_bookings set status=? where staff_id=? and booking_id=?";
		int row = jdbcTemplate.update(sql, response, id, bookId);
		ResultResponse res = new ResultResponse();
		res.setResponse(row + "");
		return res;
	}

	public ResultResponse assignStaff(List<String> data, String bookingId) {
		String sql = "INSERT INTO staff_bookings(booking_id,staff_id,status) VALUES(?,?,?)";
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
					arg0.setString(3, "Not Confirmed");
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

	public List<Booking> getUpcomingBooking(String date) {
		String sql = "SELECT id, (SELECT venue from venues as v where v.id=b.venue), staff_id, starttime, stoptime, date, tostamp, fromstamp, agenda, date_added,status FROM akoo.venue_booking as b where date>=?";
		return jdbcTemplate.query(sql, new BookingMapper(), date);

	}

	public List<Booking> getBookingMade(String date, String staff) {
		String sql = "SELECT id, (SELECT venue from venues as v where v.id=b.venue), staff_id, starttime, stoptime, date, tostamp, fromstamp, agenda, date_added,'' FROM akoo.venue_booking as b where date>=? and staff_id=?";
		return jdbcTemplate.query(sql, new BookingMapper(), date, staff);

	}

	/*
	 * public List<Booking> getUpcomingStaffMeetings(String id,String date) {
	 * String sql =
	 * "SELECT id, (SELECT venue from venues as v where v.id=b.venue), staff_id, starttime, stoptime, date, tostamp, fromstamp, agenda, date_added FROM akoo.venue_booking as b where date>=? and b.id IN (SELECT booking_id from staff_bookings where staff_id=?)"
	 * ; return jdbcTemplate.query(sql, new BookingMapper(),date, id);
	 * 
	 * }
	 */

	public List<Booking> getUpcomingStaffMeetings(String id, String date) {
		String sql = "SELECT b.id, (SELECT venue from venues as v where v.id=b.venue), s.staff_id, starttime, stoptime, date, tostamp, fromstamp, agenda, date_added,status FROM akoo.venue_booking as b join staff_bookings as s on s.booking_id=b.id where date>=?and s.staff_id=? ";
		return jdbcTemplate.query(sql, new BookingMapper(), date, id);

	}

	public List<Staff> getUpcomingBookingStaff(String id) {
		String sql = "SELECT * FROM staff as s  where s.id IN (SELECT staff_id from staff_bookings where booking_id=?);";
		return jdbcTemplate.query(sql, new StaffRowMapping(), id);

	}

	public List<Staff> getUpcomingMeetingStaff(String id, String status) {
		String sql = "SELECT * FROM staff as s  where s.id IN (SELECT staff_id from staff_bookings where booking_id=? and status=?);";
		return jdbcTemplate.query(sql, new StaffRowMapping(), id, status);

	}

	public List<StaffBooking> getUpcomingMeetingStaff(String id) {
		String sql = "SELECT staff_id,(SELECT concat(firstname,' ',lastname) from staff as s where s.id=b.staff_id),booking_id,status from staff_bookings as b where booking_id=?;";
		return jdbcTemplate.query(sql, new StaffBookingMapper(), id);

	}

	public List<Booking> getUpcomingBookingRamge(String from, String to) {
		String sql = "SELECT id, (SELECT venue from venues as v where v.id=b.venue), staff_id, starttime, stoptime, date, tostamp, fromstamp, agenda, date_added FROM akoo.venue_booking as b where date>=? and date<=?";
		return jdbcTemplate.query(sql, new BookingMapper(), from, to);

	}

	public Meeting getMeetingInformation( String id) {
		String sql = "SELECT id,agenda,date,starttime,stoptime,(SELECT venue from venues as v where v.id=vb.venue) FROM akoo.venue_booking as vb  where vb.id=?;";
		Meeting meeting = jdbcTemplate.queryForObject(sql, new MeetingsWrapper(), id);
		meeting.setAttendants(getUpcomingBookingStaff("" + meeting.getId()));
		return meeting;

	}

	public List<Meeting> getMeetingInformationByDate(String date) {
		String sql = "SELECT id,agenda,date,starttime,stoptime,(SELECT venue from venues as v where v.id=vb.venue) FROM akoo.venue_booking as vb  where vb.date=?;";
		List<Meeting> meetings = jdbcTemplate.query(sql, new MeetingsWrapper(), date);
		for(Meeting m :meetings){
		m.setAttendants(getUpcomingBookingStaff("" + m.getId()));
		}
		return meetings;

	}
	
	public Meeting getMeetingInformation(String date, String time, String status,String id) {
		String sql = "SELECT id,agenda,date,starttime,stoptime,(SELECT venue from venues as v where v.id=vb.venue) FROM akoo.venue_booking as vb where date=? and starttime=? and vb.id=?;";
		Meeting meeting = jdbcTemplate.queryForObject(sql, new MeetingsWrapper(), date, time,id);
		meeting.setAttendants(getUpcomingMeetingStaff("" + meeting.getId(), status));
		return meeting;

	}

	public Meeting getMeetingTime(String date, String time, String status) {
		String sql = "SELECT id,agenda,date,starttime,stoptime,(SELECT venue from venues as v where v.id=vb.venue) FROM akoo.venue_booking as vb where date=? and starttime=?;";
		Meeting meeting = jdbcTemplate.queryForObject(sql, new MeetingsWrapper(), date, time);
		meeting.setAttendants(getUpcomingMeetingStaff("" + meeting.getId(), status));
		return meeting;

	}
	
	public Meeting getMeetingTimeInSeesion(String date, String time) {
		String sql = "SELECT id,agenda,date,starttime,stoptime,(SELECT venue from venues as v where v.id=vb.venue) FROM akoo.venue_booking as vb where starttime<=? and stoptime>=? and date=? LIMIT 1";
		Meeting meeting = jdbcTemplate.queryForObject(sql, new MeetingsWrapper(), date, time);
		meeting.setAttendants(getUpcomingMeetingStaff("" + meeting.getId(), "Accepted"));
		return meeting;

	}

	public List<String> getMeetingDates(String year) {
		String sql = "SELECT distinct(date) FROM akoo.venue_booking where year(date)=?;";
		return jdbcTemplate.queryForList(sql, String.class, year);

	}
	
	
	public List<Venue> getVenues() {
		String sql = "SELECT * FROM venues";
		return jdbcTemplate.query(sql, new VenueMapper());

	}

}
