package com.pyramix.domain.entity;

public enum Enm_StatusInventory {
	incoming(0), process(1), ready(2), sold(3), bukapeti(4), transfer(5);
	
	private int value;

	private Enm_StatusInventory(int value) {
		this.setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
