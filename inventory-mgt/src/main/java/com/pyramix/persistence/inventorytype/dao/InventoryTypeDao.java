package com.pyramix.persistence.inventorytype.dao;

import java.util.List;

import com.pyramix.domain.entity.Ent_InventoryType;

public interface InventoryTypeDao {
	
	public Ent_InventoryType findInventoryTypeById(long id) throws Exception;
	
	public List<Ent_InventoryType> findAllInventoryType() throws Exception;
	
	public void save(Ent_InventoryType inventoryType) throws Exception;
	
	public Ent_InventoryType update(Ent_InventoryType inventoryType) throws Exception;
	
	public void delete(Ent_InventoryType inventoryType) throws Exception;

}
