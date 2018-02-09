package com.distributed.transaction.conf.redis;

import java.util.Map;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@EnableCaching
public class RedisRedissonConfig {

	@Autowired
	private RedisProperties properties;

	@Bean("redisClient")
	public RedissonClient createMasterSlaveClient() {
		Config config = new Config();
		for (Map<String, String> nodeMap : properties.getClusterNodes()) {
			config.useClusterServers().addNodeAddress(nodeMap.get("master"))
					.setConnectTimeout(Integer.valueOf(properties.getConnectTimeout()));
		}
		return Redisson.create(config);
	}

}
