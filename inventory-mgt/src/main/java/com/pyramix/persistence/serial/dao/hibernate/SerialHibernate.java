package com.pyramix.persistence.serial.dao.hibernate;

import java.util.List;

import com.pyramix.domain.entity.Ent_Serial;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.serial.dao.SerialDao;

public class SerialHibernate extends DaoHibernate implements SerialDao {

	@Override
	public Ent_Serial findSerialById(long id) throws Exception {

		return (Ent_Serial) super.findById(Ent_Serial.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Ent_Serial> findAllSerial() throws Exception {

		return super.findAll(Ent_Serial.class);
	}

	@Override
	public Ent_Serial update(Ent_Serial serial) throws Exception {

		return (Ent_Serial) super.update(serial);
	}

	@Override
	public void save(Ent_Serial serial) throws Exception {

		super.save(serial);
	}

	@Override
	public void delete(Ent_Serial serial) throws Exception {

		super.delete(serial);
	}

}
