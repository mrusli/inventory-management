package com.pyramix.web.process;

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

import com.pyramix.domain.entity.Enm_TypeProcess;
import com.pyramix.domain.entity.Ent_InventoryProcess;
import com.pyramix.domain.entity.Ent_InventoryProcessMaterial;
import com.pyramix.domain.entity.Ent_InventoryProcessProduct;
import com.pyramix.persistence.inventoryprocess.dao.InventoryProcessDao;
import com.pyramix.web.common.GFCBaseController;
import com.pyramix.web.common.JasperReportsUtil;
import com.pyramix.web.process.dto.Dto_ProcessCoilShr;
import com.pyramix.web.process.dto.Dto_ProcessCoilSlt;

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
	private InventoryProcessDao inventoryProcessDao;
	
	private Iframe iframe;
	
	private Ent_InventoryProcess selInventoryProcess;
		
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		selInventoryProcess = 
				(Ent_InventoryProcess) arg.get("selInventoryProcess");
		log.info(selInventoryProcess.toString());
	}

	/**
	 *  If you need to display only the title band without the details:
 	 *  jasperReport.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);
	 *  
	 *  PARAMETER in Jaspersoft:
	 *  (1) create a Parameter called 'customerName' 
	 *  (2) drop a 'Text Field' onto the band (eq.Title band)
	 *  (3) in the Properties of the Text Field, find the Expression, type $P{customerName}
	 *  (4) save the report. copy and paste the report jrxml file to eclipse.
	 *  (5) add the following for the parameters Map:
	 *  parameters.put("customerName", "PT.KINMASARU...");
	 *  DETAIL band in Jaspersoft:
	 *  (1) create all Fields (marking, jenisCoil, etc.)
	 *  (2) drop textfield into the detail band
	 *  (3) set the expression to $F{marking} $F{jenisCoil}
	 *  MASTER-DETAILS in Jaspersoft:
	 *	--> make sure the detail list (eq.processCoilProducts) is created in Fields
	 *  (1) drop a List element to the detail band
	 *  (2) in the wizard, rename the dataset as 'DatasetProducts'
	 *  (3) create the Fields: pmarking, pspek, ect.
	 *  (4) drop the fields into the List
	 *  (5) in the dataset - to link the processCoilProducts list into the list, use
	 *  JRDatasource expression: new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource((ArrayList)$F{processCoilProducts})
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$productionReportPrintWin(Event event) throws Exception {
		log.info("productionReportPrintWin created");
		JasperReport jasperReport = null;
		Map<String, Object> parameters = null;
		List<Dto_ProcessCoilShr> processCoilShrList = null;
		List<Dto_ProcessCoilSlt> processCoilSltList = null;
		JRDataSource dataSource = null;
		// load JasperReport XML
		if (selInventoryProcess.getProcessType().equals(Enm_TypeProcess.Shearing)) {
			jasperReport = getJasperReportUtil().loadJasperReport("reports/ReportProduction_Shr.jrxml");			
			// get the required parameters (mostly for the title band)
			parameters = getShrParameters();
			// map to process size
			processCoilShrList = getShrProcessSize();
			// set to JR datasource
			dataSource = new JRBeanCollectionDataSource(processCoilShrList);
		} else {
			jasperReport = getJasperReportUtil().loadJasperReport("reports/ReportProduction_Slt.jrxml");
			// get the required parameters (mostly for the title band)
			parameters = getShrParameters();
			// map to process size
			processCoilSltList = getSltProcessSize();			
			// set to JR datasource
			dataSource = new JRBeanCollectionDataSource(processCoilSltList);
		}
		
		// fill the report with data
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

//		==> ALTERNATIVE:
//		JRPdfExporter exporter = new JRPdfExporter();
//		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
//		exporter.exportReport();
		
		// String rtNoSuratJalan = currSuratJalan.getSuratjalanSerial().getSerialComp();
		String rtNoProcess = selInventoryProcess.getProcessNumber().getSerialComp();
		LocalDateTime currDatetime = getLocalDateTime(getZoneId());
		String rtTimestamp = datetimeToStringDisplay(currDatetime, getShortDateTimeFormat(), getLocale());

		AMedia amedia = new AMedia(rtNoProcess+"_"+rtTimestamp+".pdf", "pdf", "application/pdf", baos.toByteArray());
		iframe.setContent(amedia);
	}

	private Map<String,Object> getShrParameters() {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("customerName", selInventoryProcess.getCustomer().getCompanyType()+"."+
				selInventoryProcess.getCustomer().getCompanyLegalName());
		parameters.put("prdReportNumber", selInventoryProcess.getProcessNumber().getSerialComp());
		parameters.put("prdReportDate", dateToStringDisplay(selInventoryProcess.getOrderDate(), getShortDateFormat(), getLocale()));

		return parameters;
	}

	private List<Dto_ProcessCoilShr> getShrProcessSize() throws Exception {
		Ent_InventoryProcessMaterial processMaterial;
		List<Dto_ProcessCoilShr> processList = new ArrayList<Dto_ProcessCoilShr>();
		selInventoryProcess = getInventoryProcessDao()
				.findInventoryProcessMaterialsByProxy(selInventoryProcess.getId());
		processMaterial = selInventoryProcess.getProcessMaterials().get(0);
		processMaterial = getInventoryProcessDao()
				.findInventoryProcessProductsByProxy(processMaterial.getId());
		int idx = 1;
		double qtyKg, thck, wdth, lgth, dens;
		for (Ent_InventoryProcessProduct processProduct : processMaterial.getProcessProducts()) {
			Dto_ProcessCoilShr dtoProcessCoil = new Dto_ProcessCoilShr();
			dtoProcessCoil.setSeq(String.valueOf(idx)+".");
			dtoProcessCoil.setCoilNo(processMaterial.getMarking());
			dtoProcessCoil.setSpec(processMaterial.getInventoryCode().getProductCode());
			dtoProcessCoil.setWeigth(toDecimalFormat(new BigDecimal(
					processMaterial.getWeightQuantity()), getLocale(), "###.###"));
			dtoProcessCoil.setShearingSize(processProduct.getThickness()+"x"+
					toDecimalFormat(new BigDecimal(processProduct.getWidth()), getLocale(), "#####")+"x"+
					toDecimalFormat(new BigDecimal(processProduct.getLength()), getLocale(), "#####"));
			dtoProcessCoil.setSheet(getFormatedInteger(processProduct.getSheetQuantity()));
			// calc
			thck = processProduct.getThickness();
			wdth = processProduct.getWidth();
			lgth = processProduct.getLength();
			dens = processProduct.getInventoryCode().getInventoryType().getDensity();
			qtyKg = thck * wdth * lgth * dens / 1000000;
			// set
			dtoProcessCoil.setWeigthPcs(toDecimalFormat(new BigDecimal(qtyKg), getLocale(), "###.###"));
			// increment
			idx++;
			// add to list
			processList.add(dtoProcessCoil);
		}
		// row padding - max row for jasper report is 5 rows
		for (int i=idx; i<6; i++) {
			Dto_ProcessCoilShr dtoProcessCoil = new Dto_ProcessCoilShr();
			dtoProcessCoil.setSeq(String.valueOf(i)+".");
			dtoProcessCoil.setCoilNo(" ");
			dtoProcessCoil.setSpec(" ");
			dtoProcessCoil.setWeigth(" ");
			dtoProcessCoil.setShearingSize(" ");
			dtoProcessCoil.setSheet(" ");
			dtoProcessCoil.setWeigthPcs(" ");
			// add to list
			processList.add(dtoProcessCoil);			
		}
		
		return processList;
	}	

	private List<Dto_ProcessCoilSlt> getSltProcessSize() throws Exception {
		Ent_InventoryProcessMaterial processMaterial;
		List<Dto_ProcessCoilSlt> processList = new ArrayList<Dto_ProcessCoilSlt>();
		selInventoryProcess = getInventoryProcessDao()
				.findInventoryProcessMaterialsByProxy(selInventoryProcess.getId());
		processMaterial = selInventoryProcess.getProcessMaterials().get(0);
		processMaterial = getInventoryProcessDao()
				.findInventoryProcessProductsByProxy(processMaterial.getId());
		int idx = 1;
		for (Ent_InventoryProcessProduct processProduct : processMaterial.getProcessProducts()) {
			Dto_ProcessCoilSlt dtoProcessCoil = new Dto_ProcessCoilSlt();
			dtoProcessCoil.setSeq(String.valueOf(idx));
			dtoProcessCoil.setCoilNo(processMaterial.getMarking());
			dtoProcessCoil.setSpec(processMaterial.getInventoryCode().getProductCode());
			dtoProcessCoil.setWeigth(toDecimalFormat(new BigDecimal(
					processMaterial.getWeightQuantity()), getLocale(), "###.###")+" Kg");
			dtoProcessCoil.setMaterialSize(processMaterial.getThickness()+"x"+
					toDecimalFormat(new BigDecimal(processMaterial.getWidth()), getLocale(), "#####")+"x Coil");
			dtoProcessCoil.setSlittingSize(toDecimalFormat(new BigDecimal(
					processProduct.getWidth()), getLocale(), "#####"));
			dtoProcessCoil.setSlitQty(getFormatedInteger(processProduct.getSheetQuantity()));
			dtoProcessCoil.setSlitWeight(getFormatedInteger(processProduct.getSheetQuantity()) + " x " +
					toDecimalFormat(new BigDecimal(processProduct.getWeightQuantity()), getLocale(), "##.###")+" Kg");
			// increment
			idx++;
			// add to list
			processList.add(dtoProcessCoil);
		}
		for (int i=idx; i<5; i++) {
			Dto_ProcessCoilSlt dtoProcessCoil = new Dto_ProcessCoilSlt();
			dtoProcessCoil.setSeq(String.valueOf(i));
			dtoProcessCoil.setCoilNo(" ");
			dtoProcessCoil.setSpec(" ");
			dtoProcessCoil.setWeigth(" ");
			dtoProcessCoil.setMaterialSize(" ");
			dtoProcessCoil.setSlittingSize(" ");
			dtoProcessCoil.setSlitQty(" ");
			dtoProcessCoil.setSlitWeight(" ");
			// add to list
			processList.add(dtoProcessCoil);
		}
		
		return processList;
	}	
	
	public JasperReportsUtil getJasperReportUtil() {
		return jasperReportUtil;
	}

	public void setJasperReportUtil(JasperReportsUtil jasperReportUtil) {
		this.jasperReportUtil = jasperReportUtil;
	}

	public InventoryProcessDao getInventoryProcessDao() {
		return inventoryProcessDao;
	}

	public void setInventoryProcessDao(InventoryProcessDao inventoryProcessDao) {
		this.inventoryProcessDao = inventoryProcessDao;
	}
}
