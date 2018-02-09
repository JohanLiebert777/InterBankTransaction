package com.distributed.transaction.message.producer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

public class OrderedProducer {

	private static final String[] tags = new String[] { "createTag", "payTag", "sendTag" };

	public static void main(String[] args) throws MQClientException, RemotingException, MQBrokerException,
			InterruptedException, UnsupportedEncodingException {

		DefaultMQProducer producer = new DefaultMQProducer("ProducerGroup");
		producer.setNamesrvAddr("47.96.133.164:9876;101.132.186.26:9876");
		// producer.setRetryTimesWhenSendFailed(3);
		producer.start();

		for (int orderId = 0; orderId < 10; orderId++) {
			for (int type = 0; type < tags.length; type++) {
				Message msg;
				msg = new Message("TopicOrder", tags[type % tags.length], orderId + ":" + type,
						(orderId + ":" + type).getBytes("UTF-8"));
				SendResult result = producer.send(msg, new MessageQueueSelector() {
					public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
						Integer id = (Integer) arg;
						int index = id % mqs.size();
						return mqs.get(index);
					}
				}, orderId);
				System.out.println(result);
			}
		}
		producer.shutdown();
	}

}
