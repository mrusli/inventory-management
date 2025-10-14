package com.pyramix.domain.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "invoice")
public class Ent_Invoice extends IdBasedObject {

	private LocalDate invc_date;
	
	private Enm_TypeInvoice invc_type;
	
	private Enm_TypePayment pay_type;
	
	private int jum_hari;
	
	private boolean use_ppn;
	
	private double total_invoice;
	
	private double total_ppn;
	
	private String invc_note;

	@ManyToOne
	private Ent_Customer invc_customer;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Ent_Serial invc_ser;
		
	private Enm_StatusDocument invc_status;
	
	@OneToMany
	private List<Ent_SuratJalan> suratjalans;
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<Ent_InvoiceProduct> invoiceProducts;
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<Ent_InvoiceKwitansi> invoiceKwitansis;
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<Ent_InvoiceFaktur> invoiceFakturs;
}
