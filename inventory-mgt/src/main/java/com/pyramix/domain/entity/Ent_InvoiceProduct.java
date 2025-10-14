package com.pyramix.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
	private Ent_InventoryCode inventoryCode;
	
	private String marking;
	
	private String spek;
	
	private Enm_TypePacking packing;
	
	private int quantity_by_sht;
	
	private double quantity_by_kg;
	
	private boolean by_kg;
	
	private double unit_price;
	
	private double sub_total;
	
	private boolean use_pallet;
	
	private double pallet_price;
	
	private int pallet_qty;
	
}
