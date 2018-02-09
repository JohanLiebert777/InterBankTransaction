package com.distributed.transaction.request.merger;

import java.io.Serializable;
import java.math.BigDecimal;

import com.distributed.transaction.domain.distributed.BankAUserHistoricalOperation;

public class BalanceChangeRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7113568749326983171L;

	private String userName;

	private BigDecimal value;

	private BankAUserHistoricalOperation operationHist;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public BankAUserHistoricalOperation getOperationHist() {
		return operationHist;
	}

	public void setOperationHist(BankAUserHistoricalOperation operationHist) {
		this.operationHist = operationHist;
	}

}
