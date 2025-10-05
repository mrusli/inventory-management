package com.pyramix.persistence.inventorytable.dao.hibernate;

import java.util.List;

import com.pyramix.domain.entity.Ent_InventoryTable;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.inventorytable.dao.InventoryTableDao;

public class InventoryTableHibernate extends DaoHibernate implements InventoryTableDao {

	@Override
	public Ent_InventoryTable findInventoryTableById(long id) throws Exception {

		return (Ent_InventoryTable) super.findById(Ent_InventoryTable.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ent_InventoryTable> findAllInventoryTable() throws Exception {
		
		return super.findAll(Ent_InventoryTable.class);
	}

	@Override
	public Ent_InventoryTable update(Ent_InventoryTable inventoryTable) throws Exception {
		
		return (Ent_InventoryTable) super.update(inventoryTable);
	}

	@Override
	public void save(Ent_InventoryTable inventoryTable) throws Exception {
		
		super.save(inventoryTable);
	}

	@Override
	public void delete(Ent_InventoryTable inventoryTable) throws Exception {
		
		super.delete(inventoryTable);
	}

}
