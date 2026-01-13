package com.pyramix.persistence.inventory.dao.hibernate;

import java.util.List;

import org.hibernate.Session;

import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.domain.entity.Ent_Inventory;
import com.pyramix.domain.entity.Ent_InventoryCode;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.inventory.dao.InventoryDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class InventoryHibernate extends DaoHibernate implements InventoryDao {

	@Override
	public Ent_Inventory findInventoryById(long id) throws Exception {
		
		return (Ent_Inventory) super.findById(Ent_Inventory.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ent_Inventory> findAllInventory() throws Exception {

		return super.findAll(Ent_Inventory.class);
	}

	@Override
	public Ent_Inventory update(Ent_Inventory ent_Inventory) throws Exception {
		
		return (Ent_Inventory) super.update(ent_Inventory);
	}

	@Override
	public void save(Ent_Inventory ent_Inventory) throws Exception {
		
		super.save(ent_Inventory);
	}

	@Override
	public void delete(Ent_Inventory ent_Inventory) throws Exception {
		
		super.delete(ent_Inventory);
	}

	@Override
	public List<Ent_Inventory> findInventoryByCustomer(Ent_Customer customer) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Ent_Inventory> criteriaQuery = criteriaBuilder.createQuery(Ent_Inventory.class);
		Root<Ent_Inventory> root = criteriaQuery.from(Ent_Inventory.class);
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
	public List<Ent_Inventory> findInventoryByInventoryCode(Ent_InventoryCode inventoryCode) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Ent_Inventory> criteriaQuery = criteriaBuilder.createQuery(Ent_Inventory.class);
		Root<Ent_Inventory> root = criteriaQuery.from(Ent_Inventory.class);
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
	public List<Ent_Inventory> findInventoryByCustomer_InventoryCode(Ent_Customer customer, Ent_InventoryCode inventoryCode)
			throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Ent_Inventory> criteriaQuery = criteriaBuilder.createQuery(Ent_Inventory.class);
		Root<Ent_Inventory> root = criteriaQuery.from(Ent_Inventory.class);
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
