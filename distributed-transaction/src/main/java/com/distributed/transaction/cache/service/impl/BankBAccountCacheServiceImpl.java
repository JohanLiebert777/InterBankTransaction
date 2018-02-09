package com.distributed.transaction.cache.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.distributed.transaction.cache.service.BankBAccountCacheService;
import com.distributed.transaction.domain.distributed.BankBAccount;
import com.distributed.transaction.service.BankBAccountService;

@Service("bankBAccountCacheService")
public class BankBAccountCacheServiceImpl implements BankBAccountCacheService {

	@Autowired
	private RedissonClient redis;

	@Autowired
	private BankBAccountService service;

	private static final int ACCOUNT_EXPIRE_DURATION_IN_SECS = 600;
	private static final int LOCK_EXPIRE_DURATION_IN_SECS = 10;

	@Override
	public List<BankBAccount> findByUserName(String name) {
		List<BankBAccount> accounts = new ArrayList<>();
		RSet<BigInteger> idSet = redis.getSet("bankB:user:name:" + name + ":userId:");
		if (!idSet.isEmpty() && idSet.remainTimeToLive() > ACCOUNT_EXPIRE_DURATION_IN_SECS) {
			for (BigInteger id : idSet) {
				accounts.add(findByUserId(id));
			}
		} else {
			RLock locker = redis.getLock("bankB:user:name:" + name + ":userId:lock");
			try {
				locker.lock(LOCK_EXPIRE_DURATION_IN_SECS, TimeUnit.SECONDS);
				idSet = redis.getSet("bankB:user:name:" + name + ":userId:");
				if (idSet.isEmpty() || idSet.remainTimeToLive() <= ACCOUNT_EXPIRE_DURATION_IN_SECS) {
					accounts = service.findByUserName(name);
					putIntoCache(accounts);
				}
			} finally {
				locker.unlock();
			}
		}
		return accounts;
	}

	@Override
	public BankBAccount findByUserId(BigInteger id) {
		BankBAccount account = null;
		RBucket<BankBAccount> cacheAccount = redis.getBucket("bankB:user:id:" + id);
		account = cacheAccount.get();
		if (account == null || cacheAccount.remainTimeToLive() < ACCOUNT_EXPIRE_DURATION_IN_SECS) {
			RLock lock = redis.getLock("bankB:user:id:" + id + ":account:lock");
			try {
				lock.lock(LOCK_EXPIRE_DURATION_IN_SECS, TimeUnit.SECONDS);
				account = cacheAccount.get();
				if (account == null || cacheAccount.remainTimeToLive() <= ACCOUNT_EXPIRE_DURATION_IN_SECS) {
					account = service.findByUserId(id);
					putIntoCache(account);
				}
			} finally {
				lock.unlock();
			}
		}
		return account;
	}

	private void putIntoCache(List<BankBAccount> accounts) {
		for (BankBAccount account : accounts) {
			putIntoCache(account);
		}
	}

	private void putIntoCache(BankBAccount account) {
		createIdToAccountCache(account);
		createNameToIdCache(account);
	}

	private void createNameToIdCache(BankBAccount account) {
		RSet<BigInteger> idSet = redis.getSet("bankB:user:name:" + account.getUserName() + ":userId:");
		idSet.add(account.getUserId());
		idSet.expire(ACCOUNT_EXPIRE_DURATION_IN_SECS, TimeUnit.SECONDS);
	}

	private void createIdToAccountCache(BankBAccount account) {
		RBucket<BankBAccount> bucket = redis.getBucket("bankB:user:id:" + account.getUserId() + ":account");
		bucket.set(account);
		bucket.expire(ACCOUNT_EXPIRE_DURATION_IN_SECS, TimeUnit.SECONDS);
	}

}
