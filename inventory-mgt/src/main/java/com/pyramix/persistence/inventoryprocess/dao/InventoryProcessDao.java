package com.pyramix.persistence.inventoryprocess.dao;

import java.util.List;

import com.pyramix.domain.entity.Enm_StatusProcess;
import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.domain.entity.Ent_InventoryProcess;
import com.pyramix.domain.entity.Ent_InventoryProcessMaterial;

public interface InventoryProcessDao {

	public Ent_InventoryProcess findInventoryProcessById(long id) throws Exception;
	
	public List<Ent_InventoryProcess> findAllInventoryProcess() throws Exception;
	
	public Ent_InventoryProcess update(Ent_InventoryProcess inventoryProcess) throws Exception;
	
	public void save(Ent_InventoryProcess inventoryProcess) throws Exception;
	
	public void delete(Ent_InventoryProcess inventoryProcess) throws Exception;

	public Ent_InventoryProcess findInventoryProcessMaterialsByProxy(long id) throws Exception;

	public Ent_InventoryProcessMaterial findInventoryProcessProductsByProxy(long id) throws Exception;

	public List<Ent_InventoryProcess> findInventoryProcessByCustomer(Ent_Customer customer) throws Exception;

	public List<Ent_InventoryProcess> findInventoryByCustomerByStatus(Ent_Customer customer,
			Enm_StatusProcess statusProses) throws Exception;

	public List<Ent_InventoryProcess> findInventoryByCustomerByStatusBySuratJalan(Ent_Customer customer, 
			Enm_StatusProcess process) throws Exception;

	// public List<Ent_InventoryProcess> findInventoryProcessBySuratJalan() throws Exception;
	
}
