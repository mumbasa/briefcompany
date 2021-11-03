package com.akoo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akoo.data.StafMessage;
import com.akoo.data.Staff;
import com.akoo.repository.MessageRepository;
import com.akoo.repository.StaffRepository;
@RestController
public class MessageController {
	@Autowired
	MessageRepository messager;
	
	@Autowired
	StaffRepository staffRepository;
 
	
	@RequestMapping(value = "/api/send/message", method = RequestMethod.POST)
	public int sendMessage(@RequestParam(value = "message") String message, @RequestParam(value = "time") String time,
			@RequestParam(value = "staff") String staff, @RequestParam(value = "visitor") String visitor) {
return		messager.sendReceptionMsg(message);

	}
	 
	@RequestMapping(value = "/api/get/staff/{id}/messages", method = RequestMethod.GET)
	public List<StafMessage> getStaffMessages(@PathVariable("id") String id) {
	return messager.getStaffMessages(id);
	}

	
	@RequestMapping(value = "/api/send/frondesk/message", method = RequestMethod.GET)
	public int sendFrontDeskMessage(@RequestParam("id") String id,@RequestParam("title") String title,@RequestParam("message") String message) {
		Staff staff = staffRepository.getStaff(id);
		messager.sendFrontDeskMessage(staff.getName()+";"+title+";"+ message+";"+System.currentTimeMillis());
		return 1;
	}

	
	@RequestMapping(value = "/api/emailer/{id}/{message}", method = RequestMethod.GET)
	public void sendEmail(@PathVariable(value="id") String id,@PathVariable(value="message") String mess) {
		messager.sentAlert(id,mess);
		
	}

	@RequestMapping(value = "/api/emailer", method = RequestMethod.POST)
	public void sendEmail(@RequestParam(value = "from") String from, @RequestParam(value = "to") String to,
			@RequestParam(value = "subject") String subject, @RequestParam(value = "body") String body,
			@RequestParam(value = "name") String name) {
	//	messager.sendEmail(from, to, subject, body, name);
		
	}

}
