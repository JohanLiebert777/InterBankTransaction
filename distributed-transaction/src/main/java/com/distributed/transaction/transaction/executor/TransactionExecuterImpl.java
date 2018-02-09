package com.distributed.transaction.transaction.executor;

import java.util.Date;
import java.util.List;

import org.apache.rocketmq.client.producer.LocalTransactionExecuter;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.distributed.transaction.domain.distributed.BankAUserHistoricalOperation;
import com.distributed.transaction.domain.distributed.BankBUserHistoricalOperation;
import com.distributed.transaction.message.MessageType;
import com.distributed.transaction.service.BankAUserHistOperationService;
import com.distributed.transaction.service.BankBUserHistOperationService;

@Service
public class TransactionExecuterImpl implements LocalTransactionExecuter {

	@Autowired
	private BankAUserHistOperationService bankAUserHistOperationService;

	@Autowired
	private BankBUserHistOperationService bankBUserHistOperationService;

	@Override
	public LocalTransactionState executeLocalTransactionBranch(Message msg, Object arg) {
		if (arg instanceof List) {
			@SuppressWarnings("unchecked")
			List<BankBUserHistoricalOperation> operationList = (List<BankBUserHistoricalOperation>) arg;
			for (BankBUserHistoricalOperation operation : operationList) {
				operation.setModifiedBy(MessageType.OperationType.RECOVER_TRANSACTION.getOpeartion());
				operation.setModifiedDate(new Date());
				bankBUserHistOperationService.logOneOperation(operation);
			}
		} else {
			BankAUserHistoricalOperation operation = (BankAUserHistoricalOperation) arg;
			bankAUserHistOperationService.updateStatusForBankAUserHistoricalOperation(operation.getOperationId(), "In-progress");
		}
		return LocalTransactionState.COMMIT_MESSAGE;
	}

}
