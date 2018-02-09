package com.distributed.transaction.constant;

public enum Status {

	SUCCESS("Success"), FAILED("Failed");

	private String status;

	Status(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
