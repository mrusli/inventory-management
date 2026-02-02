package com.pyramix.web.invoice;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Iframe;

import com.pyramix.domain.entity.Ent_Invoice;
import com.pyramix.domain.entity.Ent_InvoicePallet;
import com.pyramix.web.common.GFCBaseController;
import com.pyramix.web.common.JasperReportsUtil;
import com.pyramix.web.common.NumToWordsConverter;
import com.pyramix.web.invoice.dto.Dto_Kwitansi;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;

@Slf4j
public class KwitansiBahanPrintController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2332679542465982583L;

	private JasperReportsUtil jasperReportUtil;
	private NumToWordsConverter numToWordsConverter;

	private Iframe iframe;
	
	private Ent_Invoice activeInvoice;
	
	private static final Double PPN = 11.0;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		activeInvoice = (Ent_Invoice) arg.get("activeInvoice");
	}

	public void onCreate$kwitansiBahanReportPrintWin(Event event) throws Exception {
		log.info("kwitansiBahanReportPrintWin created");

		JasperReport jasperReport = getJasperReportUtil().loadJasperReport("reports/Kwitansi-KRG.jrxml");
		jasperReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
		
		Map<String, Object> parameters = getKwitansiBahanParameters();
		
		JRDataSource dataSource = new JRBeanCollectionDataSource(getKwitansiDataSource());

		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

		String rtNoKwitansi = activeInvoice.getBahanKwitansi().getKwitansi_ser().getSerialComp();
		LocalDateTime currDatetime = getLocalDateTime(getZoneId());
		String rtTimestamp = datetimeToStringDisplay(currDatetime, getShortDateTimeFormat(), getLocale());
		
		AMedia amedia = new AMedia(rtNoKwitansi+"_"+rtTimestamp+".pdf", "pdf", "application/pdf", baos.toByteArray());
		iframe.setContent(amedia);		

	}
	
	private Map<String, Object> getKwitansiBahanParameters() throws Exception {
		double jumlahBahan = calcJumlahBahan();
		double jumlahPpn = PPN * jumlahBahan / 100;
		double jumlahTotalBahan = jumlahBahan + jumlahPpn;
		
		String jumlahEjakan = getNumToWordsConverter().angkaToTerbilang((long) jumlahTotalBahan);
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("kwitansiNo", activeInvoice.getBahanKwitansi().getKwitansi_ser().getSerialComp());
		parameters.put("customerName", activeInvoice.getInvc_customer().getCompanyType()+"."+
				activeInvoice.getInvc_customer().getCompanyLegalName());
		parameters.put("jumlahEjakan", jumlahEjakan+" Rupiah");
		parameters.put("untukPembayaran", "");
		parameters.put("sejumlah", toDecimalFormat(new BigDecimal(jumlahTotalBahan), getLocale(), getDecimalFormat()));
		parameters.put("kwitansiTgl", dateToStringDisplay(activeInvoice.getJasaKwitansi().getKwitansi_date(), getLongDateFormat(), getLocale()));
		
		return parameters;
	}

	private double calcJumlahBahan() throws Exception {
		double jumlah = 0;
		for(Ent_InvoicePallet invcPlt : activeInvoice.getInvoicePallet()) {
			jumlah = jumlah + invcPlt.getPallet_subtotal();
		}
		return jumlah;
	}	
	
	private List<Dto_Kwitansi> getKwitansiDataSource() {
		List<Dto_Kwitansi> kwitansiList = new ArrayList<Dto_Kwitansi>();
		
		return kwitansiList;
	}

	public JasperReportsUtil getJasperReportUtil() {
		return jasperReportUtil;
	}

	public void setJasperReportUtil(JasperReportsUtil jasperReportUtil) {
		this.jasperReportUtil = jasperReportUtil;
	}

	public NumToWordsConverter getNumToWordsConverter() {
		return numToWordsConverter;
	}

	public void setNumToWordsConverter(NumToWordsConverter numToWordsConverter) {
		this.numToWordsConverter = numToWordsConverter;
	}
	
	
	
}
