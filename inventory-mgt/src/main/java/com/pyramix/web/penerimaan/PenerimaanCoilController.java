package com.pyramix.web.penerimaan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

import com.pyramix.domain.entity.Enm_TypePacking;
import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.domain.entity.Ent_Inventory;
import com.pyramix.domain.entity.Ent_InventoryCode;
import com.pyramix.persistence.customer.dao.CustomerDao;
import com.pyramix.persistence.inventory.dao.InventoryDao;
import com.pyramix.persistence.inventorycode.dao.InventoryCodeDao;
import com.pyramix.web.common.GFCBaseController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PenerimaanCoilController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 930047113550035685L;
	
	private InventoryDao inventoryDao;
	private InventoryCodeDao inventoryCodeDao;
	private CustomerDao customerDao;
	
	private Listbox receiveCoilListbox;
	private Button saveButton;
	private Combobox customerCombobox, jenisCoilCombobox;
	
	private ListModelList<Ent_Inventory> inventoryModelList = null;
	private List<Ent_InventoryCode> inventoryCodeList = null;
	private List<Ent_Customer> customerList = null;
	private Ent_Inventory invtToFind = null;
	
	private final String THICKNESS_FORMAT = "#0,00";
	
	public void onCreate$infoPenerimaanCoilPanel(Event event) throws Exception {
		log.info("infoPenerimaanCoilPanel created");
		
		// list
		inventoryCodeList = getInventoryCodeDao().findAllInventoryCodesSorted();
		customerList = getCustomerDao().findAllActiveCustomerSorted();
		
		// load customer selection combobox 
		// (only customers in the inventory list)
		loadCustomerSelection();
		// load inventory code
		// (all codes)
		loadJenisCoilCombobox();
				
		// load inventory (null -> all customers' inventory)
		loadInventoryList(null, null);
		
		// display
		displayInventoryList();
		
		// hide save button
		saveButton.setVisible(false);
	}

	private void loadCustomerSelection() throws Exception {
		// find unique customers in the inventory list
		List<Ent_Customer> uniqueCustList = 
				findUniqueCustomersFromInventoryList();
		uniqueCustList.sort((n1,n2) -> {
			return n1.getCompanyLegalName().compareTo(n2.getCompanyLegalName());
		});
		uniqueCustList.forEach(c -> log.info(c.getCompanyLegalName()));
		
		// clear combobox
		customerCombobox.getItems().clear();
		
		Comboitem comboitem;
		for(Ent_Customer customer : uniqueCustList) {
			comboitem = new Comboitem();
			comboitem.setLabel(customer.getCompanyType().toString()+" "+
					customer.getCompanyLegalName());
			comboitem.setValue(customer);
			comboitem.setParent(customerCombobox);
		}
	}

	private void loadJenisCoilCombobox() throws Exception {
		List<Ent_InventoryCode> inventoryCodeList =
				getInventoryCodeDao().findAllInventoryCodesSorted();
		
		// clear combobox
		jenisCoilCombobox.getItems().clear();
		
		Comboitem comboitem;
		for(Ent_InventoryCode invtCode : inventoryCodeList) {
			comboitem = new Comboitem();
			comboitem.setLabel(invtCode.getProductCode());
			comboitem.setValue(invtCode);
			comboitem.setParent(jenisCoilCombobox);
		}
	}
	
	public void onSelect$jenisCoilCombobox(Event event) throws Exception {
		Ent_InventoryCode selInvtCode = jenisCoilCombobox.getSelectedItem().getValue();
		log.info("selected InvtCode: {}", selInvtCode.toString());
		
		if (customerCombobox.getSelectedItem()!=null) {
			// use customer name also
			Ent_Customer selCustomer =
					customerCombobox.getSelectedItem().getValue();
			// load inventory
			loadInventoryList(selCustomer, selInvtCode);
			
			// display
			displayInventoryList();			
		} else {
			// load inventory
			loadInventoryList(null, selInvtCode);
			
			// display
			displayInventoryList();				
		}
		
	}
	
	private List<Ent_Customer> findUniqueCustomersFromInventoryList() throws Exception {
		Set<Ent_Customer> customerSet = new HashSet<Ent_Customer>();
		List<Ent_Inventory> inventoryList = 
				getInventoryDao().findAllInventory();
		for(Ent_Inventory inventory : inventoryList) {
			customerSet.add(inventory.getCustomer());
		}
		
		return new ArrayList<Ent_Customer>(customerSet);
	}
	
	public void onSelect$customerCombobox(Event event) throws Exception {
		Ent_Customer selCustomer =
				customerCombobox.getSelectedItem().getValue();
		log.info("Selected Customer : {}", selCustomer.getCompanyLegalName());
		
		// load inventory
		loadInventoryList(selCustomer, null);
		
		// display
		displayInventoryList();		
	}

	private void loadInventoryList(Ent_Customer customer, Ent_InventoryCode invtCode) throws Exception {
		List<Ent_Inventory> inventoryList = null;
		if (customer==null && invtCode==null) {
			inventoryList = 
					getInventoryDao().findAllInventory();			
		} else if (customer!=null && invtCode==null) {
			inventoryList =
					getInventoryDao().findInventoryByCustomer(customer);
			
		} else if (customer==null && invtCode!=null) {
			inventoryList =
					getInventoryDao().findInventoryByInventoryCode(invtCode);
		} else {
			inventoryList =
					getInventoryDao().findInventoryByCustomer_InventoryCode_NonStatus(customer, invtCode);
		}
		
		inventoryModelList = new ListModelList<Ent_Inventory>(inventoryList);
	}

	private void displayInventoryList() {
		receiveCoilListbox.setModel(inventoryModelList);
		receiveCoilListbox.setItemRenderer(getInventoryListitemRenderer());
	}	
	
	private ListitemRenderer<Ent_Inventory> getInventoryListitemRenderer() {
		
		return new ListitemRenderer<Ent_Inventory>() {
			
			@Override
			public void render(Listitem item, Ent_Inventory inventory, int index) throws Exception {
				Listcell lc;
				
				// Tgl.Terima
				lc = new Listcell(dateToStringDisplay(
						inventory.getReceiveDate(), getShortDateFormat(), getLocale()));
				lc.setParent(item);
				
				// Jenis-Coil
				lc = new Listcell(inventory.getInventoryCode().getProductCode());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Customer
				lc = new Listcell(
						 inventory.getCustomer().getCompanyType()+" "+
				 		 inventory.getCustomer().getCompanyLegalName());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Spek
				lc = new Listcell(
						toDecimalFormat(new BigDecimal(inventory.getThickness()), getLocale(), THICKNESS_FORMAT)+" x "+
						toDecimalFormat(new BigDecimal(inventory.getWidth()), getLocale(), "###.###")+" x "+
						toDecimalFormat(new BigDecimal(inventory.getLength()), getLocale(), "###.###")								
						);
				lc.setParent(item);
				
				// Packing
				lc = new Listcell(inventory.getInventoryPacking().toString());
				lc.setParent(item);
				
				// Jmlh/Kg
				lc = new Listcell(
						toDecimalFormat(new BigDecimal(inventory.getWeightQuantity()), getLocale(), "###.###")
						);
				lc.setParent(item);
				
				// No.Coil
				lc = new Listcell(inventory.getMarking());
				lc.setParent(item);
				
				// Notif 
				lc = new Listcell();
				lc.setParent(item);

				// Button
				lc = new Listcell();
				lc.setParent(item);
				
				// Status
				lc = new Listcell();
				lc.setParent(item);

				if (inventory.getInventoryProcesses().isEmpty()) {
					lc.setLabel("");
				} else {
					lc.setIconSclass("z-icon-code");
				}
				// inventory.getInventoryProcesses().forEach(p -> log.info(p.toString()));
				
				item.setValue(inventory);
			}
		};
	}

//	protected EventListener<Event> addRow() {
//		
//		return new EventListener<Event>() {
//			
//			@Override
//			public void onEvent(Event event) throws Exception {
//				log.info("addRow click");
//				
//				coilAddRow();
//
//			}
//		};
//	}

//	protected void coilAddRow() throws Exception {
//		// add to the last pos
//		Ent_Inventory inventory = addInventoryInLastPos();
//		
//		// will cause the listbox to immediately render all listitems 
//		// based on the current model and renderer (template or 
//		// ListItemRenderer)
//		receiveCoilListbox.renderAll();
//		
//		// since the item is added into the last item,
//		// we set the active page of the last page of the listbox
//		int lastPage =
//				receiveCoilListbox.getPageCount();
//		receiveCoilListbox.setActivePage(lastPage-1);
//		
//		// access the lastitem of the listbox,
//		// which is the item just added via listmodellist
//		int lastItem =
//				receiveCoilListbox.getItemCount();
//		// log.info("lastItem-----: "+String.valueOf(lastItem));
//		if (lastItem>1) {
//			Listcell lc;
//			Listitem prevItem =	receiveCoilListbox.getItemAtIndex(lastItem-2);
//			// tglTerima
//			lc = (Listcell) prevItem.getChildren().get(0);
//			LocalDate localDate = (LocalDate) lc.getAttribute("tglTerima");
//			inventory.setReceiveDate(localDate);
//			// jenisCoil
//			lc = (Listcell) prevItem.getChildren().get(1);
//			Ent_InventoryCode invtCode = (Ent_InventoryCode) lc.getAttribute("jenisCoil");
//			inventory.setInventoryCode(invtCode);
//		}
//		Listitem activeItem = 
//		 		receiveCoilListbox.getItemAtIndex(lastItem-1);
//		
//		setupTglTerimaDatebox(activeItem, inventory.getReceiveDate());
//		setupJenisCoilCombobox(activeItem, inventory.getInventoryCode());
//		
//	}

	public void onClick$coilAddButton(Event event) throws Exception {
		log.info("coilAddButton click");
		
		// make visible
		saveButton.setVisible(true);

		// temporary remove inventory from db
		inventoryModelList = null; 
				// new ListModelList<Ent_Inventory>(new ArrayList<Ent_Inventory>());
		receiveCoilListbox.setModel(inventoryModelList);

		// setup the listcell requires for the item
		Listitem activeItem = setupListitem(); 
		
		// append the item to the listbox
		receiveCoilListbox.appendChild(activeItem);
		receiveCoilListbox.renderAll();
		
		// create a new inventory object
		Ent_Inventory inventory = new Ent_Inventory();
		// set to prevent error
		inventory.setReceiveDate(getLocalDate(getZoneId()));
		inventory.setInventoryCode(getDefaultInventoryCode());
		inventory.setCustomer(getDefaultCustomer());
		inventory.setThickness(0);
		inventory.setWidth(0);
		inventory.setLength(0);
		inventory.setInventoryPacking(Enm_TypePacking.coil);
		inventory.setWeightQuantity(0);
		inventory.setMarking("");
		// set to the item
		activeItem.setValue(inventory);
		
		int lastPage =
				receiveCoilListbox.getPageCount();
		receiveCoilListbox.setActivePage(lastPage-1);
		int lastItem =
				receiveCoilListbox.getItemCount();
		
		LocalDate recvDate = null;
		Ent_InventoryCode invtCode = null;
		Ent_Customer customer = null;
		Listitem prevItem;
		Listcell lc;
		if (lastItem>1) {
			prevItem =	receiveCoilListbox.getItemAtIndex(lastItem-2);

			// prev TglTerima
			lc = (Listcell) prevItem.getChildren().get(0);
			recvDate = (LocalDate) lc.getAttribute("tglTerima");
			// prev JnsCoil
			lc = (Listcell) prevItem.getChildren().get(1);
			invtCode =  (Ent_InventoryCode) lc.getAttribute("jenisCoil");
			// prev Customer
			lc = (Listcell) prevItem.getChildren().get(2);
			customer = (Ent_Customer) lc.getAttribute("customer");
			// prev spek
			lc = (Listcell) prevItem.getChildren().get(3);
			double spek[] = { 0.0, 0.0, 0.0 };
			// prev thickness
			spek[0] = (double) lc.getAttribute("spekThck");
			inventory.setThickness(spek[0]);
			// prev width
			spek[1] = (double) lc.getAttribute("spekWdth");
			inventory.setWidth(spek[1]);
			// prev length
			spek[2] = (double) lc.getAttribute("spekLgth");
			inventory.setLength(spek[2]);
			// prev Packing
			lc = (Listcell) prevItem.getChildren().get(4);
			inventory.setInventoryPacking((Enm_TypePacking) lc.getAttribute("packing"));
			// prev Jmlh/Kg
			lc = (Listcell) prevItem.getChildren().get(5);
			inventory.setWeightQuantity((double) lc.getAttribute("wgthQty"));
			// prev No.Coil
			lc = (Listcell) prevItem.getChildren().get(6);
			inventory.setMarking((String) lc.getAttribute("marking"));
		}
		// TglTerima		
		if (recvDate != null) {
			setupTglTerimaDatebox(activeItem, recvDate);
		} else {
			setupTglTerimaDatebox(activeItem, inventory.getReceiveDate());
		}
		// JnsCoil
		if (invtCode != null) {
			setupJenisCoilCombobox(activeItem, invtCode);
		} else {
			setupJenisCoilCombobox(activeItem, inventory.getInventoryCode());
		}
		// Customer
		if (customer != null) {
			setupCustomerCombobox(activeItem, customer);
		} else {
			setupCustomerCombobox(activeItem, inventory.getCustomer());
		}
		// Spek
		setupSpek(activeItem, inventory.getThickness(), inventory.getWidth(), 
				inventory.getLength());
		// Packing
		setupPacking(activeItem, inventory.getInventoryPacking());
		// Jmlh/Kg
		setupWeightQuantity(activeItem, inventory.getWeightQuantity());
		// No.Coil
		setupMarking(activeItem, inventory.getMarking());
		
		setupRemoveButton(activeItem);
	}

	private Listitem setupListitem() {
		Listitem item = new Listitem();
		
		Listcell lc;
		// TglTerima
		lc = new Listcell();
		lc.setParent(item);
		// JenisCoil
		lc = new Listcell();
		lc.setParent(item);
		// Customer
		lc = new Listcell();
		lc.setParent(item);
		// Spek
		lc = new Listcell();
		lc.setParent(item);		
		// Packing
		lc = new Listcell();
		lc.setParent(item);		
		// Jmlh/Kg
		lc = new Listcell();
		lc.setParent(item);
		// NoCoil
		lc = new Listcell();
		lc.setParent(item);
		// NOTIF
		lc = new Listcell();
		lc.setParent(item);
		// Remove
		lc = new Listcell();
		lc.setParent(item);

		return item;
	}

//	private Ent_Inventory addInventoryInLastPos() throws Exception {
//		Ent_Inventory inventory = new Ent_Inventory();
//		// set to prevent error
//		inventory.setReceiveDate(getLocalDate(getZoneId()));
//		inventory.setInventoryCode(getDefaultInventoryCode());
//		
//		int posToAdd =
//				inventoryModelList.size();
//		inventoryModelList.add(posToAdd, inventory);
//		
//		return inventory;
//	}

	private Ent_InventoryCode getDefaultInventoryCode() throws Exception {
		// temporary assign an inventoryCode
		Long invtCodeId = 1L;
		
		return getInventoryCodeDao().findInventoryCodeById(invtCodeId);
	}

	private Ent_Customer getDefaultCustomer() throws Exception {
		// temporary assign a customer
		long custId = 1L;
		
		return getCustomerDao().findCustomerById(custId);
	}	
	
	private void setupTglTerimaDatebox(Listitem activeItem, LocalDate receiveDate) {
		Listcell lc = (Listcell) activeItem.getChildren().get(0);
		lc.setLabel("");
		Datebox datebox = new Datebox();
		datebox.setLocale(getLocale());
		datebox.setFormat(getShortDateFormat());
		datebox.setWidth("100px");
		datebox.setValue(asDate(receiveDate, getZoneId()));
		datebox.setParent(lc);
		datebox.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				lc.setAttribute("tglTerima", datebox.getValueInLocalDate());
			}
		});
		// just to make sure the date value is saved even user not making any changes
		lc.setAttribute("tglTerima", datebox.getValueInLocalDate());
	}	
	
	private void setupJenisCoilCombobox(Listitem activeItem, Ent_InventoryCode inventoryCode) {
		Listcell lc = (Listcell) activeItem.getChildren().get(1);
		lc.setLabel("");
		Combobox combobox = new Combobox();
		// setup the combobox with inventoryCodes items
		setupInventoryCodeCombobox(combobox);
		if (inventoryCode != null) {
			for (Comboitem comboitem : combobox.getItems()) {
				Ent_InventoryCode invtCode = comboitem.getValue();
				if (inventoryCode.getId()==invtCode.getId()) {
					combobox.setSelectedItem(comboitem);
					break;
				}
			}
		} else {
			combobox.setSelectedIndex(0);
		}
		combobox.setWidth("100px");
		combobox.setParent(lc);
		combobox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				lc.setAttribute("jenisCoil", combobox.getSelectedItem().getValue());
			}
		});
		// just to make sure
		lc.setAttribute("jenisCoil", combobox.getSelectedItem().getValue());
	}

	private void setupInventoryCodeCombobox(Combobox combobox) {
		Comboitem comboitem;
		for (Ent_InventoryCode invtCode : inventoryCodeList) {
			comboitem = new Comboitem();
			comboitem.setLabel(invtCode.getProductCode());
			comboitem.setValue(invtCode);
			comboitem.setParent(combobox);
		}
	}	

	private void setupCustomerCombobox(Listitem activeItem, Ent_Customer customer) {
		Listcell lc = (Listcell) activeItem.getChildren().get(2);
		lc.setLabel("");
		Combobox combobox = new Combobox();
		// setup the combobox with customer items
		setupCustomerCombobox(combobox);
		if (customer != null) {
			for (Comboitem comboitem : combobox.getItems()) {
				Ent_Customer cust = comboitem.getValue();
				if (customer.getId()==cust.getId()) {
					combobox.setSelectedItem(comboitem);
					break;
				}
			}
		} else {
			combobox.setSelectedIndex(0);
		}
		combobox.setWidth("180px");
		combobox.setParent(lc);		
		combobox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				lc.setAttribute("customer", combobox.getSelectedItem().getValue());
			}
		});
		// just to make sure
		lc.setAttribute("customer", combobox.getSelectedItem().getValue());
	}	
	
	private void setupCustomerCombobox(Combobox combobox) {
		Comboitem comboitem;
		for (Ent_Customer cust : customerList) {
			comboitem = new Comboitem();
			comboitem.setLabel(cust.getCompanyType()+"."+
					cust.getCompanyLegalName());
			comboitem.setValue(cust);
			comboitem.setParent(combobox);
		}
	}

	private void setupSpek(Listitem activeItem, double thickness, double width, double length) {
		double spek[] = { 0.0, 0.0, 0.0 };

		Listcell lc = (Listcell) activeItem.getChildren().get(3);
		lc.setLabel("");
		// thickness
		Doublebox doublebox = new Doublebox();
		doublebox.setLocale(getLocale());
		doublebox.setWidth("60px");
		doublebox.setValue(thickness);
		doublebox.setParent(lc);
		doublebox.addEventListener(Events.ON_CHANGE, onSpekChange(0));
		spek[0] = doublebox.getValue();
		lc.setAttribute("spekThck", spek[0]);
		
		Label label = new Label(" x ");
		label.setParent(lc);
		
		doublebox = new Doublebox();
		doublebox.setLocale(getLocale());
		doublebox.setWidth("60px");
		doublebox.setValue(width);
		doublebox.setParent(lc);
		doublebox.addEventListener(Events.ON_CHANGE, onSpekChange(1));
		spek[1] = doublebox.getValue();
		lc.setAttribute("spekWdth", spek[1]);
		
		label = new Label(" x ");
		label.setParent(lc);
		
		doublebox = new Doublebox();
		doublebox.setLocale(getLocale());
		doublebox.setWidth("60px");
		doublebox.setValue(length);
		doublebox.setParent(lc);
		doublebox.addEventListener(Events.ON_CHANGE, onSpekChange(2));
		spek[2] = doublebox.getValue();
		lc.setAttribute("spekLgth", spek[2]);
		

	}	
	
	private EventListener<Event> onSpekChange(int i) {

		return new EventListener<Event>() {
			
			double spek[] = { 0.0, 0.0, 0.0 };
			@Override
			public void onEvent(Event event) throws Exception {
				Doublebox doublebox = (Doublebox) event.getTarget();
				spek[i] = doublebox.getValue();
				Listcell lc = (Listcell) doublebox.getParent();
				switch (i) {
				case 0: lc.setAttribute("spekThck", spek[0]);
				case 1: lc.setAttribute("spekWdth", spek[1]);
				case 2: lc.setAttribute("spekLgth", spek[2]);
				default:
					break;
				}
				
			}
		};
	}

	private void setupPacking(Listitem activeItem, Enm_TypePacking inventoryPacking) {
		Listcell lc = (Listcell) activeItem.getChildren().get(4);
		lc.setLabel("");
		Combobox combobox = new Combobox();
		// setup the combobox with packing items
		setupPackingCombobox(combobox);
		if (inventoryPacking != null) {
			for (Comboitem comboitem : combobox.getItems()) {
				Enm_TypePacking invtPack = comboitem.getValue();
				if (inventoryPacking.equals(invtPack)) {
					combobox.setSelectedItem(comboitem);
					break;
				}
			}
		} else {
			combobox.setSelectedIndex(0);
		}
		combobox.setWidth("100px");
		combobox.setParent(lc);		
		combobox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				lc.setAttribute("packing", combobox.getSelectedItem().getValue());
			}
		});
		// just to make sure
		lc.setAttribute("packing", combobox.getSelectedItem().getValue());
		
	}	
	
	private void setupPackingCombobox(Combobox combobox) {
		Comboitem comboitem;
		for(Enm_TypePacking invtPack : Enm_TypePacking.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(invtPack.toString());
			comboitem.setValue(invtPack);
			comboitem.setParent(combobox);
		}
		
	}

	private void setupWeightQuantity(Listitem activeItem, double weightQuantity) {
		Listcell lc = (Listcell) activeItem.getChildren().get(5);
		lc.setLabel("");
		Doublebox doublebox = new Doublebox();
		doublebox.setLocale(getLocale());
		doublebox.setWidth("100px");
		doublebox.setValue(weightQuantity);
		doublebox.setParent(lc);
		doublebox.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				lc.setAttribute("wgthQty", doublebox.getValue());
			}
		});
		// just to make sure
		lc.setAttribute("wgthQty", doublebox.getValue());		
	}	

	private void setupMarking(Listitem activeItem, String marking) {
		Listcell lc = (Listcell) activeItem.getChildren().get(6);
		lc.setLabel("");
		Textbox textbox = new Textbox();
		textbox.setWidth("100px");
		textbox.setValue(marking);
		textbox.setParent(lc);
		textbox.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				lc.setAttribute("marking", textbox.getValue());			
			}
		});
		// just to make sure
		lc.setAttribute("marking", textbox.getValue());
	}
	
	private void setupRemoveButton(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(8);
		lc.setLabel("");
		
		Button button = new Button();
		button.setParent(lc);
		button.setIconSclass("z-icon-times");
		button.setSclass("compButton");
		button.setStyle("background-color:var(--bs-danger);");
		button.addEventListener(Events.ON_CLICK, removeRow());	
	}	
	
	private EventListener<Event> removeRow() {
		
		return new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				log.info("removeButton click");
				Listitem item =
						(Listitem) event.getTarget().getParent().getParent();
				item.setValue(null);
				item.setClass("red-text");
				// notif
				Listcell lc = (Listcell) item.getChildren().get(7);
				lc.setLabel("Batal");
				
			}
		};
	}

	int pgSize = 0;
	int idx = 0;
	int pgCount = 0;
	int actPg = 0;
	public void onClick$saveButton(Event event) throws Exception {
		log.info("saveButton click");
	
		invtToFind = null;
		pgSize = receiveCoilListbox.getPageSize();
		pgCount = receiveCoilListbox.getPageCount();
		
		doSave(event.getTarget());
		
		// hide after save is complete
		saveButton.setVisible(false);
	}
	
	public void doSave(Component target) throws Exception {
		Listcell lc;
		Ent_Inventory inventory = null;
		for (Listitem item : receiveCoilListbox.getItems()) {
			idx = item.getIndex();

			log.info("--->"+String.valueOf(idx / pgSize));
			actPg = idx/pgSize;
			receiveCoilListbox.setActivePage(actPg);
			
			inventory = item.getValue();

			if (inventory != null) {			
				log.info("---index---: "+idx+" (Simpan)");
				// tglTerima
				lc = (Listcell) item.getChildren().get(0);
				if (lc.getChildren().isEmpty()) {
					continue;
				}
				Datebox datebox = (Datebox) lc.getFirstChild();
				inventory.setReceiveDate(datebox.getValueInLocalDate());
				log.info(datebox.getValueInLocalDate().toString());
				lc.getChildren().clear();
				lc.setLabel(dateToStringDisplay(datebox.getValueInLocalDate(), getShortDateFormat(), getLocale()));
				// jenisCoil
				lc = (Listcell) item.getChildren().get(1);
				Combobox combobox = (Combobox) lc.getFirstChild();
				Ent_InventoryCode invtCode = combobox.getSelectedItem().getValue();
				inventory.setInventoryCode(invtCode);
				log.info(invtCode.toString());
				lc.getChildren().clear();
				lc.setLabel(invtCode.getProductCode());
				lc.setStyle("white-space: nowrap;");
				// customer
				lc = (Listcell) item.getChildren().get(2);
				combobox = (Combobox) lc.getFirstChild();
				Ent_Customer cust = combobox.getSelectedItem().getValue();
				log.info(cust.toString());
				inventory.setCustomer(cust);
				lc.getChildren().clear();
				lc.setLabel(cust.getCompanyType()+"."+cust.getCompanyLegalName());
				lc.setStyle("white-space: nowrap;");
				// spek
				lc = (Listcell) item.getChildren().get(3);
				// thickness
				Doublebox doublebox = (Doublebox) lc.getChildren().get(0);
				Double thickness = doublebox.getValue();
				log.info(String.valueOf(thickness));
				inventory.setThickness(thickness);
				// width
				doublebox = (Doublebox) lc.getChildren().get(2);
				Double width = doublebox.getValue();
				log.info(String.valueOf(width));
				inventory.setWidth(doublebox.getValue());
				// length
				doublebox = (Doublebox) lc.getChildren().get(4);
				Double length = doublebox.getValue();
				log.info(String.valueOf(length));
				inventory.setLength(length);
				// thickness x width x length
				lc.getChildren().clear();
				lc.setLabel(toDecimalFormat(new BigDecimal(thickness), getLocale(), THICKNESS_FORMAT)
						+" x "+ toDecimalFormat(new BigDecimal(width), getLocale(), "###.###")
						+" x "+ toDecimalFormat(new BigDecimal(length), getLocale(), "###.###"));
				// Packing
				lc = (Listcell) item.getChildren().get(4);
				combobox = (Combobox) lc.getFirstChild();
				Enm_TypePacking packing = combobox.getSelectedItem().getValue();
				log.info(packing.toString());
				inventory.setInventoryPacking(packing);
				lc.getChildren().clear();
				lc.setLabel(packing.toString());
				// WeightQty
				lc = (Listcell) item.getChildren().get(5);
				doublebox = (Doublebox) lc.getFirstChild();
				Double weightQty = doublebox.getValue();
				log.info(String.valueOf(weightQty));
				inventory.setWeightQuantity(weightQty);
				lc.getChildren().clear();
				lc.setLabel(toDecimalFormat(new BigDecimal(weightQty), getLocale(), getDecimalFormat()));
				// No.Coil
				lc = (Listcell) item.getChildren().get(6);
				Textbox textbox = (Textbox) lc.getFirstChild();
				String coilNo = textbox.getValue();
				log.info(coilNo);
				inventory.setMarking(coilNo);
				lc.getChildren().clear();
				lc.setLabel(coilNo);
				// notif
				lc = (Listcell) item.getChildren().get(7);
				lc.setIconSclass("z-icon-check");
				lc.setStyle("color: --var(--bs-success");
			
				// remove delete button
				lc = (Listcell) item.getChildren().get(8);
				lc.getChildren().clear();
				lc.setLabel("");
				
				log.info("save--->"+inventory.toString());				

				// update inventory
				if (invtToFind==null) {
					invtToFind =
							getInventoryDao().update(inventory);				
				} else {
					getInventoryDao().update(inventory);
				}
			} else {
				log.info("---index---: "+idx+" (Batal)");

				continue;
			}
			
			Thread.sleep(1000);
			Events.echoEvent("onEchoSave", target, null);
			
			break;
		}
	}
	
	public void onEchoSave(Event event) throws Exception {	
		doSave(event.getTarget());
	}
	
	public void onClick$refreshButton(Event event) throws Exception {
		// load customer selection combobox 
		// (only customers in the inventory list)
		loadCustomerSelection();
		customerCombobox.setValue("");
		// load inventory code
		// (all codes)
		loadJenisCoilCombobox();
		jenisCoilCombobox.setValue("");
		// re-load (null -> all customers' inventory)
		loadInventoryList(null,null);
		// re-render
		displayInventoryList();
		
		if (invtToFind != null) {
			// find inventory
			receiveCoilListbox.renderAll();
			for (Listitem item : receiveCoilListbox.getItems()) {
				Ent_Inventory invt = item.getValue();
				if (invtToFind.getId()==invt.getId()) {
					receiveCoilListbox.setSelectedItem(item);
					break;
				}
			}
			// reset
			invtToFind = null;
		} else {
			receiveCoilListbox.renderAll();
			receiveCoilListbox.setActivePage(0);
		}
		
		// hide save button
		saveButton.setVisible(false);
	}
	
	public InventoryDao getInventoryDao() {
		return inventoryDao;
	}

	public void setInventoryDao(InventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
	}

	public InventoryCodeDao getInventoryCodeDao() {
		return inventoryCodeDao;
	}

	public void setInventoryCodeDao(InventoryCodeDao inventoryCodeDao) {
		this.inventoryCodeDao = inventoryCodeDao;
	}

	public CustomerDao getCustomerDao() {
		return customerDao;
	}

	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}

}
