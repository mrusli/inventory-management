package com.pyramix.persistence.company.dao;

import com.pyramix.domain.entity.Ent_Company;

public interface CompanyDao {

	public Ent_Company findCompanyById(long id) throws Exception;
	
}
