package com.pyramix.domain.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Entity
@Table(name = "invoice_faktur")
public class Ent_InvoiceFaktur extends IdBasedObject {

	private LocalDate faktur_date;
	
	private String faktur_number;
	
}
