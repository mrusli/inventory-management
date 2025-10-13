package com.pyramix.web.suratjalan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

import com.pyramix.domain.entity.Enm_StatusDocument;
import com.pyramix.domain.entity.Enm_StatusProcess;
import com.pyramix.domain.entity.Enm_TypeDocument;
import com.pyramix.domain.entity.Ent_Company;
import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.domain.entity.Ent_InventoryProcess;
import com.pyramix.domain.entity.Ent_InventoryProcessMaterial;
import com.pyramix.domain.entity.Ent_InventoryProcessProduct;
import com.pyramix.domain.entity.Ent_Serial;
import com.pyramix.domain.entity.Ent_SuratJalan;
import com.pyramix.domain.entity.Ent_SuratJalanProduct;
import com.pyramix.persistence.company.dao.CompanyDao;
import com.pyramix.persistence.customer.dao.CustomerDao;
import com.pyramix.persistence.inventoryprocess.dao.InventoryProcessDao;
import com.pyramix.persistence.suratjalan.dao.SuratJalanDao;
import com.pyramix.web.common.GFCBaseController;
import com.pyramix.web.common.SerialNumberGenerator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SuratJalanController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7191024765696219776L;
	
	private CustomerDao customerDao;
	private CompanyDao companyDao;
	private InventoryProcessDao inventoryProcessDao;
	private SerialNumberGenerator serialNumberGenerator;
	private SuratJalanDao suratjalanDao;
	
	private Combobox customerCombobox, processCombobox;
	private Listbox suratJalanListbox, suratjalanProductListbox;
	private Div processSelDiv;
	private Button cancelAddButton, saveAddButton, editButton;
	private Label customerNameLabel, suratjalanDateLabel, suratjalanNumberLabel, nopolLabel,
		refdocLabel;
	private Textbox nopolTextbox, refdocTextbox;
	
	private Ent_SuratJalan currSuratJalan;
	private ListModelList<Ent_SuratJalanProduct> suratjalanProductModelList;
	private Ent_Company defaultCompany = null;
	private List<Ent_SuratJalan> suratjalanList;
	
	private static final Long DEF_COMPANY_IDX = (long) 3;
	
	public void onCreate$infoSuratJalanPanel(Event event) throws Exception {
		log.info("infoSuratJalanPanel created");

		// set default company
		defaultCompany = getCompanyDao().findCompanyById(DEF_COMPANY_IDX);
		
		List<Ent_Customer> customerList = getCustomerDao().findAllCustomer();
		// load customerCombbox
		loadCustomerCombobox(customerList);

		if (!customerCombobox.getItems().isEmpty()) {
			// select 1st customer
			customerCombobox.setSelectedIndex(0);
			// load
			loadSuratJalan();
			// render
			renderSuratJalan();
		}
	}

	private void loadSuratJalan() throws Exception {
		Ent_Customer selCustomer = customerCombobox.getSelectedItem().getValue();
		
		suratjalanList = 
				getSuratjalanDao().findSuratJalanByCustomer(selCustomer);		
	}

	private void renderSuratJalan() {
		ListModelList<Ent_SuratJalan> suratjalanModelList =
				new ListModelList<Ent_SuratJalan>(suratjalanList);
		suratJalanListbox.setModel(suratjalanModelList);
		suratJalanListbox.setItemRenderer(getSuratJalanListitemRenderer());
	}	
	
	private ListitemRenderer<Ent_SuratJalan> getSuratJalanListitemRenderer() {
		
		return new ListitemRenderer<Ent_SuratJalan>() {
			
			@Override
			public void render(Listitem item, Ent_SuratJalan suratjalan, int index) throws Exception {
				Listcell lc;
				// no
				lc = new Listcell(suratjalan.getSuratjalanSerial().getSerialComp());
				lc.setParent(item);
				// tgl
				lc = new Listcell(dateToStringDisplay(suratjalan.getSuratjalanDate(), 
						getShortDateFormat(), getLocale()));
				lc.setParent(item);
				
				item.setValue(suratjalan);
			}
		};
	}
	
	public void onAfterRender$suratJalanListbox(Event event) throws Exception {
		// select 1st item in the listbox
		if (!suratJalanListbox.getItems().isEmpty()) {
			suratJalanListbox.setSelectedIndex(0);
			currSuratJalan = suratJalanListbox.getSelectedItem().getValue();
			// display
			displaySuratJalan();
		}
	}
	
	public void onSelect$suratJalanListbox(Event event) throws Exception {
		currSuratJalan = suratJalanListbox.getSelectedItem().getValue();
		if (currSuratJalan != null) {
			// display
			displaySuratJalan();
		}
	}

	public void onClick$suratJalanAddButton(Event event) throws Exception {
		log.info("suratJalanAddButton click");

		if (!customerCombobox.getItems().isEmpty()) {
			// grab the selected customer
			Ent_Customer selCustomer = customerCombobox.getSelectedItem().getValue();
			// look for the process with process status Selesai
			//					and suratJalan is null
			List<Ent_InventoryProcess> processList =
				getInventoryProcessDao().findInventoryByCustomerByStatusBySuratJalan(
						selCustomer, Enm_StatusProcess.Selesai);
			// populate the process in the combobox
			// processList.forEach(p -> log.info(p.toString()));
			loadProcessCombobox(processList);
			// select the 1st item in the process combobox
			if (!processCombobox.getItems().isEmpty()) {
				processCombobox.setSelectedIndex(0);
				Ent_InventoryProcess invtProc =
						processCombobox.getSelectedItem().getValue();
				// get all the materials and the products into product list
				List<Ent_InventoryProcessProduct> productList = 
						getInventoryProcessProducts(invtProc);
				currSuratJalan = createSuratJalan(selCustomer, productList);
				// display suratjalan
				displaySuratJalan();
				
			}
		}
		
		// lower the listbox to 340px
		suratJalanListbox.setHeight("340px");
		// make div visible true
		processSelDiv.setVisible(true);
		// hide the edit button
		editButton.setVisible(false);
		// allow user to cancel this add
		cancelAddButton.setVisible(true);
		// allow user to save this add
		saveAddButton.setVisible(true);
	}

	private void loadCustomerCombobox(List<Ent_Customer> customerList) {
		// clear customerCombobox
		customerCombobox.getItems().clear();
		Comboitem comboitem;
		for(Ent_Customer customer : customerList) {
			comboitem = new Comboitem();
			comboitem.setLabel(customer.getCompanyType()+"."+
					customer.getCompanyLegalName());
			comboitem.setValue(customer);
			comboitem.setParent(customerCombobox);
		}
	}
	
	public void onSelect$customerCombobox(Event event) throws Exception {
		// load
		loadSuratJalan();
		// render
		renderSuratJalan();
	}

	private void loadProcessCombobox(List<Ent_InventoryProcess> processList) {
		// clear processCombobox
		processCombobox.getItems().clear();
		Comboitem comboitem;
		for(Ent_InventoryProcess process : processList) {
			comboitem = new Comboitem();
			comboitem.setLabel(process.getProcessNumber().getSerialComp());
			comboitem.setValue(process);
			comboitem.setParent(processCombobox);
		}
	}
	
	private List<Ent_InventoryProcessProduct> getInventoryProcessProducts(Ent_InventoryProcess inventoryProcess) throws Exception {
		// proxy
		Ent_InventoryProcess invtProc =
				getInventoryProcessDao()
					.findInventoryProcessMaterialsByProxy(inventoryProcess.getId());
		// get all the materials and the products into product list
		Ent_InventoryProcessMaterial procMaterial;
		List<Ent_InventoryProcessProduct> productList = 
				new ArrayList<Ent_InventoryProcessProduct>();
		for(Ent_InventoryProcessMaterial material : invtProc.getProcessMaterials()) {
			// by proxy
			procMaterial = getInventoryProcessDao()
					.findInventoryProcessProductsByProxy(material.getId());
			for(Ent_InventoryProcessProduct product : procMaterial.getProcessProducts()) {
				productList.add(product);
			}
		}

		// productList.forEach(p -> log.info(p.toString()));
		
		return productList;
	}

	private Ent_SuratJalan createSuratJalan(Ent_Customer customer, List<Ent_InventoryProcessProduct> productList) {
		Ent_SuratJalan suratjalan = new Ent_SuratJalan();
		suratjalan.setCustomer(customer);
		suratjalan.setDeliveryDate(getLocalDate(getZoneId()));
		suratjalan.setInvoice(null);
		suratjalan.setProcessedByCo(defaultCompany);
		suratjalan.setRefDocument("");
		suratjalan.setSuratjalanDate(getLocalDate(getZoneId()));
		suratjalan.setSuratjalanSerial(getSuratJalanSerial(Enm_TypeDocument.SURATJALAN, getLocalDate(getZoneId())));
		// productList to suratjalanProductList
		suratjalan.setSuratjalanProducts(transformProductToSuratJalanProduct(productList));
		suratjalan.setSuratjalanStatus(Enm_StatusDocument.Normal);
		suratjalan.setNoPolisi("");
		suratjalan.setAddInProgress(true);
		
		return suratjalan;
	}	
		
	private Ent_Serial getSuratJalanSerial(Enm_TypeDocument typeDocument, LocalDate localdate) {
		int serialNum = getSerialNumberGenerator()
				.getSerialNumber(typeDocument, localdate, defaultCompany);
		
		Ent_Serial serial = new Ent_Serial();
		serial.setCompany(defaultCompany);
		serial.setDocumentType(typeDocument);
		serial.setSerialDate(localdate);
		serial.setSerialNumber(serialNum);
		serial.setSerialComp(
				formatSerialComp(typeDocument.toCode(typeDocument.getValue()),localdate,serialNum));
		
		return serial;
	}

	private List<Ent_SuratJalanProduct> transformProductToSuratJalanProduct(List<Ent_InventoryProcessProduct> productList) {
		List<Ent_SuratJalanProduct> suratjalanProductList = new ArrayList<Ent_SuratJalanProduct>();
		Ent_SuratJalanProduct suratjalanProduct;
		for(Ent_InventoryProcessProduct product : productList) {
			suratjalanProduct = new Ent_SuratJalanProduct();
			suratjalanProduct.setInventoryCode(product.getInventoryCode());
			suratjalanProduct.setMarking(product.getMarking());
			suratjalanProduct.setPacking(product.getInventoryPacking());
			suratjalanProduct.setQuantityByKg(product.getWeightQuantity());
			suratjalanProduct.setQuantityBySht(product.getSheetQuantity());
			suratjalanProduct.setThickness(product.getThickness());
			suratjalanProduct.setWidth(product.getWidth());
			suratjalanProduct.setLength(product.getLength());
			suratjalanProduct.setRecoil(product.isRecoil());
			// allow user to edit
			suratjalanProduct.setEditInProgress(true);
			// add
			suratjalanProductList.add(suratjalanProduct);
		}
		
		// suratjalanProductList.forEach(s -> log.info(s.toString()));
		
		return suratjalanProductList;
	}

	private void displaySuratJalan() throws Exception {
		customerNameLabel.setValue(currSuratJalan.getCustomer().getCompanyType()+"."+
				currSuratJalan.getCustomer().getCompanyLegalName());
		suratjalanDateLabel.setValue(dateToStringDisplay(currSuratJalan.getSuratjalanDate(),
				getShortDateFormat(), getLocale()));
		suratjalanNumberLabel.setValue(currSuratJalan.getSuratjalanSerial().getSerialComp());
		// allow user to enter noPolisi
		setSuratJalanNoPolisi();
		// allow user to enter refDoc
		setSuratJalanRefDoc();
		// render the product list into the suratjalan listbox
		renderSuratJalanProductList();
		
	}	

	private void setSuratJalanNoPolisi() {
		if (currSuratJalan.isAddInProgress()) {
			nopolLabel.setVisible(false);
			nopolLabel.setValue("");
			nopolTextbox.setVisible(true);
			nopolTextbox.setValue(currSuratJalan.getNoPolisi());					
		} else {
			nopolLabel.setVisible(true);
			nopolLabel.setValue(currSuratJalan.getNoPolisi());
			nopolTextbox.setVisible(false);
			nopolTextbox.setValue("");			
		}
	}
	
	private void setSuratJalanRefDoc() {
		if (currSuratJalan.isAddInProgress()) {
			refdocLabel.setVisible(false);
			refdocLabel.setValue("");
			refdocTextbox.setVisible(true);
			refdocTextbox.setValue(currSuratJalan.getRefDocument());
		} else {
			refdocLabel.setVisible(true);
			refdocLabel.setValue(currSuratJalan.getRefDocument());
			refdocTextbox.setVisible(false);
			refdocTextbox.setValue("");
		}
	}
	
	private void renderSuratJalanProductList() throws Exception {
		if (currSuratJalan.isAddInProgress()) {
			// no need to proxy, because currSuratJalan is new (not from DB)
		} else {
			currSuratJalan = getSuratjalanDao().getSuratJalanProductByProxy(currSuratJalan.getId());			
		}
		suratjalanProductModelList = 
				new ListModelList<Ent_SuratJalanProduct>(currSuratJalan.getSuratjalanProducts());
		suratjalanProductListbox.setModel(suratjalanProductModelList);
		suratjalanProductListbox.setItemRenderer(getSuratJalanProductListitemRenderer());
	}

	private ListitemRenderer<Ent_SuratJalanProduct> getSuratJalanProductListitemRenderer() {
		
		return new ListitemRenderer<Ent_SuratJalanProduct>() {
			
			@Override
			public void render(Listitem item, Ent_SuratJalanProduct product, int index) throws Exception {
				Listcell lc;
				
				// marking
				lc = new Listcell(product.getMarking());
				lc.setParent(item);
				
				// jenis-coil
				lc = new Listcell(product.getInventoryCode().getProductCode());
				lc.setParent(item);
				
				// spek
				lc = new Listcell(
						toDecimalFormat(new BigDecimal(product.getThickness()), getLocale(), "#0,00")+" x "+
						toDecimalFormat(new BigDecimal(product.getWidth()), getLocale(), "###.###")+" x "+
						toDecimalFormat(new BigDecimal(product.getLength()), getLocale(), "###.###"));
				lc.setParent(item);
				
				// qty(kg)
				lc = new Listcell(
						toDecimalFormat(new BigDecimal(product.getQuantityByKg()), getLocale(), "###.###"));					
				lc.setParent(item);
				
				// qty(lbr)
				lc = new Listcell(getFormatedInteger(product.getQuantityBySht()));
				lc.setParent(item);
				
				// edit/save
				lc = new Listcell();
				lc.setParent(item);
				
				if (product.isEditInProgress()) {
					Button button = new Button();
					button.setIconSclass("");
					button.setParent(lc);
					button.setSclass("compButton");
					modifToEdit(button);
					button.addEventListener(Events.ON_CLICK, editSuratJalanProduct(product));
				}
				
				
				item.setValue(product);
			}
		};
	}	
	
	protected EventListener<Event> editSuratJalanProduct(Ent_SuratJalanProduct product) {
		
		return new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				Button button = (Button) event.getTarget();
				Listitem activeItem = (Listitem) event.getTarget().getParent().getParent();
				if (product.isEditInProgress()) {
					log.info("to editSuratJalanProduct click");
					// set to save
					product.setEditInProgress(false);
					// set columns to edit
					setMarking(activeItem, product.getMarking());
					setSpek(activeItem, product.getThickness(), product.getWidth(), 
							product.getLength());
					
					
					// transform this button to save
					modifToSave(button);
				} else {
					log.info("to saveSuratJalanProduct click");
					// set to edit
					product.setEditInProgress(true);
					//			
					product.setMarking(getMarking(activeItem));
					product.setThickness(getSpek(activeItem, 0));
					product.setWidth(getSpek(activeItem,2));
					product.setLength(getSpek(activeItem,4));
					
					// getSuratjalanDao().update(currSuratJalan);
					
					// render the product list into the suratjalan listbox
					renderSuratJalanProductList();
					
					// transform this button to edit
					modifToEdit(button);
				
				}
				
			}
		};
	}
	
	private String getMarking(Listitem item) {
		Listcell lc = (Listcell) item.getChildren().get(0);
		
		Textbox textbox = (Textbox) lc.getFirstChild();
		
		return textbox.getValue();
	}
	
	private void setMarking(Listitem activeItem, String marking) {
		Listcell lc = (Listcell) activeItem.getChildren().get(0);
		lc.setLabel("");
		Textbox textbox = new Textbox();
		textbox.setValue(marking);
		textbox.setWidth("100px");
		textbox.setParent(lc);
	}

	private double getSpek(Listitem activeItem, int idx) {
		Listcell lc = (Listcell) activeItem.getChildren().get(2);
		
		Doublebox doublebox = (Doublebox) lc.getChildren().get(idx);
		
		return doublebox.getValue();
	}	
	
	protected void setSpek(Listitem activeItem, double thickness, double width, double length) {
		Listcell lc = (Listcell) activeItem.getChildren().get(2);
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
	
	public void onClick$saveAddButton(Event event) throws Exception {
		log.info("saveAddButton click");
		
		// make div visible false
		processSelDiv.setVisible(false);
		// increase listbox height to 400px
		suratJalanListbox.setHeight("390px");
		// hide save button
		saveAddButton.setVisible(false);
		// hide cancel button
		cancelAddButton.setVisible(false);
		// make edit button visible
		editButton.setVisible(true);
		// save / update the currSuratJalan
		currSuratJalan = getSuratjalanDao().update(getUpdatedSuratJalan());
		// load surat jalan for the sel customer

		// locate the currSuratJalan in the listbox
		
		// display currSuratJalan
		displaySuratJalan();
	}

	private Ent_SuratJalan getUpdatedSuratJalan() {
		currSuratJalan.setNoPolisi(nopolTextbox.getValue());
		currSuratJalan.setRefDocument(refdocTextbox.getValue());
		
		
		return currSuratJalan;
	}

	public void onClick$cancelAddButton(Event event) throws Exception {
		log.info("cancelAddButton click");
		
		// make div visible false
		processSelDiv.setVisible(false);
		// increase listbox height to 400px
		suratJalanListbox.setHeight("390px");
		// make edit button visible
		editButton.setVisible(true);		
		// hide this button
		cancelAddButton.setVisible(false);
		// hide save button
		saveAddButton.setVisible(false);
		
		// list the suratjalan 
		loadSuratJalan();		
		// display suratjalan --> after render --> set currSuratJalan
		renderSuratJalan();
	}
	
	public void onClick$editButton(Event event) throws Exception {
		log.info("editButton click");

	}
	
	protected void modifToSave(Button button) {
		button.setIconSclass("z-icon-floppy-disk");
		button.setStyle("background-color:var(--bs-primary);");
	}

	protected void modifToEdit(Button button) {
		button.setIconSclass("z-icon-pen-to-square");
		button.setStyle("background-color:var(--bs-warning);");			
	}	

	public InventoryProcessDao getInventoryProcessDao() {
		return inventoryProcessDao;
	}

	public void setInventoryProcessDao(InventoryProcessDao inventoryProcessDao) {
		this.inventoryProcessDao = inventoryProcessDao;
	}

	public CustomerDao getCustomerDao() {
		return customerDao;
	}

	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}

	public CompanyDao getCompanyDao() {
		return companyDao;
	}

	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

	public SuratJalanDao getSuratjalanDao() {
		return suratjalanDao;
	}

	public void setSuratjalanDao(SuratJalanDao suratjalanDao) {
		this.suratjalanDao = suratjalanDao;
	}
	
}
