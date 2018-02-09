package com.distributed.transaction.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.distributed.transaction.domain.ShardingTableRoute;
import com.distributed.transaction.repository.ShardingTableRouteRepo;
import com.distributed.transaction.service.ShardingTableRouteService;

@Service
public class ShardingTableRouteServiceImpl implements ShardingTableRouteService {

	@Autowired
	private ShardingTableRouteRepo routeRepo = null;

	@Override
	public List<String> getDataSourceNames() {
		List<String> dataSourceNames = new ArrayList<>();
		for (ShardingTableRoute route : routeRepo.findAll()) {
			dataSourceNames.add(route.getDbName());
		}
		return dataSourceNames;
	}

	@Override
	public List<ShardingTableRoute> findAll() {
		return routeRepo.findAll();
	}

	@Override
	public Map<String, DataSource> getDataSourceMap() {
		Map<String, DataSource> dataSourceMap = new HashMap<>();
		List<ShardingTableRoute> routes = routeRepo.findAll();
		for (ShardingTableRoute route : routes) {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName(route.getDbType());
			dataSource.setUrl(route.getUrl());
			dataSource.setUsername(route.getUserName());
			dataSource.setPassword(route.getPassword());
			dataSourceMap.put(route.getDbName(), dataSource);
		}
		return dataSourceMap;
	}

}
