package com.distributed.transaction.request.merger;

import java.math.BigDecimal;
import java.util.List;

public class Test {

	private static class Processor implements RequestProcessor<BalanceChangeRequest> {

		@Override
		public void process(List<BalanceChangeRequest> list) {
			System.out.println("*************Start to process!");
		}

	}

	private static class Task implements Runnable {

		public RequestMerger rm;
		public int from;
		public int to;

		@Override
		public void run() {
			System.out.println("*************Thread from " + from +", to "+ to);
			for (int count = from; count < to; count++) {
				BalanceChangeRequest bcr = new BalanceChangeRequest();
				bcr.setUserName("User " + count);
				bcr.setValue(new BigDecimal(count));
				rm.add(bcr);
			}
		}

	}

	public static void main(String[] args) {
		Processor processor = new Processor();
		RequestMerger rm = new RequestMerger(1000, 10000, 1, processor);
		for (int count = 1; count <= 2; count++) {
			Task task = new Task();
			task.from = (count - 1) * 1000;
			task.to = count * 1000;
			task.rm = rm;
			new Thread(task).start();
		}
	}

}
