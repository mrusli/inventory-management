package com.pyramix.persistence.inventorycode.dao.hibernate;

import java.util.List;

import com.pyramix.domain.entity.Ent_InventoryCode;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.inventorycode.dao.InventoryCodeDao;

public class InventoryCodeHibernate extends DaoHibernate implements InventoryCodeDao {

	@Override
	public Ent_InventoryCode findInventoryCodeById(long id) throws Exception {
		
		return (Ent_InventoryCode) super.findById(Ent_InventoryCode.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ent_InventoryCode> findAllInventoryCode() throws Exception {
		
		return super.findAll(Ent_InventoryCode.class);
	}

	@Override
	public void save(Ent_InventoryCode inventoryCode) throws Exception {
		
		super.save(inventoryCode);
	}

	@Override
	public void update(Ent_InventoryCode inventoryCode) throws Exception {
		
		super.update(inventoryCode);
	}

	@Override
	public void delete(Ent_InventoryCode inventoryCode) throws Exception {
		
		super.delete(inventoryCode);
	}

}
