package com.pyramix.domain.entity;

public enum Enm_TypePayment {
	bank(0), giro(1), tunai(2);
	
	private int value;

	private Enm_TypePayment(int value) {
		this.setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
