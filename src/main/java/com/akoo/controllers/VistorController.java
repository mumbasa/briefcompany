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
import org.springframework.web.multipart.MultipartFile;

import com.akoo.data.LogoutVisitor;
import com.akoo.data.ResultResponse;
import com.akoo.data.Visitor;
import com.akoo.data.VisitorVisitSummary;
import com.akoo.repository.MessageRepository;
import com.akoo.repository.VisitorRepository;

@RestController
public class VistorController {
	@Autowired
	VisitorRepository visitorRepository;
	@Autowired
	MessageRepository m;

	// done

	@RequestMapping(value = "/api/add/visitor/visit", method = RequestMethod.POST)
	public long insertVisitorVisit(@RequestParam(value = "tel") String tel,@RequestParam(value = "company") String company,
			@RequestParam(value = "car") String car,@RequestParam(value = "name") String name, @RequestParam(value = "staff") String staffid,
			@RequestParam(value = "mission") String mission, @RequestParam(name = "file") String uploadfile) {
		
		return visitorRepository.addVisitFromTab(company, name, tel, car, mission, staffid, uploadfile);
		

	}

	@RequestMapping(value = "/api/add/visit", consumes = "multipart/form-data", method = RequestMethod.POST)
	public long insertVisitorVisitPc(@RequestParam(value = "contact") String visitor,
			@RequestParam(value = "car") String car, @RequestParam(value = "name") String name,
			@RequestParam(value = "staff") String staffid, @RequestParam(value = "mission") String mission,
			@RequestParam(value = "company") String company, @RequestParam(name = "file") MultipartFile uploadfile) {

		return visitorRepository.addVisit(company, name, visitor, car, mission, staffid, uploadfile);

	}

	@RequestMapping(value = "/api/add/visitor", method = RequestMethod.POST)
	public Visitor insertVisitor(@RequestParam(value = "name") String name, @RequestParam(value = "tel") String tel,
			@RequestParam(value = "com") String com) {
		Visitor vit = new Visitor();
		vit.setCompany(com);
		vit.setName(name);
		vit.setTelephone(tel);

		vit = visitorRepository.addVisitor(vit);
		return vit;

	}

	@RequestMapping(value = "/api/add/{visitid}/colleagues", method = RequestMethod.POST)
	public ResponseEntity<ResultResponse> insertColleagues(@PathVariable(value = "visitid") String id,
			@RequestParam(value = "names") List<String> names) {
		return new ResponseEntity<>(visitorRepository.addColleagues(names, id), HttpStatus.OK);

	}

	// done
	@RequestMapping("/api/visit/signout/{id}")
	public ResultResponse greeter(@PathVariable(name = "id") String id) {

		return visitorRepository.visitorSignOut(id);

	}

	@RequestMapping("/api/get/visit/in/")
	public List<LogoutVisitor> greeters() {

		return visitorRepository.getVisitsStillintoday();

	}

	@RequestMapping("/api/get/visits/notout")
	public ResponseEntity<List<VisitorVisitSummary>> visitorsIn() {
		return new ResponseEntity<List<VisitorVisitSummary>>(visitorRepository.getVisitsTodayNotSignedOut(),
				HttpStatus.OK);

	}

	@RequestMapping("/api/get/visits/{date}")
	public ResponseEntity<List<VisitorVisitSummary>> visitors(@PathVariable(name = "date") String date) {

		return new ResponseEntity<List<VisitorVisitSummary>>(visitorRepository.getVisitsByDate(date), HttpStatus.OK);

	}

	@RequestMapping("/api/get/visits/today")
	public ResponseEntity<List<VisitorVisitSummary>> visitorsToday() {

		return new ResponseEntity<List<VisitorVisitSummary>>(visitorRepository.getVisitsToday(), HttpStatus.OK);

	}

	@RequestMapping("/api/get/visitor/presence")
	public ResponseEntity<Boolean> visitorsToday(@RequestParam("id") String id) {

		return new ResponseEntity<Boolean>(visitorRepository.isVisitorStillIn(id), HttpStatus.OK);

	}
	
	// done
	@RequestMapping("/api/get/staff/{id}/visitors")
	public ResponseEntity<List<Visitor>> staffVisitors(@PathVariable(name = "id") String id) {
		return new ResponseEntity<List<Visitor>>(visitorRepository.getStaffVisitors(id), HttpStatus.OK);

	}

	// done
	@RequestMapping("/api/get/staff/{id}/visitors/today")
	public ResponseEntity<List<VisitorVisitSummary>> staffVisitorsToday(@PathVariable(name = "id") String id) {
		return new ResponseEntity<List<VisitorVisitSummary>>(visitorRepository.getStaffVisitsToday(id), HttpStatus.OK);

	}

	@RequestMapping("/api/get/staff/{id}/visitors/waiting")
	public ResponseEntity<Integer> staffVisitorsWaitingToday(@PathVariable(name = "id") String id) {
		return new ResponseEntity<Integer>(visitorRepository.gettStaffVisitorWaiting(id), HttpStatus.OK);

	}

	// done
	@RequestMapping("/api/get/visitor/{id}")
	public ResponseEntity<Visitor> getVisitor(@PathVariable(name = "id") String id) {
		return new ResponseEntity<Visitor>(visitorRepository.getVisitorByTelephoneNumber(id), HttpStatus.OK);

	}

	// done
	@RequestMapping("/api/get/staff/{id}/{date}/visitors")
	public ResponseEntity<List<VisitorVisitSummary>> staffVisitorsbyDate(@PathVariable(name = "id") String id,
			@PathVariable(name = "date") String date) {
		return new ResponseEntity<List<VisitorVisitSummary>>(visitorRepository.getStaffVisitorByDay(id, date),
				HttpStatus.OK);

	}

	// done
	@RequestMapping(value = "/api/staff/visitor/accept", method = RequestMethod.POST)
	public ResponseEntity<ResultResponse> staffRespond(@RequestParam(name = "id") String id) {

		return new ResponseEntity<ResultResponse>(visitorRepository.visitorAccept(id), HttpStatus.OK);

	}

	@RequestMapping(value = "/api/staff/visitor/reject", method = RequestMethod.POST)
	public ResponseEntity<ResultResponse> staffReject(@RequestParam(name = "id") String id) {
		return new ResponseEntity<ResultResponse>(visitorRepository.visitorReject(id), HttpStatus.OK);

	}



}
