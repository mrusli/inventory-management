package com.pyramix.web.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SettingsUtil {

	private final String extFile = "/pyramix/settings_attendance_config.properties";

	private Properties props = new Properties();
	
	public SettingsUtil() {
		super();
		log.info("init settings utility");
	}

	public void setPropertyToExtFile(String key, String value) {
		props.setProperty(key, value);
		
		try (OutputStream output = new FileOutputStream(extFile)) {
			props.store(output, "application settings");
			log.info("saved to external file");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getPropertyFromExtFile(String key) {
		try (FileInputStream input = new FileInputStream(extFile)) {
		
			return props.getProperty(key);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "Error: Non Existing Key";
	}
	
	
}
