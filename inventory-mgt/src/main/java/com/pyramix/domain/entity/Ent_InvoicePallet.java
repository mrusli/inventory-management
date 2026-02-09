package com.pyramix.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "invoice_pallet")
public class Ent_InvoicePallet extends IdBasedObject {

	@ManyToOne
	private Ent_SuratJalan ref_suratjalan;
	
	private String marking;
	
	private String keterangan;
	
	private int qty_pcs;
	
	private double pallet_price;
	
	private double pallet_subtotal;
	
	@Transient
	private boolean editInProgress = false;
}
