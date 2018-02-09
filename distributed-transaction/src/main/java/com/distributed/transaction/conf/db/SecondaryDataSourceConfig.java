package com.distributed.transaction.conf.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dangdang.ddframe.rdb.sharding.api.ShardingDataSourceFactory;
import com.dangdang.ddframe.rdb.sharding.api.rule.DataSourceRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.distributed.transaction.domain.sharding.AbstractDomainSharding;
import com.distributed.transaction.domain.sharding.BankAAccountSharding;
import com.distributed.transaction.domain.sharding.BankAUserHistoricalOperationSharding;
import com.distributed.transaction.domain.sharding.BankBAccountSharding;
import com.distributed.transaction.domain.sharding.BankBUserHistoricalOperationSharding;
import com.distributed.transaction.service.ShardingTableRouteService;

@Configuration
@AutoConfigureAfter(PrimaryDataSourceConfig.class)
public class SecondaryDataSourceConfig {

	@Autowired
	private ShardingTableRouteService routeService;

	@Bean(name = "secondaryDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.secondary")
	public DataSource getSecondaryDataSource() throws SQLException {
		return buildShardingDataSource();
	}

	private DataSource buildShardingDataSource() throws SQLException {
		Map<String, DataSource> dataSourceMap = routeService.getDataSourceMap();
		DataSourceRule dataSourceRule = new DataSourceRule(dataSourceMap, dataSourceMap.keySet().iterator().next());
		List<TableRule> tableRules = new ArrayList<>();
		for (AbstractDomainSharding domainSharding : getShardingCollection(routeService)) {
			tableRules.add(domainSharding.getTableRule());
		}
		ShardingRule shardingRule = ShardingRule.builder().dataSourceRule(dataSourceRule).tableRules(tableRules)
				.build();
		return ShardingDataSourceFactory.createDataSource(shardingRule);
	}

	private List<AbstractDomainSharding> getShardingCollection(ShardingTableRouteService routeService) {
		List<AbstractDomainSharding> col = new ArrayList<>();

		BankAAccountSharding bankAAccount = new BankAAccountSharding();
		bankAAccount.setRouteService(routeService);
		col.add(bankAAccount);

		BankAUserHistoricalOperationSharding bankAUserHist = new BankAUserHistoricalOperationSharding();
		bankAUserHist.setRouteService(routeService);
		col.add(bankAUserHist);

		BankBAccountSharding bankBAccount = new BankBAccountSharding();
		bankBAccount.setRouteService(routeService);
		col.add(bankBAccount);

		BankBUserHistoricalOperationSharding bankBUserHist = new BankBUserHistoricalOperationSharding();
		bankBUserHist.setRouteService(routeService);
		col.add(bankBUserHist);

		return col;
	}

}
