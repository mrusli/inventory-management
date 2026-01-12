package com.pyramix.persistence.inventorycode.dao;

import java.util.List;

import com.pyramix.domain.entity.Ent_InventoryCode;

public interface InventoryCodeDao {

	public Ent_InventoryCode findInventoryCodeById(long id) throws Exception;
	
	public List<Ent_InventoryCode> findAllInventoryCode() throws Exception;
	
	public void save(Ent_InventoryCode inventoryCode) throws Exception;
	
	public void update(Ent_InventoryCode inventoryCode) throws Exception;
	
	public void delete(Ent_InventoryCode inventoryCode) throws Exception;

	public List<Ent_InventoryCode> findAllInventoryCodesSorted() throws Exception;
	
}
