package com.distributed.transaction.domain.sharding;

import java.util.ArrayList;
import java.util.List;

import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.DatabaseShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;

public class StandardDomainShardingRule implements DomainShardingRule {

	private String tableName;

	private Integer totalTableShardingNum;

	private List<String> dataSourceNames;

	private TableShardingStrategy tableShardingStrategy;

	private DatabaseShardingStrategy dbShardingStrategy;

	private StandardDomainShardingRule(String tableName, Integer totalTableShardingNum, List<String> dataSourceNames,
			TableShardingStrategy tableShardingStrategy, DatabaseShardingStrategy dbShardingStrategy) {
		this.tableName = tableName;
		this.totalTableShardingNum = totalTableShardingNum;
		this.dataSourceNames = dataSourceNames;
		this.tableShardingStrategy = tableShardingStrategy;
		this.dbShardingStrategy = dbShardingStrategy;
	}

	public static class Builder {

		private String tableName;
		private Integer totalTableShardingNum;
		private List<String> dataSourceNames;
		private TableShardingStrategy tableShardingStrategy;
		private DatabaseShardingStrategy dbShardingStrategy;

		public Builder() {
		}

		public Builder setTableName(String tableName) {
			this.tableName = tableName;
			return this;
		}

		public Builder setTotalTableShardingNum(Integer totalTableShardingNum) {
			this.totalTableShardingNum = totalTableShardingNum;
			return this;
		}

		public Builder setDatasourceNames(List<String> dataSourceNames) {
			this.dataSourceNames = dataSourceNames;
			return this;
		}

		public Builder setTableShardingStrategy(TableShardingStrategy tableShardingStrategy) {
			this.tableShardingStrategy = tableShardingStrategy;
			return this;
		}

		public Builder setDbShardingStrategy(DatabaseShardingStrategy dbShardingStrategy) {
			this.dbShardingStrategy = dbShardingStrategy;
			return this;
		}

		public StandardDomainShardingRule build() {
			return new StandardDomainShardingRule(tableName, totalTableShardingNum, dataSourceNames,
					tableShardingStrategy, dbShardingStrategy);
		}

	}

	protected List<String> getActualTables() {
		List<String> actualTables = new ArrayList<>();
		for (int tableIndex = 0; tableIndex < totalTableShardingNum; tableIndex++) {
			for (String dsName : dataSourceNames) {
				if (dsName.endsWith(String.valueOf(tableIndex))) {
					String actualTableName = dsName + "." + tableName + "_" + tableIndex;
					actualTables.add(actualTableName);
				}
			}
		}
		return actualTables;
	}

	@Override
	public TableRule getTableRule() {
		return TableRule.builder(tableName).actualTables(getActualTables()).dataSourceNames(dataSourceNames)
				.tableShardingStrategy(tableShardingStrategy).databaseShardingStrategy(dbShardingStrategy).build();
	}

}
