package com.pyramix.domain.entity;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "invoice_kwitansi")
public class Ent_InvoiceKwitansi extends IdBasedObject {
	
	private LocalDate kwitansi_date;
	
	@OneToOne(cascade = CascadeType.ALL)
	@ToString.Exclude
	private Ent_Serial kwitansi_ser;
	
	private String kwitansi_for;
	
	private String amount_words;
	
	private double amount;

}
