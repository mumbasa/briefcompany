package com.adafy.sys;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.jms.Queue;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.akoo.api.FileStorageProperties;

@SpringBootApplication
@ComponentScan("com.*")
@EnableScheduling
@EnableConfigurationProperties({
	FileStorageProperties.class
})

public class AdafyApplication {

	@Value("${file.upload-dir}")
	String UPLOAD_FOLDER;

	@Value("${spring.application.name}")
	String appName;
	
	public static void main(String[] args) {
		SpringApplication.run(AdafyApplication.class, args);
	}
	@Bean
    public Queue queue(){
        return new ActiveMQQueue("test-queue");
    }
	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
        final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }
	@PostConstruct
	public void creatFolder() {
	//	String folder =ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/download/").toString();
		File file = new File(UPLOAD_FOLDER+appName);
		if(!file.exists()) {
			System.out.println("Does not Exit. Creaitng folder");
			file.mkdir();
			
		}else {
			System.out.println("Does  Exit. ");
			
		}
		
	}
	
  
}
