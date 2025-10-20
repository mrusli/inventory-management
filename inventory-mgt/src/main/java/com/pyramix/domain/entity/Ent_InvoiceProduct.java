package com.pyramix.domain.entity;

import org.hibernate.type.TrueFalseConverter;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "invoice_product")
public class Ent_InvoiceProduct extends IdBasedObject {

	@ManyToOne
	private Ent_SuratJalan ref_suratjalan;
	
	@ManyToOne
	private Ent_InventoryCode inventoryCode;
	
	private String marking;
	
	private String spek;
	
	private Enm_TypePacking packing;
	
	private int quantity_by_sht = 0;
	
	private double quantity_by_kg = 0.0;
	
	private boolean by_kg;
	
	private double unit_price = 0.0;
	
	private double sub_total = 0.0;
	
	@Convert(converter = TrueFalseConverter.class)
	private boolean use_pallet = false;
	
	private double pallet_price = 0.0;
	
	private int pallet_qty = 0;
	
	private String ref_document;
	
	@Transient
	private boolean editInProgress = false;
	
}
