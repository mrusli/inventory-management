package com.pyramix.web.process;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Iframe;

import com.pyramix.web.common.GFCBaseController;
import com.pyramix.web.common.JasperReportsUtil;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Slf4j
public class ProcessCoilJasperController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8877503290835975935L;

	private JasperReportsUtil jasperReportUtil;
	
	private Iframe iframe;
		
	public void onCreate$productionReportPrintWin(Event event) throws Exception {
		log.info("productionReportPrintWin created");

		JasperReport jasperReport = getJasperReportUtil().loadJasperReport("reports/ReportProduction.jrxml");
		// jasperReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
		
		Map<String, Object> parameters = new HashMap<>();
//		in Jaspersoft:
//		(1) create a Parameter called 'customerName' 
//		(2) drop a 'Text Field' onto the band (eq.Title band)
//		(3) in the Properties of the Text Field, find the Expression, type ${customerName}
//		(4) save the report. copy and paste the report jrxml file to eclipse.
//		(5) add the following for the parameters Map:
//		parameters.put("customerName", "PT.KINMASARU...");
		
		List<ProcessCoilProductDto> processCoildProductDtoList = new ArrayList<ProcessCoilProductDto>();
		
		ProcessCoilProductDto processCoilProductDto = new ProcessCoilProductDto();
		processCoilProductDto.setPmarking("PKP00201");
		processCoilProductDto.setPspek("0,80 x 1219 x 2438");
		processCoilProductDto.setPqtyKg("105Kg");
		processCoilProductDto.setPqtyLbr("75");
		processCoildProductDtoList.add(processCoilProductDto);
		processCoilProductDto = new ProcessCoilProductDto();
		processCoilProductDto.setPmarking("PKP00202");
		processCoilProductDto.setPspek("0,80 x 1219 x 2438");
		processCoilProductDto.setPqtyKg("122Kg");
		processCoilProductDto.setPqtyLbr("95");
		processCoildProductDtoList.add(processCoilProductDto);
		
		List<ProcessCoilDto> processCoilDtoList = new ArrayList<ProcessCoilDto>();
		
		ProcessCoilDto processCoilDto = new ProcessCoilDto();
		processCoilDto.setJenisCoil("SPCC");
		processCoilDto.setSpek("0,80 x 1219 x 2438");
		processCoilDto.setQtyKg("478,00");
		processCoilDto.setMarking("KP002");	
		processCoilDto.setProcessCoilProducts(processCoildProductDtoList);
		processCoilDtoList.add(processCoilDto);
		processCoilDto = new ProcessCoilDto();
		processCoilDto.setJenisCoil("SPCC");
		processCoilDto.setSpek("0,80 x 1219 x 2438");
		processCoilDto.setQtyKg("510,00");
		processCoilDto.setMarking("KP003");		
		processCoilDtoList.add(processCoilDto);
//		in Jaspersoft:
//		(1) create all Fields (marking, jenisCoil, etc.)
//		(2) drop textfield into the detail band
//		(3) set the expression to $F{marking} $F{jenisCoil}
//		details in Jaspersoft:
//		--> make sure the detail list (eq.processCoilProducts) is created in Fields
//		(1) drop a List element to the detail band
//		(2) in the wizard, rename the dataset as 'DatasetProducts'
//		(3) create the Fields: pmarking, pspek, ect.
//		(4) drop the fields into the List
//		(5) in the dataset - to link the processCoilProducts list into the list, use
//		a JRDatasource expression: new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource((ArrayList)$F{processCoilProducts})
		JRDataSource dataSource = new JRBeanCollectionDataSource(processCoilDtoList);
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

//		==> ALTERNATIVE:
//		JRPdfExporter exporter = new JRPdfExporter();
//		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
//		exporter.exportReport();
		
		AMedia amedia = new AMedia("testReport", "pdf", "application/pdf", baos.toByteArray());
		iframe.setContent(amedia);
	}

	public JasperReportsUtil getJasperReportUtil() {
		return jasperReportUtil;
	}

	public void setJasperReportUtil(JasperReportsUtil jasperReportUtil) {
		this.jasperReportUtil = jasperReportUtil;
	}
}
