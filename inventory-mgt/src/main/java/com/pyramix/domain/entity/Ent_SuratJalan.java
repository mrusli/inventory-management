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

@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "suratjalan")
public class Ent_SuratJalan extends IdBasedObject {

	@Column(name = "pub_date")
	private LocalDate suratjalanDate;
	
	@Column(name = "dev_date")
	private LocalDate deliveryDate;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Ent_Serial suratjalanSerial;
	
	@ManyToOne
	private Ent_Customer customer;
	
	@Column(name = "ref_doc")
	private String refDocument;
	
	@ManyToOne
	private Ent_Invoice invoice;
	
	@OneToMany(cascade = CascadeType.ALL)
	@ToString.Exclude
	private List<Ent_SuratJalanProduct> suratjalanProducts;
	
	@Column(name = "status_doc")
	private Enm_StatusDocument suratjalanStatus;
	
	@ManyToOne
	@ToString.Exclude
	private Ent_Company processedByCo;
	
	@Column(name = "no_pol")
	private String noPolisi;
	
	@Transient
	private boolean addInProgress = false;
	
}
