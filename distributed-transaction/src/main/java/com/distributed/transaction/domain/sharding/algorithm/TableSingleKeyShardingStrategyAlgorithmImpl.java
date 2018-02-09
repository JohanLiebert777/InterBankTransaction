package com.distributed.transaction.domain.sharding.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.SingleKeyTableShardingAlgorithm;
import com.distributed.transaction.domain.sharding.util.ShardingAlgorithm;

public class TableSingleKeyShardingStrategyAlgorithmImpl
		implements SingleKeyTableShardingAlgorithm<Comparable<String>> {

	private ShardingAlgorithm algorithm;

	public TableSingleKeyShardingStrategyAlgorithmImpl(ShardingAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public String doEqualSharding(Collection<String> availableTargetNames,
			ShardingValue<Comparable<String>> shardingValue) {
		String suffix = shardingAlgorithm(shardingValue.getColumnName(), shardingValue.getValue());
		for (String dsName : availableTargetNames) {
			if (dsName.endsWith(suffix)) {
				return dsName;
			}
		}
		throw new IllegalArgumentException();
	}

	@Override
	public Collection<String> doInSharding(Collection<String> availableTargetNames,
			ShardingValue<Comparable<String>> shardingValue) {
		List<String> dsNames = new ArrayList<>();
		for (Comparable<String> value : shardingValue.getValues()) {
			String suffix = shardingAlgorithm(shardingValue.getColumnName(), value);
			for (String dsName : availableTargetNames) {
				if (dsName.endsWith(suffix)) {
					dsNames.add(dsName);
				}
			}
		}
		return dsNames;
	}

	@Override
	public Collection<String> doBetweenSharding(Collection<String> availableTargetNames,
			ShardingValue<Comparable<String>> shardingValue) {
		List<String> dsNames = new ArrayList<>();
		for (Comparable<String> value : shardingValue.getValues()) {
			String suffix = shardingAlgorithm(shardingValue.getColumnName(), value);
			for (String dsName : availableTargetNames) {
				if (dsName.endsWith(suffix)) {
					dsNames.add(dsName);
				}
			}
		}
		return dsNames;
	}

	private String shardingAlgorithm(String columnName, Comparable<String> shardingValue) {
		String shardingStrVal = String.valueOf(shardingValue);
		Integer shardingIntValue = Integer
				.valueOf(shardingStrVal.substring(shardingStrVal.length() - 2, shardingStrVal.length()));
		int result = algorithm.calculate(shardingIntValue);
		return String.valueOf(result);
	}

}
