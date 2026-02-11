package com.pyramix.web.company;

import java.util.List;

import org.zkoss.zk.ui.event.Event;
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
import com.pyramix.domain.entity.Ent_Company;
import com.pyramix.persistence.company.dao.CompanyDao;
import com.pyramix.web.common.GFCBaseController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompanyController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5334342363113571730L;
	
	private CompanyDao companyDao;
	
	private Listbox companyListbox;
	private Combobox companyTypeCombobox;
	private Label companyNameLabel, displayNameLabel, contactNumberLabel, emailLabel,
		address01Label, address02Label;
	private Textbox companyNameTextbox, displayNameTextbox, contactNumberTextbox,
		emailTextbox, address01Textbox, address02Textbox;
	private Checkbox hoqCheckbox, prcCheckbox;
	private Button companyEditButton, companySaveButton;
	
	private ListModelList<Ent_Company> companyModelList = null;
	
	private Ent_Company selCompany = null;
	
	public void onCreate$infoCompanyPanel(Event event) throws Exception {
		log.info("infoCompanyPanel created");
		
		// load companyTypes
		loadCompanyTypes();

		// load company
		loadCompanyList();
		
		// render company
		renderCompanyList();
		
		// select 1s company
		if (!companyModelList.isEmpty()) {
			selCompany =
					companyModelList.get(0);
			// display
			displayCompanyInfo();
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

	private void loadCompanyList() throws Exception {
		List<Ent_Company> companyList = getCompanyDao().findAllCompany();
		
		companyModelList =
				new ListModelList<Ent_Company>(companyList);
	}

	private void renderCompanyList() {
		companyListbox.setModel(companyModelList);
		companyListbox.setItemRenderer(getCompanyListitemRenderer());
	}
	
	private ListitemRenderer<Ent_Company> getCompanyListitemRenderer() {
		
		return new ListitemRenderer<Ent_Company>() {
			
			@Override
			public void render(Listitem item, Ent_Company comp, int index) throws Exception {
				Listcell lc;
				
				// nama perusahaan
				lc = new Listcell(
						comp.getCompanyType().toString()+"."+
						comp.getCompanyLegalName()+" ["+
						comp.getCompanyDisplayName()+"]");
				lc.setParent(item);
				
				// alamat
				lc = new Listcell(
						comp.getAddress01());
				lc.setStyle("white-space:nowrap");
				lc.setParent(item);

				item.setValue(comp);
			}
		};
	}

	public void onSelect$companyListbox(Event event) throws Exception {
		selCompany = companyListbox.getSelectedItem().getValue();
		
		displayCompanyInfo();
	}
	
	private void displayCompanyInfo() {
		companyNameLabel.setValue(selCompany.getCompanyType().toString()+"."+
				selCompany.getCompanyLegalName());
		displayNameLabel.setValue(selCompany.getCompanyDisplayName());
		contactNumberLabel.setValue(selCompany.getPhone());
		emailLabel.setValue(selCompany.getEmail());
		address01Label.setValue(selCompany.getAddress01());
		address02Label.setValue(selCompany.getAddress02());
		hoqCheckbox.setChecked(selCompany.isHoq());
		prcCheckbox.setChecked(selCompany.isProc());
	}

	public void onClick$addCompanyButton(Event event) throws Exception {
		log.info("addCompanyButton click");
		
		// create a new company
		selCompany = new Ent_Company();
		// set in progress
		selCompany.setAddInProgress(true);
		
		// allow user to enter new info
		setToAllowEditInfo();
		// select 1st company type
		companyTypeCombobox.setSelectedIndex(0);
	}
	
	private void setToAllowEditInfo() {
		companyTypeCombobox.setVisible(true);
		if (selCompany.isAddInProgress()) {
			companyTypeCombobox.setSelectedIndex(0);
		} else for (Comboitem comboitem : companyTypeCombobox.getItems()) {
			Enm_TypeCompany compType = comboitem.getValue();
			if (compType.equals(selCompany.getCompanyType())) {
				companyTypeCombobox.setSelectedItem(comboitem);
				break;
			}
		}
		
		companyNameLabel.setVisible(false);
		companyNameTextbox.setVisible(true);
		companyNameTextbox.setValue(selCompany.isAddInProgress() ?
				" " : selCompany.getCompanyLegalName());
		
		displayNameLabel.setVisible(false);
		displayNameTextbox.setVisible(true);
		displayNameTextbox.setClass("inputTextUpperCase");
		displayNameTextbox.setValue(selCompany.isAddInProgress() ?
				" " : selCompany.getCompanyDisplayName());
		
		contactNumberLabel.setVisible(false);
		contactNumberTextbox.setVisible(true);
		contactNumberTextbox.setValue(selCompany.isAddInProgress() ?
				" " : selCompany.getPhone());
		
		emailLabel.setVisible(false);
		emailTextbox.setVisible(true);
		emailTextbox.setValue(selCompany.isAddInProgress() ?
				" " : selCompany.getEmail());
		
		address01Label.setVisible(false);
		address01Textbox.setVisible(true);
		address01Textbox.setValue(selCompany.isAddInProgress() ?
				" " : selCompany.getAddress01());

		address02Label.setVisible(false);
		address02Textbox.setVisible(true);
		address02Textbox.setValue(selCompany.isAddInProgress() ?
				" " : selCompany.getAddress02());

		// hide edit button
		companyEditButton.setVisible(false);
		// show save button
		companySaveButton.setVisible(true);
	}
	
	public void onCheck$hoqCheckbox(Event event) throws Exception {
		log.info("hoqCheckbox click");
		selCompany.setHoq(hoqCheckbox.isChecked());
		getCompanyDao().update(selCompany);
	}
	
	public void onCheck$prcCheckbox(Event event) throws Exception {
		log.info("prcCheckbox click");
		selCompany.setProc(prcCheckbox.isChecked());
		getCompanyDao().update(selCompany);
	}
	
	public void onClick$companySaveButton(Event event) throws Exception {
		log.info("companySaveButton click");
		
		// display company info
		setToDisplayInfo();
		
		// update
		Ent_Company activeComp =
				getCompanyDao().update(getUpdatedCompany());
		
		// re-load
		loadCompanyList();
		
		// re-render
		renderCompanyList();
		
		// locate
		locateCompanyData(activeComp);
		
		selCompany = activeComp;
		selCompany.setAddInProgress(false);
		
		// display
		displayCompanyInfo();
	}

	private void setToDisplayInfo() {
		companyTypeCombobox.setVisible(false);
		
		companyNameTextbox.setVisible(false);
		companyNameLabel.setVisible(true);
		
		displayNameTextbox.setVisible(false);
		displayNameLabel.setVisible(true);
		
		contactNumberTextbox.setVisible(false);
		contactNumberLabel.setVisible(true);
		
		emailTextbox.setVisible(false);
		emailLabel.setVisible(true);
		
		address01Textbox.setVisible(false);
		address01Label.setVisible(true);

		address02Textbox.setVisible(false);
		address02Label.setVisible(true);
		
		// show edit button -- so that company info can be edited
		companyEditButton.setVisible(true);
		// hide save button
		companySaveButton.setVisible(false);
	}

	private Ent_Company getUpdatedCompany() {
		selCompany.setCompanyType(companyTypeCombobox.getSelectedItem().getValue());
		selCompany.setCompanyLegalName(companyNameTextbox.getValue());
		selCompany.setCompanyDisplayName(displayNameTextbox.getValue());
		selCompany.setPhone(contactNumberTextbox.getValue());
		selCompany.setEmail(emailTextbox.getValue());
		selCompany.setAddress01(address01Textbox.getValue());
		selCompany.setAddress02(address02Textbox.getValue());
		selCompany.setHoq(hoqCheckbox.isChecked());
		selCompany.setProc(prcCheckbox.isChecked());
		
		return selCompany;
	}

	private void locateCompanyData(Ent_Company activeComp) {
		companyListbox.renderAll();
		for (Listitem item : companyListbox.getItems()) {
			Ent_Company comp = item.getValue();
			if (activeComp.equals(comp)) {
				companyListbox.setSelectedItem(item);
				break;
			}
		}
	}	
	
	public void onClick$companyEditButton(Event event) {
		log.info("companyEditButton click");
		
		// allow user to edit info
		setToAllowEditInfo();
	}
	
	public CompanyDao getCompanyDao() {
		return companyDao;
	}

	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}

}
