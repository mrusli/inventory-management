package com.pyramix.web.product;

import org.zkoss.zk.ui.event.Event;

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
	
	public void onCreate$infoHasilProduksiPanel(Event event) throws Exception {
		log.info("infoHasilProduksiPanel created");
	}

}
