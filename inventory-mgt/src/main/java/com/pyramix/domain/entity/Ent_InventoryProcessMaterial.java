package com.pyramix.domain.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "inventory_process_material")
public class Ent_InventoryProcessMaterial extends IdBasedObject {

	@Column(name = "mrkng")
	private String marking;
	
	@Column(name = "inv_thkns")
	private Double thickness;
	
	@Column(name = "inv_wdth")
	private Double width;
	
	@Column(name = "inv_lgth")
	private Double length;
	
	@Column(name = "inv_pack")
	private Enm_TypePacking inventoryPacking;
	
	@Column(name = "inv_w_qty")
	private Double weightQuantity;
	
	@Column(name = "inv_s_qty")
	private int sheetQuantity;

	@ManyToOne
	private Ent_InventoryCode inventoryCode;
	
	@ManyToOne
	@ToString.Exclude
	private Ent_InventoryProcess inventoryProcess;
	
	@OneToMany(cascade = CascadeType.ALL)
	@ToString.Exclude
	private List<Ent_InventoryProcessProduct> processProducts;

	@ManyToOne
	private Ent_Inventory inventoryCoil;
	
	@Column(name = "cntrct")
	private String contractNumber;
	
	@Column(name = "lc")
	private String lcNumber;
}
