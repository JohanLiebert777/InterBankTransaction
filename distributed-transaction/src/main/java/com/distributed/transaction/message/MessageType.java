package com.distributed.transaction.message;

public class MessageType {

	private MessageType() {
	}

	public enum Direction {
		BANK_A_TO_BANK_B("SEND_TO_BANK_B_FROM_BANK_A"), BANK_B_TO_BANK_A("SEND_TO_BANK_A_FROM_BANK_B");
		private String direction;

		private Direction(String direction) {
			this.direction = direction;
		}

		public String getDirection() {
			return direction;
		}
	}

	public enum OperationType {
		TRANSFER_MONEY(1L, "TRANSFER_MONEY"), REMOVE_ACCOUNT(2L, "REMOVE_ACCOUNT"), RECOVER_TRANSACTION(3L,
				"RECOVER_TRANSACTION");
		private String operation;
		private Long opeartionId;

		private OperationType(Long opeartionId, String operation) {
			this.opeartionId = opeartionId;
			this.operation = operation;
		}

		public String getOpeartion() {
			return operation;
		}

		public Long getOpeartionId() {
			return opeartionId;
		}

	}

}