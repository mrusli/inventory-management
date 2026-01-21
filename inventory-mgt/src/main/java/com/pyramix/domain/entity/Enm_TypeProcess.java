package com.pyramix.domain.entity;

public enum Enm_TypeProcess {
	Shearing(0), Slitting(1);
	
	private int value;

	Enm_TypeProcess(int value) {
		this.setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	
}
