package com.pyramix.web.customer;

import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

import com.pyramix.domain.entity.Enm_TypeCompany;
import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.persistence.customer.dao.CustomerDao;
import com.pyramix.web.common.GFCBaseController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4508026704423696235L;

	private CustomerDao customerDao;
	
	private Label customerNameLabel, contactNameLabel, contactNumberLabel,
		address01Label, address02Label, emailLabel;
	private Textbox customerNameTextbox, contactNameTextbox, contactNumberTextbox,
		address01Textbox, address02Textbox, emailTextbox;
	private Combobox companyTypeCombobox;
	private Listbox customerListbox;
	private Button customerEditButton, customerSaveButton, customerCancelButton;
	private Checkbox statusCheckbox;
	
	private ListModelList<Ent_Customer> customerModelList = null;
	private Ent_Customer selCustomer = null;
	
	public void onCreate$infoCustomerPanel(Event event) throws Exception {
		log.info("infoCustomerPanel created");
		
		// load companyTypes
		loadCompanyTypes();
		
		// load customer list
		loadCustomerList();
		
		// display
		displayCustomerList();
		
		// select 1st customer
		if (!customerModelList.isEmpty()) {
			selCustomer =
					customerModelList.get(0);
			// display
			displayCustomerInfo();
		}
	}

	private void loadCompanyTypes() {
		Comboitem comboitem;
		
		for (Enm_TypeCompany compType : Enm_TypeCompany.values()) {
			comboitem = new Comboitem();
			comboitem.setLabel(compType.toString());
			comboitem.setValue(compType);
			comboitem.setParent(companyTypeCombobox);
		}
	}

	private void loadCustomerList() throws Exception {
		List<Ent_Customer> customerList = getCustomerDao().findAllCustomerSorted();
		
		customerModelList = 
				new ListModelList<Ent_Customer>(customerList);
	}
	
	private void displayCustomerList() throws Exception {
		customerListbox.setModel(customerModelList);
		customerListbox.setItemRenderer(getCustomerListitemRenderer());
	}

	private ListitemRenderer<Ent_Customer> getCustomerListitemRenderer() {
		
		return new ListitemRenderer<Ent_Customer>() {
			
			@Override
			public void render(Listitem item, Ent_Customer cust, int index) throws Exception {
				Listcell lc;
				
				// nama customer
				String namaCustomer = cust.getCompanyType().toString()+" "+
						cust.getCompanyLegalName();
				lc = new Listcell(namaCustomer);
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);
				
				// contact
				String contact = cust.getContactPerson()+" "+
						cust.getPhone();
				lc = new Listcell(contact);
				lc.setStyle("white-space: nowrap;");
				lc.setParent(item);

				// status
				lc = new Listcell();
				lc.setIconSclass(cust.isActive() ? "z-icon-circle-check"
						: "z-icon-circle-xmark");
				lc.setParent(item);
				
				item.setValue(cust);
			}
		};
	}
	
	public void onSelect$customerListbox(Event event) throws Exception {
		selCustomer = customerListbox.getSelectedItem().getValue();
		
		displayCustomerInfo();
	}

	private void displayCustomerInfo() {
		customerNameLabel.setValue(selCustomer.getCompanyType().toString()+" "+
				selCustomer.getCompanyLegalName());
		contactNameLabel.setValue(selCustomer.getContactPerson());
		contactNumberLabel.setValue(selCustomer.getPhone());
		emailLabel.setValue(selCustomer.getEmail());
		statusCheckbox.setChecked(selCustomer.isActive());
		statusCheckbox.setLabel(selCustomer.isActive() ? "Aktif" : "Non-Aktif");
		address01Label.setValue(selCustomer.getAddress01());
		address02Label.setValue(selCustomer.getAddress02());
	}
	
	public void onClick$customerAddButton(Event event) throws Exception {
		log.info("customerAddButton click");
				
		// create a new company
		selCustomer = new Ent_Customer();
		log.info("New Customer Id: {}",String.valueOf(selCustomer.getId()));
		// add in progress
		selCustomer.setAddInProgress(true);

		// allow user to enter new info
		setToAllowEditInfo();
		// select 1st company type
		companyTypeCombobox.setSelectedIndex(0);

	}

	private void setToAllowEditInfo() {
		companyTypeCombobox.setVisible(true);		
		if (selCustomer.isAddInProgress()) {
			companyTypeCombobox.setSelectedIndex(0);
		} else for (Comboitem comboitem : companyTypeCombobox.getItems()) {
			Enm_TypeCompany compType = comboitem.getValue();
			if (compType.equals(selCustomer.getCompanyType())) {
				companyTypeCombobox.setSelectedItem(comboitem);
				break;
			}
		}
				
		customerNameLabel.setVisible(false);
		customerNameTextbox.setVisible(true);
		customerNameTextbox.setValue(selCustomer.isAddInProgress() ? 
				"" : selCustomer.getCompanyLegalName());
		
		contactNameLabel.setVisible(false);
		contactNameTextbox.setVisible(true);
		contactNameTextbox.setValue(selCustomer.isAddInProgress() ?
				"" : selCustomer.getContactPerson());
		
		contactNumberLabel.setVisible(false);
		contactNumberTextbox.setVisible(true);
		contactNumberTextbox.setValue(selCustomer.isAddInProgress() ?
				"" : selCustomer.getPhone());
		
		emailLabel.setVisible(false);
		emailTextbox.setVisible(true);
		emailTextbox.setValue(selCustomer.isAddInProgress() ? 
				"" : selCustomer.getEmail());
		
		statusCheckbox.setChecked(false);
		statusCheckbox.setLabel("Non-Aktif");
		
		address01Label.setVisible(false);
		address01Textbox.setVisible(true);
		address01Textbox.setValue(selCustomer.isAddInProgress() ?
				"" : selCustomer.getAddress01());

		address02Label.setVisible(false);
		address02Textbox.setVisible(true);
		address02Textbox.setValue(selCustomer.isAddInProgress() ?
				"" : selCustomer.getAddress02());

		// hide edit button
		customerEditButton.setVisible(false);
		// show save button
		customerSaveButton.setVisible(true);
		// show cancel button
		customerCancelButton.setVisible(true);
	}
	
	public void onCheck$statusCheckbox(Event event) throws Exception {
		log.info("statusCheckbox click");
		
		statusCheckbox.setLabel(statusCheckbox.isChecked() ? "Aktif" : "Non-Aktif");
		
		if (selCustomer.isAddInProgress()) {
			log.info("No SAVE / UPDATE require.  Will let the user click save.");
		} else {
			log.info("Update rite away");
			// set active / non-active
			selCustomer.setActive(statusCheckbox.isChecked());
			// update
			Ent_Customer customer = getCustomerDao().update(selCustomer);
			Clients.showNotification((customer.isActive() ?
					   "Status Customer sudah diaktifkan." : "Status Customer sudah di non-aktifkan"),
							"info", null, "bottom_left", 10000);
			// re-load
			loadCustomerList();
			
			// re-display
			displayCustomerList();

			// locate
			locateCustomerData(customer);
			
			// display
			displayCustomerInfo();
		}
	}
	
	public void onClick$customerSaveButton(Event event) throws Exception {
		log.info("customerSaveButton click");
		
		// display customer info
		setToDisplayInfo();
		
		// update
		Ent_Customer activeCust = 
				getCustomerDao().update(getUpdatedCustomer());
		Clients.showNotification((selCustomer.getId()==0 ?
				   "Penambahan Customer sudah disimpan." : "Perubahan Customer sudah disimpan"),
						"info", null, "bottom_left", 10000);

		// re-load
		loadCustomerList();
		
		// re-display
		displayCustomerList();
		
		// locate
		locateCustomerData(activeCust);
		
		selCustomer = activeCust;
		selCustomer.setAddInProgress(false);
		
		// display
		displayCustomerInfo();
	}
	
	public void onClick$customerCancelButton(Event event) throws Exception {
		log.info("customerCancelButton click");
		
		// display customer info
		setToDisplayInfo(); 
		
		// re-load
		loadCustomerList();
		
		// re-display
		displayCustomerList();
		
		if (selCustomer.getId()==0) {
			if (!customerModelList.isEmpty()) {
				selCustomer =
						customerModelList.get(0);
				// display
				displayCustomerInfo();
			}			
		} else {
			// locate
			locateCustomerData(selCustomer);
			
			// not edit / create anymore
			selCustomer.setAddInProgress(false);
			
			// display
			displayCustomerInfo();
		}
	}

	private void setToDisplayInfo() {
		companyTypeCombobox.setVisible(false);
		
		customerNameTextbox.setVisible(false);
		customerNameLabel.setVisible(true);
		
		contactNameTextbox.setVisible(false);
		contactNameLabel.setVisible(true);
		
		contactNumberTextbox.setVisible(false);
		contactNumberLabel.setVisible(true);
		
		emailTextbox.setVisible(false);
		emailLabel.setVisible(true);
		
		address01Textbox.setVisible(false);
		address01Label.setVisible(true);

		address02Textbox.setVisible(false);
		address02Label.setVisible(true);
		
		// show edit button -- so that customer info can be edited
		customerEditButton.setVisible(true);
		// hide save button
		customerSaveButton.setVisible(false);
		// hide cancel button
		customerCancelButton.setVisible(false);
	}

	public void onClick$customerEditButton(Event event) throws Exception {
		log.info("customerEditButton click");
		
		// allow user to edit info
		setToAllowEditInfo();		
	}

	private Ent_Customer getUpdatedCustomer() {
		selCustomer.setCompanyType(companyTypeCombobox.getSelectedItem().getValue());
		selCustomer.setCompanyLegalName(customerNameTextbox.getValue());
		selCustomer.setContactPerson(contactNameTextbox.getValue());
		selCustomer.setPhone(contactNumberTextbox.getValue());
		selCustomer.setEmail(emailTextbox.getValue());
		selCustomer.setActive(statusCheckbox.isChecked());
		selCustomer.setAddress01(address01Textbox.getValue());
		selCustomer.setAddress02(address02Textbox.getValue());
		
		return selCustomer;
	}	
	
	private void locateCustomerData(Ent_Customer activeCust) {
		customerListbox.renderAll();
		for (Listitem item : customerListbox.getItems()) {
			Ent_Customer cust = item.getValue();
			if (activeCust.getId()==cust.getId()) {
				customerListbox.setSelectedItem(item);
				break;
			}
		}
		
	}	
	
	public CustomerDao getCustomerDao() {
		return customerDao;
	}

	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}
	
}
