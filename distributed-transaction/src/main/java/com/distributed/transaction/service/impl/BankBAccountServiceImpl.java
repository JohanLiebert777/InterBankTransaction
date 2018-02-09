package com.distributed.transaction.service.impl;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.distributed.transaction.domain.distributed.BankBAccount;
import com.distributed.transaction.repository.distributed.BankBAccountRepo;
import com.distributed.transaction.service.BankBAccountService;

@Service
public class BankBAccountServiceImpl implements BankBAccountService {

	@Autowired
	private BankBAccountRepo repo;

	@Override
	public List<BankBAccount> findByUserName(String name) {
		return repo.findByUserName(name);
	}

	@Override
	@Transactional(transactionManager = "transactionManagerSecondary", propagation = Propagation.REQUIRES_NEW)
	public BankBAccount saveOrUpdate(BankBAccount account) {
		if (account.getUserId() == null) {
			account.setUserId(
					new BigInteger(generateTimeStamp()).add(new BigInteger(String.valueOf(account.hashCode()))));
		}
		return repo.save(account);
	}

	@Override
	public BankBAccount findByUserId(BigInteger id) {
		return repo.findOne(id);
	}

	@Override
	public void removeAll() {
		repo.deleteAll();
	}

	@Override
	public List<BankBAccount> findAll() {
		return repo.findAll();
	}

}
