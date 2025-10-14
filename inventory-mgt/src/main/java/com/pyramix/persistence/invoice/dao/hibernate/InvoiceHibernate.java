package com.pyramix.persistence.invoice.dao.hibernate;

import java.util.List;

import com.pyramix.domain.entity.Ent_Invoice;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.invoice.dao.InvoiceDao;

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

}
