package com.distributed.transaction.message;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.distributed.transaction.domain.distributed.BankBUserHistoricalOperation;

public class BankBRecoverMessageContent extends MessageContent implements Serializable {

	private static final long serialVersionUID = 4124012958244590894L;

	private List<BankBUserHistoricalOperation> operationList;

	private Date benchmarkStartTime;

	private Date benchmarkEndTime;

	public List<BankBUserHistoricalOperation> getOperationList() {
		return operationList;
	}

	public void setOperationList(List<BankBUserHistoricalOperation> operationList) {
		this.operationList = operationList;
	}

	public Date getBenchmarkStartTime() {
		return benchmarkStartTime;
	}

	public void setBenchmarkStartTime(Date benchmarkStartTime) {
		this.benchmarkStartTime = benchmarkStartTime;
	}

	public Date getBenchmarkEndTime() {
		return benchmarkEndTime;
	}

	public void setBenchmarkEndTime(Date benchmarkEndTime) {
		this.benchmarkEndTime = benchmarkEndTime;
	}

}
