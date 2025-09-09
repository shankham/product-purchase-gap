package com.usk.ecomm.repository;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.usk.ecomm.entity.Transcation;

public interface TranscationRepository extends MongoRepository<Transcation, String>{

}
