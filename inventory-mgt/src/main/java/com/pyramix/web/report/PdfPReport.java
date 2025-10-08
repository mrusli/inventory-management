package com.pyramix.web.report;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PdfPReport {
	// set document
	// 		size: A4
	private final Rectangle rectSizeA4 = PageSize.A4;
	//		alignment left
	private final int alignLeft = Element.ALIGN_LEFT;
	//		extension
	private final String fileExt = ".pdf";
	
	private String outputDir = "/pyramix/inventory/pdf/";
	private String fileName = "";
	private String timestamp = LocalDateTime
			.now()
			.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
	
	public PdfPReport() {
		super();
		
		log.info("init PdfPReport utility");
	}

	public Document documentPdfCreate(String filename) throws DocumentException, FileNotFoundException {
		Document document = new Document();
		try {
			document.setPageSize(rectSizeA4.rotate());
			document.setMargins(10f, 5f, 5f, 5f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		PdfWriter.getInstance(document, new FileOutputStream(outputDir+filename+timestamp+fileExt));
		
		document.open();
		return document;
	}

	public Paragraph titleParagraph(String title) {
		Font font = FontFactory.getFont(FontFactory.HELVETICA, 16, BaseColor.BLACK);
		Paragraph paragraph = new Paragraph(title, font);

		return paragraph;
	}
	
	public Paragraph blankParagraph() {

		return new Paragraph(" ");
	}
	
	public Paragraph subTitleParagraph(String documentSubTitle) {
		
		return new Paragraph(documentSubTitle);
	}
	
	public PdfPTable createTable(int column) {
		PdfPTable table = new PdfPTable(column);
		table.setWidthPercentage(100);
		table.setHorizontalAlignment(alignLeft);
		
		return table;
	}	
	
	public void addTableHeader(PdfPTable table, String[] columnTitles) {
		Stream.of(columnTitles)
			.forEach(columnTitle -> {
				PdfPCell header = new PdfPCell();
				header.setBackgroundColor(BaseColor.LIGHT_GRAY);
				header.setBorder(0);
				// header.setBorderWidth(2);
				header.setPhrase(new Phrase(columnTitle));
				table.addCell(header);
			});
	}	
	
	public void addRow(PdfPTable table, List<String> row) {		
		row.stream()
			.forEach(rowContent -> {
				PdfPCell rowPCell = new PdfPCell();
				rowPCell.setBorder(0);
				rowPCell.setPhrase(new Phrase(rowContent));
				table.addCell(rowPCell);
			});
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
