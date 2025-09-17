package com.pyramix.domain.entity;

public enum Enm_TypeProcess {
	shearing(0), slitting(1), forming(2);
	
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
