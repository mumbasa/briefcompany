package com.akoo.controllers;

import javax.jms.Queue;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ActiveController {
	@Autowired
    private Queue queue;

    @Autowired
    private JmsTemplate jmsTemplate;

    @GetMapping("/message/{message}")
    public ResponseEntity<String> publish(@PathVariable("message") final String message){
        jmsTemplate.convertAndSend(queue, message);
        
        return new ResponseEntity<String>(message, HttpStatus.OK);
    }
    @GetMapping("/message/sec/{message}")
    public ResponseEntity<String> publisher(@PathVariable("message") final String message){
    
        jmsTemplate.convertAndSend("reception", message);
        
        return new ResponseEntity<String>(message, HttpStatus.OK);
    }
    
    @GetMapping("/message/sect/{message}")
    public ResponseEntity<String> published(@PathVariable("message") final String message){
    	ActiveMQQueue l = new ActiveMQQueue(message);
    	
        return new ResponseEntity<String>(message, HttpStatus.OK);
    }
}
