package com.akoo.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.akoo.data.KeyValues;


@Repository
public class TagRepository {

	@Autowired
	RedisTemplate<String, Integer> template;
	
	@Autowired
	@Value("${company.name}")
	String companyName;

	
	public long saveTag(int tagNumber) {
		template.opsForSet().add(companyName+":tags", tagNumber);
		return	template.opsForSet().add(companyName+":tagpool", tagNumber);
		
	}
	
	
	public long getTagsRemaining() {
		return template.opsForSet().size(companyName+":tagpool");
		
		
	}
	
	public long collectTag() {
		return template.opsForSet().pop(companyName+":tagpool");
		
		
	}
	public long giveTag(int tag) {
		return template.opsForSet().remove(companyName+":tagpool",tag);
		
		
	}
	
	public long returnTag(int tag) {
		return template.opsForSet().add(companyName+":tagpool",tag);
		
		
	}
	public void delete(int tagNumber) {
		template.opsForSet().remove(companyName+":tags", tagNumber);
		template.opsForSet().remove(companyName+":tagpool", tagNumber);
		
	}
	
	public long found(int tagNumber) {
		return	template.opsForSet().add(companyName+":tagpool", tagNumber);
		
	}
	
	public List<KeyValues> getTags() {
		List<KeyValues> tagData = new ArrayList<KeyValues>();
		Set<Integer> tags=	template.opsForSet().members(companyName+":tags");
		Set<Integer>  pool = template.opsForSet().members(companyName+":tagpool");
		for(int a:tags) {
			KeyValues v = new KeyValues();
			v.setKey(String.valueOf(pool.contains(a)==true?1:0));
			v.setValue(a);
			tagData.add(v);
		}
		return tagData;
	}
}
