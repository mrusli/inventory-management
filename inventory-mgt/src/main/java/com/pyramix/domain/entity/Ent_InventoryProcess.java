package com.pyramix.domain.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
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
@Table(name = "inventory_process")
public class Ent_InventoryProcess extends IdBasedObject {

	@Column(name = "order_date")
	private LocalDate orderDate;
	
	@Column(name = "status")
	@Enumerated(EnumType.ORDINAL)
	private Enm_StatusProcess processStatus;
	
	@Column(name = "completed_date")
	private LocalDate completedDate;
	
	@Column(name = "process_note")
	private String note;
	
	@ManyToOne(cascade = CascadeType.ALL)
	private Ent_Serial processNumber;
	
	@ManyToOne
	@ToString.Exclude
	private Ent_Company processedByCo;
	
	@ManyToOne
	@ToString.Exclude
	private Ent_Company processedForCo;
	
	@ManyToOne
	private Ent_Customer customer;	
	
	@OneToMany(cascade = CascadeType.ALL)
	@ToString.Exclude
	private List<Ent_InventoryProcessMaterial> processMaterials;	
	
	@Column(name = "process_type")
	private Enm_TypeProcess processType;
}
