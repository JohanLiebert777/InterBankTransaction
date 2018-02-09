package com.distributed.transaction.request.merger;

import java.util.List;

public interface RequestProcessor<T> {

	void process(List<T> list);

}
