package com.pyramix.web.invoice;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Iframe;

import com.pyramix.domain.entity.Ent_Invoice;
import com.pyramix.domain.entity.Ent_InvoiceProduct;
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
public class KwitansiPrintController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6449422743057407756L;
	
	private JasperReportsUtil jasperReportUtil;
	private NumToWordsConverter numToWordsConverter;
	
	private Iframe iframe;
	
	private Ent_Invoice activeInvoice;
	
	private static final Double PPN = 11.0;
	private static final Double PPH = 2.0;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		activeInvoice = (Ent_Invoice) arg.get("activeInvoice");
	}

	public void onCreate$kwitansiReportPrintWin(Event event) throws Exception {
		log.info("kwitansiReportPrintWin created");
		
		JasperReport jasperReport = getJasperReportUtil().loadJasperReport("reports/Kwitansi-KRG.jrxml");
		jasperReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
		
		Map<String, Object> parameters = getKwitansiParameters();
		
		JRDataSource dataSource = new JRBeanCollectionDataSource(getKwitansiDataSource());

		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

		AMedia amedia = new AMedia("testReport", "pdf", "application/pdf", baos.toByteArray());
		iframe.setContent(amedia);		
		
	}

	private Map<String, Object> getKwitansiParameters() throws Exception {
		// log.info(getNumToWordsConverter().angkaToTerbilang((long) 11223993));
		double jumlahJasa = calcJumlahJasa();
		double jumlahPpn = PPN * jumlahJasa / 100;
		double jumlahTotalJasa = jumlahJasa + jumlahPpn;
		double jumlahPph = PPH * jumlahJasa / 100;
		double jumlahTotalJasaDecPph = jumlahTotalJasa - jumlahPph;
		
		String jumlahEjakan = getNumToWordsConverter().angkaToTerbilang((long) jumlahTotalJasaDecPph);
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("kwitansiNo", activeInvoice.getJasaKwitansi().getKwitansi_ser().getSerialComp());
		parameters.put("customerName", activeInvoice.getInvc_customer().getCompanyType()+"."+
				activeInvoice.getInvc_customer().getCompanyLegalName());
		parameters.put("jumlahEjakan", jumlahEjakan+" Rupiah");
		parameters.put("untukPembayaran", "Tagihan/Invoice No: "+activeInvoice.getInvc_ser().getSerialComp()+" termasuk PPN dan pemotongan PPh23. Perincian Terlampir.");
		parameters.put("sejumlah", toDecimalFormat(new BigDecimal(jumlahTotalJasaDecPph), getLocale(), getDecimalFormat()));
		parameters.put("kwitansiTgl", dateToStringDisplay(activeInvoice.getJasaKwitansi().getKwitansi_date(), getLongDateFormat(), getLocale()));
		
		return parameters;
	}

	private double calcJumlahJasa() {
		double jumlah = 0;
		for(Ent_InvoiceProduct invcProd : activeInvoice.getInvoiceProducts()) {
			jumlah = jumlah + invcProd.getSub_total();
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
