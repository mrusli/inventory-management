package com.pyramix.persistence.inventoryprocess.dao;

import java.util.List;

import com.pyramix.domain.entity.Ent_InventoryProcess;

public interface InventoryProcessDao {

	public Ent_InventoryProcess findInventoryProcessById(long id) throws Exception;
	
	public List<Ent_InventoryProcess> findAllInventoryProcess() throws Exception;
	
	public Ent_InventoryProcess update(Ent_InventoryProcess inventoryProcess) throws Exception;
	
	public void save(Ent_InventoryProcess inventoryProcess) throws Exception;
	
	public void delete(Ent_InventoryProcess inventoryProcess) throws Exception;
	
}
