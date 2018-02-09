package com.distributed.transaction.service;

import java.math.BigDecimal;
import java.util.List;

import org.apache.rocketmq.client.exception.MQClientException;

import com.distributed.transaction.conf.rocketmq.RocketmqReceivedMsgEvent;
import com.distributed.transaction.domain.distributed.BankAAccount;
import com.distributed.transaction.domain.distributed.BankAUserHistoricalOperation;
import com.distributed.transaction.request.merger.BalanceChangeRequest;

public interface BankATransactionService {

	void findMissedTransaction(RocketmqReceivedMsgEvent event) throws Exception;

	List<BankAAccount> moveToBankBFromBankA(List<BalanceChangeRequest> requests)
			throws MQClientException, InterruptedException;

	BankAAccount moveToBankBFromBankA(String userName, BigDecimal value, BankAUserHistoricalOperation operation)
			throws MQClientException, InterruptedException;

}
