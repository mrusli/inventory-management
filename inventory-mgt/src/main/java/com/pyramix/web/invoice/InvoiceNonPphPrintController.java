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
import com.pyramix.domain.entity.Ent_InvoiceProduct;
import com.pyramix.web.common.GFCBaseController;
import com.pyramix.web.common.JasperReportsUtil;
import com.pyramix.web.invoice.dto.Dto_Invoice;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;

@Slf4j
public class InvoiceNonPphPrintController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8207109278027367736L;

	private JasperReportsUtil jasperReportUtil;

	private Iframe iframe;
	
	private Ent_Invoice activeInvoice;
	private double subtotal01, subtotal02;
	private double ppnAmount;
	private double totalAmount;
	private double pphAmount;
	
	private static final Double PPN = 11.0;
	private static final Double PPH = 2.0;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		activeInvoice = (Ent_Invoice) arg.get("activeInvoice");
	}
	
	public void onCreate$tagihanNonPphReportPrintWin(Event event) throws Exception {
		log.info("tagihanNonPphReportPrintWin created");
		
		JasperReport jasperReport = getJasperReportUtil().loadJasperReport("reports/Tagihan-KRG-NonPph.jrxml");
		jasperReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
		
		JRDataSource dataSource = new JRBeanCollectionDataSource(getInvoiceDataSource());
		Map<String, Object> parameters = getInvoiceParameters();		

		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

		String rtNoInvoice = activeInvoice.getInvc_ser().getSerialComp();
		LocalDateTime currDatetime = getLocalDateTime(getZoneId());
		String rtTimestamp = datetimeToStringDisplay(currDatetime, getShortDateFormat(), getLocale());
		
		AMedia amedia = new AMedia(rtNoInvoice+"_"+rtTimestamp+".pdf", "pdf", "application/pdf", baos.toByteArray());
		iframe.setContent(amedia);		
		
	}
	
	private List<Dto_Invoice> getInvoiceDataSource() throws Exception {
		List<Dto_Invoice> dtoInvoiceList = new ArrayList<Dto_Invoice>();
		subtotal01 = 0;
		for(Ent_InvoiceProduct prod : activeInvoice.getInvoiceProducts()) {
			Dto_Invoice dto_invc = new Dto_Invoice();
			dto_invc.setSuratJalanNo(prod.getRef_suratjalan().getSuratjalanSerial().getSerialComp());
			dto_invc.setSuratJalanTgl(dateToStringDisplay(prod.getRef_suratjalan().getSuratjalanDate(), getShortDateFormat(), getLocale()));
			dto_invc.setMarking(prod.getMarking());
			dto_invc.setRefPO(prod.getRef_document());
			dto_invc.setSize(
					toDecimalFormat(new BigDecimal(prod.getThickness()), getLocale(), "#0,00")+"x"+
					toDecimalFormat(new BigDecimal(prod.getWidth()), getLocale(), "######")+"x"+
					toDecimalFormat(new BigDecimal(prod.getLength()), getLocale(), "######"));
					// prod.getThickness()+"x"+prod.getWidth()+"x"+prod.getLength());
			dto_invc.setQty(
					toDecimalFormat(new BigDecimal(prod.getQuantity_by_sht()), getLocale(),"######"));
			dto_invc.setBerat(
					toDecimalFormat(new BigDecimal(prod.getQuantity_by_kg()), getLocale(),"######"));
			dto_invc.setRpKg(toDecimalFormat(new BigDecimal(prod.getUnit_price()), getLocale(), "###.###"));
			dto_invc.setJumlah(toDecimalFormat(new BigDecimal(prod.getSub_total()), getLocale(), "##.###.###"));
			subtotal01 = subtotal01 + prod.getSub_total();
			
			dtoInvoiceList.add(dto_invc);
		}
		
		
		return dtoInvoiceList;
	}
	
	private Map<String, Object> getInvoiceParameters() throws Exception {
		ppnAmount = subtotal01 * PPN / 100;
		subtotal02 = subtotal01 + ppnAmount;
		pphAmount = subtotal01 * PPH / 100;
		totalAmount = subtotal02 - pphAmount;
		
		Map<String, Object> parameters = new HashMap<String,Object>();
		parameters.put("InvoiceNo", activeInvoice.getInvc_ser().getSerialComp());
		parameters.put("InvoiceTgl", dateToStringDisplay(
				activeInvoice.getInvc_date(), getLongDateFormat(), getLocale()));
		parameters.put("CustomerName", activeInvoice.getInvc_customer().getCompanyType()+"."+
				activeInvoice.getInvc_customer().getCompanyLegalName());
		parameters.put("CustomerAddr1", activeInvoice.getInvc_customer().getAddress01());
		parameters.put("CustomerAddr2", activeInvoice.getInvc_customer().getAddress02());
		parameters.put("subtotal01", toDecimalFormat(new BigDecimal(subtotal01), getLocale(), "###.###.###"));
		parameters.put("subtotal02", toDecimalFormat(new BigDecimal(subtotal02), getLocale(), "###.###.###"));
		parameters.put("ppnAmount", toDecimalFormat(new BigDecimal(ppnAmount), getLocale(), "###.###.###"));
		parameters.put("pphAmount", toDecimalFormat(new BigDecimal(pphAmount), getLocale(), "###.###.###"));
		parameters.put("totalAmount", toDecimalFormat(new BigDecimal(totalAmount), getLocale(), "###.###.###"));
		
		return parameters;
	}

	public JasperReportsUtil getJasperReportUtil() {
		return jasperReportUtil;
	}

	public void setJasperReportUtil(JasperReportsUtil jasperReportUtil) {
		this.jasperReportUtil = jasperReportUtil;
	}
	
	
}
