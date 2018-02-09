package com.distributed.transaction.conf.rocketmq;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.fastjson.JSON;
import com.distributed.transaction.message.BankBRecoverMessageContent;
import com.distributed.transaction.message.MessageType;
import com.distributed.transaction.message.MoneyTransferMessageContent;

@Configuration
@EnableConfigurationProperties(RocketmqProperties.class)
@ConditionalOnProperty(prefix = "spring.extends.rocketmq", value = "namesrvAddr")
public class RocketmqConsumerConfig {

	@Autowired
	private RocketmqProperties properties;

	@Autowired
	private ApplicationEventPublisher publisher;

	private static final long SLEEP_MILL_SECS = 1000;

	@Bean("moneyTransferFromBankAToBankBConsumer")
	public DefaultMQPushConsumer createMoneyTransferFromBankAToBankBConsumer() throws MQClientException {
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(properties.getConsumerGroupName());
		consumer.setNamesrvAddr(properties.getNamesrvAddr());
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		consumer.subscribe(MessageType.Direction.BANK_A_TO_BANK_B.getDirection(),
				MessageType.OperationType.TRANSFER_MONEY.getOpeartion());

		consumer.registerMessageListener(new MessageListenerConcurrently() {
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
				MessageExt msgExt = msgs.get(0);
				MoneyTransferMessageContent msgContent = JSON.parseObject(msgExt.getBody(),
						MoneyTransferMessageContent.class);

				RocketmqReceivedMsgEvent event = new RocketmqReceivedMsgEvent();
				event.setContent(msgContent);
				event.setKey(msgExt.getKeys());
				event.setOperation(MessageType.OperationType.TRANSFER_MONEY.getOpeartion());
				event.setDirection(MessageType.Direction.BANK_A_TO_BANK_B.getDirection());
				publisher.publishEvent(event);
				while (true) {
					try {
						if (event.isMessageEventReceived() && event.isConsumeSuccess()) {
							break;
						}
						if (event.isMessageEventReceived() && !event.isConsumeSuccess()) {
							throw event.getException();
						}
						System.out.println("Park listener thread.");
						Thread.sleep(SLEEP_MILL_SECS);
					} catch (InterruptedException | MessageConsumeFailException e) {
						e.printStackTrace();
						return ConsumeConcurrentlyStatus.RECONSUME_LATER;
					}
				}
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		});
		return consumer;
	}

	@Bean("bankBTransactionRecoverConsumer")
	public DefaultMQPushConsumer createBankBMessageRecoverConsumer() throws MQClientException {
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(properties.getRecoverConsumerGroupName());
		consumer.setNamesrvAddr(properties.getNamesrvAddr());
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
		consumer.subscribe(MessageType.Direction.BANK_B_TO_BANK_A.getDirection(),
				MessageType.OperationType.RECOVER_TRANSACTION.getOpeartion());

		consumer.registerMessageListener(new MessageListenerConcurrently() {
			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
				MessageExt msgExt = msgs.get(0);
				BankBRecoverMessageContent msgContent = JSON.parseObject(msgExt.getBody(),
						BankBRecoverMessageContent.class);

				RocketmqReceivedMsgEvent event = new RocketmqReceivedMsgEvent();
				event.setContent(msgContent);
				event.setKey(msgExt.getKeys());
				event.setOperation(MessageType.OperationType.RECOVER_TRANSACTION.getOpeartion());
				event.setDirection(MessageType.Direction.BANK_B_TO_BANK_A.getDirection());
				publisher.publishEvent(event);
				while (true) {
					try {
						if (event.isMessageEventReceived() && event.isConsumeSuccess()) {
							break;
						}
						if (event.isMessageEventReceived() && !event.isConsumeSuccess()) {
							throw event.getException();
						}
						System.out.println("Park listener thread.");
						Thread.sleep(SLEEP_MILL_SECS);
					} catch (InterruptedException | MessageConsumeFailException e) {
						e.printStackTrace();
						return ConsumeConcurrentlyStatus.RECONSUME_LATER;
					}
				}
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		});
		return consumer;
	}
}
