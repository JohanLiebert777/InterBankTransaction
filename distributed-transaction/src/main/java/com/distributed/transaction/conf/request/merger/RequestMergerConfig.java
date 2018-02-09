package com.distributed.transaction.conf.request.merger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.distributed.transaction.request.merger.BankABalanceChangeProcessor;
import com.distributed.transaction.request.merger.BankBBalanceChangeProcessor;
import com.distributed.transaction.request.merger.RequestMerger;

@Configuration
public class RequestMergerConfig {

	@Autowired
	private BankABalanceChangeProcessor bankAProcessor;

	@Autowired
	private BankBBalanceChangeProcessor bankBProcessor;

	@Bean("bankARequestMerger")
	public RequestMerger createRequestMergerForBankA() {
		return new RequestMerger(200, 300, 5, bankAProcessor);
	}

	@Bean("bankBRequestMerger")
	public RequestMerger createRequestMergerForBankB() {
		return new RequestMerger(200, 300, 5, bankBProcessor);
	}

}
