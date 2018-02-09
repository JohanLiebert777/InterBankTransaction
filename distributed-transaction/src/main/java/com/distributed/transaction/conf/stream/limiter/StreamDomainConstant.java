package com.distributed.transaction.conf.stream.limiter;

public enum StreamDomainConstant {

	TRANSFER("transfer"), ACCOUNT("account"), MISC("misc"), UNKNOW("unknow");

	private String name;

	StreamDomainConstant(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static StreamDomainConstant findByName(String name) {
		for (StreamDomainConstant constant : StreamDomainConstant.values()) {
			if (constant.getName().equalsIgnoreCase(name)) {
				return constant;
			}
		}
		return UNKNOW;
	}

}
