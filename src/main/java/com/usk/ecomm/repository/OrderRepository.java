package com.usk.ecomm.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.usk.ecomm.entity.Order;

public interface OrderRepository extends MongoRepository<Order, String> {

	List<Order> findByCustomerCustomerId( String customerId);

	List<Order> findByCustomerCustomerIdAndOrderDateAfter(String customerId, LocalDateTime sixMothnsAgo);
	
}
