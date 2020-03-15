package com.akoo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akoo.data.Department;
import com.akoo.data.Memo;
import com.akoo.data.ResultResponse;
import com.akoo.data.Staff;
import com.akoo.data.Summary;
import com.akoo.repository.MessageRepository;
import com.akoo.repository.StaffRepository;

@RestController
public class StaffController {

	@Autowired
	StaffRepository staffRepository;
	
	@Autowired
	MessageRepository mess;

	@RequestMapping(value = "/api/add/staff", method = RequestMethod.POST)
	public ResponseEntity<ResultResponse> addStaff(@RequestParam(value = "id") String id, @RequestParam(value = "fname") String fname,
			@RequestParam(value = "mname") String mname, @RequestParam(value = "lname") String lname,
			 @RequestParam(value = "dept") String dept) {
		Staff staff = new Staff();
		staff.setId(id);
		staff.setDept(dept);
		staff.setFirstname(fname);
		staff.setLastName(lname);
		staff.setMiddleName(mname);
			return new ResponseEntity<ResultResponse>(staffRepository.saveStaff(staff),HttpStatus.OK);

	}
	
	
	@RequestMapping(value = "/api/add/dept", method = RequestMethod.POST)
	public ResponseEntity<ResultResponse> addDept(@RequestParam(value = "dept") String dept) {
	
			return new ResponseEntity<ResultResponse>(staffRepository.saveDept(dept),HttpStatus.OK);

	}
	
	@RequestMapping(value="/api/change/user/password", method=RequestMethod.POST)
	public ResponseEntity<Integer> changeStaffPassword(@RequestParam(value="id") String id,@RequestParam(value="pass") String pass){
		
		return new ResponseEntity<Integer>(staffRepository.changeStaffPassword(id, pass),HttpStatus.OK);
	}
	
	@RequestMapping("/api/get/all/staff")
	public ResponseEntity<List<Staff>> getStaff(){
		
		return new ResponseEntity<List<Staff>>(staffRepository.getAllStaff(),HttpStatus.OK);
	}
	
	@RequestMapping("/api/init")
	public ResponseEntity<ResultResponse> init(){
		ResultResponse f = new ResultResponse();
		f.setResponse("ok");
		
		return new ResponseEntity<ResultResponse>(f,HttpStatus.OK);
	}
	
	
	@RequestMapping("/api/initd")
	public ResponseEntity<String> inist(){
		
		return new ResponseEntity<String>("Meeting to Attend Asesewas Marketing",HttpStatus.OK);
	}
	

	@RequestMapping("/api/get/all/departments")
	public ResponseEntity<List<Department>> getDepartment(){
		
		return new ResponseEntity<List<Department>>(staffRepository.getAllDepartment(),HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/api/delete/staff", method = RequestMethod.POST)
	public ResponseEntity<Integer> deleteStaff(@RequestParam(value = "id") String id) {

		return new ResponseEntity<Integer>(staffRepository.deleteStaff(id), HttpStatus.OK);
	}
	
	@RequestMapping("/api/get/staff/{id}")
	public ResponseEntity<Staff> getStaff(@PathVariable(value="id") String id){
		
		return new ResponseEntity<Staff>(staffRepository.getStaff(id),HttpStatus.OK);
	}
	
	
	@RequestMapping("/api/get/staff/details/{id}/{pass}")
	public ResponseEntity<Staff> getStaffss(@PathVariable(value="id") String id,@PathVariable(value="pass") String pass){
		
		return new ResponseEntity<Staff>(staffRepository.getStaffAuth(id,pass),HttpStatus.OK);
	}
	@RequestMapping("/api/update/staff/{id}")
	public ResponseEntity<ResultResponse> updateStaff(@PathVariable(name="id") String id,@RequestParam(value="dept") String dept,@RequestParam(value="lname") String lname,@RequestParam(value="fname") String fname){
		
		return new ResponseEntity<ResultResponse>(staffRepository.changeInfo(id, lname, fname,dept),HttpStatus.OK);
	}
	
	@RequestMapping("/api/get/staff/{id}/visit/today")
	public ResponseEntity<Summary> getStaffTodayVisits(@PathVariable(value="id") String id){
		
		return new ResponseEntity<Summary>(staffRepository.getStaffVisitorsTodaySummary(id),HttpStatus.OK);
	}
	
	@RequestMapping("/api/get/staff/{id}/summary/{date}")
	public ResponseEntity<Summary> getStaffTodaySummary(@PathVariable(value="id") String id,@PathVariable(value="date") String date){
		
		return new ResponseEntity<Summary>(staffRepository.getStaffVisitorsTodaySummary2(id,date),HttpStatus.OK);
	}
	
	@RequestMapping("/api/get/staff/{id}/summary/today")
	public ResponseEntity<Summary> getStaffTodaySummary2(@PathVariable(value="id") String id){
		
		return new ResponseEntity<Summary>(staffRepository.getStaffVisitorsTodaySummary(id),HttpStatus.OK);
	}
	
	
	
	@RequestMapping("/api/get/all/memos")
	public ResponseEntity<List<Memo>> getAllMemo(){
		
		return new ResponseEntity<List<Memo>>(staffRepository.getMemos(),HttpStatus.OK);
	}
	
	@RequestMapping("/api/get/add/memos")
	public ResponseEntity<ResultResponse> saveMemo(@RequestParam(value="title") String title,@RequestParam(value="content") String content,@RequestParam(value="deadline") String deadline,@RequestParam(value="staff") String staff){
	Memo memo = new Memo();
	memo.setContent(content);
	memo.setTitle(title);
	memo.setDeadline(deadline);
	memo.setStaff(staff);
		
		return new ResponseEntity<ResultResponse>(staffRepository.addMemo(memo),HttpStatus.OK);
	}
	
	@RequestMapping("/api/get/memos/upcoming")
	public ResponseEntity<List<Memo>> getUpcoming(){
		
		return new ResponseEntity<List<Memo>>(staffRepository.getUpcomingMemos(),HttpStatus.OK);
	}
	

	
	
}
