package com.pyramix.web.product;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pyramix.domain.entity.Enm_StatusProcess;
import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.domain.entity.Ent_InventoryProcess;
import com.pyramix.domain.entity.Ent_InventoryProcessMaterial;
import com.pyramix.persistence.inventoryprocess.dao.InventoryProcessDao;
import com.pyramix.web.common.GFCBaseController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8679083207182593184L;

	private InventoryProcessDao inventoryProcessDao;
	
	private Combobox customerProcessCombobox, processCombobox;
	private Listbox materialListbox;
	
	public void onCreate$infoProductCoilPanel(Event event) throws Exception {
		log.info("infoProductCoilPanel created");
		
		// get all the customers from inventoryProcess
		Set<Ent_Customer> customerSet = inventoryProcessCustomers();
		// load customer
		loadCustomerCombobox(customerSet);
		// select
		if (!customerProcessCombobox.getItems().isEmpty()) {
			customerProcessCombobox.setSelectedIndex(0);
			
			onSelectCustomerProcessCombobox();
		}
	}

	private Set<Ent_Customer> inventoryProcessCustomers() throws Exception {
		List<Ent_InventoryProcess> invtProcList =
				getInventoryProcessDao().findAllInventoryProcess();
		Ent_Customer cust;
		Set<Ent_Customer> customerSet = new HashSet<Ent_Customer>();
		for (Ent_InventoryProcess invtProc : invtProcList) {
			cust = invtProc.getCustomer();
			customerSet.add(cust);
		}
		
		return customerSet;
	}

	private void loadCustomerCombobox(Set<Ent_Customer> customerSet) {
		Comboitem comboitem;
		for(Ent_Customer cust : customerSet) {
			comboitem = new Comboitem();
			comboitem.setLabel(cust.getCompanyType().toString()+"."+
					cust.getCompanyLegalName());
			comboitem.setValue(cust);
			comboitem.setParent(customerProcessCombobox);
		}
	}	

	private void onSelectCustomerProcessCombobox() throws Exception {
		
		Ent_Customer selCust = customerProcessCombobox.getSelectedItem().getValue();
		// list the processes status PROCESS
		List<Ent_InventoryProcess> processList =
				getInventoryProcessDao().findInventoryByCustomerByStatus(
						selCust, Enm_StatusProcess.Proses);
		loadProcessCombobox(processList);
		// select
		if (!processCombobox.getItems().isEmpty()) {
			processCombobox.setSelectedIndex(0);
			
			onSelectProcessCombobox();
		}		
	}
	
	public void onSelect$customerProcessCombobox(Event event) throws Exception {
		onSelectCustomerProcessCombobox();
	}
	
	private void loadProcessCombobox(List<Ent_InventoryProcess> processList) {
		Comboitem comboitem;
		for (Ent_InventoryProcess invtProc : processList) {
			comboitem = new Comboitem();
			comboitem.setLabel(invtProc.getProcessNumber().getSerialComp());
			comboitem.setValue(invtProc);
			comboitem.setParent(processCombobox);
		}
	}
	
	public void onSelect$processCombobox(Event event) throws Exception {
		onSelectProcessCombobox();
	}

	private void onSelectProcessCombobox() throws Exception {
		Ent_InventoryProcess invtProc =
				processCombobox.getSelectedItem().getValue();
		// proxy
		invtProc = getInventoryProcessDao()
				.findInventoryProcessMaterialsByProxy(invtProc.getId());
		// display the materials from this process
		renderProcessMaterials(invtProc.getProcessMaterials());		
	}
	
	private void renderProcessMaterials(List<Ent_InventoryProcessMaterial> processMaterials) {
		ListModelList<Ent_InventoryProcessMaterial> materialModelList =
				new ListModelList<Ent_InventoryProcessMaterial>(processMaterials);
		
		materialListbox.setModel(materialModelList);
		materialListbox.setItemRenderer(getProcessMaterialItemRenderer());
	}	
	
	private ListitemRenderer<Ent_InventoryProcessMaterial> getProcessMaterialItemRenderer() {
		
		return new ListitemRenderer<Ent_InventoryProcessMaterial>() {
			
			@Override
			public void render(Listitem item, Ent_InventoryProcessMaterial material, int index) throws Exception {
				Listcell lc;
				
				// Jenis-Coil
				lc = new Listcell(material.getInventoryCode().getProductCode());
				lc.setParent(item);

				// Spek
				lc = new Listcell(
						toDecimalFormat(new BigDecimal(material.getThickness()), getLocale(), "#0,00")+" x "+
						toDecimalFormat(new BigDecimal(material.getWidth()), getLocale(), "###.###")+" x "+
						toDecimalFormat(new BigDecimal(material.getLength()), getLocale(), "###.###"));
				lc.setParent(item);
				
				// Qty(Kg)
				lc = new Listcell(
						toDecimalFormat(new BigDecimal(material.getWeightQuantity()), getLocale(), "###.###"));
				lc.setParent(item);
				
				// Marking
				lc = new Listcell(material.getMarking());
				lc.setParent(item);
				
			}
		};
	}

	public InventoryProcessDao getInventoryProcessDao() {
		return inventoryProcessDao;
	}

	public void setInventoryProcessDao(InventoryProcessDao inventoryProcessDao) {
		this.inventoryProcessDao = inventoryProcessDao;
	}
}
