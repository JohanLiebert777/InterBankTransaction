package com.distributed.transaction.domain.sharding.util;

public final class ShardingAlgorithm {

	public enum Algorithm {
		MOD
	}

	private static int mod(final int x, final int y) {
		return Math.floorMod(x, y);
	}

	private Algorithm algorithm;

	private int rightValue;

	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public int getRightValue() {
		return rightValue;
	}

	public void setRightValue(int rightValue) {
		this.rightValue = rightValue;
	}

	public int calculate(int leftValue) {
		int result = 0;
		switch (algorithm) {
		case MOD:
			result = mod(leftValue, rightValue);
			break;
		default:
			break;
		}
		return result;
	}
}
