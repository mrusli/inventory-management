package com.pyramix.domain.entity;

public enum Enm_TypeCompany {
	PT(0), PD(1), CV(2), TOKO(3), BANK(4), PRV(5);
	
	private int value;

	private Enm_TypeCompany(int value) {
		this.setValue(value);
	}
	
	public String toString(int value) {
		switch (value) {
			case 0: return "PT";
			case 1: return "PD";
			case 2: return "CV";
			case 3: return "TOKO";
			case 4: return "BANK";
			case 5: return "PRV";
			default:
				return null;
		}
	}

	public Enm_TypeCompany toCompanyType(int value) {
		switch (value) {
			case 0: return Enm_TypeCompany.PT;
			case 1: return Enm_TypeCompany.PD;
			case 2: return Enm_TypeCompany.CV;
			case 3: return Enm_TypeCompany.TOKO;
			case 4: return Enm_TypeCompany.BANK;
			case 5: return Enm_TypeCompany.PRV;
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
