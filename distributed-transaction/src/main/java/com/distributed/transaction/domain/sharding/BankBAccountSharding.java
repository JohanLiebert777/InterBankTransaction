package com.distributed.transaction.domain.sharding;

import java.util.List;

import com.dangdang.ddframe.rdb.sharding.api.strategy.database.DatabaseShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;
import com.distributed.transaction.domain.distributed.BankBAccount;
import com.distributed.transaction.domain.sharding.algorithm.DatabaseSingleKeyShardingStrategyAlgorithmImpl;
import com.distributed.transaction.domain.sharding.algorithm.TableSingleKeyShardingStrategyAlgorithmImpl;
import com.distributed.transaction.domain.sharding.util.ActualResourceNameInterpreter;
import com.distributed.transaction.domain.sharding.util.ShardingAlgorithm;

public class BankBAccountSharding extends AbstractDomainSharding {

	private static final int USER_ID_COL_INDEX = 0;
//	private static final int GEO_ID_COL_INDEX = 2;
	private static final int TOTAL_TABLE_SHARDING_NUM = 2;

	private static final String TABLE_NAME = ActualResourceNameInterpreter.getTableName(BankBAccount.class);
	private static final String USER_ID_COL_NAME = ActualResourceNameInterpreter.getColumnName(BankBAccount.class,
			USER_ID_COL_INDEX);
//	private static final String GEO_ID_COL_NAME = ActualResourceNameInterpreter.getColumnName(BankBAccount.class,
//			GEO_ID_COL_INDEX);

	@Override
	protected int getTotalTableShardingNum() {
		return TOTAL_TABLE_SHARDING_NUM;
	}

	@Override
	protected int getTotalDatasourceShardingNum() {
		return getRouteService().getDataSourceNames().size();
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected List<String> getDataSourceNames() {
		return getRouteService().getDataSourceNames();
	}

	@Override
	protected TableShardingStrategy getTableShardingStrategy() {
		ShardingAlgorithm algorithm = new ShardingAlgorithm();
		algorithm.setRightValue(TOTAL_TABLE_SHARDING_NUM);
		algorithm.setAlgorithm(ShardingAlgorithm.Algorithm.MOD);
		return new TableShardingStrategy(USER_ID_COL_NAME, new TableSingleKeyShardingStrategyAlgorithmImpl(algorithm));
	}

	@Override
	protected DatabaseShardingStrategy getDatabaseShardingStrategy() {
		ShardingAlgorithm algorithm = new ShardingAlgorithm();
		algorithm.setRightValue(getRouteService().getDataSourceNames().size());
		algorithm.setAlgorithm(ShardingAlgorithm.Algorithm.MOD);
		return new DatabaseShardingStrategy(USER_ID_COL_NAME,
				new DatabaseSingleKeyShardingStrategyAlgorithmImpl(algorithm));
	}

	@Override
	protected DomainShardingRule getDomainShardingRule() {
		return new StandardDomainShardingRule.Builder().setTableName(getTableName())
				.setTotalTableShardingNum(TOTAL_TABLE_SHARDING_NUM).setDatasourceNames(getDataSourceNames())
				.setDbShardingStrategy(getDatabaseShardingStrategy())
				.setTableShardingStrategy(getTableShardingStrategy()).build();
	}
}
