package com.pyramix.domain.entity;

import jakarta.persistence.Column;
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
@Table(name = "inventory_table")
public class Ent_InventoryTable extends IdBasedObject {

	@Column(name = "inv_thkns")
	private double thickness;

	@Column(name = "inv_wdth")
	private double width;
	
	@Column(name = "inv_lngth")
	private double length;
	
	@Column(name = "inv_w_qty")
	private double weightQuantity;
	
	@ManyToOne
	@ToString.Exclude
	private Ent_InventoryCode inventoryCode;
	
	@Transient
	private boolean editInProgress = false;
}
