package com.usk.ecomm.service;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Service;

import com.usk.ecomm.dto.DelivaryDto;
import com.usk.ecomm.dto.KafkaJsonSerializer;

@Service
public class KafkaObjectService {

	@SuppressWarnings(value = { "rawtypes", "unchecked", "resource" })
	public void send(DelivaryDto delivaryDto) {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		Producer<String, DelivaryDto> kafkaProducer = new KafkaProducer<>(props, new StringSerializer(),
				new KafkaJsonSerializer());
		// Send a message
		kafkaProducer.send(new ProducerRecord<String, DelivaryDto>("delivary_service", delivaryDto));
	}
}
