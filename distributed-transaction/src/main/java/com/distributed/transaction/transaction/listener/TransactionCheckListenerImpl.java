package com.distributed.transaction.transaction.listener;

import java.math.BigInteger;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.distributed.transaction.domain.distributed.BankAUserHistoricalOperation;
import com.distributed.transaction.service.BankAUserHistOperationService;

@Component
@Scope("prototype")
public class TransactionCheckListenerImpl implements TransactionCheckListener {

	@Autowired
	private BankAUserHistOperationService operationService;

	public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
		BankAUserHistoricalOperation operation = operationService.findById(new BigInteger(msg.getKeys()));
		if (operation == null || !operation.getStatus().equalsIgnoreCase("In-progress")) {
			return LocalTransactionState.ROLLBACK_MESSAGE;
		}
		operationService.updateStatusForBankAUserHistoricalOperation(operation.getOperationId(), "Complete");
		return LocalTransactionState.COMMIT_MESSAGE;
	}

}
