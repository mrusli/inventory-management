package com.pyramix.web.process;

import java.time.LocalDate;

import com.pyramix.domain.entity.Ent_InventoryProcess;

public record ProcessCoilReportData(
		LocalDate reportDate,
		Ent_InventoryProcess inventoryProcess) {

}
