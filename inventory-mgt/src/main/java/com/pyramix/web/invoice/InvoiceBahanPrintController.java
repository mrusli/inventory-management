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
import com.pyramix.domain.entity.Ent_InvoicePallet;
import com.pyramix.web.common.GFCBaseController;
import com.pyramix.web.common.JasperReportsUtil;
import com.pyramix.web.invoice.dto.Dto_InvoiceBahan;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;

@Slf4j
public class InvoiceBahanPrintController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -200450998636516392L;
	
	private JasperReportsUtil jasperReportUtil;
	
	private Iframe iframe;
	
	private Ent_Invoice activeInvoice;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		activeInvoice = (Ent_Invoice) arg.get("activeInvoice");
	}

	public void onCreate$tagihanBahanReportPrintWin(Event event) throws Exception {
		log.info("tagihanBahanReportPrintWin created");
		
		JasperReport jasperReport = getJasperReportUtil().loadJasperReport("reports/Tagihan-Bahan-KRG.jrxml");
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
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put("InvoiceNo", activeInvoice.getInvc_ser().getSerialComp()+"B");
		parameters.put("InvoiceTgl", dateToStringDisplay(activeInvoice.getInvc_date(), getLongDateFormat(), getLocale()));
		parameters.put("CustomerName", activeInvoice.getInvc_customer().getCompanyType()+"."+
				activeInvoice.getInvc_customer().getCompanyLegalName());
		parameters.put("CustomerAddr1", activeInvoice.getInvc_customer().getAddress01());
		parameters.put("CustomerAddr2", activeInvoice.getInvc_customer().getAddress02());
		
		return parameters;
	}

	private List<Dto_InvoiceBahan> getInvoiceDataSource() throws Exception {
		List<Dto_InvoiceBahan> invcList = new ArrayList<Dto_InvoiceBahan>();
		
		for(Ent_InvoicePallet pallet : activeInvoice.getInvoicePallet()) {
			Dto_InvoiceBahan bahan = new Dto_InvoiceBahan();
			bahan.setSuratJalanNo(pallet.getRef_suratjalan().getSuratjalanSerial().getSerialComp());
			bahan.setSuratJalanTgl(dateToStringDisplay(pallet.getRef_suratjalan().getSuratjalanDate(), getShortDateFormat(), getLocale()));
			bahan.setMarking(pallet.getMarking());
			bahan.setKeterangan("");
			bahan.setPcs(getFormatedInteger(pallet.getQty_pcs()));
			bahan.setRpPallet(toDecimalFormat(new BigDecimal(pallet.getPallet_price()), getLocale(), getDecimalFormat()));
			bahan.setJumlah(toDecimalFormat(new BigDecimal(pallet.getPallet_subtotal()), getLocale(), getDecimalFormat()));
			
			invcList.add(bahan);
		}
		
		return invcList;
	}

	public JasperReportsUtil getJasperReportUtil() {
		return jasperReportUtil;
	}

	public void setJasperReportUtil(JasperReportsUtil jasperReportUtil) {
		this.jasperReportUtil = jasperReportUtil;
	}

}
