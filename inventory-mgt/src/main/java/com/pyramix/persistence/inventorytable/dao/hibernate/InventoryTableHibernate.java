package com.pyramix.persistence.inventorytable.dao.hibernate;

import java.util.List;

import org.hibernate.Session;

import com.pyramix.domain.entity.Ent_InventoryCode;
import com.pyramix.domain.entity.Ent_InventoryTable;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.inventorytable.dao.InventoryTableDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class InventoryTableHibernate extends DaoHibernate implements InventoryTableDao {

	@Override
	public Ent_InventoryTable findInventoryTableById(long id) throws Exception {

		return (Ent_InventoryTable) super.findById(Ent_InventoryTable.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ent_InventoryTable> findAllInventoryTable() throws Exception {
		
		return super.findAll(Ent_InventoryTable.class);
	}

	@Override
	public Ent_InventoryTable update(Ent_InventoryTable inventoryTable) throws Exception {
		
		return (Ent_InventoryTable) super.update(inventoryTable);
	}

	@Override
	public void save(Ent_InventoryTable inventoryTable) throws Exception {
		
		super.save(inventoryTable);
	}

	@Override
	public void delete(Ent_InventoryTable inventoryTable) throws Exception {
		
		super.delete(inventoryTable);
	}

	@Override
	public List<Ent_InventoryTable> findInventoryByInventoryCode(Ent_InventoryCode inventoryCode) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Ent_InventoryTable> criteriaQuery = criteriaBuilder.createQuery(Ent_InventoryTable.class);
		Root<Ent_InventoryTable> root = criteriaQuery.from(Ent_InventoryTable.class);
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

}
