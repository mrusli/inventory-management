package com.pyramix.domain.entity;

public enum Enm_StatusDocument {
	Normal(0), Batal(1);
	
	private int value;

	private Enm_StatusDocument(int value) {
		this.setValue(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	
}
