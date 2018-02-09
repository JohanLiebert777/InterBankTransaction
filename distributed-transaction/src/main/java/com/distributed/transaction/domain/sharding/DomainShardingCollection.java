/*package com.distributed.transaction.domain.sharding;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DomainShardingCollection {

	@Autowired
	private BankAAccountSharding bankAAccountSharding;

	@Autowired
	private BankBAccountSharding bankBAccountSharding;

	@Autowired
	private BankAUserHistoricalOperationSharding bankAUserHistoricalOperationSharding;

	@Autowired
	private BankBUserHistoricalOperationSharding bankBUserHistoricalOperationSharding;

	public List<AbstractDomainSharding> getDomainShardingCollection() {
		List<AbstractDomainSharding> collection = new ArrayList<>();
		collection.add(bankAAccountSharding);
		collection.add(bankBAccountSharding);
		collection.add(bankAUserHistoricalOperationSharding);
		collection.add(bankBUserHistoricalOperationSharding);
		return collection;
	}
}
*/