package com.distributed.transaction.service;

import com.distributed.transaction.conf.rocketmq.RocketmqReceivedMsgEvent;

public interface BankBRecoverService {

	void cooperateWithBankA(RocketmqReceivedMsgEvent event);

}
