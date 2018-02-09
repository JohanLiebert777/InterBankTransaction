package com.distributed.transaction.service;

import java.math.BigInteger;
import java.util.List;

import com.distributed.transaction.domain.distributed.BankBAccount;

public interface BankBAccountService extends ShardingDomainKeyGenerator{

	List<BankBAccount> findByUserName(String name);

	BankBAccount saveOrUpdate(BankBAccount account);

	BankBAccount findByUserId(BigInteger id);

	List<BankBAccount> findAll();
	
	void removeAll();

}
