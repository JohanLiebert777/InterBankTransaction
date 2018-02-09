/*package com.distributed.transaction.conf.redis;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@EnableCaching
public class RedisJedisConfig {

	@Autowired
	private RedisProperties properties;

	@Bean
	public RedisClusterConfiguration constructRedisClusterConfig() {
		RedisClusterConfiguration config = new RedisClusterConfiguration();
		for (Map<String, String> nodeProp : properties.getClusterNodes()) {
			RedisNode node = new RedisNode(nodeProp.get("host"), Integer.valueOf(nodeProp.get("port")));
			config.addClusterNode(node);
		}
		config.setMaxRedirects(Integer.valueOf(properties.getMaxRedirects()));
		return config;
	}

	@Bean
	public JedisPoolConfig constructJedisPoolConfig() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(Integer.valueOf(properties.getMaxTotal()));
		config.setMaxIdle(Integer.valueOf(properties.getMaxIdle()));
		config.setMaxWaitMillis(Integer.valueOf(properties.getMaxWaitMillis()));
		return config;
	}

	@Bean
	public JedisConnectionFactory generateJedisConnFactory() {
		JedisConnectionFactory factory = new JedisConnectionFactory(constructRedisClusterConfig(),
				constructJedisPoolConfig());
		factory.setUsePool(true);
		factory.setTimeout(Integer.valueOf(properties.getFactoryTimeOut()));
		return factory;
	}

	@Bean
	public RedisTemplate<String, Object> generateRedisTemplate(JedisConnectionFactory factory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);
		return template;
	}

	@Bean
	public CacheManager cacheManager(RedisTemplate<String, Object> redisTemplate) {
		RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
		rcm.setDefaultExpiration(Long.valueOf(properties.getCacheExpiration()));
		return rcm;
	}

}
*/