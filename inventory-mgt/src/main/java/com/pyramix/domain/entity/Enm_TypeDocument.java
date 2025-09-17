package com.pyramix.domain.entity;

public enum Enm_TypeDocument {
	PROCESS_ORDER(0), SURATJALAN(5), FAKTUR(6), CUSTOMER(7), EMPLOYEE(8),
		SETTLEMENT(9), RECEIVABLE(10), NON_PPN_ORDER(11), NON_PPN_SURATJALAN(12),
			NON_PPN_FAKTUR(13), PROCESS_ORDER_COMPLETE(14), PURCHASE_ORDER(15), 
				PAYABLE(16), QUOTATION(18);

	private int value;

	private Enm_TypeDocument(int value) {
		this.setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	
}
