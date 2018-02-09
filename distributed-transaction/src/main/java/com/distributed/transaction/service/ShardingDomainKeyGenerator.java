package com.distributed.transaction.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface ShardingDomainKeyGenerator {

	static final String TIME_STAMP_FORMAT = "YYMMddkkmmssSS";

	public default String generateTimeStamp() {
		LocalDateTime time = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_STAMP_FORMAT);
		return time.format(formatter).toString();
	}

}
