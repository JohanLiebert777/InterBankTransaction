package com.distributed.transaction.conf.rocketmq;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.distributed.transaction.message.producer.SimpleTMQProducer;
import com.distributed.transaction.transaction.executor.TransactionExecuterImpl;
import com.distributed.transaction.transaction.listener.TransactionCheckListenerImpl;

@Component
public class SimpleTMQProducerPool {

	@Autowired
	private RocketmqProperties properties;

	@Autowired
	private TransactionCheckListenerImpl listener;

	@Autowired
	private TransactionExecuterImpl executor;

	private static final LinkedBlockingDeque<TransactionMQProducer> availableProducerDeque;
	private static final LinkedBlockingQueue<TransactionMQProducer> inWorkingProducerQueue;
	private static final ReentrantLock lock;
	private static final Condition isNotFull;

	static {
		availableProducerDeque = new LinkedBlockingDeque<TransactionMQProducer>();
		inWorkingProducerQueue = new LinkedBlockingQueue<TransactionMQProducer>();
		lock = new ReentrantLock();
		isNotFull = lock.newCondition();
	}

	public SimpleTMQProducer getProducer() throws MQClientException, InterruptedException {
		while (true) {
			try {
				lock.lockInterruptibly();
				TransactionMQProducer nextProducer = null;
				if ((nextProducer = availableProducerDeque.pollFirst()) != null) {
					inWorkingProducerQueue.add(nextProducer);
					SimpleTMQProducer simpleProducer = new SimpleTMQProducer(nextProducer, executor);
					simpleProducer.setAvailableProducerDeque(availableProducerDeque);
					simpleProducer.setInWorkingProducerQueue(inWorkingProducerQueue);
					simpleProducer.setLock(lock);
					simpleProducer.setIsNotFull(isNotFull);
					return simpleProducer;
				}
			} finally {
				lock.unlock();
			}

			if (availableProducerDeque.isEmpty()) {
				try {
					lock.lockInterruptibly();
					if (availableProducerDeque.size() + inWorkingProducerQueue.size() + 1 <= Integer
							.valueOf(properties.getProducerQueueSize())) {
						TransactionMQProducer producer = new TransactionMQProducer(properties.getProducerGroupName());
						producer.setInstanceName(properties.getProducerGroupName() + Math.random());
						producer.setNamesrvAddr(properties.getNamesrvAddr());
						producer.setTransactionCheckListener(listener);
						producer.start();
						availableProducerDeque.offerFirst(producer);
					} else {
						isNotFull.await();
					}
				} finally {
					lock.unlock();
				}
			}
		}
	}

}
