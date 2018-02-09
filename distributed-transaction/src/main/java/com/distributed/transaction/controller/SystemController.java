package com.distributed.transaction.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.distributed.transaction.repository.ShardingTableRouteRepo;

@RestController
@RequestMapping("/system")
public class SystemController {

	@Autowired
	private ShardingTableRouteRepo repo;

	@RequestMapping("/getSharding")
	public Object getShardingTableRoute() {
		return repo.findAll();
	}
}
