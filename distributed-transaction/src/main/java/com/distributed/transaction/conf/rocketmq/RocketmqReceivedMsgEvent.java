package com.distributed.transaction.conf.rocketmq;

import com.distributed.transaction.message.MessageContent;

public class RocketmqReceivedMsgEvent {

	private String direction;

	private String operation;

	private String key;

	private MessageContent content;

	private boolean messageEventReceived = false;

	private boolean consumeSuccess;

	private MessageConsumeFailException exception;

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public MessageContent getContent() {
		return content;
	}

	public void setContent(MessageContent content) {
		this.content = content;
	}

	public boolean isConsumeSuccess() {
		return consumeSuccess;
	}

	public void setConsumeSuccess(boolean consumeSuccess) {
		this.consumeSuccess = consumeSuccess;
	}

	public boolean isMessageEventReceived() {
		return messageEventReceived;
	}

	public void setMessageEventReceived(boolean messageEventReceived) {
		this.messageEventReceived = messageEventReceived;
	}

	public MessageConsumeFailException getException() {
		return exception;
	}

	public void setException(MessageConsumeFailException exception) {
		this.exception = exception;
	}

}
