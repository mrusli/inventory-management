package com.pyramix.web.process;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

import com.pyramix.domain.entity.Enm_StatusInventory;
import com.pyramix.domain.entity.Enm_StatusProcess;
import com.pyramix.domain.entity.Enm_TypeDocument;
import com.pyramix.domain.entity.Enm_TypeProcess;
import com.pyramix.domain.entity.Ent_Company;
import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.domain.entity.Ent_Inventory;
import com.pyramix.domain.entity.Ent_InventoryCode;
import com.pyramix.domain.entity.Ent_InventoryProcess;
import com.pyramix.domain.entity.Ent_InventoryProcessMaterial;
import com.pyramix.domain.entity.Ent_InventoryProcessProduct;
import com.pyramix.domain.entity.Ent_Serial;
import com.pyramix.persistence.company.dao.CompanyDao;
import com.pyramix.persistence.customer.dao.CustomerDao;
import com.pyramix.persistence.inventory.dao.InventoryDao;
import com.pyramix.persistence.inventoryprocess.dao.InventoryProcessDao;
import com.pyramix.web.common.GFCBaseController;
import com.pyramix.web.common.SerialNumberGenerator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessCoilController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4358195157862161018L;

	private InventoryProcessDao inventoryProcessDao;
	private CustomerDao customerDao;
	private SerialNumberGenerator serialNumberGenerator;
	private CompanyDao companyDao;
	private InventoryDao inventoryDao;
	
	private Listbox processListbox, materialListbox, productListbox;
	private Label customerNameLabel, processDateLabel, processNumberLabel, statusLabel,
		processTypeLabel, materialToProductLabel;
	private Combobox customerNameCombobox, processTypeCombobox;
	private Datebox processDatebox;
	private Button addMaterialButton, addProductButton, processEditButton, processSaveButton,
		printReportButton;
	
	// private List<Ent_InventoryProcessMaterial> materialList = null;
	private List<Ent_Customer> customerList = null;
	private ListModelList<Ent_InventoryProcess> processModelList = null;
	private ListModelList<Ent_InventoryProcessMaterial> materialModelList = null;
	private ListModelList<Ent_InventoryProcessProduct> productModelList = null;
	private Ent_InventoryProcess selInventoryProcess = null;
	private Ent_Company defaultCompany = null;
	
	private static final Long DEF_COMPANY_IDX = (long) 2;
	
	public void onCreate$infoProcessCoilPanel(Event event) throws Exception {
		log.info("infoProcessCoilPanel created");
		
		// set default company
		defaultCompany = getCompanyDao().findCompanyById(DEF_COMPANY_IDX);
		
		// load customer list and prepare the combobox
		customerListCombobox();
		// load process type and prepare the combobox
		processTypeCombobox();
		
		// load inventoryProcess list
		loadInventoryProcessList();
		
		// render inventoryProcess list
		renderInventoryProcessList();
		
		// select
		if (!processModelList.isEmpty()) {
			selInventoryProcess =
					processModelList.get(0);
			// display
			displayInventoryProcessInfo(selInventoryProcess);
		}
	}

	private void customerListCombobox() throws Exception {
		customerList = customerDao.findAllCustomer();
		Comboitem comboitem;
		for (Ent_Customer cust : customerList) {
			comboitem = new Comboitem();
			comboitem.setLabel(cust.getCompanyType()+"."+
					cust.getCompanyLegalName());
			comboitem.setValue(cust);
			comboitem.setParent(customerNameCombobox);
		}
	}

	private void processTypeCombobox() {
		Comboitem comboitem;
		for (Enm_TypeProcess proc : Enm_TypeProcess.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(proc.toString());
			comboitem.setValue(proc);
			comboitem.setParent(processTypeCombobox);
		}
	}	
	
	private void loadInventoryProcessList() throws Exception {
		List<Ent_InventoryProcess> inventoryProcessList =
				getInventoryProcessDao().findAllInventoryProcess();
		
		processModelList = 
				new ListModelList<Ent_InventoryProcess>(inventoryProcessList);
	}

	private void renderInventoryProcessList() {
		processListbox.setModel(processModelList);
		processListbox.setItemRenderer(getProcessListitemRenderer());
	}

	private ListitemRenderer<Ent_InventoryProcess> getProcessListitemRenderer() {
		
		return new ListitemRenderer<Ent_InventoryProcess>() {
			
			@Override
			public void render(Listitem item, Ent_InventoryProcess process, int index) throws Exception {
				Listcell lc;
				
				// tgl proses
				lc = new Listcell(dateToStringDisplay(
						process.getOrderDate(), getShortDateFormat(), getLocale()));
				lc.setParent(item);
				
				// no proses
				lc = new Listcell(process.getProcessNumber().getSerialComp());
				lc.setParent(item);
				
				// customer
				lc = new Listcell(process.getCustomer().getCompanyType()+"."+
						process.getCustomer().getCompanyLegalName());
				lc.setStyle("white-space:nowrap;");
				lc.setParent(item);
				
				item.setValue(process);
			}
		};
	}


	private void displayInventoryProcessInfo(Ent_InventoryProcess selInventoryProcess) {
		customerNameCombobox.setVisible(false);
		customerNameLabel.setValue(selInventoryProcess.getCustomer().getCompanyType()+"."+
				selInventoryProcess.getCustomer().getCompanyLegalName());
		processDatebox.setVisible(false);
		processDateLabel.setValue(dateToStringDisplay(selInventoryProcess.getOrderDate(), 
				getShortDateFormat(), getLocale()));
		
		processNumberLabel.setValue(selInventoryProcess.getProcessNumber().getSerialComp());
	}	
	
	public void onClick$addProcessButton(Event event) throws Exception {
		log.info("addProcessButton click");
				
		// create a new inventoryProcess
		selInventoryProcess = new Ent_InventoryProcess();
		selInventoryProcess.setProcessMaterials(new ArrayList<Ent_InventoryProcessMaterial>());
		// set in progress
		selInventoryProcess.setAddInProgress(true);
		
		// allow user to enter new info
		setToAllowEditInfo();
		// render material list
		renderInventoryProcessMaterial(selInventoryProcess.getProcessMaterials());
		
		// allow to add material
		addMaterialButton.setVisible(true);
		// hide the edit and print button
		processEditButton.setVisible(false); 
		printReportButton.setVisible(false);
	}
	
	private void setToAllowEditInfo() {
		customerNameLabel.setVisible(false);
		customerNameLabel.setValue(" ");
		customerNameCombobox.setVisible(true);

		processDateLabel.setVisible(false);
		processDatebox.setVisible(true);
		processDatebox.setLocale(getLocale());
		processDatebox.setFormat(getShortDateFormat());

		processTypeLabel.setVisible(false);
		processTypeLabel.setValue(" ");
		processTypeCombobox.setVisible(true);

		if (selInventoryProcess.isAddInProgress()) {
			customerNameCombobox.setSelectedIndex(0);
			processDatebox.setValue(asDate(getLocalDate(getZoneId()), getZoneId()));
			Ent_Serial serial = getProcessNumberSerial(Enm_TypeDocument.PROCESS_ORDER, getLocalDate(getZoneId()));
			processNumberLabel.setValue(serial.getSerialComp());
			statusLabel.setValue(Enm_StatusProcess.Proses.toString());
			statusLabel.setAttribute("statusProcess", Enm_StatusProcess.Proses);
			processTypeCombobox.setSelectedIndex(0);
		} else {
			
		}
	}	

	private Ent_Serial getProcessNumberSerial(Enm_TypeDocument processOrder, LocalDate localDate) {
		int serialNum = getSerialNumberGenerator()
				.getSerialNumber(processOrder, localDate, defaultCompany);
		
		Ent_Serial serial = new Ent_Serial();
		serial.setCompany(defaultCompany);
		serial.setDocumentType(processOrder);
		serial.setSerialDate(localDate);
		serial.setSerialNumber(serialNum);
		serial.setSerialComp(
				formatSerialComp(processOrder.toCode(processOrder.getValue()), localDate, serialNum));
		
		return serial;
	}
	
	private void renderInventoryProcessMaterial(List<Ent_InventoryProcessMaterial> materialList) {
		materialModelList = 
				new ListModelList<Ent_InventoryProcessMaterial>(materialList);
		
		materialListbox.setModel(materialModelList);
		materialListbox.setItemRenderer(getMaterialListitemRenderer());
	}

	private ListitemRenderer<Ent_InventoryProcessMaterial> getMaterialListitemRenderer() {
		
		return new ListitemRenderer<Ent_InventoryProcessMaterial>() {
			
			@Override
			public void render(Listitem item, Ent_InventoryProcessMaterial material, int index) throws Exception {
				Listcell lc;
				
				// JenisCoil
				lc = new Listcell();
				lc.setParent(item);
				
				// Spek
				lc = new Listcell();
				lc.setParent(item);

				// Qty(Kg)
				lc = new Listcell();
				lc.setParent(item);
				
				// Marking
				lc = new Listcell();
				lc.setParent(item);
				
				item.setValue(material);
			}
		};
	}

	public void onClick$addMaterialButton(Event event) throws Exception {
		log.info("addMaterialButton click");

		// add new material
		Set<Ent_InventoryCode> inventoryCodeSet = getInventoryCodeSet();
		// add material
		Ent_InventoryProcessMaterial processMaterial =
				loadInventoryProcessMaterial(inventoryCodeSet);
		processMaterial.setProcessProducts(
				new ArrayList<Ent_InventoryProcessProduct>());
		
		renderInventoryProcessProduct(processMaterial.getProcessProducts());
	}

	private Set<Ent_InventoryCode> getInventoryCodeSet() throws Exception {
		// get the selected customer
		Ent_Customer selCustomer =
				customerNameCombobox.getSelectedItem().getValue();
//		log.info("selected customer: "+selCustomer.getCompanyLegalName());
		
		// find inventory with selected customer
		List<Ent_Inventory> inventoryList = 
				getInventoryDao().findInventoryByCustomer(selCustomer);
		Set<Ent_InventoryCode> inventoryCodeSet = new HashSet<Ent_InventoryCode>();
		if (!inventoryList.isEmpty()) {
			// find unique inventoryCode
			for (Ent_Inventory invt : inventoryList) {
				inventoryCodeSet.add(invt.getInventoryCode());
			}
		}
//		inventoryCodeSet.forEach(c -> log.info(c.toString()));
		
		return inventoryCodeSet;
	}

	private Ent_InventoryProcessMaterial loadInventoryProcessMaterial(Set<Ent_InventoryCode> inventoryCodeSet) {
		// create inventoryProcessMaterial
		Ent_InventoryProcessMaterial processMaterial = addMaterialInLastPos();
		
		setupJenisCoil(getLastMaterialItem(), inventoryCodeSet);
		
		return processMaterial;
	}
	
	public Listitem getLastMaterialItem() {
		// will cause the listbox to immediately render all listitems 
		// based on the current model and renderer (template or 
		// ListItemRenderer)
		materialListbox.renderAll();
		
		// since the item is added into the last item,
		// we set the active page of the last page of the listbox
		// int lastPage =
		//		materialListbox.getPageCount();
		// materialListbox.setActivePage(lastPage-1);
		
		// access the lastitem of the listbox,
		// which is the item just added via listmodellist
		int lastItem =
				materialListbox.getItemCount();
		// log.info("lastitem: "+lastItem);
		Listitem activeItem =
				materialListbox.getItemAtIndex(lastItem-1);

		return activeItem;
	}

	private Ent_InventoryProcessMaterial addMaterialInLastPos() {
		Ent_InventoryProcessMaterial processMaterial =
				new Ent_InventoryProcessMaterial();
		
		// add to materialList
		// materialList.add(processMaterial);
		
		int posToAdd =
				materialModelList.size();
		materialModelList.add(posToAdd, processMaterial);
		
		return processMaterial;
	}	

	private void setupJenisCoil(Listitem activeItem, Set<Ent_InventoryCode> inventoryCodeSet) {
		Listcell lc = (Listcell) activeItem.getChildren().get(0);
		lc.setLabel(" ");
		Combobox combobox = new Combobox();
		combobox.setWidth("100px");
		combobox.setParent(lc);
		combobox.addEventListener(Events.ON_SELECT, onSelectJenisCoil());
		// load inventoryCode comboitems
		loadInventoryCodeItems(combobox, inventoryCodeSet);
	}	
	
	private EventListener<Event> onSelectJenisCoil() {

		return new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				log.info("onSelectJenisCoil...");
				Combobox combobox = (Combobox) event.getTarget();
				Ent_InventoryCode invtCode = combobox.getSelectedItem().getValue();
				// log.info(invtCode.toString());
				// find inventory with invtCode
				List<Ent_Inventory> inventoryList =
						getInventoryDao().findInventoryByInventoryCode(invtCode);
				// inventoryList.forEach(i -> log.info(i.toString()));
				Listitem activeItem = (Listitem) event.getTarget().getParent().getParent();
				setupMaterialSpek(activeItem, inventoryList);
				
			}
		};
	}

	private void loadInventoryCodeItems(Combobox combobox, Set<Ent_InventoryCode> inventoryCodeSet) {
		Comboitem comboitem;
		for (Ent_InventoryCode invtCode : inventoryCodeSet) {
			comboitem = new Comboitem();
			comboitem.setLabel(invtCode.getProductCode());
			comboitem.setValue(invtCode);
			comboitem.setParent(combobox);
		}
		
	}

	private void setupMaterialSpek(Listitem activeItem, List<Ent_Inventory> inventoryList) {
		Listcell lc = (Listcell) activeItem.getChildren().get(1);
		lc.setLabel(" ");
		Combobox combobox = new Combobox();
		combobox.setWidth("160px");
		combobox.setParent(lc);
		combobox.addEventListener(Events.ON_SELECT, onSelectMaterialSpek());
		
		// load spek comboitems
		loadInventorySpekItems(combobox, inventoryList);
	}	
	
	private EventListener<Event> onSelectMaterialSpek() {

		return new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				Combobox combobox = (Combobox) event.getTarget();
				Ent_Inventory invt =
						combobox.getSelectedItem().getValue();
				Listitem activeItem = (Listitem) event.getTarget().getParent().getParent();
				// qty(Kg)
				Listcell lc = (Listcell) activeItem.getChildren().get(2);
				lc.setLabel(toDecimalFormat(new BigDecimal(
						invt.getWeightQuantity()), getLocale(), getDecimalFormat()));
				// marking
				lc = (Listcell) activeItem.getChildren().get(3);
				lc.setLabel(invt.getMarking());
				// display
				materialToProductLabel.setValue(invt.getMarking()+" "+
						invt.getInventoryCode().getProductCode()+" "+
						toDecimalFormat(new BigDecimal(invt.getWeightQuantity()), getLocale(), getDecimalFormat()));
				// make product button visible
				addProductButton.setVisible(true);
			}
		};
	}

	private void loadInventorySpekItems(Combobox combobox, List<Ent_Inventory> inventoryList) {
		Comboitem comboitem;
		for (Ent_Inventory invt : inventoryList) {
			comboitem = new Comboitem();
			comboitem.setLabel(invt.getMarking()+" - "+
						invt.getThickness()+" x "+
						invt.getWidth()+" x "+
						invt.getLength()+" - "+
						invt.getWeightQuantity()
					);
			comboitem.setValue(invt);
			comboitem.setParent(combobox);
		}
		
	}

	private void renderInventoryProcessProduct(List<Ent_InventoryProcessProduct> processProducts) {
		productModelList = 
				new ListModelList<Ent_InventoryProcessProduct>(processProducts);
				
		productListbox.setModel(productModelList);
		productListbox.setItemRenderer(getProductListitemRenderer());
	}	
	
	private ListitemRenderer<Ent_InventoryProcessProduct> getProductListitemRenderer() {
		
		return new ListitemRenderer<Ent_InventoryProcessProduct>() {
			
			@Override
			public void render(Listitem item, Ent_InventoryProcessProduct prod, int index) throws Exception {
				Listcell lc;
				
				// marking
				lc = new Listcell();
				lc.setParent(item);
				
				// spek
				lc = new Listcell();
				lc.setParent(item);
				
				// qty(Kg)
				lc = new Listcell();
				lc.setParent(item);
				
				// qty(Lbr)
				lc = new Listcell();
				lc.setParent(item);

				item.setValue(prod);
			}
		};
	}

	public void onClick$addProductButton(Event event) throws Exception {
		log.info("addProductButton click");
		
		Ent_InventoryProcessProduct product = addProductInLastPos();
		// will cause the listbox to immediately render all listitems 
		// based on the current model and renderer (template or 
		// ListItemRenderer)
		productListbox.renderAll();
		
		// since the item is added into the last item,
		// we set the active page of the last page of the listbox
		// int lastPage =
		//		materialListbox.getPageCount();
		// materialListbox.setActivePage(lastPage-1);
		
		// access the lastitem of the listbox,
		// which is the item just added via listmodellist
		int lastItem =
				productListbox.getItemCount();
		// log.info("lastitem: "+lastItem);
		Listitem activeItem =
				productListbox.getItemAtIndex(lastItem-1);
		
		setupProductMarking(activeItem, product);
		setupProductSpek(activeItem, product);
		setupProductQtyKg(activeItem, product);
		setupProductQtyLbr(activeItem, product);
		
		processSaveButton.setVisible(true);
	}
	
	private Ent_InventoryProcessProduct addProductInLastPos() {
		Ent_InventoryProcessProduct processProduct =
				new Ent_InventoryProcessProduct();
		
		int posToAdd =
				productModelList.size();
		productModelList.add(posToAdd, processProduct);
		
		return processProduct;
	}

	private void setupProductMarking(Listitem activeItem, Ent_InventoryProcessProduct product) {
		Listcell lc = (Listcell) activeItem.getChildren().get(0);
		lc.setLabel(" ");
		Textbox textbox = new Textbox();
		textbox.setWidth("100px");
		textbox.setValue(product.getMarking());
		textbox.setParent(lc);
	}

	private void setupProductSpek(Listitem activeItem, Ent_InventoryProcessProduct product) {
		Doublebox doublebox;
		Label label;
		
		Listcell lc = (Listcell) activeItem.getChildren().get(1);
		lc.setLabel(" ");
		// thkns 
		doublebox = new Doublebox();
		doublebox.setWidth("60px");
		doublebox.setLocale(getLocale());
		doublebox.setValue(product.getThickness());
		doublebox.setParent(lc);
		label = new Label();
		label.setValue(" x ");
		label.setParent(lc);
		// wdth
		doublebox = new Doublebox();
		doublebox.setWidth("60px");
		doublebox.setLocale(getLocale());
		doublebox.setValue(product.getWidth());
		doublebox.setParent(lc);
		label = new Label();
		label.setValue(" x ");
		label.setParent(lc);
		// lgth
		doublebox = new Doublebox();
		doublebox.setWidth("60px");
		doublebox.setLocale(getLocale());
		doublebox.setValue(product.getLength());
		doublebox.setParent(lc);
	}

	private void setupProductQtyKg(Listitem activeItem, Ent_InventoryProcessProduct product) {
		Listcell lc = (Listcell) activeItem.getChildren().get(2);
		lc.setLabel(" ");
		Doublebox doublebox = new Doublebox();
		doublebox.setWidth("100px");
		doublebox.setValue(product.getWeightQuantity());
		doublebox.setParent(lc);
	}

	private void setupProductQtyLbr(Listitem activeItem, Ent_InventoryProcessProduct product) {
		Listcell lc = (Listcell) activeItem.getChildren().get(3);
		lc.setLabel(" ");
		Intbox intbox = new Intbox();
		intbox.setWidth("100px");
		intbox.setValue(product.getSheetQuantity());
		intbox.setParent(lc);
	}
	
	public void onClick$processSaveButton(Event event) throws Exception {
		log.info("processSaveButton click");
		// get all the data
		Ent_InventoryProcess activeProcess = 
				getInventoryProcessDao().update(getEditedInventoryProcessData());
		// load
		loadInventoryProcessList();
		// render
		renderInventoryProcessList();
		// locate
		locateInventoryProcessData(activeProcess);
		// set sel
		selInventoryProcess = activeProcess;
		// display
		displayInventoryProcessInfo(selInventoryProcess);
		
		
		// List<Ent_InventoryProcess> processList = new ArrayList<Ent_InventoryProcess>();
		// processList.add(activeProcess);
		// access the 1st material to get the inventoryCoil
		// Ent_Inventory inventoryCoil = activeProcess.getProcessMaterials().get(0).getInventoryCoil();
		// inventoryCoil.setInventoryStatus(Enm_StatusInventory.process);
		// inventoryCoil.setInventoryProcesses(processList);
		// update inventory
		// Ent_Inventory activeInventory = getInventoryDao().update(inventoryCoil);
		
		// log.info(activeProcess.toString());
		
	}

	private Ent_InventoryProcess getEditedInventoryProcessData() {
		// selInventoryProcess.setInventoryCoil(null);
		selInventoryProcess.setOrderDate(processDatebox.getValueInLocalDate());
		selInventoryProcess.setProcessedByCo(defaultCompany);
		selInventoryProcess.setProcessedForCo(defaultCompany);
		selInventoryProcess.setProcessNumber(
				getProcessNumberSerial(Enm_TypeDocument.PROCESS_ORDER, 
						processDatebox.getValueInLocalDate()));
		selInventoryProcess.setProcessStatus(Enm_StatusProcess.Proses);
		selInventoryProcess.setProcessType(processTypeCombobox.getSelectedItem().getValue());
		selInventoryProcess.setCustomer(customerNameCombobox.getSelectedItem().getValue());
		selInventoryProcess.setProcessMaterials(getProcessMaterials(selInventoryProcess.getProcessMaterials()));
		
		// get the last material to assign products
		// int idxLastMat =
		//		selInventoryProcess.getProcessMaterials().size()-1;
		// Ent_InventoryProcessMaterial procMaterial = 
		//		selInventoryProcess.getProcessMaterials().get(idxLastMat);
		// procMaterial.setProcessProducts(getProcessProducts(procMaterial));
		
		// selInventoryProcess.getProcessMaterials().forEach(m -> log.info(m.toString()));
		
		return selInventoryProcess;
	}

	private List<Ent_InventoryProcessMaterial> getProcessMaterials(List<Ent_InventoryProcessMaterial> materialList) {
		Ent_InventoryProcessMaterial material;
		Listcell lc;
		for (Listitem listitem : materialListbox.getItems()) {
			// get the 'plain' value
			material = listitem.getValue();
			
			lc = (Listcell) listitem.getChildren().get(0);
			if (lc.getChildren().isEmpty()) {
				continue;
			}
			
			// get the 'edited' value
			material = getEditedProcessMaterial(material, listitem);			
			// add
			materialList.add(material);
		}
				
		return materialList;
	}
	
	private Ent_InventoryProcessMaterial getEditedProcessMaterial(Ent_InventoryProcessMaterial material, Listitem listitem) {
		// jenis-coil (inventoryCode)
		Listcell lc = (Listcell) listitem.getChildren().get(0);
		Combobox combobox = (Combobox) lc.getFirstChild();
		// set
		material.setInventoryCode(combobox.getSelectedItem().getValue());
		// spek -> marking, thk, wdth, lgth, qty(Kg)
		lc = (Listcell) listitem.getChildren().get(1);
		combobox = (Combobox) lc.getFirstChild();
		Ent_Inventory invt = combobox.getSelectedItem().getValue();
		// set
		material.setInventoryPacking(invt.getInventoryPacking());
		material.setMarking(invt.getMarking());
		material.setThickness(invt.getThickness());
		material.setWidth(invt.getWidth());
		material.setLength(invt.getLength());
		material.setWeightQuantity(invt.getWeightQuantity());
		material.setSheetQuantity(invt.getSheetQuantity());
		// products
		material.setProcessProducts(getProcessProducts(material));
		// material.getProcessProducts().forEach(p -> log.info(p.toString()));

		// inventoryProcess
		material.setInventoryProcess(selInventoryProcess);
		// inventory
		material.setInventoryCoil(invt);
		
		return material;
	}

	private List<Ent_InventoryProcessProduct> getProcessProducts(Ent_InventoryProcessMaterial material) {
		List<Ent_InventoryProcessProduct> productList =
				new ArrayList<Ent_InventoryProcessProduct>();
		Ent_InventoryProcessProduct product; 
		// Listcell lc;
		for (Listitem listitem : productListbox.getItems()) {
			product = listitem.getValue();
			// marking
			Textbox markingTextbox = (Textbox) listitem.getChildren().get(0).getFirstChild();
			product.setMarking(markingTextbox.getValue());
			// spek - thk
			Doublebox thkDoublebox = (Doublebox) listitem.getChildren().get(1).getChildren().get(0);
			product.setThickness(thkDoublebox.getValue());
			// spek - wdth
			Doublebox wdthDoublebox = (Doublebox) listitem.getChildren().get(1).getChildren().get(2);
			product.setWidth(wdthDoublebox.getValue());
			// spek - lgth
			Doublebox lgthDoublebox = (Doublebox) listitem.getChildren().get(1).getChildren().get(4);
			product.setLength(lgthDoublebox.getValue());
			// Qty(Kg)
			Doublebox qtyKgDoublebox = (Doublebox) listitem.getChildren().get(2).getFirstChild();
			product.setWeightQuantity(qtyKgDoublebox.getValue());
			// Qty(Lbr)
			Intbox qtyLbrIntbox = (Intbox) listitem.getChildren().get(3).getFirstChild();
			product.setSheetQuantity(qtyLbrIntbox.getValue());
			// from material
			product.setInventoryCode(material.getInventoryCode());
			
			productList.add(product);
		}
		
		return productList;
	}

	private void locateInventoryProcessData(Ent_InventoryProcess activeProcess) {
		processListbox.renderAll();
		for(Listitem item : processListbox.getItems()) {
			Ent_InventoryProcess invtProcess = item.getValue();
			if (invtProcess.equals(activeProcess)) {
				processListbox.setSelectedItem(item);
				break;
			}
		}
	}	
	
	public void onClick$testEqButton(Event event) throws Exception {
		Ent_InventoryProcess activeProcess = getInventoryProcessDao().findInventoryProcessById(1);
		log.info(activeProcess.toString());
		for(Listitem item : processListbox.getItems()) {
			Ent_InventoryProcess invtProcess = item.getValue();
			if (activeProcess.equals(invtProcess)) {
				log.info("found!!!!");
				processListbox.setSelectedItem(item);
				break;
			}
		}		
	}
	
	public void onSelect$customerNameCombobox(Event event) throws Exception {
		Ent_Customer selCustomer =
				customerNameCombobox.getSelectedItem().getValue();
		log.info("customerNameCombobox select: "+selCustomer.getCompanyLegalName());
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

	public InventoryDao getInventoryDao() {
		return inventoryDao;
	}

	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}
}
