package com.pyramix.persistence.inventoryprocess.dao.hibernate;

import java.util.List;

import com.pyramix.domain.entity.Ent_InventoryProcess;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.inventoryprocess.dao.InventoryProcessDao;

public class InventoryProcessHibernate extends DaoHibernate implements InventoryProcessDao {

	@Override
	public Ent_InventoryProcess findInventoryProcessById(long id) throws Exception {

		return (Ent_InventoryProcess) super.findById(Ent_InventoryProcess.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ent_InventoryProcess> findAllInventoryProcess() throws Exception {
		
		return super.findAll(Ent_InventoryProcess.class);
	}

	@Override
	public Ent_InventoryProcess update(Ent_InventoryProcess inventoryProcess) throws Exception {
		
		return (Ent_InventoryProcess) super.update(inventoryProcess);
	}

	@Override
	public void save(Ent_InventoryProcess inventoryProcess) throws Exception {
		
		super.save(inventoryProcess);
	}

	@Override
	public void delete(Ent_InventoryProcess inventoryProcess) throws Exception {
		
		super.delete(inventoryProcess);
	}

}
