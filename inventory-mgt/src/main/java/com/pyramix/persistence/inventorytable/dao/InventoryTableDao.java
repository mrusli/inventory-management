package com.pyramix.persistence.inventorytable.dao;

import java.util.List;

import com.pyramix.domain.entity.Ent_InventoryCode;
import com.pyramix.domain.entity.Ent_InventoryTable;

public interface InventoryTableDao {

	public Ent_InventoryTable findInventoryTableById(long id) throws Exception;
	
	public List<Ent_InventoryTable> findAllInventoryTable() throws Exception;
	
	public Ent_InventoryTable update(Ent_InventoryTable inventoryTable) throws Exception;
	
	public void save(Ent_InventoryTable inventoryTable) throws Exception;
	
	public void delete(Ent_InventoryTable inventoryTable) throws Exception;

	public List<Ent_InventoryTable> findInventoryByInventoryCode(Ent_InventoryCode inventoryCode) throws Exception;
	
}
