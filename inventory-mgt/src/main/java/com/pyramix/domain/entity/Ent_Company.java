package com.pyramix.domain.entity;

import org.hibernate.type.TrueFalseConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "organization")
public class Ent_Company extends IdBasedObject {

	@Column(name = "type")
	@Enumerated(EnumType.ORDINAL)
	private Enm_TypeCompany companyType;
	
	@Column(name = "legal_name")
	private String companyLegalName;
	
	@Column(name = "display_name")
	private String companyDisplayName;
	
	@Column(name = "address_01")
	private String address01;
	
	@Column(name = "address_02")
	private String address02;
	
	@Column(name = "city")
	private String city;
	
	@Column(name = "postal_code")
	private String postalCode;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "fax")
	private String fax;
	
	@Column(name = "hoq")
	@Convert(converter = TrueFalseConverter.class)
	private boolean hoq;
	
	@Column(name = "proc")
	@Convert(converter = TrueFalseConverter.class)
	private boolean proc;
	
}
