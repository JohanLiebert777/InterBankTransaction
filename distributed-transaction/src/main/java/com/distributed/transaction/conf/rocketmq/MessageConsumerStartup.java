package com.distributed.transaction.conf.rocketmq;

import java.util.Map;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter({ RocketmqConsumerConfig.class, RocketmqConsumerConfig.class })
public class MessageConsumerStartup {

	private class ApplicationBootListener implements ApplicationListener<ApplicationReadyEvent> {
		@Override
		public void onApplicationEvent(ApplicationReadyEvent event) {
			if (event.getApplicationContext().getParent() == null) {
				Map<String, DefaultMQPushConsumer> consumerBeanMap = event.getApplicationContext()
						.getBeansOfType(DefaultMQPushConsumer.class);
				for (DefaultMQPushConsumer consumer : consumerBeanMap.values()) {
					try {
						consumer.start();
					} catch (MQClientException e) {
						System.err.println("Consumers start failed.");
						e.printStackTrace();
						return;
					}
				}
			}
		}
	}

	@Bean
	public ApplicationBootListener registApplicationBootListener() {
		return new ApplicationBootListener();
	};

}
