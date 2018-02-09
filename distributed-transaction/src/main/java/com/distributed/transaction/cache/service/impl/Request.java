package com.distributed.transaction.cache.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;

public class Request implements Serializable {

	private static final long serialVersionUID = -82842831572289335L;

	private String name;

	private BigDecimal balance;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

}
