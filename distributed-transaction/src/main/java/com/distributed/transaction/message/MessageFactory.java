package com.distributed.transaction.message;

import java.math.BigInteger;

import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageFactory {

	public Message createMoneyTransferMessage(MessageType.Direction direction, MessageType.OperationType operation,
			BigInteger key, String content) {
		return new Message(direction.getDirection(), operation.getOpeartion(), String.valueOf(key), content.getBytes());
	}

	public Message createBankBRecoverMessage(MessageType.Direction direction, MessageType.OperationType operation,
			String content) {
		return new Message(direction.getDirection(), operation.getOpeartion(), content.getBytes());
	}

}
