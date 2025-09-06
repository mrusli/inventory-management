package com.pyramix.domain.entity;

public enum Enm_CompanyType {
	PT(0), PD(1), CV(2), TOKO(3), BANK(4), PRV(5);
	
	private int value;

	private Enm_CompanyType(int value) {
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

	public Enm_CompanyType toCompanyType(int value) {
		switch (value) {
			case 0: return Enm_CompanyType.PT;
			case 1: return Enm_CompanyType.PD;
			case 2: return Enm_CompanyType.CV;
			case 3: return Enm_CompanyType.TOKO;
			case 4: return Enm_CompanyType.BANK;
			case 5: return Enm_CompanyType.PRV;
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
