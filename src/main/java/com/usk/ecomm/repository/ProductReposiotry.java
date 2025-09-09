package com.usk.ecomm.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.usk.ecomm.entity.Product;

public interface ProductReposiotry extends MongoRepository<Product, String>{

	List<Product> findByProductNameContaining(String productName);

	Product findByProductId(String productId);

	List<Product> findByProductIdIn(List<String> productIds);

}
