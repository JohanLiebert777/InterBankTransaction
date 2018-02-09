package com.distributed.transaction.conf.stream.limiter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@EnableConfigurationProperties(StreamLimiterProperties.class)
public class StreamLimiterConfig {

	@Autowired
	private StreamLimiterProperties properties;

	@Bean("streamLimiter")
	@Scope("singleton")
	public StreamLimiter createStreamLimiter() {
		Map<StreamDomainConstant, Integer> rateMapParam = new HashMap<>();
		for (Map<String, String> rateMappConfig : properties.getDomainRate()) {
			String domain = null;
			Integer rate = null;
			for (String key : rateMappConfig.keySet()) {
				if (key.equals("domain")) {
					domain = rateMappConfig.get(key);
				} else if (key.equals("rate")) {
					rate = Integer.valueOf(rateMappConfig.get(key));
				}
			}
			rateMapParam.put(StreamDomainConstant.findByName(domain), rate);
		}
		return StreamLimiter.getInstanceAndSetConfig(rateMapParam);
	}

}
