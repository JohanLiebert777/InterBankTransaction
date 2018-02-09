package com.distributed.transaction.conf.stream.limiter;

public class RequestBlockedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8231763846260240911L;

	private String rootcause;

	public RequestBlockedException(String rootcause) {
		this.rootcause = rootcause;
	}

	public String getRootcause() {
		return rootcause;
	}

	public void setRootcause(String rootcause) {
		this.rootcause = rootcause;
	}

}
