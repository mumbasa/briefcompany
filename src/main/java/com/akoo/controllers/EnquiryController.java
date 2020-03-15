package com.akoo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akoo.data.Enquiry;
import com.akoo.data.ResultResponse;
import com.akoo.repository.EnquiryRepository;

@RestController
public class EnquiryController {

	@Autowired
	EnquiryRepository enquiryRep;

	@Autowired
	Environment env;
	@RequestMapping(value="/api/add/enquiry",method =RequestMethod.POST)
	public ResultResponse saveEnquiry(@RequestParam(value="enquiry") String enquiry,@RequestParam(value="email") String email,@RequestParam(value="visitid") String visitId,@RequestParam(value="dept") String dept){
		return enquiryRep.addEnquiry(visitId, enquiry, email, dept);
		
	}
	
	@RequestMapping(value="/api/get/all/enquiry",method =RequestMethod.GET)
	public List<Enquiry> getAll(){
		return enquiryRep.getEnqurries();
				
	}
	
	
	@RequestMapping(value="/api/get/dept/{id}/enquiry",method =RequestMethod.GET)
	public List<Enquiry> getAllDeptEnquiry(@PathVariable(value="id") String deptId){
		return enquiryRep.getEnquriesByDept(deptId);
				
	}
	
	@RequestMapping(value="/api/get/dept/{id}/enquiry/unanswered",method =RequestMethod.GET)
	public List<Enquiry> getAllDeptEnquiryUnanswered(@PathVariable(value="id") String deptId){
		return enquiryRep.getUannsweredByDept(deptId);
				
	}
	
	
	@RequestMapping(value="/api/get/all/enquiry/umanswered",method =RequestMethod.GET)
	public List<Enquiry> getallUnanswered(){
		return enquiryRep.getUanswered();
				
	}
	
	@RequestMapping(value="/api/get/company",method =RequestMethod.GET)
	public String ges(){
		return env.getProperty("company");
				
	}
}
