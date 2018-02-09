package com.distributed.transaction.conf.stream.limiter;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(ignoreUnknownFields = false, ignoreInvalidFields = false, prefix = "spring.extends.stream-limiter")
public class StreamLimiterProperties {

	@NotNull
	private List<Map<String, String>> domainRate;

	public List<Map<String, String>> getDomainRate() {
		return domainRate;
	}

	public void setDomainRate(List<Map<String, String>> domainRate) {
		this.domainRate = domainRate;
	}

}
