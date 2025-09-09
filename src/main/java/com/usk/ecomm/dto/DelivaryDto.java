package com.usk.ecomm.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usk.ecomm.entity.Order;
import com.usk.ecomm.entity.OrderItem;

public class DelivaryDto {

	private String order;

	public DelivaryDto(String order) {
		this.order = order;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "DelivaryDto(" + order + ")";
	}
}
