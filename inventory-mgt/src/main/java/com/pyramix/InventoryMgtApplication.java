package com.pyramix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.zkoss.lang.Library;
import org.zkoss.web.Attributes;

@SpringBootApplication
@ImportResource({
	"classpath*:ApplicationContext-GuiController.xml"
})
public class InventoryMgtApplication {

	public static void main(String[] args) {
		// need to set the libaray to load the zk-label_id_ID.properties file !!!
		// alternatively, place the properties file externally in the file system
		// by specifying in the zk.xml
		Library.setProperty(Attributes.PREFERRED_LOCALE, "id_ID");
		Library.setProperty(Attributes.PREFERRED_TIME_ZONE, "Asia/Jakarta");
		
		SpringApplication.run(InventoryMgtApplication.class, args);
	}

}
