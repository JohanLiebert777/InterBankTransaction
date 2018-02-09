package com.distributed.transaction.repository.distributed;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.distributed.transaction.domain.distributed.BankAAccount;

public interface BankAAccountRepo extends JpaRepository<BankAAccount, BigInteger> {

	List<BankAAccount> findByUserName(String userName);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select o from BankAAccount o where o.userName = :userName")
	List<BankAAccount> findByUserNameForUpdate(@Param(value = "userName") String userName);

	@Modifying
	@Query("update BankAAccount o set o.balance = o.balance - :reduce where o.userName = :userName and o.balance >= :reduce")
	void findByUserNameAndReduceBalance(@Param(value = "userName") String userName,
			@Param(value = "reduce") BigDecimal reduce);

}
