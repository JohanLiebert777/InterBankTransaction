package com.distributed.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.distributed.transaction.domain.BankBTimeTable;

public interface BankBTimeTableRepo extends JpaRepository<BankBTimeTable, Long>{

}
