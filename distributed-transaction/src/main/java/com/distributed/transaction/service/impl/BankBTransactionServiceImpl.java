package com.distributed.transaction.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.distributed.transaction.repository.distributed.BankBAccountRepo;
import com.distributed.transaction.request.merger.BalanceChangeRequest;
import com.distributed.transaction.service.BankBTransactionService;

@Service("bankBTransactionService")
public class BankBTransactionServiceImpl implements BankBTransactionService {

	@Autowired
	private BankBAccountRepo bankBRepo;

	@Transactional(transactionManager = "transactionManagerSecondary", propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
	@Override
	public void modifyBalance(List<BalanceChangeRequest> requests) {
		List<BalanceChangeRequest> mergedReq = mergeRequest(requests);
		for (BalanceChangeRequest req : mergedReq) {
			bankBRepo.findByUserNameAndAddBalance(req.getUserName(), req.getValue());
		}
	}

	private List<BalanceChangeRequest> mergeRequest(final List<BalanceChangeRequest> requests) {
		Map<String, BigDecimal> mergeReq = new HashMap<>();
		for (BalanceChangeRequest request : requests) {
			BigDecimal value = mergeReq.get(request.getUserName());
			mergeReq.put(request.getUserName(), value == null ? request.getValue() : value.add(request.getValue()));
		}
		List<BalanceChangeRequest> results = new ArrayList<>();
		for (String name : mergeReq.keySet()) {
			BalanceChangeRequest request = new BalanceChangeRequest();
			request.setUserName(name);
			request.setValue(mergeReq.get(name));
			results.add(request);
		}
		return results;
	}

	@Transactional(transactionManager = "transactionManagerSecondary", propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
	@Override
	public void modifyBalance(String userName, BigDecimal value) {
		bankBRepo.findByUserNameAndAddBalance(userName, value);
	}


}
