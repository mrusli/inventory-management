package com.pyramix.persistence.serial.dao.hibernate;

import java.util.List;

import org.hibernate.Session;

import com.pyramix.domain.entity.Enm_TypeDocument;
import com.pyramix.domain.entity.Ent_Company;
import com.pyramix.domain.entity.Ent_Serial;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.serial.dao.SerialDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

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

	@Override
	public Ent_Serial findLastByDocumentType(Enm_TypeDocument documentType, Ent_Company company) {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Ent_Serial> criteriaQuery = criteriaBuilder.createQuery(Ent_Serial.class);
		Root<Ent_Serial> root = criteriaQuery.from(Ent_Serial.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("documentType"), documentType),
				criteriaBuilder.equal(root.get("company"), company));
		criteriaQuery.orderBy(
				criteriaBuilder.desc(root.get("serialDatetime")));
		
		try {
			
			if (session.createQuery(criteriaQuery).getResultList().isEmpty()) {
				return null;
			} else {

				return session.createQuery(criteriaQuery).getResultList().get(0);
				
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
	}

}
