package com.distributed.transaction.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.distributed.transaction.domain.distributed.BankBUserHistoricalOperation;

public interface BankBUserHistOperationService extends ShardingDomainKeyGenerator {

	public BankBUserHistoricalOperation logOneOperation(BankBUserHistoricalOperation operation);

	public BankBUserHistoricalOperation findById(BigInteger id);

	public BankBUserHistoricalOperation findByCorrelativeOperationId(BigInteger correlativeOperationId);

	public List<BankBUserHistoricalOperation> findAllLaterThanDate(Date date);

	BankBUserHistoricalOperation logOneOperation(String userName, BigDecimal amount, BigInteger correlativeOperationId);
}
