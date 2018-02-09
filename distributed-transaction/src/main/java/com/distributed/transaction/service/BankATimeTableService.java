package com.distributed.transaction.service;

import com.distributed.transaction.domain.BankATimeTable;

public interface BankATimeTableService {

	public BankATimeTable findById(Long id);

	public BankATimeTable save(BankATimeTable timeTable);

}
