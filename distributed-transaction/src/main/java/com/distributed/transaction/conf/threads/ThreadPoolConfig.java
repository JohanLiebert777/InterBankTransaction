package com.distributed.transaction.conf.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@Configurable
public class ThreadPoolConfig {

	private static final int corePoolSize;

	static {
		corePoolSize = 10;
	}

	@Bean("defaultThreadPool")
	@Scope("singleton")
	public ExecutorService getDefaultThreadPool() {
		return Executors.newFixedThreadPool(corePoolSize);
	}

	@Bean("scheduledThreadPool")
	@Scope("singleton")
	public ScheduledExecutorService getScheduledThreadPool() {
		return Executors.newScheduledThreadPool(corePoolSize);
	}

}
