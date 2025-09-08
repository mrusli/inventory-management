package com.pyramix.persistence.inventory.dao.hibernate;

import java.util.List;

import com.pyramix.domain.entity.Ent_Inventory;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.inventory.dao.InventoryDao;

public class InventoryHibernate extends DaoHibernate implements InventoryDao {

	@Override
	public Ent_Inventory findInventoryById(long id) throws Exception {
		
		return (Ent_Inventory) super.findById(Ent_Inventory.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ent_Inventory> findAllInventory() throws Exception {

		return super.findAll(Ent_Inventory.class);
	}

	@Override
	public Ent_Inventory update(Ent_Inventory ent_Inventory) throws Exception {
		
		return (Ent_Inventory) super.update(ent_Inventory);
	}

	@Override
	public void save(Ent_Inventory ent_Inventory) throws Exception {
		
		super.save(ent_Inventory);
	}

	@Override
	public void delete(Ent_Inventory ent_Inventory) throws Exception {
		
		super.delete(ent_Inventory);
	}

}
