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
public class InvoicePrintController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4516944169780855774L;

	private JasperReportsUtil jasperReportUtil;
	
	private Iframe iframe;

	private Ent_Invoice activeInvoice;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		activeInvoice = (Ent_Invoice) arg.get("activeInvoice");
	}

	public void onCreate$tagihanReportPrintWin(Event event) throws Exception {
		log.info("tagihanReportPrintWin created");
		
		JasperReport jasperReport = getJasperReportUtil().loadJasperReport("reports/Tagihan-KRG.jrxml");
		jasperReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
		
		Map<String, Object> parameters = getInvoiceParameters();
		
		JRDataSource dataSource = new JRBeanCollectionDataSource(getInvoiceDataSource());

		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

		AMedia amedia = new AMedia("testReport", "pdf", "application/pdf", baos.toByteArray());
		iframe.setContent(amedia);		
		
	}

	private Map<String, Object> getInvoiceParameters() {
		Map<String, Object> parameters = new HashMap<String,Object>();
		parameters.put("InvoiceNo", activeInvoice.getInvc_ser().getSerialComp());
		parameters.put("InvoiceTgl", dateToStringDisplay(
				activeInvoice.getInvc_date(), getLongDateFormat(), getLocale()));
		parameters.put("CustomerName", activeInvoice.getInvc_customer().getCompanyType()+"."+
				activeInvoice.getInvc_customer().getCompanyLegalName());
		parameters.put("CustomerAddr1", activeInvoice.getInvc_customer().getAddress01());
		parameters.put("CustomerAddr2", activeInvoice.getInvc_customer().getAddress02());
		
		return parameters;
	}
	
	private List<Dto_Invoice> getInvoiceDataSource() throws Exception {
		List<Dto_Invoice> dtoInvoiceList = new ArrayList<Dto_Invoice>();
		
		for(Ent_InvoiceProduct prod : activeInvoice.getInvoiceProducts()) {
			Dto_Invoice dto_invc = new Dto_Invoice();
			dto_invc.setSuratJalanNo(prod.getRef_suratjalan().getSuratjalanSerial().getSerialComp());
			dto_invc.setSuratJalanTgl(dateToStringDisplay(prod.getRef_suratjalan().getSuratjalanDate(), getShortDateFormat(), getLocale()));
			dto_invc.setMarking(prod.getMarking());
			dto_invc.setRefPO(prod.getRef_document());
			dto_invc.setSize(prod.getThickness()+"x"+prod.getWidth()+prod.getLength());
			dto_invc.setBerat(toDecimalFormat(new BigDecimal(prod.getQuantity_by_kg()), getLocale(), getDecimalFormat()));
			dto_invc.setRpKg(toDecimalFormat(new BigDecimal(prod.getUnit_price()), getLocale(), getDecimalFormat()));
			dto_invc.setJumlah(toDecimalFormat(new BigDecimal(prod.getSub_total()), getLocale(), getDecimalFormat()));
			
			dtoInvoiceList.add(dto_invc);
		}
		
		
		return dtoInvoiceList;
	}

	public JasperReportsUtil getJasperReportUtil() {
		return jasperReportUtil;
	}

	public void setJasperReportUtil(JasperReportsUtil jasperReportUtil) {
		this.jasperReportUtil = jasperReportUtil;
	}
	
}
