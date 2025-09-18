package com.pyramix.persistence.serial.dao;

import java.util.List;

import com.pyramix.domain.entity.Ent_Serial;

public interface SerialDao {

	public Ent_Serial findSerialById(long id) throws Exception;
	
	public List<Ent_Serial> findAllSerial() throws Exception;
	
	public Ent_Serial update(Ent_Serial serial) throws Exception;
	
	public void save(Ent_Serial serial) throws Exception;
	
	public void delete(Ent_Serial serial) throws Exception;
	
}
