package com.distributed.transaction.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.distributed.transaction.domain.BankATimeTable;
import com.distributed.transaction.repository.BankATimeTableRepo;
import com.distributed.transaction.service.BankATimeTableService;

@Service
public class BankATimeTableServiceImpl implements BankATimeTableService {

	@Autowired
	private BankATimeTableRepo repo;

	@Override
	public BankATimeTable findById(Long id) {
		return repo.findOne(id);
	}

	@Override
	public BankATimeTable save(BankATimeTable timeTable) {
		return repo.save(timeTable);
	}

}
