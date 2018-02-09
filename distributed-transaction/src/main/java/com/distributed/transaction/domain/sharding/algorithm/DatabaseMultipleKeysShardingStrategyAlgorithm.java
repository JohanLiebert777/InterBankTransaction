package com.distributed.transaction.domain.sharding.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.MultipleKeysDatabaseShardingAlgorithm;
import com.distributed.transaction.domain.sharding.util.ShardingAlgorithm;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

public class DatabaseMultipleKeysShardingStrategyAlgorithm implements MultipleKeysDatabaseShardingAlgorithm {

	private ShardingAlgorithm algorithm;

	public DatabaseMultipleKeysShardingStrategyAlgorithm(ShardingAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public Collection<String> doSharding(Collection<String> availableTargetNames,
			Collection<ShardingValue<?>> shardingValues) {
		Map<String, Set<Integer>> valueMap = composeShardingValueMap(shardingValues);
		List<String> result = new ArrayList<>();
		Set<List<Integer>> valueResult = Sets
				.cartesianProduct((List<? extends Set<? extends Integer>>) valueMap.values());
		for (List<Integer> value : valueResult) {
			String suffix = Joiner.on("").join(algorithm.calculate(value.get(0)), algorithm.calculate(value.get(1)));
			for (String dsName : availableTargetNames) {
				if (dsName.endsWith(suffix)) {
					result.add(dsName);
				}
			}
		}
		return result;
	}

	private Map<String, Set<Integer>> composeShardingValueMap(final Collection<ShardingValue<?>> shardingValues) {
		Map<String, Set<Integer>> valueMap = new HashMap<>();

		Set<String> shardingColumns = new HashSet<>();
		for (ShardingValue<?> value : shardingValues) {
			shardingColumns.add(value.getColumnName());
		}

		for (String column : shardingColumns) {
			Set<Integer> values = new HashSet<>();
			valueMap.put(column, values);
			for (ShardingValue<?> value : shardingValues) {
				if (value.getColumnName().equals(column)) {
					switch (value.getType()) {
					case SINGLE:
						values.add(getTargetDigital(String.valueOf(value)));
						break;
					case LIST:
						values.addAll(getTargetDigitals(value.getValues()));
						break;
					case RANGE:
						for (Integer inBetVal = getTargetDigital(
								String.valueOf(value.getValueRange().lowerEndpoint())); inBetVal <= getTargetDigital(
										String.valueOf(value.getValueRange().upperEndpoint())); inBetVal++) {
							values.add(inBetVal);
						}
						break;
					default:
						throw new UnsupportedOperationException();
					}
				}
			}
		}
		return valueMap;
	}

	private List<Integer> getTargetDigitals(Collection<?> values) {
		List<Integer> results = new ArrayList<>();
		for (Object value : values) {
			results.add((getTargetDigital(String.valueOf(value))));
		}
		return results;
	}

	private int getTargetDigital(String value) {
		Integer shardingIntValue = Integer.valueOf(value.substring(value.length() - 2, value.length()));
		return shardingIntValue;
	}

}
