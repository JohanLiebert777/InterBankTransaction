package com.distributed.transaction.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.distributed.transaction.cache.service.BankBAccountCacheService;
import com.distributed.transaction.domain.distributed.BankBAccount;
import com.distributed.transaction.domain.distributed.BankBUserHistoricalOperation;
import com.distributed.transaction.message.MessageType.OperationType;
import com.distributed.transaction.repository.distributed.BankBUserHistoricalOperationRepo;
import com.distributed.transaction.service.BankBUserHistOperationService;

@Service
public class BankBUserHistOperationServiceImpl implements BankBUserHistOperationService {

	@Autowired
	private BankBUserHistoricalOperationRepo repo;

	@Autowired
	private RedissonClient redis;
	@Autowired
	private BankBAccountCacheService bankBAccountCacheService;

	@Override
	@Transactional(transactionManager = "transactionManagerSecondary", propagation = Propagation.REQUIRES_NEW)
	public BankBUserHistoricalOperation logOneOperation(String userName, BigDecimal amount,
			BigInteger correlativeOperationId) {
		BankBAccount account = bankBAccountCacheService.findByUserName(userName).get(0);
		BankBUserHistoricalOperation operation = new BankBUserHistoricalOperation();
		operation.setOperationTypeId(OperationType.TRANSFER_MONEY.getOpeartionId());
		operation.setUserId(account.getUserId());
		operation.setAmount(amount);
		operation.setReason("Move " + amount + " from Bank A to Bank B");
		operation.setCreatedBy(userName);
		operation.setCreatedDate(new Date());
		operation.setCorrelativeOperationId(correlativeOperationId);
		return logOneOperation(operation);
	}

	@Override
	@Transactional(transactionManager = "transactionManagerSecondary", propagation = Propagation.REQUIRES_NEW)
	public BankBUserHistoricalOperation logOneOperation(BankBUserHistoricalOperation operation) {
		if (operation.getOperationId() == null) {
			RAtomicLong pkSeq = redis.getAtomicLong("bankB:user:hist:operation:id");
			long pkLong = pkSeq.addAndGet(1);
			String pkStr = new BigInteger(generateTimeStamp()).add(new BigInteger(String.valueOf(operation.hashCode())))
					.toString() + pkLong;
			operation.setOperationId(new BigInteger(pkStr));
		}
		return repo.save(operation);
	}

	@Override
	public BankBUserHistoricalOperation findById(BigInteger id) {
		return repo.findOne(id);
	}

	@Override
	public BankBUserHistoricalOperation findByCorrelativeOperationId(BigInteger correlativeOperationId) {
		return repo.findByCorrelativeOperationId(correlativeOperationId);
	}

	@Override
	public List<BankBUserHistoricalOperation> findAllLaterThanDate(Date date) {
		return repo.findAllLaterThanDate(date);
	}

}
