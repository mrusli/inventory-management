package com.pyramix.domain.entity;

public enum Enm_StatusProcess {
	Permohonan(0), Proses(1), Selesai(2), Batal(3);

	private int value;
	
	Enm_StatusProcess(int value) {
		this.setValue(value);
	}

	public String toCode(int value) {
		switch (value) {
			case 0: return "M";
			case 1: return "P";	
			case 2: return "S";
			case 3: return "B";
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
