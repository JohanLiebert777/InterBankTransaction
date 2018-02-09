package com.distributed.transaction.repository.distributed;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.distributed.transaction.domain.distributed.BankAUserHistoricalOperation;

public interface BankAUserHistoricalOperationRepo extends JpaRepository<BankAUserHistoricalOperation, BigInteger> {

	List<BankAUserHistoricalOperation> findByUserId(BigInteger userId);

	@Query("SELECT o FROM BankAUserHistoricalOperation o WHERE o.createdDate > :startDate AND o.createdDate < :endDate")
	List<BankAUserHistoricalOperation> findAllBetweenStartDateAndEndDate(@Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	@Modifying
	@Query("update BankAUserHistoricalOperation o set o.status = :status where o.operationId = :operationId")
	void updateStatusForBankAUserHistoricalOperation(@Param("operationId") BigInteger operationId,
			@Param("status") String status);
}
