package com.usk.ecomm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	
	String kafkaTopic = "bank_transcation";
	
	public void send(String fromAccountNum) {
	    
	    kafkaTemplate.send(kafkaTopic, fromAccountNum);
	}
	

}
