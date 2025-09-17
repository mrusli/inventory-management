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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "inventory_process")
public class Ent_InventoryProcess {

	@Column(name = "order_date")
	private LocalDate orderDate;
	
	@Column(name = "status")
	@Enumerated(EnumType.ORDINAL)
	private Enm_StatusProcess processStatus;
	
	@Column(name = "completed_date")
	private LocalDate completedDate;
	
	@Column(name = "process_note")
	private String note;
	
	@ManyToOne
	private Ent_Serial processNumber;
	
	@ManyToOne
	private Ent_Company processedByCo;
	
	@ManyToOne
	private Ent_Company processedForCo;
	
	@OneToMany
	private List<Ent_InventoryProcessMaterial> processMaterials;
	
	@ManyToOne
	private Ent_Inventory inventoryCoil;

}
