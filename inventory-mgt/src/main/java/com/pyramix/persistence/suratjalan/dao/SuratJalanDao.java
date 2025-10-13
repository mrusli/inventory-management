package com.pyramix.persistence.suratjalan.dao;

import java.util.List;

import com.pyramix.domain.entity.Ent_Customer;
import com.pyramix.domain.entity.Ent_SuratJalan;

public interface SuratJalanDao {

	public Ent_SuratJalan findSuratJalanById(long id) throws Exception;
	
	public List<Ent_SuratJalan> findAllSuratJalan() throws Exception;
	
	public Ent_SuratJalan update(Ent_SuratJalan suratjalan) throws Exception;
	
	public void save(Ent_SuratJalan suratjalan) throws Exception;
	
	public void delete(Ent_SuratJalan suratjalan) throws Exception;

	public List<Ent_SuratJalan> findSuratJalanByCustomer(Ent_Customer customer) throws Exception;

	public Ent_SuratJalan getSuratJalanProductByProxy(long id) throws Exception;
	
}
