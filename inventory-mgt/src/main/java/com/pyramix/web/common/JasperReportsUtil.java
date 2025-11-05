package com.pyramix.web.common;

import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;

@Slf4j
public class JasperReportsUtil {

	public JasperReportsUtil() {
		super();
		log.info("init JasperReport Utility");
	}

	public JasperReport loadJasperReport(String reportPath) throws Exception {
		InputStream inputStream = new ClassPathResource(reportPath).getInputStream();
		
		return JasperCompileManager.compileReport(inputStream);
	}
	
}
