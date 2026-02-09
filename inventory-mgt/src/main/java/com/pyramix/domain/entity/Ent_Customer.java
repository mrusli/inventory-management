package com.pyramix.domain.entity;

import org.hibernate.type.TrueFalseConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "customer")
public class Ent_Customer extends IdBasedObject {

	@Column(name = "co_type")
	@Enumerated(EnumType.ORDINAL)
	private Enm_TypeCompany companyType;
	
	@Column(name = "co_legalname")
	private String companyLegalName;
	
	@Column(name = "co_displname")
	private String companyDisplayName;
	
	@Column(name = "contact")
	private String contactPerson;
	
	@Column(name = "addrs01")
	private String address01;
	
	@Column(name = "addrs02")
	private String address02;
	
	@Column(name = "city")
	private String city;
	
	@Column(name = "p_code")
	private String postalCode;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "phone_ext")
	private String extension;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "note")
	private String note;
	
	@Column(name = "co_actv")
	@Convert(converter = TrueFalseConverter.class)
	private boolean active = true;
	
	@Transient
	private boolean addInProgress = false;	

	public Ent_Customer(Enm_TypeCompany companyType, String companyLegalName, String companyDisplayName) {
		super();
		this.companyType = companyType;
		this.companyLegalName = companyLegalName;
		this.companyDisplayName = companyDisplayName;
	}

	public Ent_Customer() {
		super();
	}
}
