package com.distributed.transaction.conf.redis;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(ignoreUnknownFields = false, ignoreInvalidFields = false, prefix = "spring.extends.redis")
public class RedisProperties {

	@NotNull
	private List<Map<String, String>> clusterNodes;
	private String maxRedirects;
	private String maxTotal;
	private String maxIdle;
	private String maxWaitMillis;
	private String connectTimeout;
	private String cacheExpiration;

	public List<Map<String, String>> getClusterNodes() {
		return clusterNodes;
	}

	public void setClusterNodes(List<Map<String, String>> clusterNodes) {
		this.clusterNodes = clusterNodes;
	}

	public String getMaxRedirects() {
		return maxRedirects;
	}

	public void setMaxRedirects(String maxRedirects) {
		this.maxRedirects = maxRedirects;
	}

	public String getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(String maxTotal) {
		this.maxTotal = maxTotal;
	}

	public String getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(String maxIdle) {
		this.maxIdle = maxIdle;
	}

	public String getMaxWaitMillis() {
		return maxWaitMillis;
	}

	public void setMaxWaitMillis(String maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	public String getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(String connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public String getCacheExpiration() {
		return cacheExpiration;
	}

	public void setCacheExpiration(String cacheExpiration) {
		this.cacheExpiration = cacheExpiration;
	}

}
