package com.usk.ecomm.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class OrderPayment {

	@Id
	private String orderPaymentId;
	private long orderId;
	private String paymentStatus;
	private String remarks;

	public String getOrderPaymentId() {
		return orderPaymentId;
	}

	public void setOrderPaymentId(String orderPaymentId) {
		this.orderPaymentId = orderPaymentId;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}
