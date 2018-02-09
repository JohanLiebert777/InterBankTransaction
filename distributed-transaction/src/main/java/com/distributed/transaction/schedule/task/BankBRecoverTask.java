package com.distributed.transaction.schedule.task;

import java.util.Date;
import java.util.List;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.distributed.transaction.conf.rocketmq.SimpleTMQProducerPool;
import com.distributed.transaction.domain.BankBTimeTable;
import com.distributed.transaction.domain.distributed.BankBUserHistoricalOperation;
import com.distributed.transaction.message.BankBRecoverMessageContent;
import com.distributed.transaction.message.MessageFactory;
import com.distributed.transaction.message.MessageType.Direction;
import com.distributed.transaction.message.MessageType.OperationType;
import com.distributed.transaction.repository.BankBTimeTableRepo;
import com.distributed.transaction.service.BankBUserHistOperationService;

@Component
@Configurable
@EnableScheduling
public class BankBRecoverTask {

	@Autowired
	private BankBTimeTableRepo bankBTimeTableRepo;

	@Autowired
	private BankBUserHistOperationService operationService;

	@Autowired
	@Qualifier("simpleTMQProducerPool")
	private SimpleTMQProducerPool simpleTMQProducerPool;

	@Autowired
	private MessageFactory msgFactory;

	@Scheduled(cron = "0 0/5 * * * *")
	public void sendOperationForDoubleCheck() throws MQClientException, InterruptedException {
		Date currentTime = new Date();
		List<BankBTimeTable> allTimeTableTable = bankBTimeTableRepo.findAll();
		BankBTimeTable timeTable;
		Date lastTime = currentTime;
		if (allTimeTableTable.isEmpty()) {
			timeTable = new BankBTimeTable();
			timeTable.setNewUpdatedTime(currentTime);
			timeTable.setCreatedBy("System");
			timeTable.setCreatedDate(currentTime);
		} else {
			timeTable = allTimeTableTable.get(0);
			lastTime = timeTable.getNewUpdatedTime();
			timeTable.setNewUpdatedTime(currentTime);
		}
		bankBTimeTableRepo.save(timeTable);
		List<BankBUserHistoricalOperation> operationList = operationService.findAllLaterThanDate(lastTime);
		if (operationList.isEmpty()) {
			return;
		}
		BankBRecoverMessageContent msgContent = new BankBRecoverMessageContent();
		msgContent.setBenchmarkStartTime(lastTime);
		msgContent.setBenchmarkEndTime(currentTime);
		msgContent.setOperationList(operationList);
		Message msg = msgFactory.createBankBRecoverMessage(Direction.BANK_B_TO_BANK_A,
				OperationType.RECOVER_TRANSACTION, JSON.toJSONString(msgContent));

		simpleTMQProducerPool.getProducer().sendMessageInTransaction(msg, operationList);
	}
}
