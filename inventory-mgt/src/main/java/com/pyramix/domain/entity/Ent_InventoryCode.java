package com.pyramix.domain.entity;

import jakarta.persistence.Column;
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
@Table(name = "inventory_code")
public class Ent_InventoryCode extends IdBasedObject {
	
	@Column(name = "prod_code")
	private String productCode;
	
	@Column(name = "prod_code_dscrpt")
	private String codeDescription;
	
	@ManyToOne
	@ToString.Exclude
	private Ent_InventoryType inventoryType;

	@Transient
	private boolean editInProgress = false;

	@Transient
	private boolean addInProgress = false;	
	
	public Ent_InventoryCode(String productCode, Ent_InventoryType inventoryType) {
		super();
		this.productCode = productCode;
		this.inventoryType = inventoryType;
	}

	public Ent_InventoryCode() {
		super();
	}
}
