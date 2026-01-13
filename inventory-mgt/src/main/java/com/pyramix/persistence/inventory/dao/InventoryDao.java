package com.pyramix.persistence.inventory.dao;

import java.util.List;

import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.domain.entity.Ent_Inventory;
import com.pyramix.domain.entity.Ent_InventoryCode;

public interface InventoryDao {

	public Ent_Inventory findInventoryById(long id) throws Exception;
	
	public List<Ent_Inventory> findAllInventory() throws Exception;
	
	public Ent_Inventory update(Ent_Inventory ent_Inventory) throws Exception;
	
	public void save(Ent_Inventory ent_Inventory) throws Exception;
	
	public void delete(Ent_Inventory ent_Inventory) throws Exception;

	/**
	 * @param selCustomer
	 * @return {@link List} of {@link Ent_Inventory}
	 * @throws Exception
	 */
	public List<Ent_Inventory> findInventoryByCustomer(Ent_Customer customer) throws Exception;

	public List<Ent_Inventory> findInventoryByInventoryCode(Ent_InventoryCode inventoryCode) throws Exception;

	public List<Ent_Inventory> findInventoryByCustomer_InventoryCode(Ent_Customer customer, 
			Ent_InventoryCode inventoryCode) throws Exception;
	
}
