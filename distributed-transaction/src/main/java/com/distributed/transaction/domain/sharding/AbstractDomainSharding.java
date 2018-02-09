package com.distributed.transaction.domain.sharding;

import java.util.List;

import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.DatabaseShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;
import com.distributed.transaction.service.ShardingTableRouteService;

public abstract class AbstractDomainSharding {

	protected abstract int getTotalTableShardingNum();

	protected abstract int getTotalDatasourceShardingNum();

	protected abstract String getTableName();

	protected abstract List<String> getDataSourceNames();

	protected abstract TableShardingStrategy getTableShardingStrategy();

	protected abstract DatabaseShardingStrategy getDatabaseShardingStrategy();

	protected abstract DomainShardingRule getDomainShardingRule();

	public TableRule getTableRule() {
		return getDomainShardingRule().getTableRule();
	}

	private ShardingTableRouteService routeService;

	public ShardingTableRouteService getRouteService() {
		return routeService;
	}

	public void setRouteService(ShardingTableRouteService routeService) {
		this.routeService = routeService;
	}

}
