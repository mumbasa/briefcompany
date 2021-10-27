package com.akoo.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.adafy.services.MailContentBuilder;
import com.akoo.data.Appointment;

@Service
public class Emailer {

	@Autowired
    private JavaMailSender mailSender;
    
    private MailContentBuilder mailContentBuilder;

    @Autowired
    public Emailer(JavaMailSender mailSender, MailContentBuilder mailContentBuilder) {
        this.mailSender = mailSender;
        this.mailContentBuilder = mailContentBuilder;
    }
    
   public void sendEmail() {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("bry_lar@yahoo.com");
        msg.setSubject("Testing from Spring Boot");
        msg.setText("Hello World \n Spring Boot Email");
        mailSender.send(msg);

    }

    

    public void prepareAndSend(Appointment appointment) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
         //   messageHelper.setFrom("brieftavms@gmail.com");
         messageHelper.setFrom("brieftavms@gmail.com", "Briefta Technologies");
            System.out.println(appointment.getEmail());
            messageHelper.setTo(appointment.getEmail());
            messageHelper.setSubject("Appointment Decision");
            String content = mailContentBuilder.build(appointment);
            messageHelper.setText(content, true);
        };
        try {
            mailSender.send(messagePreparator);
        } catch (MailException e) {
        	e.printStackTrace();
            // runtime exception; compiler will not force you to handle it
        }
    }
}
