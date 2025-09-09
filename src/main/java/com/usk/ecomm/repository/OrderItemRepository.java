package com.usk.ecomm.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.usk.ecomm.entity.OrderItem;

public interface OrderItemRepository extends MongoRepository<OrderItem, String>{

}
