package com.adafy.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class MessageCleaner {
	@Autowired
	RedisTemplate<String, String> template;
	
	@Scheduled(cron = "0 0 0 * * *")
	public void cleanDatabase() {
		System.err.println("Deleting data");
		template.delete("reception:messages");
	}
}
