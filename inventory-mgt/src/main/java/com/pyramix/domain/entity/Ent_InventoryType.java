package com.pyramix.domain.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinTable;
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
@Table(name = "inventory_type")
public class Ent_InventoryType extends IdBasedObject {

	@Column(name = "prod_dnsty")
	private double density;
	
	@Column(name = "prod_type")
	private String productType;
	
	@Column(name = "prod_type_dscrpt")
	private String productDescription;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@ToString.Exclude
	@JoinTable
	private List<Ent_InventoryCode> inventoryCodes;
	
	@Transient
	private boolean editInProgress = false;

	public Ent_InventoryType(double density, String productType, String productDescription) {
		super();
		this.density = density;
		this.productType = productType;
		this.productDescription = productDescription;
	}

	public Ent_InventoryType() {
		super();
	}
	
}
