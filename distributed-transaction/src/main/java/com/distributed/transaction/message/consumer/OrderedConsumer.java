package com.distributed.transaction.message.consumer;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

public class OrderedConsumer {
	public static void main(String[] args) throws MQClientException {
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ConsumerGroup");
		consumer.setNamesrvAddr("47.96.133.164:9876;101.132.186.26:9876");
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		consumer.subscribe("TopicOrder", "*");

		consumer.registerMessageListener(new MessageListenerOrderly() {
			public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msg, ConsumeOrderlyContext context) {
				
				System.out.println(msg);
				return ConsumeOrderlyStatus.SUCCESS;
			}
		});
		consumer.start();
		System.out.println("Consumer Started");
	}
}
