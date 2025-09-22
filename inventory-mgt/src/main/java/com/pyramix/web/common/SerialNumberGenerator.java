package com.pyramix.web.common;

import java.time.LocalDate;

import com.pyramix.domain.entity.Enm_TypeDocument;
import com.pyramix.domain.entity.Ent_Company;
import com.pyramix.domain.entity.Ent_Serial;
import com.pyramix.persistence.serial.dao.SerialDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SerialNumberGenerator extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1344153931284192294L;
	
	private SerialDao serialDao;
	
	public SerialNumberGenerator() {
		super();
		log.info("SerialNumberGenerator created");
	}

	public int getSerialNumber(Enm_TypeDocument documentType, LocalDate currentDate, Ent_Company company) {
		int serNum = 1;
		
		Ent_Serial serialNum =
				getSerialDao().findLastByDocumentType(documentType, company);
		
		if (serialNum != null) {
			LocalDate lastDate = serialNum.getSerialDate();
			
			// compare year
			int lastYearValue = getLocalDateYearValue(lastDate);
			int currYearValue = getLocalDateYearValue(currentDate);
			// compare month
			int lastMonthValue = getLocalDateMonthValue(lastDate);
			int currMonthValue = getLocalDateMonthValue(currentDate);
			
			if (lastYearValue==currYearValue) {
				
				if (lastMonthValue==currMonthValue) {
					
					serNum = serialNum.getSerialNumber()+1;
				}
			}
		}
		
		return serNum;
	}
	
	public SerialDao getSerialDao() {
		return serialDao;
	}

	public void setSerialDao(SerialDao serialDao) {
		this.serialDao = serialDao;
	}

}
