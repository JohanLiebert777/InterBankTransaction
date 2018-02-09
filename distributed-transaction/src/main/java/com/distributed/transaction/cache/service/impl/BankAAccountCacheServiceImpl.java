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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.distributed.transaction.cache.service.BankAAccountCacheService;
import com.distributed.transaction.domain.distributed.BankAAccount;
import com.distributed.transaction.service.BankAAccountService;

@Service("bankAAccountCacheService")
public class BankAAccountCacheServiceImpl implements BankAAccountCacheService {

	@Autowired
	private RedissonClient redis;

	@Autowired
	@Qualifier("bankAAccountService")
	private BankAAccountService service;

	private static final int ACCOUNT_EXPIRE_DURATION_IN_SECS = 600;
	private static final int LOCK_EXPIRE_DURATION_IN_SECS = 10;

	@Override
	public boolean removeAccountByUserName(String name) {
		boolean rlt = false;
		RSet<BigInteger> idSet = redis.getSet("bankA:user:name:" + name + ":userId:");
		if (idSet != null && idSet.size() != 0) {
			rlt = idSet.delete();
		}
		return rlt;
	}

	@Override
	public List<BankAAccount> findByUserName(String name) {
		List<BankAAccount> accounts = new ArrayList<>();
		RSet<BigInteger> idSet = redis.getSet("bankA:user:name:" + name + ":userId:");
		if (!idSet.isEmpty() && idSet.remainTimeToLive() > ACCOUNT_EXPIRE_DURATION_IN_SECS) {
			for (BigInteger id : idSet) {
				accounts.add(findByUserId(id));
			}
		} else {
			RLock locker = redis.getLock("bankA:user:name:" + name + ":userId:lock");
			try {
				locker.lock(LOCK_EXPIRE_DURATION_IN_SECS, TimeUnit.SECONDS);
				idSet = redis.getSet("bankA:user:name:" + name + ":userId:");
				if (idSet.isEmpty() || idSet.remainTimeToLive() <= ACCOUNT_EXPIRE_DURATION_IN_SECS) {
					accounts = service.findByUserName(name);
					putIntoCache(accounts);
				} else {
					for (BigInteger id : idSet) {
						accounts.add(findByUserId(id));
					}
				}
			} finally {
				locker.unlock();
			}
		}
		return accounts;
	}

	private void putIntoCache(List<BankAAccount> accounts) {
		for (BankAAccount account : accounts) {
			putIntoCache(account);
		}
	}

	private void putIntoCache(BankAAccount account) {
		createIdToAccountCache(account);
		createNameToIdCache(account);
	}

	private void createNameToIdCache(BankAAccount account) {
		RSet<BigInteger> idSet = redis.getSet("bankA:user:name:" + account.getUserName() + ":userId:");
		idSet.add(account.getUserId());
		idSet.expire(ACCOUNT_EXPIRE_DURATION_IN_SECS, TimeUnit.SECONDS);
	}

	private void createIdToAccountCache(BankAAccount account) {
		RBucket<BankAAccount> bucket = redis.getBucket("bankA:user:id:" + account.getUserId() + ":account");
		bucket.set(account);
		bucket.expire(ACCOUNT_EXPIRE_DURATION_IN_SECS, TimeUnit.SECONDS);
	}

	@Override
	public BankAAccount saveOrUpdate(BankAAccount account) {
		service.saveOrUpdate(account);
		return account;
	}

	@Override
	public BankAAccount findByUserId(BigInteger id) {
		BankAAccount account = null;
		RBucket<BankAAccount> bucket = redis.getBucket("bankA:user:id:" + id + ":account");
		account = bucket.get();
		if (account == null || bucket.remainTimeToLive() < ACCOUNT_EXPIRE_DURATION_IN_SECS) {
			RLock locker = redis.getLock("bankA:user:id:" + id + ":account:lock");
			try {
				locker.lock(LOCK_EXPIRE_DURATION_IN_SECS, TimeUnit.SECONDS);
				bucket = redis.getBucket("bankA:user:id:" + id + ":account");
				if (bucket.get() == null || bucket.remainTimeToLive() < ACCOUNT_EXPIRE_DURATION_IN_SECS) {
					account = service.findByUserId(id);
					putIntoCache(account);
				}
			} finally {
				locker.unlock();
			}
		}
		return account;
	}

	@Override
	public List<BankAAccount> findAll() {
		RBucket<Boolean> isAllLoadedBucket = redis.getBucket("bankA:user:all");
		boolean isAllLoaded = isAllLoadedBucket.get() == null ? false : true;
		List<BankAAccount> accounts = new ArrayList<>();
		if (isAllLoaded) {
			List<RBucket<BankAAccount>> buckets = redis.getBuckets().find("bankA:user:id:*:account");
			for (RBucket<BankAAccount> bucket : buckets) {
				accounts.add(bucket.get());
			}
		} else {
			RLock locker = redis.getLock("bankA:user:id:*:account");
			try {
				locker.lock(LOCK_EXPIRE_DURATION_IN_SECS, TimeUnit.SECONDS);
				isAllLoadedBucket.expire(ACCOUNT_EXPIRE_DURATION_IN_SECS, TimeUnit.SECONDS);
				isAllLoadedBucket.set(true);
				accounts = service.findAll();
				putIntoCache(accounts);
			} finally {
				locker.unlock();
			}
		}
		return accounts;
	}

	@Override
	public void saveAll(List<BankAAccount> accounts) {
		service.saveAll(accounts);
	}

	@Override
	public void removeAll() {
		service.removeAll();
	}

}
