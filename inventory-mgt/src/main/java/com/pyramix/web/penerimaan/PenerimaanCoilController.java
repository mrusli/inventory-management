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

import com.pyramix.domain.entity.Ent_Inventory;
import com.pyramix.domain.entity.Ent_InventoryCode;
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
	
	private Listbox receiveCoilListbox;
	
	private ListModelList<Ent_Inventory> inventoryModelList = null;
	private List<Ent_InventoryCode> inventoryCodeList = null;
	
	public void onCreate$infoPenerimaanCoilPanel(Event event) throws Exception {
		log.info("infoPenerimaanCoilPanel created");
		
		// list of inventoryCodes
		inventoryCodeList = getInventoryCodeDao().findAllInventoryCode();
		
		// load inventory
		loadInventoryList();
		
		// display
		displayInventoryList();
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
				lc = new Listcell();
						// inventory.getCustomer().getCompanyType()+" "+
				 		// inventory.getCustomer().getCompanyLegalName());
				lc.setParent(item);
				
				// No.Coil
				lc = new Listcell();
						// inventory.getMarking());
				lc.setParent(item);
				
				// add
				lc = new Listcell();
				lc.setParent(item);
				
				Button button = new Button();
				button.setParent(lc);
				button.setIconSclass("z-icon-level-down");
				button.setSclass("compButton");
				button.setStyle("background-color:var(--bs-success);");
				button.addEventListener(Events.ON_CLICK, addRow());				

				item.setValue(inventory);
			}
		};
	}

	protected EventListener<Event> addRow() {
		
		return new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				log.info("addRow click");
				
				coilAddRow();

			}
		};
	}

	protected void coilAddRow() throws Exception {
		// add to the last pos
		Ent_Inventory inventory = addInventoryInLastPos();
		
		// will cause the listbox to immediately render all listitems 
		// based on the current model and renderer (template or 
		// ListItemRenderer)
		receiveCoilListbox.renderAll();
		
		// since the item is added into the last item,
		// we set the active page of the last page of the listbox
		int lastPage =
				receiveCoilListbox.getPageCount();
		receiveCoilListbox.setActivePage(lastPage-1);
		
		// access the lastitem of the listbox,
		// which is the item just added via listmodellist
		int lastItem =
				receiveCoilListbox.getItemCount();
		// log.info("lastItem-----: "+String.valueOf(lastItem));
		if (lastItem>1) {
			Listcell lc;
			// log.info("need to get to the prev item");
			Listitem prevItem =	receiveCoilListbox.getItemAtIndex(lastItem-2);
			// tglTerima
			lc = (Listcell) prevItem.getChildren().get(0);
			LocalDate localDate = (LocalDate) lc.getAttribute("tglTerima");
			inventory.setReceiveDate(localDate);
			// jenisCoil
			lc = (Listcell) prevItem.getChildren().get(1);
			Ent_InventoryCode invtCode = (Ent_InventoryCode) lc.getAttribute("jenisCoil");
			inventory.setInventoryCode(invtCode);
			// if (localDate != null) {
			//	log.info(dateToStringDisplay(localDate, getShortDateFormat(), getLocale()));
			//}
		}
		Listitem activeItem = 
		 		receiveCoilListbox.getItemAtIndex(lastItem-1);
		
		setupTglTerimaDatebox(activeItem, inventory.getReceiveDate());
		setupJenisCoilCombobox(activeItem, inventory.getInventoryCode());
		
	}

	public void onClick$coilAddButton(Event event) throws Exception {
		log.info("coilAddButton click");

		// temporary remove inventory from db
		inventoryModelList = null; 
				// new ListModelList<Ent_Inventory>(new ArrayList<Ent_Inventory>());
		receiveCoilListbox.setModel(inventoryModelList);
		
		Listitem activeItem = new Listitem();
		
		Listcell lc;
		// TglTerima
		lc = new Listcell();
		lc.setParent(activeItem);
		// JenisCoil
		lc = new Listcell();
		lc.setParent(activeItem);

		receiveCoilListbox.appendChild(activeItem);
		receiveCoilListbox.renderAll();
		
		Ent_Inventory inventory = new Ent_Inventory();
		// set to prevent error
		inventory.setReceiveDate(getLocalDate(getZoneId()));
		inventory.setInventoryCode(getDefaultInventoryCode());

		activeItem.setValue(inventory);
		
		setupTglTerimaDatebox(activeItem, inventory.getReceiveDate());
		setupJenisCoilCombobox(activeItem, inventory.getInventoryCode());
		
		// display
		// displayInventoryList();
		// add 1st row
		// coilAddRow();
	}

	private Ent_Inventory addInventoryInLastPos() throws Exception {
		Ent_Inventory inventory = new Ent_Inventory();
		// set to prevent error
		inventory.setReceiveDate(getLocalDate(getZoneId()));
		inventory.setInventoryCode(getDefaultInventoryCode());
		
		int posToAdd =
				inventoryModelList.size();
		inventoryModelList.add(posToAdd, inventory);
		
		return inventory;
	}

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
		datebox.setWidth("140px");
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
		combobox.setWidth("120px");
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
	
	public void onClick$saveButton(Event event) throws Exception {
		log.info("saveButton click");
	
		doSave(event.getTarget());		
	}
	
	public void doSave(Component target) throws Exception {
		Listcell lc;
		Ent_Inventory inventory;
		for (Listitem item : receiveCoilListbox.getItems()) {
			log.info("---index---: "+item.getIndex());

			inventory = item.getValue();
			
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
			
			// update inventory
			// getInventoryDao().update(inventory);
			
			Events.echoEvent("onEchoSave", target, null);
			
			break;
		}
	}
	
	public void onEchoSave(Event event) throws Exception {	
		doSave(event.getTarget());
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

}
