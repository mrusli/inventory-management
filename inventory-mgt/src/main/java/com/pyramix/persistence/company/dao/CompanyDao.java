package com.pyramix.persistence.company.dao;

import java.util.List;

import com.pyramix.domain.entity.Ent_Company;

public interface CompanyDao {

	public Ent_Company findCompanyById(long id) throws Exception;
	
	public List<Ent_Company> findAllCompany() throws Exception;
	
	public Ent_Company update(Ent_Company company) throws Exception;
	
	public void save(Ent_Company company) throws Exception;
	
	public void delete(Ent_Company company) throws Exception;
	
}
