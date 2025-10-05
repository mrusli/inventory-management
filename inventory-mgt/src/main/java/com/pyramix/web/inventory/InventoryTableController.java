package com.pyramix.web.inventory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pyramix.domain.entity.Ent_InventoryCode;
import com.pyramix.domain.entity.Ent_InventoryTable;
import com.pyramix.domain.entity.Ent_InventoryType;
import com.pyramix.persistence.inventorytable.dao.InventoryTableDao;
import com.pyramix.persistence.inventorytype.dao.InventoryTypeDao;
import com.pyramix.web.common.GFCBaseController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InventoryTableController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5765283367825806301L;
	
	private InventoryTypeDao inventoryTypeDao;
	private InventoryTableDao inventoryTableDao;
	
	private Combobox inventoryCodeCombobox;
	private Listbox inventoryTableListbox;
	
	private List<Ent_InventoryCode> inventoryCodeList;
	private ListModelList<Ent_InventoryTable> inventoryTableListModel;
	private Ent_InventoryCode selInventoryCode;
	
	public void onCreate$inventoryCodePanel(Event event) throws Exception {
		log.info("inventoryCodePanel created");
		
		// load inventoryCode
		inventoryCodeList = getInventoryCodeList();
		
		// load into combobox
		setupInventoryCodeCombobox();
		// select the 1st item
		if (!inventoryCodeCombobox.getItems().isEmpty()) {
			inventoryCodeCombobox.setSelectedIndex(0);
		}
		
		// load inventoryTable
		loadInventoryTableListModel();
		// render
		renderInventoryTable();
	}

	private List<Ent_InventoryCode> getInventoryCodeList() throws Exception {
		List<Ent_InventoryType> inventoryTypeList =
				getInventoryTypeDao().findAllInventoryType();
		Set<Ent_InventoryCode> inventoryCodeSet = new HashSet<Ent_InventoryCode>();
		for(Ent_InventoryType inventoryType : inventoryTypeList) {
			for(Ent_InventoryCode inventoryCode : inventoryType.getInventoryCodes()) {
				inventoryCodeSet.add(inventoryCode);
			}
		}
		
		return new ArrayList<Ent_InventoryCode>(inventoryCodeSet);
	}

	private void setupInventoryCodeCombobox() {
		// sort before add
		Collections.sort(inventoryCodeList, new Comparator<Ent_InventoryCode>() {

			@Override
			public int compare(Ent_InventoryCode o1, Ent_InventoryCode o2) {
				
				return o1.getProductCode().compareTo(o2.getProductCode());
			}
			
		});
		
		Comboitem comboitem;
		for (Ent_InventoryCode inventoryCode : inventoryCodeList) {
			comboitem = new Comboitem();
			comboitem.setLabel(inventoryCode.getProductCode());
			comboitem.setValue(inventoryCode);
			comboitem.setParent(inventoryCodeCombobox);
		}
	}
	
	public void onSelect$InventoryCodeCombobox(Event event) throws Exception {
		log.info("selected: "+inventoryCodeCombobox.getSelectedItem().getValue());
		// re-load the inventoryTable according to inventoryCode
		
	}
	
	private void loadInventoryTableListModel() throws Exception {
		List<Ent_InventoryTable> inventoryTableList =
				getInventoryTableDao().findAllInventoryTable();
		
		inventoryTableListModel = new ListModelList<Ent_InventoryTable>(inventoryTableList);
	}
	
	private void renderInventoryTable() {
		inventoryTableListbox.setModel(inventoryTableListModel);
		inventoryTableListbox.setItemRenderer(getInventoryTableListitemRenderer());
	}
	
	private ListitemRenderer<Ent_InventoryTable> getInventoryTableListitemRenderer() {
		
		return new ListitemRenderer<Ent_InventoryTable>() {
			
			@Override
			public void render(Listitem item, Ent_InventoryTable invtTable, int index) throws Exception {
				Listcell lc;
				
				// Jenis-Coil
				lc = new Listcell(invtTable.getInventoryCode().getProductCode());
				lc.setAttribute("inventoryCode", invtTable.getInventoryCode());
				lc.setParent(item);
				
				// Spek
				lc = new Listcell(
						toDecimalFormat(new BigDecimal(invtTable.getThickness()), getLocale(), "#0,##")+" x "+
						toDecimalFormat(new BigDecimal(invtTable.getWidth()), getLocale(), "##.###,##")+" x "+
						toDecimalFormat(new BigDecimal(invtTable.getLength()), getLocale(), "##.###,##")
						);
				lc.setParent(item);
				
				// Qty(Kg)
				lc = new Listcell(
						toDecimalFormat(new BigDecimal(invtTable.getWeightQuantity()), getLocale(), "###.###,00")						
						);
				lc.setParent(item);

				// Edit / Save
				lc = new Listcell();
				lc.setParent(item);
				
				Button button = new Button();
				button.setParent(lc);
				button.setSclass("compButton");
				modifToEdit(button);
				button.addEventListener(Events.ON_CLICK, editInventoryTable(invtTable));				

				// Delete
				lc = new Listcell();
				lc.setParent(item);
				
				button = new Button();
				button.setParent(lc);
				button.setSclass("compButton");
				button.setIconSclass("z-icon-trash");
				button.setStyle("background-color:var(--bs-danger);");
				button.addEventListener(Events.ON_CLICK, deleteInventoryTable(invtTable));
				item.setValue(invtTable);
			}
		};
	}
	
	protected EventListener<Event> deleteInventoryTable(Ent_InventoryTable invtTable) {

		return new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				
				if (invtTable.isEditInProgress()) {
					log.info("remove from model list");
					// remove from the model list
					int posToRemove = 
							inventoryTableListModel.size()-1;
					inventoryTableListModel.remove(posToRemove);
					
				} else {
					log.info("remove from db");
					getInventoryTableDao().delete(invtTable);
					// re-load
					loadInventoryTableListModel();
					// re-render
					renderInventoryTable();
				}
			}
		};
	}

	protected EventListener<Event> editInventoryTable(Ent_InventoryTable invtTable) {
		
		return new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				log.info("editInventoryTable button click");
				Button button = (Button) event.getTarget();
				// get the current listitem
				Listitem activeItem = (Listitem) event.getTarget().getParent().getParent();
				// get the data
				// Ent_InventoryTable activeInvtTable = activeItem.getValue();
				
				if (invtTable.isEditInProgress()) {
					// to update / save
					log.info("to update or save");
					Ent_InventoryTable activeInvtTable = getInventoryTableDao().update(
							getUpdatedInventoryTable(invtTable, activeItem));
					log.info(activeInvtTable.toString());
					
					// re-load inventoryTable from db
					loadInventoryTableListModel();
					// re-render the data into the listbox
					renderInventoryTable();
					// locate
					locateInventoryTableData(activeInvtTable);
					
					// set to false, because it's done updating
					activeInvtTable.setEditInProgress(false);
					// change to edit icon
					modifToEdit(button);
					
				} else {
					// set to edit
					log.info("set to edit");
					
					setSpek(activeItem, invtTable.getThickness(), invtTable.getWidth(), 
							invtTable.getLength(), invtTable.getInventoryCode());
					setQtyKg(activeItem, invtTable.getWeightQuantity());
					
					// set to true, becuase we need to save / update
					invtTable.setEditInProgress(true);
					// change to save icon
					modifToSave(button);
				}
			}
		};
	}

	protected void locateInventoryTableData(Ent_InventoryTable activeInvtTable) {
		inventoryTableListbox.renderAll();
		for(Listitem item : inventoryTableListbox.getItems()) {
			Ent_InventoryTable invtTable = item.getValue();
			if (activeInvtTable.getId()==invtTable.getId()) {
				inventoryTableListbox.setSelectedItem(item);
				break;
			}
		}
	}	
	
	public void onClick$addInventoryTableButton(Event event) throws Exception {
		log.info("addInventoryTableButton click");
		
		selInventoryCode = inventoryCodeCombobox.getSelectedItem().getValue();
		log.info(selInventoryCode.toString());

		// add to the last pos using selInventoryCode
		Ent_InventoryTable inventoryTable = addInventoryTableInLastPos();
		inventoryTable.setInventoryCode(selInventoryCode);
		inventoryTable.setEditInProgress(true);
		
		// last item
		Listitem activeItem = getLastItem();
		
		setJenisCoil(activeItem, inventoryTable.getInventoryCode());
		setSpek(activeItem, inventoryTable.getThickness(), inventoryTable.getWidth(), 
				inventoryTable.getLength(), inventoryTable.getInventoryCode());
		setQtyKg(activeItem, inventoryTable.getWeightQuantity());
		setEditToSaveButton(activeItem);
	}

	private Ent_InventoryTable addInventoryTableInLastPos() {
		Ent_InventoryTable inventoryTable = new Ent_InventoryTable();
		
		int posToAdd = 
				inventoryTableListModel.size();
		inventoryTableListModel.add(posToAdd, inventoryTable);
		
		return inventoryTable;
	}

	private Listitem getLastItem() {
		// renderAll
		inventoryTableListbox.renderAll();
		// get the last item
		int lastItem =
				inventoryTableListbox.getItemCount();
		
		return inventoryTableListbox.getItemAtIndex(lastItem-1);
	}
	
	private Ent_InventoryCode getJenisCoil(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(0);
		
		return (Ent_InventoryCode) lc.getAttribute("inventoryCode");
	}	

	private void setJenisCoil(Listitem activeItem, Ent_InventoryCode inventoryCode) {
		Listcell lc = (Listcell) activeItem.getChildren().get(0);
		lc.setLabel("");
		// non-editable - selected in the header
		lc.setLabel(inventoryCode.getProductCode());
		lc.setAttribute("inventoryCode", inventoryCode);
	}
	
	private double getSpek(Listitem activeItem, int idx) {
		Listcell lc = (Listcell) activeItem.getChildren().get(1);
		// doublebox 
		Doublebox doublebox = (Doublebox) lc.getChildren().get(idx);
		
		return doublebox.getValue();
	}	
	
	private void setSpek(Listitem activeItem, double thickness, double width, double length, 
			Ent_InventoryCode inventoryCode) {
		Listcell lc = (Listcell) activeItem.getChildren().get(1);
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
		label = new Label();
		label.setValue(" ");
		label.setParent(lc);
		// calc
		Button button = new Button();
		button.setIconSclass("z-icon-calculator");
		button.setSclass("compButton");
		button.setStyle("background-color: var(--bs-warning)");
		button.setParent(lc);
		button.addEventListener(Events.ON_CLICK, calcInventoryTable(inventoryCode));
	}
	
	private EventListener<Event> calcInventoryTable(Ent_InventoryCode inventoryCode) {
		
		return new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				double thck, wdth, lgth, dens;
				log.info("calculate inventory table...");
				Listcell lc = (Listcell) event.getTarget().getParent();
				// thickness
				Doublebox doublebox = (Doublebox) lc.getChildren().get(0);
				thck = doublebox.getValue();
				log.info(String.valueOf(thck));
				// width
				doublebox = (Doublebox) lc.getChildren().get(2);
				wdth = doublebox.getValue();
				log.info(String.valueOf(wdth));
				// length
				doublebox = (Doublebox) lc.getChildren().get(4);
				lgth = doublebox.getValue();
				log.info(String.valueOf(lgth));
				// density
				dens = inventoryCode.getInventoryType().getDensity();
				log.info(String.valueOf(dens));
				
				double qtyKg = thck * wdth * lgth * dens / 1000000;
				log.info(String.valueOf(qtyKg));
				
				Listitem item = (Listitem) event.getTarget().getParent().getParent();
				Doublebox ansDoublebox = (Doublebox) item.getChildren().get(2).getFirstChild();
				ansDoublebox.setValue(qtyKg);
			}
		};
	}


	private double getQtyKg(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(2);
		// Qty(Kg)
		Doublebox doublebox = (Doublebox) lc.getFirstChild();
		
		return doublebox.getValue();
	}	
	
	private void setQtyKg(Listitem activeItem, double weightQuantity) {
		Listcell lc = (Listcell) activeItem.getChildren().get(2);
		lc.setLabel("");
		// Qty(Kg)
		Doublebox doublebox = new Doublebox();
		doublebox.setLocale(getLocale());
		doublebox.setWidth("120px");
		doublebox.setValue(weightQuantity);
		doublebox.setParent(lc);
	}
	
	private void setEditToSaveButton(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(3);
		Button button = (Button) lc.getFirstChild();
		
		// signal it can be used to save the entry
		modifToSave(button);
	}

	protected Ent_InventoryTable getUpdatedInventoryTable(Ent_InventoryTable activeInvtTable, Listitem activeItem) {
		activeInvtTable.setInventoryCode(getJenisCoil(activeItem));
		activeInvtTable.setThickness(getSpek(activeItem, 0));
		activeInvtTable.setWidth(getSpek(activeItem, 2));
		activeInvtTable.setLength(getSpek(activeItem, 4));
		activeInvtTable.setWeightQuantity(getQtyKg(activeItem));
		
		return activeInvtTable;
	}	

	protected void modifToSave(Button button) {
		button.setIconSclass("z-icon-floppy-disk");
		button.setStyle("background-color:var(--bs-primary);");
	}

	protected void modifToEdit(Button button) {
		button.setIconSclass("z-icon-pen-to-square");
		button.setStyle("background-color:var(--bs-warning);");			
	}	
	
	public InventoryTypeDao getInventoryTypeDao() {
		return inventoryTypeDao;
	}

	public void setInventoryTypeDao(InventoryTypeDao inventoryTypeDao) {
		this.inventoryTypeDao = inventoryTypeDao;
	}

	public InventoryTableDao getInventoryTableDao() {
		return inventoryTableDao;
	}

	public void setInventoryTableDao(InventoryTableDao inventoryTableDao) {
		this.inventoryTableDao = inventoryTableDao;
	}

}
