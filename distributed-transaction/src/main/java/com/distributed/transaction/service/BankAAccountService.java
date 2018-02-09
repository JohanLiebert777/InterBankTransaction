package com.distributed.transaction.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.distributed.transaction.domain.distributed.BankAAccount;

public interface BankAAccountService extends ShardingDomainKeyGenerator {

	List<BankAAccount> findByUserName(String name);
	
	List<BankAAccount> findByUserNameForUpdate(String name);

	BankAAccount saveOrUpdate(BankAAccount account);

	BankAAccount findByUserId(BigInteger id);

	List<BankAAccount> findAll();

	void saveAll(List<BankAAccount> accounts);

	void removeAll();

	void findByUserNameAndReduceBalance(String userName, BigDecimal reduce);

}
