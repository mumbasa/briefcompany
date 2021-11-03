package com.akoo.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.akoo.data.StafMessage;
import com.akoo.data.Visit;

import kong.unirest.Unirest;

@Service
public class MessageRepository {

	@Autowired
	Environment env;

	String data;;
	private RedisTemplate<String, String> redisTemplate;
	HashMapper<Object, byte[], byte[]> mapper = new ObjectHashMapper();
	
	@Autowired
	JmsTemplate jmsTemplate;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Value("${mnotify.key}")
	private String smsApiKey;
	
	@Value("${company.name}")
	private String companyName;
	
	@Autowired
	public MessageRepository(RedisTemplate<String, String> redisTemplate) {
		// TODO Auto-generated constructor stub
		this.redisTemplate = redisTemplate;

	}

/*	public void sendEmail() {
		try {
			Email email = DefaultEmail.builder().from(new InternetAddress("droid9user@gmail.com", "Bryan"))
					.to(Lists.newArrayList(new InternetAddress("bry_lar@yahoo.com", "bryan Laryea")))
					.subject("Fafa Engament").body("Yurn u youist").encoding("UTF-8").build();
		//	emailService.send(email);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
	}*/

	public String sendSms(String message,String contact) {
		System.err.println("sendt");
	return 	Unirest.get("https://apps.mnotify.net/smsapi").
			queryString("key", smsApiKey).
			queryString("to", contact).
			queryString("msg",message ).
			queryString("sender_id", "BrieftaTech")
			.asString().getBody();
	}

	
	
	public static void main(String[] args) {
	String k =	Unirest.get("https://apps.mnotify.net/smsapi").
		queryString("key", "dYxNxwUHdPDSYlCgd4cTxJJap").
		queryString("to", "0267600606").
		queryString("msg","message" ).
		queryString("sender_id", "BrieftaTech")
		.asString().getBody();
	System.err.println(k);
	}

	public List<StafMessage> getReceptionistMessages() {
		List<StafMessage> messages= new ArrayList<StafMessage>();
		ArrayList<String> data = (ArrayList<String>) redisTemplate.opsForList().range(companyName+":reception:messages", 0, -1);
		for(String a : data) {
			String[] dataSplit = a.split(";");
			StafMessage msg = new StafMessage();
			msg.setMessage(dataSplit[2]);
			msg.setSender(dataSplit[0]);
			msg.setTitle(dataSplit[1]);
			msg.setTimeStamp(Long.parseLong(dataSplit[3]));
			messages.add(msg);
		}
		return messages;
	}
	
	public List<StafMessage> getReceptionistMessages(int number) {
		List<StafMessage> messages= new ArrayList<StafMessage>();
		ArrayList<String> data = (ArrayList<String>) redisTemplate.opsForList().range(companyName+":reception:messages", 0, number);
		for(String a : data) {
			String[] dataSplit = a.split(";");
			StafMessage msg = new StafMessage();
			msg.setMessage(dataSplit[2]);
			msg.setSender(dataSplit[0]);
			msg.setTitle(dataSplit[1]);
			msg.setTimeStamp(Long.parseLong(dataSplit[3]));
			messages.add(msg);
		}
		return messages;
	}


	
	public List<StafMessage> getStaffMessages(String  id) {
		List<StafMessage> messages= new ArrayList<StafMessage>();
		String sql ="SELECT * FROM messages where staffid=? order by id desc";
		SqlRowSet set = jdbcTemplate.queryForRowSet(sql,id);
		while (set.next()) {
			
			StafMessage staffMessage = new StafMessage();
			staffMessage.setId(set.getLong(1));
			staffMessage.setMessage(set.getString(2));
			staffMessage.setSender(set.getString(3));
			staffMessage.setDateTime(set.getString(5));
			messages.add(staffMessage);
		}
		return messages;
	}

	public int sendNotfiyStaff(String  id,String message) {
	String sql ="INSERT INTO `messages`(`message`,`from`,`staffid`,`date`) VALUES(?,'Receptionist',?,curdate())";
	jmsTemplate.convertAndSend(id, message);
	return jdbcTemplate.update(sql, message,id);
	
	}

	
	
	

	public void sentAlert(Visit visitor) {
		
		redisTemplate.convertAndSend("alert:"+env.getProperty("company")+":" + visitor.getStaffId(), "You have a visitor");
 	}
	
	public void sendFrontDeskMessage(String message) {
		
		jmsTemplate.convertAndSend(companyName+":reception", message);
		redisTemplate.opsForSet().add("receptions", companyName+":reception:messages");
		redisTemplate.opsForList().rightPush(companyName+":reception:messages", message);
 	}
	
	
	
	public void sentAlert(String staff,String message) {
		
		jmsTemplate.convertAndSend(staff, message);
 	}
	
	
	public void sentAlerts(String staff,String message) {
		jmsTemplate.convertAndSend(staff, message);
		//, "You have a visitor");

	}

	public int sendReceptionMsg(String message) {
		redisTemplate.opsForZSet().add("messages:"+env.getProperty("company"), message, (double) System.currentTimeMillis());
		redisTemplate.convertAndSend("reception:"+env.getProperty("company"), message);
		return 1;
		
	}

	public void visitorAction(String staffId, String visitor, String message) {

	}

}
