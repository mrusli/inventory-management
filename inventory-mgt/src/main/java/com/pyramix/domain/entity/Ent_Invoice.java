package com.pyramix.domain.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
	
	/**
	 * subtotal for jasa BEFORE ppn 11%
	 */
	private double subtotal01 = 0.0;
	
	/**
	 * subtotal for pallete / bahan BEFORE ppn 11% 
	 */
	private double subtotal01Plt = 0.0;
	
	/**
	 * subtotal for jasa AFTER ppn 11%
	 */
	private double subtotal02 = 0.0;
	
	/**
	 * subtotal for pallete / bahan AFTER ppn 11%
	 */
	private double subtotal02Plt;
	
	/**
	 * after deducting pph23 from subtotal02
	 */
	private double total_invoice = 0.0;
	
	private double amount_ppn = 0.0;
	
	private double amount_ppn_plt = 0.0;
	
	private double amount_pph = 0.0;
	
	private String invc_note;

	@ManyToOne
	private Ent_Customer invc_customer;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Ent_Serial invc_ser;
		
	private Enm_StatusDocument invc_status;
		
	@EqualsAndHashCode.Exclude
	@OneToMany(cascade = CascadeType.ALL)
	private List<Ent_InvoiceProduct> invoiceProducts;

	@EqualsAndHashCode.Exclude
	@OneToMany(cascade = CascadeType.ALL)	
	private List<Ent_InvoicePallet> invoicePallet;
	
	@ManyToOne(cascade = CascadeType.ALL)
	private Ent_InvoiceKwitansi jasaKwitansi;
	
	@ManyToOne
	private Ent_InvoiceKwitansi bahanKwitansi;
	
	@ManyToOne(cascade = CascadeType.ALL)
	private Ent_InvoiceFaktur jasaFaktur;
	
	@ManyToOne
	private Ent_InvoiceFaktur bahanFaktur;
	
	@Transient
	private boolean addInProgress = false;
}
