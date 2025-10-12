package com.pyramix.domain.entity;

public enum Enm_TypeInvoice {
	penjualan(0), normal(1);
	
	private int value;

	private Enm_TypeInvoice(int value) {
		this.setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	
}
