package com.distributed.transaction.controller;

import java.math.BigInteger;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.distributed.transaction.domain.distributed.BankAAccount;
import com.distributed.transaction.domain.distributed.BankAUserHistoricalOperation;
import com.distributed.transaction.domain.distributed.BankBAccount;
import com.distributed.transaction.repository.distributed.BankAAccountRepo;
import com.distributed.transaction.repository.distributed.BankAUserHistoricalOperationRepo;
import com.distributed.transaction.repository.distributed.BankBAccountRepo;

@RestController
@RequestMapping("/operation")
public class UserOperationHistController {

	@Autowired
	private BankAUserHistoricalOperationRepo repo;

	@Autowired
	private BankAAccountRepo bankARepo;

	@Autowired
	private BankBAccountRepo bankBRepo;

	@RequestMapping("/hello")
	public Object hello() {
		return "hello";
	}

	@RequestMapping("/addForBankA")
	public String add() {

		for (BankAAccount account : bankARepo.findAll()) {
			for (int i = 0; i < 10; i++) {
				BankAUserHistoricalOperation oper = new BankAUserHistoricalOperation();
				oper.setOperationTypeId((long) (i % 5));
				oper.setUserId(account.getUserId());
				oper.setReason("test reason " + Math.random());
				oper.setCreatedBy("Tester A");
				oper.setCreatedDate(new Date());
				repo.save(oper);
			}
		}

		for (BankBAccount account : bankBRepo.findAll()) {
			for (int i = 0; i < 10; i++) {
				BankAUserHistoricalOperation oper = new BankAUserHistoricalOperation();
				oper.setOperationTypeId((long) (i % 5));
				oper.setUserId(account.getUserId());
				oper.setReason("test reason " + Math.random());
				oper.setCreatedBy("Tester B");
				oper.setCreatedDate(new Date());
				repo.save(oper);
			}
		}

		return "success";
	}

	@RequestMapping("/dropAllOpe")
	public String dropAllOperation() {
		repo.deleteAll();
		return "success";
	}

	@RequestMapping("/query")
	public Object query() {
		return repo.findAll();
	}

	@RequestMapping("/findById")
	public Object findByID(BigInteger id) {
		return repo.findOne(id);
	}

	@RequestMapping("/findByUserId")
	public Object findByUserID(BigInteger userId) {
		return repo.findByUserId(userId);
	}
}
