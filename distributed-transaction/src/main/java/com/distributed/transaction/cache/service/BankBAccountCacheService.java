package com.distributed.transaction.cache.service;

import java.math.BigInteger;
import java.util.List;

import com.distributed.transaction.domain.distributed.BankBAccount;

public interface BankBAccountCacheService {

	List<BankBAccount> findByUserName(String name);

	BankBAccount findByUserId(BigInteger id);
}
