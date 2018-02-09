package com.distributed.transaction.domain.sharding;

import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;

public interface DomainShardingRule {

	public TableRule getTableRule();

}
