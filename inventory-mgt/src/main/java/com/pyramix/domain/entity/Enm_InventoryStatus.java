package com.pyramix.domain.entity;

public enum Enm_InventoryStatus {
	incoming(0), process(1), ready(2), sold(3), bukapeti(4), transfer(5);
	
	private int value;

	private Enm_InventoryStatus(int value) {
		this.setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
