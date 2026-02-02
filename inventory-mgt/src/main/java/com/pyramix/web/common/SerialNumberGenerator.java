package com.pyramix.web.common;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

	public int getSerialNumber(Enm_TypeDocument documentType, LocalDateTime currentDatetime, Ent_Company company) {
		int serNum = 1;
		
		Ent_Serial serialNum =
				getSerialDao().findLastByDocumentType(documentType, company);
		
		if (serialNum != null) {
			LocalDateTime lastDate = serialNum.getSerialDatetime();
			
			// compare year
			int lastYearValue = lastDate.getYear();
			int currYearValue = currentDatetime.getYear();
			// compare month
			int lastMonthValue = lastDate.getMonthValue();
			int currMonthValue = currentDatetime.getMonthValue();
			
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
