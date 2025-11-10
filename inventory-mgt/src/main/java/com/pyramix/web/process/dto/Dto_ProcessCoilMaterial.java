package com.pyramix.web.process.dto;

import java.util.List;

import lombok.Data;

@Data
public class Dto_ProcessCoilMaterial {
	
	private String seq;

	private String marking;
	
	private String jenisCoil;
	
	private String qtyKg;
	
	private String spek;
	
	private List<Dto_ProcessCoilProduct> processCoilProducts;
}
