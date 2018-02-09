package com.distributed.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.distributed.transaction.domain.BankATimeTable;

public interface BankATimeTableRepo extends JpaRepository<BankATimeTable, Long> {

}
