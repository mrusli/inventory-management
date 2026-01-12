package com.pyramix.persistence.customer.dao;

import java.util.List;

import com.pyramix.domain.entity.Ent_Customer;

public interface CustomerDao {
	
	public Ent_Customer findCustomerById(long id) throws Exception;
	
	public List<Ent_Customer> findAllCustomer() throws Exception;
	
	public Ent_Customer update(Ent_Customer ent_Customer) throws Exception;
	
	public void save(Ent_Customer ent_Customer) throws Exception;
	
	public void delete(Ent_Customer ent_Customer) throws Exception;

	public List<Ent_Customer> findAllCustomerSorted() throws Exception;

}
