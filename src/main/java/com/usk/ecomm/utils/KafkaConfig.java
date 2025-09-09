package com.usk.ecomm.utils;


import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.support.converter.JsonMessageConverter;

@Configuration
public class KafkaConfig {

    @Bean
    public JsonMessageConverter jsonMessageConverter() {

        return new JsonMessageConverter();

    }

}

