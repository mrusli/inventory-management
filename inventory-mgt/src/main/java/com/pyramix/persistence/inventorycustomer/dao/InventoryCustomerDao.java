package com.pyramix.persistence.inventorycustomer.dao;

import java.util.List;

import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.domain.entity.Ent_InventoryCode;
import com.pyramix.domain.entity.Ent_InventoryCustomer;

public interface InventoryCustomerDao {

	public Ent_InventoryCustomer findInventoryCustomerById(long id) throws Exception;
	
	public List<Ent_InventoryCustomer> findAllInventoryCustomer() throws Exception;
	
	public Ent_InventoryCustomer update(Ent_InventoryCustomer inventoryCustomer) throws Exception;
	
	public void save(Ent_InventoryCustomer inventoryCustomer) throws Exception;
	
	public void delete(Ent_InventoryCustomer inventoryCustomer) throws Exception;
	
	/**
	 * @param customer
	 * @return {@link List} of {@link Ent_InventoryCustomer}
	 * @throws Exception
	 */
	public List<Ent_InventoryCustomer> findInventoryCustomerByCustomer(Ent_Customer customer) throws Exception;
	
	public List<Ent_InventoryCustomer> findInventoryCustomerByInventoryCode(Ent_InventoryCode inventoryCode) throws Exception;

	public List<Ent_InventoryCustomer> findInventoryCustomerByCustomer_InventoryCode(Ent_Customer customer, 
			Ent_InventoryCode inventoryCode) throws Exception;

	public List<Ent_InventoryCustomer> findInventoryCustomerByCustomer_InventoryCode_NonStatus(Ent_Customer customer,
			Ent_InventoryCode inventoryCode) throws Exception;

	public List<Ent_InventoryCustomer> findInventoryCustomerByCustomerNonSuratJalan(Ent_Customer customer)
		throws Exception;
	
	
}
