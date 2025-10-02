package com.pyramix.web.inventory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SortEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

import com.pyramix.domain.entity.Ent_InventoryCode;
import com.pyramix.domain.entity.Ent_InventoryType;
import com.pyramix.persistence.inventorytype.dao.InventoryTypeDao;
import com.pyramix.web.common.GFCBaseController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InventoryTypeController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1147277591984336828L;
	
	private InventoryTypeDao inventoryTypeDao;
	
	private Listbox inventoryTypeListbox, inventoryCodeListbox;
	private Label tipeLabel, brtJnsLabel, keteranganLabel;
	
	private ListModelList<Ent_InventoryType> inventoryTypeModelList = null;
	private ListModelList<Ent_InventoryCode> inventoryCodeModelList = null;
	private Ent_InventoryType selInvtType = null;
	
	public void onCreate$inventoryTypePanel(Event event) throws Exception {
		log.info("inventoryTypePanel created");
		
		// load
		loadInventoryTypeList();
		
		// display
		displayInventoryTypeList();
		
		// select
		selectInventoryType(0);
	}

	private void loadInventoryTypeList() throws Exception {
		List<Ent_InventoryType> inventoryTypeList = 
				getInventoryTypeDao().findAllInventoryType();
		inventoryTypeModelList =
				new ListModelList<Ent_InventoryType>(inventoryTypeList);
	}

	private void displayInventoryTypeList() {
		inventoryTypeListbox.setModel(inventoryTypeModelList);
		inventoryTypeListbox.setItemRenderer(getInventoryTypeListitemRenderer());
		
	}

	private ListitemRenderer<Ent_InventoryType> getInventoryTypeListitemRenderer() {
		
		return new ListitemRenderer<Ent_InventoryType>() {
			
			@Override
			public void render(Listitem item, Ent_InventoryType inventoryType, int index) throws Exception {
				Listcell lc;
				
				// tipe
				lc = new Listcell(inventoryType.getProductType());
				lc.setParent(item);
				
				// Berat-Jns
				lc = new Listcell(toDecimalFormat(
						BigDecimal.valueOf(inventoryType.getDensity()), getLocale(), getDecimalFormat()));
				lc.setParent(item);
				
				// Keterangan
				lc = new Listcell(inventoryType.getProductDescription());
				lc.setParent(item);
				
				// Edit / Save
				lc = new Listcell();
				lc.setParent(item);
				
				Button button = new Button();
				button.setParent(lc);
				button.setIconSclass("z-icon-pencil");
				button.setSclass("compButton");
				button.setStyle("background-color:var(--bs-warning);");
				button.addEventListener(Events.ON_CLICK, editInventoryType(inventoryType));				
				
				item.setValue(inventoryType);
			}
		};
	}
	
	protected EventListener<Event> editInventoryType(Ent_InventoryType inventoryType) {

		return new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				log.info("editInventoryType button click");
				Button button = (Button) event.getTarget();
				// get the current listitem
				Listitem activeItem = (Listitem) event.getTarget().getParent().getParent();

				if (inventoryType.isEditInProgress()) {
					// will update / save inventoryType
					log.info("will save or update inventoryType");
					// update
					Ent_InventoryType activeInvtType = getInventoryTypeDao().update(
							getUpdatedInventoryType(inventoryType, activeItem));
					
					// re-load inventoryType from db
					loadInventoryTypeList();
					
					// re-render the data in the listbox
					displayInventoryTypeList();
					
					// locate
					locateInventoryTypeData(activeInvtType);

					// set to false, because it's done updating
					inventoryType.setEditInProgress(false);
					// change to edit icon, so that this can be edited again
					modifToEdit(button);
					int lastIdx =
							inventoryTypeModelList.size()-1;
					// display
					selectInventoryType(lastIdx);
				} else {
					// allow to edit
					log.info("allow user to edit inventoryType");
					
					setupTipeTextbox(activeItem, inventoryType.getProductType());
					setupBeratJnsDoublebox(activeItem, inventoryType.getDensity());
					setupKeteranganTextbox(activeItem, inventoryType.getProductDescription());					
					// set to true, because we need to save / update this object
					inventoryType.setEditInProgress(true);
					// change to save icon, so that object can be saved
					modifToSave(button);					
				}
			}
		};
	}
	
	private void selectInventoryType(int idx) throws Exception {
//		inventoryTypeListbox.renderAll();
//		if (!inventoryTypeListbox.getItems().isEmpty()) {
//			Listitem item = 
//					inventoryTypeListbox.getItemAtIndex(0);
//			selInvtType = item.getValue();
//			log.info(selInvtType.toString());
//			displayInventoryCode(selInvtType);
//		}
		
		if (!inventoryTypeModelList.isEmpty()) {
			selInvtType = 
					inventoryTypeModelList.get(idx);
			log.info(selInvtType.toString());
			displayInventoryCode(selInvtType);
		}
	}	
	
	public void onSelect$inventoryTypeListbox(Event event) throws Exception {
		selInvtType = inventoryTypeListbox.getSelectedItem().getValue();
		if (selInvtType != null) {
			displayInventoryCode(selInvtType);
		}
	}

	private void displayInventoryCode(Ent_InventoryType selInvtType) throws Exception {
		log.info("displayInventoryCode...");
		tipeLabel.setValue(selInvtType.getProductType()); 
		brtJnsLabel.setValue(toDecimalFormat(
						BigDecimal.valueOf(selInvtType.getDensity()), getLocale(), getDecimalFormat()));
		keteranganLabel.setValue(selInvtType.getProductDescription());
		
		// load the codes from selected type and display
		loadToDisplayInventoryCodes(selInvtType);
	}	


	private void resetInventoryCode(Ent_InventoryType inventoryType) {
		log.info("resetInventoryCode...");
		tipeLabel.setValue("");
		brtJnsLabel.setValue("");
		keteranganLabel.setValue("");
		
		inventoryType.setInventoryCodes(new ArrayList<Ent_InventoryCode>());
		loadToDisplayInventoryCodes(inventoryType);
		
	}
	
	public void onClick$addInventoryTypeButton(Event event) throws Exception {
		log.info("addInventoryTypeButton click");
		// add to the last pos
		Ent_InventoryType inventoryType = addInventoryTypeInLastPos();
		
		// will cause the listbox to immediately render all listitems 
		// based on the current model and renderer (template or 
		// ListItemRenderer)
		inventoryTypeListbox.renderAll();
		
		// since the item is added into the last item,
		// we set the active page of the last page of the listbox
		int lastPage =
				inventoryTypeListbox.getPageCount();
		inventoryTypeListbox.setActivePage(lastPage-1);
		
		// access the lastitem of the listbox,
		// which is the item just added via listmodellist
		int lastItem =
				inventoryTypeListbox.getItemCount();
		Listitem activeItem = 
				inventoryTypeListbox.getItemAtIndex(lastItem-1);
		
		setupTipeTextbox(activeItem, inventoryType.getProductType());
		setupBeratJnsDoublebox(activeItem, inventoryType.getDensity());
		setupKeteranganTextbox(activeItem, inventoryType.getProductDescription());
		setupEditToSaveButton(activeItem, 3);
		// switch to true so that it'll be updated / saved
		inventoryType.setEditInProgress(true);
		
		// clean up the details
		resetInventoryCode(inventoryType);
	}

	private Ent_InventoryType addInventoryTypeInLastPos() {
		Ent_InventoryType inventoryType = new Ent_InventoryType();
		
		int posToAdd =
				inventoryTypeModelList.size();
		inventoryTypeModelList.add(posToAdd, inventoryType);
		
		return inventoryType;
	}

	protected Ent_InventoryType getUpdatedInventoryType(Ent_InventoryType inventoryType, Listitem activeItem) {
		inventoryType.setProductType(getTipeTextbox(activeItem));
		inventoryType.setDensity(getBeratJnsDoublebox(activeItem));
		inventoryType.setProductDescription(getKeteranganTextbox(activeItem));
		
		return inventoryType;
	}	

	private String getTipeTextbox(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(0);
		Textbox textbox = (Textbox) lc.getFirstChild();
		
		return textbox.getValue();
	}
	
	private void setupTipeTextbox(Listitem activeItem, String productType) {
		Listcell lc = (Listcell) activeItem.getChildren().get(0);
		lc.setLabel("");
		Textbox textbox = new Textbox();
		textbox.setWidth("140px");
		textbox.setValue(productType);
		textbox.setParent(lc);		
	}
	
	private double getBeratJnsDoublebox(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(1);
		Doublebox doublebox = (Doublebox) lc.getFirstChild();
		
		return doublebox.getValue();
	}	

	private void setupBeratJnsDoublebox(Listitem activeItem, double density) {
		Listcell lc = (Listcell) activeItem.getChildren().get(1);
		lc.setLabel("");
		Doublebox doublebox = new Doublebox();
		doublebox.setWidth("100px");
		doublebox.setLocale(getLocale());
		doublebox.setValue(density);
		doublebox.setParent(lc);
	}	
	
	private String getKeteranganTextbox(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(2);
		Textbox textbox = (Textbox) lc.getFirstChild();
		
		return textbox.getValue();
	}	
	
	private void setupKeteranganTextbox(Listitem activeItem, String productDescription) {
		Listcell lc = (Listcell) activeItem.getChildren().get(2);
		lc.setLabel("");
		Textbox textbox = new Textbox();
		textbox.setWidth("260px");
		textbox.setValue(productDescription);
		textbox.setParent(lc);			
	}	
	
	protected void modifToEdit(Button button) {
		button.setIconSclass("z-icon-pencil");
		button.setStyle("background-color:var(--bs-warning);");			
	}

	protected void modifToSave(Button button) {
		button.setIconSclass("z-icon-floppy-o");
		button.setStyle("background-color:var(--bs-primary);");
	}	
	
	private void setupEditToSaveButton(Listitem activeItem, int pos) {
		Listcell lc = (Listcell) activeItem.getChildren().get(pos);
		Button button = (Button) lc.getFirstChild();
		
		// signal it can be used to save the entry
		modifToSave(button);		
	}
	
	protected void locateInventoryTypeData(Ent_InventoryType activeInvtType) {
		inventoryTypeListbox.renderAll();
		for(Listitem item : inventoryTypeListbox.getItems()) {
			Ent_InventoryType invtType = item.getValue();
			if (activeInvtType.getId()==invtType.getId()) {
				inventoryTypeListbox.setSelectedItem(item);
				break;
			}
		}
		
	}	
	
	private void loadToDisplayInventoryCodes(Ent_InventoryType selInvtType) {
		inventoryCodeModelList = 
				new ListModelList<Ent_InventoryCode>(selInvtType.getInventoryCodes());
		
		// display
		inventoryCodeListbox.setModel(inventoryCodeModelList);
		inventoryCodeListbox.setItemRenderer(getInventoryCodeListitemRenderer());
	}	
	
	private ListitemRenderer<Ent_InventoryCode> getInventoryCodeListitemRenderer() {
		
		return new ListitemRenderer<Ent_InventoryCode>() {
			
			@Override
			public void render(Listitem item, Ent_InventoryCode inventoryCode, int index) throws Exception {
				Listcell lc;
				
				// Kode
				lc = new Listcell(inventoryCode.getProductCode());
				lc.setParent(item);
				
				// Keterangan
				lc = new Listcell(inventoryCode.getCodeDescription());
				lc.setParent(item);
				
				// edit / save
				lc = new Listcell();
				lc.setParent(item);
				
				Button button = new Button();
				button.setParent(lc);
				button.setIconSclass("z-icon-pencil");
				button.setSclass("compButton");
				button.setStyle("background-color:var(--bs-warning);");
				button.addEventListener(Events.ON_CLICK, editInventoryCode(inventoryCode));	
				
				item.setValue(inventoryCode);
			}
		};
	}

	protected EventListener<Event> editInventoryCode(Ent_InventoryCode inventoryCode) {
		
		return new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				log.info("editInventoryCode button click");
				Button button = (Button) event.getTarget();
				// get the current listitem
				Listitem activeItem = (Listitem) event.getTarget().getParent().getParent();
				// get the data
				Ent_InventoryCode activeInvtCode = activeItem.getValue();
				
				if (activeInvtCode.isEditInProgress()) {
					// to update / save
					log.info("to update or save");
					int idx = 0;
					if (activeInvtCode.isAddInProgress()) {
						log.info("add to inventoryType");
						// add
						selInvtType.getInventoryCodes().add(
								getUpdatedInventoryCode(activeItem, activeInvtCode));
						activeInvtCode.setAddInProgress(false);
					} else {
						log.info("use the index of the inventory code to update");
						idx = selInvtType.getInventoryCodes().indexOf(activeInvtCode);
						// set
						selInvtType.getInventoryCodes().set(
								idx, 
								getUpdatedInventoryCode(activeItem, activeInvtCode));
					}
					// update
					getInventoryTypeDao().update(selInvtType);
					
					// re-load
					loadToDisplayInventoryCodes(selInvtType);
					
					// look for the data
					locateInventoryCodeData(selInvtType.getInventoryCodes().get(idx));
					
					// set to false, because it's done updating
					activeInvtCode.setEditInProgress(false);
					// change to edit icon, so that this can be edited again
					modifToEdit(button);
				} else {
					// allow to edit
					log.info("allow to edit");

					setupKodeTextbox(activeItem, activeInvtCode.getProductCode());
					setupKodeKeteranganTextbox(activeItem, activeInvtCode.getCodeDescription());
					
					// set to true, because we need to save / update this object
					activeInvtCode.setEditInProgress(true);
					// change to save icon, so that object can be saved
					modifToSave(button);					
				}
				
			}
		};
	}

	private void locateInventoryCodeData(Ent_InventoryCode ent_InventoryCode) {
		inventoryCodeListbox.renderAll();
		for(Listitem item : inventoryCodeListbox.getItems()) {
			Ent_InventoryCode invtCode = item.getValue();
			if (ent_InventoryCode.getId()==invtCode.getId()) {
				inventoryCodeListbox.setSelectedItem(item);
				break;
			}
		}
	}

	public void onClick$addInventoryCodeButton(Event event) throws Exception {
		// add to the last pos
		Ent_InventoryCode activeInvtCode = addInventoryCodeInLastPos();
		
		// will cause the listbox to immediately render all listitems 
		// based on the current model and renderer (template or 
		// ListItemRenderer)
		inventoryCodeListbox.renderAll();
		
		// since the item is added into the last item,
		// we set the active page of the last page of the listbox
		int lastPage =
				inventoryCodeListbox.getPageCount();
		inventoryCodeListbox.setActivePage(lastPage-1);
		
		// access the lastitem of the listbox,
		// which is the item just added via listmodellist
		int lastItem =
				inventoryCodeListbox.getItemCount();
		Listitem activeItem =
				inventoryCodeListbox.getItemAtIndex(lastItem-1);
		
		setupKodeTextbox(activeItem, activeInvtCode.getProductCode());
		setupKodeKeteranganTextbox(activeItem, activeInvtCode.getCodeDescription());
		setupEditToSaveButton(activeItem, 2);
		
		// switch to true so that it'll be updated / saved
		activeInvtCode.setEditInProgress(true);
		// switch to true so that the object will added to type
		activeInvtCode.setAddInProgress(true);
	}

	private String getKodeTextbox(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(0);
		Textbox textbox = (Textbox) lc.getFirstChild();
		
		return textbox.getValue();
	}
	
	private void setupKodeTextbox(Listitem activeItem, String productCode) {
		Listcell lc = (Listcell) activeItem.getChildren().get(0);
		lc.setLabel("");
		Textbox textbox = new Textbox();
		textbox.setWidth("140px");
		textbox.setValue(productCode);
		textbox.setParent(lc);
	}

	private String getKodeKeteranganTextbox(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(1);
		Textbox textbox = (Textbox) lc.getFirstChild();
		
		return textbox.getValue();		
	}
	
	private void setupKodeKeteranganTextbox(Listitem activeItem, String codeDescription) {
		Listcell lc = (Listcell) activeItem.getChildren().get(1);
		lc.setLabel("");
		Textbox textbox = new Textbox();
		textbox.setWidth("200px");
		textbox.setValue(codeDescription);
		textbox.setParent(lc);
	}

	private Ent_InventoryCode addInventoryCodeInLastPos() {
		Ent_InventoryCode invtCode = new Ent_InventoryCode();
		
		int posToAdd =
				inventoryCodeModelList.size();
		inventoryCodeModelList.add(posToAdd, invtCode);
		
		return invtCode;
	}

	private Ent_InventoryCode getUpdatedInventoryCode(Listitem activeItem, Ent_InventoryCode activeInvtCode) {
		activeInvtCode.setProductCode(getKodeTextbox(activeItem));
		activeInvtCode.setCodeDescription(getKodeKeteranganTextbox(activeItem));
		
		return activeInvtCode;
	}
	
	public void onSort$tipeListheader(SortEvent sortEvent) throws Exception {
		log.info("tipeListheader click");
		
		// loadInventoryTypeList();
		
		// displayInventoryTypeList();
		
		selectInventoryType(0);
	}

	public InventoryTypeDao getInventoryTypeDao() {
		return inventoryTypeDao;
	}

	public void setInventoryTypeDao(InventoryTypeDao inventoryTypeDao) {
		this.inventoryTypeDao = inventoryTypeDao;
	}

}
