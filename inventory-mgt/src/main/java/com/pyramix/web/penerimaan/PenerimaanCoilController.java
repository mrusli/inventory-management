package com.pyramix.web.penerimaan;

import java.time.LocalDate;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

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
	
	private ListModelList<Ent_Inventory> inventoryModelList = null;
	private List<Ent_InventoryCode> inventoryCodeList = null;
	private Ent_Inventory invtToFind = null;
	
	public void onCreate$infoPenerimaanCoilPanel(Event event) throws Exception {
		log.info("infoPenerimaanCoilPanel created");
		
		// list of inventoryCodes
		inventoryCodeList = getInventoryCodeDao().findAllInventoryCode();
		
		// load inventory
		loadInventoryList();
		
		// display
		displayInventoryList();
		
		// hide save button
		saveButton.setVisible(false);
	}

	private void loadInventoryList() throws Exception {
		List<Ent_Inventory> inventoryList = 
				getInventoryDao().findAllInventory();
		
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
				lc.setParent(item);
				
				// Customer
				lc = new Listcell(
						 inventory.getCustomer().getCompanyType()+" "+
				 		 inventory.getCustomer().getCompanyLegalName());
				lc.setStyle("white-space: nowrap;");
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
				
				// No.Coil
				lc = new Listcell();
						// inventory.getMarking());
				lc.setParent(item);
				
				// Notif
				lc = new Listcell();
				lc.setParent(item);

				// Button
				lc = new Listcell();
				lc.setParent(item);
				
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
		// set to the item
		activeItem.setValue(inventory);
		
		int lastPage =
				receiveCoilListbox.getPageCount();
		receiveCoilListbox.setActivePage(lastPage-1);
		int lastItem =
				receiveCoilListbox.getItemCount();
		
		LocalDate recvDate = null;
		Ent_InventoryCode invtCode = null;
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
		Long invtCodeId = 1L;
		
		return getInventoryCodeDao().findInventoryCodeById(invtCodeId);
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

	private Ent_Customer getDefaultCustomer() throws Exception {
		// temporary assign a customer
		long custId = 1L;
		
		return getCustomerDao().findCustomerById(custId);
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
			
				// notif
				lc = (Listcell) item.getChildren().get(7);
				lc.setLabel("Tersimpan");
				lc.setStyle("color: --var(--bs-success");
			
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
		// re-load
		loadInventoryList();
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
