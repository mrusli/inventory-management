package com.pyramix.persistence.inventorytype.dao.hibernate;

import java.util.List;

import com.pyramix.domain.entity.Ent_InventoryType;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.inventorytype.dao.InventoryTypeDao;

public class InventoryTypeHibernate extends DaoHibernate implements InventoryTypeDao {

	@Override
	public Ent_InventoryType findInventoryTypeById(long id) throws Exception {
		
		return (Ent_InventoryType) super.findById(Ent_InventoryType.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ent_InventoryType> findAllInventoryType() throws Exception {
		
		return super.findAll(Ent_InventoryType.class);
	}

	@Override
	public void save(Ent_InventoryType inventoryType) throws Exception {
		
		super.save(inventoryType);
	}

	@Override
	public Ent_InventoryType update(Ent_InventoryType inventoryType) throws Exception {
		
		return (Ent_InventoryType) super.update(inventoryType);
	}

	@Override
	public void delete(Ent_InventoryType inventoryType) throws Exception {
		
		super.delete(inventoryType);
	}

}
