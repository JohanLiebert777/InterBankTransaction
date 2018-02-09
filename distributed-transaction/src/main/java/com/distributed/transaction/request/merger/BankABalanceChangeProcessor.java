package com.distributed.transaction.request.merger;

import java.util.List;

import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.distributed.transaction.service.BankATransactionService;

@Component("bankABalanceChangeProcessor")
public class BankABalanceChangeProcessor implements RequestProcessor<BalanceChangeRequest> {

	@Autowired
	private BankATransactionService bankATransactionService;

	@Override
	public void process(List<BalanceChangeRequest> list) {
		try {
			bankATransactionService.moveToBankBFromBankA(list);
		} catch (MQClientException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
