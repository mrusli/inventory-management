package com.pyramix.persistence.suratjalan.dao.hibernate;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.domain.entity.Ent_SuratJalan;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.suratjalan.dao.SuratJalanDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class SuratJalanHibernate extends DaoHibernate implements SuratJalanDao {

	@Override
	public Ent_SuratJalan findSuratJalanById(long id) throws Exception {
		
		return (Ent_SuratJalan) super.findById(Ent_SuratJalan.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ent_SuratJalan> findAllSuratJalan() throws Exception {
		
		return super.findAll(Ent_SuratJalan.class);
	}

	@Override
	public Ent_SuratJalan update(Ent_SuratJalan suratjalan) throws Exception {
		
		return (Ent_SuratJalan) super.update(suratjalan);
	}

	@Override
	public void save(Ent_SuratJalan suratjalan) throws Exception {
		
		super.save(suratjalan);
	}

	@Override
	public void delete(Ent_SuratJalan suratjalan) throws Exception {
		
		super.delete(suratjalan);
	}

	@Override
	public List<Ent_SuratJalan> findSuratJalanByCustomer(Ent_Customer customer) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Ent_SuratJalan> criteriaQuery = criteriaBuilder.createQuery(Ent_SuratJalan.class);
		Root<Ent_SuratJalan> root = criteriaQuery.from(Ent_SuratJalan.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("customer"), customer),
				criteriaBuilder.isNull(root.get("invoice")));
		criteriaQuery.orderBy(
				criteriaBuilder.desc(root.get("suratjalanDate")));
		
		try {
			
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}

	}

	@Override
	public Ent_SuratJalan getSuratJalanProductByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Transaction tx = session.beginTransaction();
		Ent_SuratJalan suratjalan = null;
		try {
			suratjalan = session
					.get(Ent_SuratJalan.class, id);
			Hibernate.initialize(suratjalan.getSuratjalanProducts());
			tx.commit();
			suratjalan.getSuratjalanProducts().size();
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
		
		return suratjalan;
	}

	@Override
	public List<Ent_SuratJalan> findSuratJalanByCustomerNonInvoice(Ent_Customer customer) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Ent_SuratJalan> criteriaQuery = criteriaBuilder.createQuery(Ent_SuratJalan.class);
		Root<Ent_SuratJalan> root = criteriaQuery.from(Ent_SuratJalan.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("customer"), customer));
		criteriaQuery.orderBy(
				criteriaBuilder.desc(root.get("suratjalanDate")));
		
		try {
			
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

}
