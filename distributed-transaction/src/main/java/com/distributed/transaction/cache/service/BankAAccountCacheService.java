package com.distributed.transaction.cache.service;

import java.math.BigInteger;
import java.util.List;

import com.distributed.transaction.domain.distributed.BankAAccount;
import com.distributed.transaction.service.ShardingDomainKeyGenerator;

public interface BankAAccountCacheService extends ShardingDomainKeyGenerator {

	List<BankAAccount> findByUserName(String name);

	BankAAccount saveOrUpdate(BankAAccount account);

	BankAAccount findByUserId(BigInteger id);

	List<BankAAccount> findAll();

	void saveAll(List<BankAAccount> accounts);

	void removeAll();

	boolean removeAccountByUserName(String name);

}
