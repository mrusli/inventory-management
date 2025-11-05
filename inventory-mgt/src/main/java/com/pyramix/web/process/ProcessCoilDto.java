package com.pyramix.web.process;

import java.util.List;

public class ProcessCoilDto {

	private String marking;
	
	private String jenisCoil;
	
	private String qtyKg;
	
	private String spek;
	
	private List<ProcessCoilProductDto> processCoilProducts;

	public String getMarking() {
		return marking;
	}

	public void setMarking(String marking) {
		this.marking = marking;
	}

	public String getJenisCoil() {
		return jenisCoil;
	}

	public void setJenisCoil(String jenisCoil) {
		this.jenisCoil = jenisCoil;
	}

	public String getQtyKg() {
		return qtyKg;
	}

	public void setQtyKg(String qtyKg) {
		this.qtyKg = qtyKg;
	}

	public String getSpek() {
		return spek;
	}

	public void setSpek(String spek) {
		this.spek = spek;
	}

	public List<ProcessCoilProductDto> getProcessCoilProducts() {
		return processCoilProducts;
	}

	public void setProcessCoilProducts(List<ProcessCoilProductDto> processCoilProducts) {
		this.processCoilProducts = processCoilProducts;
	}
}
