package com.usk.ecomm.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.usk.ecomm.dto.TranscationResponse;
import com.usk.ecomm.entity.Transcation;

//@FeignClient(value = "bank-service", url="http://BANK-SERVICE/bank")
@FeignClient(name="http://localhost:8082/bank")
public interface BankFeignClient {

	@PostMapping("/transcation")
	public Transcation transcation(@RequestBody TranscationResponse transcation);
}
