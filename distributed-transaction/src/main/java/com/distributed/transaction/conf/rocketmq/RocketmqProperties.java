package com.distributed.transaction.conf.rocketmq;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(ignoreUnknownFields = false, ignoreInvalidFields = false, prefix = "spring.extends.rocketmq")
public class RocketmqProperties {

	@NotBlank
	private String namesrvAddr;
	private String instanceName;
	@NotBlank
	private String producerGroupName;
	@NotBlank
	private String consumerGroupName;
	@NotBlank
	private String recoverConsumerGroupName;
	@NotBlank
	private String producerQueueSize;

	public String getNamesrvAddr() {
		return namesrvAddr;
	}

	public void setNamesrvAddr(String namesrvAddr) {
		this.namesrvAddr = namesrvAddr;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getProducerGroupName() {
		return producerGroupName;
	}

	public void setProducerGroupName(String producerGroupName) {
		this.producerGroupName = producerGroupName;
	}

	public String getConsumerGroupName() {
		return consumerGroupName;
	}

	public void setConsumerGroupName(String consumerGroupName) {
		this.consumerGroupName = consumerGroupName;
	}

	public String getProducerQueueSize() {
		return producerQueueSize;
	}

	public void setProducerQueueSize(String producerQueueSize) {
		this.producerQueueSize = producerQueueSize;
	}

	public String getRecoverConsumerGroupName() {
		return recoverConsumerGroupName;
	}

	public void setRecoverConsumerGroupName(String recoverConsumerGroupName) {
		this.recoverConsumerGroupName = recoverConsumerGroupName;
	}

}
