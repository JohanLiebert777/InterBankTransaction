package com.distributed.transaction.service;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.distributed.transaction.domain.distributed.BankAUserHistoricalOperation;

public interface BankAUserHistOperationService extends ShardingDomainKeyGenerator {

	public BankAUserHistoricalOperation saveOrUpdate(BankAUserHistoricalOperation operation);

	public BankAUserHistoricalOperation findById(BigInteger id);

	public List<BankAUserHistoricalOperation> findAllBetweenStartDateAndEndDate(Date startDate, Date endDate);

	BankAUserHistoricalOperation saveNewForTransferMoney(String userName, Double amount);

	void updateStatusForBankAUserHistoricalOperation(BigInteger operationId, String status);

}
