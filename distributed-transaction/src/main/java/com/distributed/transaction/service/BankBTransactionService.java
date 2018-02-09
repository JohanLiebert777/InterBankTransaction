package com.distributed.transaction.service;

import java.math.BigDecimal;
import java.util.List;

import com.distributed.transaction.request.merger.BalanceChangeRequest;

public interface BankBTransactionService {

	void modifyBalance(String userName, BigDecimal value);

	void modifyBalance(List<BalanceChangeRequest> requests);
}
