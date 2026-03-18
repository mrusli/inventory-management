package com.pyramix.web.product;

import java.math.BigDecimal;
import java.util.ArrayList;
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

import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.domain.entity.Ent_InventoryCode;
import com.pyramix.domain.entity.Ent_InventoryCustomer;
import com.pyramix.persistence.customer.dao.CustomerDao;
import com.pyramix.persistence.inventorycode.dao.InventoryCodeDao;
import com.pyramix.persistence.inventorycustomer.dao.InventoryCustomerDao;
import com.pyramix.web.common.GFCBaseController;

import lombok.extern.slf4j.Slf4j;

/**
 * Products from the results of processes saved as Customer Inventory
 */
@Slf4j
public class HasilProduksiController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3314595534486563194L;
	
	private InventoryCustomerDao inventoryCustomerDao;
	private InventoryCodeDao inventoryCodeDao;
	private CustomerDao customerDao;

	private Combobox customerCombobox, jenisCoilCombobox;
	private Listbox productListbox;
	
	private List<Ent_InventoryCode> inventoryCodeList = null;
	private List<Ent_Customer> customerList = null;
	private ListModelList<Ent_InventoryCustomer> inventoryCustomerModelList = null;
	
	private final String THICKNESS_FORMAT = "#0,00";
	
	public void onCreate$infoHasilProduksiPanel(Event event) throws Exception {
		log.info("infoHasilProduksiPanel created");
		
		// list
		inventoryCodeList = getInventoryCodeDao().findAllInventoryCodesSorted();
		customerList = getCustomerDao().findAllActiveCustomerSorted();
		
		// load customer selection combobox 
		// (only customers in the inventoryCustomer list)
		loadCustomerSelection();
		// load inventory code
		// (all codes)
		loadJenisCoilCombobox();
		
		// load inventory (null -> all customers' inventory)
		loadInventoryList(null, null);
		// display
		displayInventoryList();
	}

	private void loadCustomerSelection() throws Exception {
		// find unique customers in the inventory list
		List<Ent_Customer> uniqueCustList = 
				findUniqueCustomersFromCustomerInventoryList();
		uniqueCustList.sort((n1,n2) -> {
			return n1.getCompanyLegalName().compareTo(n2.getCompanyLegalName());
		});
		// uniqueCustList.forEach(c -> log.info(c.getCompanyLegalName()));
		
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

	private List<Ent_Customer> findUniqueCustomersFromCustomerInventoryList() throws Exception {
		Set<Ent_Customer> customerSet = new HashSet<Ent_Customer>();
		List<Ent_InventoryCustomer> inventoryCustomerList =
				getInventoryCustomerDao().findAllInventoryCustomer();
		for (Ent_InventoryCustomer invtCust : inventoryCustomerList) {
			customerSet.add(invtCust.getCustomer());
		}
		
		return new ArrayList<Ent_Customer>(customerSet);
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

	private void loadInventoryList(Ent_Customer customer, Ent_InventoryCode invtCode) throws Exception {
		List<Ent_InventoryCustomer> inventoryCustomerList = null;
		if (customer==null && invtCode==null) {
			inventoryCustomerList = 
					getInventoryCustomerDao().findAllInventoryCustomer();			
		} else if (customer!=null && invtCode==null) {
			inventoryCustomerList =
					getInventoryCustomerDao().findInventoryCustomerByCustomer(customer);
			
		} else if (customer==null && invtCode!=null) {
			inventoryCustomerList =
					getInventoryCustomerDao().findInventoryCustomerByInventoryCode(invtCode);
		} else {
			inventoryCustomerList =
					getInventoryCustomerDao().findInventoryCustomerByCustomer_InventoryCode_NonStatus(customer, invtCode);
		}
		
		inventoryCustomerModelList = new ListModelList<Ent_InventoryCustomer>(inventoryCustomerList);
		
	}
	
	private void displayInventoryList() {
		productListbox.setModel(inventoryCustomerModelList);
		productListbox.setItemRenderer(getCustomerInventoryListitemRenderer());
	}
	
	private ListitemRenderer<Ent_InventoryCustomer> getCustomerInventoryListitemRenderer() {
		
		return new ListitemRenderer<Ent_InventoryCustomer>() {
			
			@Override
			public void render(Listitem item, Ent_InventoryCustomer invtCust, int index) throws Exception {
				Listcell lc;
				
				// Tgl.Produksi
				lc = new Listcell(dateToStringDisplay(
						invtCust.getEntryDate(), getShortDateFormat(), getLocale()));
				lc.setParent(item);
				
				// Jenis-Coil
				lc = new Listcell(invtCust.getInventoryCode().getProductCode());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Customer
				lc = new Listcell(invtCust.getCustomer().getCompanyType()+" "+
				 		 invtCust.getCustomer().getCompanyLegalName());
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// Spek
				lc = new Listcell(
						toDecimalFormat(new BigDecimal(invtCust.getThickness()), getLocale(), THICKNESS_FORMAT)+" x "+
						toDecimalFormat(new BigDecimal(invtCust.getWidth()), getLocale(), "###.###")+" x "+
						toDecimalFormat(new BigDecimal(invtCust.getLength()), getLocale(), "###.###")								
						);
				lc.setParent(item);
				
				// Packing
				lc = new Listcell(invtCust.getInventoryPacking().toString());
				lc.setParent(item);
				
				// Jmlh/Kg
				lc = new Listcell(
						toDecimalFormat(new BigDecimal(invtCust.getWeightQuantity()), getLocale(), "###.###")
						);
				lc.setParent(item);
				
				// No.Coil
				lc = new Listcell(invtCust.getMarking());
				lc.setParent(item);
				
			}
		};
	}

	public InventoryCustomerDao getInventoryCustomerDao() {
		return inventoryCustomerDao;
	}

	public void setInventoryCustomerDao(InventoryCustomerDao inventoryCustomerDao) {
		this.inventoryCustomerDao = inventoryCustomerDao;
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
