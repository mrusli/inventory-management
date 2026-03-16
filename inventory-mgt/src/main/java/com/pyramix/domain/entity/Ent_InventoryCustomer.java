package com.pyramix.domain.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "inventory_customer")
public class Ent_InventoryCustomer extends IdBasedObject {

	@Column(name = "inv_thkns")
	private double thickness;
	
	@Column(name = "inv_wdth")
	private double width;
	
	@Column(name = "inv_lngth")
	private double length;
	
	@Column(name = "inv_s_qty")
	private int sheetQuantity;
	
	@Column(name = "inv_w_qty")
	private double weightQuantity;
	
	@Column(name = "mrkng")
	private String marking;
	
	@Column(name = "dscrpt")
	private String description;
	
	@ManyToOne
	private Ent_Customer customer;
	
	@Column(name = "entry_date")
	private LocalDate entryDate;
	
	@ManyToOne
	private Ent_InventoryCode inventoryCode;
	
	@Column(name = "inv_status")
	@Enumerated(EnumType.ORDINAL)
	private Enm_StatusInventory inventoryStatus = Enm_StatusInventory.ready;
	
	@Column(name = "inv_pack")
	@Enumerated(EnumType.ORDINAL)
	private Enm_TypePacking inventoryPacking = Enm_TypePacking.coil;
	
	@Column(name = "inv_note")
	private String note;
	
	@ManyToOne
	private Ent_InventoryProcess inventoryProcess;
	
	@ManyToOne
	private Ent_SuratJalan suratJalan;
}
