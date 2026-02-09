package com.pyramix.domain.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "suratjalan")
public class Ent_SuratJalan extends IdBasedObject {

	@Column(name = "pub_date")
	private LocalDate suratjalanDate;
	
	@Column(name = "dev_date")
	private LocalDate deliveryDate;
	
	@EqualsAndHashCode.Exclude
	@OneToOne(cascade = CascadeType.ALL)
	@ToString.Exclude
	private Ent_Serial suratjalanSerial;
	
	@ManyToOne
	@ToString.Exclude
	private Ent_Customer customer;
	
	@Column(name = "ref_doc")
	private String refDocument;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@ToString.Exclude
	private Ent_Invoice invoice;
	
	@OneToMany(cascade = CascadeType.ALL)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<Ent_SuratJalanProduct> suratjalanProducts;
	
	@Column(name = "status_doc")
	private Enm_StatusDocument suratjalanStatus;
	
	@EqualsAndHashCode.Exclude
	@ManyToOne
	@ToString.Exclude
	private Ent_Company processedByCo;
	
	@Column(name = "no_pol")
	private String noPolisi;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Ent_InventoryProcess inventoryProcess;
	
	@Transient
	private boolean addInProgress = false;
	
	@Transient
	private boolean editInProgress = false;
	
}
