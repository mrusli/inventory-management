package com.pyramix.domain.entity;

public enum Enm_TypeDocument {
	PROCESS_ORDER(0), TRANSFER_ORDER(1), BUKAPETI_ORDER(2), DELIVERY_ORDER(3), 
		CUSTOMER_ORDER(4), SURATJALAN(5), FAKTUR(6), CUSTOMER(7), EMPLOYEE(8),
			SETTLEMENT(9), RECEIVABLE(10), NON_PPN_ORDER(11), NON_PPN_SURATJALAN(12),
				NON_PPN_FAKTUR(13), PROCESS_ORDER_COMPLETE(14), PURCHASE_ORDER(15), 
					PAYABLE(16), QUOTATION(18), KWITANSI(19);

	private int value;

	private Enm_TypeDocument(int value) {
		this.setValue(value);
	}

	public String toCode(int value) {
		switch (value) {
			case 0: return "PR";
			case 1: return "TR";
			case 2: return "BP";
			case 3: return "DO";
			case 4: return "CO";
			case 5: return "SJ";
			case 6: return "FK";
			case 7: return "CR";
			case 8: return "EE";
			case 9: return "SM";
			case 10: return "RV";
			case 11: return "NP";
			case 12: return "NS";
			case 13: return "NF";
			case 14: return "PC";
			case 15: return "PO";
			case 16: return "PY";
			case 18: return "QT";
			case 19: return "KW";
			default:
				return null;
		}
	}
	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	
}
