package com.pyramix.web.suratjalan;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Iframe;

import com.pyramix.domain.entity.Ent_SuratJalan;
import com.pyramix.domain.entity.Ent_SuratJalanProduct;
import com.pyramix.web.common.GFCBaseController;
import com.pyramix.web.common.JasperReportsUtil;
import com.pyramix.web.suratjalan.dto.Dto_SuratJalan;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;

@Slf4j
public class SuratJalanPrintController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 580375452000761195L;

	private JasperReportsUtil jasperReportUtil;
	
	private Iframe iframe;
	
	private Ent_SuratJalan currSuratJalan;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		currSuratJalan = (Ent_SuratJalan) arg.get("currSuratJalan");
	}

	public void onCreate$suratjalanReportPrintWin(Event event) throws Exception {
		log.info("suratjalanReportPrintWin created");
		
		JasperReport jasperReport = getJasperReportUtil().loadJasperReport("reports/SuratJalan-KRG.jrxml");
		jasperReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
		
		Map<String, Object> parameters = getSuratJalanParameters();
		
		JRDataSource dataSource = new JRBeanCollectionDataSource(getSuratJalanDataSource());

		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

		String rtNoSuratJalan = currSuratJalan.getSuratjalanSerial().getSerialComp();
		LocalDateTime currDatetime = getLocalDateTime(getZoneId());
		String rtTimestamp = datetimeToStringDisplay(currDatetime, getShortDateTimeFormat(), getLocale());
		
		AMedia amedia = new AMedia(rtNoSuratJalan+"_"+rtTimestamp+".pdf", "pdf", "application/pdf", baos.toByteArray());
		iframe.setContent(amedia);		
	}

	private Map<String, Object> getSuratJalanParameters() {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("SuratJalanNo", currSuratJalan.getSuratjalanSerial().getSerialComp());
		parameters.put("SuratJalanTgl", dateToStringDisplay(currSuratJalan.getSuratjalanDate(), getLongDateFormat(), getLocale()));
		parameters.put("CustomerName", currSuratJalan.getCustomer().getCompanyType()+"."+currSuratJalan.getCustomer().getCompanyLegalName());
		parameters.put("CustomerAddr01", currSuratJalan.getCustomer().getAddress01());
		parameters.put("CustomerAddr02", currSuratJalan.getCustomer().getAddress02());
		parameters.put("NoPolisi", currSuratJalan.getNoPolisi());
		parameters.put("NoPesanan", currSuratJalan.getRefDocument());
		
		return parameters;
	}

	private List<Dto_SuratJalan> getSuratJalanDataSource() {
		List<Dto_SuratJalan> dtoSuratJalanList = new ArrayList<Dto_SuratJalan>();
		for(Ent_SuratJalanProduct prod : currSuratJalan.getSuratjalanProducts()) {
			Dto_SuratJalan dtoSuratJalan = new Dto_SuratJalan();
			dtoSuratJalan.setNoCoil(prod.getMarking());
			dtoSuratJalan.setSpec(prod.getInventoryCode().getProductCode());
			dtoSuratJalan.setUkuran(prod.getThickness()+"x"+
					prod.getWidth()+"x"+
					prod.getLength());
			dtoSuratJalan.setBeratKg(prod.getQuantityByKg()+" Kg.");
			dtoSuratJalan.setKeterangan(String.valueOf(prod.getQuantityBySht()));
			
			dtoSuratJalanList.add(dtoSuratJalan);
		}
		
		return dtoSuratJalanList;
	}	
	
	public JasperReportsUtil getJasperReportUtil() {
		return jasperReportUtil;
	}

	public void setJasperReportUtil(JasperReportsUtil jasperReportUtil) {
		this.jasperReportUtil = jasperReportUtil;
	}
	
}
