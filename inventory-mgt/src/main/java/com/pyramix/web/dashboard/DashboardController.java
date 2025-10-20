package com.pyramix.web.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;

import com.google.gson.Gson;
import com.pyramix.web.common.GFCBaseController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DashboardController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4283465508371094903L;

	public void onCreate$infoDashboardPanel(Event event) throws Exception {
		log.info("infoDashboardPanel created");
		
		List<BrowserMarketshare> marketShareList = new ArrayList<BrowserMarketshare>();
		
		BrowserMarketshare marketShare;
		marketShare = new BrowserMarketshare("Firefox", 45.0);
		marketShareList.add(marketShare);
				
		marketShare = new BrowserMarketshare("Edge", 26.8);
		marketShareList.add(marketShare);
				
		marketShare = new BrowserMarketshare("Chrome", 12.8);
		marketShareList.add(marketShare);
				
		marketShare = new BrowserMarketshare("Safari", 8.5);
		marketShareList.add(marketShare);
				
		marketShare = new BrowserMarketshare("Opera", 6.2);
		marketShareList.add(marketShare);
				
		marketShare = new BrowserMarketshare("Others", 0.7);
		marketShareList.add(marketShare);
		
		String json = new Gson().toJson(marketShareList);
		log.info(json);

		Clients.evalJavaScript("setData('" + json + "')");
	}
	
}
