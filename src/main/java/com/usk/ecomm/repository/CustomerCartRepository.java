package com.usk.ecomm.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.usk.ecomm.entity.CustomerCart;

@Repository
public interface CustomerCartRepository extends MongoRepository<CustomerCart, String> {

	List<CustomerCart> findByCustomerId(String customerId);

	
	void deleteByCustomerId(String customerId);

}
