package com.pyramix.persistence.inventorycustomer.dao.hibernate;

import java.util.List;

import org.hibernate.Session;

import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.domain.entity.Ent_InventoryCode;
import com.pyramix.domain.entity.Ent_InventoryCustomer;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.inventorycustomer.dao.InventoryCustomerDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class InventoryCustomerHibernate extends DaoHibernate implements InventoryCustomerDao {

	@Override
	public Ent_InventoryCustomer findInventoryCustomerById(long id) throws Exception {

		return (Ent_InventoryCustomer) super.findById(Ent_InventoryCustomer.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ent_InventoryCustomer> findAllInventoryCustomer() throws Exception {
		
		return super.findAll(Ent_InventoryCustomer.class);
	}

	@Override
	public Ent_InventoryCustomer update(Ent_InventoryCustomer inventoryCustomer) throws Exception {
		
		return (Ent_InventoryCustomer) super.update(inventoryCustomer);
	}

	@Override
	public void save(Ent_InventoryCustomer inventoryCustomer) throws Exception {
		
		super.save(inventoryCustomer);
	}

	@Override
	public void delete(Ent_InventoryCustomer inventoryCustomer) throws Exception {
		
		super.delete(inventoryCustomer);
	}

	@Override
	public List<Ent_InventoryCustomer> findInventoryCustomerByCustomer(Ent_Customer customer) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Ent_InventoryCustomer> criteriaQuery = criteriaBuilder.createQuery(Ent_InventoryCustomer.class);
		Root<Ent_InventoryCustomer> root = criteriaQuery.from(Ent_InventoryCustomer.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("customer"), customer));
		
		try {
			
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}

	}

	@Override
	public List<Ent_InventoryCustomer> findInventoryCustomerByInventoryCode(Ent_InventoryCode inventoryCode)
			throws Exception {
		
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Ent_InventoryCustomer> criteriaQuery = criteriaBuilder.createQuery(Ent_InventoryCustomer.class);
		Root<Ent_InventoryCustomer> root = criteriaQuery.from(Ent_InventoryCustomer.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("inventoryCode"), inventoryCode));
		try {
			
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public List<Ent_InventoryCustomer> findInventoryCustomerByCustomer_InventoryCode(Ent_Customer customer,
			Ent_InventoryCode inventoryCode) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Ent_InventoryCustomer> criteriaQuery = criteriaBuilder.createQuery(Ent_InventoryCustomer.class);
		Root<Ent_InventoryCustomer> root = criteriaQuery.from(Ent_InventoryCustomer.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("customer"), customer),
				criteriaBuilder.equal(root.get("inventoryCode"), inventoryCode));
		
		try {
			
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public List<Ent_InventoryCustomer> findInventoryCustomerByCustomer_InventoryCode_NonStatus(Ent_Customer customer,
			Ent_InventoryCode inventoryCode) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Ent_InventoryCustomer> criteriaQuery = criteriaBuilder.createQuery(Ent_InventoryCustomer.class);
		Root<Ent_InventoryCustomer> root = criteriaQuery.from(Ent_InventoryCustomer.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("customer"), customer),
				criteriaBuilder.equal(root.get("inventoryCode"), inventoryCode));
		
		try {
			
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

}
