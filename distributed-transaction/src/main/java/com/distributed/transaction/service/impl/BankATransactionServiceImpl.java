package com.distributed.transaction.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.distributed.transaction.cache.service.BankAAccountCacheService;
import com.distributed.transaction.conf.rocketmq.RocketmqReceivedMsgEvent;
import com.distributed.transaction.conf.rocketmq.SimpleTMQProducerPool;
import com.distributed.transaction.domain.distributed.BankAAccount;
import com.distributed.transaction.domain.distributed.BankAUserHistoricalOperation;
import com.distributed.transaction.domain.distributed.BankBUserHistoricalOperation;
import com.distributed.transaction.message.BankBRecoverMessageContent;
import com.distributed.transaction.message.MessageFactory;
import com.distributed.transaction.message.MessageType;
import com.distributed.transaction.message.MessageType.Direction;
import com.distributed.transaction.message.MessageType.OperationType;
import com.distributed.transaction.message.MoneyTransferMessageContent;
import com.distributed.transaction.request.merger.BalanceChangeRequest;
import com.distributed.transaction.service.BankAAccountService;
import com.distributed.transaction.service.BankATransactionService;
import com.distributed.transaction.service.BankAUserHistOperationService;

@Service
public class BankATransactionServiceImpl implements BankATransactionService {

	@Autowired
	private BankAAccountService bankAAccountService;

	@Autowired
	@Qualifier("simpleTMQProducerPool")
	private SimpleTMQProducerPool simpleTMQProducerPool;

	@Autowired
	private MessageFactory msgFactory;

	@Autowired
	private BankAUserHistOperationService operationService;

	@Autowired
	private BankAAccountCacheService bankAAccontCacheService;

	@Override
	@Transactional(transactionManager = "transactionManagerSecondary", propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
	public List<BankAAccount> moveToBankBFromBankA(List<BalanceChangeRequest> requests)
			throws MQClientException, InterruptedException {

		List<BalanceChangeRequest> mergedReq = mergeRequest(requests);
		for (BalanceChangeRequest req : mergedReq) {
			bankAAccountService.findByUserNameAndReduceBalance(req.getUserName(), req.getValue());
		}
		List<BankAAccount> accounts = new ArrayList<>();
		for (BalanceChangeRequest req : requests) {
			BankAAccount account = bankAAccontCacheService.findByUserName(req.getUserName()).get(0);
			accounts.add(account);
			MoneyTransferMessageContent msgContent = new MoneyTransferMessageContent();
			msgContent.setUserName(account.getUserName());
			msgContent.setOperation(
					Direction.BANK_A_TO_BANK_B.getDirection() + OperationType.TRANSFER_MONEY.getOpeartion());
			msgContent.setMoney(req.getValue());
			Message msg = msgFactory.createMoneyTransferMessage(Direction.BANK_A_TO_BANK_B,
					OperationType.TRANSFER_MONEY, req.getOperationHist().getOperationId(),
					JSON.toJSONString(msgContent));
			simpleTMQProducerPool.getProducer().sendMessageInTransaction(msg, req.getOperationHist());
		}
		return accounts;
	}

	private List<BalanceChangeRequest> mergeRequest(final List<BalanceChangeRequest> requests) {
		Map<String, BigDecimal> mergeReq = new HashMap<>();
		for (BalanceChangeRequest request : requests) {
			BigDecimal value = mergeReq.get(request.getUserName());
			mergeReq.put(request.getUserName(), value == null ? request.getValue() : value.add(request.getValue()));
		}
		List<BalanceChangeRequest> results = new ArrayList<>();
		for (String name : mergeReq.keySet()) {
			BalanceChangeRequest request = new BalanceChangeRequest();
			request.setUserName(name);
			request.setValue(mergeReq.get(name));
			results.add(request);
		}
		return results;
	}

	@Override
	@Transactional(transactionManager = "transactionManagerSecondary", propagation = Propagation.REQUIRED, rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
	public BankAAccount moveToBankBFromBankA(String userName, BigDecimal value, BankAUserHistoricalOperation operation)
			throws MQClientException, InterruptedException {
		bankAAccountService.findByUserNameAndReduceBalance(userName, value);
		BankAAccount account = bankAAccontCacheService.findByUserName(userName).get(0);
		MoneyTransferMessageContent msgContent = new MoneyTransferMessageContent();
		msgContent.setUserName(userName);
		msgContent
				.setOperation(Direction.BANK_A_TO_BANK_B.getDirection() + OperationType.TRANSFER_MONEY.getOpeartion());
		msgContent.setMoney(value);
		Message msg = msgFactory.createMoneyTransferMessage(Direction.BANK_A_TO_BANK_B, OperationType.TRANSFER_MONEY,
				operation.getOperationId(), JSON.toJSONString(msgContent));
		simpleTMQProducerPool.getProducer().sendMessageInTransaction(msg, operation);
		return account;
	}

	@Override
	@EventListener(condition = "#event.direction=='SEND_TO_BANK_A_FROM_BANK_B' && #event.operation=='RECOVER_TRANSACTION'")
	public void findMissedTransaction(RocketmqReceivedMsgEvent event) throws Exception {
		event.setMessageEventReceived(true);
		try {
			if (!(event.getContent() instanceof BankBRecoverMessageContent)) {
				throw new IllegalArgumentException("Message content is not BankBRecoverMessageContent");
			}
			BankBRecoverMessageContent messageContent = (BankBRecoverMessageContent) event.getContent();
			List<BankAUserHistoricalOperation> bankAOperationList = operationService.findAllBetweenStartDateAndEndDate(
					messageContent.getBenchmarkStartTime(), messageContent.getBenchmarkEndTime());
			List<BankAUserHistoricalOperation> missingTransactions = findMissingTransaction(
					messageContent.getOperationList(), bankAOperationList);
			if (missingTransactions.isEmpty()) {
				return;
			}
			Map<Message, BankAUserHistoricalOperation> messageMap = constructRecoverMessageMap(missingTransactions);
			simpleTMQProducerPool.getProducer().sendMessageInTransaction(messageMap);
		} catch (Exception e) {
			event.setConsumeSuccess(false);
			throw e;
		}
		event.setConsumeSuccess(true);
	}

	private Map<Message, BankAUserHistoricalOperation> constructRecoverMessageMap(
			List<BankAUserHistoricalOperation> missingTransactions) {
		Map<Message, BankAUserHistoricalOperation> messageMap = new HashMap<>();
		for (BankAUserHistoricalOperation bankAUserHistOp : missingTransactions) {
			BigDecimal value = bankAUserHistOp.getAmount().negate();
			BankAAccount account = bankAAccountService.findByUserId(bankAUserHistOp.getUserId());

			BankAUserHistoricalOperation opeartion = new BankAUserHistoricalOperation();
			opeartion.setOperationTypeId(OperationType.TRANSFER_MONEY.getOpeartionId());
			opeartion.setUserId(account.getUserId());
			opeartion.setAmount(value.negate());
			opeartion.setReason("Move " + value + " from Bank A to Bank B");
			opeartion.setCreatedBy(account.getUserName());
			opeartion.setCreatedDate(new Date());

			MoneyTransferMessageContent msgContent = new MoneyTransferMessageContent();
			msgContent.setUserName(account.getUserName());
			msgContent.setOperation(
					Direction.BANK_A_TO_BANK_B.getDirection() + OperationType.TRANSFER_MONEY.getOpeartion());
			msgContent.setMoney(bankAUserHistOp.getAmount().negate());
			Message msg = msgFactory.createMoneyTransferMessage(Direction.BANK_A_TO_BANK_B,
					OperationType.TRANSFER_MONEY, opeartion.getOperationId(), JSON.toJSONString(msgContent));
			messageMap.put(msg, opeartion);
		}
		return messageMap;
	}

	private List<BankAUserHistoricalOperation> findMissingTransaction(
			List<BankBUserHistoricalOperation> bankBOperationList,
			List<BankAUserHistoricalOperation> bankAOperationList) {
		List<BankAUserHistoricalOperation> missingTransactions = new ArrayList<>();
		for (BankAUserHistoricalOperation bankAUserHistOp : bankAOperationList) {
			boolean foundInBankB = false;
			for (BankBUserHistoricalOperation bankBUserHistOp : bankBOperationList) {
				if (bankAUserHistOp.getOperationId().equals(bankBUserHistOp.getCorrelativeOperationId())) {
					bankAUserHistOp.setModifiedBy(MessageType.OperationType.RECOVER_TRANSACTION.getOpeartion());
					bankAUserHistOp.setModifiedDate(new Date());
					operationService.saveOrUpdate(bankAUserHistOp);
					foundInBankB = true;
					break;
				}
			}
			if (!foundInBankB) {
				missingTransactions.add(bankAUserHistOp);
			}
		}
		return missingTransactions;
	}

}
