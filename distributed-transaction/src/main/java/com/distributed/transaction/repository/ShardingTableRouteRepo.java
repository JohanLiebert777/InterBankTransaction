package com.distributed.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.distributed.transaction.domain.ShardingTableRoute;

public interface ShardingTableRouteRepo extends JpaRepository<ShardingTableRoute, Long>{

}
