package com.distributed.transaction.message.producer;
/*package com.distributedtransaction.message.producer;

import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.distributedtransaction.message.MessageType.Direction;
import com.distributedtransaction.transaction.executor.TransactionExecuterImpl;
import com.distributedtransaction.transaction.listener.TransactionCheckListenerImpl;;

@Service
public class TransactionalMQProducerFactory {

	@Autowired
	private TransactionCheckListenerImpl listener;

	@Autowired
	private TransactionExecuterImpl executor;

	private static final String NAME_SERVER_ADDR = "47.96.133.164:9876;101.132.186.26:9876";

	public SimpleTMQProducer createTransactionalProducer(Direction msgType, String groupName) {
		TransactionMQProducer producer = new TransactionMQProducer(groupName);
		switch (msgType) {
		case BANK_A_TO_BANK_B:
			producer.setNamesrvAddr(NAME_SERVER_ADDR);
			break;
		case BANK_B_TO_BANK_A:
			producer.setNamesrvAddr(NAME_SERVER_ADDR);
			break;
		default:
			break;
		}
		producer.setTransactionCheckListener(listener);
		return new SimpleTMQProducer(producer, executor);
	}

}
*/