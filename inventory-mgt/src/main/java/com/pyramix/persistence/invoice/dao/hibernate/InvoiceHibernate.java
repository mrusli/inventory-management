package com.pyramix.persistence.invoice.dao.hibernate;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.domain.entity.Ent_Invoice;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.invoice.dao.InvoiceDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class InvoiceHibernate extends DaoHibernate implements InvoiceDao {

	@Override
	public Ent_Invoice findInvoiceById(long id) throws Exception {
		
		return (Ent_Invoice) super.findById(Ent_Invoice.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ent_Invoice> findAllInvoice() throws Exception {
		
		return super.findAll(Ent_Invoice.class);
	}

	@Override
	public Ent_Invoice update(Ent_Invoice invoice) throws Exception {
		
		return (Ent_Invoice) super.update(invoice);
	}

	@Override
	public void save(Ent_Invoice invoice) throws Exception {
		
		super.save(invoice);
	}

	@Override
	public void delete(Ent_Invoice invoice) throws Exception {
		
		super.delete(invoice);
	}

	@Override
	public List<Ent_Invoice> findInvoiceByCustomer(Ent_Customer customer) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Ent_Invoice> criteriaQuery = criteriaBuilder.createQuery(Ent_Invoice.class);
		Root<Ent_Invoice> root = criteriaQuery.from(Ent_Invoice.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("invc_customer"), customer));
		
		try {
			
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public Ent_Invoice findInvoiceProductsByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Transaction tx = session.beginTransaction();
		Ent_Invoice invoice = null;
		try {
			invoice = session
					.get(Ent_Invoice.class, id);
			Hibernate.initialize(invoice.getInvoiceProducts());
			tx.commit();
			invoice.getInvoiceProducts().size();
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}

		return invoice;
	}

	@Override
	public Ent_Invoice findInvoicePalletsByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		Transaction tx = session.beginTransaction();
		Ent_Invoice invoice = null;
		try {
			invoice = session
					.get(Ent_Invoice.class, id);
			Hibernate.initialize(invoice.getInvoicePallet());
			tx.commit();
			invoice.getInvoiceProducts().size();
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}

		return invoice;

	}

}
