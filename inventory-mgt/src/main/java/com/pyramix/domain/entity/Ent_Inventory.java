package com.pyramix.domain.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "inventory")
public class Ent_Inventory extends IdBasedObject {

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
	
	@Column(name = "rcv_date")
	private LocalDate receiveDate;

	@ManyToOne
	private Ent_InventoryCode inventoryCode;
	
	@Column(name = "inv_status")
	@Enumerated(EnumType.ORDINAL)
	private Enm_StatusInventory inventoryStatus = Enm_StatusInventory.ready;
	
	@Column(name = "inv_pack")
	@Enumerated(EnumType.ORDINAL)
	private Enm_InventoryPacking inventoryPacking = Enm_InventoryPacking.coil;

	@Column(name = "inv_note")
	private String note;
	
	@OneToMany
	private List<Ent_InventoryProcess> inventoryProcesses;
	
	@Transient
	private boolean addInProgress = false;
	
	public Ent_Inventory() {
		super();
	}

	public Ent_Inventory(double thickness, double width, double length, int sheetQuantity, double weightQuantity,
			String marking, LocalDate receiveDate) {
		super();
		this.thickness = thickness;
		this.width = width;
		this.length = length;
		this.sheetQuantity = sheetQuantity;
		this.weightQuantity = weightQuantity;
		this.marking = marking;
		this.receiveDate = receiveDate;
	}
	
	
}
