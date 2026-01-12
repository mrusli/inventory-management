package com.pyramix.persistence.customer.dao.hibernate;

import java.util.List;

import org.hibernate.Session;

import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.customer.dao.CustomerDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

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

	@Override
	public List<Ent_Customer> findAllCustomerSorted() throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Ent_Customer> criteriaQuery = criteriaBuilder.createQuery(Ent_Customer.class);
		Root<Ent_Customer> root = criteriaQuery.from(Ent_Customer.class);
		criteriaQuery.orderBy(
				criteriaBuilder.asc(root.get("companyLegalName")));
		
		try {
			
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

}
