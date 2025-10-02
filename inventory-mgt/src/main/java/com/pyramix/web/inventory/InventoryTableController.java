package com.pyramix.web.inventory;

import org.zkoss.zk.ui.event.Event;

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
	
	public void onCreate$inventoryCodePanel(Event event) throws Exception {
		log.info("inventoryCodePanel created");
	}

	public InventoryTypeDao getInventoryTypeDao() {
		return inventoryTypeDao;
	}

	public void setInventoryTypeDao(InventoryTypeDao inventoryTypeDao) {
		this.inventoryTypeDao = inventoryTypeDao;
	}

}
