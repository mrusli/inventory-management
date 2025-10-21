package com.pyramix.domain.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
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
@Table(name = "serial")
public class Ent_Serial extends IdBasedObject {

	@Column(name = "doc_type")
	private Enm_TypeDocument documentType;
	
	@ManyToOne
	@ToString.Exclude
	private Ent_Company company;
	
	@Column(name = "ser_date")
	private LocalDate serialDate;
	
	@Column(name = "ser_num")
	private int serialNumber;
	
	@Column(name = "ser_comp")
	private String serialComp;
	
}
