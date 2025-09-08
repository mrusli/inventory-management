package com.pyramix.persistence.inventory.dao;

import java.util.List;

import com.pyramix.domain.entity.Ent_Inventory;

public interface InventoryDao {

	public Ent_Inventory findInventoryById(long id) throws Exception;
	
	public List<Ent_Inventory> findAllInventory() throws Exception;
	
	public Ent_Inventory update(Ent_Inventory ent_Inventory) throws Exception;
	
	public void save(Ent_Inventory ent_Inventory) throws Exception;
	
	public void delete(Ent_Inventory ent_Inventory) throws Exception;
	
}
