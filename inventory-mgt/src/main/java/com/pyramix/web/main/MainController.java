package com.pyramix.web.main;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Include;

import com.pyramix.web.common.GFCBaseController;
import com.pyramix.web.common.SettingsUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4175893358651137021L;

	private SettingsUtil settingsUtility;
	
	private Include mainInclude;
	
	public void onCreate$mainWindow(Event event) throws Exception {
		log.info("mainWindow created");
		
		log.info("Version: "+getSettingsUtility().getWebAppProperties("build.version"));
		log.info("Build No: "+getSettingsUtility().getWebAppProperties("build.timestamp"));
		log.info("Name: "+getSettingsUtility().getWebAppProperties("build.name"));
		
		mainInclude.setSrc("~./src/info_company.zul");
		// mainInclude.setSrc("~./src/info_penerimaancoil.zul");
		// mainInclude.setSrc("~./src/info_customer.zul");
		// mainInclude.setSrc("~./src/info_inventory_type.zul");
		// mainInclude.setSrc("~./src/info_penerimaancoil.zul");
	}
	
	public void onPenerimaanCoilMenuClick(Event event) {
		log.info("onPenerimaanCoilMenuClick...");
		
		mainInclude.setSrc("~./src/info_penerimaancoil.zul");
	}
	
	public void onProsesCoilMenuClick(Event event) {
		log.info("onProsesCoilMenuClick...");
		
		mainInclude.setSrc("~./src/info_processcoil.zul");		
	}
	
	public void onProdukMenuClick(Event event) {
		log.info("onProdukMenuClick...");
		
		mainInclude.setSrc("~./src/info_produk.zul");		
	}
	
	public void onSuratjalanCoilMenuClick(Event event) {
		log.info("onSuratjalanCoilMenuClick...");
		
		mainInclude.setSrc("~./src/info_suratjalan.zul");		
	}
	
	public void onTagihanCoilMenuClick(Event event) {
		log.info("onTagihanCoilMenuClick...");
		
		mainInclude.setSrc("~./src/info_tagihan.zul");		
	}
	
	public void onCustomerCoilMenuClick(Event event) {
		log.info("onCustomerCoilMenuClick...");
		
		mainInclude.setSrc("~./src/info_customer.zul");		
	}

	public void onClickInventoryTypeMenu(Event event) {
		log.info("inventoryTypeMenuitem click...");
		
		mainInclude.setSrc("~./src/info_inventory_type.zul");		
	}
	
	public void onClickInventoryCodeMenu(Event event) {
		log.info("inventoryCodeMenuitem click...");
		
		mainInclude.setSrc("~./src/info_inventory_code.zul");		
	}	
	
	public void onClickCompanyMenu(Event event) {
		log.info("companyMenuitem click...");
		
		mainInclude.setSrc("~./src/info_company.zul");				
	}
	
	public SettingsUtil getSettingsUtility() {
		return settingsUtility;
	}

	public void setSettingsUtility(SettingsUtil settingsUtility) {
		this.settingsUtility = settingsUtility;
	}
}
