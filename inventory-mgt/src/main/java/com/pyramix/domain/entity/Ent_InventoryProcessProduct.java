package com.pyramix.domain.entity;

import org.hibernate.type.TrueFalseConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "inventory_process_product")
public class Ent_InventoryProcessProduct extends IdBasedObject {
		
	@Column(name = "mrkng")
	private String marking;
	
	@ManyToOne
	private Ent_InventoryCode inventoryCode;
	
	@Column(name = "inv_thkns")
	private double thickness;
	
	@Column(name = "inv_wdth")
	private double width;
	
	@Column(name = "inv_lngth")
	private double length;	
	
	@Column(name = "re_coil")
	@Convert(converter = TrueFalseConverter.class)
	private boolean recoil;
	
	@Column(name = "inv_s_qty")
	private int sheetQuantity;
	
	@Column(name = "inv_w_qty")
	private double weightQuantity;
	
	@Column(name = "inv_pack")
	@Enumerated(EnumType.ORDINAL)
	private Enm_InventoryPacking inventoryPacking = Enm_InventoryPacking.petian;
	
	@ManyToOne
	@ToString.Exclude
	private Ent_Company processedByCo;
	
	@ManyToOne
	private Ent_InventoryProcessMaterial processMaterial;
}
