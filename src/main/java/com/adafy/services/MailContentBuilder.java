package com.adafy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.akoo.data.Appointment;

@Service
public class MailContentBuilder {
	@Autowired
    private TemplateEngine templateEngine;
	
	@Value("${company.name}")
	String company;	
	
    @Autowired
    public MailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }
 
    public String build(Appointment appointment) {
        Context context = new Context();
        context.setVariable("message", appointment.getMessage());
        context.setVariable("staffName", appointment.getStaff().getFirstname()+" "+appointment.getStaff().getLastName());
        context.setVariable("time", appointment.getAppointmentTime());
        context.setVariable("date", appointment.getAppointmentDate());
        context.setVariable("decision", appointment.getStatus());
        context.setVariable("visitor", appointment.getVisitor().getName());
        context.setVariable("company", company);
        return templateEngine.process("/admin/ping", context);
    }

}
