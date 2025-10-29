package com.pyramix.web.product;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

import com.pyramix.domain.entity.Enm_StatusProcess;
import com.pyramix.domain.entity.Ent_Company;
import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.domain.entity.Ent_InventoryProcess;
import com.pyramix.domain.entity.Ent_InventoryProcessMaterial;
import com.pyramix.domain.entity.Ent_InventoryProcessProduct;
import com.pyramix.persistence.company.dao.CompanyDao;
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
	private CompanyDao companyDao;
	
	private Combobox customerProcessCombobox, processCombobox, processTypeCombobox;
	private Listbox materialListbox, productListbox;
	private Label materialLabel;
	private Checkbox processCompletedCheckbox;
	private Button saveProcButton;
	
	private ListModelList<Ent_InventoryProcessProduct> productModelList;
	private Ent_InventoryProcess selInvtProc = null;
	private Ent_InventoryProcessMaterial selMaterial = null;
	private Ent_Company defaultCompany = null;
	private Ent_Customer selCustomer;
	
	private static final Long DEF_COMPANY_IDX = (long) 3;
	
	public void onCreate$infoProductCoilPanel(Event event) throws Exception {
		log.info("infoProductCoilPanel created");
		
		// set default company
		defaultCompany = getCompanyDao().findCompanyById(DEF_COMPANY_IDX);
		
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
		selCustomer = customerProcessCombobox.getSelectedItem().getValue();
		// list the processes status PROCESS
		List<Ent_InventoryProcess> processList =
				getInventoryProcessDao().findInventoryByCustomerByStatus(
						selCustomer, Enm_StatusProcess.Proses);
		// clear before load
		processCombobox.getItems().clear();
		processCombobox.setValue("");
		// load
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
		log.info("processCombobox select");
		onSelectProcessCombobox();
	}

	private void onSelectProcessCombobox() throws Exception {
		selInvtProc =
				processCombobox.getSelectedItem().getValue();

// 		is process completed?
//		if (selInvtProc.getCompletedDate() != null) {
//			processCompletedCheckbox.setChecked(true);
//			processCompletedCheckbox.setLabel(dateToStringDisplay(
//					selInvtProc.getCompletedDate(), getShortDateFormat(), getLocale()));
//		} else {
//			processCompletedCheckbox.setChecked(false);
//			processCompletedCheckbox.setLabel("");
//		}

		// proxy
		selInvtProc = getInventoryProcessDao()
				.findInventoryProcessMaterialsByProxy(selInvtProc.getId());
		// display the materials from this process
		renderProcessMaterials(selInvtProc.getProcessMaterials());		
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
				
				item.setValue(material);
			}
		};
	}
	
	public void onAfterRender$materialListbox(Event event) throws Exception {
		if (!materialListbox.getItems().isEmpty()) {
			// set selected material
			Listitem item = materialListbox.getItemAtIndex(0);
			selMaterial = item.getValue();
			log.info("selected: "+selMaterial.toString());
		
			processTypeCombobox.setValue(selMaterial.getInventoryProcess().getProcessType().toString());
			materialLabel.setValue(
					selMaterial.getMarking()+" "+
							selMaterial.getInventoryCode().getProductCode()+" "+
							toDecimalFormat(new BigDecimal(selMaterial.getWeightQuantity()), getLocale(), getDecimalFormat())+" Kg."
					);
			List<Ent_InventoryProcessProduct> productList =
					onSelectMaterialListbox(selMaterial);
			
			renderProcessProducts(productList);
		}
	}

	public void onSelect$materialListbox(Event event) throws Exception {
		selMaterial =
				materialListbox.getSelectedItem().getValue();
		log.info("selected: "+selMaterial.toString());
				
		List<Ent_InventoryProcessProduct> productList =
				onSelectMaterialListbox(selMaterial);
		
		renderProcessProducts(productList);
	}

	private List<Ent_InventoryProcessProduct> onSelectMaterialListbox(
			Ent_InventoryProcessMaterial material) throws Exception {
		// by proxy
		Ent_InventoryProcessMaterial selMaterial = getInventoryProcessDao()
				.findInventoryProcessProductsByProxy(material.getId());
		// products
		log.info("products: "+selMaterial.getProcessProducts().toString());
		
		return selMaterial.getProcessProducts();
	}

	private void renderProcessProducts(List<Ent_InventoryProcessProduct> productList) {
		productModelList = 
				new ListModelList<Ent_InventoryProcessProduct>(productList);
		
		productListbox.setModel(productModelList);
		productListbox.setItemRenderer(getProcessProductListitemRenderer());
	}	
	
	private ListitemRenderer<Ent_InventoryProcessProduct> getProcessProductListitemRenderer() {
		
		return new ListitemRenderer<Ent_InventoryProcessProduct>() {
			
			@Override
			public void render(Listitem item, Ent_InventoryProcessProduct product, int index) throws Exception {
				Listcell lc;
				
				// marking
				lc = new Listcell(product.getMarking());
				lc.setParent(item);
				
				// spek
				lc = new Listcell(
						toDecimalFormat(new BigDecimal(product.getThickness()), getLocale(), "#0,00")+" x "+
						toDecimalFormat(new BigDecimal(product.getWidth()), getLocale(), "###.###")+" x "+
						toDecimalFormat(new BigDecimal(product.getLength()), getLocale(), "###.###"));
				lc.setParent(item);
				
				// qty(kg)
				lc = new Listcell(
						toDecimalFormat(new BigDecimal(product.getWeightQuantity()), getLocale(), "###.###")
						);
				lc.setParent(item);
				
				// qty(lbr)
				lc = new Listcell(getFormatedInteger(product.getSheetQuantity()));
				lc.setParent(item);
				
				// edit / save
				lc = new Listcell();
				lc.setParent(item);
				
				Button button = new Button();
				button.setParent(lc);
				button.setSclass("compButton");
				modifToEdit(button);
				button.addEventListener(Events.ON_CLICK, editProcessProduct(product));
				
				// delete
				lc = new Listcell();
				lc.setParent(item);
				
				button = new Button();
				button.setSclass("compButton");
				button.setIconSclass("z-icon-trash");
				button.setStyle("background-color:var(--bs-danger);");		
				button.setParent(lc);
				button.addEventListener(Events.ON_CLICK, onDeleteProductButtonClick(product));
			}
		};
	}

	protected EventListener<Event> editProcessProduct(Ent_InventoryProcessProduct product) {

		return new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				log.info("editProcessProduct button click");			
				Button button = (Button) event.getTarget();
				// get the current listitem
				Listitem activeItem = (Listitem) event.getTarget().getParent().getParent();
				
				if (product.isEditInProgress()) {
					// to update / save
					log.info("to update or save");
					Ent_InventoryProcessProduct updatedProduct =
							getUpdatedInventoryProcessProduct(product, activeItem);
					log.info(updatedProduct.toString());
					// get products by proxy
					Ent_InventoryProcessMaterial procMaterialByProxy = getInventoryProcessDao()
							.findInventoryProcessProductsByProxy(selMaterial.getId());					
					List<Ent_InventoryProcessProduct> procProducts = procMaterialByProxy.getProcessProducts();
					if (product.isAddInProgress()) {
						// to add into the list
						log.info("to add into the list...");
						// add into the list
						procProducts.add(updatedProduct);
					} else {
						// to update the existing list
						log.info("to update existing list...");
						// find and update the product
						for (Ent_InventoryProcessProduct procProduct : procProducts) {
							if (procProduct.getId()==updatedProduct.getId()) {
								procProduct.setMarking(updatedProduct.getMarking());
								procProduct.setThickness(updatedProduct.getThickness());
								procProduct.setWidth(updatedProduct.getWidth());
								procProduct.setLength(updatedProduct.getLength());
								procProduct.setWeightQuantity(updatedProduct.getWeightQuantity());
								procProduct.setSheetQuantity(updatedProduct.getSheetQuantity());
								procProduct.setProcessMaterial(selMaterial);
								procProduct.setProcessedByCo(defaultCompany);
								procProduct.setCustomer(selCustomer);								
								break;
							}
						}
					}
					// re-assign the products
					selMaterial.setProcessProducts(procProducts);				
					// update
					getInventoryProcessDao().update(selInvtProc);
					// re-load
					List<Ent_InventoryProcessProduct> productList =
							onSelectMaterialListbox(selMaterial);
					// re-render
					renderProcessProducts(productList);					
					// set to false
					product.setEditInProgress(false);
					product.setAddInProgress(false);
					// change to edit
					modifToEdit(button);
				} else {
					// set to edit
					log.info("set to edit");
					
					setProductMarking(activeItem, product.getMarking());
					setProductSpek(activeItem, product.getThickness(), product.getWidth(),
							product.getLength());
					setProductQtyKg(activeItem, product.getWeightQuantity());
					setProductQtyLbr(activeItem, product.getSheetQuantity());
					
					// set to true
					product.setEditInProgress(true);
					// change to save
					modifToSave(button);
				}
			}
		};
	}

	protected EventListener<Event> onDeleteProductButtonClick(Ent_InventoryProcessProduct product) {

		return new EventListener<Event>() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				log.info("onDeleteProductButtonClick...");
				// get products by proxy
				Ent_InventoryProcessMaterial procMaterialByProxy = getInventoryProcessDao()
						.findInventoryProcessProductsByProxy(selMaterial.getId());					
				List<Ent_InventoryProcessProduct> procProducts = procMaterialByProxy.getProcessProducts();
				for (Ent_InventoryProcessProduct procProduct : procProducts) {
					if (procProduct.getId()==product.getId()) {
						procProducts.remove(procProduct);
						break;
					}
				}
				// re-assign the products
				selMaterial.setProcessProducts(procProducts);				
				// update
				getInventoryProcessDao().update(selInvtProc);
				// re-load
				List<Ent_InventoryProcessProduct> productList =
						onSelectMaterialListbox(selMaterial);
				// re-render
				renderProcessProducts(productList);				
			}
		};
	}
	
	
	public void onClick$addProductButton(Event event) throws Exception {
		log.info("addProductButton click");
		
		// add to the last pos
		Ent_InventoryProcessProduct processProduct = addInventoryProcessProductInLastPos();
		processProduct.setInventoryCode(selMaterial.getInventoryCode());
		processProduct.setEditInProgress(true);
		processProduct.setAddInProgress(true);
		
		// last item
		Listitem activeItem = getProductLastItem();
		
		setProductMarking(activeItem, processProduct.getMarking());
		setProductSpek(activeItem, processProduct.getThickness(), processProduct.getWidth(),
				processProduct.getLength());
		setProductQtyKg(activeItem, processProduct.getWeightQuantity());
		setProductQtyLbr(activeItem, processProduct.getSheetQuantity());
		setEditToSaveButton(activeItem);

	}

	private String getProductMarking(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(0);
		
		Textbox textbox = (Textbox) lc.getFirstChild();
		
		return textbox.getValue();
	}	
	
	private void setProductMarking(Listitem activeItem, String marking) {
		Listcell lc = (Listcell) activeItem.getChildren().get(0);
		lc.setLabel("");
		Textbox textbox = new Textbox();
		textbox.setValue(marking);
		textbox.setWidth("100px");
		textbox.setParent(lc);
	}
	
	private double getProductSpek(Listitem activeItem, int idx) {
		Listcell lc = (Listcell) activeItem.getChildren().get(1);
		
		Doublebox doublebox = (Doublebox) lc.getChildren().get(idx);
		
		return doublebox.getValue();
	}	
	
	protected void setProductSpek(Listitem activeItem, double thickness, double width, double length) {
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
	}	
	

	private double getProductQtyKg(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(2);

		Doublebox doublebox = (Doublebox) lc.getFirstChild();
		
		return doublebox.getValue();
	}

	protected void setProductQtyKg(Listitem activeItem, double weightQuantity) {
		Listcell lc = (Listcell) activeItem.getChildren().get(2);
		lc.setLabel("");
		Doublebox doublebox = new Doublebox();
		doublebox.setLocale(getLocale());
		doublebox.setValue(weightQuantity);
		doublebox.setWidth("100px");
		doublebox.setParent(lc);
	}	
	
	private int getProductQtyLbr(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(3);

		Intbox intbox = (Intbox) lc.getFirstChild();
		
		return intbox.getValue();
	}
	
	protected void setProductQtyLbr(Listitem activeItem, int sheetQuantity) {
		Listcell lc = (Listcell) activeItem.getChildren().get(3);
		lc.setLabel("");
		Intbox intbox = new Intbox();
		intbox.setValue(sheetQuantity);
		intbox.setWidth("100px");
		intbox.setParent(lc);		
	}	
	
	private void setEditToSaveButton(Listitem activeItem) {
		Listcell lc = (Listcell) activeItem.getChildren().get(4);
		Button button = (Button) lc.getFirstChild();
		
		// signal it can be used to save the entry
		modifToSave(button);
	}
	
	private Ent_InventoryProcessProduct addInventoryProcessProductInLastPos() {
		Ent_InventoryProcessProduct processProduct = new Ent_InventoryProcessProduct();
		
		int posToAdd =
				productModelList.size();
		productModelList.add(posToAdd, processProduct);
		
		return processProduct;
	}

	private Listitem getProductLastItem() {
		// renderAll
		productListbox.renderAll();
		// get the last item
		int lastItemIdx =
				productListbox.getItemCount();
		
		return productListbox.getItemAtIndex(lastItemIdx-1);
	}	

	protected Ent_InventoryProcessProduct getUpdatedInventoryProcessProduct(
			Ent_InventoryProcessProduct product,
			Listitem activeItem) {
		product.setMarking(getProductMarking(activeItem));
		product.setThickness(getProductSpek(activeItem,0));
		product.setWidth(getProductSpek(activeItem,2));
		product.setLength(getProductSpek(activeItem,4));
		product.setWeightQuantity(getProductQtyKg(activeItem));
		product.setSheetQuantity(getProductQtyLbr(activeItem));
		product.setProcessMaterial(selMaterial);
		product.setProcessedByCo(defaultCompany);
		product.setCustomer(selCustomer);
		
		return product;
	}	

	public void onCheck$processCompletedCheckbox(Event event) throws Exception {
		log.info("processCompletedCheckbox: "+processCompletedCheckbox.isChecked());
		
		// allow user to click save
		saveProcButton.setVisible(processCompletedCheckbox.isChecked());
		
	}
	
	public void onClick$saveProcButton(Event event) throws Exception {
		// set to 'Selesai'
		selInvtProc.setProcessStatus(Enm_StatusProcess.Selesai);
		selInvtProc.setCompletedDate(getLocalDate(getZoneId()));
		// update
		getInventoryProcessDao().update(selInvtProc);
		// reload
		onSelectCustomerProcessCombobox();
		// reset checkbox
		processCompletedCheckbox.setChecked(false);		
	}
	
	protected void modifToSave(Button button) {
		button.setIconSclass("z-icon-floppy-disk");
		button.setStyle("background-color:var(--bs-primary);");
	}

	protected void modifToEdit(Button button) {
		button.setIconSclass("z-icon-pen-to-square");
		button.setStyle("background-color:var(--bs-warning);");			
	}	
	
	public InventoryProcessDao getInventoryProcessDao() {
		return inventoryProcessDao;
	}

	public void setInventoryProcessDao(InventoryProcessDao inventoryProcessDao) {
		this.inventoryProcessDao = inventoryProcessDao;
	}

	public CompanyDao getCompanyDao() {
		return companyDao;
	}

	public void setCompanyDao(CompanyDao companyDao) {
		this.companyDao = companyDao;
	}
}
