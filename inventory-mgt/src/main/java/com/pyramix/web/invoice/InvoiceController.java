package com.pyramix.web.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.domain.entity.Enm_StatusDocument;
import com.pyramix.domain.entity.Enm_TypeDocument;
import com.pyramix.domain.entity.Enm_TypeInvoice;
import com.pyramix.domain.entity.Enm_TypePayment;
import com.pyramix.domain.entity.Ent_Company;
import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.domain.entity.Ent_Invoice;
import com.pyramix.domain.entity.Ent_InvoiceFaktur;
import com.pyramix.domain.entity.Ent_InvoiceKwitansi;
import com.pyramix.domain.entity.Ent_InvoicePallet;
import com.pyramix.domain.entity.Ent_InvoiceProduct;
import com.pyramix.domain.entity.Ent_Serial;
import com.pyramix.domain.entity.Ent_SuratJalan;
import com.pyramix.domain.entity.Ent_SuratJalanProduct;
import com.pyramix.persistence.company.dao.CompanyDao;
import com.pyramix.persistence.customer.dao.CustomerDao;
import com.pyramix.persistence.invoice.dao.InvoiceDao;
import com.pyramix.persistence.suratjalan.dao.SuratJalanDao;
import com.pyramix.web.common.GFCBaseController;
import com.pyramix.web.common.NumToWordsConverter;
import com.pyramix.web.common.SerialNumberGenerator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvoiceController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3231974253231835389L;

	private InvoiceDao invoiceDao;
	private CustomerDao customerDao;
	private SerialNumberGenerator serialNumberGenerator;
	private CompanyDao companyDao;
	private SuratJalanDao suratjalanDao;
	private NumToWordsConverter numToWordsConverter;
	
	private Combobox customerCombobox, suratjalanCombobox;
	private Listbox invoiceListbox, invoiceProductListbox, palletListbox;
	private Label subtotal01JasaLabel, ppnJasaLabel, subtotal02JasaLabel,
		pph23JasaLabel, totalJasaLabel, subtotalPalletLabel, ppnPalletLabel, 
		totalPalletLabel, invoiceDateLabel, invoiceNumberLabel, kwitansiNumberLabel,
		fakturNumberLabel, invoicePltDateLabel, invoiceNumberPltLabel, 
		kwitansiNumberPltLabel, fakturNumberPltLabel, customerNameLabel;
	private Tabbox invoiceTabbox;
	private Grid suratjalanGrid;
	private Button cancelAddButton, saveAddButton, cancelAddPltButton, saveAddPltButton,
		createKwitansiButton, editButton, createKwitansiPltButton, editPltButton;
	private Textbox fakturNumberTextbox, fakturNumberPltTextbox;
	
	private Ent_Customer selCustomer;
	private ListModelList<Ent_Invoice> invoiceModelList;
	private Ent_Company defaultCompany = null;
	private List<Ent_InvoiceProduct> invoiceProductList = 
			new ArrayList<Ent_InvoiceProduct>();
	private List<Ent_SuratJalan> selSuratJalanList =
			new ArrayList<Ent_SuratJalan>();
	private Ent_Invoice activeInvoice = null;
	private List<Ent_InvoicePallet> palletList;
	
	private static final Long DEF_COMPANY_IDX = (long) 3;
	private static final Double PPN = 11.0;
	private static final Double PPH = 2.0;
	
	@SuppressWarnings("unchecked")
	public void onCreate$infoInvoicePanel(Event event) throws Exception {
		log.info("infoInvoicePanel created");
		
		// set listener
		fakturNumberTextbox.addEventListener(Events.ON_OK, new fakturNumberTextboxListener());
		fakturNumberPltTextbox.addEventListener(Events.ON_OK, new fakturNumberPltTextboxListener());
		// set default company
		defaultCompany = getCompanyDao().findCompanyById(DEF_COMPANY_IDX);
		
		// load customer combobox
		loadCustomerCombobox();
		// select 1st customer
		if (!customerCombobox.getItems().isEmpty()) {
			customerCombobox.setSelectedIndex(0);
			selCustomer = customerCombobox.getSelectedItem().getValue();
			
			// load invoice by selCustomer
			loadInvoiceBySelCustomer();
			// latest invoice for this customer
			setActiveInvoice();
			// display invoice info
			displayInvoiceInfo();
			
		}
	}

	@SuppressWarnings("unchecked")
	private void displayInvoiceInfo() throws Exception {
		if (activeInvoice==null) {
			log.info("clear InvoiceInfo");
			// reset all
			clearInvoiceInfo();
		} else {
			// display total
			// subtotal01JasaLabel.setValue(toDecimalFormat(new BigDecimal(activeInvoice.getTotal_invoice()), getLocale(), getDecimalFormat()));
			// ppnJasaLabel.setValue(toDecimalFormat(new BigDecimal(activeInvoice.getAmount_ppn()), getLocale(), getDecimalFormat()));
			// double subtotal = activeInvoice.getTotal_invoice() + activeInvoice.getAmount_ppn();
			// subtotal02JasaLabel.setValue(toDecimalFormat(new BigDecimal(subtotal), getLocale(), getDecimalFormat()));
			// pph23JasaLabel.setValue("-"+toDecimalFormat(new BigDecimal(activeInvoice.getAmount_pph()), getLocale(), getDecimalFormat()));
			// double total = subtotal - activeInvoice.getAmount_pph();
			// totalJasaLabel.setValue(toDecimalFormat(new BigDecimal(total), getLocale(), getDecimalFormat()));
			// jasa
			customerNameLabel.setValue(activeInvoice.getInvc_customer().getCompanyType()+"."+
					activeInvoice.getInvc_customer().getCompanyLegalName());
			invoiceDateLabel.setValue(dateToStringDisplay(activeInvoice.getInvc_date(), 
					getShortDateFormat(), getLocale()));
			invoiceNumberLabel.setValue(activeInvoice.getInvc_ser().getSerialComp());
			if (activeInvoice.getJasaKwitansi()==null) {
				// allow user to create and display - enable the createKwitansiButton
				createKwitansiButton.setVisible(true);
				kwitansiNumberLabel.setValue("");
			} else {
				createKwitansiButton.setVisible(false);
				kwitansiNumberLabel.setValue(activeInvoice
						.getJasaKwitansi().getKwitansi_ser().getSerialComp());
			}
			if (activeInvoice.getJasaFaktur()==null) {
				fakturNumberLabel.setValue("ClickToAdd");				
			} else {
				fakturNumberLabel.setValue(activeInvoice.getJasaFaktur().getFaktur_number());
			}
			fakturNumberLabel.addEventListener(Events.ON_CLICK, new fakturNumberLabelClickListener());
			fakturNumberLabel.setStyle("cursor:pointer;");
			
			// bahan
			invoicePltDateLabel.setValue(dateToStringDisplay(activeInvoice.getInvc_date(), 
					getShortDateFormat(), getLocale()));
			invoiceNumberPltLabel.setValue(activeInvoice.getInvc_ser().getSerialComp());
			if (activeInvoice.getBahanKwitansi()==null) {
				// allow user to create
				createKwitansiPltButton.setVisible(true);
				kwitansiNumberPltLabel.setValue("");
			} else {
				createKwitansiPltButton.setVisible(false);
				kwitansiNumberPltLabel.setValue(activeInvoice
						.getBahanKwitansi().getKwitansi_ser().getSerialComp());
			}
			if (activeInvoice.getBahanFaktur()==null) {
				fakturNumberPltLabel.setValue("ClickToAdd");
			} else {
				fakturNumberPltLabel.setValue(activeInvoice.getBahanFaktur().getFaktur_number());

			}
			fakturNumberPltLabel.addEventListener(Events.ON_CLICK, new fakturNumberPltLabelClickListener());
			fakturNumberPltLabel.setStyle("cursor:pointer;");
			// clear from previous objects
			invoiceProductList.clear();
			invoiceProductList.addAll(activeInvoice.getInvoiceProducts());
			
			// render
			renderInvoiceProduct();
			// render bahan
			activeInvoice = getInvoiceDao().findInvoicePalletsByProxy(activeInvoice.getId());
			// activeInvoice.getInvoicePallet().forEach(p -> log.info(p.toString()));
			palletList = activeInvoice.getInvoicePallet();
			renderPalletListbox();
		}
	}
	
	public void onClick$createKwitansiButton(Event event) throws Exception {
		log.info("createKwitansiButton click");
		
		// set
		activeInvoice.setJasaKwitansi(createKwitansi());
		// save
		activeInvoice = getInvoiceDao().update(activeInvoice);
		// display
		kwitansiNumberLabel.setValue(activeInvoice
				.getJasaKwitansi().getKwitansi_ser().getSerialComp());
		
		// hide this button
		createKwitansiButton.setVisible(false);
	}

	public void onClick$createKwitansiPltButton(Event event) throws Exception {
		log.info("createKwitansiPltButton click");
		
		// set
		activeInvoice.setBahanKwitansi(createKwitansi());
		// save
		activeInvoice = getInvoiceDao().update(activeInvoice);
		// display
		kwitansiNumberPltLabel.setValue(activeInvoice
				.getBahanKwitansi().getKwitansi_ser().getSerialComp());
		
		// hide this button
		createKwitansiPltButton.setVisible(false);
	}

	private Ent_InvoiceKwitansi createKwitansi() throws Exception {
		Ent_InvoiceKwitansi kwitansi = new Ent_InvoiceKwitansi();
		kwitansi.setAmount(0.0);
		kwitansi.setAmount_words("");
		kwitansi.setKwitansi_date(activeInvoice.getInvc_date());
		kwitansi.setKwitansi_for(activeInvoice.getInvc_customer().getCompanyType()+"."+
				activeInvoice.getInvc_customer().getCompanyLegalName());
		kwitansi.setKwitansi_ser(getDocumentSerial(Enm_TypeDocument.KWITANSI, 
				activeInvoice.getInvc_date()));

		return kwitansi;
	}
	
	@SuppressWarnings("rawtypes")
	public class fakturNumberLabelClickListener implements EventListener {

		@Override
		public void onEvent(Event event) throws Exception {
			log.info("fakturNumberLabel click...");
			fakturNumberLabel.setVisible(false);
			fakturNumberTextbox.setValue("");
			fakturNumberTextbox.setVisible(true);
		}		
	}
	
	@SuppressWarnings("rawtypes")
	public class fakturNumberPltLabelClickListener implements EventListener {

		@Override
		public void onEvent(Event event) throws Exception {
			log.info("fakturNumberPltLabel click...");
			fakturNumberPltLabel.setVisible(false);
			fakturNumberPltTextbox.setValue("");
			fakturNumberPltTextbox.setVisible(true);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public class fakturNumberTextboxListener implements EventListener {

		@Override
		public void onEvent(Event event) throws Exception {
			Ent_InvoiceFaktur invcFaktur;
			log.info("fakturNumberTextbox...:"+fakturNumberTextbox.getValue());
			if (activeInvoice.getJasaFaktur()==null) {
				// create
				invcFaktur = new Ent_InvoiceFaktur();
				invcFaktur.setFaktur_date(activeInvoice.getInvc_date());
				invcFaktur.setFaktur_number(fakturNumberTextbox.getValue());
				// set
				activeInvoice.setJasaFaktur(invcFaktur);
			} else {
				// modify
				activeInvoice.getJasaFaktur().setFaktur_number(fakturNumberTextbox.getValue());
			}
			// update
			activeInvoice = getInvoiceDao().update(activeInvoice);
			// display
			fakturNumberTextbox.setVisible(false);
			fakturNumberLabel.setVisible(true);
			fakturNumberLabel.setValue(activeInvoice.getJasaFaktur().getFaktur_number());
			// notif
			Clients.showNotification(
					   "No.Faktur berhasil disimpan", "info", null, "bottom_left", 10000);
		}	
	}
	
	@SuppressWarnings("rawtypes")
	public class fakturNumberPltTextboxListener implements EventListener {

		@Override
		public void onEvent(Event event) throws Exception {
			Ent_InvoiceFaktur invcFaktur;			
			log.info("fakturNumberPltTextbox...:"+fakturNumberPltTextbox.getValue());
			if (activeInvoice.getBahanFaktur()==null) {
				// create
				invcFaktur = new Ent_InvoiceFaktur();
				invcFaktur.setFaktur_date(activeInvoice.getInvc_date());
				invcFaktur.setFaktur_number(fakturNumberPltTextbox.getValue());
				// set
				activeInvoice.setBahanFaktur(invcFaktur);
			} else {
				// modify
				activeInvoice.getBahanFaktur().setFaktur_number(fakturNumberPltTextbox.getValue());
			}
			// update
			activeInvoice = getInvoiceDao().update(activeInvoice);
			// display
			fakturNumberPltTextbox.setVisible(false);
			fakturNumberPltLabel.setVisible(true);
			fakturNumberPltLabel.setValue(activeInvoice.getBahanFaktur().getFaktur_number());
			// notif
			Clients.showNotification(
					   "No.Faktur berhasil disimpan", "info", null, "bottom_left", 10000);
		}
		
	}
	
	private void setActiveInvoice() throws Exception {
		int latestInvc = invoiceModelList.getSize()-1;
		if (latestInvc>=0) {
			activeInvoice = invoiceModelList.get(latestInvc);
			// init to load invoiceProduct
			activeInvoice = getInvoiceDao().findInvoiceProductsByProxy(activeInvoice.getId());
		} else {
			activeInvoice = null;
		}
	}

	private void loadCustomerCombobox() throws Exception {
		// clear items before adding
		customerCombobox.getItems().clear();
		// get from db
		List<Ent_Customer> customerList = getCustomerDao().findAllCustomer();
		Comboitem comboitem;
		for (Ent_Customer customer : customerList) {
			comboitem = new Comboitem();
			comboitem.setLabel(customer.getCompanyType()+"."+
					customer.getCompanyLegalName());
			comboitem.setValue(customer);
			comboitem.setParent(customerCombobox);
		}
	}

	public void onSelect$customerCombobox(Event event) throws Exception {
		selCustomer = customerCombobox.getSelectedItem().getValue();
		// load invoice by selCustomer
		loadInvoiceBySelCustomer();
		// latest invoice for this customer
		setActiveInvoice();
		// display invoice info
		displayInvoiceInfo();

	}
	
	private void loadInvoiceBySelCustomer() throws Exception {
		invoiceModelList = new ListModelList<Ent_Invoice>(
				getInvoiceDao().findInvoiceByCustomer(selCustomer));
		
		invoiceListbox.setModel(invoiceModelList);
		invoiceListbox.setItemRenderer(getInvoiceListitemRenderer());
	}
	
	private ListitemRenderer<Ent_Invoice> getInvoiceListitemRenderer() {
		
		return new ListitemRenderer<Ent_Invoice>() {
			
			@Override
			public void render(Listitem item, Ent_Invoice invoice, int index) throws Exception {
				Listcell lc;
				
				// No.
				lc = new Listcell(invoice.getInvc_ser()==null ? 
						"" : invoice.getInvc_ser().getSerialComp());
				lc.setParent(item);
				
				// Tgl.
				lc = new Listcell(invoice.getInvc_date()==null ?
						"" : dateToStringDisplay(invoice.getInvc_date(), 
								getShortDateFormat(), getLocale()));
				lc.setParent(item);
				
				// status
				lc = new Listcell();
				lc.setParent(item);

				if (invoice.isAddInProgress()) {
					Label label = new Label("New");
					label.setSclass("badge bg-success");
					label.setParent(lc);
				} else {
					
				}

				item.setValue(invoice);
			}
		};
	}
	
	public void onSelect$invoiceListbox(Event event) throws Exception {
		log.info("invoiceListbox select");
		if (activeInvoice.isAddInProgress()) {
			// cancel add
			log.info("cancel add");
			cancelAdd();			
		} else {
			activeInvoice = invoiceListbox.getSelectedItem().getValue();
			// init to load invoiceProduct
			activeInvoice = getInvoiceDao().findInvoiceProductsByProxy(activeInvoice.getId());
			// display invoice info
			displayInvoiceInfo();
		}
	}
	
	public void onClick$invoiceAddButton(Event event) throws Exception {
		log.info("invoiceAddButton click");
		
		// clear total
		clearInvoiceInfo();

		invoiceListbox.renderAll();
		// create new invoice
		createInvoice();
		// insert to last pos
		invoiceModelList.add(0, activeInvoice);
		// use invoiceTabbox
		invoiceTabbox.setSelectedIndex(0);
		// jasa - enable suratjalan selection
		suratjalanGrid.setVisible(true);
		invoiceDateLabel.setValue(dateToStringDisplay(activeInvoice.getInvc_date(), 
								getShortDateFormat(), getLocale()));
		invoiceNumberLabel.setValue(activeInvoice.getInvc_ser().getSerialComp());
		// bahan
		invoicePltDateLabel.setValue(dateToStringDisplay(activeInvoice.getInvc_date(), 
				getShortDateFormat(), getLocale()));
		invoiceNumberPltLabel.setValue(activeInvoice.getInvc_ser().getSerialComp());

		// load suratjalan to invoice by selCustomer
		loadSuratJalanCombobox();
		// allow to cancel or save
		cancelAddButton.setVisible(true);
		saveAddButton.setVisible(true);
		// not allow to edit
		editButton.setVisible(false);
	}

	private void clearInvoiceInfo() throws Exception {
		subtotal01JasaLabel.setValue(""); 
		ppnJasaLabel.setValue("");
		subtotal02JasaLabel.setValue("");
		pph23JasaLabel.setValue("");
		totalJasaLabel.setValue("");
		subtotalPalletLabel.setValue("");
		ppnPalletLabel.setValue("");
		totalPalletLabel.setValue("");
		
		// jasa label
		invoiceDateLabel.setValue("");
		invoiceNumberLabel.setValue("");
		kwitansiNumberLabel.setValue("");
		fakturNumberLabel.setValue("");
		// render with empty array
		invoiceProductList.clear();
		renderInvoiceProduct();
		if (palletList!=null) {
			palletList.clear();
			renderPalletListbox();			
		}
		
		// bahan label
		invoicePltDateLabel.setValue("");
		invoiceNumberPltLabel.setValue("");
		kwitansiNumberPltLabel.setValue("");
		fakturNumberPltLabel.setValue("");
	}

	private void createInvoice() {
		activeInvoice = new Ent_Invoice();
		activeInvoice.setInvc_date(getLocalDate(getZoneId()));
		activeInvoice.setInvc_ser(getDocumentSerial(Enm_TypeDocument.FAKTUR,
				getLocalDate(getZoneId())));
		activeInvoice.setAddInProgress(true);		
	}

	private Ent_Serial getDocumentSerial(Enm_TypeDocument typeDocument, LocalDate localDate) {
		int serialNum = getSerialNumberGenerator()
				.getSerialNumber(typeDocument, localDate, defaultCompany);
		
		Ent_Serial serial = new Ent_Serial();
		serial.setCompany(defaultCompany);
		serial.setDocumentType(typeDocument);
		serial.setSerialDate(localDate);
		serial.setSerialNumber(serialNum);
		serial.setSerialComp(
				formatSerialComp(typeDocument.toCode(typeDocument.getValue()),localDate,serialNum));
		
		log.info(serial.toString());
		
		return serial;
	}

	private void loadSuratJalanCombobox() throws Exception {
		suratjalanCombobox.getItems().clear();
		List<Ent_SuratJalan> suratjalanList = 
				getSuratjalanDao().findSuratJalanByCustomer(selCustomer);
		if (selSuratJalanList.isEmpty()) {
			// do nothing
		} else {
			suratjalanList.removeAll(selSuratJalanList);
		}
		Comboitem comboitem;
		for(Ent_SuratJalan suratjalan : suratjalanList) {
			comboitem = new Comboitem();
			comboitem.setLabel(suratjalan.getSuratjalanSerial().getSerialComp());
			comboitem.setValue(suratjalan);
			comboitem.setParent(suratjalanCombobox);
		}
	}
	
	public void onClick$suratjalanAddButton(Event event) throws Exception {
		log.info("suratjalanAddButton click");
		Ent_SuratJalan selSuratJalan;
		// get the selected suratjalan in the suratjalanCombobox
		if (suratjalanCombobox.getSelectedItem()!=null) {
			selSuratJalan = suratjalanCombobox.getSelectedItem().getValue();
			// log.info("sel:"+selSuratJalan.toString());
			selSuratJalanList.add(selSuratJalan);
			// find the suratjalanproduct and transform invoiceproduct
			selSuratJalan = getSuratjalanDao().getSuratJalanProductByProxy(selSuratJalan.getId());
			// transform
			loadInvoiceProduct(selSuratJalan);
			// render
			renderInvoiceProduct();
			// re-load suratjalanCombobox (remove the selected suratjalan)
			loadSuratJalanCombobox();
			// clear the combobox
			suratjalanCombobox.setValue("");

		}
	}
	
	private void loadInvoiceProduct(Ent_SuratJalan suratjalan) throws Exception {
		if (invoiceProductList.isEmpty()) {
			invoiceProductList = 
					suratjalanProductToInvoiceProductList(suratjalan);
		} else {
			List<Ent_InvoiceProduct> invoiceProducts =
					suratjalanProductToInvoiceProductList(suratjalan);
			invoiceProductList.addAll(invoiceProducts);
		}
	}

	private List<Ent_InvoiceProduct> suratjalanProductToInvoiceProductList(Ent_SuratJalan suratjalan) throws Exception {
		List<Ent_InvoiceProduct> productList = new ArrayList<Ent_InvoiceProduct>();
		Ent_InvoiceProduct invoiceProduct;
		for(Ent_SuratJalanProduct suratjalanProduct : suratjalan.getSuratjalanProducts()) {
			invoiceProduct = new Ent_InvoiceProduct();
			invoiceProduct.setRef_suratjalan(suratjalan);
			invoiceProduct.setMarking(suratjalanProduct.getMarking());
			invoiceProduct.setRef_document(suratjalan.getRefDocument());
			invoiceProduct.setSpek(
					toDecimalFormat(new BigDecimal(suratjalanProduct.getThickness()), getLocale(), "#0,00")+" x "+
					toDecimalFormat(new BigDecimal(suratjalanProduct.getWidth()), getLocale(), "###.###")+" x "+
					toDecimalFormat(new BigDecimal(suratjalanProduct.getLength()), getLocale(), "###.###"));
			invoiceProduct.setThickness(suratjalanProduct.getThickness());
			invoiceProduct.setWidth(suratjalanProduct.getWidth());
			invoiceProduct.setLength(suratjalanProduct.getLength());
			invoiceProduct.setQuantity_by_sht(suratjalanProduct.getQuantityBySht());
			invoiceProduct.setQuantity_by_kg(suratjalanProduct.getQuantityByKg());
			invoiceProduct.setUnit_price(0.0);
			invoiceProduct.setSub_total(0.0);
			invoiceProduct.setUse_pallet(false);
			invoiceProduct.setInventoryCode(suratjalanProduct.getInventoryCode());
			invoiceProduct.setEditInProgress(true);
			
			productList.add(invoiceProduct);
		}
		
		return productList;
	}

	private void renderInvoiceProduct() throws Exception {
		ListModelList<Ent_InvoiceProduct> invoiceProductListModel = 
				new ListModelList<Ent_InvoiceProduct>(invoiceProductList);
		// render
		invoiceProductListbox.setModel(invoiceProductListModel);
		invoiceProductListbox.setItemRenderer(getInvoiceProductListitemRenderer());
	}	
	
	private ListitemRenderer<Ent_InvoiceProduct> getInvoiceProductListitemRenderer() {
		
		return new ListitemRenderer<Ent_InvoiceProduct>() {
			
			@Override
			public void render(Listitem item, Ent_InvoiceProduct product, int index) throws Exception {
				Listcell lc;
				
				// SJ
				lc = new Listcell(product.getRef_suratjalan().getSuratjalanSerial().getSerialComp());
				lc.setParent(item);
				
				// No.Coil
				lc = new Listcell(product.getMarking());
				lc.setParent(item);
				
				// PO
				lc = new Listcell(product.getRef_document());
				lc.setParent(item);
				
				// Spek
				lc = new Listcell(
						toDecimalFormat(new BigDecimal(product.getThickness()), getLocale(), "#0,00")+" x "+
						toDecimalFormat(new BigDecimal(product.getWidth()), getLocale(), "###.###")+" x "+
						toDecimalFormat(new BigDecimal(product.getLength()), getLocale(), "###.###"));
				lc.setParent(item);
				
				// Pcs
				lc = new Listcell(getFormatedInteger(product.getQuantity_by_sht()));
				lc.setParent(item);
				
				// Berat
				lc = new Listcell(toDecimalFormat(new BigDecimal(product.getQuantity_by_kg()), getLocale(), "###.###"));
				lc.setParent(item);
				
				// Rp/Kg
				lc = new Listcell(toDecimalFormat(new BigDecimal(product.getUnit_price()), getLocale(), "#.###.###"));
				lc.setParent(item);
				
				// Jumlah
				lc = new Listcell(toDecimalFormat(new BigDecimal(product.getSub_total()), getLocale(), "##.###.###"));
				lc.setParent(item);
				
				// Plt
				lc = new Listcell();
				lc.setParent(item);
				Checkbox checkbox = new Checkbox();
				checkbox.setDisabled(!product.isEditInProgress());
				checkbox.setChecked(product.isUse_pallet());
				checkbox.setParent(lc);
				
				// edit/save
				lc = new Listcell();
				lc.setParent(item);
				Button button = new Button();
				button.setVisible(product.isEditInProgress());
				button.setIconSclass("");
				button.setParent(lc);
				button.setSclass("compButton");
				modifToEdit(button);
				button.addEventListener(Events.ON_CLICK, editInvoiceProduct(product));

				item.setValue(product);
			}
		};
	}	
	
	public void onAfterRender$invoiceProductListbox(Event event) throws Exception {
		double jumlahJasa = calcJumlahJasa();
		subtotal01JasaLabel.setValue(
				toDecimalFormat(new BigDecimal(jumlahJasa), getLocale(), getDecimalFormat()));
		log.info(String.valueOf(jumlahJasa));
		double jumlahPpn = PPN * jumlahJasa / 100;
		ppnJasaLabel.setValue(
				toDecimalFormat(new BigDecimal(jumlahPpn), getLocale(), getDecimalFormat()));
		log.info(String.valueOf(jumlahPpn));
		double jumlahTotalJasa = jumlahJasa + jumlahPpn;
		subtotal02JasaLabel.setValue(
				toDecimalFormat(new BigDecimal(jumlahTotalJasa), getLocale(), getDecimalFormat()));
		double jumlahPph = PPH * jumlahJasa / 100;
		log.info(String.valueOf(jumlahPpn));
		pph23JasaLabel.setValue("-"+
				toDecimalFormat(new BigDecimal(jumlahPph), getLocale(), getDecimalFormat()));
		double jumlahTotalJasaDecPph = jumlahTotalJasa - jumlahPph;
		log.info(String.valueOf(jumlahTotalJasaDecPph));
		totalJasaLabel.setValue(
				toDecimalFormat(new BigDecimal(jumlahTotalJasaDecPph), getLocale(), getDecimalFormat()));
				
	}

	private double calcJumlahJasa() {
		double jumlah = 0;
		if (activeInvoice.getInvoiceProducts()!=null) {
			for(Ent_InvoiceProduct invcProd : activeInvoice.getInvoiceProducts()) {
				jumlah = jumlah + invcProd.getSub_total();
			}			
		}
		return jumlah;
	}
	
	
	protected EventListener<Event> editInvoiceProduct(Ent_InvoiceProduct product) {

		return new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				log.info("edit/save invoiceproduct button click");
				Button button = (Button) event.getTarget();
				Listitem activeItem = (Listitem) event.getTarget().getParent().getParent();
				if (product.isEditInProgress()) {
					log.info("to edit invoiceProduct click");
					product.setEditInProgress(false);

					// set columns to edit
					setMarking(activeItem, product.getMarking(),1);
					setPO(activeItem, product.getRef_document());
					setSpek(activeItem, product.getThickness(), product.getWidth(), product.getLength());
					setPcs(activeItem, product.getQuantity_by_sht());
					setBerat(activeItem, product.getQuantity_by_kg());
					setRpKg(activeItem, product.getUnit_price());
					setJumlah(activeItem, product.getSub_total());
					setUsePalette(activeItem, product.isUse_pallet());
					// transform this button to save
					modifToSave(button);
				} else {
					log.info("to save invoiceProduct click");
					product.setEditInProgress(true);
					// get updated product
					product.setMarking(getMarking(activeItem,1));
					product.setRef_document(getPO(activeItem));
					product.setThickness(getSpek(activeItem,0));
					product.setWidth(getSpek(activeItem,2));
					product.setLength(getSpek(activeItem,4));
					product.setQuantity_by_sht(getPcs(activeItem));
					product.setQuantity_by_kg(getBerat(activeItem));
					product.setUnit_price(getRpKg(activeItem));
					product.setSub_total(getJumlah(activeItem));
					product.setUse_pallet(getUsePallete(activeItem));
					
					// re-render
					renderInvoiceProduct();
					
					// transform this button to edit
					modifToEdit(button);					
				}
			}
		};
	}

	protected String getMarking(Listitem item, int idx) {
		Listcell lc = (Listcell) item.getChildren().get(idx);
		
		Textbox textbox = (Textbox) lc.getFirstChild();
		
		return textbox.getValue();
	}
	
	protected void setMarking(Listitem activeItem, String marking, int idx) {
		Listcell lc = (Listcell) activeItem.getChildren().get(idx);
		lc.setLabel(" ");
		Textbox textbox = new Textbox();
		textbox.setValue(marking);
		textbox.setWidth("100px");
		textbox.setParent(lc);
	}

	protected String getPO(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(2);
		
		Textbox textbox = (Textbox) lc.getFirstChild();
		
		return textbox.getValue();
	}	

	protected void setPO(Listitem activeItem, String ref_document) {
		Listcell lc = (Listcell) activeItem.getChildren().get(2);
		lc.setLabel("");
		Textbox textbox = new Textbox();
		textbox.setValue(ref_document);
		textbox.setWidth("100px");
		textbox.setParent(lc);
	}

	private double getSpek(Listitem activeItem, int subIdx) {
		Listcell lc = (Listcell) activeItem.getChildren().get(3);
		
		Doublebox doublebox = (Doublebox) lc.getChildren().get(subIdx);
		
		return doublebox.getValue();
	}	
	
	protected void setSpek(Listitem activeItem, double thickness, double width, double length) {
		Listcell lc = (Listcell) activeItem.getChildren().get(3);
		lc.setLabel("");
		// thickness
		Doublebox doublebox = new Doublebox();
		doublebox.setLocale(getLocale());
		doublebox.setWidth("60px");
		doublebox.setValue(thickness);
		doublebox.setParent(lc);
		Label label = new Label();
		label.setValue(" x ");
		label.setParent(lc);
		// width
		doublebox = new Doublebox();
		doublebox.setLocale(getLocale());
		doublebox.setWidth("60px");
		doublebox.setValue(width);
		doublebox.setParent(lc);
		label = new Label();
		label.setValue(" x ");
		label.setParent(lc);
		// length
		doublebox = new Doublebox();
		doublebox.setLocale(getLocale());
		doublebox.setWidth("60px");
		doublebox.setValue(length);
		doublebox.setParent(lc);
	}	

	protected int getPcs(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(4);
		
		Intbox intbox = (Intbox) lc.getFirstChild();
		
		return intbox.getValue();
	}

	protected void setPcs(Listitem activeItem, int quantity_by_sht) {
		Listcell lc = (Listcell) activeItem.getChildren().get(4);
		lc.setLabel("");
		Intbox intbox = new Intbox();
		intbox.setValue(quantity_by_sht);
		intbox.setWidth("100px");
		intbox.setParent(lc);
	}

	protected double getBerat(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(5);

		Doublebox doublebox = (Doublebox) lc.getFirstChild();
		
		return doublebox.getValue();
	}	

	protected void setBerat(Listitem activeItem, double quantity_by_kg) {
		Listcell lc = (Listcell) activeItem.getChildren().get(5);
		lc.setLabel("");
		Doublebox doublebox = new Doublebox();
		doublebox.setValue(quantity_by_kg);
		doublebox.setWidth("100px");
		doublebox.setParent(lc);
		doublebox.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Listcell lc;
				Doublebox doublebox;
				double qtyKg, rpKg, subtotal;
				doublebox = (Doublebox) event.getTarget();
				qtyKg = doublebox.getValue();
				log.info("calc Jumlah:"+qtyKg);
				// get the 'RpKg'
				lc = (Listcell) activeItem.getChildren().get(6);
				doublebox = (Doublebox) lc.getFirstChild();
				rpKg = doublebox.getValue();
				log.info("calc Jumlah:"+rpKg);
				// calc
				subtotal = qtyKg * rpKg;
				log.info("calc Jumlah:"+subtotal);
				// jumlah
				lc = (Listcell) activeItem.getChildren().get(7);				
				doublebox = (Doublebox) lc.getFirstChild();
				doublebox.setValue(subtotal);				
			}
		});
	}

	protected double getRpKg(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(6);
		
		Doublebox doublebox = (Doublebox) lc.getFirstChild();
		
		return doublebox.getValue();
	}

	protected void setRpKg(Listitem activeItem, double unit_price) {
		Listcell lc = (Listcell) activeItem.getChildren().get(6);
		lc.setLabel("");
		Doublebox doublebox = new Doublebox();
		doublebox.setValue(unit_price);
		doublebox.setWidth("100px");
		doublebox.setParent(lc);
		doublebox.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Listcell lc;
				Doublebox doublebox;
				double qtyKg, rpKg, subtotal;
				doublebox = (Doublebox) event.getTarget();
				rpKg = doublebox.getValue();
				log.info("calc Jumlah:"+rpKg);
				// get the 'Berat'
				lc = (Listcell) activeItem.getChildren().get(5);
				doublebox = (Doublebox) lc.getFirstChild();
				qtyKg = doublebox.getValue();
				log.info("calc Jumlah:"+qtyKg);
				// calc
				subtotal = qtyKg * rpKg;
				log.info("calc Jumlah:"+subtotal);
				// jumlah
				lc = (Listcell) activeItem.getChildren().get(7);				
				doublebox = (Doublebox) lc.getFirstChild();
				doublebox.setValue(subtotal);
			}
		});
	}	

	protected double getJumlah(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(7);
		
		Doublebox doublebox = (Doublebox) lc.getFirstChild();
		
		return doublebox.getValue();
	}

	protected void setJumlah(Listitem activeItem, double sub_total) {
		Listcell lc = (Listcell) activeItem.getChildren().get(7);
		lc.setLabel("");
		Doublebox doublebox = new Doublebox();
		doublebox.setValue(sub_total);
		doublebox.setWidth("100px");
		doublebox.setParent(lc);
		
	}
	
	protected boolean getUsePallete(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(8);
		Checkbox checkbox = (Checkbox) lc.getFirstChild();
		checkbox.setDisabled(true);
		
		return checkbox.isChecked();
	}	
	
	protected void setUsePalette(Listitem activeItem, boolean use_pallet) {
		Listcell lc = (Listcell) activeItem.getChildren().get(8);
		
		Checkbox checkbox = (Checkbox) lc.getFirstChild();
		checkbox.setDisabled(false);
	}	
	
	public void onClick$cancelAddButton(Event event) throws Exception {
		cancelAdd();
	}
	
	private void cancelAdd() throws Exception {
		// re-load invoice
		loadInvoiceBySelCustomer();
		// latest invoice for this customer
		setActiveInvoice();
		// display invoice info
		displayInvoiceInfo();
		// reset to invoiceTabbox
		invoiceTabbox.setSelectedIndex(0);
		// hide suratjalan selection
		suratjalanGrid.setVisible(false);
		// clear selection
		selSuratJalanList.clear();
		// hide cancel and save button
		cancelAddButton.setVisible(false);
		saveAddButton.setVisible(false);
	}
	
	public void onClick$saveAddButton(Event event) throws Exception {
		if (activeInvoice.isAddInProgress()) {
			// set kwitansi
			
			// set faktur
			
			// set invoiceProducts
			activeInvoice.setInvoiceProducts(invoiceProductList);
			// set others
			activeInvoice.setInvc_status(Enm_StatusDocument.Normal);
			activeInvoice.setInvc_type(Enm_TypeInvoice.normal);
			activeInvoice.setPay_type(Enm_TypePayment.bank);
			activeInvoice.setInvc_customer(selCustomer);
			// save
			activeInvoice = getInvoiceDao().update(activeInvoice);
			// latest invoice for this customer
			// setActiveInvoice();
			// display invoice info
			displayInvoiceInfo();
			// re-load invoice
			loadInvoiceBySelCustomer();
			// reset to invoiceTabbox
			// invoiceTabbox.setSelectedIndex(0);
			// hide suratjalan selection
			suratjalanGrid.setVisible(false);
			// clear selected list
			selSuratJalanList.clear();
		} else {

		}
		// hide cancel and save button
		cancelAddButton.setVisible(false);
		saveAddButton.setVisible(false);
		// allow user to edit
		editButton.setVisible(true);
	}
	
	public void onClick$editButton(Event event) throws Exception {
		log.info("editButton click");
		
		invoiceProductList.forEach(p -> {
			p.setEditInProgress(true);
		});
		// re-render
		renderInvoiceProduct();
		// set to 'inProgress'
		activeInvoice.setAddInProgress(true);
		// allow user to save or batal
		saveAddButton.setVisible(true);
		cancelAddButton.setVisible(true);
	}
	
	public void onClick$palletAddButton(Event event) throws Exception {
		log.info("palletAddButton click");		
		if (activeInvoice.isAddInProgress()) {
			palletList = 
					transformToInvoicePallet(invoiceProductList);
							//activeInvoice.getInvoiceProducts());
		} else {
			
			if (palletList.isEmpty()) {
				palletList =
						transformToInvoicePallet(activeInvoice.getInvoiceProducts());				
			} else {
				// grab the suratjalan
				Ent_SuratJalan ref_suratJalan = palletList.get(0).getRef_suratjalan();
				// add another item
				Ent_InvoicePallet pallet = new Ent_InvoicePallet();
				pallet.setMarking("");
				pallet.setPallet_price(0.0);
				pallet.setPallet_subtotal(0.0);
				pallet.setQty_pcs(0);
				pallet.setRef_suratjalan(ref_suratJalan);
				pallet.setEditInProgress(true);

				palletList.add(pallet);
			}
			
		}
		
		// allow user to cancel or save
		cancelAddPltButton.setVisible(true);
		saveAddPltButton.setVisible(true);
		// render
		renderPalletListbox();
	}
	
	protected List<Ent_InvoicePallet> transformToInvoicePallet(List<Ent_InvoiceProduct> invoiceProducts) {
		List<Ent_InvoicePallet> palletList = new ArrayList<Ent_InvoicePallet>();
		// get all the products and transform
		invoiceProducts.forEach(p -> {
			if (p.isUse_pallet()) {
				Ent_InvoicePallet pallet = new Ent_InvoicePallet();
				pallet.setMarking(p.getMarking());
				pallet.setKeterangan("Biaya Pallet");
				pallet.setPallet_price(0.0);
				pallet.setPallet_subtotal(0.0);
				pallet.setQty_pcs(0);
				pallet.setRef_suratjalan(p.getRef_suratjalan());
				pallet.setEditInProgress(true);
				
				palletList.add(pallet);					
			}
		});

		return palletList;
	}

	protected void renderPalletListbox() {
		// render
		ListModelList<Ent_InvoicePallet> palletModelList =
				new ListModelList<Ent_InvoicePallet>(palletList);
		palletListbox.setModel(palletModelList);
		palletListbox.setItemRenderer(getPalletListitemRenderer());		
	}
	
	private ListitemRenderer<Ent_InvoicePallet> getPalletListitemRenderer() {
		
		return new ListitemRenderer<Ent_InvoicePallet>() {
			
			@Override
			public void render(Listitem item, Ent_InvoicePallet pallet, int index) throws Exception {
				Listcell lc;
				
				// SJ
				lc = new Listcell(pallet.getRef_suratjalan().getSuratjalanSerial().getSerialComp());
				lc.setParent(item);
				
				// No.Coil
				lc = new Listcell(pallet.getMarking());
				lc.setParent(item);
				
				// Keterangan
				lc = new Listcell(pallet.getKeterangan());
				lc.setParent(item);
				
				// Pcs
				lc = new Listcell(getFormatedInteger(pallet.getQty_pcs()));
				lc.setParent(item);
				
				// Pallet
				lc = new Listcell(toDecimalFormat(new BigDecimal(pallet.getPallet_price()), getLocale(), getDecimalFormat()));
				lc.setParent(item);
				
				// Jumlah
				lc = new Listcell(toDecimalFormat(new BigDecimal(pallet.getPallet_subtotal()), getLocale(), getDecimalFormat()));
				lc.setParent(item);
				
				// Edit/Save
				lc = new Listcell();
				lc.setParent(item);
				Button button = new Button();
				button.setVisible(pallet.isEditInProgress());
				button.setIconSclass("");
				button.setParent(lc);
				button.setSclass("compButton");
				modifToEdit(button);
				button.addEventListener(Events.ON_CLICK, editInvoicePallet(pallet));
 
			}
		};
	}

	public void onAfterRender$palletListbox(Event event) throws Exception {
		double jumlahBahan = calcJumlahBahan();
		log.info("bhn - jumlah:"+jumlahBahan);
		subtotalPalletLabel.setValue(
				toDecimalFormat(new BigDecimal(jumlahBahan), getLocale(), getDecimalFormat()));
		double jumlahPpn = PPN * jumlahBahan / 100;
		log.info("bhn - ppn:"+jumlahPpn);
		ppnPalletLabel.setValue(
				toDecimalFormat(new BigDecimal(jumlahPpn), getLocale(), getDecimalFormat()));
		double jumlahTotalBahan = jumlahBahan + jumlahPpn;
		log.info("bhn - total:"+jumlahTotalBahan);
		totalPalletLabel.setValue(
				toDecimalFormat(new BigDecimal(jumlahTotalBahan), getLocale(), getDecimalFormat()));
	}
	
	private double calcJumlahBahan() throws Exception {
		double jumlah = 0;
		for(Ent_InvoicePallet invcPlt : palletList) {
			jumlah = jumlah + invcPlt.getPallet_subtotal();
		}
		return jumlah;
	}
	
	protected EventListener<Event> editInvoicePallet(Ent_InvoicePallet pallet) {
		
		return new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				Button button = (Button) event.getTarget();
				Listitem activeItem = (Listitem) event.getTarget().getParent().getParent();
				if (pallet.isEditInProgress()) {
					log.info("to edit invoice pallet");
					// set to save
					pallet.setEditInProgress(false);
					// set columns to edit
					setMarking(activeItem, pallet.getMarking(),1);
					setKeterangan(activeItem, pallet.getKeterangan());
					setPltPcs(activeItem, pallet.getQty_pcs());
					setPalletPrc(activeItem, pallet.getPallet_price());
					setPltJumlah(activeItem, pallet.getPallet_subtotal());
					
					// transform this button to save
					modifToSave(button);
				} else {
					log.info("to save invoice pallet");
					// set to edit
					pallet.setEditInProgress(true);
					// get edited invoice pallet
					pallet.setMarking(getMarking(activeItem,1));
					pallet.setKeterangan(getKeterangan(activeItem));
					pallet.setQty_pcs(getPltPcs(activeItem));
					pallet.setPallet_price(getPalletPrc(activeItem));
					pallet.setPallet_subtotal(getPltJumlah(activeItem));
					// re-render
					renderPalletListbox();
					
					// transform this button to edit
					modifToEdit(button);
				}
			}

		};
	}
	
	protected String getKeterangan(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(2);
		
		Textbox textbox = (Textbox) lc.getFirstChild();

		return textbox.getValue();
	}
	
	private void setKeterangan(Listitem activeItem, String keterangan) {
		Listcell lc = (Listcell) activeItem.getChildren().get(2);
		lc.setLabel("");
		Textbox textbox = new Textbox();
		textbox.setValue(keterangan);
		textbox.setWidth("150px");
		textbox.setParent(lc);
	}	

	protected int getPltPcs(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(3);
		
		Intbox intbox = (Intbox) lc.getFirstChild();
		
		return intbox.getValue();
	}

	protected void setPltPcs(Listitem activeItem, int qty_pcs) {
		Listcell lc = (Listcell) activeItem.getChildren().get(3);
		lc.setLabel("");
		Intbox intbox = new Intbox();
		intbox.setValue(qty_pcs);
		intbox.setWidth("100px");
		intbox.setParent(lc);
		intbox.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Listcell lc;
				Doublebox doublebox;
				Intbox intbox;
				double qtyPcs, rpPcs, subtotal;
				intbox = (Intbox) event.getTarget();
				qtyPcs = intbox.getValue();
				log.info("calc Jumlah - qty:"+qtyPcs);
				// get the Prc
				lc = (Listcell) activeItem.getChildren().get(4);
				doublebox = (Doublebox) lc.getFirstChild();
				rpPcs = doublebox.getValue();
				log.info("calc Jumlah - Rp:"+rpPcs);
				// calc
				subtotal = rpPcs * qtyPcs;
				log.info("calc Jumlah:"+subtotal);
				// jumlah
				lc = (Listcell) activeItem.getChildren().get(5);				
				doublebox = (Doublebox) lc.getFirstChild();
				doublebox.setValue(subtotal);
				
			}
		});
	}

	protected double getPalletPrc(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(4);
		
		Doublebox doublebox = (Doublebox) lc.getFirstChild();
		
		return doublebox.getValue();
	}

	protected void setPalletPrc(Listitem activeItem, double pallet_price) {
		Listcell lc = (Listcell) activeItem.getChildren().get(4);
		lc.setLabel("");
		Doublebox doublebox = new Doublebox();
		doublebox.setValue(pallet_price);
		doublebox.setWidth("100px");
		doublebox.setParent(lc);
		doublebox.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Listcell lc;
				Doublebox doublebox;
				Intbox intbox;
				double qtyPcs, rpPcs, subtotal;
				doublebox = (Doublebox) event.getTarget();
				rpPcs = doublebox.getValue();
				log.info("calc Jumlah - Rp:"+rpPcs);
				// get the Pcs
				lc = (Listcell) activeItem.getChildren().get(4);
				intbox = (Intbox) lc.getFirstChild();
				qtyPcs = intbox.getValue();
				log.info("calc Jumlah - Pcs:"+qtyPcs);
				// calc
				subtotal = rpPcs * qtyPcs;
				log.info("calc Jumlah:"+subtotal);
				// jumlah
				lc = (Listcell) activeItem.getChildren().get(5);				
				doublebox = (Doublebox) lc.getFirstChild();
				doublebox.setValue(subtotal);
			}
		});
	}
	
	protected double getPltJumlah(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(5);
		
		Doublebox doublebox = (Doublebox) lc.getFirstChild();
		
		return doublebox.getValue();
	}

	protected void setPltJumlah(Listitem activeItem, double pallet_subtotal) {
		Listcell lc = (Listcell) activeItem.getChildren().get(5);
		lc.setLabel("");
		Doublebox doublebox = new Doublebox();
		doublebox.setValue(pallet_subtotal);
		doublebox.setWidth("100px");
		doublebox.setParent(lc);
	}
	
	
	public void onClick$cancelAddPltButton(Event event) throws Exception {
		log.info("cancelAddPltButton click");
		// activeInvoice.getInvoicePallet().forEach(p -> log.info(p.toString()));
		if (activeInvoice.getInvoicePallet().isEmpty()) {
			palletList.clear();
			renderPalletListbox();			
		} else {
			// palletList.forEach(p -> log.info(p.toString()));
			palletList.forEach(p -> p.setEditInProgress(false));
			renderPalletListbox();
		}
		// create empty pallet list
		// List<Ent_InvoicePallet> palletList = new ArrayList<Ent_InvoicePallet>();
		// re-render
		// palletList = activeInvoice.getInvoicePallet();
		// palletList.forEach(p -> log.info(p.toString()));
		// make sure it's non-edit
		// palletList.forEach(p -> p.setEditInProgress(false));
		
		cancelAddPltButton.setVisible(false);
		saveAddPltButton.setVisible(false);
		// show edit button
		editPltButton.setVisible(true);
	}
	
	public void onClick$saveAddPltButton(Event event) throws Exception {
		log.info("saveAddPltButton click");
		// set kwitansi
		
		// set faktur
		
		// set invoicePallet
		activeInvoice.setInvoicePallet(palletList);
		// save
		getInvoiceDao().update(activeInvoice);
		// re-render
		palletList = activeInvoice.getInvoicePallet();
		// make sure it's non-edit
		palletList.forEach(p -> p.setEditInProgress(false));
		renderPalletListbox();

		cancelAddPltButton.setVisible(false);
		saveAddPltButton.setVisible(false);
	}
	
	public void onClick$editPltButton(Event event) throws Exception {
		log.info("editPltButton click");
		palletList.forEach(p -> p.setEditInProgress(true));
		// render
		renderPalletListbox();
		// allow user to cancel or save
		cancelAddPltButton.setVisible(true);
		saveAddPltButton.setVisible(true);
		// hide edit button
		editPltButton.setVisible(false);
	}
	
	public void onClick$printTagihanJasperReportButton(Event event) throws Exception {
		log.info("printTagihanJasperReportButton click");
		
		Map<String, Ent_Invoice> arg = Collections.singletonMap("activeInvoice", activeInvoice);
				
		Window tagihanPrintWin = (Window) Executions.createComponents("~./src/info_tagihan_jasper.zul", null, arg);
		
		tagihanPrintWin.doModal();
	}
	
	public void onClick$printKwitansiTagihanJasperReportButton(Event event) throws Exception {
		log.info("printKwitansiJasperReportButton click");
		
		Map<String, Ent_Invoice> arg = Collections.singletonMap("activeInvoice", activeInvoice);
		
		Window kwitansiPrintWin = (Window) Executions.createComponents("~./src/info_tagihan_kwitansi_jasper.zul", null, arg);
		
		kwitansiPrintWin.doModal();
	}
	
	public void onClick$printTagihanBahanJasperReportButton(Event event) throws Exception {
		log.info("printTagihanBahanJasperReportButton click");
		
		Map<String, Ent_Invoice> arg = Collections.singletonMap("activeInvoice", activeInvoice);
		
		Window tagihanBahanPrintWin = (Window) Executions.createComponents("~./src/info_tagihan_bahan_jasper.zul", null, arg);
		
		tagihanBahanPrintWin.doModal();
	}
	
	public void onClick$printKwitansiBahanJasperReportButton(Event event) throws Exception {
		log.info("printKwitansiBahanJasperReportButton click");

		Map<String, Ent_Invoice> arg = Collections.singletonMap("activeInvoice", activeInvoice);
		
		Window kwitansiPrintWin = (Window) Executions.createComponents("~./src/info_tagihan_bahan_kwitansi_jasper.zul", null, arg);
		
		kwitansiPrintWin.doModal();
	
	}
	
	protected void modifToSave(Button button) {
		button.setIconSclass("z-icon-floppy-disk");
		button.setStyle("background-color:var(--bs-primary);");
	}

	protected void modifToEdit(Button button) {
		button.setIconSclass("z-icon-pen-to-square");
		button.setStyle("background-color:var(--bs-warning);");			
	}
	
	public InvoiceDao getInvoiceDao() {
		return invoiceDao;
	}

	public void setInvoiceDao(InvoiceDao invoiceDao) {
		this.invoiceDao = invoiceDao;
	}

	public CustomerDao getCustomerDao() {
		return customerDao;
	}

	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

	public CompanyDao getCompanyDao() {
		return companyDao;
	}

	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}

	public SuratJalanDao getSuratjalanDao() {
		return suratjalanDao;
	}

	public void setSuratjalanDao(SuratJalanDao suratjalanDao) {
		this.suratjalanDao = suratjalanDao;
	}

	public NumToWordsConverter getNumToWordsConverter() {
		return numToWordsConverter;
	}

	public void setNumToWordsConverter(NumToWordsConverter numToWordsConverter) {
		this.numToWordsConverter = numToWordsConverter;
	}
	
}
