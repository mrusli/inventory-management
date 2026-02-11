package com.pyramix.web.main.panel;

import java.util.TimeZone;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;

import com.pyramix.web.common.GFCBaseController;
import com.pyramix.web.common.SettingsUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InfoMainPanelController extends GFCBaseController {
	
	private SettingsUtil settingsUtility;
	
	private Label versionLabel, buildLabel, nameLabel,
		localeLabel, languageLabel, timezoneLabel;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2456406660336047332L;

	public void onCreate$infoMainPanel(Event event) throws Exception {
		log.info("infoMainPanel created");
		
		String version = getSettingsUtility().getWebAppProperties("build.version");
		String build = getSettingsUtility().getWebAppProperties("build.timestamp");
		String name = getSettingsUtility().getWebAppProperties("build.name");
		
		// log.info("Version		: {}", version);
		// log.info("Build No	: {}", build);
		// log.info("Build Name	: {}", name);
		
		versionLabel.setValue(version);
		buildLabel.setValue(build);
		nameLabel.setValue(name);
		
//		log.info("Locale		: {}", Locales.getCurrent());
//		log.info("Language	: {}", Labels.getLabel("language"));
//		log.info("Timezone	: {}", TimeZone.getDefault().getID());
//		log.info("DateTime	: {}", getLocalDateTime(getZoneId()));
		
		localeLabel.setValue(getLocale().toString());
		languageLabel.setValue(Labels.getLabel("language"));
		timezoneLabel.setValue(getTimezone().getDisplayName()+" ("+
				TimeZone.getDefault().getID()+")");		
	}

	public SettingsUtil getSettingsUtility() {
		return settingsUtility;
	}

	public void setSettingsUtility(SettingsUtil settingsUtility) {
		this.settingsUtility = settingsUtility;
	}
}
