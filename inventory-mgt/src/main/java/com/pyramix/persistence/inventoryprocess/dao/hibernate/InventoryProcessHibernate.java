package com.pyramix.persistence.inventoryprocess.dao.hibernate;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.pyramix.domain.entity.Enm_StatusProcess;
import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.domain.entity.Ent_InventoryProcess;
import com.pyramix.domain.entity.Ent_InventoryProcessMaterial;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.inventoryprocess.dao.InventoryProcessDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
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

	@Override
	public List<Ent_InventoryProcess> findInventoryProcessByCustomer(Ent_Customer customer) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Ent_InventoryProcess> criteriaQuery = criteriaBuilder.createQuery(Ent_InventoryProcess.class);
		Root<Ent_InventoryProcess> root = criteriaQuery.from(Ent_InventoryProcess.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("customer"), customer));
		criteriaQuery.orderBy(
				criteriaBuilder.desc(root.get("orderDate")));
		
		try {
			
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public List<Ent_InventoryProcess> findInventoryByCustomerByStatus(Ent_Customer customer,
			Enm_StatusProcess statusProses) throws Exception {
		
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Ent_InventoryProcess> criteriaQuery = criteriaBuilder.createQuery(Ent_InventoryProcess.class);
		Root<Ent_InventoryProcess> root = criteriaQuery.from(Ent_InventoryProcess.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("customer"), customer),
				criteriaBuilder.equal(root.get("processStatus"), statusProses));
		
		try {
			
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}

	}

	@Override
	public List<Ent_InventoryProcess> findInventoryByCustomerByStatusBySuratJalan(Ent_Customer customer, 
			Enm_StatusProcess process) throws Exception {
		
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Ent_InventoryProcess> criteriaQuery = criteriaBuilder.createQuery(Ent_InventoryProcess.class);
		Root<Ent_InventoryProcess> root = criteriaQuery.from(Ent_InventoryProcess.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("customer"), customer),
				criteriaBuilder.equal(root.get("processStatus"), process),
				criteriaBuilder.isNull(root.get("suratjalan")));
		
		try {
			
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
	}

//	@Override
//	public List<Ent_InventoryProcess> findInventoryProcessBySuratJalan() throws Exception {
//		Session session = super.getSessionFactory().openSession();
//		
//		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//		CriteriaQuery<Ent_InventoryProcess> criteriaQuery = criteriaBuilder.createQuery(Ent_InventoryProcess.class);
//		Root<Ent_InventoryProcess> root = criteriaQuery.from(Ent_InventoryProcess.class);
//		criteriaQuery.select(root).where(
//				criteriaBuilder.isNotNull(root.get("completedDate")),
//				criteriaBuilder.isNull(root.get("suratjalan")));
//		
//		try {
//			
//			return session.createQuery(criteriaQuery).getResultList();
//			
//		} catch (Exception e) {
//			throw e;
//		} finally {
//			session.close();
//		}
//
//	}

}
