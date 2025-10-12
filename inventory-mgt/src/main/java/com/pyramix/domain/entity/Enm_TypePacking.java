package com.pyramix.domain.entity;

public enum Enm_TypePacking {
	coil(0), petian(1), lembaran(2);
	
	private int value;

	private Enm_TypePacking(int value) {
		this.setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}
