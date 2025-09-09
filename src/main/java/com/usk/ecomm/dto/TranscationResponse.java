package com.usk.ecomm.dto;

import java.time.LocalDate;

public class TranscationResponse {
	
	private long fromAccount;
	private long toAccount;
	private double amount;
	private String status;
	private String remarks;
	private LocalDate transcationDate;
	public long getFromAccount() {
		return fromAccount;
	}
	public void setFromAccount(long fromAccount) {
		this.fromAccount = fromAccount;
	}
	public long getToAccount() {
		return toAccount;
	}
	public void setToAccount(long toAccount) {
		this.toAccount = toAccount;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public LocalDate getTranscationDate() {
		return transcationDate;
	}
	public void setTranscationDate(LocalDate transcationDate) {
		this.transcationDate = transcationDate;
	}
	
	
	

}
