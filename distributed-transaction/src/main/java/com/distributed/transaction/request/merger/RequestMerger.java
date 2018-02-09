package com.distributed.transaction.request.merger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class RequestMerger {

	private final RequestQueue[] requestQueueArray;

	private AtomicInteger index;

	private static final Random ramdom = new Random();

	private static final int seed = 50;

	private static ScheduledExecutorService timer = new ScheduledThreadPoolExecutor(1);

	private static ExecutorService pool = Executors.newCachedThreadPool();

	public RequestMerger(int bufferSize, int flushInterval, int threads,
			RequestProcessor<BalanceChangeRequest> processor) {
		this.requestQueueArray = new RequestQueue[threads];
		if (threads > 1) {
			index = new AtomicInteger();
		}
		for (int count = 0; count < threads; count++) {
			final RequestQueue requestQueue = new RequestQueue(count, bufferSize, flushInterval, processor);
			requestQueueArray[count] = requestQueue;
			pool.submit(requestQueue);
			timer.scheduleAtFixedRate(requestQueue::timeOutTrigger, ramdom.nextInt(seed), flushInterval,
					TimeUnit.MILLISECONDS);
		}
	}

	public boolean add(BalanceChangeRequest item) {
		int len = requestQueueArray.length;
		RequestQueue rq = null;
		if (len == 1) {
			rq = requestQueueArray[0];
			return rq.add(item);
		}
		int mod = index.incrementAndGet() % len;
		rq = requestQueueArray[mod];
		return rq.add(item);
	}

	private static class RequestQueue implements Runnable {

		private final long id;

		private final int bufferSize;

		private int flushInterval;

		private volatile long lastFlushTime;

		private volatile Thread writer;

		private final BlockingQueue<BalanceChangeRequest> workQueue;

		private final RequestProcessor<BalanceChangeRequest> processor;

		public RequestQueue(long id, int bufferSize, int flushInterval,
				RequestProcessor<BalanceChangeRequest> processor) {
			this.id = id;
			this.bufferSize = bufferSize;
			this.flushInterval = flushInterval;
			this.lastFlushTime = System.currentTimeMillis();
			this.processor = processor;
			this.workQueue = new LinkedBlockingQueue<>();
		}

		public boolean add(BalanceChangeRequest item) {
			boolean rlt = workQueue.offer(item);
			bufferSizeTrigger();
			return rlt;
		}

		private void bufferSizeTrigger() {
			if (reachBufferSize()) {
				unpark();
			}
		}

		private void unpark() {
			if (!workQueue.isEmpty()) {
				LockSupport.unpark(writer);
			}
		}

		public void timeOutTrigger() {
			if (reachTimeout()) {
				unpark();
			}
		}

		public void flushAndProcess() {
			lastFlushTime = System.currentTimeMillis();
			List<BalanceChangeRequest> temp = new ArrayList<>(bufferSize);
			workQueue.drainTo(temp, bufferSize);
			if (temp.isEmpty()) {
				return;
			}
			processor.process(temp);
		}

		private boolean reachBufferSize() {
			return workQueue.size() >= bufferSize;
		}

		private boolean reachTimeout() {
			return System.currentTimeMillis() - lastFlushTime >= flushInterval;
		}

		@Override
		public void run() {
			writer = Thread.currentThread();
			writer.setName(String.valueOf(id));
			while (!writer.isInterrupted()) {
				if (!(reachBufferSize() || reachTimeout())) {
					LockSupport.park(this);
				}
				flushAndProcess();
			}
		}
	}

}
