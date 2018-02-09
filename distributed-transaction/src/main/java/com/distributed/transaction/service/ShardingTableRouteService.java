package com.distributed.transaction.service;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.distributed.transaction.domain.ShardingTableRoute;

public interface ShardingTableRouteService {

	public List<String> getDataSourceNames();
	
	public List<ShardingTableRoute> findAll();
	
	public Map<String, DataSource> getDataSourceMap();

}
