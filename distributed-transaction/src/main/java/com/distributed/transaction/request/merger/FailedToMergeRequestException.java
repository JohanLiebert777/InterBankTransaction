package com.distributed.transaction.request.merger;

public class FailedToMergeRequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6710777633920736929L;
	private String rootcause;

	public FailedToMergeRequestException(String rootcause) {
		this.rootcause = rootcause;
	}

	public String getRootcause() {
		return rootcause;
	}

	public void setRootcause(String rootcause) {
		this.rootcause = rootcause;
	}

}
