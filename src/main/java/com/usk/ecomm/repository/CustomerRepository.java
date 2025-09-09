package com.usk.ecomm.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.usk.ecomm.entity.Customer;

public interface CustomerRepository extends MongoRepository<Customer, String>{


	boolean existsByEmail(String email);

	Customer findByEmailAndPassword(String email, String password);

}
