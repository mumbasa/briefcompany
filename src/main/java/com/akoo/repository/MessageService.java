package com.akoo.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kong.unirest.Unirest;

@Service
public class MessageService {

	@Value("${mnotify.key}")
	private String smsApiKey;

	@Value("${company.name}")
	private String companyName;

	String sendSms(String message, String contact) {
	
		return Unirest.get("https://apps.mnotify.net/smsapi").queryString("key", smsApiKey).queryString("to", contact)
				.queryString("msg", message).queryString("sender_id", "BrieftaVMS").asString().getBody();
	}
}
