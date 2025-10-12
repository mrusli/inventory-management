package com.pyramix.domain.entity;

import org.hibernate.type.TrueFalseConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "suratjalan_product")
public class Ent_SuratJalanProduct extends IdBasedObject {

	@ManyToOne
	private Ent_InventoryCode inventoryCode;
	
	@Column(name = "marking")
	private String marking;
	
	@Column(name = "spek")
	private String spek;
	
	@Column(name = "inv_thkns")
	private double thickness;
	
	@Column(name = "inv_wdth")
	private double width;
	
	@Column(name = "inv_lngth")
	private double length;
	
	@Column(name = "pack")
	@Enumerated(EnumType.ORDINAL)
	private Enm_TypePacking packing;
	
	@Column(name = "w_qty")
	private double quantityByKg;
	
	@Column(name = "s_qty")
	private int quantityBySht;
	
	@Column(name = "re_coil")
	@Convert(converter = TrueFalseConverter.class)
	private boolean recoil;
	
	@Transient
	private boolean editInProgress = false;
	
}
