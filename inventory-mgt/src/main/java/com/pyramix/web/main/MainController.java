package com.pyramix.web.main;

import java.util.TimeZone;

import org.zkoss.util.Locales;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
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
		
		TimeZone.setDefault(TimeZone.getTimeZone(getZoneId()));

		log.info("Version	: {}", getSettingsUtility().getWebAppProperties("build.version"));
		log.info("Build No	: {}", getSettingsUtility().getWebAppProperties("build.timestamp"));
		log.info("Build Name	: {}", getSettingsUtility().getWebAppProperties("build.name"));
		
		// log.info("Locale: "+getLocale().toString());
		// log.info("Language: "+getLocale().getLanguage());
		// log.info("Region(Country): "+getLocale().getCountry());
		// log.info("Display Name: "+getLocale().getDisplayName());
		// log.info("Zone Id: "+getZoneId().toString());
		
		log.info("Locale		: {}", Locales.getCurrent());
		log.info("Language	: {}", Labels.getLabel("language"));
		log.info("Timezone	: {}", TimeZone.getDefault().getID());
		log.info("DateTime	: {}", getLocalDateTime(getZoneId()));
		
		// log.info("Pref Locale: "+Library.getProperty(Attributes.PREFERRED_LOCALE));

		mainInclude.setSrc("~./src/info_panel_main.zul");

		// mainInclude.setSrc("~./src/info_dashboard.zul");
		// mainInclude.setSrc("~./src/info_tagihan.zul");
		// mainInclude.setSrc("~./src/info_suratjalan.zul");
		// mainInclude.setSrc("~./src/info_produk.zul");
		// mainInclude.setSrc("~./src/info_inventory_table.zul");
		// mainInclude.setSrc("~./src/info_processcoil.zul");
		// mainInclude.setSrc("~./src/info_company.zul");
		// mainInclude.setSrc("~./src/info_penerimaancoil.zul");
		// mainInclude.setSrc("~./src/info_customer.zul");
		// mainInclude.setSrc("~./src/info_inventory_type.zul");
		// mainInclude.setSrc("~./src/info_penerimaancoil.zul");
	}
	
	public void onPenerimaanCoilMenuClick(Event event) {
		log.info("onPenerimaanCoilMenuClick...");
		// change page title
		Executions.getCurrent().getDesktop().getFirstPage().setTitle("Inventory-Penerimaan");
		// load page
		mainInclude.setSrc("~./src/info_penerimaancoil.zul");
	}
	
	public void onProsesCoilMenuClick(Event event) {
		log.info("onProsesCoilMenuClick...");
		// change page title
		Executions.getCurrent().getDesktop().getFirstPage().setTitle("Inventory-Proses");
		// load page		
		mainInclude.setSrc("~./src/info_processcoil.zul");		
	}
	
	public void onProdukMenuClick(Event event) {
		log.info("onProdukMenuClick...");
		// change page title
		Executions.getCurrent().getDesktop().getFirstPage().setTitle("Inventory-Produk");
		// load page		
		mainInclude.setSrc("~./src/info_produk.zul");		
	}
	
	public void onSuratjalanCoilMenuClick(Event event) {
		log.info("onSuratjalanCoilMenuClick...");
		// change page title
		Executions.getCurrent().getDesktop().getFirstPage().setTitle("Inventory-SuratJalan");
		// load page		
		mainInclude.setSrc("~./src/info_suratjalan.zul");		
	}
	
	public void onTagihanCoilMenuClick(Event event) {
		log.info("onTagihanCoilMenuClick...");
		// change page title
		Executions.getCurrent().getDesktop().getFirstPage().setTitle("Inventory-Tagihan");
		// load page		
		mainInclude.setSrc("~./src/info_tagihan.zul");		
	}
	
	public void onCustomerCoilMenuClick(Event event) {
		log.info("onCustomerCoilMenuClick...");
		// change page title
		Executions.getCurrent().getDesktop().getFirstPage().setTitle("Inventory-Customer");
		// load page
		mainInclude.setSrc("~./src/info_customer.zul");		
	}

	public void onClickInventoryTypeMenu(Event event) {
		log.info("inventoryTypeMenuitem click...");
		// change page title
		Executions.getCurrent().getDesktop().getFirstPage().setTitle("Inventory-Tipe & Kode");
		// load page		
		mainInclude.setSrc("~./src/info_inventory_type.zul");		
	}
	
	public void onClickInventoryCodeMenu(Event event) {
		log.info("inventoryCodeMenuitem click...");
		// change page title
		Executions.getCurrent().getDesktop().getFirstPage().setTitle("Inventory-Tabel");
		// load page
		mainInclude.setSrc("~./src/info_inventory_table.zul");		
	}	
	
	public void onClickCompanyMenu(Event event) {
		log.info("companyMenuitem click...");
		// change page title
		Executions.getCurrent().getDesktop().getFirstPage().setTitle("Inventory-Company");
		// load page
		mainInclude.setSrc("~./src/info_company.zul");				
	}
	
	public void onClickInfoPanelMenu(Event event) {
		log.info("onClickInfoPanelMenu click...");
		// change page title
		Executions.getCurrent().getDesktop().getFirstPage().setTitle("Inventory-Info");
		// load page
		mainInclude.setSrc("~./src/info_panel_main.zul");						
	}
	
	public SettingsUtil getSettingsUtility() {
		return settingsUtility;
	}

	public void setSettingsUtility(SettingsUtil settingsUtility) {
		this.settingsUtility = settingsUtility;
	}
}
