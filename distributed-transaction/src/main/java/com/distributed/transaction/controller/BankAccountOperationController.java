package com.distributed.transaction.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.distributed.transaction.cache.service.BankAAccountCacheService;
import com.distributed.transaction.conf.stream.limiter.LimitRequest;
import com.distributed.transaction.conf.stream.limiter.StreamDomainConstant;
import com.distributed.transaction.domain.distributed.BankAAccount;
import com.distributed.transaction.domain.distributed.BankAUserHistoricalOperation;
import com.distributed.transaction.domain.distributed.BankBAccount;
import com.distributed.transaction.request.merger.BalanceChangeRequest;
import com.distributed.transaction.request.merger.RequestMerger;
import com.distributed.transaction.service.BankATransactionService;
import com.distributed.transaction.service.BankAUserHistOperationService;
import com.distributed.transaction.service.BankBAccountService;

@RestController
@RequestMapping("/account")
public class BankAccountOperationController {

	@Autowired
	@Qualifier("bankAAccountCacheService")
	private BankAAccountCacheService bankAAccountService;

	@Autowired
	private BankBAccountService bankBAccountService;

	@Autowired
	private BankATransactionService bankATransactionService;

	@LimitRequest(StreamDomainConstant.TRANSFER)
	@RequestMapping("/addInBankA")
	public String addInBankA() {
		List<BankAAccount> bankAAccounts = new ArrayList<>();
		for (int i = 1; i < 100; i++) {
			BankAAccount account = new BankAAccount();
			account.setGeographyId(Long.valueOf(new Random().ints(1, 9).findFirst().getAsInt()));
			account.setAccountType("Type " + new Random().ints(1, 9).findFirst().getAsInt() % 10);
			account.setBalance(BigDecimal.valueOf(i * new Random().ints(1000, 100000).findFirst().getAsInt()));
			account.setCreatedBy("Tester A");
			account.setCreatedDate(new Date());
			account.setUserName("User " + i);
			bankAAccounts.add(account);
		}
		bankAAccountService.saveAll(bankAAccounts);
		return "success";
	}

	@RequestMapping("/addInBankB")
	public String addInBankB() {
		for (int i = 1; i < 100; i++) {
			BankBAccount account = new BankBAccount();
			account.setGeographyId(Long.valueOf(new Random().ints(1, 9).findFirst().getAsInt()));
			account.setAccountType("Type " + new Random().ints(1, 9).findFirst().getAsInt() % 10);
			account.setBalance(BigDecimal.valueOf(i * new Random().ints(1000, 100000).findFirst().getAsInt()));
			account.setCreatedBy("Tester B");
			account.setCreatedDate(new Date());
			account.setUserName("User " + i);
			bankBAccountService.saveOrUpdate(account);
		}
		return "success";
	}

	@RequestMapping("/moveToBankB")
	@LimitRequest
	public String moveToBankB(String userName, Double amount) throws MQClientException, InterruptedException {
		BankAUserHistoricalOperation operationLog = bankAUserHistOperationService.saveNewForTransferMoney(userName,
				amount);
		bankATransactionService.moveToBankBFromBankA(userName, new BigDecimal(amount), operationLog);
		return "success";
	}

	@Autowired
	@Qualifier("bankARequestMerger")
	private RequestMerger requestMerger;

	@Autowired
	private BankAUserHistOperationService bankAUserHistOperationService;

	@RequestMapping("/batchMoveToBankB")
	@LimitRequest
	public String batchMoveToBankB(String userName, Double amount)
			throws MQClientException, InterruptedException, ExecutionException {
		// Log transfer history.
		BankAUserHistoricalOperation operationLog = bankAUserHistOperationService.saveNewForTransferMoney(userName,
				amount);
		// Batch transfer.
		BalanceChangeRequest request = new BalanceChangeRequest();
		request.setUserName(userName);
		request.setValue(new BigDecimal(amount));
		request.setOperationHist(operationLog);
		requestMerger.add(request);
		return "success";
	}

	@RequestMapping("/removeAllFromBankA")
	public String removeAllFromBankA() {
		bankAAccountService.removeAll();
		return "success";
	}

	@RequestMapping("/removeAllFromBankB")
	public String removeAllFromBankB() {
		bankBAccountService.removeAll();
		return "success";
	}

	@RequestMapping("/findAllInBankA")
	public Object findAlliInBankA() {
		return bankAAccountService.findAll();
	}

	@RequestMapping("/findAllInBankB")
	public Object findAlliInBankB() {
		return bankBAccountService.findAll();
	}

	@RequestMapping("/findByUserNameInBankA")
	@LimitRequest
	public Object findByUserNameInBankA(String userName) {
		List<BankAAccount> accounts = bankAAccountService.findByUserName(userName);
		return JSON.toJSON(accounts.get(0));
	}

	@RequestMapping("/findByUserNameInBankB")
	public Object findByUserNameInBankB(String userName) {
		return bankBAccountService.findByUserName(userName);
	}

}
