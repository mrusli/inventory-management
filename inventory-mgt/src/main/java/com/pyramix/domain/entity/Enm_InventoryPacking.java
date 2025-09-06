package com.pyramix.domain.entity;

public enum Enm_InventoryPacking {
	coil(0), petian(1), lembaran(2);
	
	private int value;

	private Enm_InventoryPacking(int value) {
		this.setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}
