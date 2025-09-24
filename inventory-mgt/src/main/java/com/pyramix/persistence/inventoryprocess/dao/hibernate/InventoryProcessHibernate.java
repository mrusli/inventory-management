package com.pyramix.persistence.inventoryprocess.dao.hibernate;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.pyramix.domain.entity.Ent_InventoryProcess;
import com.pyramix.domain.entity.Ent_InventoryProcessMaterial;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.inventoryprocess.dao.InventoryProcessDao;

import jakarta.transaction.Transactional;

public class InventoryProcessHibernate extends DaoHibernate implements InventoryProcessDao {

	@Override
	public Ent_InventoryProcess findInventoryProcessById(long id) throws Exception {

		return (Ent_InventoryProcess) super.findById(Ent_InventoryProcess.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ent_InventoryProcess> findAllInventoryProcess() throws Exception {
		
		return super.findAll(Ent_InventoryProcess.class);
	}

	@Override
	public Ent_InventoryProcess update(Ent_InventoryProcess inventoryProcess) throws Exception {
		
		return (Ent_InventoryProcess) super.update(inventoryProcess);
	}

	@Override
	public void save(Ent_InventoryProcess inventoryProcess) throws Exception {
		
		super.save(inventoryProcess);
	}

	@Override
	public void delete(Ent_InventoryProcess inventoryProcess) throws Exception {
		
		super.delete(inventoryProcess);
	}

	@Override
	@Transactional
	public Ent_InventoryProcess findInventoryProcessMaterialsByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Transaction tx = session.beginTransaction();
		Ent_InventoryProcess invtProc = null;
		try {
			invtProc = session
					.get(Ent_InventoryProcess.class, id);
			Hibernate.initialize(invtProc.getProcessMaterials());
			tx.commit();
			invtProc.getProcessMaterials().size();
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
		return invtProc;
	}

	@Override
	public Ent_InventoryProcessMaterial findInventoryProcessProductsByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Transaction tx = session.beginTransaction();
		Ent_InventoryProcessMaterial invtProcMaterial = null;
		try {
			invtProcMaterial = session
					.get(Ent_InventoryProcessMaterial.class, id);
			Hibernate.initialize(invtProcMaterial.getProcessProducts());
			tx.commit();
			invtProcMaterial.getProcessProducts().size();
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
		return invtProcMaterial;
	}

}
