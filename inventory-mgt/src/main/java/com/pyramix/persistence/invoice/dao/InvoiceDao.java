package com.pyramix.persistence.invoice.dao;

import java.util.List;

import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.domain.entity.Ent_Invoice;

public interface InvoiceDao {

	public Ent_Invoice findInvoiceById(long id) throws Exception;
	
	public List<Ent_Invoice> findAllInvoice() throws Exception;
	
	public Ent_Invoice update(Ent_Invoice invoice) throws Exception;
	
	public void save(Ent_Invoice invoice) throws Exception;
	
	public void delete(Ent_Invoice invoice) throws Exception;

	public List<Ent_Invoice> findInvoiceByCustomer(Ent_Customer customer) throws Exception;

	public Ent_Invoice findInvoiceProductsByProxy(long id) throws Exception;
	
	public Ent_Invoice findInvoicePalletsByProxy(long id) throws Exception;
	
}
