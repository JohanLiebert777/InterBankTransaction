package com.distributed.transaction.message.producer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionExecuter;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;

import com.distributed.transaction.domain.distributed.BankAUserHistoricalOperation;
import com.distributed.transaction.domain.distributed.BankBUserHistoricalOperation;

public class SimpleTMQProducer {

	private TransactionMQProducer producer;

	private LocalTransactionExecuter executor;

	private ReentrantLock lock;
	private Condition isNotFull;

	private LinkedBlockingDeque<TransactionMQProducer> availableProducerDeque;
	private LinkedBlockingQueue<TransactionMQProducer> inWorkingProducerQueue;

	public SimpleTMQProducer(TransactionMQProducer producer, LocalTransactionExecuter executor) {
		this.producer = producer;
		this.executor = executor;
	}

	public List<SendResult> sendMessageInTransaction(Map<Message, BankAUserHistoricalOperation> messageMap)
			throws MQClientException {
		List<SendResult> results = new ArrayList<>();
		for (Message msg : messageMap.keySet()) {
			results.add(producer.sendMessageInTransaction(msg, executor, messageMap.get(msg)));
		}
		cleanProducer();
		return results;
	}

	public List<SendResult> sendMessageInTransaction(List<Message> msgs) throws MQClientException {
		List<SendResult> results = new ArrayList<>();
		for (Message msg : msgs) {
			results.add(producer.sendMessageInTransaction(msg, executor, null));
		}
		cleanProducer();
		return results;
	}

	public SendResult sendMessageInTransaction(Message msg) throws MQClientException {
		SendResult result = producer.sendMessageInTransaction(msg, executor, null);
		cleanProducer();
		return result;
	}

	public SendResult sendMessageInTransaction(Message msg, List<BankBUserHistoricalOperation> operations)
			throws MQClientException {
		SendResult result = producer.sendMessageInTransaction(msg, executor, operations);
		cleanProducer();
		return result;
	}

	public SendResult sendMessageInTransaction(Message msg, BankAUserHistoricalOperation operation)
			throws MQClientException {
		SendResult result = producer.sendMessageInTransaction(msg, executor, operation);
		cleanProducer();
		return result;
	}

	private void cleanProducer() {
		try {
			lock.lock();
			if (inWorkingProducerQueue.remove(producer)) {
				availableProducerDeque.addLast(producer);
				producer = null;
				executor = null;
				availableProducerDeque = null;
				inWorkingProducerQueue = null;
				isNotFull.signal();
			}
		} finally {
			lock.unlock();
		}
	}

	public LinkedBlockingDeque<TransactionMQProducer> getAvailableProducerDeque() {
		return availableProducerDeque;
	}

	public void setAvailableProducerDeque(LinkedBlockingDeque<TransactionMQProducer> availableProducerDeque) {
		this.availableProducerDeque = availableProducerDeque;
	}

	public LinkedBlockingQueue<TransactionMQProducer> getInWorkingProducerQueue() {
		return inWorkingProducerQueue;
	}

	public void setInWorkingProducerQueue(LinkedBlockingQueue<TransactionMQProducer> inWorkingProducerQueue) {
		this.inWorkingProducerQueue = inWorkingProducerQueue;
	}

	public void setLock(final ReentrantLock lock) {
		this.lock = lock;
	}

	public void setIsNotFull(final Condition isNotFull) {
		this.isNotFull = isNotFull;
	}

}
