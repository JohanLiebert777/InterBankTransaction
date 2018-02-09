package com.distributed.transaction.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.distributed.transaction.cache.service.BankAAccountCacheService;
import com.distributed.transaction.domain.distributed.BankAAccount;
import com.distributed.transaction.domain.distributed.BankAUserHistoricalOperation;
import com.distributed.transaction.message.MessageType.OperationType;
import com.distributed.transaction.repository.distributed.BankAUserHistoricalOperationRepo;
import com.distributed.transaction.service.BankAUserHistOperationService;

@Service
public class BankAUserHistOpeartionServiceImpl implements BankAUserHistOperationService {

	@Autowired
	private BankAUserHistoricalOperationRepo repo;

	@Autowired
	private BankAAccountCacheService bankAAccontCacheService;

	@Override
	public BankAUserHistoricalOperation saveNewForTransferMoney(String userName, Double amount) {
		BankAAccount account = bankAAccontCacheService.findByUserName(userName).get(0);
		BankAUserHistoricalOperation operation = new BankAUserHistoricalOperation();
		operation.setOperationTypeId(OperationType.TRANSFER_MONEY.getOpeartionId());
		operation.setUserId(account.getUserId());
		operation.setAmount(new BigDecimal(amount).negate());
		operation.setReason("Move " + amount + " from Bank A to Bank B");
		operation.setStatus("Start");
		operation.setCreatedBy(account.getUserName());
		operation.setCreatedDate(new Date());
//		System.out.println(Thread.currentThread().getName()+"__________Before Save!__________");
		saveOrUpdate(operation);
//		System.out.println(Thread.currentThread().getName()+"__________After Save!__________");
		return operation;
	}

	@Override
	@Transactional(transactionManager = "transactionManagerSecondary", propagation = Propagation.REQUIRED)
	public void updateStatusForBankAUserHistoricalOperation(BigInteger operationId, String status) {
		repo.updateStatusForBankAUserHistoricalOperation(operationId, status);
	}

	@Autowired
	private RedissonClient redis;

	@Override
	public BankAUserHistoricalOperation saveOrUpdate(BankAUserHistoricalOperation operation) {
		if (Optional.ofNullable(operation).isPresent()
				&& !Optional.ofNullable(operation.getOperationId()).isPresent()) {
			RAtomicLong pkSeq = redis.getAtomicLong("bankA:user:hist:operation:id");
			long pkLong = pkSeq.addAndGet(1);
			String pkStr = new BigInteger(generateTimeStamp()).add(new BigInteger(String.valueOf(operation.hashCode())))
					.toString() + pkLong;
			operation.setOperationId(new BigInteger(pkStr));
		}
		return repo.save(operation);
	}

	@Override
	@Transactional(transactionManager = "transactionManagerSecondary", propagation = Propagation.REQUIRED)
	public BankAUserHistoricalOperation findById(BigInteger id) {
		return repo.findOne(id);
	}

	@Override
	public List<BankAUserHistoricalOperation> findAllBetweenStartDateAndEndDate(Date startDate, Date endDate) {
		return repo.findAllBetweenStartDateAndEndDate(startDate, endDate);
	}

}
