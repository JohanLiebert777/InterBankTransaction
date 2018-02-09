package com.distributed.transaction.service.impl;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.distributed.transaction.conf.rocketmq.RocketmqReceivedMsgEvent;
import com.distributed.transaction.message.MoneyTransferMessageContent;
import com.distributed.transaction.request.merger.BalanceChangeRequest;
import com.distributed.transaction.request.merger.RequestMerger;
import com.distributed.transaction.service.BankBRecoverService;
import com.distributed.transaction.service.BankBUserHistOperationService;

@Service("BankBRecoverService")
public class BankBRecoverServiceImpl implements BankBRecoverService {

	@Autowired
	@Qualifier("bankBRequestMerger")
	private RequestMerger requestMerger;

	@Autowired
	private BankBUserHistOperationService operationService;

	@Override
	@EventListener(condition = "#event.direction=='SEND_TO_BANK_B_FROM_BANK_A' && #event.operation=='TRANSFER_MONEY'")
	public void cooperateWithBankA(RocketmqReceivedMsgEvent event) {
		event.setMessageEventReceived(true);
		try {
			if (!(event.getContent() instanceof MoneyTransferMessageContent)) {
				throw new IllegalArgumentException("Message content is not MoneyTransferMessageContent");
			}
			MoneyTransferMessageContent messageContent = (MoneyTransferMessageContent) event.getContent();
			if (operationService.findByCorrelativeOperationId(new BigInteger(event.getKey())) != null) {
				return;
			}

			BigInteger correlativeOperationId = new BigInteger(event.getKey());
			operationService.logOneOperation(messageContent.getUserName(), messageContent.getMoney(),
					correlativeOperationId);

			BalanceChangeRequest req = new BalanceChangeRequest();
			req.setUserName(messageContent.getUserName());
			req.setValue(messageContent.getMoney());
			requestMerger.add(req);
		} catch (Exception e) {
			event.setConsumeSuccess(false);
			throw e;
		}
		event.setConsumeSuccess(true);
	}

}
