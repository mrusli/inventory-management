package com.pyramix.web.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NumToWordsConverter {

	private String[] huruf={"","Satu","Dua","Tiga","Empat","Lima","Enam","Tujuh","Delapan","Sembilan","Sepuluh","Sebelas"};
	
	public NumToWordsConverter() {
		super();
		log.info("init NumToWords converter");
	}

	public String angkaToTerbilang(Long angka) {
		if (angka < 12)
			return huruf[angka.intValue()];
		if (angka >= 12 && angka <=19)
			return huruf[angka.intValue() % 10] + " Belas";
	    if(angka >= 20 && angka <= 99)
	    	return angkaToTerbilang(angka / 10) + " Puluh " + huruf[angka.intValue() % 10];
        if(angka >= 100 && angka <= 199)
            return "Seratus " + angkaToTerbilang(angka % 100);
        if(angka >= 200 && angka <= 999)
            return angkaToTerbilang(angka / 100) + " Ratus " + angkaToTerbilang(angka % 100);
        if(angka >= 1000 && angka <= 1999)
            return "Seribu " + angkaToTerbilang(angka % 1000);
        if(angka >= 2000 && angka <= 999999)
            return angkaToTerbilang(angka / 1000) + " Ribu " + angkaToTerbilang(angka % 1000);
        if(angka >= 1000000 && angka <= 999999999)
            return angkaToTerbilang(angka / 1000000) + " Juta " + angkaToTerbilang(angka % 1000000);
        if(angka >= 1000000000 && angka <= 999999999999L)
            return angkaToTerbilang(angka / 1000000000) + " Milyar " + angkaToTerbilang(angka % 1000000000);

		return "";
		
	}	
}
