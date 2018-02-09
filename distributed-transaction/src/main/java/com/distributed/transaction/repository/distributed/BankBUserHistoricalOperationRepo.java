package com.distributed.transaction.repository.distributed;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.distributed.transaction.domain.distributed.BankBUserHistoricalOperation;

public interface BankBUserHistoricalOperationRepo extends JpaRepository<BankBUserHistoricalOperation, BigInteger> {

	List<BankBUserHistoricalOperation> findByUserId(BigInteger userId);

	BankBUserHistoricalOperation findByCorrelativeOperationId(BigInteger correlativeOperationId);

	@Query("SELECT o FROM BankBUserHistoricalOperation o WHERE o.createdDate > :date")
	List<BankBUserHistoricalOperation> findAllLaterThanDate(@Param("date") Date date);
}
