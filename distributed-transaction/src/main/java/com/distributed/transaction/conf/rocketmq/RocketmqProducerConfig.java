package com.distributed.transaction.conf.rocketmq;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(RocketmqConsumerConfig.class)
@EnableConfigurationProperties(RocketmqProperties.class)
@ConditionalOnProperty(prefix = "spring.extends.rocketmq", value = "namesrvAddr")
public class RocketmqProducerConfig {

	@Bean("simpleTMQProducerPool")
	public SimpleTMQProducerPool createSimpleTMQProducerPool() {
		return new SimpleTMQProducerPool();
	}

}
