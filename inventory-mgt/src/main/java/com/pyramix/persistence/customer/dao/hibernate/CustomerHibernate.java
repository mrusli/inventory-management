package com.pyramix.persistence.customer.dao.hibernate;

import java.util.List;

import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.customer.dao.CustomerDao;

public class CustomerHibernate extends DaoHibernate implements CustomerDao {

	@Override
	public Ent_Customer findCustomerById(long id) throws Exception {
		
		return (Ent_Customer) super.findById(Ent_Customer.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ent_Customer> findAllCustomer() throws Exception {
		
		return super.findAll(Ent_Customer.class);
	}

	@Override
	public Ent_Customer update(Ent_Customer ent_Customer) throws Exception {
		
		return (Ent_Customer) super.update(ent_Customer);
	}

	@Override
	public void save(Ent_Customer ent_Customer) throws Exception {
		
		super.save(ent_Customer);
	}

	@Override
	public void delete(Ent_Customer ent_Customer) throws Exception {
		
		super.delete(ent_Customer);
	}

}
