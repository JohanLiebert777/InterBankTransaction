package com.distributed.transaction.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.distributed.transaction.domain.distributed.BankAAccount;
import com.distributed.transaction.repository.distributed.BankAAccountRepo;
import com.distributed.transaction.service.BankAAccountService;

@Service("bankAAccountService")
public class BankAAccountServiceImpl implements BankAAccountService {

	@Autowired
	private BankAAccountRepo bankARepo;

	@Override
	public List<BankAAccount> findByUserName(String name) {
		return bankARepo.findByUserName(name);
	}

	@Override
	@Transactional(transactionManager = "transactionManagerSecondary", propagation = Propagation.REQUIRED)
	public List<BankAAccount> findByUserNameForUpdate(String name) {
		return bankARepo.findByUserNameForUpdate(name);
	}
	
	@Override
	public void findByUserNameAndReduceBalance(String userName, BigDecimal reduce) {
		bankARepo.findByUserNameAndReduceBalance(userName, reduce);
	}

	@Override
	@Transactional(transactionManager = "transactionManagerSecondary")
	public BankAAccount saveOrUpdate(BankAAccount account) {
		setAccountId(account);
		return bankARepo.save(account);
	}

	@Override
	public BankAAccount findByUserId(BigInteger id) {
		return bankARepo.findOne(id);
	}

	@Override
	public void removeAll() {
		bankARepo.deleteAll();
	}

	@Override
	public void saveAll(List<BankAAccount> accounts) {
		for (BankAAccount account : accounts) {
			setAccountId(account);
		}
		bankARepo.save(accounts);
	}

	private void setAccountId(BankAAccount account) {
		if (account.getUserId() == null) {
			account.setUserId(
					new BigInteger(generateTimeStamp()).add(new BigInteger(String.valueOf(account.hashCode()))));
		}
	}

	@Override
	public List<BankAAccount> findAll() {
		return bankARepo.findAll();
	}

}
