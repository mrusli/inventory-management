package com.pyramix.persistence.inventorycode.dao.hibernate;

import java.util.List;

import org.hibernate.Session;

import com.pyramix.domain.entity.Ent_InventoryCode;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.inventorycode.dao.InventoryCodeDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class InventoryCodeHibernate extends DaoHibernate implements InventoryCodeDao {

	@Override
	public Ent_InventoryCode findInventoryCodeById(long id) throws Exception {
		
		return (Ent_InventoryCode) super.findById(Ent_InventoryCode.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ent_InventoryCode> findAllInventoryCode() throws Exception {
		
		return super.findAll(Ent_InventoryCode.class);
	}

	@Override
	public void save(Ent_InventoryCode inventoryCode) throws Exception {
		
		super.save(inventoryCode);
	}

	@Override
	public void update(Ent_InventoryCode inventoryCode) throws Exception {
		
		super.update(inventoryCode);
	}

	@Override
	public void delete(Ent_InventoryCode inventoryCode) throws Exception {
		
		super.delete(inventoryCode);
	}

	@Override
	public List<Ent_InventoryCode> findAllInventoryCodesSorted() throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Ent_InventoryCode> criteriaQuery =
				criteriaBuilder.createQuery(Ent_InventoryCode.class);
		Root<Ent_InventoryCode> root = criteriaQuery.from(Ent_InventoryCode.class);
		criteriaQuery.orderBy(
				criteriaBuilder.asc(root.get("productCode")));
		
		try {
		
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
	}

}
