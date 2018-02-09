package com.distributed.transaction.message.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

public class NormalProducer {

	public static void main(String[] args) throws MQClientException {
		DefaultMQProducer producer = new DefaultMQProducer("ProducerGroup");
		producer.setNamesrvAddr("47.96.133.164:9876;101.132.186.26:9876");
		// producer.setRetryTimesWhenSendFailed(3);
		producer.start();

		for (int i = 0; i < 10; i++) {
			try {
				Message msg = new Message("TopicTest", "TagC", ("Hello RocketMQ xyz " + i).getBytes("UTF-8"));
				SendResult result = producer.send(msg);
				System.out.println(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		producer.shutdown();
	}

}
