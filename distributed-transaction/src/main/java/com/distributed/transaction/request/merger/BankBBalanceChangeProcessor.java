package com.distributed.transaction.request.merger;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.distributed.transaction.service.BankBTransactionService;

@Component("bankBBalanceChangeProcessor")
public class BankBBalanceChangeProcessor implements RequestProcessor<BalanceChangeRequest> {

	@Autowired
	private BankBTransactionService bankBTransactionService;

	@Override
	public void process(List<BalanceChangeRequest> list) {
		bankBTransactionService.modifyBalance(list);
	}

}
