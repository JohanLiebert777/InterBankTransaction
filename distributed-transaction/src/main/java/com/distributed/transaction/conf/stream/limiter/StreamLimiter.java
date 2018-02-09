package com.distributed.transaction.conf.stream.limiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.util.concurrent.RateLimiter;

public class StreamLimiter {

	private StreamLimiter() {
	}

	private static class Singleton {
		private static final StreamLimiter instanceHolder = new StreamLimiter();
	}

	private static final ConcurrentHashMap<StreamDomainConstant, RateLimiter> rateLimiter = new ConcurrentHashMap<>();

	public static StreamLimiter getInstanceAndSetConfig(Map<StreamDomainConstant, Integer> rateMap) {
		for (StreamDomainConstant domain : rateMap.keySet()) {
			if (rateLimiter.containsKey(domain)) {
				rateLimiter.get(domain).setRate(rateMap.get(domain));
			} else {
				RateLimiter limiter = RateLimiter.create(rateMap.get(domain));
				rateLimiter.putIfAbsent(domain, limiter);
			}
		}
		return Singleton.instanceHolder;
	}

	public boolean tryAcquire(StreamDomainConstant domain) {
		return rateLimiter.get(domain).tryAcquire();
	}

}
