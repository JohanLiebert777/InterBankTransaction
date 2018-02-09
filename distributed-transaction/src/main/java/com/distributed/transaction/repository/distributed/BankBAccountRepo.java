package com.distributed.transaction.repository.distributed;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.distributed.transaction.domain.distributed.BankBAccount;

public interface BankBAccountRepo extends JpaRepository<BankBAccount, BigInteger> {

	List<BankBAccount> findByUserName(String userName);

	@Modifying
	@Query("update BankBAccount o set o.balance = o.balance + :add where o.userName = :userName")
	void findByUserNameAndAddBalance(@Param(value = "userName") String userName,
			@Param(value = "add") BigDecimal add);
}
