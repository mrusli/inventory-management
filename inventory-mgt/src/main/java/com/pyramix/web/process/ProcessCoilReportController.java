package com.pyramix.web.process;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.pyramix.web.common.GFCBaseController;
import com.pyramix.web.report.PdfPReport;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessCoilReportController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5919750921672200748L;

	private PdfPReport pdfpReportUtility;

	private Window processcoilReportWin;
	private Iframe processcoilReportIframe;
	private AMedia amedia;
	
	public void onCreate$processcoilReportWin(Event event) throws Exception {
		log.info("processcoilReportWin created...");
		
		getPdfpReportUtility().setFileName("Production-Report");
		String filename = getPdfpReportUtility().getFileName();
		Document document = getPdfpReportUtility().documentPdfCreate(filename);
		// title
		String documentTitle = "Production Report";
		Paragraph titleParagraph = getPdfpReportUtility().titleParagraph(documentTitle);
		Paragraph blankLine = getPdfpReportUtility().blankParagraph();
		// create table
		String[] columnTitle = { "Coil-No.","Spec.","Weight","Material","Check","Slitting","Qty","Male-K",
				"RB-Top","RB-Btm","Sp=I+II","II","Real","Cut-Qty","Cut-Lgth","Weight" };
		PdfPTable table = getPdfpReportUtility().createTable(columnTitle.length);
		// add header
		getPdfpReportUtility().addTableHeader(table, columnTitle);
		
		document.add(titleParagraph);
		document.add(blankLine);
		document.add(table);
		document.close();
		
		String outputDir = getPdfpReportUtility().getOutputDir();
		String timestamp = getPdfpReportUtility().getTimestamp();
		String fileExt = ".pdf";
		
		String outputFilename = outputDir+filename+timestamp+fileExt;
		
		File f = new File(outputFilename);
		FileInputStream fis = new FileInputStream(f);
		ByteArrayOutputStream bios = new ByteArrayOutputStream();
		
		byte[] buf = new byte[8192];
		int c = 0;
		while ((c=fis.read(buf, 0, buf.length))>0) {
			bios.write(buf, 0, c);
		}
		
		amedia = new AMedia(outputFilename, "pdf", "application/pdf", bios.toByteArray());
		processcoilReportIframe.setContent(amedia);

		fis.close();		
	}
	
	public void onClick$closeButton(Event event) throws Exception {
		processcoilReportWin.detach();		
	}
	
	public PdfPReport getPdfpReportUtility() {
		return pdfpReportUtility;
	}

	public void setPdfpReportUtility(PdfPReport pdfpReportUtility) {
		this.pdfpReportUtility = pdfpReportUtility;
	}
}
